package com.truedigital.features.tuned.data.api

import android.content.Context
import com.google.gson.GsonBuilder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.listens.share.constant.MusicConstant.Intercepter.AUTHORIZATION_HEADER
import com.truedigital.features.tuned.data.ObfuscatedKeyValueStoreInterface
import com.truedigital.features.tuned.data.authentication.model.AuthenticationToken
import com.truedigital.features.tuned.data.authentication.repository.AuthenticationTokenRepository
import com.truedigital.features.tuned.data.device.repository.DeviceRepository
import com.truedigital.features.tuned.data.get
import com.truedigital.features.tuned.data.user.model.Login
import com.truedigital.features.tuned.data.user.model.User
import com.truedigital.features.tuned.data.user.model.UserAccountType
import com.truedigital.features.tuned.data.user.repository.MusicUserRepositoryImpl
import com.truedigital.features.tuned.injection.module.NetworkModule
import com.truedigital.features.utils.MockDataModel
import com.truedigital.features.utils.MusicTestApiInterface
import io.mockk.every
import io.mockk.mockk
import io.reactivex.Single
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.ResponseBody.Companion.toResponseBody
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.test.assertEquals

internal class AuthenticatedRequestInterceptorTest {

    private val context: Context = mockk()
    private val sharedPreferences: ObfuscatedKeyValueStoreInterface = mock()
    private val authenticationTokenRepository: AuthenticationTokenRepository = mockk()
    private val deviceRepository: DeviceRepository = mockk()
    private val mockWebServer = MockWebServer()
    private val client = OkHttpClient.Builder()
        .addInterceptor(
            AuthenticatedRequestInterceptor(
                context,
                sharedPreferences,
                authenticationTokenRepository,
                deviceRepository
            )
        )
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(mockWebServer.url("/"))
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .build()

    private fun mockUserDefault() {
        whenever(sharedPreferences.get<User>(MusicUserRepositoryImpl.CURRENT_USER_KEY))
            .thenReturn(MockDataModel.mockUserTuned)
    }

    private fun mockUserLogin() {
        whenever(sharedPreferences.get<User>(MusicUserRepositoryImpl.CURRENT_USER_KEY))
            .thenReturn(
                MockDataModel.mockUserTuned.copy(
                    logins = listOf(
                        Login(
                            type = UserAccountType.DEVICE.type,
                            value = "device"
                        )
                    )
                )
            )
    }

    @Test
    fun testAppLevelCheck_getTokenSuccess_assertHeaderNull() {
        val mockAccessToken = "abc"
        mockUserDefault()
        every { deviceRepository.get() } returns MockDataModel.mockDevice
        every { authenticationTokenRepository.get(any(), any()) } returns Single.just(
            Pair(
                AuthenticationToken(
                    refreshToken = "refreshToken",
                    expiration = 10L,
                    accessToken = mockAccessToken
                ),
                false
            )
        )

        val testDataJson = "{\"name\":\"test\"}"
        val successResponse = MockResponse().setResponseCode(200).setBody(testDataJson)
        val api = retrofit.create(MusicTestApiInterface::class.java)
        mockWebServer.enqueue(successResponse)
        api.test().execute()

        val request = mockWebServer.takeRequest()
        val header = request.getHeader(AUTHORIZATION_HEADER)
        assertEquals("Bearer $mockAccessToken", header)
    }

    @Test
    fun testAppLevelCheck_getTokenThrowable_canReauthFalse_assertHeader() {
        mockUserDefault()
        every { deviceRepository.get() } returns MockDataModel.mockDevice
        every { authenticationTokenRepository.get(any(), any()) } throws Exception()

        val testDataJson = "{\"name\":\"test\"}"
        val successResponse = MockResponse().setResponseCode(200).setBody(testDataJson)
        val api = retrofit.create(MusicTestApiInterface::class.java)
        mockWebServer.enqueue(successResponse)
        api.test().execute()

        val request = mockWebServer.takeRequest()
        val header = request.getHeader(AUTHORIZATION_HEADER)
        assertEquals("Bearer null", header)
    }

