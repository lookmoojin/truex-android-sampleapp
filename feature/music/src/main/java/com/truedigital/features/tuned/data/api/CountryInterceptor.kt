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
class CountryInterceptor @Inject constructor(
    private val sharedPreferences: ObfuscatedKeyValueStoreInterface
) : Interceptor {

    companion object {
        const val COUNTRY_HEADER = "Country"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        return try {
            val user = sharedPreferences.get<User>(MusicUserRepositoryImpl.CURRENT_USER_KEY)
            val request = user?.country?.let { country ->
                chain.request().newBuilder()
                    .header(COUNTRY_HEADER, country)
                    .build()
            } ?: run {
                chain.request().newBuilder().build()
            }
            chain.proceed(request)
        } catch (e: Exception) {
            Timber.e(e)
            chain.proceed(chain.request())
        }
    }
}
