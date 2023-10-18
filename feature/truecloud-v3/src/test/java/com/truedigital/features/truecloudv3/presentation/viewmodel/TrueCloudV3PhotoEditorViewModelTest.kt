package com.truedigital.features.truecloudv3.presentation.viewmodel

import android.content.Context
import android.net.Uri
import com.jraska.livedata.TestObserver
import com.truedigital.common.share.datalegacy.wrapper.ContextDataProviderWrapper
import com.truedigital.core.coroutines.TestCoroutineDispatcherProvider
import com.truedigital.features.truecloudv3.common.TrueCloudV3TransferState
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3Model
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3TransferObserver
import com.truedigital.features.truecloudv3.domain.usecase.CompleteReplaceUploadUseCase
import com.truedigital.features.truecloudv3.domain.usecase.DownloadType
import com.truedigital.features.truecloudv3.domain.usecase.DownloadUseCase
import com.truedigital.features.truecloudv3.domain.usecase.RemoveTaskUseCase
import com.truedigital.features.truecloudv3.domain.usecase.ReplaceFileUploadUseCase
import com.truedigital.features.truecloudv3.domain.usecase.UpdateTaskUploadStatusUseCase
import com.truedigital.features.truecloudv3.navigation.PhotoEditorToPhotoEditorAdjust
import com.truedigital.features.truecloudv3.navigation.PhotoEditorToPhotoEditorFocus
import com.truedigital.features.truecloudv3.navigation.PhotoEditorToPhotoEditorText
import com.truedigital.features.truecloudv3.navigation.PhotoEditorToPhotoEditorTransform
import com.truedigital.features.truecloudv3.navigation.router.TrueCloudV3FileViewerRouterUseCase
import com.truedigital.features.truecloudv3.provider.FileProvider
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.coroutines.TestCoroutinesExtension
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.slot
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import java.io.File

@ExtendWith(InstantTaskExecutorExtension::class)
class TrueCloudV3PhotoEditorViewModelTest {
    @ExperimentalCoroutinesApi
    @RegisterExtension
    @JvmField
    val testCoroutine = TestCoroutinesExtension()
    private val coroutineDispatcher = TestCoroutineDispatcherProvider(testCoroutine.dispatcher)
    private val downloadUseCase: DownloadUseCase = mockk()
    private val fileProvider = mockk<FileProvider>(relaxed = true)
    private val contextDataProviderWrapper: ContextDataProviderWrapper = mockk()
    private val router: TrueCloudV3FileViewerRouterUseCase = mockk()
    private val replaceFileUploadUseCase: ReplaceFileUploadUseCase = mockk()
    private val completeReplaceUploadUseCase: CompleteReplaceUploadUseCase = mockk()
    private val removeTaskUseCase: RemoveTaskUseCase = mockk(relaxed = true)
    private val updateTaskUploadStatusUseCase: UpdateTaskUploadStatusUseCase = mockk(relaxed = true)
    private val context = mockk<Context>()
    private val mockFile = mockk<File>()
    private val mockUri = mockk<Uri>()
    private lateinit var viewModel: TrueCloudV3PhotoEditorViewModel

    @BeforeEach
    fun setUp() {
        viewModel = TrueCloudV3PhotoEditorViewModel(
            coroutineDispatcher = coroutineDispatcher,
            downloadUseCase = downloadUseCase,
            fileProvider = fileProvider,
            contextDataProviderWrapper = contextDataProviderWrapper,
            router = router,
            replaceFileUploadUseCase = replaceFileUploadUseCase,
            completeReplaceUploadUseCase = completeReplaceUploadUseCase,
            removeTaskUseCase = removeTaskUseCase,
            updateTaskUploadStatusUseCase = updateTaskUploadStatusUseCase,
        )

        every {
            contextDataProviderWrapper.get().getDataContext()
        } returns context
        every {
            context.cacheDir
        } returns mockFile

        every {
            mockFile.absolutePath
        } returns "/cacheDir/absolutePath"
        every {
            mockFile.exists()
        } returns true
        every {
            mockFile.path
        } returns "path"
        every {
            mockFile.delete()
        } returns true

        mockkStatic(Uri::class)
        every { Uri.fromFile(any()) } returns mockUri
        every { mockUri.path } returns "path"
    }

