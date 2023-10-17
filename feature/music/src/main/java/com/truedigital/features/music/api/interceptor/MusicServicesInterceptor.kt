package com.truedigital.features.music.api.interceptor

import com.truedigital.common.share.datalegacy.data.endpoint.ApiConfigurationManager
import com.truedigital.features.listens.share.constant.MusicConstant
import com.truedigital.features.tuned.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class MusicServicesInterceptor @Inject constructor(
    private val apiConfigurationManager: ApiConfigurationManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val pathSegments = chain.request().url.pathSegments
        val featurePath = pathSegments.first()

        val customRequest = if (apiConfigurationManager.hasApiConfiguration(featurePath)) {
            val encodedPath = chain.request().url.encodedPath
                .removePrefix("/$featurePath")
                .removePrefix("/")
            val queryParameters =
                if (chain.request().url.query != null) "?${chain.request().url.query}" else ""
            val fullUrl =
                "${apiConfigurationManager.getUrl(featurePath)}$encodedPath$queryParameters"
            val storeId = apiConfigurationManager.getToken(featurePath)
                .ifEmpty { BuildConfig.STORE_ID_TUNEDGLOBAL }

            chain.request().newBuilder()
                .header(MusicConstant.Intercepter.STORE_ID_HEADER, storeId)
                .url(fullUrl)
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(customRequest)
    }
}
