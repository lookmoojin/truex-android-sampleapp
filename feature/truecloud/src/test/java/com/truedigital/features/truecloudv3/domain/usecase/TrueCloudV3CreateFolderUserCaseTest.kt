package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.truecloudv3.data.model.CreateFolderResponse
import com.truedigital.features.truecloudv3.data.model.TrueCloudV3StorageData
import com.truedigital.features.truecloudv3.data.repository.CreateFolderRepository
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3Model
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TrueCloudV3CreateFolderUserCaseTest {
    private var createFolderRepository: CreateFolderRepository = mockk(relaxed = true)
    private lateinit var createFolderUserCase: CreateFolderUserCase

    @BeforeEach
    fun setup() {
        createFolderUserCase = CreateFolderUserCaseImpl(
            createFolderRepository = createFolderRepository
        )
    }

    @Test
    fun `test create folder execute success`() = runTest {
        // arrange
        val trueCloudV3StorageData = TrueCloudV3StorageData(
            id = "id_test"
        )
        val mockResponse = CreateFolderResponse(
            data = trueCloudV3StorageData,
            error = null
        )
        val responseModel = TrueCloudV3Model(
            id = trueCloudV3StorageData.id,
            parentObjectId = trueCloudV3StorageData.parentObjectId,
            objectType = trueCloudV3StorageData.objectType,
            name = trueCloudV3StorageData.name,
            size = trueCloudV3StorageData.size,
            mimeType = trueCloudV3StorageData.mimeType ?: "",
            category = trueCloudV3StorageData.category,
            coverImageKey = trueCloudV3StorageData.coverImageKey,
            coverImageSize = trueCloudV3StorageData.coverImageSize,
            updatedAt = trueCloudV3StorageData.updatedAt,
            createdAt = trueCloudV3StorageData.createdAt
        )
        coEvery {
            createFolderRepository.create(any(), any())
        } returns flow {
            mutableListOf(mockResponse)
        }

        // act
        val flow = createFolderUserCase.execute("parent", "name")

        // assert
        flow.collectSafe { response ->
            assertEquals(responseModel, response)
        }
    }
}