    @Test
    fun `test setObjectFile success`() = runTest {
        // arrange
        val file = TrueCloudFilesModel.File(
            id = "id",
            parentObjectId = "parentObjectId",
        )
        val testObserver = TestObserver.test(viewModel.imagePreview)
        mockDownloadPreviewSuccess()

        // act
        viewModel.setObjectFile(file)

        // assert
        coVerify(exactly = 1) { downloadUseCase.execute(any(), any(), DownloadType.SHARE) }
        testObserver.assertHasValue()
    }

    @Test
    fun `test setObjectFile onError`() = runTest {
        // arrange
        val file = TrueCloudFilesModel.File(
            id = "id",
            parentObjectId = "parentObjectId",
        )
        val testObserver = TestObserver.test(viewModel.imagePreview)
        mockDownloadPreviewError()

        // act
        viewModel.setObjectFile(file)

        // assert
        coVerify(exactly = 1) { downloadUseCase.execute(any(), any(), DownloadType.SHARE) }
        testObserver.assertNoValue()
    }

    @Test
    fun `test onTransformClick`() = runTest {
        // arrange
        val file = TrueCloudFilesModel.File(
            id = "id",
            parentObjectId = "parentObjectId",
        )
        val testObserver = TestObserver.test(viewModel.imagePreview)
        mockDownloadPreviewSuccess()
        every { router.execute(destination = any(), bundle = any()) } just runs
        viewModel.setObjectFile(file)

        // act
        viewModel.onTransformClick()

        // assert
        coVerify(exactly = 1) { router.execute(PhotoEditorToPhotoEditorTransform, any()) }
        testObserver.assertHasValue()
    }

    @Test
    fun `test onAdjustClick`() = runTest {
        // arrange
        val file = TrueCloudFilesModel.File(
            id = "id",
            parentObjectId = "parentObjectId",
        )
        val testObserver = TestObserver.test(viewModel.imagePreview)
        mockDownloadPreviewSuccess()
        every { router.execute(destination = any(), bundle = any()) } just runs
        viewModel.setObjectFile(file)

        // act
        viewModel.onAdjustClick()

        // assert
        coVerify(exactly = 1) { router.execute(PhotoEditorToPhotoEditorAdjust, any()) }
        testObserver.assertHasValue()
    }

    @Test
    fun `test onFocusClick`() = runTest {
        // arrange
        val file = TrueCloudFilesModel.File(
            id = "id",
            parentObjectId = "parentObjectId",
        )
        val testObserver = TestObserver.test(viewModel.imagePreview)
        mockDownloadPreviewSuccess()
        every { router.execute(destination = any(), bundle = any()) } just runs
        viewModel.setObjectFile(file)

        // act
        viewModel.onFocusClick()

        // assert
        coVerify(exactly = 1) { router.execute(PhotoEditorToPhotoEditorFocus, any()) }
        testObserver.assertHasValue()
    }

    @Test
    fun `test onTextClick`() = runTest {
        // arrange
        val file = TrueCloudFilesModel.File(
            id = "id",
            parentObjectId = "parentObjectId",
        )
        val testObserver = TestObserver.test(viewModel.imagePreview)
        mockDownloadPreviewSuccess()
        every { router.execute(destination = any(), bundle = any()) } just runs
        viewModel.setObjectFile(file)

        // act
        viewModel.onTextClick()

        // assert
        coVerify(exactly = 1) { router.execute(PhotoEditorToPhotoEditorText, any()) }
        testObserver.assertHasValue()
    }

