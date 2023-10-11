package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.truecloudv3.common.TrueCloudV3ErrorMessage
import com.truedigital.features.truecloudv3.data.model.DataResponse
import com.truedigital.features.truecloudv3.data.model.ErrorResponse
import com.truedigital.features.truecloudv3.data.model.MigrateResponse
import com.truedigital.features.truecloudv3.data.model.MigrationResponse
import com.truedigital.features.truecloudv3.data.repository.MigrateDataRepository
import com.truedigital.features.truecloudv3.domain.model.MigrationModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class TrueCloudV3MigratePatchStatusUseCaseTest {
    private lateinit var patchMigrateStatusUseCase: PatchMigrateStatusUseCase
    private val migrateDataRepository: MigrateDataRepository = mockk()

    @BeforeEach
    fun setup() {
        patchMigrateStatusUseCase = PatchMigrateStatusUseCaseImpl(
            migrateDataRepository
        )
    }

    @Test
    fun `test patchStatus error`() = runTest {
        // arrange
        val mockResponse = MigrateResponse(
            data = null,
            error = ErrorResponse(
                code = "ABC-123",
                message = "error message",
                cause = "unknown error"
            )
        )
        coEvery { migrateDataRepository.patchStatus() } returns flowOf(
            mockResponse
        )

        // act
        val flow = patchMigrateStatusUseCase.execute()

        // assert
        flow.catch { throwable ->
            assertEquals(TrueCloudV3ErrorMessage.ERROR_MIGRATION_UPDATE, throwable.message)
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
        val expectResponse = MigrationModel(
            status = "Migrating"
        )
        coEvery {
            migrateDataRepository.patchStatus()
        } returns flowOf(
            mockResponse
        )

        // act
        val flow = patchMigrateStatusUseCase.execute()

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
            assertEquals(expectResponse, response)
        }
    }
}