    @Test
    fun testAppLevelCheck_getTokenThrowable_canReauthTrue_recreateTokenSuccess_assertHeader() {
        val mockAccessToken = "abc"
        mockUserLogin()
        every { deviceRepository.get() } returns MockDataModel.mockDevice
        every { authenticationTokenRepository.get(any(), any()) } throws Exception()
        every {
            authenticationTokenRepository.recreateToken(
                any(),
                any(),
                any()
            )
        } returns Single.just(
            AuthenticationToken(
                refreshToken = "refreshToken",
                expiration = 10L,
                accessToken = mockAccessToken
            )
        )

        val testDataJson = "{\"name\":\"test\"}"
        val successResponse = MockResponse().setResponseCode(200).setBody(testDataJson)
        val api = retrofit.create(MusicTestApiInterface::class.java)
        mockWebServer.enqueue(successResponse)
        api.test().execute()

        val request = mockWebServer.takeRequest()
        val header = request.getHeader(AUTHORIZATION_HEADER)
        assertEquals("Bearer $mockAccessToken", header)
    }

    @Test
    fun testAppLevelCheck_getTokenThrowable_canReauthTrue_recreateTokenThrowable_assertHeader() {
        mockUserLogin()
        every { deviceRepository.get() } returns MockDataModel.mockDevice
        every { authenticationTokenRepository.get(any(), any()) } throws Exception()
        every {
            authenticationTokenRepository.recreateToken(
                any(),
                any(),
                any()
            )
        } throws HttpException(
            Response.error<Any>(
                NetworkModule.HTTP_CODE_FORBIDDEN,
                "".toResponseBody("application/json".toMediaTypeOrNull())
            )
        )

        val testDataJson = "{\"name\":\"test\"}"
        val successResponse = MockResponse().setResponseCode(200).setBody(testDataJson)
        val api = retrofit.create(MusicTestApiInterface::class.java)
        mockWebServer.enqueue(successResponse)
        api.test().execute()

        val request = mockWebServer.takeRequest()
        val header = request.getHeader(AUTHORIZATION_HEADER)
        assertEquals("Bearer null", header)
    }

    @Test
    fun testAppLevelCheck_getTokenThrowable_canReauthTrue_recreateTokenThrowableCode500_assertHeader() {
        mockUserLogin()
        every { deviceRepository.get() } returns MockDataModel.mockDevice
        every { authenticationTokenRepository.get(any(), any()) } throws Exception()
        every {
            authenticationTokenRepository.recreateToken(
                any(),
                any(),
                any()
            )
        } throws HttpException(
            Response.error<Any>(
                500,
                "".toResponseBody("application/json".toMediaTypeOrNull())
            )
        )

        val testDataJson = "{\"name\":\"test\"}"
        val successResponse = MockResponse().setResponseCode(200).setBody(testDataJson)
        val api = retrofit.create(MusicTestApiInterface::class.java)
        mockWebServer.enqueue(successResponse)
        api.test().execute()

        val request = mockWebServer.takeRequest()
        val header = request.getHeader(AUTHORIZATION_HEADER)
        assertEquals("Bearer null", header)
    }

    @Test
    fun testUnauthorizedRefreshCheck_getTokenSuccess_assertHeader() {
        val mockAccessToken = "abc"
        mockUserDefault()
        every { deviceRepository.get() } returns MockDataModel.mockDevice
        every { authenticationTokenRepository.get(any(), any()) } returns Single.just(
            Pair(
                AuthenticationToken(
                    refreshToken = "refreshToken",
                    expiration = 10L,
                    accessToken = mockAccessToken
                ),
                false
            )
        )

        val testDataJson = "{\"name\":\"test\"}"
        val api = retrofit.create(MusicTestApiInterface::class.java)
        val invalidTokenResponse =
            MockResponse().setResponseCode(NetworkModule.HTTP_CODE_UNAUTHORISED)
                .setBody(testDataJson)
        val refreshResponse = MockResponse().setResponseCode(200).setBody(testDataJson)
        mockWebServer.enqueue(invalidTokenResponse)
        mockWebServer.enqueue(refreshResponse)
        api.test().execute()

        mockWebServer.takeRequest()
        val request = mockWebServer.takeRequest()
        val header = request.getHeader(AUTHORIZATION_HEADER)
        assertEquals("Bearer $mockAccessToken", header)
    }

