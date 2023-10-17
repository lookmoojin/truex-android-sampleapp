package com.truedigital.features.tuned.data.api

import com.truedigital.features.tuned.injection.module.NetworkModule
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody
import timber.log.Timber
import java.io.IOException

/**
 * Okhttp chucks a hissy fit when you unsub from a request, this should fix that
 */
class CancelledRequestInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response = try {
        chain.proceed(chain.request())
    } catch (e: IOException) {
        if (e.message == "Canceled" || e.message == "Socket closed") {
            Timber.e("One must enhance their calm")
            Response.Builder()
                .code(NetworkModule.HTTP_CODE_ENHANCE_YOUR_CALM)
                .protocol(Protocol.HTTP_1_1)
                .message(e.message.orEmpty())
                .request(chain.request())
                .body(ResponseBody.create(null, ""))
                .build()
        } else {
            throw e
        }
    }
}
