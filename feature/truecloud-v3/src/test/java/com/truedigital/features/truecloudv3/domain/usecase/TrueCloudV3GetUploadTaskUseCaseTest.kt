package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.truecloudv3.common.FileMimeType
import com.truedigital.features.truecloudv3.common.TaskStatusType
import com.truedigital.features.truecloudv3.data.repository.CacheUploadTaskRepository
import com.truedigital.features.truecloudv3.domain.model.TaskUploadModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TrueCloudV3GetUploadTaskUseCaseTest {
    private val cacheUploadTaskRepository: CacheUploadTaskRepository = mockk()
    private lateinit var getUploadTaskUseCase: GetUploadTaskUseCase
    private lateinit var getNewUploadTaskListUseCase: GetNewUploadTaskListUseCase
    private lateinit var getUploadTaskListUseCase: GetUploadTaskListUseCase

    @BeforeEach
    fun setup() {
        getUploadTaskUseCase = GetUploadTaskUseCaseImpl(
            cacheUploadTaskRepository = cacheUploadTaskRepository
        )
        getNewUploadTaskListUseCase = GetNewUploadTaskListUseCaseImpl(
            cacheUploadTaskRepository = cacheUploadTaskRepository
        )
        getUploadTaskListUseCase = GetUploadTaskListUseCaseImpl(
            cacheUploadTaskRepository = cacheUploadTaskRepository
        )
    }

    @Test
    fun `test get upload task execute success`() = runTest {
        // arrange
        val expectResponse = TaskUploadModel(
            id = 2,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 0L
        )
        coEvery {
            cacheUploadTaskRepository.getRefreshTasks()
        } returns flow {
            mutableListOf(expectResponse)
        }

        // act
        val flow = getNewUploadTaskListUseCase.execute()

        // assert
        flow.collectSafe { response ->
            assertEquals(mutableListOf(expectResponse), response)
        }
    }

    @Test
    fun `test get upload task executeRefresh success`() = runTest {
        // arrange
        val expectResponse = TaskUploadModel(
            id = 3,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 0L
        )
        coEvery {
            cacheUploadTaskRepository.getRefreshTasks()
        } returns flow {
            mutableListOf(expectResponse)
        }

        // act
        val flow = getUploadTaskListUseCase.execute()

        // assert
        flow.collectSafe { response ->
            assertEquals(mutableListOf(expectResponse), response)
        }
    }

    @Test
    fun `test get upload task executeID success`() = runTest {
        // arrange
        val expectResponse = TaskUploadModel(
            id = 2,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 0L
        )
        coEvery {
            cacheUploadTaskRepository.getTask(any())
        } returns expectResponse

        // act
        val response = getUploadTaskUseCase.execute(0)

        // assert
        assertEquals(expectResponse, response)
    }
}