    @Test
    fun `test onSaveClick success`() = runTest {
        // arrange
        val file = TrueCloudFilesModel.File(
            id = "id",
            parentObjectId = "parentObjectId",
        )
        val imagePreviewObserver = TestObserver.test(viewModel.imagePreview)
        mockDownloadPreviewSuccess()
        viewModel.setObjectFile(file)

        val onShowLoading = TestObserver.test(viewModel.onShowLoading)
        val onUploadComplete = TestObserver.test(viewModel.onUploadComplete)
        val onUploadError = TestObserver.test(viewModel.onUploadError)
        val trueCloudV3Model = TrueCloudV3Model(
            id = "id",
            name = "name test",
            objectType = "FOLDER",
            parentObjectId = "b"
        )
        val transferObserver: TrueCloudV3TransferObserver = mockk()
        every { replaceFileUploadUseCase.execute(any(), any()) } returns flowOf(transferObserver)
        val slotListener = slot<TrueCloudV3TransferObserver.TrueCloudV3TransferListener>()
        every {
            transferObserver.setTransferListener(capture(slotListener))
        } answers {
            slotListener.captured.onStateChanged(0, TrueCloudV3TransferState.COMPLETED)
        }
        every { completeReplaceUploadUseCase.execute(any()) } returns flowOf(trueCloudV3Model)

        // act
        viewModel.onSaveClick()

        // assert
        coVerify(exactly = 1) { completeReplaceUploadUseCase.execute(any()) }
        coVerify(exactly = 1) { removeTaskUseCase.execute(any()) }
        coVerify(exactly = 0) { updateTaskUploadStatusUseCase.execute(any(), any()) }
        imagePreviewObserver.assertHasValue()
        onShowLoading.assertValueHistory(true, false)
        onUploadComplete.assertHasValue()
        onUploadError.assertNoValue()
    }

    @Test
    fun `test onSaveClick upload fail`() = runTest {
        // arrange
        val file = TrueCloudFilesModel.File(
            id = "id",
            parentObjectId = "parentObjectId",
        )
        val imagePreviewObserver = TestObserver.test(viewModel.imagePreview)
        mockDownloadPreviewSuccess()
        viewModel.setObjectFile(file)

        val onShowLoading = TestObserver.test(viewModel.onShowLoading)
        val onUploadComplete = TestObserver.test(viewModel.onUploadComplete)
        val onUploadError = TestObserver.test(viewModel.onUploadError)
        val transferObserver: TrueCloudV3TransferObserver = mockk()
        every { replaceFileUploadUseCase.execute(any(), any()) } returns flowOf(transferObserver)
        val slotListener = slot<TrueCloudV3TransferObserver.TrueCloudV3TransferListener>()
        every {
            transferObserver.setTransferListener(capture(slotListener))
        } answers {
            slotListener.captured.onError(0, Exception())
        }

        // act
        viewModel.onSaveClick()

        // assert
        coVerify(exactly = 1) { updateTaskUploadStatusUseCase.execute(any(), any()) }
        coVerify(exactly = 0) { completeReplaceUploadUseCase.execute(any()) }
        coVerify(exactly = 0) { removeTaskUseCase.execute(any()) }
        imagePreviewObserver.assertHasValue()
        onShowLoading.assertValueHistory(true, false)
        onUploadComplete.assertNoValue()
        onUploadError.assertHasValue()
    }

    @Test
    fun `test onSaveClick complete upload fail`() = runTest {
        // arrange
        val file = TrueCloudFilesModel.File(
            id = "id",
            parentObjectId = "parentObjectId",
        )
        val imagePreviewObserver = TestObserver.test(viewModel.imagePreview)
        mockDownloadPreviewSuccess()
        viewModel.setObjectFile(file)

        val onShowLoading = TestObserver.test(viewModel.onShowLoading)
        val onUploadComplete = TestObserver.test(viewModel.onUploadComplete)
        val onUploadError = TestObserver.test(viewModel.onUploadError)
        val transferObserver: TrueCloudV3TransferObserver = mockk()
        every { replaceFileUploadUseCase.execute(any(), any()) } returns flowOf(transferObserver)
        val slotListener = slot<TrueCloudV3TransferObserver.TrueCloudV3TransferListener>()
        every {
            transferObserver.setTransferListener(capture(slotListener))
        } answers {
            slotListener.captured.onStateChanged(0, TrueCloudV3TransferState.COMPLETED)
        }
        every { completeReplaceUploadUseCase.execute(any()) } returns flow { throw Exception() }

        // act
        viewModel.onSaveClick()

        // assert
        coVerify(exactly = 1) { completeReplaceUploadUseCase.execute(any()) }
        coVerify(exactly = 1) { updateTaskUploadStatusUseCase.execute(any(), any()) }
        coVerify(exactly = 0) { removeTaskUseCase.execute(any()) }
        imagePreviewObserver.assertHasValue()
        onShowLoading.assertValueHistory(true, false)
        onUploadComplete.assertNoValue()
        onUploadError.assertHasValue()
    }

