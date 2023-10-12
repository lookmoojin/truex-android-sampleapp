package com.truedigital.features.truecloudv3.presentation.viewmodel

import android.content.Context
import android.os.Environment
import androidx.work.ListenableWorker
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.workDataOf
import com.jraska.livedata.TestObserver
import com.truedigital.common.share.analytics.measurement.AnalyticManagerInterface
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.common.share.datalegacy.wrapper.ContextDataProviderWrapper
import com.truedigital.core.coroutines.TestCoroutineDispatcherProvider
import com.truedigital.features.truecloudv3.common.FileCategoryType
import com.truedigital.features.truecloudv3.common.SortType
import com.truedigital.features.truecloudv3.common.StorageType
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3DownloadInterface
import com.truedigital.features.truecloudv3.data.model.InitialDataResponse
import com.truedigital.features.truecloudv3.data.model.InitialDownloadResponse
import com.truedigital.features.truecloudv3.data.model.SecureTokenServiceDataResponse
import com.truedigital.features.truecloudv3.data.worker.TrueCloudV3DownloadWorker
import com.truedigital.features.truecloudv3.data.worker.TrueCloudV3DownloadWorker.Companion.KEY
import com.truedigital.features.truecloudv3.data.worker.TrueCloudV3DownloadWorker.Companion.PATH
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3Model
import com.truedigital.features.truecloudv3.domain.usecase.DeleteFileUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetStorageListUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetStorageListWithCategoryUseCase
import com.truedigital.features.truecloudv3.domain.usecase.MoveToTrashUseCase
import com.truedigital.features.truecloudv3.domain.usecase.ProvideWorkManagerUseCase
import com.truedigital.features.truecloudv3.domain.usecase.RenameFileUseCase
import com.truedigital.features.truecloudv3.extension.convertToFilesModel
import com.truedigital.features.truecloudv3.navigation.FileViewerToOptionFileBottomSheet
import com.truedigital.features.truecloudv3.navigation.router.TrueCloudV3FileViewerRouterUseCase
import com.truedigital.features.truecloudv3.provider.SecureTokenServiceProvider
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.coroutines.TestCoroutinesExtension
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import retrofit2.Response
import java.io.File

@ExtendWith(InstantTaskExecutorExtension::class)
class FileViewerViewModelTest {

    @ExperimentalCoroutinesApi
    @RegisterExtension
    @JvmField
    val testCoroutine = TestCoroutinesExtension()
    private val coroutineDispatcher = TestCoroutineDispatcherProvider(testCoroutine.dispatcher)
    private val provideWorkManagerUseCase: ProvideWorkManagerUseCase = mockk()
    private val deleteFileUseCase: DeleteFileUseCase = mockk()
    private val renameFileUseCase: RenameFileUseCase = mockk()
    private val moveToTrashUseCase: MoveToTrashUseCase = mockk()
    private val getStorageListUseCase: GetStorageListUseCase = mockk()
    private val getStorageListWithCategoryUseCase: GetStorageListWithCategoryUseCase = mockk()
    private val contextDataProviderWrapper: ContextDataProviderWrapper = mockk()
    private val analyticManagerInterface: AnalyticManagerInterface = mockk()
    private val trueCloudV3DownloadInterface = mockk<TrueCloudV3DownloadInterface>()
    private val userRepository = mockk<UserRepository>()
    private val sTSProvider = mockk<SecureTokenServiceProvider>()
    private val router: TrueCloudV3FileViewerRouterUseCase = mockk()
    private val workManager = mockk<WorkManager>(relaxed = true)
    private val context = mockk<Context>()
    private val mockFile = mockk<File>()
    private lateinit var viewModel: FileViewerViewModel

