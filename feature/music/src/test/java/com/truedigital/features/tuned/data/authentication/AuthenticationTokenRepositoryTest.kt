package com.truedigital.features.tuned.data.authentication

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.common.share.datalegacy.wrapper.AuthManagerWrapper
import com.truedigital.features.tuned.api.authentication.AuthenticationTokenApiInterface
import com.truedigital.features.tuned.application.configuration.Configuration
import com.truedigital.features.tuned.data.ObfuscatedKeyValueStoreInterface
import com.truedigital.features.tuned.data.authentication.model.AuthenticationToken
import com.truedigital.features.tuned.data.authentication.model.response.AccessToken
import com.truedigital.features.tuned.data.authentication.repository.AuthenticationTokenRepository
import com.truedigital.features.tuned.data.authentication.repository.AuthenticationTokenRepositoryImpl
import com.truedigital.features.tuned.data.get
import com.truedigital.features.tuned.data.put
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Single
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AuthenticationTokenRepositoryTest {

    private lateinit var authenticationTokenRepository: AuthenticationTokenRepository
    private val sharedPreferences: ObfuscatedKeyValueStoreInterface = mock()
    private val config: Configuration = mockk(relaxed = true)
    private val authenticationTokenApi: AuthenticationTokenApiInterface = mockk()
    private val authManagerWrapper: AuthManagerWrapper = mockk()
    private val mockAuthToken = AuthenticationToken(
        refreshToken = "refreshToken",
        expiration = 10L,
        accessToken = "accessToken"
    )

    @BeforeEach
    fun setup() {
        authenticationTokenRepository = AuthenticationTokenRepositoryImpl(
            sharedPreferences, authenticationTokenApi, config, authManagerWrapper
        )
    }

    @Test
    fun testGetTokenByJwt_success() {
        every { authenticationTokenApi.getTokenByJwt(any(), any(), any(), any()) } returns
            Single.just(AccessToken("access_token", "Jwt", 12, "refresh_token"))

        authenticationTokenRepository.getTokenByJwt("uniqueid", "userjwt")
            .test()
            .assertValue {
                it.accessToken == "access_token"
            }
    }

    @Test
    fun testGetTokenByJwt_error() {
        every {
            authenticationTokenApi.getTokenByJwt(
                any(),
                any(),
                any(),
                any()
            )
        } returns Single.error(Throwable("error"))

        authenticationTokenRepository.getTokenByJwt("uniqueid", "userjwt")
            .test()
            .assertError {
                it.message == "error"
            }
    }

    @Test
    fun testGetTokenByLogin() {
        every {
            authenticationTokenApi.getTokenByLogin(
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        } returns
            Single.just(AccessToken("access_token", "TOKEN", 12, "refresh_token"))

        authenticationTokenRepository.getTokenByLogin("uniqueid", "admin", "1234")
            .test()
            .assertValue {
                it.accessToken == "access_token"
            }
    }

    @Test
    fun testGetToken_authTokenNull_returnError() {
        whenever(sharedPreferences.get<AuthenticationToken>(AuthenticationTokenRepositoryImpl.AUTH_TOKEN_KEY))
            .thenReturn(null)

        authenticationTokenRepository.get("uniqueid", false)
            .test()
            .assertError {
                it.message == "auth token does not exist"
            }
    }

    @Test
    fun testGetToken_authTokenNotNull_accessTokenNotNull_notExpire_notRefresh_returnPairTokenAndFalse() {
        val mockTokenNotExpire =
            mockAuthToken.copy(expiration = System.currentTimeMillis() + 10000L)
        whenever(sharedPreferences.get<AuthenticationToken>(AuthenticationTokenRepositoryImpl.AUTH_TOKEN_KEY))
            .thenReturn(mockTokenNotExpire)

        authenticationTokenRepository.get("uniqueid", false)
            .test()
            .assertValue {
                it.first == mockTokenNotExpire && !it.second
            }
    }

    @Test
    fun testGetToken_accessTokenNull_tokenNotExpire_returnComplete() {
        val mockAccessTokenNull = mockAuthToken.copy(
            accessToken = null,
            expiration = System.currentTimeMillis() + 10000L
        )
        whenever(sharedPreferences.get<AuthenticationToken>(AuthenticationTokenRepositoryImpl.AUTH_TOKEN_KEY))
            .thenReturn(mockAccessTokenNull)
        every { authenticationTokenApi.refreshToken(any(), any(), any()) } returns
            Single.just(AccessToken("access_token", "Jwt", 10L, "refresh_token"))

        authenticationTokenRepository.get("uniqueid", false)
            .test()
            .assertComplete()
    }

    @Test
    fun testGetToken_tokenExpire_returnError() {
        val mockAccessTokenNull = mockAuthToken.copy(
            expiration = System.currentTimeMillis() - 10000L
        )
        whenever(sharedPreferences.get<AuthenticationToken>(AuthenticationTokenRepositoryImpl.AUTH_TOKEN_KEY))
            .thenReturn(mockAccessTokenNull)
        every { authenticationTokenApi.refreshToken(any(), any(), any()) } returns
            Single.just(AccessToken("access_token", "Jwt", -10000L, "refresh_token"))

        authenticationTokenRepository.get("uniqueid", false)
            .test()
            .assertError(IllegalStateException::class.java)
    }

    @Test
    fun testGetToken_refreshTrue_returnComplete() {
        val mockAccessTokenNull = mockAuthToken.copy(
            expiration = System.currentTimeMillis() + 10000L
        )
        whenever(sharedPreferences.get<AuthenticationToken>(AuthenticationTokenRepositoryImpl.AUTH_TOKEN_KEY))
            .thenReturn(mockAccessTokenNull)
        every { authenticationTokenApi.refreshToken(any(), any(), any()) } returns
            Single.just(AccessToken("access_token", "Jwt", 10L, "refresh_token"))

        authenticationTokenRepository.get("uniqueid", true)
            .test()
            .assertComplete()
    }

    @Test
    fun testGetCurrentToken_authTokenNull_returnNull() {
        // Given
        whenever(sharedPreferences.get<AuthenticationToken>(AuthenticationTokenRepositoryImpl.AUTH_TOKEN_KEY))
            .thenReturn(null)

        // When
        val currentToken = authenticationTokenRepository.getCurrentToken()

        // Then
        assertEquals(null, currentToken)
    }

    @Test
    fun testGetCurrentToken_authTokenNotNull_returnAuthToken() {
        // Given
        whenever(sharedPreferences.get<AuthenticationToken>(AuthenticationTokenRepositoryImpl.AUTH_TOKEN_KEY))
            .thenReturn(mockAuthToken)

        // When
        val result = authenticationTokenRepository.getCurrentToken()

        // Then
        assertEquals(mockAuthToken, result)
    }

    @Test
    fun testSetAuthenticationToken_verifySharePrefPut() {
        authenticationTokenRepository.set(mockAuthToken)
            .test()
            .assertValue {
                it.refreshToken == "refreshToken" &&
                    it.expiration == 10L &&
                    it.accessToken == "accessToken"
            }
        verify(sharedPreferences, times(1)).put(
            AuthenticationTokenRepositoryImpl.AUTH_TOKEN_KEY,
            mockAuthToken
        )
    }

    @Test
    fun testClearToken_verifySharePrefRemove() {
        authenticationTokenRepository.clearToken()
        verify(sharedPreferences, times(1)).remove(AuthenticationTokenRepositoryImpl.AUTH_TOKEN_KEY)
    }

    @Test
    fun testGetUserIdFromToken_authTokenNull_returnNull() {
        // Given
        whenever(sharedPreferences.get<AuthenticationToken>(AuthenticationTokenRepositoryImpl.AUTH_TOKEN_KEY))
            .thenReturn(null)
        // When
        val result = authenticationTokenRepository.getUserIdFromToken()

        // Then
        assertNull(result)
    }
}
