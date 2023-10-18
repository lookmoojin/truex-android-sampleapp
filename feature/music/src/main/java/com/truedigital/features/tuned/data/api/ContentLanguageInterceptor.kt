package com.truedigital.features.tuned.data.api

import com.truedigital.features.tuned.data.ObfuscatedKeyValueStoreInterface
import com.truedigital.features.tuned.data.get
import com.truedigital.features.tuned.data.user.model.User
import com.truedigital.features.tuned.data.user.repository.MusicUserRepositoryImpl
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContentLanguageInterceptor @Inject constructor(
    private val sharedPreferences: ObfuscatedKeyValueStoreInterface
) : Interceptor {

    companion object {
        const val CONTENT_LANGUAGE_HEADER = "ContentLanguage"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        return try {
            val user = sharedPreferences.get<User>(MusicUserRepositoryImpl.CURRENT_USER_KEY)
            val contentLanguages = user?.contentLanguages?.joinToString { it.code }
            val request = if (contentLanguages == null) {
                chain.request().newBuilder().build()
            } else {
                chain.request().newBuilder()
                    .header(CONTENT_LANGUAGE_HEADER, contentLanguages.replace(" ", ""))
                    .build()
            }
            chain.proceed(request)
        } catch (e: Exception) {
            Timber.e(e)
            chain.proceed(chain.request())
        }
    }
}
