package com.truedigital.features.music.api.interceptor

import com.truedigital.features.listens.share.constant.MusicConstant
import com.truedigital.features.tuned.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

class MusicInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header(MusicConstant.Intercepter.STORE_ID_HEADER, BuildConfig.STORE_ID_TUNEDGLOBAL)
            .url(chain.request().url)
            .build()
        return chain.proceed(request)
    }
}