    @BeforeEach
    fun setUp() {
        viewModel = FileViewerViewModel(
            router = router,
            coroutineDispatcher = coroutineDispatcher,
            getStorageListUseCase = getStorageListUseCase,
            getStorageListWithCategoryUseCase = getStorageListWithCategoryUseCase,
            workManager = workManager,
            renameObjectUseCase = renameFileUseCase,
            contextDataProviderWrapper = contextDataProviderWrapper,
            analyticManagerInterface = analyticManagerInterface,
            moveToTrashUseCase = moveToTrashUseCase
        )

        every {
            context.cacheDir
        } returns mockFile

        every {
            mockFile.absolutePath
        } returns "/cacheDir/absolutePath"

        val trueCloudV3FilesModel = TrueCloudV3Model(
            id = "testid",
        ).convertToFilesModel(context)

        val testObserver = TestObserver.test(viewModel.onSetCurrentPosition)
        // act
        viewModel.onViewCreated(
            folderId = "111",
            categoryType = FileCategoryType.IMAGE.type,
            sortType = SortType.SORT_DATE_DESC,
            selectedFilesModel = trueCloudV3FilesModel,
            filesListModel = mutableListOf(trueCloudV3FilesModel),
            page = 1
        )

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test imageview onBackPressed success`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onBackPressed)

        // act
        viewModel.onClickBack()

        // assert
        testObserver.assertHasValue()
        testObserver.assertValue(true)
    }

    @Test
    fun `test file view onMoreClick success`() {
        // arrange
        every { router.execute(destination = any(), bundle = any()) } just runs

        // act
        viewModel.onClickMore()

        // assert
        verify(exactly = 1) {
            router.execute(
                destination = FileViewerToOptionFileBottomSheet,
                bundle = any()
            )
        }
    }

    @Test
    fun `test file view onPageScrolled success`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSetTitle)

        // act
        viewModel.onPageScrolled(0)

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test file view onPageScrolled success and loadFileList WithCategory`() = runTest {
        // arrange
        val file1 = TrueCloudV3Model(
            id = "1",
            objectType = StorageType.FILE.type
        )
        val file2 = TrueCloudV3Model(
            id = "2",
            objectType = StorageType.FILE.type
        )
        val file3 = TrueCloudV3Model(
            id = "3"
        )
        val file4 = TrueCloudV3Model(
            id = "4",
            objectType = StorageType.FILE.type
        )
        val file5 = TrueCloudV3Model(
            id = "5",
            objectType = StorageType.FILE.type
        )
        every {
            contextDataProviderWrapper.get().getDataContext().cacheDir.absolutePath
        } returns "/cacheDir/absolutePath"
        every {
            getStorageListWithCategoryUseCase.execute(any(), any(), any())
        } returns flowOf(listOf(file1, file2, file3, file4, file5))

        // act
        viewModel.onViewCreated(
            folderId = "111",
            categoryType = FileCategoryType.IMAGE.type,
            sortType = SortType.SORT_DATE_DESC,
            selectedFilesModel = file1.convertToFilesModel(context),
            filesListModel = mutableListOf(
                file1.convertToFilesModel(context),
                file2.convertToFilesModel(context),
                file3.convertToFilesModel(context)
            ),
            page = 1
        )
        viewModel.onPageScrolled(1)

        // assert
        coVerify(exactly = 1) { getStorageListWithCategoryUseCase.execute(any(), any(), any()) }
    }
    @Test
    fun `test file view onPageScrolled success and loadFileList`() = runTest {
        // arrange
        val file1 = TrueCloudV3Model(
            id = "1",
            objectType = StorageType.FILE.type
        )
        val file2 = TrueCloudV3Model(
            id = "2",
            objectType = StorageType.FILE.type
        )
        val file3 = TrueCloudV3Model(
            id = "3"
        )
        val file4 = TrueCloudV3Model(
            id = "4",
            objectType = StorageType.FILE.type
        )
        val file5 = TrueCloudV3Model(
            id = "5",
            objectType = StorageType.FILE.type
        )
        every {
            contextDataProviderWrapper.get().getDataContext().cacheDir.absolutePath
        } returns "/cacheDir/absolutePath"
        every {
            getStorageListUseCase.execute(any(), any(), any())
        } returns flowOf(listOf(file1, file2, file3, file4, file5))

        // act
        viewModel.onViewCreated(
            folderId = "111",
            categoryType = FileCategoryType.UNSUPPORTED_FORMAT.type,
            sortType = SortType.SORT_DATE_DESC,
            selectedFilesModel = file1.convertToFilesModel(context),
            filesListModel = mutableListOf(
                file1.convertToFilesModel(context),
                file2.convertToFilesModel(context),
                file3.convertToFilesModel(context)
            ),
            page = 1
        )
        viewModel.onPageScrolled(1)

        // assert
        coVerify(exactly = 1) { getStorageListUseCase.execute(any(), any(), any()) }
    }

    @Test
    fun `test imageview rename success`() = runTest {
        // arrange
        val trueCloudFilesModel = TrueCloudFilesModel.File(
            id = "id",
            name = "test name",
            parentObjectId = "parentObjectId",
        )
        val file = TrueCloudV3Model(
            id = "id",
            name = "test name",
            parentObjectId = "parentObjectId",
        )
        every {
            contextDataProviderWrapper.get().getDataContext().cacheDir.absolutePath
        } returns "/cacheDir/absolutePath"
        every {
            renameFileUseCase.execute(any(), any())
        } returns flowOf(file)

        // act
        viewModel.rename(trueCloudFilesModel)

        // assert
        coVerify(exactly = 1) { renameFileUseCase.execute(any(), any()) }
    }

    @Test
    fun `test imageview rename null`() = runTest {
        // arrange
        val trueCloudFilesModel = TrueCloudFilesModel.File(
            id = "id",
            name = "test name",
            parentObjectId = "parentObjectId",
        )
        val file = TrueCloudV3Model(
            id = "id",
            name = null,
            parentObjectId = "parentObjectId",
        )
        every {
            contextDataProviderWrapper.get().getDataContext().cacheDir.absolutePath
        } returns "/cacheDir/absolutePath"
        every {
            renameFileUseCase.execute(any(), any())
        } returns flowOf(file)

        // act
        viewModel.rename(trueCloudFilesModel)

        // assert
        coVerify(exactly = 1) { renameFileUseCase.execute(any(), any()) }
    }

    @Test
    fun `test imageview onDownloadFile success`() = runTest {
        // arrange
        val file = TrueCloudFilesModel.File(
            id = "id",
            parentObjectId = "parentObjectId",
        )
        mockkStatic(Environment::class)
        every { Environment.getExternalStoragePublicDirectory(any()).path } returns "/storage/emulated/0"
        every { provideWorkManagerUseCase.execute() } returns workManager
        every { workManager.enqueue(any<WorkRequest>()) } returns mockk()
        justRun { analyticManagerInterface.trackEvent(any()) }

        // act
        viewModel.onDownloadFile(file)

        // assert
        coVerify(exactly = 1) { workManager.enqueue(any<WorkRequest>()) }
    }

    @Test
    fun `test imageview onDeleteFile success`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onDeleted)
        val trueCloudV3FilesModel = TrueCloudV3Model(
            id = "testid",
        ).convertToFilesModel(context)
        val trueCloudV3Status = 200
        every {
            moveToTrashUseCase.execute(any())
        } returns flowOf(trueCloudV3Status)
        justRun { analyticManagerInterface.trackEvent(any()) }
        // act
        viewModel.onDeleteFile(trueCloudV3FilesModel)

