package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.common.TaskStatusType
import com.truedigital.features.truecloudv3.data.repository.CacheUploadTaskRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TrueCloudV3UpdateStatusTaskUploadUseCaseTest {

    private val cacheUploadTaskRepository: CacheUploadTaskRepository = mockk()
    private lateinit var updateTaskUploadStatusUseCase: UpdateTaskUploadStatusUseCase

    @BeforeEach
    fun setup() {
        updateTaskUploadStatusUseCase = UpdateTaskUploadStatusUseCaseImpl(
            cacheUploadTaskRepository = cacheUploadTaskRepository
        )
    }

    @Test
    fun `test get UpdateStatusTask success`() = runTest {
        // arrange
        coEvery {
            cacheUploadTaskRepository.updateTaskStatus(any(), any())
        } returns Unit

        // act
        updateTaskUploadStatusUseCase.execute(0, TaskStatusType.COMPLETE)

        // assert
        coVerify(exactly = 1) {
            cacheUploadTaskRepository.updateTaskStatus(any(), any())
        }
    }
}
