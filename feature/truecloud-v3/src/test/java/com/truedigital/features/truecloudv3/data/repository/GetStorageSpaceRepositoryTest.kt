package com.truedigital.features.truecloudv3.data.repository

import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3Interface
import com.truedigital.features.truecloudv3.data.model.DataMigration
import com.truedigital.features.truecloudv3.data.model.DataStorage
import com.truedigital.features.truecloudv3.data.model.DataUsage
import com.truedigital.features.truecloudv3.data.model.StorageResponse
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

internal interface GetStorageSpaceRepositoryTestCase {
    fun `test get Storage isSuccessful return StorageResponse`()
    fun `test get Storage error return data error`()
}

internal class GetStorageSpaceRepositoryTest : GetStorageSpaceRepositoryTestCase {
    private lateinit var getStorageSpaceRepository: GetStorageSpaceRepository
    private var userRepository = mockk<UserRepository>()
    private val trueCloudV3Interface: TrueCloudV3Interface = mockk()

    @BeforeEach
    fun setUp() {
        getStorageSpaceRepository = GetStorageSpaceRepositoryImpl(
            trueCloudV3Interface,
            userRepository
        )
        coEvery { userRepository.getSsoId() }.returns("ssoid")
    }

    @Test
    override fun `test get Storage isSuccessful return StorageResponse`() = runTest {
        val dataUsage = DataUsage(
            images = 0L,
            videos = 0L,
            audio = 0L,
            others = 0L,
            contacts = 0L,
            total = 0L,
            status = "",
            usagePercent = 0f,
        )
        dataUsage.status = ""
        dataUsage.usagePercent = 1f

        val migration = DataMigration(
            status = "Migrated"
        )
        val trueCloudV3StorageData = TrueCloudV3StorageData()
        val dataStorage = DataStorage(
            quota = 100,
            rootFolder = trueCloudV3StorageData,
            dataUsage = dataUsage,
            migration = migration
        )
        val mockResponse = StorageResponse(
            data = dataStorage,
            error = null,

        )
        mockResponse.data = dataStorage
        coEvery {
            trueCloudV3Interface.storage(
                ssoid = "ssoid"
            )
        } returns Response.success(
            mockResponse
        )

        // act
        val flow = getStorageSpaceRepository.getStorage()

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
            assertEquals(mockResponse, response)
            assertEquals(mockResponse.error, null)
            assertEquals(mockResponse.code, response.code)
            assertEquals(mockResponse.message, response.message)
            assertEquals(mockResponse.platformModule, response.platformModule)
            assertEquals(mockResponse.reportDashboard, response.reportDashboard)
            assertEquals(mockResponse.data?.dataUsage?.status, response.data?.dataUsage?.status)
            assertEquals(mockResponse.data?.dataUsage?.usagePercent, response.data?.dataUsage?.usagePercent)
        }
    }

    @Test
    override fun `test get Storage error return data error`() = runTest {
        // arrange
        val responseBody = "null".toResponseBody("application/json".toMediaTypeOrNull())

        coEvery {
            trueCloudV3Interface.storage(
                ssoid = "ssoid"
            )
        } returns Response.error(400, responseBody)

        // act
        val flow = getStorageSpaceRepository.getStorage()

        // assert
        flow.catch { response ->
            assertNotNull(response)
        }.collect()
    }
}
