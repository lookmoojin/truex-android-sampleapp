package com.truedigital.features.music.domain.authentication.usecase

import com.truedigital.features.music.data.authentication.repository.MusicAuthenticationRepository
import com.truedigital.features.tuned.data.ObfuscatedKeyValueStoreInterface
import com.truedigital.features.tuned.data.authentication.model.AuthenticationToken
import com.truedigital.features.tuned.data.authentication.repository.AuthenticationTokenRepositoryImpl
import com.truedigital.features.tuned.data.get
import com.truedigital.features.tuned.data.put
import com.truedigital.features.tuned.injection.module.SharePreferenceModule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Named

interface RefreshMusicTokenUseCase {
    fun execute(): Flow<String>
}

class RefreshMusicTokenUseCaseImpl @Inject constructor(
    @Named(SharePreferenceModule.KVS_AUTH_TOKEN)
    private val sharedPreferences: ObfuscatedKeyValueStoreInterface,
    private val musicAuthenticationRepository: MusicAuthenticationRepository
) : RefreshMusicTokenUseCase {

    companion object {
        const val ERROR_AUTHENTICATION_TOKEN = "Authentication Token is null"
    }

    override fun execute(): Flow<String> {
        val authToken: AuthenticationToken? =
            sharedPreferences.get(AuthenticationTokenRepositoryImpl.AUTH_TOKEN_KEY)

        authToken?.let { token ->
            return token.accessToken?.let { accessToken ->
                if (accessToken.isNotEmpty() && System.currentTimeMillis() < token.expiration) {
                    flow { emit(accessToken) }
                } else {
                    refreshToken()
                }
            } ?: run {
                refreshToken()
            }
        } ?: return flow { error(ERROR_AUTHENTICATION_TOKEN) }
    }

    private fun refreshToken(): Flow<String> {
        return musicAuthenticationRepository.refreshToken()
            .map { response ->
                AuthenticationToken(
                    refreshToken = response.refreshToken,
                    expiration = System.currentTimeMillis() +
                        (response.expiresIn * AuthenticationTokenRepositoryImpl.SECONDS_TO_MILLISECONDS),
                    accessToken = response.accessToken
                )
            }
            .onEach {
                sharedPreferences.put(AuthenticationTokenRepositoryImpl.AUTH_TOKEN_KEY, it)
            }
            .map {
                it.accessToken.orEmpty()
            }
    }
}
