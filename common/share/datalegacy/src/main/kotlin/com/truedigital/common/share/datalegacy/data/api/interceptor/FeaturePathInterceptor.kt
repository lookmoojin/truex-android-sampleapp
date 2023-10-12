package com.truedigital.common.share.datalegacy.data.api.interceptor

import com.truedigital.common.share.datalegacy.data.endpoint.ApiConfigurationManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class FeaturePathInterceptor @Inject constructor(
    private val apiConfigurationManager: ApiConfigurationManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val pathSegments = chain.request().url.pathSegments
        val featurePath = pathSegments.first()

        val customRequest = if (apiConfigurationManager.hasApiConfiguration(featurePath)) {
            val encodedPath = chain.request().url.encodedPath ?: ""
            val queryParameters =
                if (chain.request().url.query != null) "?${chain.request().url.query}" else ""
            val fullUrl = "${
            apiConfigurationManager.getUrl(featurePath).replace("/$featurePath/", encodedPath)
            }$queryParameters"

            chain.request().newBuilder()
                .url(fullUrl)
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(customRequest)
    }
}
