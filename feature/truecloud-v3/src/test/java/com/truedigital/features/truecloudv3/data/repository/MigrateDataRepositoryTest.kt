package com.truedigital.features.truecloudv3.data.repository

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.core.extensions.collectSafe
import com.truedigital.core.utils.DataStoreUtil
import com.truedigital.features.truecloudv3.common.TrueCloudV3ErrorMessage
import com.truedigital.features.truecloudv3.common.TrueCloudV3ErrorMessage.ERROR_MIGRATION_MIGRATE
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3Interface
import com.truedigital.features.truecloudv3.data.model.DataResponse
import com.truedigital.features.truecloudv3.data.model.MigrateResponse
import com.truedigital.features.truecloudv3.data.model.MigrationResponse
import io.mockk.coEvery
import io.mockk.coVerify
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

class MigrateDataRepositoryTest {
    private lateinit var migrateDataRepository: MigrateDataRepository
    private var userRepository = mockk<UserRepository>()
    private val trueCloudV3Interface: TrueCloudV3Interface = mockk()
    private val mockDataStoreUtil: DataStoreUtil = mockk()

    @BeforeEach
    fun setUp() {
        migrateDataRepository = MigrateDataRepositoryImpl(
            trueCloudV3Interface,
            userRepository,
            mockDataStoreUtil
        )
        coEvery { userRepository.getSsoId() }.returns("ssoid")
    }

    @Test
    fun `test migrate error`() = runTest {
        // arrange
        val responseBody = "null".toResponseBody("application/json".toMediaTypeOrNull())
        coEvery {
            trueCloudV3Interface.migrateData(
                ssoid = "ssoid",
                obj = any()
            )
        } returns Response.error(400, responseBody)

        // act
        val flow = migrateDataRepository.migrate()

        // assert
        flow.catch { response ->
            assertEquals(ERROR_MIGRATION_MIGRATE, response.message)
        }.collect()
    }

    @Test
    fun `test migrate success`() = runTest {
        // arrange
        val migrationResponse = MigrationResponse(
            status = "",
            estimatedTimeToComplete = "estimatedTimeToComplete"
        )
        migrationResponse.status = "Migrating"
        migrationResponse.estimatedTimeToComplete = "TimeToComplete"
        val dataResponse = DataResponse(
            migration = migrationResponse
        )
        dataResponse.migration = migrationResponse
        val mockResponse = MigrateResponse(
            data = dataResponse,
            error = null,
            code = 1,
            message = "message",
            platformModule = "platformModule",
            reportDashboard = "reportDashboard"
        )
        mockResponse.data = dataResponse
        coEvery {
            trueCloudV3Interface.migrateData(
                ssoid = "ssoid",
                obj = any()
            )
        } returns Response.success(mockResponse)

        // act
        val flow = migrateDataRepository.migrate()

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
            assertEquals(mockResponse, response)
        }
        assertEquals(mockResponse.code, 1)
        assertEquals(mockResponse.message, "message")
        assertEquals(mockResponse.platformModule, "platformModule")
        assertEquals(mockResponse.reportDashboard, "reportDashboard")
        assertEquals(mockResponse.error, null)
    }

    @Test
    fun `test patchStatus error`() = runTest {
        // arrange
        val responseBody = "null".toResponseBody("application/json".toMediaTypeOrNull())

        coEvery {
            trueCloudV3Interface.updateMigrationStatus(
                ssoid = "ssoid",
                obj = any()
            )
        } returns Response.error(400, responseBody)

        // act
        val flow = migrateDataRepository.patchStatus()

        // assert
        flow.catch { response ->
            assertEquals(TrueCloudV3ErrorMessage.ERROR_MIGRATION_UPDATE, response.message)
        }.collect()
    }

    @Test
    fun `test patchStatus success`() = runTest {
        // arrange
        val dataResponse = DataResponse(
            migration = MigrationResponse(
                status = "Migrating"
            )
        )
        val mockResponse = MigrateResponse(
            data = dataResponse,
            error = null
        )

        coEvery {
            trueCloudV3Interface.updateMigrationStatus(
                ssoid = "ssoid",
                obj = any()
            )
        } returns Response.success(mockResponse)

        // act
        val flow = migrateDataRepository.patchStatus()

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
            assertEquals(mockResponse, response)
        }
    }

    @Test
    fun `test addFailDisplayTime success`() = runTest {
        // arrange
        coEvery {
            mockDataStoreUtil.putPreference(
                key = any<Preferences.Key<String>>(),
                value = any()
            )
        } returns Unit
        val mockTime = 1000L

        // act
        val flow = migrateDataRepository.addFailDisplayTime(mockTime)

        // assert
        coVerify(exactly = 1) {
            mockDataStoreUtil.putPreference(
                key = eq(longPreferencesKey("KEY_TURE_CLOUD_V3_FAIL_DISPLAY_TIME")),
                value = eq(mockTime)
            )
        }
    }

    @Test
    fun `test getFailDisplayTime success`() = runTest {
        // arrange
        val mockTime = 1000L
        coEvery {
            mockDataStoreUtil.getSinglePreference(
                longPreferencesKey("KEY_TURE_CLOUD_V3_FAIL_DISPLAY_TIME"),
                0L
            )
        } returns mockTime

        // act
        val response = migrateDataRepository.getFailDisplayTime()

        // assert
        assertEquals(mockTime, response)
    }
}
