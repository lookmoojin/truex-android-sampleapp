package com.truedigital.features.tuned.data.api

import com.google.gson.GsonBuilder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.data.ObfuscatedKeyValueStoreInterface
import com.truedigital.features.tuned.data.get
import com.truedigital.features.tuned.data.user.model.User
import com.truedigital.features.tuned.data.user.repository.MusicUserRepositoryImpl
import com.truedigital.features.utils.MockDataModel
import com.truedigital.features.utils.MusicTestApiInterface
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.test.assertEquals

internal class CountryInterceptorTest {

    private val sharedPreferences: ObfuscatedKeyValueStoreInterface = mock()
    private val mockWebServer = MockWebServer()

    private val client = OkHttpClient.Builder()
        .addInterceptor(CountryInterceptor(sharedPreferences))
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(mockWebServer.url("/"))
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .build()

    @Test
    fun testCountryHeader_countryNotNull_returnValue() {
        val mockCountry = "th"
        whenever(sharedPreferences.get<User>(MusicUserRepositoryImpl.CURRENT_USER_KEY))
            .thenReturn(MockDataModel.mockUserTuned.copy(country = mockCountry))

        val testDataJson = "{\"name\":\"test\"}"
        val successResponse = MockResponse().setBody(testDataJson)
        val api = retrofit.create(MusicTestApiInterface::class.java)
        mockWebServer.enqueue(successResponse)
        api.test().execute()

        val request = mockWebServer.takeRequest()
        val header = request.getHeader(CountryInterceptor.COUNTRY_HEADER)
        assertEquals(mockCountry, header)
    }

    @Test
    fun testCountryHeader_countryNull_returnNull() {
        whenever(sharedPreferences.get<User>(MusicUserRepositoryImpl.CURRENT_USER_KEY))
            .thenReturn(MockDataModel.mockUserTuned.copy(country = null))

        val testDataJson = "{\"name\":\"test\"}"
        val successResponse = MockResponse().setBody(testDataJson)
        val api = retrofit.create(MusicTestApiInterface::class.java)
        mockWebServer.enqueue(successResponse)
        api.test().execute()

        val request = mockWebServer.takeRequest()
        val header = request.getHeader(CountryInterceptor.COUNTRY_HEADER)
        assertEquals(null, header)
    }

    @Test
    fun testCountryHeader_userNull_returnNull() {
        whenever(sharedPreferences.get<User>(MusicUserRepositoryImpl.CURRENT_USER_KEY))
            .thenReturn(null)

        val testDataJson = "{\"name\":\"test\"}"
        val successResponse = MockResponse().setBody(testDataJson)
        val api = retrofit.create(MusicTestApiInterface::class.java)
        mockWebServer.enqueue(successResponse)
        api.test().execute()

        val request = mockWebServer.takeRequest()
        val header = request.getHeader(CountryInterceptor.COUNTRY_HEADER)
        assertEquals(null, header)
    }

    @Test
    fun testCountryHeader_exception_returnNull() {
        whenever(sharedPreferences.get<User>(MusicUserRepositoryImpl.CURRENT_USER_KEY))
            .thenAnswer { throw Exception() }

        val testDataJson = "{\"name\":\"test\"}"
        val successResponse = MockResponse().setBody(testDataJson)
        val api = retrofit.create(MusicTestApiInterface::class.java)
        mockWebServer.enqueue(successResponse)
        api.test().execute()

        val request = mockWebServer.takeRequest()
        val header = request.getHeader(CountryInterceptor.COUNTRY_HEADER)
        assertEquals(null, header)
    }
}
