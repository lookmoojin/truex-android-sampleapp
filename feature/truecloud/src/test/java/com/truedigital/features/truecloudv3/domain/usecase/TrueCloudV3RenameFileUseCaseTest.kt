package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.truecloudv3.data.model.RenameStorageResponse
import com.truedigital.features.truecloudv3.data.model.TrueCloudV3StorageData
import com.truedigital.features.truecloudv3.data.repository.RenameFileRepository
import com.truedigital.features.truecloudv3.extension.convertToTrueCloudV3Model
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TrueCloudV3RenameFileUseCaseTest {
    private var renameFileRepository: RenameFileRepository = mockk(relaxed = true)
    private lateinit var renameFileUseCase: RenameFileUseCase

    @BeforeEach
    fun setup() {
        renameFileUseCase = RenameFileUseCaseImpl(
            renameFileRepository = renameFileRepository
        )
    }

    @Test
    fun `test rename usecase execute success`() = runTest {
        // arrange
        val trueCloudV3StorageData = TrueCloudV3StorageData(
            id = "id_test"
        )
        val mockResponse = RenameStorageResponse(
            data = trueCloudV3StorageData,
            error = null
        )
        coEvery {
            renameFileRepository.rename(any(), any())
        } returns flow {
            mutableListOf(mockResponse)
        }

        // act
        val flow = renameFileUseCase.execute("parent", "name")

        // assert
        flow.collectSafe { response ->
            assertEquals(trueCloudV3StorageData.convertToTrueCloudV3Model().id, response.id)
        }
    }
}
