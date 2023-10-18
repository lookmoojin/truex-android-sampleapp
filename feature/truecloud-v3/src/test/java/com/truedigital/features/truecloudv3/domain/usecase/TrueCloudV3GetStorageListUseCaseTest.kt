package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.truecloudv3.common.SortType
import com.truedigital.features.truecloudv3.data.model.DataListStorageResponse
import com.truedigital.features.truecloudv3.data.model.DataUsage
import com.truedigital.features.truecloudv3.data.model.ListStorageResponse
import com.truedigital.features.truecloudv3.data.model.TrueCloudV3StorageData
import com.truedigital.features.truecloudv3.data.repository.GetTrueCloudStorageListRepository
import com.truedigital.features.truecloudv3.extension.convertToListTrueCloudV3Model
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TrueCloudV3GetStorageListUseCaseTest {

    private var getTrueCloudStorageListRepository: GetTrueCloudStorageListRepository = mockk(relaxed = true)
    private lateinit var getStorageListUseCase: GetStorageListUseCase
    private lateinit var getStorageListWithCategoryUseCase: GetStorageListWithCategoryUseCase

    @BeforeEach
    fun setup() {
        getStorageListUseCase = GetStorageListUseCaseImpl(
            getTrueCloudStorageListRepository = getTrueCloudStorageListRepository
        )
        getStorageListWithCategoryUseCase = GetStorageListWithCategoryUseCaseImpl(
            getTrueCloudStorageListRepository = getTrueCloudStorageListRepository
        )
    }

    @Test
    fun `test getList usecase execute success`() = runTest {
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
            getTrueCloudStorageListRepository.getList(
                any(),
                any(),
                10,
                25
            )
        } returns flow {
            mutableListOf(mockResponse)
        }

        // act
        val flow = getStorageListUseCase.execute(
            rootFolderId = "rootFolderId",
            sortType = SortType.SORT_DATE_DESC,
            skip = 0
        )

        // assert
        flow.collectSafe { response ->
            assertEquals(mutableListOf(trueCloudV3StorageData).convertToListTrueCloudV3Model().size, response.size)
        }
    }

    @Test
    fun `test execute WithCategory usecase execute success`() = runTest {
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
            getTrueCloudStorageListRepository.getListWithCategory(
                any(),
                any(),
                10,
                25
            )
        } returns flow {
            mutableListOf(mockResponse)
        }

        // act
        val flow = getStorageListWithCategoryUseCase.execute(
            category = "category",
            sortType = SortType.SORT_DATE_DESC,
            skip = 0
        )

        // assert
        flow.collectSafe { response ->
            assertEquals(mutableListOf(trueCloudV3StorageData).convertToListTrueCloudV3Model().size, response.size)
        }
    }
}
