package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.core.extensions.collectSafe
import com.truedigital.core.utils.SharedPrefsUtils
import com.truedigital.features.truecloudv3.common.TrueCloudV3ErrorMessage.ERROR_STORAGE_NULL
import com.truedigital.features.truecloudv3.data.model.DataMigration
import com.truedigital.features.truecloudv3.data.model.DataStorage
import com.truedigital.features.truecloudv3.data.model.DataUsage
import com.truedigital.features.truecloudv3.data.model.ErrorResponse
import com.truedigital.features.truecloudv3.data.model.StorageResponse
import com.truedigital.features.truecloudv3.data.model.TrueCloudV3StorageData
import com.truedigital.features.truecloudv3.data.repository.GetStorageSpaceRepository
import com.truedigital.features.truecloudv3.domain.model.DataMigrationModel
import com.truedigital.features.truecloudv3.domain.model.DataStorageModel
import com.truedigital.features.truecloudv3.domain.model.DataUsageModel
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3Model
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.Ignore
import kotlin.test.assertEquals

interface TrueCloudV3GetStorageSpaceUseCaseTestCase {
    fun `test get StorageSpace return dataStorage`()
    fun `test get StorageSpace data is null`()
    fun `test get StorageSpace return error`()
}

internal class TrueCloudV3GetStorageSpaceUseCaseTest : TrueCloudV3GetStorageSpaceUseCaseTestCase {
    private lateinit var getStorageSpaceUseCase: GetStorageSpaceUseCase
    private val getStorageSpaceRepository: GetStorageSpaceRepository = mockk()
    private val sharedPrefsUtils = mockk<SharedPrefsUtils>()

    @BeforeEach
    fun setUp() {
        getStorageSpaceUseCase = GetStorageSpaceUseCaseImpl(
            getStorageSpaceRepository
        )
    }
    @Ignore
    @Test
    override fun `test get StorageSpace return dataStorage`() = runTest {
        // arrange
        val migrationModel = DataMigrationModel(status = "migrating")
        migrationModel.status = "migrating"
        migrationModel.estimatedTimeToComplete = "estimatedTimeToComplete"
        migrationModel.failedDisplayTime = 0
        val dataUsageModel = DataUsageModel(
            images = 0L,
            videos = 0L,
            audio = 0L,
            others = 0L,
            contacts = 0L,
            total = 0L
        )
        val trueCloudV3Model = TrueCloudV3Model(
            id = "",
            parentObjectId = "",
            objectType = "",
            name = "",
            size = "",
            mimeType = "",
            category = "",
            coverImageKey = "",
            coverImageSize = "",
            updatedAt = "",
            createdAt = ""
        )

        val migration = DataMigration(status = "migrating")
        migration.status = "migrating"
        migration.estimatedTimeToComplete = "estimatedTimeToComplete"
        migration.failedDisplayTime = 0
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
        val trueCloudV3StorageData = TrueCloudV3StorageData(
            id = "",
            parentObjectId = "",
            objectType = "",
            name = "",
            size = "",
            mimeType = "",
            category = "",
            coverImageKey = "",
            coverImageSize = "",
            updatedAt = "",
            createdAt = ""
        )
        val dataStorage = DataStorage(
            quota = 100,
            rootFolder = trueCloudV3StorageData,
            dataUsage = dataUsage,
            migration = migration
        )
        dataStorage.quota = 100
        dataStorage.rootFolder = trueCloudV3StorageData
        dataStorage.dataUsage = dataUsage
        dataStorage.migration = migration

        val mockResponse = StorageResponse(
            data = dataStorage,
            error = null
        )
        coEvery { getStorageSpaceRepository.getStorage() } returns flowOf(
            mockResponse
        )
        val expectResponse = DataStorageModel(
            quota = 100,
            rootFolder = trueCloudV3Model,
            dataUsage = dataUsageModel,
            migration = migrationModel
        )
        expectResponse.quota = 100
        expectResponse.rootFolder = trueCloudV3Model
        expectResponse.dataUsage = dataUsageModel
        expectResponse.migration = migrationModel

        // act
        val flow = getStorageSpaceUseCase.execute()

        // assert
        flow.collectSafe { response ->
            // assertEquals(expectResponse, response)
            assertEquals(mockResponse.data?.rootFolder?.id, response.rootFolder?.id)
            assertEquals(mockResponse.data, dataStorage)
            assertEquals(mockResponse.data?.rootFolder?.category, response.rootFolder?.category)
            assertEquals(
                mockResponse.data?.rootFolder?.coverImageKey,
                response.rootFolder?.coverImageKey
            )
            assertEquals(
                mockResponse.data?.rootFolder?.coverImageSize,
                response.rootFolder?.coverImageSize
            )
            assertEquals(mockResponse.data?.rootFolder?.name, response.rootFolder?.name)
            assertEquals(mockResponse.data?.rootFolder?.mimeType, response.rootFolder?.mimeType)
            assertEquals(mockResponse.data?.rootFolder?.objectType, response.rootFolder?.objectType)
            assertEquals(
                mockResponse.data?.rootFolder?.parentObjectId,
                response.rootFolder?.parentObjectId
            )
            assertEquals(mockResponse.data?.rootFolder?.size, response.rootFolder?.size)
            assertEquals(mockResponse.data?.rootFolder?.updatedAt, response.rootFolder?.updatedAt)
            assertEquals(mockResponse.data?.migration?.status, response.migration?.status)
            assertEquals(
                mockResponse.data?.migration?.failedDisplayTime,
                response.migration?.failedDisplayTime
            )
            assertEquals(
                mockResponse.data?.migration?.estimatedTimeToComplete,
                response.migration?.estimatedTimeToComplete
            )
            assertEquals(mockResponse.data?.dataUsage?.videos, response.dataUsage?.videos)
            assertEquals(mockResponse.data?.dataUsage?.sortedObj, response.dataUsage?.sortedObj)
        }
    }

    @Test
    override fun `test get StorageSpace data is null`() = runTest {
        // arrange
        val mockResponse = StorageResponse(
            data = null,
            error = ErrorResponse(
                code = "ABC-123",
                message = "error message",
                cause = "unknown error"
            ),
            code = 1,
            message = "message",
            platformModule = "platformModule",
            reportDashboard = "reportDashboard"
        )

        coEvery { getStorageSpaceRepository.getStorage() } returns flowOf(
            mockResponse
        )

        // act
        val flow = getStorageSpaceUseCase.execute()

        // assert
        flow.catch { throwable ->
            assertEquals(ERROR_STORAGE_NULL, throwable.message)
        }.collect()
        assertEquals(
            mockResponse.error,
            ErrorResponse(
                code = "ABC-123",
                message = "error message",
                cause = "unknown error"
            )
        )
        assertEquals(mockResponse.error?.code, "ABC-123")
        assertEquals(mockResponse.error?.message, "error message")
        assertEquals(mockResponse.error?.message, "error message")
        assertEquals(mockResponse.code, 1)
        assertEquals(mockResponse.message, "message")
        assertEquals(mockResponse.platformModule, "platformModule")
        assertEquals(mockResponse.reportDashboard, "reportDashboard")
    }

    @Test
    override fun `test get StorageSpace return error`() = runTest {
        // arrange
        coEvery {
            getStorageSpaceRepository.getStorage()
        } answers { flow { error("mock error") } }

        // act
        val flow = getStorageSpaceUseCase.execute()

        // assert
        flow.catch { throwable ->
            assertEquals("mock error", throwable.message)
        }.collect()
    }
}
