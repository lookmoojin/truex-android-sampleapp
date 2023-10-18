package com.truedigital.features.tuned.data.api

import android.content.Context
import android.content.Intent
import com.google.gson.GsonBuilder
import com.truedigital.features.tuned.data.ObfuscatedKeyValueStoreInterface
import com.truedigital.features.tuned.data.device.repository.DeviceRepository
import com.truedigital.features.tuned.data.user.repository.MusicUserRepositoryImpl
import com.truedigital.features.tuned.presentation.common.TunedActivity
import com.truedigital.features.utils.MusicTestApiInterface
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.spyk
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.StringContains
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import kotlin.test.assertEquals

internal class ConnectivityInterceptorTest {

    private val context: Context = spyk()
    private val sharedPreferences: ObfuscatedKeyValueStoreInterface = mockk()
    private val deviceRepository: DeviceRepository = mockk()
    private val mockWebServer = MockWebServer()

    private val client = OkHttpClient.Builder()
        .addInterceptor(ConnectivityInterceptor(context, sharedPreferences, deviceRepository))
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(mockWebServer.url("/"))
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .build()

    @Test
    fun testRequest_containUserKeyTrue_isNetworkConnectedTrue_returnNotFailure() {
        every { sharedPreferences.contains(MusicUserRepositoryImpl.CURRENT_USER_KEY) } returns true
        every { deviceRepository.isNetworkConnected() } returns true
        val testDataJson = "{\"name\":\"test\"}"
        val successResponse = MockResponse().setBody(testDataJson)
        val api = retrofit.create(MusicTestApiInterface::class.java)
        mockWebServer.enqueue(successResponse)
        api.test().execute()

        val request = mockWebServer.takeRequest()
        assertEquals(null, request.failure)
    }

    @Test
    fun testRequest_containUserKeyFalse_isNetworkConnectedTrue_returnNotFailure() {
        every { sharedPreferences.contains(MusicUserRepositoryImpl.CURRENT_USER_KEY) } returns false
        every { deviceRepository.isNetworkConnected() } returns true

        val testDataJson = "{\"name\":\"test\"}"
        val successResponse = MockResponse().setBody(testDataJson)
        val api = retrofit.create(MusicTestApiInterface::class.java)
        mockWebServer.enqueue(successResponse)
        api.test().execute()

        val request = mockWebServer.takeRequest()
        assertEquals(null, request.failure)
    }

    @Test
    fun testRequest_containUserKeyFalse_isNetworkConnectedFalse_returnNotFailure() {
        every { sharedPreferences.contains(MusicUserRepositoryImpl.CURRENT_USER_KEY) } returns false
        every { deviceRepository.isNetworkConnected() } returns false

        val testDataJson = "{\"name\":\"test\"}"
        val successResponse = MockResponse().setBody(testDataJson)
        val api = retrofit.create(MusicTestApiInterface::class.java)
        mockWebServer.enqueue(successResponse)
        api.test().execute()

        val request = mockWebServer.takeRequest()
        assertEquals(null, request.failure)
    }

    @Test
    fun testRequest_containUserKeyTrue_isNetworkConnectedFalse_returnFailure() {
        every { sharedPreferences.contains(MusicUserRepositoryImpl.CURRENT_USER_KEY) } returns true
        every { deviceRepository.isNetworkConnected() } returns false
        every { context.sendBroadcast(Intent(TunedActivity.LOSS_OF_NETWORK_INTENT)) } just runs

        val testDataJson = "{\"name\":\"test\"}"
        val successResponse = MockResponse().setBody(testDataJson)
        val api = retrofit.create(MusicTestApiInterface::class.java)
        mockWebServer.enqueue(successResponse)

        try {
            api.test().execute()
            fail("IOException expected")
        } catch (e: IOException) {
            assertThat(e.message, StringContains("No network connected"))
        }
    }
}
