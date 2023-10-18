package com.truedigital.common.share.datalegacy.data.api.interceptor

import android.util.Patterns
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class AvatarInterceptor : Interceptor {
    companion object {
        private const val CODE_ERROR = 404
        private const val HOST_NAME = "avatar"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestHost = chain.request().url.host
        val requestUrl = chain.request().url.toString()
        val response = chain.proceed(chain.request())
        return if (isCanGenerateNewRequest(response.code, requestHost, requestUrl)
        ) {
            response.close()
            chain.proceed(generateNewRequest(chain))
        } else {
            response
        }
    }

    private fun isCanGenerateNewRequest(
        responseCode: Int,
        requestHost: String,
        requestUrl: String
    ): Boolean {
        return responseCode == CODE_ERROR &&
            requestHost.contains(HOST_NAME) &&
            Patterns.WEB_URL.matcher(requestUrl).matches() &&
            (requestUrl.startsWith("https://") || requestUrl.startsWith("http://"))
    }

    private fun generateNewRequest(chain: Interceptor.Chain): Request {
        return chain.request().newBuilder()
            .url(generateDefaultUrl(chain))
            .build()
    }

    private fun generateDefaultUrl(chain: Interceptor.Chain): String {
        val scheme = chain.request().url.scheme
        val host = chain.request().url.host
        val pathSegments = chain.request().url.pathSegments
        val sizeImage = if (pathSegments.isNotEmpty()) pathSegments[0] else ""
        return "$scheme://$host/$sizeImage/default.png"
    }
}
