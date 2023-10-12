package com.truedigital.features.truecloudv3.domain.usecase

import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.extensions.collectSafe
import com.truedigital.core.injections.CoreComponent
import com.truedigital.core.utils.SharedPrefsUtils
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
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3Model
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
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TrueCloudV3CompleteUploadUseCaseTest {
    private lateinit var completeUploadUseCase: CompleteUploadUseCase
    private lateinit var completeUploadContactUseCase: CompleteUploadContactUseCase
    private var completeUploadRepository: CompleteUploadRepository = mockk()
    private val cacheUploadTaskRepository: CacheUploadTaskRepository = mockk()
    private val localizationRepository: LocalizationRepository = mockk()
    private val sharedPrefsUtils = mockk<SharedPrefsUtils>()

    @BeforeEach
    fun setup() {
        completeUploadUseCase = CompleteUploadUseCaseImpl(
            completeUploadRepository = completeUploadRepository,
            cacheUploadTaskRepository = cacheUploadTaskRepository,
        )
        completeUploadContactUseCase = CompleteUploadContactUseCaseImpl(
            completeUploadRepository = completeUploadRepository,
        )

        mockkObject(CoreComponent)
        every { CoreComponent.getInstance().getCoreSubComponent() } returns mockk()
        every {
            CoreComponent.getInstance().getCoreSubComponent().getLocalizationRepository()
        } returns mockk(relaxed = true)
    }
    @Disabled
    @Test
    fun `test CompleteUpload execute success`() = runTest {
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
        val trueCloudV3Model = TrueCloudV3Model(
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

        every { sharedPrefsUtils.get<String>(any()) } returns "test"
        val expected = trueCloudV3StorageData.convertToTrueCloudV3Model()
        coEvery {
            cacheUploadTaskRepository.updateTaskStatus(any(), TaskStatusType.COMPLETE)
        } returns Unit
        coEvery {
            cacheUploadTaskRepository.getTask(any())
        } returns uploadModel
        coEvery {
            completeUploadRepository.callComplete(any(), any())
        } answers {
            flow {
                emit(completeUploadResponse)
            }
        }

        // act
        val flow = completeUploadUseCase.execute(0)

        // assert
        flow?.collectSafe { response ->
            assertEquals(expected.id, response.id)
        }
    }

    @Test
    fun `test execute fail`() = runTest {
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
            completeUploadRepository.callComplete(any(), any())
        } answers {
            flow {
                emit(mockResponse)
            }
        }

        // act
        val flow = completeUploadUseCase.execute(0)

        // assert
        flow?.catch { throwable ->
            assertEquals(TrueCloudV3ErrorMessage.ERROR_COMPLETE_UPLOAD, throwable.message)
        }?.collect()
    }
    @Disabled
    @Test
    fun `test executeContact success`() = runTest {
        // arrange
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
        val mockResponse = CompleteUploadResponse(
            data = trueCloudV3StorageData,
            error = null,
        )

        every { sharedPrefsUtils.get<String>(any()) } returns "test"
        val expected = trueCloudV3StorageData.convertToTrueCloudV3Model()
        coEvery {
            completeUploadRepository.callComplete(any(), any())
        } answers {
            flow {
                emit(mockResponse)
            }
        }

        // act
        val flow = completeUploadContactUseCase.execute("abc")

        // assert
        flow.collectSafe { response ->
            assertEquals(expected.id, response.id)
        }
    }

    @Test
    fun `test executeContact fail`() = runTest {
        // arrange
        val mockResponse = CompleteUploadResponse(
            data = null,
            error = ErrorResponse(
                code = "ABC-123",
                message = "error message",
                cause = "unknown error",
            ),
        )
        coEvery {
            completeUploadRepository.callComplete(any(), any())
        } answers {
            flow {
                emit(mockResponse)
            }
        }

        // act
        val flow = completeUploadContactUseCase.execute("abc")

        // assert
        flow.catch { throwable ->
            assertEquals(TrueCloudV3ErrorMessage.ERROR_COMPLETE_UPLOAD, throwable.message)
        }.collect()
    }
}
