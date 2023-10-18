package com.truedigital.features.tuned.data.api

import com.google.gson.GsonBuilder
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.data.ObfuscatedKeyValueStoreInterface
import com.truedigital.features.tuned.data.get
import com.truedigital.features.tuned.data.user.model.ContentLanguage
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

internal class CountryLanguageInterceptorTest {

    private val sharedPreferences: ObfuscatedKeyValueStoreInterface = mock()
    private val mockWebServer = MockWebServer()

    private val client = OkHttpClient.Builder()
        .addInterceptor(ContentLanguageInterceptor(sharedPreferences))
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(mockWebServer.url("/"))
        .client(client)
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .build()

    @Test
    fun testCountryLanguageHeader_codeNotNull_returnValue() {
        whenever(sharedPreferences.get<User>(MusicUserRepositoryImpl.CURRENT_USER_KEY))
            .thenReturn(
                MockDataModel.mockUserTuned.copy(
                    contentLanguages = listOf(
                        ContentLanguage(
                            code = "1 1",
                            localDisplayName = "localDisplayName1"
                        ),
                        ContentLanguage(
                            code = "2",
                            localDisplayName = "localDisplayName2"
                        )
                    )
                )
            )

        val testDataJson = "{\"name\":\"test\"}"
        val successResponse = MockResponse().setBody(testDataJson)
        val api = retrofit.create(MusicTestApiInterface::class.java)
        mockWebServer.enqueue(successResponse)
        api.test().execute()

        val request = mockWebServer.takeRequest()
        val header = request.getHeader(ContentLanguageInterceptor.CONTENT_LANGUAGE_HEADER)
        assertEquals("11,2", header)
    }

    @Test
    fun testCountryLanguageHeader_codeEmpty_returnValue() {
        whenever(sharedPreferences.get<User>(MusicUserRepositoryImpl.CURRENT_USER_KEY))
            .thenReturn(
                MockDataModel.mockUserTuned.copy(
                    contentLanguages = listOf(
                        ContentLanguage(
                            code = " ",
                            localDisplayName = "localDisplayName1"
                        ),
                        ContentLanguage(
                            code = "",
                            localDisplayName = "localDisplayName2"
                        )
                    )
                )
            )

        val testDataJson = "{\"name\":\"test\"}"
        val successResponse = MockResponse().setBody(testDataJson)
        val api = retrofit.create(MusicTestApiInterface::class.java)
        mockWebServer.enqueue(successResponse)
        api.test().execute()

        val request = mockWebServer.takeRequest()
        val header = request.getHeader(ContentLanguageInterceptor.CONTENT_LANGUAGE_HEADER)
        assertEquals(",", header)
    }

    @Test
    fun testCountryLanguageHeader_contentLanguagesNull_returnNull() {
        whenever(sharedPreferences.get<User>(MusicUserRepositoryImpl.CURRENT_USER_KEY))
            .thenReturn(MockDataModel.mockUserTuned.copy(contentLanguages = null))

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
    fun testCountryLanguageHeader_userNull_returnNull() {
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
    fun testCountryLanguageHeader_exception_returnNull() {
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
