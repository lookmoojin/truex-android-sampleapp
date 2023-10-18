package com.truedigital.features.music.api.interceptor

import com.google.gson.GsonBuilder
import com.truedigital.features.listens.share.constant.MusicConstant
import com.truedigital.features.tuned.BuildConfig
import com.truedigital.features.utils.MusicTestApiInterface
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.test.assertEquals

internal class MusicInterceptorTest {

    private val mockWebServer = MockWebServer()
    private val client = OkHttpClient.Builder()
        .addInterceptor(MusicInterceptor())
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(mockWebServer.url("/"))
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .build()

    @Test
    fun testStoreIdHeader_returnValue() {
        val testDataJson = "{\"name\":\"test\"}"
        val successResponse = MockResponse().setBody(testDataJson)
        val api = retrofit.create(MusicTestApiInterface::class.java)
        mockWebServer.enqueue(successResponse)
        api.test().execute()

        val request = mockWebServer.takeRequest()
        val header = request.getHeader(MusicConstant.Intercepter.STORE_ID_HEADER)

        assertEquals(BuildConfig.STORE_ID_TUNEDGLOBAL, header)
        assertEquals(null, request.failure)
    }
}
