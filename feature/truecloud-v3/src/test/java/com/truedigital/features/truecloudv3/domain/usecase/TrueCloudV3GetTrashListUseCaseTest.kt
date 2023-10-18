package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.common.share.datalegacy.wrapper.ContextDataProviderWrapper
import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.truecloudv3.common.TrueCloudV3ErrorMessage
import com.truedigital.features.truecloudv3.data.model.DataListStorageResponse
import com.truedigital.features.truecloudv3.data.model.DataUsage
import com.truedigital.features.truecloudv3.data.model.ListStorageResponse
import com.truedigital.features.truecloudv3.data.model.TrueCloudV3StorageData
import com.truedigital.features.truecloudv3.data.repository.GetTrueCloudStorageListRepository
import com.truedigital.features.truecloudv3.extension.convertToListTrueCloudV3Model
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TrueCloudV3GetTrashListUseCaseTest {
    private var getTrueCloudStorageListRepository: GetTrueCloudStorageListRepository = mockk(relaxed = true)
    private val contextDataProviderWrapper: ContextDataProviderWrapper = mockk(relaxed = true)
    private lateinit var getTrashListUseCase: GetTrashListUseCase

    @BeforeEach
    fun setup() {
        getTrashListUseCase = GetTrashListUseCaseImpl(
            getTrueCloudStorageListRepository = getTrueCloudStorageListRepository,
            contextDataProviderWrapper = contextDataProviderWrapper
        )
    }

    @Test
    fun `test getTrashList execute success`() = runTest {
        // arrange
        val dataUsage = DataUsage(
            images = 0L,
            videos = 0L,
            audio = 0L,
            others = 0L,
            contacts = 0L,
            total = 0L,
            status = "",
            usagePercent = 0f
        )
        val trueCloudV3StorageData = TrueCloudV3StorageData()
        val dataListStorageResponse = DataListStorageResponse(
            uploaded = mutableListOf(trueCloudV3StorageData),
            storage = dataUsage
        )
        val mockResponse = ListStorageResponse(
            data = dataListStorageResponse,
            error = null
        )
        coEvery {
            getTrueCloudStorageListRepository.getTrashList()
        } returns flow {
            mutableListOf(mockResponse)
        }

        // act
        val flow = getTrashListUseCase.execute()
        // assert
        flow.collectSafe { response ->
            assertEquals(mutableListOf(trueCloudV3StorageData).convertToListTrueCloudV3Model(contextDataProviderWrapper).size, response.size)
        }
    }
    @Test
    fun `test getTrashList execute error`() = runTest {
        // arrange
        val dataUsage = DataUsage(
            images = 0L,
            videos = 0L,
            audio = 0L,
            others = 0L,
            contacts = 0L,
            total = 0L,
            status = "",
            usagePercent = 0f
        )
        val trueCloudV3StorageData = TrueCloudV3StorageData()
        val dataListStorageResponse = DataListStorageResponse(
            uploaded = mutableListOf(trueCloudV3StorageData),
            storage = dataUsage
        )
        val mockResponse = ListStorageResponse(
            data = dataListStorageResponse,
            error = null
        )
        coEvery {
            getTrueCloudStorageListRepository.getTrashList()
        } returns flow {
            error(TrueCloudV3ErrorMessage.ERROR_STORAGE_NULL)
        }

        // act
        val flow = getTrashListUseCase.execute()
        // assert
        flow.catch { error ->
            assertEquals(error.message, TrueCloudV3ErrorMessage.ERROR_STORAGE_NULL)
        }
    }
}
