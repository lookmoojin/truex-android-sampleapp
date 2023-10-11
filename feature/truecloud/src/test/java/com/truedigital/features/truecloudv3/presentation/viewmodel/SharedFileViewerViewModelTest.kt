package com.truedigital.features.truecloudv3.presentation.viewmodel

import android.content.Context
import android.net.Uri
import androidx.navigation.NavController
import com.jraska.livedata.TestObserver
import com.truedigital.common.share.datalegacy.login.LoginManagerInterface
import com.truedigital.common.share.datalegacy.wrapper.AuthLoginListener
import com.truedigital.common.share.datalegacy.wrapper.ContextDataProviderWrapper
import com.truedigital.core.coroutines.TestCoroutineDispatcherProvider
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.common.TaskStatusType
import com.truedigital.features.truecloudv3.common.TrueCloudV3TransferState
import com.truedigital.features.truecloudv3.data.model.DataMigration
import com.truedigital.features.truecloudv3.data.model.DataStorage
import com.truedigital.features.truecloudv3.data.model.DataUsage
import com.truedigital.features.truecloudv3.data.model.TrueCloudV3StorageData
import com.truedigital.features.truecloudv3.domain.model.DataMigrationModel
import com.truedigital.features.truecloudv3.domain.model.DataStorageModel
import com.truedigital.features.truecloudv3.domain.model.DataUsageModel
import com.truedigital.features.truecloudv3.domain.model.SharedFileModel
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3Model
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3TransferObserver
import com.truedigital.features.truecloudv3.domain.usecase.CompleteUploadUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetPrivateSharedFileUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetPublicSharedFileUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetSharedFileAccessTokenUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetStorageSpaceUseCase
import com.truedigital.features.truecloudv3.domain.usecase.RemoveTaskUseCase
import com.truedigital.features.truecloudv3.domain.usecase.UpdateTaskUploadStatusUseCase
import com.truedigital.features.truecloudv3.domain.usecase.UploadFileUseCase
import com.truedigital.features.truecloudv3.navigation.SharedFileToBottomSheet
import com.truedigital.features.truecloudv3.navigation.SharedFileViewerToMain
import com.truedigital.features.truecloudv3.navigation.router.TrueCloudV3SharedFileViewerRouterUseCase
import com.truedigital.navigation.domain.usecase.SetRouterToNavControllerUseCase
import com.truedigital.navigation.usecase.GetNavigationControllerRepository
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.coroutines.TestCoroutinesExtension
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(InstantTaskExecutorExtension::class)
class SharedFileViewerViewModelTest {
    @ExperimentalCoroutinesApi
    @RegisterExtension
    @JvmField
    val testCoroutine = TestCoroutinesExtension()
    private lateinit var viewModel: SharedFileViewerViewModel
    private val router: TrueCloudV3SharedFileViewerRouterUseCase = mockk(relaxed = true)
    private val trueCloudLoginManagerInterface: LoginManagerInterface = mockk(relaxed = true)
    private val getStorageSpaceUseCase: GetStorageSpaceUseCase = mockk(relaxed = true)
    private val getPublicSharedFileUseCase: GetPublicSharedFileUseCase = mockk(relaxed = true)
    private val getPrivateSharedFileUseCase: GetPrivateSharedFileUseCase = mockk(relaxed = true)
    private val getSharedFileAccessTokenUseCase: GetSharedFileAccessTokenUseCase =
        mockk(relaxed = true)
    private val setRouterToNavControllerUseCase: SetRouterToNavControllerUseCase =
        mockk(relaxed = true)
    private val completeUploadUseCase: CompleteUploadUseCase = mockk(relaxed = true)
    private val removeTaskUseCase: RemoveTaskUseCase = mockk(relaxed = true)
    private val updateTaskUploadStatusUseCase: UpdateTaskUploadStatusUseCase = mockk(relaxed = true)
    private val coroutineDispatcher = TestCoroutineDispatcherProvider(testCoroutine.dispatcher)
    private val contextDataProviderWrapper: ContextDataProviderWrapper = mockk(relaxed = true)
    private val uploadFileUseCase: UploadFileUseCase = mockk(relaxed = true)
    private val getNavigationControllerRepository: GetNavigationControllerRepository =
        mockk(relaxed = true)
    private val context = mockk<Context>()
    private val uri: Uri = mockk()
    private val obj = mockkObject(SharedFileViewerViewModel.Companion)

