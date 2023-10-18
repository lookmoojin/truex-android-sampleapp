package com.truedigital.common.share.datalegacy.data.api.interceptor

import com.truedigital.authentication.data.api.interceptor.RequestTokenInterceptor
import com.truedigital.common.share.datalegacy.data.endpoint.ApiConfigurationManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class SevenTokenInterceptor @Inject constructor(
    private val apiConfigurationManager: ApiConfigurationManager
) : RequestTokenInterceptor() {

    override fun getSuccessResponse(chain: Interceptor.Chain): Response {
        val pathSegments = chain.request().url.pathSegments
        val featurePath = pathSegments.first()

        val tokenConfiguration = apiConfigurationManager.getToken(featurePath)
        val request = chain.request().newBuilder()
            .header("Authorization", tokenConfiguration)
            .header("access_token", getAccessToken().orEmpty())
            .method(chain.request().method, chain.request().body)
            .build()
        return chain.proceed(request)
    }
}