    @Test
    fun `test undo`() = runTest {
        // arrange
        val file = TrueCloudFilesModel.File(
            id = "id",
            parentObjectId = "parentObjectId",
        )
        val testObserver = TestObserver.test(viewModel.imagePreview)
        mockDownloadPreviewSuccess()
        viewModel.setObjectFile(file)
        val mockUri1 = mockk<Uri>()
        val mockUri2 = mockk<Uri>()
        viewModel.onNewImage(mockUri1)
        viewModel.onNewImage(mockUri2)

        // act
        viewModel.onUndoClick()

        // assert
        coVerify(exactly = 1) { downloadUseCase.execute(any(), any(), DownloadType.SHARE) }
        testObserver.assertValue(mockUri1)
    }

    @Test
    fun `test undo new image`() = runTest {
        // arrange
        val file = TrueCloudFilesModel.File(
            id = "id",
            parentObjectId = "parentObjectId",
        )
        val testObserver = TestObserver.test(viewModel.imagePreview)
        mockDownloadPreviewSuccess()
        viewModel.setObjectFile(file)
        val mockUri1 = mockk<Uri>()
        val mockUri2 = mockk<Uri>()
        val mockUri3 = mockk<Uri>()
        viewModel.onNewImage(mockUri1)
        viewModel.onNewImage(mockUri2)
        viewModel.onUndoClick()

        // act
        viewModel.onNewImage(mockUri3)

        // assert
        coVerify(exactly = 1) { downloadUseCase.execute(any(), any(), DownloadType.SHARE) }
        testObserver.assertValue(mockUri3)
    }

    @Test
    fun `test redo`() = runTest {
        // arrange
        val file = TrueCloudFilesModel.File(
            id = "id",
            parentObjectId = "parentObjectId",
        )
        val testObserver = TestObserver.test(viewModel.imagePreview)
        mockDownloadPreviewSuccess()
        viewModel.setObjectFile(file)
        val mockUri1 = mockk<Uri>()
        val mockUri2 = mockk<Uri>()
        viewModel.onNewImage(mockUri1)
        viewModel.onNewImage(mockUri2)
        viewModel.onUndoClick()

        // act
        viewModel.onRedoClick()

        // assert
        coVerify(exactly = 1) { downloadUseCase.execute(any(), any(), DownloadType.SHARE) }
        testObserver.assertValue(mockUri2)
    }

    private fun mockDownloadPreviewSuccess() {
        val transferObserver: TrueCloudV3TransferObserver = mockk()
        every {
            contextDataProviderWrapper.get().getDataContext().cacheDir.absolutePath
        } returns "/cacheDir/absolutePath"
        every {
            downloadUseCase.execute(any(), any(), DownloadType.SHARE)
        } returns flowOf(transferObserver)

        val slotListener = slot<TrueCloudV3TransferObserver.TrueCloudV3TransferListener>()

        every {
            transferObserver.setTransferListener(capture(slotListener))
        } answers {
            slotListener.captured.onStateChanged(0, TrueCloudV3TransferState.COMPLETED)
        }

        val mockFileData = mockk<File>()
        every {
            fileProvider.getFile(any(), any())
        } returns mockFileData
        every { mockFileData.exists() } returns true
        every { mockFileData.absolutePath } returns "/test/path"
        every { mockFileData.path } returns "path"
    }

    private fun mockDownloadPreviewError() {
        val transferObserver: TrueCloudV3TransferObserver = mockk()
        every {
            contextDataProviderWrapper.get().getDataContext().cacheDir.absolutePath
        } returns "/cacheDir/absolutePath"
        every {
            downloadUseCase.execute(any(), any(), DownloadType.SHARE)
        } returns flowOf(transferObserver)

        val slotListener = slot<TrueCloudV3TransferObserver.TrueCloudV3TransferListener>()

        every {
            transferObserver.setTransferListener(capture(slotListener))
        } answers {
            slotListener.captured.onError(0, Exception())
        }

        val mockFileData = mockk<File>()
        every {
            fileProvider.getFile(any(), any())
        } returns mockFileData
        every { mockFileData.exists() } returns true
        every { mockFileData.absolutePath } returns "/test/path"
        every { mockFileData.path } returns "path"
    }
}