    @Test
    fun testUnauthorizedRefreshCheck_getTokenThrowable_canReauthFalse_assertHeader() {
        mockUserDefault()
        every { deviceRepository.get() } returns MockDataModel.mockDevice
        every { authenticationTokenRepository.get(any(), any()) } throws Exception()

        val testDataJson = "{\"name\":\"test\"}"
        val api = retrofit.create(MusicTestApiInterface::class.java)
        val invalidTokenResponse =
            MockResponse().setResponseCode(NetworkModule.HTTP_CODE_UNAUTHORISED)
                .setBody(testDataJson)
        val refreshResponse = MockResponse().setResponseCode(200).setBody(testDataJson)
        mockWebServer.enqueue(invalidTokenResponse)
        mockWebServer.enqueue(refreshResponse)
        api.test().execute()

        mockWebServer.takeRequest()
        val request = mockWebServer.takeRequest()
        val header = request.getHeader(AUTHORIZATION_HEADER)
        assertEquals("Bearer null", header)
    }

    @Test
    fun testUnauthorizedRefreshCheck_getTokenThrowable_canReauthTrue_hasReauthedTrue_assertHeader() {
        mockUserLogin()
        every { deviceRepository.get() } returns MockDataModel.mockDevice
        every { authenticationTokenRepository.get(any(), any()) } throws Exception()

        val testDataJson = "{\"name\":\"test\"}"
        val api = retrofit.create(MusicTestApiInterface::class.java)
        val invalidTokenResponse =
            MockResponse().setResponseCode(NetworkModule.HTTP_CODE_UNAUTHORISED)
                .setBody(testDataJson)
        val refreshResponse = MockResponse().setResponseCode(200).setBody(testDataJson)
        mockWebServer.enqueue(invalidTokenResponse)
        mockWebServer.enqueue(refreshResponse)
        api.test().execute()

        mockWebServer.takeRequest()
        val request = mockWebServer.takeRequest()
        val header = request.getHeader(AUTHORIZATION_HEADER)
        assertEquals("Bearer null", header)
    }

    @Test
    fun testUnauthorizedRefreshCheck_getTokenThrowable_canReauthTrue_hasReauthedFalse_recreateTokenSuccess_assertHeader() {
        val mockAccessToken = "abc"
        mockUserLogin()
        every { deviceRepository.get() } returns MockDataModel.mockDevice
        every { authenticationTokenRepository.get(any(), false) } returns Single.just(
            Pair(
                AuthenticationToken(
                    refreshToken = "refreshToken",
                    expiration = 10L,
                    accessToken = ""
                ),
                false
            )
        )
        every { authenticationTokenRepository.get(any(), true) } throws Exception()
        every {
            authenticationTokenRepository.recreateToken(
                any(),
                any(),
                any()
            )
        } returns Single.just(
            AuthenticationToken(
                refreshToken = "refreshToken",
                expiration = 10L,
                accessToken = mockAccessToken
            )
        )

        val testDataJson = "{\"name\":\"test\"}"
        val api = retrofit.create(MusicTestApiInterface::class.java)
        val invalidTokenResponse =
            MockResponse().setResponseCode(NetworkModule.HTTP_CODE_UNAUTHORISED)
                .setBody(testDataJson)
        val refreshResponse = MockResponse().setResponseCode(200).setBody(testDataJson)
        mockWebServer.enqueue(invalidTokenResponse)
        mockWebServer.enqueue(refreshResponse)
        api.test().execute()

        mockWebServer.takeRequest()
        val request = mockWebServer.takeRequest()
        val header = request.getHeader(AUTHORIZATION_HEADER)
        assertEquals("Bearer $mockAccessToken", header)
    }

