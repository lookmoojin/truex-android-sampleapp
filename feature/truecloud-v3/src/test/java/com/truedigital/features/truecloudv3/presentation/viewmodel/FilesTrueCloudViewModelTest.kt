package com.truedigital.features.truecloudv3.presentation.viewmodel

import android.app.NotificationManager
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import androidx.core.app.NotificationManagerCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.work.WorkManager
import com.jraska.livedata.TestObserver
import com.truedigital.common.share.analytics.measurement.AnalyticManagerInterface
import com.truedigital.common.share.datalegacy.wrapper.ContextDataProviderWrapper
import com.truedigital.core.coroutines.TestCoroutineDispatcherProvider
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.common.FileCategoryType
import com.truedigital.features.truecloudv3.common.FileMimeType
import com.truedigital.features.truecloudv3.common.SortType
import com.truedigital.features.truecloudv3.common.TaskActionType
import com.truedigital.features.truecloudv3.common.TaskStatusType
import com.truedigital.features.truecloudv3.common.TrueCloudV3MediaType
import com.truedigital.features.truecloudv3.common.TrueCloudV3TransferState
import com.truedigital.features.truecloudv3.domain.model.HeaderType
import com.truedigital.features.truecloudv3.domain.model.TaskUploadModel
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3Model
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3TransferObserver
import com.truedigital.features.truecloudv3.domain.usecase.CompleteUploadUseCase
import com.truedigital.features.truecloudv3.domain.usecase.CreateFolderUserCase
import com.truedigital.features.truecloudv3.domain.usecase.DeleteFileUseCase
import com.truedigital.features.truecloudv3.domain.usecase.DeleteTrashDataUseCase
import com.truedigital.features.truecloudv3.domain.usecase.DownloadCoverImageUseCase
import com.truedigital.features.truecloudv3.domain.usecase.DownloadUseCase
import com.truedigital.features.truecloudv3.domain.usecase.EmptyTrashDataUseCase
import com.truedigital.features.truecloudv3.domain.usecase.FileLocatorUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetNewUploadTaskListUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetStorageListUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetStorageListWithCategoryUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetTrashListUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetUploadTaskListUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetUploadTaskUseCase
import com.truedigital.features.truecloudv3.domain.usecase.MoveToTrashUseCase
import com.truedigital.features.truecloudv3.domain.usecase.ProvideWorkManagerUseCase
import com.truedigital.features.truecloudv3.domain.usecase.RemoveAllTaskUseCase
import com.truedigital.features.truecloudv3.domain.usecase.RemoveTaskUseCase
import com.truedigital.features.truecloudv3.domain.usecase.RenameFileUseCase
import com.truedigital.features.truecloudv3.domain.usecase.RestoreTrashDataUseCase
import com.truedigital.features.truecloudv3.domain.usecase.RetryUploadUseCase
import com.truedigital.features.truecloudv3.domain.usecase.SearchInAllFilesUseCase
import com.truedigital.features.truecloudv3.domain.usecase.SearchWithCategoryUseCase
import com.truedigital.features.truecloudv3.domain.usecase.UpdateTaskUploadStatusUseCase
import com.truedigital.features.truecloudv3.domain.usecase.UploadFileUseCase
import com.truedigital.features.truecloudv3.domain.usecase.UploadFileWithPathUseCase
import com.truedigital.features.truecloudv3.domain.usecase.UploadQueueUseCase
import com.truedigital.features.truecloudv3.extension.convertToFilesModel
import com.truedigital.features.truecloudv3.navigation.AllFileToImageViewer
import com.truedigital.features.truecloudv3.navigation.AllFileToMainOptionBottomSheet
import com.truedigital.features.truecloudv3.navigation.AllFileToOptionFileBottomSheet
import com.truedigital.features.truecloudv3.navigation.AllFileToPermission
import com.truedigital.features.truecloudv3.navigation.SelectFileToOptionFileSelectedBottomSheet
import com.truedigital.features.truecloudv3.navigation.TrashToMainOptionBottomSheet
import com.truedigital.features.truecloudv3.navigation.TrashToTrashBottomSheet
import com.truedigital.features.truecloudv3.navigation.router.FileTrueCloudRouterUseCase
import com.truedigital.features.truecloudv3.provider.TrueCloudV3TransferUtilityProvider
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.coroutines.TestCoroutinesExtension
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull

@ExtendWith(InstantTaskExecutorExtension::class)
class FilesTrueCloudViewModelTest {

