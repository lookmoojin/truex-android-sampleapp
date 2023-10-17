package com.truedigital.features.tuned.data.api

import com.truedigital.features.listens.share.constant.MusicConstant
import com.truedigital.features.tuned.application.configuration.Configuration
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class StoreIdInterceptor @Inject constructor(private val config: Configuration) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .header(MusicConstant.Intercepter.STORE_ID_HEADER, config.storeId)
            .build()
        return chain.proceed(request)
    }
}
