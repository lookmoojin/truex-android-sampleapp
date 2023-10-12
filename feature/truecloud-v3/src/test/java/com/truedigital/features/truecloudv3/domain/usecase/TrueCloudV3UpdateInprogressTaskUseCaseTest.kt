package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.features.truecloudv3.common.FileMimeType
import com.truedigital.features.truecloudv3.common.TaskStatusType
import com.truedigital.features.truecloudv3.data.repository.CacheUploadTaskRepository
import com.truedigital.features.truecloudv3.domain.model.TaskUploadModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TrueCloudV3UpdateInprogressTaskUseCaseTest {
    private val cacheUploadTaskRepository: CacheUploadTaskRepository = mockk()
    private lateinit var trueCloudV3UpdateInprogressTaskUseCase: TrueCloudV3UpdateInprogressTaskUseCase

    @BeforeEach
    fun setup() {
        trueCloudV3UpdateInprogressTaskUseCase = TrueCloudV3UpdateInprogressTaskUseCaseImpl(
            cacheUploadTaskRepository = cacheUploadTaskRepository
        )
    }

    @Test
    fun `test get UpdateInprogress ID success`() = runTest {
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
        val response = trueCloudV3UpdateInprogressTaskUseCase.execute(0)

        // assert
        assertEquals(expectResponse, response)
    }
}