    @RegisterExtension
    @JvmField
    val testCoroutine = TestCoroutinesExtension()
    private val coroutineDispatcher = TestCoroutineDispatcherProvider(testCoroutine.dispatcher)
    private val router: FileTrueCloudRouterUseCase = mockk(relaxed = true)
    private val getStorageListUseCase: GetStorageListUseCase =
        mockk(relaxed = true)
    private val getUploadTaskUseCase: GetUploadTaskUseCase =
        mockk(relaxed = true)
    private val uploadFileUseCase: UploadFileUseCase = mockk(relaxed = true)
    private val uploadFileWithPathUseCase: UploadFileWithPathUseCase = mockk(relaxed = true)
    private val removeTaskUseCase: RemoveTaskUseCase =
        mockk(relaxed = true)
    private val completeUploadUseCase: CompleteUploadUseCase =
        mockk(relaxed = true)
    private val updateTaskUploadStatusUseCase: UpdateTaskUploadStatusUseCase =
        mockk(relaxed = true)
    private val createFolderUserCase: CreateFolderUserCase =
        mockk(relaxed = true)
    private val downloadUseCase: DownloadUseCase = mockk(relaxed = true)
    private val retryUploadUseCase: RetryUploadUseCase = mockk(relaxed = true)
    private val renameFileUseCase: RenameFileUseCase = mockk(relaxed = true)
    private val deleteFileUseCase: DeleteFileUseCase = mockk(relaxed = true)
    private val contextDataProviderWrapper: ContextDataProviderWrapper = mockk(relaxed = true)
    private val getUploadTaskListUseCase: GetUploadTaskListUseCase = mockk(relaxed = true)
    private val getNewUploadTaskListUseCase: GetNewUploadTaskListUseCase = mockk(relaxed = true)
    private val removeAllTaskUseCase: RemoveAllTaskUseCase = mockk(relaxed = true)
    private val analyticManagerInterface: AnalyticManagerInterface = mockk(relaxed = true)
    private val getStorageListWithCategoryUseCase: GetStorageListWithCategoryUseCase =
        mockk(relaxed = true)
    private val provideWorkManagerUseCase: ProvideWorkManagerUseCase = mockk(relaxed = true)
    private val downloadCoverImageUseCase: DownloadCoverImageUseCase = mockk(relaxed = true)
    private val fileLocatorUseCase: FileLocatorUseCase = mockk(relaxed = true)
    private val moveToTrashUseCase: MoveToTrashUseCase = mockk(relaxed = true)
    private val getTrashListUseCase: GetTrashListUseCase = mockk(relaxed = true)
    private val restoreTrashDataUseCase: RestoreTrashDataUseCase = mockk(relaxed = true)
    private val deleteTrashDataUseCase: DeleteTrashDataUseCase = mockk(relaxed = true)
    private val emptyTrashDataUseCase: EmptyTrashDataUseCase = mockk(relaxed = true)
    private val context: Context = mockk(relaxed = true)
    private val workManager: WorkManager = mockk(relaxed = true)
    private lateinit var viewModel: FilesTrueCloudViewModel
    private val trueCloudV3TransferUtilityProvider: TrueCloudV3TransferUtilityProvider = mockk()
    private val stackIds = mockkObject(FilesTrueCloudViewModel.Companion)
    private val searchInAllFilesUseCase: SearchInAllFilesUseCase = mockk(relaxed = true)
    private val searchWithCategoryUseCase: SearchWithCategoryUseCase = mockk(relaxed = true)
    private val uploadQueueUseCase: UploadQueueUseCase = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        viewModel = FilesTrueCloudViewModel(
            router = router,
            coroutineDispatcher = coroutineDispatcher,
            getStorageListUseCase = getStorageListUseCase,
            getStorageListWithCategoryUseCase = getStorageListWithCategoryUseCase,
            getUploadTaskListUseCase = getUploadTaskListUseCase,
            getUploadTaskUseCase = getUploadTaskUseCase,
            uploadFileUseCase = uploadFileUseCase,
            uploadFileWithPathUseCase = uploadFileWithPathUseCase,
            removeTaskUseCase = removeTaskUseCase,
            removeAllTaskUseCase = removeAllTaskUseCase,
            completeUploadUseCase = completeUploadUseCase,
            updateTaskUploadStatusUseCase = updateTaskUploadStatusUseCase,
            createFolderUserCase = createFolderUserCase,
            downloadCoverImageUseCase = downloadCoverImageUseCase,
            retryUploadUseCase = retryUploadUseCase,
            renameFileUseCase = renameFileUseCase,
            deleteFileUseCase = deleteFileUseCase,
            trueCloudV3TransferUtilityProvider = trueCloudV3TransferUtilityProvider,
            contextDataProviderWrapper = contextDataProviderWrapper,
            analyticManagerInterface = analyticManagerInterface,
            workManager = workManager,
            fileLocatorUseCase = fileLocatorUseCase,
            moveToTrashUseCase = moveToTrashUseCase,
            getTrashListUseCase = getTrashListUseCase,
            restoreTrashDataUseCase = restoreTrashDataUseCase,
            deleteTrashDataUseCase = deleteTrashDataUseCase,
            emptyTrashDataUseCase = emptyTrashDataUseCase,
            searchInAllFilesUseCase = searchInAllFilesUseCase,
            searchWithCategoryUseCase = searchWithCategoryUseCase,
            uploadQueueUseCase = uploadQueueUseCase
        )
    }

    @Test
    fun `test performClickIntroPermission success`() {
        // arrange
        every { router.execute(any()) } just runs

        // act
        viewModel.performClickIntroPermission()

        // assert
        verify(exactly = 1) { router.execute(AllFileToPermission, any()) }
    }

    @Test
    fun `test onClickItemMoreOption success`() {
        // arrange
        every { router.execute(any()) } just runs
        val file = TrueCloudFilesModel.File(
            id = "id",
            parentObjectId = "parentObjectId"
        )
        // act
        viewModel.onClickItemMoreOption(file)

        // assert
        verify(exactly = 1) { router.execute(AllFileToOptionFileBottomSheet, any()) }
    }

    @Test
    fun `test onLongClickItemMoreOption success`() {
        // arrange
        every { router.execute(any()) } just runs
        val file = TrueCloudFilesModel.File(
            id = "id",
            parentObjectId = "parentObjectId"
        )
        // act
        viewModel.onLongClickItemMoreOption(file)

        // assert
        verify(exactly = 1) { router.execute(AllFileToOptionFileBottomSheet, any()) }
    }

    @Test
    fun `test onViewCreated success`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSetHeaderTitle)
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 0L
        )
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))
        coEvery {
            getNewUploadTaskListUseCase.execute()
        } answers {
            flowOf(mutableListOf(taskUploadModel))
        }
        every { analyticManagerInterface.trackScreen(any()) } just runs
        // act
        viewModel.onViewCreated("cateName", "folderId")

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test onViewCreated success category Image`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSetHeaderTitle)
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 0L
        )
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))
        coEvery {
            getNewUploadTaskListUseCase.execute()
        } answers {
            flowOf(mutableListOf(taskUploadModel))
        }
        every { analyticManagerInterface.trackScreen(any()) } just runs
        // act
        viewModel.onViewCreated("IMAGE", "folderId")

        // assert
        coVerify(exactly = 1) {
            analyticManagerInterface.trackScreen(any())
        }
        testObserver.assertHasValue()
    }

    @Test
    fun `test onViewCreated success category Video`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSetHeaderTitle)
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 0L
        )
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))
        coEvery {
            getNewUploadTaskListUseCase.execute()
        } answers {
            flowOf(mutableListOf(taskUploadModel))
        }
        every { analyticManagerInterface.trackScreen(any()) } just runs
        // act
        viewModel.onViewCreated("VIDEO", "folderId")

        // assert
        coVerify(exactly = 1) {
            analyticManagerInterface.trackScreen(any())
        }
        testObserver.assertHasValue()
    }

    @Test
    fun `test onViewCreated success category Audio`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSetHeaderTitle)
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 0L
        )
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))
        coEvery {
            getNewUploadTaskListUseCase.execute()
        } answers {
            flowOf(mutableListOf(taskUploadModel))
        }
        every { analyticManagerInterface.trackScreen(any()) } just runs
        // act
        viewModel.onViewCreated("AUDIO", "folderId")

        // assert
        coVerify(exactly = 1) {
            analyticManagerInterface.trackScreen(any())
        }
        testObserver.assertHasValue()
    }

    @Test
    fun `test onViewCreated success category Other`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSetHeaderTitle)
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 0L
        )
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))
        coEvery {
            getNewUploadTaskListUseCase.execute()
        } answers {
            flowOf(mutableListOf(taskUploadModel))
        }
        every { analyticManagerInterface.trackScreen(any()) } just runs
        // act
        viewModel.onViewCreated("OTHER", "folderId")

        // assert
        coVerify(exactly = 1) {
            analyticManagerInterface.trackScreen(any())
        }
        testObserver.assertHasValue()
    }

    @Test
    fun `test onViewCreated success category Recent`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSetHeaderTitle)
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 0L
        )
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))
        coEvery {
            getNewUploadTaskListUseCase.execute()
        } answers {
            flowOf(mutableListOf(taskUploadModel))
        }
        every { analyticManagerInterface.trackScreen(any()) } just runs
        // act
        viewModel.onViewCreated("IMAGE,VIDEO,AUDIO,OTHER", "folderId")

        // assert
        coVerify(exactly = 1) {
            analyticManagerInterface.trackScreen(any())
        }
        testObserver.assertHasValue()
    }

    @Test
    fun `test performClickToImageViewer success`() {
        // arrange
        every { router.execute(any()) } just runs
        val file = TrueCloudFilesModel.File(
            id = "id",
            parentObjectId = "parentObjectId"
        )
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))
        // act
        viewModel.performClickToImageViewer(file)

        // assert
        verify(exactly = 1) { router.execute(AllFileToImageViewer, any()) }
    }

    @Test
    fun `test onClickHeaderMoreOption success`() {
        // arrange
        every { router.execute(any()) } just runs
        // act
        viewModel.onClickHeaderMoreOption()

        // assert
        verify(exactly = 1) { router.execute(AllFileToMainOptionBottomSheet, any()) }
    }

    @Test
    fun `test onClickChangeLayout success`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onAddDecoration)
        // act
        viewModel.onClickChangeLayout()

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test onClickChangeLayout is grid success `() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onRemoveDecoration)
        // act
        viewModel.onClickChangeLayout()
        viewModel.onClickChangeLayout()

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test addSelectItem success`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSelectUpdate)
        val file = TrueCloudFilesModel.File(
            id = "id",
            parentObjectId = "parentObjectId"
        )

        // act
        viewModel.addSelectItem(file)

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test selectAllItem selectAllState true`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSelectAll)
        viewModel.selectAllState = true
        // act
        viewModel.selectAllItem()

        // assert
        testObserver.assertValue(false)
    }

    @Test
    fun `test selectAllItem success`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSelectAll)

        // act
        viewModel.selectAllItem()

        // assert
        testObserver.assertValue(true)
    }

    @Test
    fun `test onOptionClickSelect success`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onShowSelectMode)
        // act
        viewModel.onOptionClickSelect(true)

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test onOptionClickSelect status false`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onShowSelectMode)
        // act
        viewModel.onOptionClickSelect(false)

        // assert
        testObserver.assertNoValue()
    }

    @Test
    fun `test getSelectedItem success`() = runTest {
        // arrange
        val expect = viewModel.selectItemList

        // act
        val list = viewModel.getSelectedItem()

        // assert
        assertEquals(expect, list)
    }

    @Test
    fun `test onFolderItemClick success`() = runTest {
        // arrange
        val mockkFolder = TrueCloudFilesModel.Folder(
            id = "1234",
            name = "testName"
        )
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))
        // act
        viewModel.onFolderItemClick(mockkFolder)

        // assert
        assertEquals("1234", FilesTrueCloudViewModel.Companion.stackFolderIds.last().first)
    }

    @Test
    fun `test onFolderItemClick folder id null`() = runTest {
        // arrange
        val mockkFolder = TrueCloudFilesModel.Folder(
            name = "testName"
        )
        // act
        viewModel.onFolderItemClick(mockkFolder)

        // assert
        verify(exactly = 0) { getStorageListWithCategoryUseCase.execute(any(), any(), any()) }
    }

    @Test
    fun `test unselectAllItem success`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSelectAll)

        // act
        viewModel.unselectAllItem()

        // assert
        testObserver.assertValue(false)
    }

    @Test
    fun `test onExpandClicked success`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSetExpandState)
        // act
        viewModel.onExpandClicked(true)

        // assert
        testObserver.assertValue(false)
    }

    @Test
    fun `test onClickDelete success`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onShowConfirmDialogDelete)
        // act
        viewModel.onClickDelete()

        // assert
        testObserver.assertValue(Unit)
    }

    @Test
    fun testOnSelectedUploadFileResult() {
        // arrange
        val intent = mockk<Intent>()
        val clipData = mockk<ClipData>()
        val contentUri1 = mockk<Uri>(name = "content://uri-1")
        val contentUri2 = mockk<Uri>(name = "content://uri-2")
        val trueCloudV3TransferObserver = mockk<TrueCloudV3TransferObserver>()
        val slotListener = slot<TrueCloudV3TransferObserver.TrueCloudV3TransferListener>()
        every { intent.data } returns null
        every { intent.clipData } returns clipData
        every { intent.clipData?.itemCount } returns 2
        every { intent.clipData?.getItemAt(0)?.uri } returns contentUri1
        every { intent.clipData?.getItemAt(1)?.uri } returns contentUri2
        every {
            uploadFileUseCase.execute(any(), any())
        } answers {
            flowOf(trueCloudV3TransferObserver)
        }
        every {
            completeUploadUseCase.execute(any())
        } returns flow {}
        every {
            trueCloudV3TransferObserver.setTransferListener(capture(slotListener))
        } answers {
            slotListener.captured.onStateChanged(0, TrueCloudV3TransferState.COMPLETED)
        }
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))
        // act
        viewModel.onSelectedUploadFileResult(intent)

        // assert
        verify(exactly = 1) {
            intent.clipData?.getItemAt(0)?.uri
        }
    }

    @Test
    fun testOnSelectedUploadFileResultUploadExceedLimit() {
        // arrange
        val intent = mockk<Intent>()
        val clipData = mockk<ClipData>()
        val contentUri1 = mockk<Uri>(name = "content://uri-1")
        val contentUri2 = mockk<Uri>(name = "content://uri-2")
        val trueCloudV3TransferObserver = mockk<TrueCloudV3TransferObserver>()
        val slotListener = slot<TrueCloudV3TransferObserver.TrueCloudV3TransferListener>()
        every { intent.data } returns null
        every { intent.clipData } returns clipData
        every { intent.clipData?.itemCount } returns 2
        every { intent.clipData?.getItemAt(0)?.uri } returns contentUri1
        every { intent.clipData?.getItemAt(1)?.uri } returns contentUri2
        every {
            uploadFileUseCase.execute(any(), any())
        } returns flow { error("Exceed limit error") }
        every {
            completeUploadUseCase.execute(any())
        } returns flow {}
        every {
            trueCloudV3TransferObserver.setTransferListener(capture(slotListener))
        } answers {
            slotListener.captured.onStateChanged(0, TrueCloudV3TransferState.COMPLETED)
        }
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))
        // act
        viewModel.onSelectedUploadFileResult(intent)

        // assert
        verify(exactly = 1) {
            intent.clipData?.getItemAt(0)?.uri
        }
    }

    @Test
    fun testOnSelectedUploadFileResultOneFileComplete() {
        // arrange
        val intent = mockk<Intent>()
        val contentUri1 = mockk<Uri>(name = "content://uri-1")
        val trueCloudV3TransferObserver = mockk<TrueCloudV3TransferObserver>()
        val slotListener = slot<TrueCloudV3TransferObserver.TrueCloudV3TransferListener>()
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))
        every { intent.data } returns contentUri1
        every { intent.clipData } returns null
        every {
            uploadFileUseCase.execute(any(), any())
        } answers {
            flowOf(trueCloudV3TransferObserver)
        }
        every {
            completeUploadUseCase.execute(any())
        } returns flow {}
        every {
            trueCloudV3TransferObserver.setTransferListener(capture(slotListener))
        } answers {
            slotListener.captured.onStateChanged(0, TrueCloudV3TransferState.COMPLETED)
        }
        coEvery {
            trueCloudV3TransferObserver.getState()
        } returns TrueCloudV3TransferState.COMPLETED
        coEvery {
            trueCloudV3TransferObserver.getId()
        } returns 0

        // act
        viewModel.onSelectedUploadFileResult(intent)

        // assert
        verify(exactly = 1) {
            intent.data
        }
    }

    @Test
    fun testOnSelectedUploadFileResultOneFileCANCELED() {
        // arrange
        val intent = mockk<Intent>()
        val contentUri1 = mockk<Uri>(name = "content://uri-1")
        val trueCloudV3TransferObserver = mockk<TrueCloudV3TransferObserver>()
        val slotListener = slot<TrueCloudV3TransferObserver.TrueCloudV3TransferListener>()
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))
        every { intent.data } returns contentUri1
        every { intent.clipData } returns null
        every {
            uploadFileUseCase.execute(any(), any())
        } answers {
            flowOf(trueCloudV3TransferObserver)
        }
        every {
            completeUploadUseCase.execute(any())
        } returns flow {}
        every {
            trueCloudV3TransferObserver.setTransferListener(capture(slotListener))
        } answers {
            slotListener.captured.onStateChanged(0, TrueCloudV3TransferState.CANCELED)
        }
        coEvery {
            trueCloudV3TransferObserver.getState()
        } returns TrueCloudV3TransferState.COMPLETED
        coEvery {
            trueCloudV3TransferObserver.getId()
        } returns 0

        // act
        viewModel.onSelectedUploadFileResult(intent)

        // assert
        verify(exactly = 1) {
            intent.data
        }
    }

    @Test
    fun testOnSelectedUploadFileResultOneFileIN_PROGRESS() {
        // arrange
        val intent = mockk<Intent>()
        val contentUri1 = mockk<Uri>(name = "content://uri-1")
        val trueCloudV3TransferObserver = mockk<TrueCloudV3TransferObserver>()
        val slotListener = slot<TrueCloudV3TransferObserver.TrueCloudV3TransferListener>()
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))
        every { intent.data } returns contentUri1
        every { intent.clipData } returns null
        every {
            uploadFileUseCase.execute(any(), any())
        } answers {
            flowOf(trueCloudV3TransferObserver)
        }
        every {
            completeUploadUseCase.execute(any())
        } returns flow {}
        every {
            trueCloudV3TransferObserver.setTransferListener(capture(slotListener))
        } answers {
            slotListener.captured.onStateChanged(0, TrueCloudV3TransferState.IN_PROGRESS)
        }
        coEvery {
            trueCloudV3TransferObserver.getState()
        } returns TrueCloudV3TransferState.COMPLETED
        coEvery {
            trueCloudV3TransferObserver.getId()
        } returns 0

        // act
        viewModel.onSelectedUploadFileResult(intent)

        // assert
        verify(exactly = 1) {
            intent.data
        }
    }

    @Test
    fun testOnSelectedUploadFileResultOneFileRESUMED_WAITING() {
        // arrange
        val intent = mockk<Intent>()
        val contentUri1 = mockk<Uri>(name = "content://uri-1")
        val trueCloudV3TransferObserver = mockk<TrueCloudV3TransferObserver>()
        val slotListener = slot<TrueCloudV3TransferObserver.TrueCloudV3TransferListener>()
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))
        every { intent.data } returns contentUri1
        every { intent.clipData } returns null
        every {
            uploadFileUseCase.execute(any(), any())
        } answers {
            flowOf(trueCloudV3TransferObserver)
        }
        every {
            completeUploadUseCase.execute(any())
        } returns flow {}
        every {
            trueCloudV3TransferObserver.setTransferListener(capture(slotListener))
        } answers {
            slotListener.captured.onStateChanged(0, TrueCloudV3TransferState.RESUMED_WAITING)
        }
        coEvery {
            trueCloudV3TransferObserver.getState()
        } returns TrueCloudV3TransferState.COMPLETED
        coEvery {
            updateTaskUploadStatusUseCase.execute(any(), any())
        } returns Unit
        coEvery {
            trueCloudV3TransferObserver.getId()
        } returns 0

        // act
        viewModel.onSelectedUploadFileResult(intent)

        // assert
        coVerify(exactly = 1) {
            updateTaskUploadStatusUseCase.execute(any(), any())
        }
    }

    @Test
    fun testOnSelectedUploadFileResultOneFilePAUSED() {
        // arrange
        val intent = mockk<Intent>()
        val contentUri1 = mockk<Uri>(name = "content://uri-1")
        val trueCloudV3TransferObserver = mockk<TrueCloudV3TransferObserver>()
        val slotListener = slot<TrueCloudV3TransferObserver.TrueCloudV3TransferListener>()
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))
        every { intent.data } returns contentUri1
        every { intent.clipData } returns null
        every {
            uploadFileUseCase.execute(any(), any())
        } answers {
            flowOf(trueCloudV3TransferObserver)
        }
        every {
            completeUploadUseCase.execute(any())
        } returns flow {}
        every {
            trueCloudV3TransferObserver.setTransferListener(capture(slotListener))
        } answers {
            slotListener.captured.onStateChanged(0, TrueCloudV3TransferState.PAUSED)
        }
        coEvery {
            trueCloudV3TransferObserver.getState()
        } returns TrueCloudV3TransferState.COMPLETED
        coEvery {
            updateTaskUploadStatusUseCase.execute(any(), any())
        } returns Unit
        coEvery {
            trueCloudV3TransferObserver.getId()
        } returns 0

        // act
        viewModel.onSelectedUploadFileResult(intent)

        // assert
        coVerify(exactly = 1) {
            updateTaskUploadStatusUseCase.execute(any(), any())
        }
    }

    @Test
    fun testOnSelectedUploadFileResultOneFileFAILED() {
        // arrange
        val intent = mockk<Intent>()
        val contentUri1 = mockk<Uri>(name = "content://uri-1")
        val trueCloudV3TransferObserver = mockk<TrueCloudV3TransferObserver>()
        val slotListener = slot<TrueCloudV3TransferObserver.TrueCloudV3TransferListener>()
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))
        every { intent.data } returns contentUri1
        every { intent.clipData } returns null
        every {
            uploadFileUseCase.execute(any(), any())
        } answers {
            flowOf(trueCloudV3TransferObserver)
        }
        every {
            completeUploadUseCase.execute(any())
        } returns flow {}
        every {
            trueCloudV3TransferObserver.setTransferListener(capture(slotListener))
        } answers {
            slotListener.captured.onStateChanged(0, TrueCloudV3TransferState.FAILED)
        }
        coEvery {
            trueCloudV3TransferObserver.getState()
        } returns TrueCloudV3TransferState.COMPLETED
        coEvery {
            updateTaskUploadStatusUseCase.execute(any(), any())
        } returns Unit
        coEvery {
            trueCloudV3TransferObserver.getId()
        } returns 0

        // act
        viewModel.onSelectedUploadFileResult(intent)

        // assert
        coVerify(exactly = 1) {
            updateTaskUploadStatusUseCase.execute(any(), any())
        }
    }

    @Test
    fun testOnSelectedUploadFileResultOneFileUNKNOWN() {
        // arrange
        val intent = mockk<Intent>()
        val contentUri1 = mockk<Uri>(name = "content://uri-1")
        val trueCloudV3TransferObserver = mockk<TrueCloudV3TransferObserver>()
        val slotListener = slot<TrueCloudV3TransferObserver.TrueCloudV3TransferListener>()
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))
        every { intent.data } returns contentUri1
        every { intent.clipData } returns null
        every {
            uploadFileUseCase.execute(any(), any())
        } answers {
            flowOf(trueCloudV3TransferObserver)
        }
        every {
            completeUploadUseCase.execute(any())
        } returns flow {}
        every {
            trueCloudV3TransferObserver.setTransferListener(capture(slotListener))
        } answers {
            slotListener.captured.onStateChanged(0, TrueCloudV3TransferState.UNKNOWN)
        }
        coEvery {
            trueCloudV3TransferObserver.getState()
        } returns TrueCloudV3TransferState.COMPLETED
        coEvery {
            updateTaskUploadStatusUseCase.execute(any(), any())
        } returns Unit
        coEvery {
            trueCloudV3TransferObserver.getId()
        } returns 0

        // act
        viewModel.onSelectedUploadFileResult(intent)

        // assert
        coVerify(exactly = 1) {
            updateTaskUploadStatusUseCase.execute(any(), any())
        }
    }

    @Test
    fun testOnSelectedUploadFileResultOneFileShowProgressChange() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onShowProgressChange)
        val uploadModel = TrueCloudFilesModel.Upload(
            id = 0,
            progress = 30L,
            path = "path/x",
            status = TaskStatusType.IN_PROGRESS,
            name = "name",
            size = "size",
            type = FileMimeType.IMAGE,
            coverImageSize = 12
        )
        val intent = mockk<Intent>()
        val contentUri1 = mockk<Uri>(name = "content://uri-1")
        val trueCloudV3TransferObserver = mockk<TrueCloudV3TransferObserver>()
        val slotListener = slot<TrueCloudV3TransferObserver.TrueCloudV3TransferListener>()
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))
        every { intent.data } returns contentUri1
        every { intent.clipData } returns null
        every {
            uploadFileUseCase.execute(any(), any())
        } answers {
            flowOf(trueCloudV3TransferObserver)
        }
        every {
            completeUploadUseCase.execute(any())
        } returns flow {}
        every {
            trueCloudV3TransferObserver.setTransferListener(capture(slotListener))
        } answers {
            slotListener.captured.onProgressChanged(0, 50L, 100L)
        }
        viewModel.items.add(uploadModel)
        coEvery {
            trueCloudV3TransferObserver.getState()
        } returns TrueCloudV3TransferState.COMPLETED

        coEvery {
            trueCloudV3TransferObserver.getId()
        } returns 0

        // act
        viewModel.onSelectedUploadFileResult(intent)

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun testOnSelectedUploadFileResultOneFileShowProgressChangeWrong() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onShowProgressChange)
        val uploadModel = TrueCloudFilesModel.Upload(
            id = 0,
            progress = 30L,
            path = "path/x",
            status = TaskStatusType.IN_PROGRESS,
            name = "name",
            size = "size",
            type = FileMimeType.IMAGE,
            coverImageSize = 12
        )
        val intent = mockk<Intent>()
        val contentUri1 = mockk<Uri>(name = "content://uri-1")
        val trueCloudV3TransferObserver = mockk<TrueCloudV3TransferObserver>()
        val slotListener = slot<TrueCloudV3TransferObserver.TrueCloudV3TransferListener>()
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))
        every { intent.data } returns contentUri1
        every { intent.clipData } returns null
        every {
            uploadFileUseCase.execute(any(), any())
        } answers {
            flowOf(trueCloudV3TransferObserver)
        }
        every {
            completeUploadUseCase.execute(any())
        } returns flow {}
        every {
            trueCloudV3TransferObserver.setTransferListener(capture(slotListener))
        } answers {
            slotListener.captured.onProgressChanged(0, 20L, 100L)
        }
        viewModel.items.add(uploadModel)
        coEvery {
            trueCloudV3TransferObserver.getState()
        } returns TrueCloudV3TransferState.COMPLETED

        coEvery {
            trueCloudV3TransferObserver.getId()
        } returns 0

        // act
        viewModel.onSelectedUploadFileResult(intent)

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun testOnSelectedUploadFileResultOneFileShowProgressChangeIdNotMatch() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onShowProgressChange)
        val uploadModel = TrueCloudFilesModel.Upload(
            id = 1,
            progress = 30L,
            path = "path/x",
            status = TaskStatusType.IN_PROGRESS,
            name = "name",
            size = "size",
            type = FileMimeType.IMAGE,
            coverImageSize = 12
        )
        val intent = mockk<Intent>()
        val contentUri1 = mockk<Uri>(name = "content://uri-1")
        val trueCloudV3TransferObserver = mockk<TrueCloudV3TransferObserver>()
        val slotListener = slot<TrueCloudV3TransferObserver.TrueCloudV3TransferListener>()
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))
        every { intent.data } returns contentUri1
        every { intent.clipData } returns null
        every {
            uploadFileUseCase.execute(any(), any())
        } answers {
            flowOf(trueCloudV3TransferObserver)
        }
        every {
            completeUploadUseCase.execute(any())
        } returns flow {}
        every {
            trueCloudV3TransferObserver.setTransferListener(capture(slotListener))
        } answers {
            slotListener.captured.onProgressChanged(0, 20L, 100L)
        }
        viewModel.items.add(uploadModel)
        coEvery {
            trueCloudV3TransferObserver.getState()
        } returns TrueCloudV3TransferState.COMPLETED

        coEvery {
            trueCloudV3TransferObserver.getId()
        } returns 0

        // act
        viewModel.onSelectedUploadFileResult(intent)

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun testOnSelectedUploadFileResultOneFileError() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onShowProgressChange)
        val intent = mockk<Intent>()
        val contentUri1 = mockk<Uri>(name = "content://uri-1")
        val trueCloudV3TransferObserver = mockk<TrueCloudV3TransferObserver>()
        val slotListener = slot<TrueCloudV3TransferObserver.TrueCloudV3TransferListener>()
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))
        every { intent.data } returns contentUri1
        every { intent.clipData } returns null
        every {
            uploadFileUseCase.execute(any(), any())
        } answers {
            flowOf(trueCloudV3TransferObserver)
        }
        every {
            completeUploadUseCase.execute(any())
        } returns flow {}
        every {
            trueCloudV3TransferObserver.setTransferListener(capture(slotListener))
        } answers {
            slotListener.captured.onError(0, Exception())
        }
        coEvery {
            updateTaskUploadStatusUseCase.execute(any(), any())
        } returns Unit
        coEvery {
            trueCloudV3TransferObserver.getState()
        } returns TrueCloudV3TransferState.COMPLETED

        coEvery {
            trueCloudV3TransferObserver.getId()
        } returns 0

        // act
        viewModel.onSelectedUploadFileResult(intent)

        // assert
        coVerify(exactly = 1) {
            updateTaskUploadStatusUseCase.execute(any(), any())
        }
    }

    @Test
    fun `test onScrolled success`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSetCategoryName)
        val layoutManager = mockk<GridLayoutManager>()
        val x = GridLayoutManager(context, 1)
        coEvery {
            getStorageListUseCase.execute(any(), any(), any())
        } answers {
            flowOf(listOf(TrueCloudV3Model()))
        }
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))

        // act
        viewModel.onScrolled(2, x)

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test onScrolled with searchString success`() = runTest {
        // arrange
        val layoutManager = mockk<GridLayoutManager>()
        val x = GridLayoutManager(context, 1)
        coEvery {
            getStorageListUseCase.execute(any(), any(), any())
        } answers {
            flowOf(listOf(TrueCloudV3Model()))
        }
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))
        viewModel.onSearchStringInput("test")

        // act
        viewModel.onScrolled(2, x)

        // assert
        assertNotNull(viewModel.searchString)
    }

    @Test
    fun `test onScrolled with searchString with categorytype success`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSetCategoryName)
        val layoutManager = mockk<GridLayoutManager>()
        val x = GridLayoutManager(context, 1)
        coEvery {
            getStorageListUseCase.execute(any(), any(), any())
        } answers {
            flowOf(listOf(TrueCloudV3Model()))
        }
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))
        viewModel.onSearchStringInput("test")
        viewModel.categoryType = FileCategoryType.IMAGE

        // act
        viewModel.onScrolled(2, x)

        // assert
        assertNotNull(viewModel.searchString)
    }

    @Test
    fun `test onCloseSelectAll success`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onCloseSelectAll)
        val onSelectAllObserver = TestObserver.test(viewModel.onSelectAll)
        val fileModel = TrueCloudV3Model().convertToFilesModel(context)
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))
        viewModel.selectItemList.add(fileModel)
        every { analyticManagerInterface.trackEvent(any()) } just runs
        // act
        viewModel.closeSelectedItem()

        // assert
        testObserver.assertValue(true)
        onSelectAllObserver.assertValue(false)
    }

    @Test
    fun `test removeSelectItem success`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSelectUpdate)
        val file = TrueCloudFilesModel.File(
            id = "id",
            parentObjectId = "parentObjectId"
        )

        // act
        viewModel.removeSelectItem(file)

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test createFolder success`() = runTest {
        // arrange
        val snackbarObserver = TestObserver.test(viewModel.onShowSnackbarComplete)
        val testObserver = TestObserver.test(viewModel.onSetCategoryName)
        val trueCloudV3tModel = TrueCloudV3Model(
            id = "id",
            name = "name test",
            objectType = "FILE"
        )
        coEvery {
            createFolderUserCase.execute(any(), any())
        } answers {
            flowOf(trueCloudV3tModel)
        }
        coEvery {
            getStorageListWithCategoryUseCase.execute(
                category = any(),
                sortType = any(),
                skip = any()
            )
        } answers {
            flowOf(mutableListOf(trueCloudV3tModel))
        }
        viewModel.categoryType = FileCategoryType.IMAGE
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))

        // act
        viewModel.createFolder("folder name")

        // assert
        snackbarObserver.assertHasValue()
        testObserver.assertHasValue()
    }

    @Test
    fun `test createFolder failed`() = runTest {
        // arrange
        val snackbarObserver = TestObserver.test(viewModel.onShowSnackbarError)
        val trueCloudV3tModel = TrueCloudV3Model(
            id = "id",
            name = "name test",
            objectType = "FILE"
        )
        coEvery {
            createFolderUserCase.execute(any(), any())
        } coAnswers { flow { error("mock error") } }
        every {
            contextDataProviderWrapper.get()
                .getString(R.string.true_cloudv3_can_not_add_new_folder)
        } returns "true_cloudv3_can_not_add_new_folder"

        viewModel.categoryType = FileCategoryType.IMAGE
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))

        // act
        viewModel.createFolder("folder name")

        // assert
        snackbarObserver.assertHasValue()
    }

    @Test
    fun `test createFolder name null`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSetCategoryName)

        // act
        viewModel.createFolder(null)

        // assert
        testObserver.assertNoValue()
    }

    @Test
    fun `test createFolder unsupport type success`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSetCategoryName)
        val trueCloudV3Model = TrueCloudV3Model()
        trueCloudV3Model.objectType = "FOLDER"
        coEvery {
            createFolderUserCase.execute(any(), any())
        } answers {
            flowOf(trueCloudV3Model)
        }
        coEvery {
            getStorageListUseCase.execute(
                rootFolderId = any(),
                sortType = any(),
                skip = any()
            )
        } answers {
            flowOf(mutableListOf(trueCloudV3Model))
        }
        viewModel.categoryType = FileCategoryType.UNSUPPORTED_FORMAT
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))
        // act
        viewModel.createFolder("folder name")

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test getUploadTask success`() = runTest {
        // arrange
        val testhowUploadTaskListObserver = TestObserver.test(viewModel.onShowUploadTaskList)
        val trueCloudV3Model = TrueCloudV3Model()
        coEvery {
            createFolderUserCase.execute(any(), any())
        } answers {
            flowOf(trueCloudV3Model)
        }
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 10L,
            actionType = TaskActionType.UPLOAD
        )
        coEvery {
            getUploadTaskListUseCase.execute()
        } answers {
            flowOf(mutableListOf(taskUploadModel))
        }
        viewModel.categoryType = FileCategoryType.UNSUPPORTED_FORMAT
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))
        viewModel.isListLayout = false
        // act
        viewModel.getUploadTask()

        // assert
        testhowUploadTaskListObserver.assertHasValue()
    }

    @Test
    fun `test getUploadTask success empty list`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onShowFileList)
        val testhowUploadTaskListObserver = TestObserver.test(viewModel.onShowUploadTaskList)
        val trueCloudV3Model = TrueCloudV3Model()
        coEvery {
            createFolderUserCase.execute(any(), any())
        } answers {
            flowOf(trueCloudV3Model)
        }
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 0L,
            actionType = TaskActionType.UPLOAD
        )
        coEvery {
            getUploadTaskListUseCase.execute()
        } answers {
            flowOf(mutableListOf())
        }
        coEvery {
            removeAllTaskUseCase.execute()
        } returns Unit
        viewModel.categoryType = FileCategoryType.UNSUPPORTED_FORMAT
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))
        viewModel.isListLayout = false
        // act
        viewModel.getUploadTask()

        // assert
        coVerify {
            removeAllTaskUseCase.execute()
        }
    }

    @Test
    fun `test getUploadTask success id not 0 and complete`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onShowFileList)
        val testhowUploadTaskListObserver = TestObserver.test(viewModel.onShowUploadTaskList)
        val trueCloudV3Model = TrueCloudV3Model()
        coEvery {
            createFolderUserCase.execute(any(), any())
        } answers {
            flowOf(trueCloudV3Model)
        }
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 0L
        )
        coEvery {
            getUploadTaskListUseCase.execute()
        } answers {
            flowOf(mutableListOf(taskUploadModel))
        }
        coEvery {
            getUploadTaskListUseCase.execute()
        } returns flowOf(mutableListOf(taskUploadModel))

        val transfer: TrueCloudV3TransferObserver = mockk(relaxed = true)
        coEvery {
            trueCloudV3TransferUtilityProvider.getTrueCloudV3TransferObserverById(any(), any())
        } returns transfer
        coEvery {
            transfer.getState()
        } returns TrueCloudV3TransferState.COMPLETED
        every {
            completeUploadUseCase.execute(any())
        } returns flow {}
        viewModel.categoryType = FileCategoryType.UNSUPPORTED_FORMAT
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))
        viewModel.isListLayout = false
        // act
        viewModel.getUploadTask()

        // assert
        testhowUploadTaskListObserver.assertHasValue()
        testObserver.assertHasValue()
        verify {
            completeUploadUseCase.execute(any())
        }
    }

    @Test
    fun `test getUploadTask success id not 0 and cancel`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onShowFileList)
        val testhowUploadTaskListObserver = TestObserver.test(viewModel.onShowUploadTaskList)
        val trueCloudV3Model = TrueCloudV3Model()
        coEvery {
            createFolderUserCase.execute(any(), any())
        } answers {
            flowOf(trueCloudV3Model)
        }
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 0L
        )
        coEvery {
            getUploadTaskListUseCase.execute()
        } answers {
            flowOf(mutableListOf(taskUploadModel))
        }
        coEvery {
            getUploadTaskListUseCase.execute()
        } returns flowOf(mutableListOf(taskUploadModel))

        val transfer: TrueCloudV3TransferObserver = mockk(relaxed = true)
        coEvery {
            trueCloudV3TransferUtilityProvider.getTrueCloudV3TransferObserverById(any(), any())
        } returns transfer
        coEvery {
            transfer.getState()
        } returns TrueCloudV3TransferState.CANCELED
        coEvery {
            removeTaskUseCase.execute(any())
        } returns Unit
        viewModel.categoryType = FileCategoryType.UNSUPPORTED_FORMAT
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))
        viewModel.isListLayout = false
        // act
        viewModel.getUploadTask()

        // assert
        testhowUploadTaskListObserver.assertHasValue()
        testObserver.assertHasValue()
        coVerify(exactly = 1) {
            removeTaskUseCase.execute(any())
        }
    }

    @Test
    fun `test getUploadTask success id not 0 and IN_PROGRESS`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onShowFileList)
        val testhowUploadTaskListObserver = TestObserver.test(viewModel.onShowUploadTaskList)
        val trueCloudV3Model = TrueCloudV3Model()
        coEvery {
            createFolderUserCase.execute(any(), any())
        } answers {
            flowOf(trueCloudV3Model)
        }
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 0L
        )
        coEvery {
            getUploadTaskListUseCase.execute()
        } answers {
            flowOf(mutableListOf(taskUploadModel))
        }
        coEvery {
            getUploadTaskListUseCase.execute()
        } returns flowOf(mutableListOf(taskUploadModel))

        val transfer: TrueCloudV3TransferObserver = mockk(relaxed = true)
        coEvery {
            trueCloudV3TransferUtilityProvider.getTrueCloudV3TransferObserverById(any(), any())
        } returns transfer
        coEvery {
            transfer.getState()
        } returns TrueCloudV3TransferState.IN_PROGRESS
        coEvery {
            trueCloudV3TransferUtilityProvider.resumeTransferById(any(), any())
        } returns transfer
        viewModel.categoryType = FileCategoryType.UNSUPPORTED_FORMAT
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))
        viewModel.isListLayout = false
        // act
        viewModel.getUploadTask()

        // assert
        testhowUploadTaskListObserver.assertHasValue()
        testObserver.assertHasValue()
        coVerify(exactly = 1) {
            trueCloudV3TransferUtilityProvider.resumeTransferById(any(), any())
        }
    }

    @Test
    fun `test getUploadTask success id not 0 and RESUMED_WAITING`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onShowFileList)
        val testhowUploadTaskListObserver = TestObserver.test(viewModel.onShowUploadTaskList)
        val trueCloudV3Model = TrueCloudV3Model()
        coEvery {
            createFolderUserCase.execute(any(), any())
        } answers {
            flowOf(trueCloudV3Model)
        }
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 0L
        )
        coEvery {
            getUploadTaskListUseCase.execute()
        } answers {
            flowOf(mutableListOf(taskUploadModel))
        }
        coEvery {
            getUploadTaskListUseCase.execute()
        } returns flowOf(mutableListOf(taskUploadModel))

        val transfer: TrueCloudV3TransferObserver = mockk(relaxed = true)
        coEvery {
            trueCloudV3TransferUtilityProvider.getTrueCloudV3TransferObserverById(any(), any())
        } returns transfer
        coEvery {
            trueCloudV3TransferUtilityProvider.resumeTransferById(any(), any())
        } returns transfer
        coEvery {
            transfer.getState()
        } returns TrueCloudV3TransferState.RESUMED_WAITING
        coEvery {
            updateTaskUploadStatusUseCase.execute(any(), any())
        } returns Unit
        viewModel.categoryType = FileCategoryType.UNSUPPORTED_FORMAT
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))
        viewModel.isListLayout = false
        // act
        viewModel.getUploadTask()

        // assert
        testhowUploadTaskListObserver.assertHasValue()
        testObserver.assertHasValue()
        coVerify(exactly = 1) {
            updateTaskUploadStatusUseCase.execute(any(), any())
            trueCloudV3TransferUtilityProvider.resumeTransferById(any(), any())
        }
    }

    @Test
    fun `test getUploadTask success id not 0 and PAUSED`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onShowFileList)
        val testhowUploadTaskListObserver = TestObserver.test(viewModel.onShowUploadTaskList)
        val trueCloudV3Model = TrueCloudV3Model()
        coEvery {
            createFolderUserCase.execute(any(), any())
        } answers {
            flowOf(trueCloudV3Model)
        }
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 0L
        )
        coEvery {
            getNewUploadTaskListUseCase.execute()
        } answers {
            flowOf(mutableListOf(taskUploadModel))
        }
        coEvery {
            getUploadTaskListUseCase.execute()
        } returns flowOf(mutableListOf(taskUploadModel))

        val transfer: TrueCloudV3TransferObserver = mockk(relaxed = true)
        coEvery {
            trueCloudV3TransferUtilityProvider.getTrueCloudV3TransferObserverById(any(), any())
        } returns transfer
        coEvery {
            transfer.getState()
        } returns TrueCloudV3TransferState.PAUSED
        coEvery {
            updateTaskUploadStatusUseCase.execute(any(), any())
        } returns Unit
        viewModel.categoryType = FileCategoryType.UNSUPPORTED_FORMAT
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))
        viewModel.isListLayout = false
        // act
        viewModel.getUploadTask()

        // assert
        testhowUploadTaskListObserver.assertHasValue()
        testObserver.assertHasValue()
        coVerify(exactly = 1) {
            updateTaskUploadStatusUseCase.execute(any(), any())
        }
    }

    @Test
    fun `test getUploadTask success id not 0 and FAILED`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onShowFileList)
        val testhowUploadTaskListObserver = TestObserver.test(viewModel.onShowUploadTaskList)
        val trueCloudV3Model = TrueCloudV3Model()
        coEvery {
            createFolderUserCase.execute(any(), any())
        } answers {
            flowOf(trueCloudV3Model)
        }
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 0L
        )
        coEvery {
            getUploadTaskListUseCase.execute()
        } answers {
            flowOf(mutableListOf(taskUploadModel))
        }
        coEvery {
            getUploadTaskListUseCase.execute()
        } returns flowOf(mutableListOf(taskUploadModel))

        val transfer: TrueCloudV3TransferObserver = mockk(relaxed = true)
        coEvery {
            trueCloudV3TransferUtilityProvider.getTrueCloudV3TransferObserverById(any(), any())
        } returns transfer
        coEvery {
            transfer.getState()
        } returns TrueCloudV3TransferState.FAILED
        coEvery {
            updateTaskUploadStatusUseCase.execute(any(), any())
        } returns Unit
        viewModel.categoryType = FileCategoryType.UNSUPPORTED_FORMAT
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))
        viewModel.isListLayout = false
        // act
        viewModel.getUploadTask()

        // assert
        testhowUploadTaskListObserver.assertHasValue()
        testObserver.assertHasValue()
        coVerify(exactly = 1) {
            updateTaskUploadStatusUseCase.execute(any(), any())
        }
    }

    @Test
    fun `test getUploadTask success id not 0 and PENDING_PAUSE`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onShowFileList)
        val testhowUploadTaskListObserver = TestObserver.test(viewModel.onShowUploadTaskList)
        val trueCloudV3Model = TrueCloudV3Model()
        coEvery {
            createFolderUserCase.execute(any(), any())
        } answers {
            flowOf(trueCloudV3Model)
        }
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 0L
        )
        coEvery {
            getUploadTaskListUseCase.execute()
        } answers {
            flowOf(mutableListOf(taskUploadModel))
        }
        coEvery {
            getUploadTaskListUseCase.execute()
        } returns flowOf(mutableListOf(taskUploadModel))

        val transfer: TrueCloudV3TransferObserver = mockk(relaxed = true)
        coEvery {
            trueCloudV3TransferUtilityProvider.getTrueCloudV3TransferObserverById(any(), any())
        } returns transfer
        coEvery {
            transfer.getState()
        } returns TrueCloudV3TransferState.PENDING_PAUSE
        coEvery {
            updateTaskUploadStatusUseCase.execute(any(), any())
        } returns Unit
        viewModel.categoryType = FileCategoryType.UNSUPPORTED_FORMAT
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))
        viewModel.isListLayout = false
        // act
        viewModel.getUploadTask()

        // assert
        testhowUploadTaskListObserver.assertHasValue()
        testObserver.assertHasValue()
        coVerify(exactly = 1) {
            updateTaskUploadStatusUseCase.execute(any(), any())
        }
    }

    @Test
    fun `test getUploadTask success id not 0 and transfer null`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onShowFileList)
        val testhowUploadTaskListObserver = TestObserver.test(viewModel.onShowUploadTaskList)
        val trueCloudV3Model = TrueCloudV3Model()
        coEvery {
            createFolderUserCase.execute(any(), any())
        } answers {
            flowOf(trueCloudV3Model)
        }
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.WAITING,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 0L,
            objectId = "object1"
        )
        coEvery {
            getUploadTaskListUseCase.execute()
        } returns flowOf(mutableListOf(taskUploadModel))

        val transfer: TrueCloudV3TransferObserver = mockk(relaxed = true)
        coEvery {
            uploadQueueUseCase.execute(any())
        } returns flowOf(transfer)
        every { transfer.getState() } returns TrueCloudV3TransferState.COMPLETED
        coEvery {
            trueCloudV3TransferUtilityProvider.getTrueCloudV3TransferObserverById(any(), any())
        } returns transfer
        coEvery {
            completeUploadUseCase.execute(any())
        } returns flow {}
        viewModel.categoryType = FileCategoryType.UNSUPPORTED_FORMAT
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))
        viewModel.isListLayout = false
        // act
        viewModel.getUploadTask()

        // assert
        testhowUploadTaskListObserver.assertHasValue()
        testObserver.assertHasValue()
    }

    @Test
    fun `test downloadCoverImage success`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onLoadCoverFinished)
        val trueCloudV3TransferObserver = mockk<TrueCloudV3TransferObserver>()
        val slotListener = slot<TrueCloudV3TransferObserver.TrueCloudV3TransferListener>()
        every {
            contextDataProviderWrapper.get().getDataContext().cacheDir.absolutePath
        } returns "/cacheDir/absolutePath"
        coEvery {
            downloadCoverImageUseCase.execute(
                any(),
                any()
            )
        } answers {
            flowOf(trueCloudV3TransferObserver)
        }
        every {
            trueCloudV3TransferObserver.setTransferListener(capture(slotListener))
        } just runs
        val mockFile = TrueCloudFilesModel.File(
            id = "123",
            coverImageKey = "cover_key",
        )
        // act
        viewModel.itemList.add(mockFile)
        viewModel.downloadCoverImage("cover_key")
        slotListener.captured.onStateChanged(0, TrueCloudV3TransferState.COMPLETED)
        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test onClickPause success`() = runTest {
        // arrange
        coEvery {
            updateTaskUploadStatusUseCase.execute(
                any(),
                any()
            )
        } returns Unit
        coEvery {
            trueCloudV3TransferUtilityProvider.pauseTransferById(any(), any())
        } returns true

        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 0L
        )
        // act
        viewModel.onClickPause(taskUploadModel.getUploadFilesModel())

        // assert
        coVerify(exactly = 1) {
            updateTaskUploadStatusUseCase.execute(
                any(),
                any()
            )
        }
    }

    @Test
    fun `test onClickCancel success`() = runTest {
        // arrange
        coEvery {
            removeTaskUseCase.execute(any())
        } returns Unit
        coEvery {
            trueCloudV3TransferUtilityProvider.cancelTransferById(any(), any())
        } returns true
        coEvery {
            deleteFileUseCase.execute(any())
        } returns flowOf(TrueCloudV3Model())

        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            objectId = "1234321",
            type = FileMimeType.IMAGE,
            updateAt = 0L
        )
        // act
        viewModel.onClickCancel(taskUploadModel.getUploadFilesModel())

        // assert
        coVerify(exactly = 1) {
            removeTaskUseCase.execute(any())
        }
    }

    @Test
    fun `test onClickCancelAll success`() = runTest {
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
            updateAt = 1L
        )

        coEvery {
            removeAllTaskUseCase.execute()
        } returns Unit
        coEvery {
            trueCloudV3TransferUtilityProvider.getDefaultTransferUtility(any())
                .cancelAllWithType(any())
        } returns Unit
        coEvery {
            getUploadTaskListUseCase.execute()
        } returns flowOf(mutableListOf(taskUploadModel))
        coEvery {
            deleteFileUseCase.execute(any())
        } returns flowOf(TrueCloudV3Model())

        // act
        viewModel.onClickCancelAll()

        // assert
        coVerify(exactly = 1) {
            removeAllTaskUseCase.execute()
            //  getUploadTaskListUseCase.execute()
        }
    }

    @Test
    fun `test onClickRetry status PAUSE success`() = runTest {
        // arrange
        val trueCloudV3TransferObserver = mockk<TrueCloudV3TransferObserver>()
        val slotListener = slot<TrueCloudV3TransferObserver.TrueCloudV3TransferListener>()
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.PAUSE,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            actionType = TaskActionType.UPLOAD,
            updateAt = 0L
        )
        val transfer: TrueCloudV3TransferObserver = mockk(relaxed = true)
        every {
            contextDataProviderWrapper.get().getDataContext()
        } returns context
        coEvery {
            trueCloudV3TransferUtilityProvider.getTrueCloudV3TransferObserverById(any(), any())
        } returns transfer
        coEvery {
            updateTaskUploadStatusUseCase.execute(
                any(),
                any()
            )
        } returns Unit
        coEvery {
            trueCloudV3TransferUtilityProvider.getDefaultTransferUtility(
                contextDataProviderWrapper.get().getDataContext()
            )
                .resume(taskUploadModel.id)?.setTransferListener(any())
        } returns Unit
        coEvery {
            trueCloudV3TransferUtilityProvider.resumeTransferById(any(), any())
        } returns trueCloudV3TransferObserver
        every {
            trueCloudV3TransferObserver.setTransferListener(capture(slotListener))
        } answers {
            slotListener.captured.onStateChanged(0, TrueCloudV3TransferState.COMPLETED)
        }

        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))

        // act
        viewModel.onClickRetry(taskUploadModel.getUploadFilesModel())

        // assert
        coVerify(exactly = 1) {
            updateTaskUploadStatusUseCase.execute(
                any(),
                any()
            )
        }
    }

    @Test
    fun `test onClickRetry status API complete success`() = runTest {
        // arrange
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.COMPLETE_API_FAILED,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            actionType = TaskActionType.UPLOAD,
            updateAt = 0L
        )
        val transfer: TrueCloudV3TransferObserver = mockk(relaxed = true)
        every {
            completeUploadUseCase.execute(any())
        } returns flowOf()
        every {
            contextDataProviderWrapper.get().getDataContext()
        } returns context
        coEvery {
            trueCloudV3TransferUtilityProvider.getTrueCloudV3TransferObserverById(any(), any())
        } returns transfer
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))

        // act
        viewModel.onClickRetry(taskUploadModel.getUploadFilesModel())

        // assert
        coVerify(exactly = 1) {
            completeUploadUseCase.execute(any())
        }
    }

    @Test
    fun `test onClickRetry status Not Pause success`() = runTest {
        // arrange
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.COMPLETE,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            actionType = TaskActionType.UPLOAD,
            updateAt = 0L
        )
        val transfer: TrueCloudV3TransferObserver = mockk(relaxed = true)

        every {
            contextDataProviderWrapper.get().getDataContext()
        } returns context
        coEvery {
            trueCloudV3TransferUtilityProvider.getTrueCloudV3TransferObserverById(any(), any())
        } returns transfer
        coEvery {
            removeTaskUseCase.execute(any())
        } returns Unit
        coEvery {
            retryUploadUseCase.execute(any())
        } returns mockk()
        coEvery {
            uploadFileWithPathUseCase.execute(any(), any())
        } returns mockk()

        coEvery {
            trueCloudV3TransferUtilityProvider.cancelTransferById(any(), any())
        } returns true

        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))

        // act
        viewModel.onClickRetry(taskUploadModel.getUploadFilesModel())

        // assert
        coVerify(exactly = 1) {
            uploadFileWithPathUseCase.execute(any(), any())
        }
    }

    @Test
    fun `test onClickRetry status Not Pause has objectid success`() = runTest {
        // arrange
        val taskUploadModel = TaskUploadModel(
            id = 1,
            objectId = "1234",
            path = "abc",
            status = TaskStatusType.COMPLETE,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            actionType = TaskActionType.UPLOAD,
            updateAt = 0L
        )
        val transfer: TrueCloudV3TransferObserver = mockk(relaxed = true)
        every {
            contextDataProviderWrapper.get().getDataContext()
        } returns context
        coEvery {
            trueCloudV3TransferUtilityProvider.getTrueCloudV3TransferObserverById(any(), any())
        } returns transfer
        coEvery {
            retryUploadUseCase.execute(any())
        } returns mockk()
        coEvery {
            uploadFileWithPathUseCase.execute(any(), any())
        } returns mockk()
        coEvery {
            trueCloudV3TransferUtilityProvider.cancelTransferById(any(), any())
        } returns true

        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))

        // act
        viewModel.onClickRetry(taskUploadModel.getUploadFilesModel())

        // assert
        coVerify(exactly = 1) {
            retryUploadUseCase.execute(any())
        }
    }

    @Test
    fun `test onClickBack success`() {
        // arrange
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))
        viewModel.isSelectMode = false
        val testObserver = TestObserver.test(viewModel.onBackPressed)
        // act
        viewModel.onClickBack()

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test onClickUpload success`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onOpenSelectFile)

        // act
        viewModel.onClickUpload()

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test onClickBack from folder success`() {
        // arrange
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.addAll(
            listOf(
                Pair("a", "aa"),
                Pair("b", "aa")
            )
        )
        coEvery {
            getStorageListUseCase.execute(any(), any(), any())
        } returns flow {}

        // act
        viewModel.onClickBack()

        // assertd
        coVerify(exactly = 1) {
            getStorageListUseCase.execute(any(), any(), any())
        }
    }

    @Test
    fun `test onSortByClick success`() {
        // arrange
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.addAll(
            listOf(
                Pair("a", "aa"),
                Pair("b", "aa")
            )
        )
        coEvery {
            getStorageListUseCase.execute(any(), any(), any())
        } returns flow {}

        // act
        viewModel.onSortByClick(SortType.SORT_DATE_DESC)

        // assertd
        coVerify(exactly = 1) {
            getStorageListUseCase.execute(any(), any(), any())
        }
    }

    @Test
    fun `test onSortByClick selectedSortType is null`() {
        // act
        viewModel.onSortByClick(null)

        // assertd
        coVerify(exactly = 0) {
            getStorageListUseCase.execute(any(), any(), any())
        }
    }

    @Test
    fun `test onClickBack select mode success`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onCloseSelectAll)
        viewModel.isSelectMode = true
        // act
        viewModel.onClickBack()

        // assert
        testObserver.assertValue(true)
    }

    @Test
    fun `test completeUpload success`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onShowSnackbarComplete)
        val trueCloudV3Model = TrueCloudV3Model(
            id = "id",
            name = "name test",
            objectType = "FOLDER",
            parentObjectId = "b"
        )

        coEvery {
            getStorageListUseCase.execute(any(), any(), any())
        } returns flowOf(listOf(trueCloudV3Model))
        coEvery {
            completeUploadUseCase.execute(any())
        } returns flowOf(trueCloudV3Model)
        every { contextDataProviderWrapper.get().getString(any()) } returns "Successfully Uploaded"

        coEvery {
            removeTaskUseCase.execute(any())
        } returns Unit
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.addAll(
            listOf(
                Pair("a", "aa"),
                Pair("b", "aa")
            )
        )
        // act
        viewModel.completeUpload(1)

        // assert
        testObserver.assertValue { _msg ->
            assertEquals("Successfully Uploaded", _msg)
            true
        }
        coVerify(exactly = 1) {
            removeTaskUseCase.execute(any())
        }
        assertEquals(viewModel.folderItemSize, 1)
    }

    @Test
    fun `test completeUpload file type Video success`() = runTest {
        // arrange
        val trueCloudV3Model = TrueCloudV3Model()
        trueCloudV3Model.mimeType = FileMimeType.VIDEO.mimeType
        coEvery {
            completeUploadUseCase.execute(any())
        } returns flowOf(trueCloudV3Model)

        coEvery {
            removeTaskUseCase.execute(any())
        } returns Unit
        // act
        viewModel.completeUpload(1)

        // assert
        coVerify(exactly = 1) {
            removeTaskUseCase.execute(any())
        }
    }

    @Test
    fun `test completeUpload file type Image success`() = runTest {
        // arrange
        val trueCloudV3Model = TrueCloudV3Model()
        trueCloudV3Model.mimeType = FileMimeType.IMAGE.mimeType
        coEvery {
            completeUploadUseCase.execute(any())
        } returns flowOf(trueCloudV3Model)

        coEvery {
            removeTaskUseCase.execute(any())
        } returns Unit
        every { analyticManagerInterface.trackEvent(any()) } just runs
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.addAll(
            listOf(
                Pair("a", "aa"),
                Pair("b", "aa")
            )
        )
        // act
        viewModel.completeUpload(1)

        // assert
        coVerify(exactly = 1) {
            analyticManagerInterface.trackEvent(any())
            removeTaskUseCase.execute(any())
        }
    }

    @Test
    fun `test completeUpload file type Audio success`() = runTest {
        // arrange
        val trueCloudV3Model = TrueCloudV3Model()
        trueCloudV3Model.mimeType = FileMimeType.AUDIO.mimeType
        coEvery {
            completeUploadUseCase.execute(any())
        } returns flowOf(trueCloudV3Model)

        coEvery {
            removeTaskUseCase.execute(any())
        } returns Unit
        // act
        viewModel.completeUpload(1)

        // assert
        coVerify(exactly = 1) {
            removeTaskUseCase.execute(any())
        }
    }

    @Test
    fun `test rename success`() = runTest {
        // arrange
        val trueCloudV3Model = TrueCloudV3Model(
            id = "1234"
        )
        val testObserver = TestObserver.test(viewModel.onRenameFileSuccess)
        coEvery {
            renameFileUseCase.execute(any(), any())
        } returns flowOf(trueCloudV3Model)
        // act
        viewModel.rename(trueCloudV3Model.convertToFilesModel(context))
        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test rename null`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onRenameFileSuccess)
        // act
        viewModel.rename(null)
        // assert
        testObserver.assertNoValue()
    }

    @Test
    fun `test getSpanSize no item success`() = runTest {
        // arrange
        viewModel.items.clear()
        // act
        val response = viewModel.getSpanSize(0, true)

        // assert
        assertEquals(2, response)
    }

    @Test
    fun `test getSpanSize no item horizontal false`() = runTest {
        // arrange
        viewModel.items.clear()
        // act
        val response = viewModel.getSpanSize(0, false)

        // assert
        assertEquals(1, response)
    }

    @Test
    fun `test getSpanSize Upload horizontal false`() = runTest {
        // arrange
        val x = TrueCloudFilesModel.Upload(
            id = 1,
            path = "path/x",
            status = TaskStatusType.IN_PROGRESS,
            name = "name",
            size = "size",
            type = FileMimeType.IMAGE,
            coverImageSize = 12
        )
        viewModel.items.add(x)
        // act
        val response = viewModel.getSpanSize(0, false)

        // assert
        assertEquals(1, response)
    }

    @Test
    fun `test getSpanSize Upload horizontal true`() = runTest {
        // arrange
        val x = TrueCloudFilesModel.Upload(
            id = 1,
            path = "path/x",
            status = TaskStatusType.IN_PROGRESS,
            name = "name",
            size = "size",
            type = FileMimeType.IMAGE,
            coverImageSize = 12
        )
        viewModel.items.add(x)
        // act
        val response = viewModel.getSpanSize(0, true)

        // assert
        assertEquals(2, response)
    }

    @Test
    fun `test getSpanSize File horizontal true`() = runTest {
        // arrange
        val x = TrueCloudFilesModel.File(
            id = "123"
        )
        viewModel.items.add(x)
        // act
        val response = viewModel.getSpanSize(0, true)

        // assert
        assertEquals(2, response)
    }

    @Test
    fun `test getSpanSize File horizontal false`() = runTest {
        // arrange
        val x = TrueCloudFilesModel.File(
            id = "123"
        )
        viewModel.items.add(x)
        // act
        val response = viewModel.getSpanSize(0, false)

        // assert
        assertEquals(1, response)
    }

    @Test
    fun `test getSpanSize Folder horizontal false`() = runTest {
        // arrange
        val x = TrueCloudFilesModel.Folder(
            id = "123"
        )
        viewModel.items.add(x)
        // act
        val response = viewModel.getSpanSize(0, false)

        // assert
        assertEquals(1, response)
    }

    @Test
    fun `test getSpanSize Folder horizontal true`() = runTest {
        // arrange
        val x = TrueCloudFilesModel.Folder(
            id = "123"
        )
        viewModel.items.add(x)
        // act
        val response = viewModel.getSpanSize(0, true)

        // assert
        assertEquals(2, response)
    }

    @Test
    fun `test getSpanSize Header horizontal true`() = runTest {
        // arrange
        val x = TrueCloudFilesModel.Header(
            title = "123",
            headerType = HeaderType.FILE
        )
        viewModel.items.add(x)
        // act
        val response = viewModel.getSpanSize(0, true)

        // assert
        assertEquals(2, response)
    }

    @Test
    fun `test getNextActionIntent success`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onIntentActionGetContent)
        // act
        viewModel.getNextActionIntent(TrueCloudV3MediaType.TypeImage)

        // assert
        testObserver.assertValue(TrueCloudV3MediaType.TypeImage.mimeType)
    }

    @Test
    fun `test removeLastFolder success`() = runTest {
        // arrange
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.addAll(
            listOf(
                Pair("a", "aa"),
                Pair("b", "aa")
            )
        )
        // act
        viewModel.removeLastFolder()

        // assert
        assertEquals(1, FilesTrueCloudViewModel.Companion.stackFolderIds.size)
    }

    @Test
    fun `test onDeleteFile success`() = runTest {
        // arrange
        val testSnackbarCompleteObserver = TestObserver.test(viewModel.onShowSnackbarComplete)
        val testObserver = TestObserver.test(viewModel.onDeleteFileSuccess)
        val file = TrueCloudFilesModel.File(
            id = "id",
            parentObjectId = "parentObjectId"
        )
        val treucloudModel = TrueCloudV3Model()
        coEvery {
            deleteFileUseCase.execute(any())
        } returns flowOf(treucloudModel)
        // act
        viewModel.onDeleteFile(file)

        // assert
        testSnackbarCompleteObserver.assertHasValue()
        testObserver.assertValue(file)
    }

    @Test
    fun `test onDeleteFile null`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onDeleteFileSuccess)
        val file = TrueCloudFilesModel.File(
            id = "id",
            parentObjectId = "parentObjectId"
        )
        val treucloudModel = TrueCloudV3Model()
        coEvery {
            deleteFileUseCase.execute(any())
        } returns flowOf(treucloudModel)
        // act
        viewModel.onDeleteFile(file)

        // assert
        testObserver.assertValue(file)
    }

    @Test
    fun `test onDownloadFile success`() = runTest {
        // arrange
        val mockNotificationManagerCompat = mockk<NotificationManagerCompat>(relaxed = true)
        every { mockNotificationManagerCompat.areNotificationsEnabled() } returns true
        val file = TrueCloudFilesModel.File(
            id = "id",
            parentObjectId = "parentObjectId",
            name = "fileName"
        )
        val context = mockk<Context>()
        every {
            contextDataProviderWrapper.get().getDataContext()
        } returns context
        every { analyticManagerInterface.trackEvent(any()) } just runs
        mockkStatic(Environment::class)
        every { Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path } returns "/storage/emulated/0"
        val mockNotificationManager = mockk<NotificationManager>(relaxed = true)
        every { context.getSystemService(any()) } returns mockNotificationManager
        // act
        viewModel.onDownloadFile(file)

        // assert
        coVerify(exactly = 1) {
            context.getSystemService(any())
            analyticManagerInterface.trackEvent(any())
        }
    }

    @Test
    fun `test onDownloadFile file is null `() = runTest {
        // act
        viewModel.onDownloadFile(null)

        // assert
        coVerify(exactly = 0) {
            context.getSystemService(any())
        }
    }

    @Test
    fun `test checkRetryState getAllFileStorageList`() = runTest {
        // arrange
        val trueCloudV3Model = TrueCloudV3Model(
            id = "id",
            name = "name test",
            objectType = "FOLDER"
        )
        every {
            getStorageListUseCase.execute(any(), any(), any())
        } returns flowOf(listOf(trueCloudV3Model))

        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.addAll(
            listOf(
                Pair("a", "aa"),
                Pair("b", "aa")
            )
        )
        // act
        viewModel.checkRetryState("ACTION_GET_ALL_FILE_LIST")

        // assert
        coVerify(exactly = 1) {
            getStorageListUseCase.execute(any(), any(), any())
        }
    }

    @Test
    fun `test checkRetryState getFileCategoryStorageList`() = runTest {
        // arrange
        every {
            getStorageListWithCategoryUseCase.execute(any(), any(), any())
        } returns flowOf()

        // act
        viewModel.checkRetryState("ACTION_GET_CATEGORY_FILE_LIST")

        // assert
        coVerify(exactly = 1) {
            getStorageListWithCategoryUseCase.execute(any(), any(), any())
        }
    }

    @Test
    fun `test checkRetryState not match`() = runTest {
        // arrange

        // act
        viewModel.checkRetryState("")

        // assert
        coVerify(exactly = 0) {
            getStorageListWithCategoryUseCase.execute(any(), any(), any())
            getStorageListUseCase.execute(any(), any(), any())
        }
    }

    @Test
    fun `test setSelectedList success type move`() = runTest {
        // arrange
        val itemList = arrayListOf<String>("1")
        // act
        viewModel.setSelectedList(itemList, "move", "cat")
        // assert
        assertEquals("1", viewModel.itemListId[0])
        assertEquals("move", viewModel.type)
        assertEquals("cat", viewModel.checkType)
    }

    @Test
    fun `test setSelectedList success type copy`() = runTest {
        // arrange
        val itemList = arrayListOf<String>("1")
        // act
        viewModel.setSelectedList(itemList, "copy", "cat")
        // assert
        assertEquals("copy", viewModel.type)
    }

    @Test
    fun `test onClickLocate success type move`() = runTest {
        // arrange
        val status = 201
        val testObserver = TestObserver.test(viewModel.onLocateFileSuccess)
        coEvery {
            fileLocatorUseCase.execute(any(), any(), any())
        } answers {
            flowOf(status)
        }
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.addAll(
            listOf(
                Pair("a", "aa"),
                Pair("b", "aa")
            )
        )
        viewModel.itemListId.add("1")
        viewModel.type = "move"

        // act
        viewModel.onClickLocate()

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test onClickLocate success type copy`() = runTest {
        // arrange
        val status = 201
        val testObserver = TestObserver.test(viewModel.onLocateFileSuccess)
        coEvery {
            fileLocatorUseCase.execute(any(), any(), any())
        } answers {
            flowOf(status)
        }
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.addAll(
            listOf(
                Pair("a", "aa"),
                Pair("b", "aa")
            )
        )
        viewModel.itemListId.add("1")
        viewModel.type = "copy"

        // act
        viewModel.onClickLocate()

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test onClickLocate error type move`() = runTest {
        // arrange
        val status = 999
        val testObserver = TestObserver.test(viewModel.onLocateFileError)
        coEvery {
            fileLocatorUseCase.execute(any(), any(), any())
        } answers {
            flowOf(status)
        }
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.addAll(
            listOf(
                Pair("a", "aa"),
                Pair("b", "aa")
            )
        )
        viewModel.type = "move"

        // act
        viewModel.onClickLocate()

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test onClickLocate error type copy`() = runTest {
        // arrange
        val status = 999
        val testObserver = TestObserver.test(viewModel.onLocateFileError)
        coEvery {
            fileLocatorUseCase.execute(any(), any(), any())
        } answers {
            flowOf(status)
        }
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        viewModel.type = "copy"

        // act
        viewModel.onClickLocate()

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test onClickLocate error type none`() = runTest {
        // arrange
        val status = 999
        val testObserver = TestObserver.test(viewModel.onLocateFileError)
        coEvery {
            fileLocatorUseCase.execute(any(), any(), any())
        } answers {
            flowOf(status)
        }

        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.addAll(
            listOf(
                Pair("a", "aa"),
                Pair("b", "aa")
            )
        )
        viewModel.type = "none"

        // act
        viewModel.onClickLocate()

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test onOpenFileSelectedBottomSheet success`() {
        // arrange
        every { router.execute(any()) } just runs
        val file = TrueCloudFilesModel.File(
            id = "id",
            parentObjectId = "parentObjectId"
        )
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.addAll(
            listOf(
                Pair("a", "aa"),
                Pair("b", "aa")
            )
        )
        viewModel.selectItemList.add(file)
        viewModel.categoryType = FileCategoryType.UNSUPPORTED_FORMAT

        // act
        viewModel.onClickSelectOption()

        // assert
        verify(exactly = 1) { router.execute(SelectFileToOptionFileSelectedBottomSheet, any()) }
    }

    @Test
    fun `test onSelectOption success`() {
        // arrange
        every { router.execute(any()) } just runs
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.addAll(
            listOf(
                Pair("a", "aa"),
                Pair("b", "aa")
            )
        )
        // act
        viewModel.onClickSelectOption()

        // assert
        verify(exactly = 1) { router.execute(SelectFileToOptionFileSelectedBottomSheet, any()) }
    }

    @Test
    fun `test onRestoreStackIds success`() {
        // arrange
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.addAll(
            listOf(
                Pair("a", "aa"),
                Pair("b", "aa")
            )
        )
        FilesTrueCloudViewModel.Companion.stackFolderIdsCopy.addAll(
            listOf(
                Pair("a", "aa"),
                Pair("b", "aa")
            )
        )

        // act
        viewModel.onRestoreStackIds()

        // assert
        assertNotEquals(0, FilesTrueCloudViewModel.Companion.stackFolderIds.size)
    }

    @Test
    fun `test onRestoreStackIds with searchString success`() {
        // arrange
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.addAll(
            listOf(
                Pair("a", "aa"),
                Pair("b", "aa")
            )
        )
        FilesTrueCloudViewModel.Companion.stackFolderIdsCopy.addAll(
            listOf(
                Pair("a", "aa"),
                Pair("b", "aa")
            )
        )
        viewModel.onSearchStringInput("test")

        // act
        viewModel.onRestoreStackIds()

        // assert
        assertNotEquals(0, FilesTrueCloudViewModel.Companion.stackFolderIds.size)
    }

    @Test
    fun `test onCheckSelectOptionVisibility success`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onChangeSelectOption)
        viewModel.categoryType = FileCategoryType.UNSUPPORTED_FORMAT
        // act
        viewModel.checkSelectOptionVisibility()

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test onLocateFinish success`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onCloseSelectAll)
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.addAll(
            listOf(
                Pair("a", "aa"),
                Pair("b", "aa")
            )
        )
        FilesTrueCloudViewModel.Companion.stackFolderIdsCopy.addAll(
            listOf(
                Pair("a", "aa"),
                Pair("b", "aa")
            )
        )

        // act
        viewModel.onLocateFinish(false)

        // assert
        assertNotEquals(0, FilesTrueCloudViewModel.Companion.stackFolderIds.size)
        testObserver.assertValue(true)
    }

    @Test
    fun `test onClickFolderMoreOption success`() = runTest {
        // arrange
        every { router.execute(any()) } just runs
        val folder = TrueCloudFilesModel.Folder(
            id = "id",
            parentObjectId = "parentObjectId"
        )
        // act
        viewModel.onClickFolderMoreOption(folder)

        // assert
        verify(exactly = 1) { router.execute(AllFileToOptionFileBottomSheet, any()) }
    }

    @Test
    fun `test deleteGroupFile success`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onCloseSelectAll)
        val onSelectAllObserver = TestObserver.test(viewModel.onSelectAll)
        val file = TrueCloudFilesModel.File(
            id = "id",
            parentObjectId = "parentObjectId"
        )
        // act
        viewModel.onDeleteGroupFile(mutableListOf(file))

        // assert
        testObserver.assertValue(true)
        onSelectAllObserver.assertValue(false)
    }

    @Test
    fun `test onGetTrashData success`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSetFileList)
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))
        val fileModel = TrueCloudV3Model(
            id = "1",
            objectType = "FOLDER"
        )
        coEvery {
            getTrashListUseCase.execute()
        } answers {
            flowOf(listOf(fileModel))
        }
        every { analyticManagerInterface.trackScreen(any()) } just runs
        // act
        viewModel.onGetTrashData("folderId")

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test onClickHeaderTrashMoreOption success`() = runTest {
        // arrange
        every { router.execute(any()) } just runs
        // act
        viewModel.onClickHeaderTrashMoreOption(true)

        // assert
        verify(exactly = 1) { router.execute(TrashToMainOptionBottomSheet, any()) }
    }

    @Test
    fun `test onClickTrashFileOption success`() = runTest {
        // arrange
        every { router.execute(any()) } just runs
        val file = TrueCloudFilesModel.File(
            id = "id",
            parentObjectId = "parentObjectId"
        )
        // act
        viewModel.onClickTrashFileOption(file)

        // assert
        verify(exactly = 1) { router.execute(TrashToTrashBottomSheet, any()) }
    }

    @Test
    fun `test onClickTrashFolderOption success`() = runTest {
        // arrange
        every { router.execute(any()) } just runs
        val file = TrueCloudFilesModel.Folder(
            id = "id",
            parentObjectId = "parentObjectId"
        )
        // act
        viewModel.onClickTrashFolderOption(file)

        // assert
        verify(exactly = 1) { router.execute(TrashToTrashBottomSheet, any()) }
    }

    @Test
    fun `test onSelectOptionTrash success`() {
        // arrange
        every { router.execute(any()) } just runs
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.addAll(
            listOf(
                Pair("a", "aa"),
                Pair("b", "aa")
            )
        )
        // act
        viewModel.onClickSelectOption(true)

        // assert
        verify(exactly = 1) { router.execute(TrashToTrashBottomSheet, any()) }
    }

    @Test
    fun `test onTrashSortByClick success`() {
        // arrange
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.addAll(
            listOf(
                Pair("a", "aa"),
                Pair("b", "aa")
            )
        )
        coEvery {
            getTrashListUseCase.execute()
        } returns flow {}

        // act
        viewModel.onTrashSortByClick(SortType.SORT_DATE_DESC)

        // assertd
        coVerify(exactly = 1) {
            getTrashListUseCase.execute()
        }
    }

    @Test
    fun `test onEmptyTrash success`() {
        // arrange
        val status = 201
        val testObserver = TestObserver.test(viewModel.onShowSnackbarComplete)
        coEvery {
            emptyTrashDataUseCase.execute(any())
        } answers {
            flowOf(status)
        }

        val file = TrueCloudFilesModel.File(
            parentObjectId = "id",
            id = "1"
        )
        val folder = TrueCloudFilesModel.Folder(
            parentObjectId = "id",
            id = "1"
        )
        viewModel.items.add(file)
        viewModel.items.add(folder)
        // act
        viewModel.emptyTrash()

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test permanenceDelete success`() {
        // arrange
        val status = 201
        val testObserver = TestObserver.test(viewModel.onShowSnackbarComplete)
        coEvery {
            deleteTrashDataUseCase.execute(any())
        } answers {
            flowOf(status)
        }

        val file = TrueCloudFilesModel.File(
            parentObjectId = "id",
            id = "1"
        )
        // act
        viewModel.permanenceDelete(mutableListOf(file))

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test restoreFile success`() {
        // arrange
        val status = 201
        val testObserver = TestObserver.test(viewModel.onShowSnackbarComplete)
        coEvery {
            restoreTrashDataUseCase.execute(any())
        } answers {
            flowOf(status)
        }
        val folder = TrueCloudFilesModel.File(
            parentObjectId = "id",
            id = "1",
            objectType = "FOLDER"
        )
        val file = TrueCloudFilesModel.File(
            parentObjectId = "id",
            id = "1"
        )
        val data = mutableListOf(folder, file)
        // act
        viewModel.restoreFile(data)

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test onExpandBackupClicked success`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSetExpandBackupState)
        // act
        viewModel.onExpandBackupClicked(true)

        // assert
        testObserver.assertValue(false)
    }

    @Test
    fun `test onClickPauseAll success`() = runTest {
        // arrange
        coEvery {
            updateTaskUploadStatusUseCase.execute(
                any(),
                any()
            )
        } returns Unit
        coEvery {
            trueCloudV3TransferUtilityProvider.pauseTransferById(any(), any())
        } returns true
        val transfer: TrueCloudV3TransferObserver = mockk(relaxed = true)
        every {
            contextDataProviderWrapper.get().getDataContext()
        } returns context
        coEvery {
            trueCloudV3TransferUtilityProvider.getTrueCloudV3TransferObserverById(any(), any())
        } returns transfer
        viewModel.itemBackupList = mutableListOf(
            TrueCloudFilesModel.AutoBackup(
                id = 1,
                path = "abc",
                status = TaskStatusType.IN_PROGRESS,
                name = "xyz.jpg",
                size = "100",
                type = FileMimeType.IMAGE,
                updateAt = 0L,
                coverImageSize = 1
            )
        )
        // act
        viewModel.onClickPauseAllBackup()

        // assert
        coVerify(exactly = 1) {
            updateTaskUploadStatusUseCase.execute(
                any(),
                any()
            )
        }
    }

    @Test
    fun `test onClickCancelAllBackup success`() = runTest {
        // arrange
        coEvery {
            removeTaskUseCase.execute(any())
        } returns Unit
        coEvery {
            trueCloudV3TransferUtilityProvider.cancelTransferById(any(), any())
        } returns true
        coEvery {
            deleteFileUseCase.execute(any())
        } returns flowOf(TrueCloudV3Model())

        viewModel.itemBackupList = mutableListOf(
            TrueCloudFilesModel.AutoBackup(
                id = 1,
                path = "abc",
                status = TaskStatusType.IN_PROGRESS,
                name = "xyz.jpg",
                size = "100",
                type = FileMimeType.IMAGE,
                updateAt = 0L,
                coverImageSize = 1
            )
        )
        // act
        viewModel.onClickCancelAllBackup()

        // assert
        coVerify(exactly = 1) {
            removeTaskUseCase.execute(any())
        }
    }

    @Test
    fun `test onClickResumeAllBackup success`() = runTest {
        // arrange
        val trueCloudV3TransferObserver = mockk<TrueCloudV3TransferObserver>()
        val slotListener = slot<TrueCloudV3TransferObserver.TrueCloudV3TransferListener>()
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.PAUSE,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            actionType = TaskActionType.UPLOAD,
            updateAt = 0L
        )
        viewModel.itemBackupList = mutableListOf(
            TrueCloudFilesModel.AutoBackup(
                id = 1,
                path = "abc",
                status = TaskStatusType.PAUSE,
                name = "xyz.jpg",
                size = "100",
                type = FileMimeType.IMAGE,
                updateAt = 0L,
                coverImageSize = 1
            )
        )
        val transfer: TrueCloudV3TransferObserver = mockk(relaxed = true)
        every {
            contextDataProviderWrapper.get().getDataContext()
        } returns context
        coEvery {
            trueCloudV3TransferUtilityProvider.getTrueCloudV3TransferObserverById(any(), any())
        } returns transfer
        coEvery {
            updateTaskUploadStatusUseCase.execute(
                any(),
                any()
            )
        } returns Unit
        coEvery {
            trueCloudV3TransferUtilityProvider.getDefaultTransferUtility(
                contextDataProviderWrapper.get().getDataContext()
            )
                .resume(taskUploadModel.id)?.setTransferListener(any())
        } returns Unit
        coEvery {
            trueCloudV3TransferUtilityProvider.resumeTransferById(any(), any())
        } returns trueCloudV3TransferObserver
        every {
            trueCloudV3TransferObserver.setTransferListener(capture(slotListener))
        } answers {
            slotListener.captured.onStateChanged(0, TrueCloudV3TransferState.COMPLETED)
        }

        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.add(Pair("folderId", "cateName"))

        // act
        viewModel.onClickResumeAllBackup()

        // assert
        coVerify(exactly = 1) {
            updateTaskUploadStatusUseCase.execute(
                any(),
                any()
            )
        }
    }

    @Test
    fun `test search all files onSearchStringInput searching`() = runTest {
        val input = "test"
        coEvery {
            searchInAllFilesUseCase.execute(any(), any(), any())
        } returns flowOf(listOf())

        viewModel.onSearchStringInput(input)

        assertEquals(input, viewModel.searchString)
        assertEquals(0, viewModel.folderItemSize)
        assertEquals(listOf(), viewModel.itemList)
    }

    @Test
    fun `test search category onSearchStringInput searching`() = runTest {
        viewModel.categoryType = FileCategoryType.IMAGE
        val input = "test"
        coEvery {
            searchWithCategoryUseCase.execute(any(), any(), any(), any())
        } returns flowOf(listOf())

        viewModel.onSearchStringInput(input)

        assertEquals(input, viewModel.searchString)
        assertEquals(0, viewModel.folderItemSize)
        assertEquals(listOf(), viewModel.itemList)
    }

    @Test
    fun `test onSearchStringInput searching`() {
        val input = "test"
        viewModel.onSearchStringInput(input)

        assertEquals(input, viewModel.searchString)
    }

    @Test
    fun `test search all files onSearchStringInput not searching empty string`() = runTest {
        val input = ""
        viewModel.onSearchStringInput(input)

        verify(exactly = 1) { getStorageListUseCase.execute(any(), any(), any()) }
    }

    @Test
    fun `test search category onSearchStringInput not searching empty string`() = runTest {
        val input = ""
        viewModel.categoryType = FileCategoryType.IMAGE
        viewModel.onSearchStringInput(input)

        verify(exactly = 1) { getStorageListWithCategoryUseCase.execute(any(), any(), any()) }
    }

    @Test
    fun `test on reload require`() = runTest {
        FilesTrueCloudViewModel.Companion.stackFolderIds.clear()
        FilesTrueCloudViewModel.Companion.stackFolderIds.addAll(
            listOf(
                Pair("a", "aa"),
                Pair("b", "aa")
            )
        )
        viewModel.onRequireReload(true)
        verify { getStorageListUseCase.execute(any(), any(), any()) }
    }
}