    @BeforeEach
    fun setUp() {
        viewModel = SharedFileViewerViewModel(
            router = router,
            completeUploadUseCase = completeUploadUseCase,
            getPublicSharedFileUseCase = getPublicSharedFileUseCase,
            getPrivateSharedFileUseCase = getPrivateSharedFileUseCase,
            getSharedFileAccessTokenUseCase = getSharedFileAccessTokenUseCase,
            contextDataProviderWrapper = contextDataProviderWrapper,
            coroutineDispatcher = coroutineDispatcher,
            getStorageSpaceUseCase = getStorageSpaceUseCase,
            removeTaskUseCase = removeTaskUseCase,
            setRouterToNavControllerUseCase = setRouterToNavControllerUseCase,
            trueCloudLoginManagerInterface = trueCloudLoginManagerInterface,
            updateTaskUploadStatusUseCase = updateTaskUploadStatusUseCase,
            uploadFileUseCase = uploadFileUseCase,
        )
    }

    @Test
    fun `test setNavController`() = runTest {
        val navController = NavController(context)
        viewModel.setRouterToNavController(navController)
        assertNotNull(getNavigationControllerRepository.getRouterToNavController())
    }

    @Test
    fun `test onClickBack success`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onBackPressed)

        // act
        viewModel.onClickBack()

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test onMoreClick success`() {
        // arrange
        every { router.execute(destination = any(), bundle = any()) } just runs

        // act
        viewModel.onClickMore()

        // assert
        verify(exactly = 1) {
            router.execute(
                destination = SharedFileToBottomSheet,
                bundle = any()
            )
        }
    }

    @Test
    fun `test checkResponseStatus response unauthorized`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onRequirePassword)
        val sharedFileModel = SharedFileModel(
            status = SharedFileViewerViewModel.Companion.UNAUTHORIZED_STATUS_CODE
        )
        // act
        viewModel.checkResponseStatus(sharedFileModel)

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test checkResponseStatus response forbidden`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onLinkExpired)
        val sharedFileModel = SharedFileModel(
            status = SharedFileViewerViewModel.Companion.FORBIDDEN_STATUS_CODE
        )
        // act
        viewModel.checkResponseStatus(sharedFileModel)

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test checkResponseStatus response ok`() {
        // arrange
        val sharedObjectObserver = TestObserver.test(viewModel.sharedObject)
        val onSuccessObserver = TestObserver.test(viewModel.onSuccess)
        val sharedFileModel = SharedFileModel(
            status = SharedFileViewerViewModel.Companion.OK_STATUS_CODE
        )
        // act
        viewModel.checkResponseStatus(sharedFileModel)

        // assert
        sharedObjectObserver.assertValue(sharedFileModel)
        onSuccessObserver.assertHasValue()
    }

    @Test
    fun `test checkResponseStatus response others`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onLoadDataError)
        val sharedFileModel = SharedFileModel(
            status = 999,
            statusMessage = "error"
        )
        val expectedValue = Pair(
            "error",
            SharedFileViewerViewModel.ACTION_GET_SHARED_FILE
        )
        // act
        viewModel.checkResponseStatus(sharedFileModel)

        // assert
        testObserver.assertValue(expectedValue)
    }

    @Test
    fun `test submitPassword password is null`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onShowSnackBarError)
        // act
        viewModel.submitPassword(null)
        // assert
        testObserver.assertValue(
            contextDataProviderWrapper.get()
                .getString(R.string.true_cloudv3_view_private_file_wrong_password)
        )
    }

    @Test
    fun `test submitPassword password is empty`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onShowSnackBarError)
        // act
        viewModel.submitPassword("")
        // assert
        testObserver.assertValue(
            contextDataProviderWrapper.get()
                .getString(R.string.true_cloudv3_view_private_file_wrong_password)
        )
    }

    @Test
    fun `test submitPassword accessToken is null`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onShowSnackBarError)

        // act
        every {
            getSharedFileAccessTokenUseCase.execute(any(), any())
        } returns flowOf(null)
        viewModel.submitPassword("password")
        // assert
        testObserver.assertValue(
            contextDataProviderWrapper.get()
                .getString(R.string.true_cloudv3_view_private_file_wrong_password)
        )
    }

    @Test
    fun `test submitPassword accessToken is not null`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.sharedObjectAccessToken)
        every {
            getSharedFileAccessTokenUseCase.execute(any(), any())
        } returns flowOf("token")

        // act
        viewModel.submitPassword("password")
        // assert
        testObserver.assertValue("token")
    }

    @Test
    fun `test loadSharedObjectData unauthorized`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.linkStatus)
        val sharedFileModel = SharedFileModel(
            status = SharedFileViewerViewModel.Companion.UNAUTHORIZED_STATUS_CODE
        )
        every {
            getPrivateSharedFileUseCase.execute(any(), any())
        } returns flowOf(sharedFileModel)
        // act
        viewModel.loadSharedObjectData("token")
        // assert
        testObserver.assertValue(SharedFileViewerViewModel.Companion.UNAUTHORIZED_STATUS_CODE)
    }

    @Test
    fun `test loadSharedObjectData forbidden`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.linkStatus)
        val sharedFileModel = SharedFileModel(
            status = SharedFileViewerViewModel.Companion.FORBIDDEN_STATUS_CODE
        )
        every {
            getPrivateSharedFileUseCase.execute(any(), any())
        } returns flowOf(sharedFileModel)
        // act
        viewModel.loadSharedObjectData("token")
        // assert
        testObserver.assertValue(SharedFileViewerViewModel.Companion.FORBIDDEN_STATUS_CODE)
    }

    @Test
    fun `test loadSharedObjectData ok`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.linkStatus)
        val sharedFileModel = SharedFileModel(
            status = SharedFileViewerViewModel.Companion.OK_STATUS_CODE
        )
        coEvery {
            getPrivateSharedFileUseCase.execute(any(), any())
        } returns flowOf(sharedFileModel)
        // act
        viewModel.loadSharedObjectData("token")
        // assert
        testObserver.assertValue(SharedFileViewerViewModel.Companion.OK_STATUS_CODE)
    }

    @Test
    fun `test loadSharedObjectData others`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.linkStatus)
        val sharedFileModel = SharedFileModel(
            status = SharedFileViewerViewModel.Companion.ERROR_CODE,
        )
        coEvery {
            getPrivateSharedFileUseCase.execute(any(), any())
        } returns flowOf(sharedFileModel)
        // act
        viewModel.loadSharedObjectData("token")
        // assert
        testObserver.assertValue(SharedFileViewerViewModel.Companion.ERROR_CODE)
    }

    @Test
    fun `test validateObjectAccess unauthorized`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.linkStatus)
        val sharedFileModel = SharedFileModel(
            status = SharedFileViewerViewModel.Companion.UNAUTHORIZED_STATUS_CODE
        )
        coEvery {
            getPublicSharedFileUseCase.execute(any())
        } returns flowOf(sharedFileModel)

        // act
        viewModel.validateObjectAccess()
        // assert
        testObserver.assertValue(SharedFileViewerViewModel.Companion.UNAUTHORIZED_STATUS_CODE)
    }

    @Test
    fun `test validateObjectAccess forbidden`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.linkStatus)
        val sharedFileModel = SharedFileModel(
            status = SharedFileViewerViewModel.Companion.FORBIDDEN_STATUS_CODE
        )
        coEvery {
            getPublicSharedFileUseCase.execute(any())
        } returns flowOf(sharedFileModel)
        // act
        viewModel.validateObjectAccess()
        // assert
        testObserver.assertValue(SharedFileViewerViewModel.Companion.FORBIDDEN_STATUS_CODE)
    }

    @Test
    fun `test validateObjectAccess ok`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.linkStatus)
        val sharedFileModel = SharedFileModel(
            status = SharedFileViewerViewModel.Companion.OK_STATUS_CODE
        )
        coEvery {
            getPublicSharedFileUseCase.execute(any())
        } returns flowOf(sharedFileModel)
        // act
        viewModel.validateObjectAccess()
        // assert
        testObserver.assertValue(SharedFileViewerViewModel.Companion.OK_STATUS_CODE)
    }

    @Test
    fun `test validateObjectAccess others`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.linkStatus)
        val sharedFileModel = SharedFileModel(
            status = SharedFileViewerViewModel.Companion.ERROR_CODE,
        )
        coEvery {
            getPublicSharedFileUseCase.execute(any())
        } returns flowOf(sharedFileModel)
        // act
        viewModel.validateObjectAccess()
        // assert
        testObserver.assertValue(SharedFileViewerViewModel.Companion.ERROR_CODE)
    }

    @Test
    fun `test sharedObjectId`() {
        viewModel.setSharedObjectId("id")
        assertEquals("id", viewModel.sharedObjId)
    }

    @Test
    fun `test CheckRetryState`() {
        // arrange
        coEvery {
            getPublicSharedFileUseCase.execute(any())
        } returns flowOf()
        // act
        viewModel.checkRetryState(SharedFileViewerViewModel.Companion.ACTION_GET_STORAGE)
        // assert
        coVerify(exactly = 1) {
            getPublicSharedFileUseCase.execute(any())
        }
    }

    @Test
    fun `test checkAuthenticationState isLogin`() {
        // arrange
        val dataUsage = DataUsageModel(
            images = 0L,
            videos = 0L,
            audio = 0L,
            others = 0L,
            contacts = 0L,
            total = 0L,
        )

        val migration = DataMigrationModel(
            status = "Migrated",
        )
        val trueCloudV3Object = TrueCloudV3Model()

        val response = DataStorageModel(
            quota = 500L,
            rootFolder = trueCloudV3Object,
            dataUsage = dataUsage,
            migration = migration,
        )

        coEvery { getStorageSpaceUseCase.execute() } returns flowOf(response)
        coEvery {
            trueCloudLoginManagerInterface.isLoggedIn()
        } returns true

        // act
        viewModel.checkAuthenticationState()

        // assert
        verify(exactly = 1) { getStorageSpaceUseCase.execute() }
    }

    @Test
    fun `test checkAuthenticationState isLogin false`() {
        // arrange
        val dataUsage = DataUsageModel(
            images = 0L,
            videos = 0L,
            audio = 0L,
            others = 0L,
            contacts = 0L,
            total = 0L,
        )

        val migration = DataMigrationModel(
            status = "Migrated",
        )
        val trueCloudV3Object = TrueCloudV3Model()

        val response = DataStorageModel(
            quota = 500L,
            rootFolder = trueCloudV3Object,
            dataUsage = dataUsage,
            migration = migration,
        )

        coEvery { getStorageSpaceUseCase.execute() } returns flowOf(response)
        val slotListener = slot<AuthLoginListener>()
        every { trueCloudLoginManagerInterface.isLoggedIn() } returns false
        every {
            trueCloudLoginManagerInterface.login(
                capture(slotListener),
                any(),
                any()
            )
        } just runs

        // act
        viewModel.checkAuthenticationState()

        // assert
        slotListener.captured.onLoginSuccess()
        verify(exactly = 1) { trueCloudLoginManagerInterface.login(any(), any(), any()) }
    }

    @Test
    fun `test GetStorage success`() {
        // arrange
        val context = mockk<Context>()
        val migrationModel = DataMigrationModel(status = "migrating")
        migrationModel.status = "migrating"
        migrationModel.estimatedTimeToComplete = "estimatedTimeToComplete"
        migrationModel.failedDisplayTime = 0
        val dataUsageModel = DataUsageModel(
            images = 0L,
            videos = 0L,
            audio = 0L,
            others = 0L,
            contacts = 0L,
            total = 1000L,
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

        val migration = DataMigration(status = "migrating")
        migration.status = "migrating"
        migration.estimatedTimeToComplete = "estimatedTimeToComplete"
        migration.failedDisplayTime = 0
        val dataUsage = DataUsage(
            images = 0L,
            videos = 0L,
            audio = 0L,
            others = 0L,
            contacts = 0L,
            total = 0L,
            status = "",
            usagePercent = 0f,
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
        val dataStorage = DataStorage(
            quota = 100,
            rootFolder = trueCloudV3StorageData,
            dataUsage = dataUsage,
            migration = migration,
        )
        dataStorage.quota = 100
        dataStorage.rootFolder = trueCloudV3StorageData
        dataStorage.dataUsage = dataUsage
        dataStorage.migration = migration

        val mockResponse = DataStorageModel(
            quota = 100,
            rootFolder = trueCloudV3Model,
            dataUsage = dataUsageModel,
            migration = migrationModel,
        )
        every {
            getStorageSpaceUseCase.execute()
        } returns flowOf(mockResponse)
        every {
            contextDataProviderWrapper.get().getDataContext()
        } returns context

        // act
        viewModel.getStorage()
        // assert
        assertNotNull(viewModel.rootFolderId)
    }

    @Test
    fun `test completeUpload`() {
        val testObserver = TestObserver.test(viewModel.onShowSnackbarComplete)
        coEvery {
            completeUploadUseCase.execute(1)
        } returns flowOf(TrueCloudV3Model())
        viewModel.completeUpload(1)

        testObserver.assertHasValue()
    }

    @Test
    fun `test uploadWithTransferUtility complete success`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onShowSnackbarComplete)
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
        val transferObserver = mockk<TrueCloudV3TransferObserver>(relaxed = true)

        coEvery { uploadFileUseCase.execute(any(), any()) } coAnswers {
            flowOf(transferObserver)
        }
        coEvery { completeUploadUseCase.execute(any()) } coAnswers {
            flowOf(trueCloudV3Model)
        }
        coEvery { removeTaskUseCase.execute(any()) } returns Unit

        every { transferObserver.getState() } returns TrueCloudV3TransferState.COMPLETED
        every {
            contextDataProviderWrapper.get()
                .getString(R.string.true_cloudv3_migration_toast_success)
        } returns "true_cloudv3_migration_toast_success"

        viewModel.rootFolderId = "rootFolderId_test"
        // act
        viewModel.uploadFile(uri)

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test upload complete success`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onShowSnackbarComplete)
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
        val transferObserver = mockk<TrueCloudV3TransferObserver>(relaxed = true)

        coEvery { uploadFileUseCase.execute(any(), any()) } coAnswers {
            flowOf(transferObserver)
        }
        coEvery { completeUploadUseCase.execute(any()) } coAnswers {
            flowOf(trueCloudV3Model)
        }
        coEvery { removeTaskUseCase.execute(any()) } returns Unit

        every { transferObserver.getState() } returns TrueCloudV3TransferState.COMPLETED
        every {
            contextDataProviderWrapper.get()
                .getString(R.string.true_cloudv3_migration_toast_success)
        } returns "true_cloudv3_migration_toast_success"

        viewModel.rootFolderId = "rootFolderId_test"
        // act
        viewModel.uploadFile(uri)

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test TransferlistenerServiceOnStateChangedCanceled`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onShowSnackbarComplete)
        coEvery { removeTaskUseCase.execute(any()) } returns Unit
        // act
        viewModel.transferlistener.onStateChanged(1, TrueCloudV3TransferState.CANCELED)

        // assert
        coVerify(exactly = 1) {
            removeTaskUseCase.execute(any())
        }
        testObserver.assertNoValue()
    }

    @Test
    fun `test TransferlistenerServiceOnStateChangedPause`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onShowSnackbarComplete)
        coEvery {
            updateTaskUploadStatusUseCase.execute(
                any(),
                TaskStatusType.PAUSE,
            )
        } returns Unit
        // act
        viewModel.transferlistener.onStateChanged(1, TrueCloudV3TransferState.PAUSED)

        // assert
        coVerify(exactly = 1) {
            updateTaskUploadStatusUseCase.execute(
                any(),
                TaskStatusType.PAUSE,
            )
        }
        testObserver.assertNoValue()
    }

    @Test
    fun `test TransferlistenerServiceOnStateChangedResumeWaiting`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onShowSnackbarComplete)
        coEvery {
            updateTaskUploadStatusUseCase.execute(
                any(),
                TaskStatusType.WAITING,
            )
        } returns Unit
        // act
        viewModel.transferlistener.onStateChanged(1, TrueCloudV3TransferState.RESUMED_WAITING)
        // assert
        coVerify(exactly = 1) {
            updateTaskUploadStatusUseCase.execute(
                any(),
                TaskStatusType.WAITING,
            )
        }
        testObserver.assertNoValue()
    }

    @Test
    fun `test TransferlistenerServiceOnStateChangedOther`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onShowSnackbarComplete)
        coEvery {
            updateTaskUploadStatusUseCase.execute(
                any(),
                TaskStatusType.UNKNOWN,
            )
        } returns Unit
        // act
        viewModel.transferlistener.onStateChanged(1, TrueCloudV3TransferState.UNKNOWN)
        // assert
        coVerify(exactly = 1) {
            updateTaskUploadStatusUseCase.execute(
                any(),
                TaskStatusType.UNKNOWN,
            )
        }
        testObserver.assertNoValue()
    }

    @Test
    fun `test TransferlistenerServiceOnStateChangedFailed`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onShowSnackbarComplete)
        coEvery {
            updateTaskUploadStatusUseCase.execute(
                any(),
                TaskStatusType.FAILED,
            )
        } returns Unit
        // act
        viewModel.transferlistener.onStateChanged(1, TrueCloudV3TransferState.FAILED)
        // assert
        coVerify(exactly = 1) {
            updateTaskUploadStatusUseCase.execute(
                any(),
                TaskStatusType.FAILED,
            )
        }
        testObserver.assertNoValue()
    }

    @Test
    fun `test TransferlistenerServiceOnError`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onShowSnackbarComplete)
        coEvery {
            updateTaskUploadStatusUseCase.execute(
                any(),
                TaskStatusType.FAILED,
            )
        } returns Unit
        // act
        viewModel.transferlistener.onError(1, Exception())
        // assert
        coVerify(exactly = 1) {
            updateTaskUploadStatusUseCase.execute(
                any(),
                TaskStatusType.FAILED,
            )
        }
        testObserver.assertNoValue()
    }

    @Test
    fun `test TransferlistenerServiceOnProgressChange`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onShowSnackbarComplete)
        // act
        viewModel.transferlistener.onProgressChanged(1, 45, 100)
        // assert
        coVerify(exactly = 0) {
            updateTaskUploadStatusUseCase.execute(
                any(),
                any(),
            )
        }
        testObserver.assertNoValue()
    }

    @Test
    fun `test saveFileToCloud from public share`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSaveFileToCloud)
        val pairData = Pair("id", null)
        coEvery {
            trueCloudLoginManagerInterface.isLoggedIn()
        } returns true
        coEvery {
            getPublicSharedFileUseCase.execute("id")
        } returns flowOf(SharedFileModel())
        coEvery {
            getPrivateSharedFileUseCase.execute("id", "password")
        } returns flowOf(SharedFileModel())
        // act
        viewModel.saveFileToCloud(pairData)
        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test saveFileToCloud from private share`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSaveFileToCloud)
        val pairData = Pair("id", "password")
        coEvery {
            trueCloudLoginManagerInterface.isLoggedIn()
        } returns true
        coEvery {
            getPublicSharedFileUseCase.execute("id")
        } returns flowOf(SharedFileModel())
        coEvery {
            getPrivateSharedFileUseCase.execute("id", "password")
        } returns flowOf(SharedFileModel())
        // act
        viewModel.saveFileToCloud(pairData)
        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test saveFileToDevice from public share`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSaveFileToDevice)
        val pairData = Pair("id", null)
        coEvery {
            trueCloudLoginManagerInterface.isLoggedIn()
        } returns true
        coEvery {
            getPublicSharedFileUseCase.execute("id")
        } returns flowOf(SharedFileModel())
        coEvery {
            getPrivateSharedFileUseCase.execute("id", "password")
        } returns flowOf(SharedFileModel())
        // act
        viewModel.saveFileToDevice(pairData)
        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test saveFileToDevice from private share`() {
        // arrange
        val testObserver = TestObserver.test(viewModel.onSaveFileToDevice)
        val pairData = Pair("id", "password")
        coEvery {
            trueCloudLoginManagerInterface.isLoggedIn()
        } returns true
        coEvery {
            getPublicSharedFileUseCase.execute("id")
        } returns flowOf(SharedFileModel())
        coEvery {
            getPrivateSharedFileUseCase.execute("id", "password")
        } returns flowOf(SharedFileModel())
        // act
        viewModel.saveFileToDevice(pairData)
        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test navigateBackToMain`() {
        // arrange
        every { router.execute(destination = any(), bundle = any()) } just runs

        // act
        viewModel.navigateBackToMain()

        // assert
        verify(exactly = 1) {
            router.execute(
                destination = SharedFileViewerToMain,
                bundle = any()
            )
        }
    }
}
