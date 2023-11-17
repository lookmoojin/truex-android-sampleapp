package com.truedigital.features.truecloudv3.domain.usecase

import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.truedigital.core.extensions.collectSafe
import com.truedigital.features.truecloudv3.common.FileMimeType
import com.truedigital.features.truecloudv3.common.TaskActionType
import com.truedigital.features.truecloudv3.common.TaskStatusType
import com.truedigital.features.truecloudv3.data.repository.UploadFileRepository
import com.truedigital.features.truecloudv3.domain.model.TaskUploadModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertNotNull

class TrueCloudV3UploadQueueUseCaseTest {
    private val uploadFileRepository: UploadFileRepository = mockk()
    lateinit var uploadQueueUseCase: UploadQueueUseCase

    @BeforeEach
    fun setup() {
        uploadQueueUseCase = UploadQueueUseCaseImpl(
            uploadFileRepository = uploadFileRepository
        )
    }

    @Test
    fun `test executeUploadFileWithTaskUseCase `() = runTest {
        // arrange
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.PAUSE,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            objectId = "1234321",
            actionType = TaskActionType.UPLOAD,
            updateAt = 0L
        )
        val transferObserver: TransferObserver = mockk()

        coEvery {
            uploadFileRepository.uploadMultiFileWithTask(any())
        } answers {
            flow {
                emit(transferObserver)
            }
        }
        // act
        val flow = uploadQueueUseCase.execute(listOf(taskUploadModel))

        // assert
        flow.collectSafe { response ->
            assertNotNull(response)
        }
    }
}
