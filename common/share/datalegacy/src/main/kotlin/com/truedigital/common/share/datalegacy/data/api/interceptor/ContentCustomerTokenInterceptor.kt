package com.truedigital.common.share.datalegacy.data.api.interceptor

import com.truedigital.authentication.data.api.interceptor.RequestTokenInterceptor
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class ContentCustomerTokenInterceptor @Inject constructor() : RequestTokenInterceptor() {

    override fun getSuccessResponse(chain: Interceptor.Chain): Response {
        val token = getAccessToken()
        val request = chain.request().newBuilder()
            .header("Authorization", token.orEmpty())
            .method(chain.request().method, chain.request().body)
            .build()
        return chain.proceed(request)
    }
}
