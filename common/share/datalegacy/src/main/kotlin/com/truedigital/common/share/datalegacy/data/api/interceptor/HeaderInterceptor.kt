package com.truedigital.common.share.datalegacy.data.api.interceptor

import com.truedigital.common.share.datalegacy.data.endpoint.ApiConfigurationManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class HeaderInterceptor @Inject constructor(
    private val apiConfigurationManager: ApiConfigurationManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val pathSegments = chain.request().url.pathSegments
        val featurePath = pathSegments.first()

        val tokenConfiguration = apiConfigurationManager.getToken(featurePath)
        return if (tokenConfiguration.isNotEmpty()) {
            val createRequestHeader = chain.request().newBuilder()
                .header("Authorization", tokenConfiguration)
                .build()

            chain.proceed(createRequestHeader)
        } else {
            chain.proceed(chain.request())
        }
    }
}
