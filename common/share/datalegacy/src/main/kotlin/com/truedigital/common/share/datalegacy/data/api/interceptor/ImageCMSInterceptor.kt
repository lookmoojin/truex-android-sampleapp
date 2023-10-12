package com.truedigital.common.share.datalegacy.data.api.interceptor

import android.util.Patterns
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class ImageCMSInterceptor : Interceptor {
    companion object {
        private const val CODE_SUCCESS = 200
        private const val HOST_NAME = "cms.dmpcdn.com"
        private const val IMAGE_NAME_ORIGINAL = "original"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        return if (isCanGenerateNewUrl(chain.request().url, response.code)) {
            response.close()
            chain.proceed(generateNewRequest(chain))
        } else {
            response
        }
    }

    private fun isCanGenerateNewUrl(url: HttpUrl, responseCode: Int): Boolean {
        val host = url.host
        val requestUrl = url.toString()
        val imageName = getImageName(url.toString())
        return responseCode != CODE_SUCCESS &&
            !imageName.contains(IMAGE_NAME_ORIGINAL) &&
            host.contains(HOST_NAME) &&
            Patterns.WEB_URL.matcher(requestUrl).matches() &&
            (requestUrl.startsWith("https://") || requestUrl.startsWith("http://"))
    }

    private fun generateNewRequest(chain: Interceptor.Chain): Request {
        return chain.request().newBuilder()
            .url(generateOriginalUrl(chain.request().url.toString()))
            .build()
    }

    private fun generateOriginalUrl(imageUrl: String): String {
        val oldImageName = getImageName(imageUrl)
        val newImageName = oldImageName.replaceBefore(".", IMAGE_NAME_ORIGINAL)
        val newUrlImage = imageUrl.replace(oldImageName, newImageName)
        return if (Patterns.WEB_URL.matcher(newUrlImage).matches() &&
            (newUrlImage.startsWith("https://") || newUrlImage.startsWith("http://"))
        ) {
            newUrlImage
        } else {
            imageUrl
        }
    }

    private fun getImageName(imageUrl: String): String {
        return imageUrl.substringAfterLast("_")
    }
}
