package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.data.repository.MigrateDataRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class TrueCloudV3MigrateGetFailDisplayTimeUseCaseTest {
    private lateinit var getMigrateFailDisplayTimeUseCase: GetMigrateFailDisplayTimeUseCase
    private val migrateDataRepository: MigrateDataRepository = mockk()

    @BeforeEach
    fun setup() {
        getMigrateFailDisplayTimeUseCase = GetMigrateFailDisplayTimeUseCaseImpl(
            migrateDataRepository
        )
    }
    @Test
    fun `test getFailDisplayTime success`() = runTest {
        // arrange
        val mockTime = 1000L
        coEvery {
            migrateDataRepository.getFailDisplayTime()
        } returns mockTime

        // act
        val response = getMigrateFailDisplayTimeUseCase.execute()

        // assert
        assertEquals(mockTime, response)
    }

    @Test
    fun `test getFailDisplayTime error`() = runTest {
        // arrange
        val mockTime = 0L
        coEvery {
            migrateDataRepository.getFailDisplayTime()
        } returns mockTime

        // act
        val response = getMigrateFailDisplayTimeUseCase.execute()

        // assert
        assertEquals(mockTime, response)
    }
}
