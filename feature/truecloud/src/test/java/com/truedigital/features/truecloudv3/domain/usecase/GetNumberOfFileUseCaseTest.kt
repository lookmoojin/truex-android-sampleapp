package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.truecloudv3.common.FileCategoryType
import com.truedigital.features.truecloudv3.common.SortType
import com.truedigital.features.truecloudv3.data.model.DataListStorageResponse
import com.truedigital.features.truecloudv3.data.model.DataUsage
import com.truedigital.features.truecloudv3.data.model.ListStorageResponse
import com.truedigital.features.truecloudv3.data.model.TrueCloudV3StorageData
import com.truedigital.features.truecloudv3.data.repository.GetTrueCloudStorageListRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GetNumberOfFileUseCaseTest {

    private var getTrueCloudStorageListRepository: GetTrueCloudStorageListRepository =
        mockk(relaxed = true)
    private lateinit var getNumberOfFileUseCase: GetNumberOfFileUseCase

    @BeforeEach
    fun setup() {
        getNumberOfFileUseCase = GetNumberOfFileUseCaseImpl(
            getTrueCloudStorageListRepository = getTrueCloudStorageListRepository
        )
    }

    @Test
    fun `test getList usecase execute success UNSUPPORTED_FORMAT`() = runTest {
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
        val flow = getNumberOfFileUseCase.execute(
            rootFolderId = "rootFolderId",
            category = FileCategoryType.UNSUPPORTED_FORMAT.type,
            sortType = SortType.SORT_DATE_DESC,
        )

        // assert
        flow.collectSafe { response ->
            assertEquals(1, response)
        }
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
        val flow = getNumberOfFileUseCase.execute(
            rootFolderId = "rootFolderId",
            category = FileCategoryType.IMAGE.type,
            sortType = SortType.SORT_DATE_DESC,
        )

        // assert
        flow.collectSafe { response ->
            assertEquals(1, response)
        }
    }
}
