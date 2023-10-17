package com.truedigital.features.music.data.authentication.repository

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.music.api.MusicAuthenticationApiInterface
import com.truedigital.features.music.data.authentication.repository.MusicAuthenticationRepositoryImpl.Companion.ERROR_REFRESH_TOKEN
import com.truedigital.features.tuned.data.ObfuscatedKeyValueStoreInterface
import com.truedigital.features.tuned.data.authentication.model.AuthenticationToken
import com.truedigital.features.tuned.data.authentication.model.response.AccessToken
import com.truedigital.features.tuned.data.authentication.repository.AuthenticationTokenRepositoryImpl
import com.truedigital.features.tuned.data.get
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import retrofit2.Response
import kotlin.test.assertEquals

class MusicAuthenticationRepositoryTest {

    private lateinit var musicAuthenticationRepository: MusicAuthenticationRepository
    private val sharedPreferences: ObfuscatedKeyValueStoreInterface = mock()
    private val musicAuthenticationApi: MusicAuthenticationApiInterface = mock()
    private val bodyResponse =
        "{\"code\":400,\"status\":400,\"error\":null,\"message\":\"unexpected error\"}"
            .toResponseBody("application/json".toMediaTypeOrNull())
    private val rawResponse = okhttp3.Response.Builder()
        .code(400)
        .message("unexpected error")
        .request(Request.Builder().url("http://example.com").build())
        .protocol(Protocol.HTTP_1_0)
        .build()

    @BeforeEach
    fun setup() {
        musicAuthenticationRepository = MusicAuthenticationRepositoryImpl(
            sharedPreferences,
            musicAuthenticationApi
        )
    }

    @Test
    fun testRefreshToken_code404_returnError() = runTest {
        whenever(sharedPreferences.get<AuthenticationToken>(AuthenticationTokenRepositoryImpl.AUTH_TOKEN_KEY))
            .thenReturn(null)
        whenever(musicAuthenticationApi.refreshToken(anyString(), anyString()))
            .thenReturn(Response.error(bodyResponse, rawResponse))

        val flow = musicAuthenticationRepository.refreshToken()

        flow.catch {
            Assertions.assertEquals(it.localizedMessage, ERROR_REFRESH_TOKEN)
        }.collect()
    }

    @Test
    fun testRefreshToken_code200_bodyNull_returnError() = runTest {
        val mockResponse: AccessToken? = null
        whenever(sharedPreferences.get<AuthenticationToken>(AuthenticationTokenRepositoryImpl.AUTH_TOKEN_KEY))
            .thenReturn(null)
        whenever(musicAuthenticationApi.refreshToken(anyString(), anyString()))
            .thenReturn(Response.success(200, mockResponse))

        val flow = musicAuthenticationRepository.refreshToken()

        flow.catch {
            Assertions.assertEquals(it.localizedMessage, ERROR_REFRESH_TOKEN)
        }.collect()
    }

    @Test
    fun testRefreshToken_code200_bodyNotNull_returnAccessToken() = runTest {
        val mockResponse = AccessToken(
            accessToken = "accessToken",
            tokenType = "tokenType",
            expiresIn = 10L,
            refreshToken = "refreshToken"
        )
        whenever(sharedPreferences.get<AuthenticationToken>(AuthenticationTokenRepositoryImpl.AUTH_TOKEN_KEY))
            .thenReturn(
                AuthenticationToken(
                    refreshToken = "refreshToken",
                    expiration = 10L,
                    accessToken = "accessToken"
                )
            )
        whenever(musicAuthenticationApi.refreshToken(anyString(), anyString()))
            .thenReturn(Response.success(200, mockResponse))

        val flow = musicAuthenticationRepository.refreshToken()

        flow.collect {
            assertEquals("accessToken", it.accessToken)
            assertEquals("tokenType", it.tokenType)
            assertEquals(10L, it.expiresIn)
            assertEquals("refreshToken", it.refreshToken)
        }
    }
}
