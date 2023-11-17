package com.truedigital.features.truecloudv3.presentation.viewmodel

import android.os.Environment
import com.jraska.livedata.TestObserver
import com.truedigital.common.share.analytics.measurement.AnalyticManagerInterface
import com.truedigital.core.coroutines.TestCoroutineDispatcherProvider
import com.truedigital.features.truecloudv3.common.FileMimeType
import com.truedigital.features.truecloudv3.common.TrueCloudV3TransferState
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3TransferObserver
import com.truedigital.features.truecloudv3.domain.usecase.DownloadType
import com.truedigital.features.truecloudv3.domain.usecase.DownloadUseCase
import com.truedigital.features.truecloudv3.navigation.OptionFileToFileInfoBottomSheet
import com.truedigital.features.truecloudv3.navigation.OptionFileToPhotoEditor
import com.truedigital.features.truecloudv3.navigation.OptionFileToRenameDialog
import com.truedigital.features.truecloudv3.navigation.OptionFileToShareBottomSheet
import com.truedigital.features.truecloudv3.navigation.router.FileTrueCloudRouterUseCase
import com.truedigital.features.truecloudv3.provider.FileProvider
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.coroutines.TestCoroutinesExtension
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import java.io.File

@ExtendWith(InstantTaskExecutorExtension::class)
class OptionFileBottomSheetDialogViewModelTest {
    @RegisterExtension
    @JvmField
    val testCoroutine = TestCoroutinesExtension()
    private val coroutineDispatcher = TestCoroutineDispatcherProvider(testCoroutine.dispatcher)
    private val router: FileTrueCloudRouterUseCase = mockk()
    private val downloadUseCase: DownloadUseCase = mockk()
    private val fileProvider: FileProvider = mockk()
    private lateinit var viewModel: OptionFileBottomSheetDialogViewModel
    private val analyticManagerInterface: AnalyticManagerInterface = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        viewModel = OptionFileBottomSheetDialogViewModel(
            router = router,
            coroutineDispatcher = coroutineDispatcher,
            downloadUseCase = downloadUseCase,
            fileProvider = fileProvider,
            analyticManagerInterface = analyticManagerInterface
        )
        val fileModel = TrueCloudFilesModel.File(
            id = "test id"
        )
        // act
        viewModel.setFileModel(fileModel)
    }

    @Test
    fun `test setFileModel Image`() {
        // arrange
        val testObserverViewFile = TestObserver.test(viewModel.onSetShowViewFile)
        val testObserverPhotoEditor = TestObserver.test(viewModel.onSetShowPhotoEditor)
        val fileModel = TrueCloudFilesModel.File(
            id = "test id",
            fileMimeType = FileMimeType.IMAGE
        )
        // act
        viewModel.setFileModel(fileModel)
        // assert
        testObserverViewFile.assertValue(false)
        testObserverPhotoEditor.assertValue(true)
    }

    @Test
    fun `test setFileModel Video`() {
        // arrange
        val testObserverViewFile = TestObserver.test(viewModel.onSetShowViewFile)
        val testObserverPhotoEditor = TestObserver.test(viewModel.onSetShowPhotoEditor)
        val fileModel = TrueCloudFilesModel.File(
            id = "test id",
            fileMimeType = FileMimeType.VIDEO
        )
        // act
        viewModel.setFileModel(fileModel)
        // assert
        testObserverViewFile.assertValue(false)
        testObserverPhotoEditor.assertValue(false)
    }

    @Test
    fun `test setFileModel Audio`() {
        // arrange
        val testObserverViewFile = TestObserver.test(viewModel.onSetShowViewFile)
        val testObserverPhotoEditor = TestObserver.test(viewModel.onSetShowPhotoEditor)
        val fileModel = TrueCloudFilesModel.File(
            id = "test id",
            fileMimeType = FileMimeType.AUDIO
        )
        // act
        viewModel.setFileModel(fileModel)
        // assert
        testObserverViewFile.assertValue(false)
        testObserverPhotoEditor.assertValue(false)
    }

    @Test
    fun `test setFileModel Contact`() {
        // arrange
        val testObserverViewFile = TestObserver.test(viewModel.onSetShowViewFile)
        val testObserverPhotoEditor = TestObserver.test(viewModel.onSetShowPhotoEditor)
        val fileModel = TrueCloudFilesModel.File(
            id = "test id",
            fileMimeType = FileMimeType.CONTACT
        )
        // act
        viewModel.setFileModel(fileModel)
        // assert
        testObserverViewFile.assertValue(false)
        testObserverPhotoEditor.assertValue(false)
    }

    @Test
    fun `test setFileModel Other`() {
        // arrange
        val testObserverViewFile = TestObserver.test(viewModel.onSetShowViewFile)
        val testObserverPhotoEditor = TestObserver.test(viewModel.onSetShowPhotoEditor)
        val fileModel = TrueCloudFilesModel.File(
            id = "test id",
            fileMimeType = FileMimeType.OTHER
        )
        // act
        viewModel.setFileModel(fileModel)
        // assert
        testObserverViewFile.assertValue(true)
        testObserverPhotoEditor.assertValue(false)
    }

    @Test
    fun `test setFileModel Unsupported`() {
        // arrange
        val testObserverViewFile = TestObserver.test(viewModel.onSetShowViewFile)
        val testObserverPhotoEditor = TestObserver.test(viewModel.onSetShowPhotoEditor)
        val fileModel = TrueCloudFilesModel.File(
            id = "test id",
            fileMimeType = FileMimeType.UNSUPPORTED_FORMAT
        )
        // act
        viewModel.setFileModel(fileModel)
        // assert
        testObserverViewFile.assertValue(true)
        testObserverPhotoEditor.assertValue(false)
    }

    @Test
    fun `test onReceiveFileName success`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onRename)

        val fileModel = TrueCloudFilesModel.File(
            id = "test id",
            name = "test name"
        )
        // act
        viewModel.onReceiveFileName(fileModel)

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test File onClickRename success`() {
        // arrange
        every { router.execute(any(), any()) } just runs

        // act
        viewModel.onClickRename()

        // assert
        verify(exactly = 1) { router.execute(OptionFileToRenameDialog, any()) }
    }

    @Test
    fun `test File onClickSeeInfo success`() {
        // arrange
        every { router.execute(any(), any()) } just runs

        // act
        viewModel.onClickSeeInfo()

        // assert
        verify(exactly = 1) { router.execute(OptionFileToFileInfoBottomSheet, any()) }
    }

    @Test
    fun `test onClickDelete success`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onDelete)
        // act
        viewModel.onClickDelete()

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test onClickDownload success`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onDownload)
        // act
        viewModel.onClickDownload()

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test onClickShare success`() {
        // arrange
        every { router.execute(any(), any()) } just runs

        // act
        viewModel.onClickShare()

        // assert
        verify(exactly = 1) { router.execute(OptionFileToShareBottomSheet, any()) }
    }

    @Test
    fun `test onClickViewFile fileExists`() {
        // arrange
        val testObserveShowProgressLoading = TestObserver.test(viewModel.onShowProgressLoading)
        val testObserveStartShare = TestObserver.test(viewModel.onStartViewFile)

        val mockFile = mockk<File>(relaxed = true)
        mockkStatic(Environment::class)
        every { Environment.getExternalStoragePublicDirectory(any()).path } returns "/storage/emulated/0"
        every { mockFile.exists() } returns true
        every { mockFile.isFile } returns true
        every { fileProvider.getFile(any()) } returns mockFile

        // act
        viewModel.onClickViewFile()

        // assert
        testObserveShowProgressLoading.assertValueHistory(true, false)
        testObserveStartShare.assertHasValue()
    }

    @Test
    fun `test onClickViewFile fileNotExists`() {
        // arrange
        val testObserveShowProgressLoading = TestObserver.test(viewModel.onShowProgressLoading)
        val testObserveStartShare = TestObserver.test(viewModel.onStartViewFile)

        val mockFile = mockk<File>(relaxed = true)
        mockkStatic(Environment::class)
        every { Environment.getExternalStoragePublicDirectory(any()).path } returns "/storage/emulated/0"
        every { mockFile.exists() } returns false
        every { mockFile.isFile } returns true
        every { fileProvider.getFile(any()) } returns mockFile
        val transferObserver: TrueCloudV3TransferObserver = mockk()
        val slotListener = slot<TrueCloudV3TransferObserver.TrueCloudV3TransferListener>()
        every { downloadUseCase.execute(any(), any(), DownloadType.SHARE) } returns flowOf(
            transferObserver
        )
        every {
            transferObserver.setTransferListener(capture(slotListener))
        } answers {
            slotListener.captured.onStateChanged(0, TrueCloudV3TransferState.COMPLETED)
        }

        // act
        viewModel.onClickViewFile()

        // assert
        testObserveShowProgressLoading.assertValueHistory(true, false)
        testObserveStartShare.assertHasValue()
    }

    @Test
    fun `test onClickPhotoEditor success`() {
        // arrange
        every { router.execute(any(), any()) } just runs

        // act
        viewModel.onClickPhotoEditor()

        // assert
        verify(exactly = 1) { router.execute(OptionFileToPhotoEditor, any()) }
    }
}
