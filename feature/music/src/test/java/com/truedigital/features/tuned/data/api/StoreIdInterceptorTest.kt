package com.truedigital.features.tuned.data.api

import com.google.gson.GsonBuilder
import com.truedigital.features.tuned.application.configuration.Configuration
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

internal class StoreIdInterceptorTest {

    private val configuration: Configuration = mockk()
    private val mockWebServer = MockWebServer()

    private val client = OkHttpClient.Builder()
        .addInterceptor(StoreIdInterceptor(configuration))
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(mockWebServer.url("/"))
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .build()

    @Test
    fun testStoreIdHeader_returnValue() {
        every { configuration.storeId } returns "123"
        val testDataJson = "{\"name\":\"test\"}"
        val successResponse = MockResponse().setBody(testDataJson)
        val api = retrofit.create(MusicTestApiInterface::class.java)
        mockWebServer.enqueue(successResponse)
        api.test().execute()

        val request = mockWebServer.takeRequest()
        val header =
            request.getHeader(com.truedigital.features.listens.share.constant.MusicConstant.Intercepter.STORE_ID_HEADER)
        assertEquals("123", header)
    }
}
