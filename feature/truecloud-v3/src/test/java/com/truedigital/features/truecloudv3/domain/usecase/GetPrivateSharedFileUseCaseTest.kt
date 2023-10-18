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

class GetPrivateSharedFileUseCaseTest {
    private var getSharedFileRepository: GetSharedFileRepository = mockk(relaxed = true)
    private lateinit var getPrivateSharedFileUseCase: GetPrivateSharedFileUseCase

    @BeforeEach
    fun setup() {
        getPrivateSharedFileUseCase = GetPrivateSharedFileUseCaseImpl(
            getSharedFileRepository = getSharedFileRepository
        )
    }

    @Test
    fun `test getPrivateSharedFile success`() = runTest {
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
            getSharedFileRepository.getPrivateSharedObject("id", "password")
        } returns flowOf(mockResponse)

        // act
        val flow = getPrivateSharedFileUseCase.execute("id", "password")

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
            assertEquals("id", response.id)
        }
    }
}
