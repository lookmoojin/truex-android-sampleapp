package com.truedigital.features.truecloudv3.data.repository

import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.truecloudv3.common.SortType
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3SearchFileInterface
import com.truedigital.features.truecloudv3.data.model.SearchFileResponse
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import retrofit2.Response
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SearchFileRepositoryTest {
    private lateinit var searchFileRepository: SearchFileRepository
    private val searchFileInterface: TrueCloudV3SearchFileInterface = mockk(relaxed = true)
    private val userRepository: UserRepository = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        searchFileRepository = SearchFileRepositoryImpl(
            searchFileInterface,
            userRepository
        )
    }

    @Test
    fun `test get search file success`() = runTest {
        val searchFileResponse = SearchFileResponse()
        coEvery {
            searchFileInterface.searchFile(any(), any(), any(), any(), any())
        } returns Response.success(searchFileResponse)

        coEvery {
            userRepository.getSsoId()
        } returns ""

        val flow = searchFileRepository.searchFile("", SortType.SORT_DATE_ASC, 5, 5)

        flow.collectSafe {
            assertEquals(searchFileResponse, it)
        }
    }

    @Test
    fun `test get search file error`() = runTest {
        val responseBody = "null".toResponseBody("application/json".toMediaTypeOrNull())
        coEvery {
            searchFileInterface.searchFile(any(), any(), any(), any(), any())
        } returns Response.error(500, responseBody)

        val flow = searchFileRepository.searchFile("", SortType.SORT_DATE_ASC, 5, 5)

        flow.catch {
            assertNotNull(it)
        }.collect()
    }

    @Test
    fun `test search with category success`() = runTest {
        val searchFileResponse = SearchFileResponse()
        coEvery {
            searchFileInterface.searchFileWithCategory(any(), any(), any(), any(), any(), any())
        } returns Response.success(searchFileResponse)
        coEvery {
            userRepository.getSsoId()
        } returns ""

        val flow = searchFileRepository.searchWithCategory(
            "",
            SortType.SORT_DATE_ASC,
            "",
            5,
            5
        )

        flow.collectSafe {
            assertEquals(searchFileResponse, it)
        }
    }

    @Test
    fun `test search with category fail`() = runTest {
        val responseBody = "null".toResponseBody("application/json".toMediaTypeOrNull())
        coEvery {
            searchFileInterface.searchFileWithCategory(any(), any(), any(), any(), any(), any())
        } returns Response.error(500, responseBody)

        val flow = searchFileRepository.searchWithCategory(
            "",
            SortType.SORT_DATE_ASC,
            "",
            5,
            5
        )

        flow.catch {
            assertNotNull(it)
        }.collect()
    }
}
