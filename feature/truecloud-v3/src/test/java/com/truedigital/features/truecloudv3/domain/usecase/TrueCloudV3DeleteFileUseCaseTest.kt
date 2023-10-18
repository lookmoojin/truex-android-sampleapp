package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.truecloudv3.data.model.DeleteStorageResponse
import com.truedigital.features.truecloudv3.data.model.TrueCloudV3StorageData
import com.truedigital.features.truecloudv3.data.repository.DeleteFileRepository
import com.truedigital.features.truecloudv3.extension.convertToTrueCloudV3Model
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TrueCloudV3DeleteFileUseCaseTest {

    private var deleteFileRepository: DeleteFileRepository = mockk(relaxed = true)
    private lateinit var deleteFileUseCase: DeleteFileUseCase

    @BeforeEach
    fun setup() {
        deleteFileUseCase = DeleteFileUseCaseImpl(
            deleteFileRepository = deleteFileRepository
        )
    }

    @Test
    fun `test execute delete usecase execute success`() = runTest {
        // arrange
        val TrueCloudV3StorageData = TrueCloudV3StorageData(
            id = "id_test"
        )
        val mockResponse = DeleteStorageResponse(
            data = TrueCloudV3StorageData,
            error = null
        )
        coEvery {
            deleteFileRepository.delete(any())
        } returns flow {
            emit(mockResponse)
        }

        // act
        val flow = deleteFileUseCase.execute("id_test")

        // assert
        flow.collectSafe { response ->
            assertEquals(mockResponse.data.convertToTrueCloudV3Model(), response)
        }
    }
}
