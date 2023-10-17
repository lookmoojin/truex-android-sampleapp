package com.truedigital.features.music.api.interceptor

import com.google.gson.GsonBuilder
import com.truedigital.common.share.datalegacy.data.endpoint.ApiConfigurationManager
import com.truedigital.common.share.datalegacy.login.LoginManagerInterface
import com.truedigital.core.utils.networkconnection.ConnectivityStateHolder
import com.truedigital.features.music.domain.authentication.usecase.RefreshMusicTokenUseCase
import com.truedigital.features.utils.MusicTestApiInterface
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.assertEquals

internal class MusicRefreshTokenInterceptorTest {

    private val refreshMusicTokenUseCase: RefreshMusicTokenUseCase = mockk()
    private val mockWebServer = MockWebServer()
    private val apiConfigurationManager: ApiConfigurationManager = mockk()
    private val loginManagerInterface: LoginManagerInterface = mockk()

    private val client = OkHttpClient.Builder()
        .addInterceptor(
            MusicRefreshTokenInterceptor(
                apiConfigurationManager,
                refreshMusicTokenUseCase,
                loginManagerInterface
            )
        )
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(mockWebServer.url("/"))
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .build()

    @Test
    fun testNetwork_isConnectedFalse_verifyNotRefreshToken() {
        mockkObject(ConnectivityStateHolder)
        every { ConnectivityStateHolder.isConnected } returns false
        val successResponse = MockResponse().setBody("{\"name\":\"test\"}")
        val api = retrofit.create(MusicTestApiInterface::class.java)
        mockWebServer.enqueue(successResponse)
        api.test().execute()

        val request = mockWebServer.takeRequest()

        assertEquals(null, request.failure)
        verify(exactly = 0) { refreshMusicTokenUseCase.execute() }
    }

    @Test
    fun testRefreshTokenSuccess_isApiSupportJwtTrue_isLoggedInTrue_verifyRefreshToken() {
        mockkObject(ConnectivityStateHolder)
        every { ConnectivityStateHolder.isConnected } returns true

        every { apiConfigurationManager.isApiSupportJwt(any()) } returns true

        every { loginManagerInterface.isLoggedIn() } returns true
        coEvery { refreshMusicTokenUseCase.execute() } returns flowOf("token")

        val successResponse = MockResponse().setBody("{\"name\":\"test\"}")
        val api = retrofit.create(MusicTestApiInterface::class.java)
        mockWebServer.enqueue(successResponse)
        api.test().execute()

        val request = mockWebServer.takeRequest()
        val header =
            request.getHeader(com.truedigital.features.listens.share.constant.MusicConstant.Intercepter.AUTHORIZATION_HEADER)

        assertEquals(null, request.failure)
        assertEquals("${MusicRefreshTokenInterceptor.BEARER_VALUE} token", header)
        verify { refreshMusicTokenUseCase.execute() }
    }

    @Test
    fun testRefreshTokenSuccess_isApiSupportJwtFalse_isLoggedInTrue_verifyNotRefreshToken() {
        mockkObject(ConnectivityStateHolder)
        every { ConnectivityStateHolder.isConnected } returns true
        every { apiConfigurationManager.isApiSupportJwt(any()) } returns false

        every { loginManagerInterface.isLoggedIn() } returns true
        coEvery { refreshMusicTokenUseCase.execute() } returns flowOf("token")

        val successResponse = MockResponse().setBody("{\"name\":\"test\"}")
        val api = retrofit.create(MusicTestApiInterface::class.java)
        mockWebServer.enqueue(successResponse)
        api.test().execute()

        val request = mockWebServer.takeRequest()
        assertEquals(null, request.failure)

        verify(exactly = 0) { refreshMusicTokenUseCase.execute() }
    }

    @Test
    fun testRefreshTokenSuccess_isApiSupportJwtTrue_isLoggedInFalse_verifyNotRefreshToken() {
        mockkObject(ConnectivityStateHolder)
        every { ConnectivityStateHolder.isConnected } returns true

        every { apiConfigurationManager.isApiSupportJwt(any()) } returns true

        every { loginManagerInterface.isLoggedIn() } returns false
        coEvery { refreshMusicTokenUseCase.execute() } returns flowOf("token")

        val successResponse = MockResponse().setBody("{\"name\":\"test\"}")
        val api = retrofit.create(MusicTestApiInterface::class.java)
        mockWebServer.enqueue(successResponse)
        api.test().execute()

        val request = mockWebServer.takeRequest()
        assertEquals(null, request.failure)

        verify(exactly = 0) { refreshMusicTokenUseCase.execute() }
    }

    @Test
    fun testRefreshTokenException_verifyRefreshToken() {
        mockkObject(ConnectivityStateHolder)
        every { ConnectivityStateHolder.isConnected } returns true

        every { apiConfigurationManager.isApiSupportJwt(any()) } returns true

        every { loginManagerInterface.isLoggedIn() } returns true
        coEvery { refreshMusicTokenUseCase.execute() } returns flow { throw Exception() }

        val successResponse = MockResponse().setBody("{\"name\":\"test\"}")
        val api = retrofit.create(MusicTestApiInterface::class.java)
        mockWebServer.enqueue(successResponse)
        api.test().execute()

        val request = mockWebServer.takeRequest()
        val header =
            request.getHeader(com.truedigital.features.listens.share.constant.MusicConstant.Intercepter.AUTHORIZATION_HEADER)

        assertEquals(null, header)
        verify { refreshMusicTokenUseCase.execute() }
    }

    @Test
    fun testRefreshTokenCancellationException_verifyRefreshToken() {
        mockkObject(ConnectivityStateHolder)
        every { ConnectivityStateHolder.isConnected } returns true

        every { apiConfigurationManager.isApiSupportJwt(any()) } returns true

        every { loginManagerInterface.isLoggedIn() } returns true
        coEvery { refreshMusicTokenUseCase.execute() } returns flow { throw CancellationException() }

        val successResponse = MockResponse().setBody("{\"name\":\"test\"}")
        val api = retrofit.create(MusicTestApiInterface::class.java)
        mockWebServer.enqueue(successResponse)
        api.test().execute()

        val request = mockWebServer.takeRequest()
        val header =
            request.getHeader(com.truedigital.features.listens.share.constant.MusicConstant.Intercepter.AUTHORIZATION_HEADER)

        assertEquals(null, header)
        verify { refreshMusicTokenUseCase.execute() }
    }
}
