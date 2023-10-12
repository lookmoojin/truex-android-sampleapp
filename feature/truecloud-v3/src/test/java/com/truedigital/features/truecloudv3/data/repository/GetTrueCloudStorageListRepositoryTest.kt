package com.truedigital.features.truecloudv3.data.repository

import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.truecloudv3.common.SortType
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3Interface
import com.truedigital.features.truecloudv3.data.model.DataListStorageResponse
import com.truedigital.features.truecloudv3.data.model.DataUsage
import com.truedigital.features.truecloudv3.data.model.ListStorageResponse
import com.truedigital.features.truecloudv3.data.model.TrueCloudV3StorageData
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

class GetTrueCloudStorageListRepositoryTest {

    private lateinit var getTrueCloudStorageListRepository: GetTrueCloudStorageListRepository
    private val trueCloudV3Interface: TrueCloudV3Interface = mockk()
    private val userRepository: UserRepository = mockk(relaxed = true)

    @BeforeEach
    fun setup() {
        getTrueCloudStorageListRepository = GetTrueCloudStorageListRepositoryImpl(
            trueCloudV3Interface = trueCloudV3Interface,
            userRepository = userRepository
        )
    }

    @Test
    fun `test get ObjectList return success`() = runTest {
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
            trueCloudV3Interface.getFileList(
                ssoid = any(),
                rootFolderId = any(),
                order = SortType.SORT_DATE_DESC.type,
                skip = 0,
                take = 25
            )
        } returns Response.success(
            mockResponse
        )

        // act
        val flow = getTrueCloudStorageListRepository.getList(
            rootFolderId = "root_id",
            sortType = SortType.SORT_DATE_DESC,
            skip = 0,
            take = 25
        )

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
            assertEquals(mockResponse, response)
        }
    }
    @Test
    fun `test getObjectListWithCategory return success`() = runTest {
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
            trueCloudV3Interface.getCategoryFileList(
                ssoid = any(),
                category = any(),
                order = SortType.SORT_DATE_DESC.type,
                skip = 0,
                take = 25
            )
        } returns Response.success(
            mockResponse
        )

        // act
        val flow = getTrueCloudStorageListRepository.getListWithCategory(
            category = "category",
            sortType = SortType.SORT_DATE_DESC,
            skip = 0,
            take = 25
        )

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
            assertEquals(mockResponse, response)
        }
    }

    @Test
    fun `test getObjectList with category error return data error`() = runTest {
        // arrange
        val responseBody = "null".toResponseBody("application/json".toMediaTypeOrNull())

        coEvery {
            trueCloudV3Interface.getCategoryFileList(
                ssoid = any(),
                category = any(),
                order = SortType.SORT_DATE_DESC.type,
                skip = 0,
                take = 25
            )
        } returns Response.error(400, responseBody)

        // act
        val flow = getTrueCloudStorageListRepository.getListWithCategory(
            category = "category",
            sortType = SortType.SORT_DATE_DESC,
            skip = 0,
            take = 25
        )

        // assert
        flow.catch { response ->
            assertNotNull(response)
        }.collect()
    }
    @Test
    fun `test getObjectList error return data error`() = runTest {
        // arrange
        val responseBody = "null".toResponseBody("application/json".toMediaTypeOrNull())

        coEvery {
            trueCloudV3Interface.getFileList(
                ssoid = any(),
                rootFolderId = any(),
                order = SortType.SORT_DATE_DESC.type,
                skip = 0,
                take = 25
            )
        } returns Response.error(400, responseBody)

        // act
        val flow = getTrueCloudStorageListRepository.getList(
            rootFolderId = "root_id",
            sortType = SortType.SORT_DATE_DESC,
            skip = 0,
            take = 25
        )

        // assert
        flow.catch { response ->
            assertNotNull(response)
        }.collect()
    }

    @Test
    fun `test getTrashList return success`() = runTest {
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
            trueCloudV3Interface.getTrashList(
                ssoid = any()
            )
        } returns Response.success(
            mockResponse
        )

        // act
        val flow = getTrueCloudStorageListRepository.getTrashList()

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
            assertEquals(mockResponse, response)
        }
    }

    @Test
    fun `test getTrashList error return data error`() = runTest {
        // arrange
        val responseBody = "null".toResponseBody("application/json".toMediaTypeOrNull())

        coEvery {
            trueCloudV3Interface.getTrashList(
                ssoid = any()
            )
        } returns Response.error(400, responseBody)

        // act
        val flow = getTrueCloudStorageListRepository.getTrashList()

        // assert
        flow.catch { response ->
            assertNotNull(response)
        }.collect()
    }
}