    @Test
    fun testUnauthorizedRefreshCheck_getTokenThrowable_canReauthTrue_hasReauthedFalse_recreateTokenThrowable_assertHeader() {
        mockUserLogin()
        every { deviceRepository.get() } returns MockDataModel.mockDevice
        every { authenticationTokenRepository.get(any(), false) } returns Single.just(
            Pair(
                AuthenticationToken(
                    refreshToken = "refreshToken",
                    expiration = 10L,
                    accessToken = ""
                ),
                false
            )
        )
        every { authenticationTokenRepository.get(any(), true) } throws Exception()
        every {
            authenticationTokenRepository.recreateToken(
                any(),
                any(),
                any()
            )
        } throws HttpException(
            Response.error<Any>(
                500,
                "".toResponseBody("application/json".toMediaTypeOrNull())
            )
        )

        val testDataJson = "{\"name\":\"test\"}"
        val api = retrofit.create(MusicTestApiInterface::class.java)
        val invalidTokenResponse =
            MockResponse().setResponseCode(NetworkModule.HTTP_CODE_UNAUTHORISED)
                .setBody(testDataJson)
        val refreshResponse = MockResponse().setResponseCode(200).setBody(testDataJson)
        mockWebServer.enqueue(invalidTokenResponse)
        mockWebServer.enqueue(refreshResponse)
        api.test().execute()

        mockWebServer.takeRequest()
        val request = mockWebServer.takeRequest()
        val header = request.getHeader(AUTHORIZATION_HEADER)
        assertEquals("Bearer null", header)
    }

    @Test
    fun testUnauthorizedRecreateCheck_recreateTokenSuccess_assertHeader() {
        val mockAccessToken = "abc"
        mockUserLogin()
        every { deviceRepository.get() } returns MockDataModel.mockDevice
        every { authenticationTokenRepository.get(any(), any()) } returns Single.just(
            Pair(
                AuthenticationToken(
                    refreshToken = "refreshToken",
                    expiration = 10L,
                    accessToken = mockAccessToken
                ),
                false
            )
        )
        every {
            authenticationTokenRepository.recreateToken(
                any(),
                any(),
                any()
            )
        } returns Single.just(
            AuthenticationToken(
                refreshToken = "refreshToken",
                expiration = 10L,
                accessToken = mockAccessToken
            )
        )

        val testDataJson = "{\"name\":\"test\"}"
        val api = retrofit.create(MusicTestApiInterface::class.java)
        val invalidTokenResponse =
            MockResponse().setResponseCode(NetworkModule.HTTP_CODE_UNAUTHORISED)
                .setBody(testDataJson)
        val refreshResponse = MockResponse().setResponseCode(200).setBody(testDataJson)
        mockWebServer.enqueue(invalidTokenResponse)
        mockWebServer.enqueue(invalidTokenResponse)
        mockWebServer.enqueue(refreshResponse)
        api.test().execute()

        mockWebServer.takeRequest()
        mockWebServer.takeRequest()
        val request = mockWebServer.takeRequest()
        val header = request.getHeader(AUTHORIZATION_HEADER)
        assertEquals("Bearer $mockAccessToken", header)
    }

    @Test
    fun testUnauthorizedRecreateCheck_recreateTokenThrowable_assertHeader() {
        val mockAccessToken = "abc"
        mockUserLogin()
        every { deviceRepository.get() } returns MockDataModel.mockDevice
        every { authenticationTokenRepository.get(any(), any()) } returns Single.just(
            Pair(
                AuthenticationToken(
                    refreshToken = "refreshToken",
                    expiration = 10L,
                    accessToken = mockAccessToken
                ),
                false
            )
        )
        every {
            authenticationTokenRepository.recreateToken(
                any(),
                any(),
                any()
            )
        } throws NullPointerException()

        val testDataJson = "{\"name\":\"test\"}"
        val api = retrofit.create(MusicTestApiInterface::class.java)
        val invalidTokenResponse =
            MockResponse().setResponseCode(NetworkModule.HTTP_CODE_UNAUTHORISED)
                .setBody(testDataJson)
        val refreshResponse = MockResponse().setResponseCode(200).setBody(testDataJson)
        mockWebServer.enqueue(invalidTokenResponse)
        mockWebServer.enqueue(invalidTokenResponse)
        mockWebServer.enqueue(refreshResponse)
        api.test().execute()

        mockWebServer.takeRequest()
        mockWebServer.takeRequest()
        val request = mockWebServer.takeRequest()
        val header = request.getHeader(AUTHORIZATION_HEADER)
        assertEquals("Bearer null", header)
    }
}
