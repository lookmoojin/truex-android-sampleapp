package com.truedigital.features.tuned.data.api

import com.google.gson.GsonBuilder
import com.truedigital.features.utils.MusicTestApiInterface
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.test.assertEquals

internal class CancelledRequestInterceptorTest {

    private val mockWebServer = MockWebServer()

    private val client = OkHttpClient.Builder()
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(mockWebServer.url("/"))
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .build()

    @Test
    fun testCancelledRequest_success() {
        val testDataJson = "{\"name\":\"test\"}"
        val successResponse = MockResponse().setBody(testDataJson)
        val api = retrofit.create(MusicTestApiInterface::class.java)
        mockWebServer.enqueue(successResponse)
        api.test().execute()

        val request = mockWebServer.takeRequest()
        assertEquals(null, request.failure)
    }
}
