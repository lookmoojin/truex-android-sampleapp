package com.truedigital.features.music.api.interceptor

import com.google.gson.GsonBuilder
import com.truedigital.common.share.datalegacy.data.endpoint.ApiConfigurationManager
import com.truedigital.features.tuned.BuildConfig
import com.truedigital.features.utils.MusicTestApiInterface
import io.mockk.every
import io.mockk.mockk
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.test.assertEquals

internal class MusicServiceInterceptorTest {

    private val mockWebServer = MockWebServer()
    private val apiConfigurationManager: ApiConfigurationManager = mockk()
    private val client = OkHttpClient.Builder()
        .addInterceptor(MusicServicesInterceptor(apiConfigurationManager))
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(mockWebServer.url("/"))
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .build()

    @Test
    fun testStoreIdHeader_hasApiConfigurationFalse_returnStoreIdNull() {
        every { apiConfigurationManager.hasApiConfiguration(any()) } returns false
        val testDataJson = "{\"name\":\"test\"}"
        val successResponse = MockResponse().setBody(testDataJson)
        val api = retrofit.create(MusicTestApiInterface::class.java)
        mockWebServer.enqueue(successResponse)
        api.test().execute()

        val request = mockWebServer.takeRequest()
        val header = request.getHeader(com.truedigital.features.listens.share.constant.MusicConstant.Intercepter.STORE_ID_HEADER)

        assertEquals(null, header)
    }

    @Test
    fun testStoreIdHeader_hasApiConfigurationTrue_queryNull_returnStoreId() {
        every { apiConfigurationManager.hasApiConfiguration(any()) } returns true
        every { apiConfigurationManager.getUrl(any()) } returns retrofit.baseUrl().toUrl()
            .toString().removeSuffix("/")
        every { apiConfigurationManager.getToken(any()) } returns "token"
        val testDataJson = "{\"name\":\"test\"}"
        val successResponse = MockResponse().setBody(testDataJson)
        val api = retrofit.create(MusicTestApiInterface::class.java)
        mockWebServer.enqueue(successResponse)
        api.test().execute()

        val request = mockWebServer.takeRequest()
        val header = request.getHeader(com.truedigital.features.listens.share.constant.MusicConstant.Intercepter.STORE_ID_HEADER)

        assertEquals("token", header)
    }

    @Test
    fun testStoreIdHeader_hasApiConfigurationTrue_queryNotNull_returnStoreId() {
        every { apiConfigurationManager.hasApiConfiguration(any()) } returns true
        every { apiConfigurationManager.getUrl(any()) } returns retrofit.baseUrl().toUrl()
            .toString().removeSuffix("/")
        every { apiConfigurationManager.getToken(any()) } returns ""
        val testDataJson = "{\"name\":\"test\"}"
        val successResponse = MockResponse().setBody(testDataJson)
        val api = retrofit.create(MusicTestApiInterface::class.java)
        mockWebServer.enqueue(successResponse)
        api.testWithQuery("query").execute()

        val request = mockWebServer.takeRequest()
        val header = request.getHeader(com.truedigital.features.listens.share.constant.MusicConstant.Intercepter.STORE_ID_HEADER)

        assertEquals(BuildConfig.STORE_ID_TUNEDGLOBAL, header)
    }

    @Test
    fun testStoreIdHeader_hasApiConfigurationTrue_tokenEmpty_returnStoreId() {
        every { apiConfigurationManager.hasApiConfiguration(any()) } returns true
        every { apiConfigurationManager.getUrl(any()) } returns retrofit.baseUrl().toUrl()
            .toString().removeSuffix("/")
        every { apiConfigurationManager.getToken(any()) } returns ""
        val testDataJson = "{\"name\":\"test\"}"
        val successResponse = MockResponse().setBody(testDataJson)
        val api = retrofit.create(MusicTestApiInterface::class.java)
        mockWebServer.enqueue(successResponse)
        api.test().execute()

        val request = mockWebServer.takeRequest()
        val header = request.getHeader(com.truedigital.features.listens.share.constant.MusicConstant.Intercepter.STORE_ID_HEADER)

        assertEquals(BuildConfig.STORE_ID_TUNEDGLOBAL, header)
    }
}
