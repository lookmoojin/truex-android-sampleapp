package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.truecloudv3.data.model.GetSharedFileResponseModel
import com.truedigital.features.truecloudv3.data.model.SharedFileData
import com.truedigital.features.truecloudv3.data.model.SharedFileObject
import com.truedigital.features.truecloudv3.data.repository.GetSharedFileRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GetPublicSharedFileUseCaseTest {

    private var getSharedFileRepository: GetSharedFileRepository = mockk(relaxed = true)
    private lateinit var getPublicSharedFileUseCase: GetPublicSharedFileUseCase

    @BeforeEach
    fun setup() {
        getPublicSharedFileUseCase = GetPublicSharedFileUseCaseImpl(
            getSharedFileRepository = getSharedFileRepository
        )
    }

    @Test
    fun `test getPublicSharedFile success`() = runTest {

        // arrange
        val sharedFileObject = SharedFileObject(
            id = "id"
        )
        val sharedFileData = SharedFileData(
            fileObject = sharedFileObject
        )
        val mockResponse = GetSharedFileResponseModel(
            data = sharedFileData,
        )
        coEvery {
            getSharedFileRepository.getPublicSharedObject("id")
        } returns flowOf(mockResponse)

        // act
        val flow = getPublicSharedFileUseCase.execute("id")

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
            assertEquals("id", response.id)
        }
    }

    @Test
    fun `test getPublicSharedFile private`() = runTest {

        // arrange
        val mockResponse = GetSharedFileResponseModel(
            statusMessage = "private",
            statusCode = 401
        )
        coEvery {
            getSharedFileRepository.getPublicSharedObject(any())
        } returns flowOf(mockResponse)

        // act
        val flow = getPublicSharedFileUseCase.execute("id")

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
            assertEquals(401, response.status)
            assertEquals("private", response.statusMessage)
        }
    }

    @Test
    fun `test getPublicSharedFile expired`() = runTest {

        // arrange
        val mockResponse = GetSharedFileResponseModel(
            statusMessage = "expired",
            statusCode = 403
        )
        coEvery {
            getSharedFileRepository.getPublicSharedObject(any())
        } returns flowOf(mockResponse)

        // act
        val flow = getPublicSharedFileUseCase.execute("id")

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
            assertEquals(mockResponse.statusCode, response.status)
            assertEquals(mockResponse.statusMessage, response.statusMessage)
        }
    }

    @Test
    fun `test getPublicSharedFile error`() = runTest {

        // arrange
        val mockResponse = GetSharedFileResponseModel(
            statusMessage = "error",
            statusCode = 500
        )
        coEvery {
            getSharedFileRepository.getPublicSharedObject(any())
        } returns flowOf(mockResponse)

        // act
        val flow = getPublicSharedFileUseCase.execute("id")

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
            assertEquals(mockResponse.statusCode, response.status)
            assertEquals(mockResponse.statusMessage, response.statusMessage)
        }
    }
}
