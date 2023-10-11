package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.data.repository.CacheUploadTaskRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TrueCloudV3CacheRemoveTaskUseCaseTest {
    private val cacheUploadTaskRepository: CacheUploadTaskRepository = mockk()
    private lateinit var removeTaskUseCase: RemoveTaskUseCase
    private lateinit var removeAllTaskUseCase: RemoveAllTaskUseCase

    @BeforeEach
    fun setup() {
        removeTaskUseCase = RemoveTaskUseCaseImpl(
            cacheUploadTaskRepository = cacheUploadTaskRepository
        )
        removeAllTaskUseCase = RemoveAllTaskUseCaseImpl(
            cacheUploadTaskRepository = cacheUploadTaskRepository
        )
    }

    @Test
    fun `test execute return success`() = runTest {
        // arrange
        coEvery {
            cacheUploadTaskRepository.removeTask(any())
        } returns Unit

        // act
        removeTaskUseCase.execute(0)

        // assert
        coVerify(exactly = 1) {
            cacheUploadTaskRepository.removeTask(0)
        }
    }

    @Test
    fun `test executeRemoveAll return success`() = runTest {
        // arrange
        coEvery {
            cacheUploadTaskRepository.clearAllTask()
        } returns Unit

        // act
        removeAllTaskUseCase.execute()

        // assert
        coVerify(exactly = 1) {
            cacheUploadTaskRepository.clearAllTask()
        }
    }
}
