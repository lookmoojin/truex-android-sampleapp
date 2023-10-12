package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.core.extensions.collectSafe
import com.truedigital.core.injections.CoreComponent
import com.truedigital.features.truecloudv3.common.FileMimeType
import com.truedigital.features.truecloudv3.common.TaskActionType
import com.truedigital.features.truecloudv3.common.TaskStatusType
import com.truedigital.features.truecloudv3.common.TrueCloudV3ErrorMessage
import com.truedigital.features.truecloudv3.data.model.CompleteUploadResponse
import com.truedigital.features.truecloudv3.data.model.ErrorResponse
import com.truedigital.features.truecloudv3.data.model.TrueCloudV3StorageData
import com.truedigital.features.truecloudv3.data.repository.CacheUploadTaskRepository
import com.truedigital.features.truecloudv3.data.repository.CompleteUploadRepository
import com.truedigital.features.truecloudv3.domain.model.TaskUploadModel
import com.truedigital.features.truecloudv3.extension.convertToTrueCloudV3Model
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CompleteReplaceUploadUseCaseTest {
    private var completeUploadRepository: CompleteUploadRepository = mockk()
    private val cacheUploadTaskRepository: CacheUploadTaskRepository = mockk()
    private lateinit var completeReplaceUploadUseCase: CompleteReplaceUploadUseCase

    @BeforeEach
    fun setUp() {
        completeReplaceUploadUseCase = CompleteReplaceUploadUseCaseImpl(
            completeUploadRepository = completeUploadRepository,
            cacheUploadTaskRepository = cacheUploadTaskRepository,
        )

        mockkObject(CoreComponent)
        every { CoreComponent.getInstance().getCoreSubComponent() } returns mockk()
        every {
            CoreComponent.getInstance().getCoreSubComponent().getLocalizationRepository()
        } returns mockk(relaxed = true)
    }

    @Test
    fun `test Complete Replace Upload success`() = runTest {
        // arrange
        val uploadModel = TaskUploadModel(
            id = 0,
            path = "",
            status = TaskStatusType.COMPLETE,
            size = "",
            name = "",
            type = FileMimeType.IMAGE,
            coverImageSize = 0,
            progress = 0L,
            updateAt = 0L,
            objectId = "",
            actionType = TaskActionType.UPLOAD,
        )
        val trueCloudV3StorageData = TrueCloudV3StorageData(
            id = "",
            parentObjectId = "",
            objectType = "",
            name = "",
            size = "",
            mimeType = "",
            category = "",
            coverImageKey = "",
            coverImageSize = "",
            updatedAt = "",
            createdAt = "",
        )
        val completeUploadResponse = CompleteUploadResponse(
            data = trueCloudV3StorageData,
            error = null,
        )
        val expected = trueCloudV3StorageData.convertToTrueCloudV3Model()
        coEvery {
            cacheUploadTaskRepository.updateTaskStatus(any(), TaskStatusType.COMPLETE)
        } returns Unit
        coEvery {
            cacheUploadTaskRepository.getTask(any())
        } returns uploadModel
        coEvery {
            completeUploadRepository.callReplaceComplete(any(), any())
        } answers {
            flow {
                emit(completeUploadResponse)
            }
        }

        // act
        val flow = completeReplaceUploadUseCase.execute(0)

        // assert
        flow.collectSafe { response ->
            assertEquals(expected.id, response.id)
        }
    }

    @Test
    fun `test Complete Replace Upload fail`() = runTest {
        // arrange
        val uploadModel = TaskUploadModel(
            id = 0,
            path = "",
            status = TaskStatusType.COMPLETE,
            size = "",
            name = "",
            type = FileMimeType.IMAGE,
            coverImageSize = 0,
            progress = 0L,
            updateAt = 0L,
            objectId = "",
            actionType = TaskActionType.UPLOAD,
        )
        val mockResponse = CompleteUploadResponse(
            data = null,
            error = ErrorResponse(
                code = "ABC-123",
                message = "error message",
                cause = "unknown error",
            ),
        )
        coEvery {
            cacheUploadTaskRepository.updateTaskStatus(any(), TaskStatusType.COMPLETE)
        } returns Unit
        coEvery {
            cacheUploadTaskRepository.getTask(any())
        } returns uploadModel
        coEvery {
            completeUploadRepository.callReplaceComplete(any(), any())
        } answers {
            flow {
                emit(mockResponse)
            }
        }

        // act
        val flow = completeReplaceUploadUseCase.execute(0)

        // assert
        flow.catch { throwable ->
            assertEquals(TrueCloudV3ErrorMessage.ERROR_COMPLETE_UPLOAD, throwable.message)
        }.collect()
    }
}
