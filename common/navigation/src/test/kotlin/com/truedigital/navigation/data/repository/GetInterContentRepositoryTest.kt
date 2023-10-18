package com.truedigital.navigation.data.repository

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.core.extensions.collectSafe
import com.truedigital.navigation.TestData
import com.truedigital.navigation.data.PersonaData
import com.truedigital.navigation.data.PersonaResponse
import com.truedigital.navigation.data.api.PersonaApiInterface
import com.truedigital.navigations.share.data.api.InterCdnApiInterface
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.HttpException
import retrofit2.Response
import kotlin.test.assertEquals

internal class GetInterContentRepositoryTest {

    private val api: InterCdnApiInterface = mock()
    private val personaApi: PersonaApiInterface = mock()

    private lateinit var repository: GetInterContentRepository

    @BeforeEach
    fun setUp() {
        repository = GetInterContentRepositoryImpl(api, personaApi)
    }

    @Test
    fun `get content feed successfully, expect return json array`() {
        val contentCount = 2
        runTest {
            whenever(api.getContent(any())).thenReturn(
                TestData.getShelfSkeletonJsonArray(contentCount)
            )

            val flow = repository.getContentFeed("")

            val content = flow.single()
            verify(api, times(1)).getContent(any())
            assertNotNull(content)
            assertTrue(content.isJsonArray)
            assertTrue(content.size() == contentCount)
        }
    }

    @Test
    fun `get content feed unsuccessfully, expect catching exception`() {
        val expectedException = TestData.Exception.http404
        runTest {
            whenever(api.getContent(any())).thenThrow(
                expectedException
            )

            repository.getContentFeed("").catch { e ->
                verify(api, times(1)).getContent(any())
                assertTrue(e == expectedException)
            }
        }
    }

    @Test
    fun `get content persona data successfully, expect return persona data`() = runTest {
        val mockResponse = PersonaResponse(
            code = 10001,
            data = PersonaData(
                url = "url",
                schemaId = "schemaId"
            )
        )
        val country = "th"
        val lang = "th"
        whenever(
            personaApi.getPersonaData(
                country = country,
                lang = lang,
                pageName = "today",
                placementId = "trueid-personalize-shelf-puerto"
            )
        ).thenReturn(Response.success(mockResponse))

        repository.getPersonaData("th", "th").collectSafe { personaData ->
            verify(personaApi, times(1)).getPersonaData(
                "th",
                "th",
                "today",
                "trueid-personalize-shelf-puerto"
            )
            assertNotNull(personaData)
            assertEquals("url", personaData.url)
            assertEquals("schemaId", personaData.schemaId)
        }
    }

    @Test
    fun `get content persona data unsuccessfully, expect catching exception`() = runTest {
        val mockResponse = PersonaResponse(
            code = 101,
            data = null
        )
        val country = "th"
        val lang = "th"
        whenever(
            personaApi.getPersonaData(
                country = country,
                lang = lang,
                pageName = "today",
                placementId = "trueid-personalize-shelf-puerto"
            )
        ).thenReturn(Response.success(mockResponse))

        repository.getPersonaData("th", "th").catch { e ->
            verify(personaApi, times(1)).getPersonaData(
                "th",
                "th",
                "today",
                "trueid-personalize-shelf-puerto"
            )
            assertTrue(e is HttpException)
        }
    }

    @Test
    fun `get content persona data null, expect catching exception`() = runTest {
        val mockResponse = PersonaResponse(
            code = 10001,
            data = null
        )
        val country = "th"
        val lang = "th"
        whenever(
            personaApi.getPersonaData(
                country = country,
                lang = lang,
                pageName = "today",
                placementId = "trueid-personalize-shelf-puerto"
            )
        ).thenReturn(Response.success(mockResponse))

        repository.getPersonaData("th", "th").catch { e ->
            verify(personaApi, times(1)).getPersonaData(
                "th",
                "th",
                "today",
                "trueid-personalize-shelf-puerto"
            )
            assertTrue(e is HttpException)
        }
    }

    @Test
    fun `get content persona data response error, expect catching exception`() = runTest {
        val country = "th"
        val lang = "th"
        whenever(
            personaApi.getPersonaData(
                country = country,
                lang = lang,
                pageName = "today",
                placementId = "trueid-personalize-shelf-puerto"
            )
        ).thenReturn(Response.error(400, "".toResponseBody()))

        repository.getPersonaData("th", "th").catch { e ->
            verify(personaApi, times(1)).getPersonaData(
                "th",
                "th",
                "today",
                "trueid-personalize-shelf-puerto"
            )
            assertTrue(e is HttpException)
        }
    }
}
