package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.common.FileMimeType
import com.truedigital.features.truecloudv3.common.TaskStatusType
import com.truedigital.features.truecloudv3.data.repository.CacheUploadTaskRepository
import com.truedigital.features.truecloudv3.domain.model.TaskUploadModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TrueCloudV3AddCacheUploadTaskUseCaseTest {
    private val cacheUploadTaskRepository: CacheUploadTaskRepository = mockk()
    private lateinit var addUploadTaskUseCase: AddUploadTaskUseCase

    @BeforeEach
    fun setup() {
        addUploadTaskUseCase = AddUploadTaskUseCaseImpl(
            cacheUploadTaskRepository = cacheUploadTaskRepository
        )
    }

    @Test
    fun `test execute return success`() = runTest {
        // arrange
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 1L
        )
        coEvery {
            cacheUploadTaskRepository.addUploadTask(any())
        } returns Unit

        // act
        addUploadTaskUseCase.execute(taskUploadModel)

        // assert
        coVerify(exactly = 1) {
            cacheUploadTaskRepository.addUploadTask(any())
        }
    }
}
