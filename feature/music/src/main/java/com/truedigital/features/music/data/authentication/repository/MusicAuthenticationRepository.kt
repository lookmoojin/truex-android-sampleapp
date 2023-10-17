package com.truedigital.features.music.data.authentication.repository

import com.truedigital.features.music.api.MusicAuthenticationApiInterface
import com.truedigital.features.tuned.data.ObfuscatedKeyValueStoreInterface
import com.truedigital.features.tuned.data.authentication.model.AuthenticationToken
import com.truedigital.features.tuned.data.authentication.model.response.AccessToken
import com.truedigital.features.tuned.data.authentication.repository.AuthenticationTokenRepositoryImpl
import com.truedigital.features.tuned.data.get
import com.truedigital.features.tuned.injection.module.SharePreferenceModule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Named

interface MusicAuthenticationRepository {
    fun refreshToken(): Flow<AccessToken>
}

class MusicAuthenticationRepositoryImpl @Inject constructor(
    @Named(SharePreferenceModule.KVS_AUTH_TOKEN)
    private val sharedPreferences: ObfuscatedKeyValueStoreInterface,
    private val musicAuthenticationApi: MusicAuthenticationApiInterface
) : MusicAuthenticationRepository {

    companion object {
        private const val GRANT_TYPE_REFRESH_TOKEN = "refresh_token"
        const val ERROR_REFRESH_TOKEN = "Music Refresh Token is fail or data not found"
    }

    override fun refreshToken(): Flow<AccessToken> {
        val authToken: AuthenticationToken? =
            sharedPreferences.get(AuthenticationTokenRepositoryImpl.AUTH_TOKEN_KEY)

        return flow {
            val response = musicAuthenticationApi.refreshToken(
                grantType = GRANT_TYPE_REFRESH_TOKEN,
                refreshToken = authToken?.refreshToken.orEmpty()
            )
            val body = response.body()
            if (response.isSuccessful && body != null) {
                emit(body)
            } else {
                error(ERROR_REFRESH_TOKEN)
            }
        }
    }
}