        // assert
        coVerify(exactly = 1) { moveToTrashUseCase.execute(any()) }
        testObserver.assertHasValue()
    }

    @Test
    @Disabled
    fun `test imageview download success`() = runTest {
        // arrange
        val trueCloudV3Model = TrueCloudV3Model(
            id = "id",
            name = "name",
        )
        val dataResponse = SecureTokenServiceDataResponse(
            accessToken = "accessToken",
            secretKey = "secretKey",
            sessionKey = "sessionKey",
            endpoint = "endpoint",
            expiresAt = "2022-04-07T08:19:27.019Z",
        )
        mockkStatic(Environment::class)
        every { Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path } returns "/storage/emulated/0"
        every {
            renameFileUseCase.execute(any(), any())
        } returns flowOf(trueCloudV3Model)
        every {
            sTSProvider.getSTS()
        } returns flowOf(dataResponse)
        every {
            context.startService(any())
        } returns mockk()
        every {
            userRepository.getSsoId()
        } returns "ssoid"
        every {
            contextDataProviderWrapper.get().getDataContext()
        } returns context
        val data = InitialDataResponse()
        val initDownloadRes = InitialDownloadResponse(
            data = data,
        )
        coEvery {
            trueCloudV3DownloadInterface.initialDownload(
                any(),
                any(),
            )
        } returns Response.success(initDownloadRes)

        val worker =
            TestListenableWorkerBuilder<TrueCloudV3DownloadWorker>(context).setWorkerFactory(
                TrueCloudV3DownloadWorkerFactory(),
            ).setInputData(
                workDataOf(
                    KEY to "key",
                    PATH to "path",
                ),
            ).build()
        // act
        val result = worker.doWork()

        // assert
        coVerify(exactly = 1) {
            trueCloudV3DownloadInterface.initialDownload(
                any(),
                any(),
            )
        }
    }

    class TrueCloudV3DownloadWorkerFactory : WorkerFactory() {
        override fun createWorker(
            appContext: Context,
            workerClassName: String,
            workerParameters: WorkerParameters,
        ): ListenableWorker {
            return TrueCloudV3DownloadWorker(
                appContext,
                workerParameters
            )
        }
    }
}
