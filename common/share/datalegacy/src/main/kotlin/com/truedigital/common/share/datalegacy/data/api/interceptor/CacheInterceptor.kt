package com.truedigital.common.share.datalegacy.data.api.interceptor

import com.truedigital.core.utils.networkconnection.ConnectivityStateHolder
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

const val CACHE_REQUEST_MAX_AGE = 0
const val CACHE_REQUEST_MAX_STALE = 365
const val CACHE_RESPONSE_MAX_AGE = 60 //
const val CACHE_RESPONSE_MAX_STALE = 60 * 60 * 24 * 28 // 4 weeks stale
const val CACHE_DIRECTORY_SIZE = 10 * 1024 * 1024 // 100 MiB
const val CACHE_DIRECTORY_NAME = "TrueID.Cache"

class CacheInterceptor : Interceptor {

    private val isOnline = ConnectivityStateHolder.isConnected

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val cacheBuilder = CacheControl.Builder()
        cacheBuilder.maxAge(CACHE_REQUEST_MAX_AGE, TimeUnit.SECONDS)
        cacheBuilder.maxStale(CACHE_REQUEST_MAX_STALE, TimeUnit.DAYS)

        val cacheControl = cacheBuilder.build()
        var request = chain.request()

        if (isOnline) {
            request = request.newBuilder()
                .cacheControl(cacheControl)
                .build()
        }

        val originalResponse = chain.proceed(request)

        return when (isOnline) {
            true -> {
                originalResponse.newBuilder()
                    .header("Cache-Control", "public, max-age=$CACHE_RESPONSE_MAX_AGE")
                    .build()
            }
            false -> {
                originalResponse.newBuilder()
                    .header("Cache-Control", "public, only-if-cached, max-stale=$CACHE_RESPONSE_MAX_STALE")
                    .build()
            }
        }
    }
}

fun provideOkHttpCache(): Cache {
    val cacheSize = CACHE_DIRECTORY_SIZE
    val cacheDir = File(CACHE_DIRECTORY_NAME)
    return Cache(cacheDir, cacheSize.toLong())
}
