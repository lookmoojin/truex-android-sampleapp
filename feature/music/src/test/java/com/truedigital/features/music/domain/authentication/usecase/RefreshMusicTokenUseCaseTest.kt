package com.truedigital.features.music.domain.authentication.usecase

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.music.data.authentication.repository.MusicAuthenticationRepository
import com.truedigital.features.music.domain.authentication.usecase.RefreshMusicTokenUseCaseImpl.Companion.ERROR_AUTHENTICATION_TOKEN
import com.truedigital.features.tuned.data.ObfuscatedKeyValueStoreInterface
import com.truedigital.features.tuned.data.authentication.model.AuthenticationToken
import com.truedigital.features.tuned.data.authentication.model.response.AccessToken
import com.truedigital.features.tuned.data.authentication.repository.AuthenticationTokenRepositoryImpl.Companion.AUTH_TOKEN_KEY
import com.truedigital.features.tuned.data.get
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RefreshMusicTokenUseCaseTest {
    private lateinit var refreshMusicTokenUseCase: RefreshMusicTokenUseCase
    private val sharedPreferences: ObfuscatedKeyValueStoreInterface = mock()
    private val musicAuthenticationRepository: MusicAuthenticationRepository = mock()

    @BeforeEach
    fun setup() {
        refreshMusicTokenUseCase = RefreshMusicTokenUseCaseImpl(
            sharedPreferences,
            musicAuthenticationRepository
        )
    }

    @Test
    fun testRefreshToken_authTokenNull_returnError() = runTest {
        whenever(sharedPreferences.get<AuthenticationToken>(AUTH_TOKEN_KEY)).thenReturn(null)

        val flow = refreshMusicTokenUseCase.execute()
        flow.catch {
            assertEquals(it.localizedMessage, ERROR_AUTHENTICATION_TOKEN)
        }.collect()
    }

    @Test
    fun testRefreshToken_accessTokenNotNull_notExpire_returnAccessToken() = runTest {
        whenever(sharedPreferences.get<AuthenticationToken>(AUTH_TOKEN_KEY)).thenReturn(
            AuthenticationToken(
                refreshToken = "refreshToken",
                expiration = System.currentTimeMillis() + 100000,
                accessToken = "accessToken"
            )
        )

        val flow = refreshMusicTokenUseCase.execute()
        flow.collect {
            assertEquals("accessToken", it)
        }
    }

    @Test
    fun testRefreshToken_accessTokenNotNull_expire_verifyRefreshToken() = runTest {
        val mockAccessToken = "accessToken"
        whenever(sharedPreferences.get<AuthenticationToken>(AUTH_TOKEN_KEY)).thenReturn(
            AuthenticationToken(
                refreshToken = "refreshToken",
                expiration = System.currentTimeMillis() - 100000,
                accessToken = "accessTokenOld"
            )
        )
        whenever(musicAuthenticationRepository.refreshToken()).thenReturn(
            flowOf(AccessToken(accessToken = mockAccessToken))
        )

        val flow = refreshMusicTokenUseCase.execute()
        flow.collect {
            assertEquals(mockAccessToken, it)
        }
        verify(musicAuthenticationRepository, times(1)).refreshToken()
    }

    @Test
    fun testRefreshToken_accessTokenNull_notExpire_verifyRefreshToken() = runTest {
        val mockAccessToken = "accessToken"
        whenever(sharedPreferences.get<AuthenticationToken>(AUTH_TOKEN_KEY)).thenReturn(
            AuthenticationToken(
                refreshToken = "refreshToken",
                expiration = System.currentTimeMillis() + 100000,
                accessToken = null
            )
        )
        whenever(musicAuthenticationRepository.refreshToken()).thenReturn(
            flowOf(AccessToken(accessToken = mockAccessToken))
        )

        val flow = refreshMusicTokenUseCase.execute()
        flow.collect {
            assertEquals(mockAccessToken, it)
        }
        verify(musicAuthenticationRepository, times(1)).refreshToken()
    }

    @Test
    fun testRefreshToken_accessTokenEmpty_notExpire_verifyRefreshToken() = runTest {
        val mockAccessToken = "accessToken"
        whenever(sharedPreferences.get<AuthenticationToken>(AUTH_TOKEN_KEY)).thenReturn(
            AuthenticationToken(
                refreshToken = "refreshToken",
                expiration = System.currentTimeMillis() + 100000,
                accessToken = ""
            )
        )
        whenever(musicAuthenticationRepository.refreshToken()).thenReturn(
            flowOf(AccessToken(accessToken = mockAccessToken))
        )

        val flow = refreshMusicTokenUseCase.execute()
        flow.collect {
            assertEquals(mockAccessToken, it)
        }
        verify(musicAuthenticationRepository, times(1)).refreshToken()
    }
}
