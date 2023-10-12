package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.data.repository.MigrateDataRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TrueCloudV3MigrateSetFailDisplayTimeUseCaseTest {
    private lateinit var setMigrateFailDisplayTimeUseCase: SetMigrateFailDisplayTimeUseCase
    private val migrateDataRepository: MigrateDataRepository = mockk()

    @BeforeEach
    fun setup() {
        setMigrateFailDisplayTimeUseCase = SetMigrateFailDisplayTimeUseCaseImpl(
            migrateDataRepository
        )
    }

    @Test
    fun `test addFailDisplayTime success`() = runTest {
        // arrange
        val mockTime = 1000L
        coEvery {
            migrateDataRepository.addFailDisplayTime(any())
        } returns Unit

        // act
        setMigrateFailDisplayTimeUseCase.execute(mockTime)

        // assert
        coVerify(exactly = 1) {
            migrateDataRepository.addFailDisplayTime(mockTime)
        }
    }
}
