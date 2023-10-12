package com.truedigital.features.truecloudv3.presentation.viewmodel

import android.os.Environment
import com.jraska.livedata.TestObserver
import com.truedigital.common.share.analytics.measurement.AnalyticManagerInterface
import com.truedigital.common.share.datalegacy.wrapper.ContextDataProviderWrapper
import com.truedigital.core.coroutines.TestCoroutineDispatcherProvider
import com.truedigital.core.provider.ContextDataProvider
import com.truedigital.features.truecloudv3.common.TrueCloudV3TransferState
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3TransferObserver
import com.truedigital.features.truecloudv3.domain.usecase.DownloadType
import com.truedigital.features.truecloudv3.domain.usecase.DownloadUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetShareLinkUseCase
import com.truedigital.features.truecloudv3.navigation.ShareBottomSheetToShareControlAccess
import com.truedigital.features.truecloudv3.navigation.router.ControlAccessRouterUseCase
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
class ShareBottomSheetDialogViewModelTest {

    @RegisterExtension
    @JvmField
    val testCoroutine = TestCoroutinesExtension()
    private val coroutineDispatcher = TestCoroutineDispatcherProvider(testCoroutine.dispatcher)
    private val downloadUseCase: DownloadUseCase = mockk()
    private val getShareLinkUseCase: GetShareLinkUseCase = mockk()
    private val fileProvider: FileProvider = mockk()
    private val router: ControlAccessRouterUseCase = mockk()
    private lateinit var viewModel: ShareBottomSheetDialogViewModel
    private val contextDataProviderWrapper: ContextDataProviderWrapper = mockk()
    private val analyticManagerInterface: AnalyticManagerInterface = mockk(relaxed = true)
    @BeforeEach
    fun setUp() {
        viewModel = ShareBottomSheetDialogViewModel(
            coroutineDispatcher = coroutineDispatcher,
            downloadUseCase = downloadUseCase,
            getShareLinkUseCase = getShareLinkUseCase,
            fileProvider = fileProvider,
            router = router,
            contextDataProviderWrapper = contextDataProviderWrapper,
            analyticManagerInterface = analyticManagerInterface
        )
        val fileModel = TrueCloudFilesModel.File(
            id = "test id",
            name = "test name"
        )
        // act
        viewModel.setFileModel(fileModel)
    }

    @Test
    fun setupViewTest() {
        // arrange
        val testObserverStartShare = TestObserver.test(viewModel.onSetTitleStatus)
        val fileModel = TrueCloudFilesModel.File(
            id = "test id",
            name = "test name",
            isPrivate = true
        )
        val contextDataProvider = mockk<ContextDataProvider>()
        every { contextDataProviderWrapper.get() } returns contextDataProvider
        every { contextDataProvider.getString(any()) } returns "test string"
        // act
        viewModel.setFileModel(fileModel)
        // assert
        testObserverStartShare.assertHasValue()
    }
    @Test
    fun setupViewIsPrivateFalseTest() {
        // arrange
        val testObserverStartShare = TestObserver.test(viewModel.onSetTitleStatus)
        val fileModel = TrueCloudFilesModel.File(
            id = "test id",
            name = "test name",
            isPrivate = false
        )
        val contextDataProvider = mockk<ContextDataProvider>()
        every { contextDataProviderWrapper.get() } returns contextDataProvider
        every { contextDataProvider.getString(any()) } returns "test string"
        // act
        viewModel.setFileModel(fileModel)
        // assert
        testObserverStartShare.assertHasValue()
    }

    @Test
    fun onClickShareTestFileExists() {
        // arrange
        val testObserverStartShare = TestObserver.test(viewModel.onStartShare)

        val mockFile = mockk<File>(relaxed = true)
        mockkStatic(Environment::class)
        every { Environment.getExternalStoragePublicDirectory(any()).path } returns "/storage/emulated/0"
        every { mockFile.exists() } returns true
        every { mockFile.isFile } returns true
        every { fileProvider.getFile(any()) } returns mockFile

        val fileModel = TrueCloudFilesModel.File(
            id = "test id",
            name = "test name"
        )

        // act
        viewModel.onClickShare()

        // assert
        testObserverStartShare.assertHasValue()
    }

    @Test
    fun onClickControlShareTest() {
        every { router.execute(any(), any()) } just runs
        // act
        viewModel.onClickControlShare()

        // assert
        verify(exactly = 1) { router.execute(ShareBottomSheetToShareControlAccess, any()) }
    }

    @Test
    fun onClickShareTestFileNotExists() {
        // arrange
        val testObserverProgressView = TestObserver.test(viewModel.onShowProgressLoading)
        val testObserverStartShare = TestObserver.test(viewModel.onStartShare)

        val mockFile = mockk<File>(relaxed = true)
        mockkStatic(Environment::class)
        every { Environment.getExternalStoragePublicDirectory(any()).path } returns "/storage/emulated/0"
        every { mockFile.exists() } returns false
        every { mockFile.isFile } returns true
        every { fileProvider.getFile(any()) } returns mockFile
        val transferObserver: TrueCloudV3TransferObserver = mockk()
        val slotListener = slot<TrueCloudV3TransferObserver.TrueCloudV3TransferListener>()
        every { downloadUseCase.execute(any(), any(), DownloadType.SHARE) } returns flowOf(transferObserver)
        every {
            transferObserver.setTransferListener(capture(slotListener))
        } answers {
            slotListener.captured.onStateChanged(0, TrueCloudV3TransferState.COMPLETED)
        }
        // act
        viewModel.onClickShare()

        // assert
        testObserverProgressView.assertHasValue()
        testObserverStartShare.assertHasValue()
    }

    @Test
    fun onClickCopyLinkShareTest() {
        // arrange
        val testObserverCopyShare = TestObserver.test(viewModel.onCopyLink)

        every {
            getShareLinkUseCase.execute(any())
        } returns flowOf("url_1")
        // act
        viewModel.onClickShareLink()

        // assert
        testObserverCopyShare.assertHasValue()
    }
}
