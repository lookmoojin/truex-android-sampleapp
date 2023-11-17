package com.truedigital.features.truecloudv3.presentation.viewmodel

import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.datastore.preferences.core.stringPreferencesKey
import com.jraska.livedata.TestObserver
import com.truedigital.common.share.analytics.measurement.AnalyticManagerInterface
import com.truedigital.common.share.currentdate.usecase.GetCurrentDateTimeUseCase
import com.truedigital.common.share.datalegacy.login.LoginManagerInterface
import com.truedigital.common.share.datalegacy.wrapper.AuthLoginListener
import com.truedigital.common.share.datalegacy.wrapper.ContextDataProviderWrapper
import com.truedigital.core.coroutines.TestCoroutineDispatcherProvider
import com.truedigital.core.provider.ContextDataProvider
import com.truedigital.core.utils.DataStoreUtil
import com.truedigital.core.utils.SharedPrefsUtils
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.common.FileCategoryType
import com.truedigital.features.truecloudv3.common.FileMimeType
import com.truedigital.features.truecloudv3.common.MigrationStatus
import com.truedigital.features.truecloudv3.common.TaskStatusType
import com.truedigital.features.truecloudv3.common.TrueCloudV3MediaType
import com.truedigital.features.truecloudv3.common.TrueCloudV3TransferState
import com.truedigital.features.truecloudv3.data.model.DataMigration
import com.truedigital.features.truecloudv3.data.model.DataStorage
import com.truedigital.features.truecloudv3.data.model.DataUsage
import com.truedigital.features.truecloudv3.data.model.TrueCloudV3StorageData
import com.truedigital.features.truecloudv3.domain.model.DataMigrationModel
import com.truedigital.features.truecloudv3.domain.model.DataStorageModel
import com.truedigital.features.truecloudv3.domain.model.DataUsageModel
import com.truedigital.features.truecloudv3.domain.model.TaskUploadModel
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3Model
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3TransferObserver
import com.truedigital.features.truecloudv3.domain.usecase.CompleteUploadUseCase
import com.truedigital.features.truecloudv3.domain.usecase.CreateFolderUserCase
import com.truedigital.features.truecloudv3.domain.usecase.GetMigrateFailDisplayTimeUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetStorageSpaceUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetUploadTaskListUseCase
import com.truedigital.features.truecloudv3.domain.usecase.RemoveTaskUseCase
import com.truedigital.features.truecloudv3.domain.usecase.ScanFileUseCase
import com.truedigital.features.truecloudv3.domain.usecase.SetMigrateFailDisplayTimeUseCase
import com.truedigital.features.truecloudv3.domain.usecase.UpdateTaskUploadStatusUseCase
import com.truedigital.features.truecloudv3.domain.usecase.UploadFileUseCase
import com.truedigital.features.truecloudv3.domain.usecase.UploadQueueUseCase
import com.truedigital.features.truecloudv3.navigation.MainToContact
import com.truedigital.features.truecloudv3.navigation.MainToCreateNewFolder
import com.truedigital.features.truecloudv3.navigation.MainToFiles
import com.truedigital.features.truecloudv3.navigation.MainToIntroMigrate
import com.truedigital.features.truecloudv3.navigation.MainToMigrate
import com.truedigital.features.truecloudv3.navigation.MainToPermission
import com.truedigital.features.truecloudv3.navigation.MainToSetting
import com.truedigital.features.truecloudv3.navigation.MainToTrash
import com.truedigital.features.truecloudv3.navigation.router.MainTrueCloudV3RouterUseCase
import com.truedigital.features.truecloudv3.provider.TrueCloudV3TransferUtilityProvider
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.coroutines.TestCoroutinesExtension
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal interface MainTrueCloudViewModelTestCase {
    fun `test getStorage success`()
    fun `test getStorage error`()
    fun `test getNextActionCategoryIntent case null`()
    fun `test getNextActionCategoryIntent case TypeImage`()
    fun `test getNextActionCategoryIntent case TypeVideo`()
    fun `test getNextActionCategoryIntent case TypeAudio`()
    fun `test getNextActionCategoryIntent case TypeContact`()
    fun `test getNextActionCategoryIntent case TypeFile`()
    fun `test getNextActionCategoryIntent case TypeAllFile`()
    fun `test getNextActionCategoryIntent case TypeOther not response`()
    fun `test getNextActionIntent case null`()
    fun `test getNextActionIntent case Contact NoValue`()
    fun `test getNextActionIntent case mediaType isEmpty`()
    fun `test getNextActionIntent case Image`()
    fun `test getNextActionIntent case Video`()
    fun `test getNextActionIntent case Audio`()
    fun `test getNextActionIntent case File`()
    fun `test getNextActionIntent case AllFile`()
    fun `test getNextActionIntent case Other`()
    fun `test toIntroMigrationPage case`()
    fun `test createNewFolder case`()
    fun `test toMigrationPage case`()
    fun `test toContactPage case`()
    fun `test performClickIntroPermission case success`()
    fun `test gotoSettingPage case`()
    fun `test checkMigration case INIT`()
    fun `test checkMigration case MIGRATED`()
    fun `test checkMigration case FAILED`()
    fun `test checkMigration case FAILED failedDisplayTime null`()
    fun `test checkMigration case FAILED displayTime not default`()
    fun `test onSelectedUploadFileResult null`()
    fun `test onSelectedUploadFileResult multiple`()
}

@ExtendWith(InstantTaskExecutorExtension::class)
internal class MainTrueCloudV3ViewModelTest : MainTrueCloudViewModelTestCase {
    @ExperimentalCoroutinesApi
    @RegisterExtension
    @JvmField
    val testCoroutine = TestCoroutinesExtension()
    private val router: MainTrueCloudV3RouterUseCase = mockk()
    private val getStorageSpaceUseCase: GetStorageSpaceUseCase = mockk()
    private val getMigrateFailDisplayTimeUseCase: GetMigrateFailDisplayTimeUseCase = mockk()
    private val setMigrateFailDisplayTimeUseCase: SetMigrateFailDisplayTimeUseCase = mockk()
    private val contextDataProviderWrapper: ContextDataProviderWrapper = mockk()
    private val coroutineDispatcher = TestCoroutineDispatcherProvider(testCoroutine.dispatcher)
    private lateinit var viewModel: MainTrueCloudV3ViewModel
    private val loginManagerInterface: LoginManagerInterface = mockk(relaxed = true)
    private val completeUploadUseCase: CompleteUploadUseCase = mockk(relaxed = true)
    private val trueCloudV3TransferUtilityProvider: TrueCloudV3TransferUtilityProvider =
        mockk(relaxed = true)
    private val getUploadTaskListUseCase: GetUploadTaskListUseCase = mockk(relaxed = true)
    private val uploadFileUseCase: UploadFileUseCase = mockk()
    private val removeTaskUseCase: RemoveTaskUseCase = mockk(relaxed = true)
    private val analyticManagerInterface: AnalyticManagerInterface = mockk(relaxed = true)
    private val createFolderUserCase: CreateFolderUserCase = mockk()
    private val updateTaskUploadStatusUseCase: UpdateTaskUploadStatusUseCase = mockk()
    private val sharedPrefsUtils: SharedPrefsUtils = mockk(relaxed = true)
    private val dataStoreUtil: DataStoreUtil = mockk(relaxed = true)
    private val getCurrentDateTimeUseCase: GetCurrentDateTimeUseCase = mockk()
    private val scanFileUseCase: ScanFileUseCase = mockk()
    private val uri: Uri = mockk()
    private val uploadQueueUseCase: UploadQueueUseCase = mockk(relaxed = true)

    @BeforeEach
    fun setUp() {
        viewModel = MainTrueCloudV3ViewModel(
            router = router,
            coroutineDispatcher = coroutineDispatcher,
            trueCloudLoginManagerInterface = loginManagerInterface,
            getStorageSpaceUseCase = getStorageSpaceUseCase,
            getMigrateFailDisplayTimeUseCase = getMigrateFailDisplayTimeUseCase,
            setMigrateFailDisplayTimeUseCase = setMigrateFailDisplayTimeUseCase,
            completeUploadUseCase = completeUploadUseCase,
            uploadFileUseCase = uploadFileUseCase,
            removeTaskUseCase = removeTaskUseCase,
            createFolderUserCase = createFolderUserCase,
            updateTaskUploadStatusUseCase = updateTaskUploadStatusUseCase,
            getUploadTaskListUseCase = getUploadTaskListUseCase,
            trueCloudV3TransferUtilityProvider = trueCloudV3TransferUtilityProvider,
            contextDataProviderWrapper = contextDataProviderWrapper,
            getCurrentDateTimeUseCase = getCurrentDateTimeUseCase,
            scanFileUseCase = scanFileUseCase,
            analyticManagerInterface = analyticManagerInterface,
            dataStoreUtil = dataStoreUtil,
            uploadQueueUseCase = uploadQueueUseCase
        )
    }

    @Test
    override fun `test getNextActionIntent case null`() {
        // arrange
        val observerActionGetContent = TestObserver.test(viewModel.onIntentActionGetContent)

        // act
        viewModel.getNextActionIntent(null)

        // assert
        observerActionGetContent.assertNoValue()
    }

    @Test
    override fun `test getNextActionIntent case Contact NoValue`() {
        // arrange
        val observerActionGetContent = TestObserver.test(viewModel.onIntentActionGetContent)

        // act
        viewModel.getNextActionIntent(TrueCloudV3MediaType.TypeContact)

        // assert
        observerActionGetContent.assertNoValue()
    }

    @Test
    override fun `test getNextActionIntent case mediaType isEmpty`() {
        // arrange
        val observerActionGetContent = TestObserver.test(viewModel.onIntentActionGetContent)
        val trueCloudMediaType = mockk<TrueCloudV3MediaType>()
        every {
            trueCloudMediaType.mediaType
        } returns ""
        // act
        viewModel.getNextActionIntent(trueCloudMediaType)

        // assert
        observerActionGetContent.assertNoValue()
    }

    @Test
    override fun `test getNextActionIntent case Image`() {
        // arrange
        val observerActionGetContent = TestObserver.test(viewModel.onIntentActionGetContent)

        // act
        viewModel.getNextActionIntent(TrueCloudV3MediaType.TypeImage)

        // assert
        observerActionGetContent.assertValue(TrueCloudV3MediaType.TypeImage.mimeType)
    }

    @Test
    override fun `test getNextActionIntent case Video`() {
        // arrange
        val observerActionGetContent = TestObserver.test(viewModel.onIntentActionGetContent)

        // act
        viewModel.getNextActionIntent(TrueCloudV3MediaType.TypeVideo)

        // assert
        observerActionGetContent.assertValue(TrueCloudV3MediaType.TypeVideo.mimeType)
    }

    @Test
    override fun `test getNextActionIntent case Audio`() {
        // arrange
        val observerActionGetContent = TestObserver.test(viewModel.onIntentActionGetContent)

        // act
        viewModel.getNextActionIntent(TrueCloudV3MediaType.TypeAudio)

        // assert
        observerActionGetContent.assertValue(TrueCloudV3MediaType.TypeAudio.mimeType)
    }

    @Test
    override fun `test getNextActionIntent case File`() {
        // arrange
        val observerActionGetContent = TestObserver.test(viewModel.onIntentActionGetContent)

        // act
        viewModel.getNextActionIntent(TrueCloudV3MediaType.TypeFile)

        // assert
        observerActionGetContent.assertValue(TrueCloudV3MediaType.TypeFile.mimeType)
    }

    @Test
    override fun `test getNextActionIntent case AllFile`() {
        // arrange
        val observerActionGetContent = TestObserver.test(viewModel.onIntentActionGetContent)

        // act
        viewModel.getNextActionIntent(TrueCloudV3MediaType.TypeAllFile)

        // assert
        observerActionGetContent.assertValue(TrueCloudV3MediaType.TypeAllFile.mimeType)
    }

    @Test
    override fun `test getNextActionIntent case Other`() {
        // arrange
        val observerActionGetContent = TestObserver.test(viewModel.onIntentActionGetContent)

        // act
        viewModel.getNextActionIntent(TrueCloudV3MediaType.TypeOther)

        // assert
        observerActionGetContent.assertValue(TrueCloudV3MediaType.TypeOther.mimeType)
    }

    @Test
    override fun `test toIntroMigrationPage case`() {
        // arrange
        every { router.execute(any()) } just runs

        // act
        viewModel.toIntroMigrationPage()

        // assert
        verify(exactly = 1) { router.execute(MainToIntroMigrate) }
    }

    @Test
    override fun `test createNewFolder case`() {
        // arrange
        every { router.execute(destination = any()) } just runs

        // act
        viewModel.createNewFolder()

        // assert
        verify(exactly = 1) { router.execute(destination = MainToCreateNewFolder) }
    }

    @Test
    override fun `test toMigrationPage case`() {
        // arrange
        every { router.execute(any()) } just runs

        // act
        viewModel.migrateClicked()

        // assert
        verify(exactly = 1) { router.execute(MainToMigrate) }
    }

    @Test
    override fun `test toContactPage case`() {
        // arrange
        every { router.execute(any(), any()) } just runs

        // act
        viewModel.contactClicked(FileCategoryType.CONTACT.type)

        // assert
        verify(exactly = 1) { router.execute(MainToContact, any()) }
    }

    @Test
    override fun `test performClickIntroPermission case success`() {
        // arrange
        every { router.execute(any(), any()) } just runs

        // act
        viewModel.performClickIntroPermission(Bundle())

        // assert
        verify(exactly = 1) { router.execute(MainToPermission, any()) }
    }

    @Test
    override fun `test gotoSettingPage case`() {
        // arrange
        every { router.execute(any(), any()) } just runs

        // act
        viewModel.settingClicked()

        // assert
        verify(exactly = 1) { router.execute(MainToSetting, any()) }
    }

    @Test
    override fun `test checkMigration case INIT`() {
        // arrange
        every { router.execute(any(), any()) } just runs
        val migration = DataMigrationModel(
            status = MigrationStatus.INIT.key,
        )
        // act
        viewModel.checkMigration(migration)

        // assert
        verify(exactly = 1) { router.execute(MainToIntroMigrate) }
    }

    @Test
    override fun `test checkMigration case MIGRATED`() {
        // arrange
        val migration = DataMigrationModel(
            status = MigrationStatus.MIGRATED.key,
        )
        val testObserver = TestObserver.test(viewModel.onShowMigrated)

        // act
        viewModel.checkMigration(migration)

        // assert
        testObserver.assertValue(true)
    }

    @Test
    override fun `test checkMigration case FAILED`() = runTest {
        // arrange
        coEvery { getMigrateFailDisplayTimeUseCase.execute() } returns (0L)
        val migration = DataMigrationModel(
            status = MigrationStatus.FAILED.key,
            failedDisplayTime = 1,
        )
        val testObserver = TestObserver.test(viewModel.onShowMigrateFail)

        coEvery {
            setMigrateFailDisplayTimeUseCase.execute(any())
        } returns Unit

        // act
        viewModel.checkMigration(migration)

        // assert
        coVerify(exactly = 1) { getMigrateFailDisplayTimeUseCase.execute() }
        coVerify(exactly = 1) { setMigrateFailDisplayTimeUseCase.execute(any()) }
        testObserver.assertHasValue()
    }

    @Test
    override fun `test checkMigration case FAILED displayTime not default`() = runTest {
        // arrange
        coEvery { getMigrateFailDisplayTimeUseCase.execute() } returns (1L)
        val migration = DataMigrationModel(
            status = MigrationStatus.FAILED.key,
            failedDisplayTime = 1,
        )
        val testObserver = TestObserver.test(viewModel.onShowMigrateFail)

        coEvery {
            setMigrateFailDisplayTimeUseCase.execute(any())
        } returns Unit

        // act
        viewModel.checkMigration(migration)

        // assert
        coVerify(exactly = 0) { setMigrateFailDisplayTimeUseCase.execute(any()) }
        testObserver.assertHasValue()
    }

    @Test
    override fun `test checkMigration case FAILED failedDisplayTime null`() = runTest {
        // arrange
        coEvery { getMigrateFailDisplayTimeUseCase.execute() } returns (0L)
        val migration = DataMigrationModel(
            status = MigrationStatus.FAILED.key,
        )

        // act
        viewModel.checkMigration(migration)

        // assert
        coVerify(exactly = 0) { getMigrateFailDisplayTimeUseCase.execute() }
        coVerify(exactly = 0) { setMigrateFailDisplayTimeUseCase.execute(any()) }
    }

    @Test
    override fun `test getNextActionCategoryIntent case TypeImage`() {
        // arrange
        every { router.execute(MainToFiles, any()) } just runs

        // act
        viewModel.getNextActionCategoryIntent(TrueCloudV3MediaType.TypeImage)

        // assert
        verify(exactly = 1) { router.execute(MainToFiles, any()) }
    }

    @Test
    override fun `test getNextActionCategoryIntent case TypeVideo`() {
        // arrange
        every { router.execute(MainToFiles, any()) } just runs

        // act
        viewModel.getNextActionCategoryIntent(TrueCloudV3MediaType.TypeVideo)

        // assert
        verify(exactly = 1) { router.execute(MainToFiles, any()) }
    }

    @Test
    override fun `test getNextActionCategoryIntent case TypeAudio`() {
        // arrange
        every { router.execute(MainToFiles, any()) } just runs

        // act
        viewModel.getNextActionCategoryIntent(TrueCloudV3MediaType.TypeAudio)

        // assert
        verify(exactly = 1) { router.execute(MainToFiles, any()) }
    }

    @Test
    override fun `test getNextActionCategoryIntent case TypeContact`() {
        // arrange
        every { router.execute(MainToContact, any()) } just runs
        every { analyticManagerInterface.trackEvent(any()) } just runs
        // act
        viewModel.getNextActionCategoryIntent(TrueCloudV3MediaType.TypeContact)

        // assert
        verify(exactly = 1) { router.execute(MainToContact, any()) }
    }

    @Test
    override fun `test getNextActionCategoryIntent case TypeFile`() {
        // arrange
        every { router.execute(MainToFiles, any()) } just runs
        every { analyticManagerInterface.trackEvent(any()) } just runs
        // act
        viewModel.getNextActionCategoryIntent(TrueCloudV3MediaType.TypeFile)

        // assert
        verify(exactly = 1) { router.execute(MainToFiles, any()) }
    }

    @Test
    override fun `test getNextActionCategoryIntent case TypeAllFile`() {
        // arrange
        every { router.execute(MainToFiles, any()) } just runs

        // act
        viewModel.getNextActionCategoryIntent(TrueCloudV3MediaType.TypeAllFile)

        // assert
        verify(exactly = 1) { router.execute(MainToFiles, any()) }
    }

    @Test
    override fun `test getNextActionCategoryIntent case TypeOther not response`() {
        // arrange
        every { router.execute(MainToFiles, any()) } just runs
        every { analyticManagerInterface.trackEvent(any()) } just runs

        // act
        viewModel.getNextActionCategoryIntent(TrueCloudV3MediaType.TypeOther)

        // assert
        verify(exactly = 1) {
            router.execute(MainToFiles, any())
            analyticManagerInterface.trackEvent(any())
        }
    }

    @Test
    override fun `test getNextActionCategoryIntent case null`() {
        // arrange
        every { router.execute(MainToFiles, any()) } just runs

        // act
        viewModel.getNextActionCategoryIntent(null)

        // assert
        verify(exactly = 0) { router.execute(MainToFiles, any()) }
    }

    @Test
    override fun `test getStorage success`() {
        // arrange
        val dataUsage = DataUsageModel()
        dataUsage.images = 0L
        dataUsage.videos = 0L
        dataUsage.audio = 0L
        dataUsage.others = 0L
        dataUsage.contacts = 0L
        dataUsage.total = 0L
        dataUsage.sortedObj = null

        val migration = DataMigrationModel()
        migration.status = "Migrated"
        val trueCloudV3Object = TrueCloudV3Model()
        trueCloudV3Object.id = "test_id"
        trueCloudV3Object.size = "test_size"
        trueCloudV3Object.coverImageSize = "test_coverimagesize"
        trueCloudV3Object.createdAt = "test_createdAt"
        trueCloudV3Object.mimeType = "test_mimeType"
        trueCloudV3Object.coverImageKey = "test_coverImageKey"
        trueCloudV3Object.name = "test_name"
        trueCloudV3Object.category = "test_category"
        trueCloudV3Object.objectType = "test_objectType"
        trueCloudV3Object.parentObjectId = "test_parentObjectId"
        trueCloudV3Object.updatedAt = "test_updatedAt"

        val response = DataStorageModel()
        response.quota = 500L
        response.rootFolder = trueCloudV3Object
        response.dataUsage = dataUsage
        response.migration = migration

        coEvery { getStorageSpaceUseCase.execute() } coAnswers { flowOf(response) }

        val testObserver = TestObserver.test(viewModel.onShowStorage)

        // act
        viewModel.getStorage()

        // assert
        testObserver.assertValue {
            assertEquals(response.quota, it.quota)
            true
        }
        testObserver.assertValue {
            assertEquals(response.dataUsage?.audio, it.dataUsage?.audio)
            true
        }
        testObserver.assertValue {
            assertEquals(response.dataUsage?.contacts, it.dataUsage?.contacts)
            true
        }
        testObserver.assertValue {
            assertEquals(response.dataUsage?.images, it.dataUsage?.images)
            true
        }
        testObserver.assertValue {
            assertEquals(response.dataUsage?.others, it.dataUsage?.others)
            true
        }
        testObserver.assertValue {
            assertEquals(response.dataUsage?.total, it.dataUsage?.total)
            true
        }
        testObserver.assertValue {
            assertEquals(response.migration?.status, it.migration?.status)
            true
        }
    }

    @Test
    fun `test getStorage success with migration status empty`() {
        // arrange
        val dataUsage = DataUsageModel()
        dataUsage.images = 0L
        dataUsage.videos = 0L
        dataUsage.audio = 0L
        dataUsage.others = 0L
        dataUsage.contacts = 0L
        dataUsage.total = 0L
        dataUsage.sortedObj = null

        val trueCloudV3Object = TrueCloudV3Model()
        trueCloudV3Object.id = "test_id"
        trueCloudV3Object.size = "test_size"
        trueCloudV3Object.coverImageSize = "test_coverimagesize"
        trueCloudV3Object.createdAt = "test_createdAt"
        trueCloudV3Object.mimeType = "test_mimeType"
        trueCloudV3Object.coverImageKey = "test_coverImageKey"
        trueCloudV3Object.name = "test_name"
        trueCloudV3Object.category = "test_category"
        trueCloudV3Object.objectType = "test_objectType"
        trueCloudV3Object.parentObjectId = "test_parentObjectId"
        trueCloudV3Object.updatedAt = "test_updatedAt"

        val response = DataStorageModel()
        response.quota = 500L
        response.rootFolder = null
        response.dataUsage = null
        response.migration = null

        coEvery { getStorageSpaceUseCase.execute() } coAnswers { flowOf(response) }

        val testObserver = TestObserver.test(viewModel.onShowStorage)

        // act
        viewModel.getStorage()

        // assert
        testObserver.assertValue {
            assertEquals(response.quota, it.quota)
            true
        }
        testObserver.assertValue {
            assertEquals(response.dataUsage?.audio, it.dataUsage?.audio)
            true
        }
        testObserver.assertValue {
            assertEquals(response.dataUsage?.contacts, it.dataUsage?.contacts)
            true
        }
        testObserver.assertValue {
            assertEquals(response.dataUsage?.images, it.dataUsage?.images)
            true
        }
        testObserver.assertValue {
            assertEquals(response.dataUsage?.others, it.dataUsage?.others)
            true
        }
        testObserver.assertValue {
            assertEquals(response.dataUsage?.total, it.dataUsage?.total)
            true
        }
        testObserver.assertValue {
            assertEquals(response.migration?.status, it.migration?.status)
            true
        }
    }

    @Test
    override fun `test getStorage error`() {
        // arrange
        coEvery { getStorageSpaceUseCase.execute() } coAnswers { flow { error("mock error") } }
        val testObserver = TestObserver.test(viewModel.onGetStorageError)

        // act
        viewModel.getStorage()

        // assert
        verify(exactly = 1) { getStorageSpaceUseCase.execute() }
        testObserver.assertHasValue()
    }

    @Test
    override fun `test onSelectedUploadFileResult null`() {
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
        val intent = mockk<Intent>()

        every { intent.data } returns uri
        every { intent.clipData } returns null
        // act
        viewModel.onSelectedUploadFileResult(intent)

        // assert
        testObserver.assertHasValue()
    }

    @Test
    override fun `test onSelectedUploadFileResult multiple`() = runTest {
        val testObserver = TestObserver.test(viewModel.onShowSnackbarComplete)
        val handler = mockk<Handler>()
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

        coEvery { uploadFileUseCase.execute(any(), any()) } returns flowOf(transferObserver)
        coEvery { completeUploadUseCase.execute(any()) } coAnswers {
            flowOf(trueCloudV3Model)
        }
        coEvery { removeTaskUseCase.execute(any()) } returns Unit

        every { transferObserver.getState() } returns TrueCloudV3TransferState.COMPLETED
        every {
            contextDataProviderWrapper.get()
                .getString(R.string.true_cloudv3_migration_toast_success)
        } returns "true_cloudv3_migration_toast_success"
        every { handler.postDelayed(any(), any()) } returns true

        viewModel.rootFolderId = "rootFolderId_test"
        val intent = mockk<Intent>()
        val clipData = mockk<ClipData>()
        val contentUri1 = mockk<Uri>(name = "content://uri-1")
        val contentUri2 = mockk<Uri>(name = "content://uri-2")

        every { intent.data } returns null
        every { intent.clipData } returns clipData
        every { intent.clipData?.itemCount } returns 2
        every { intent.clipData?.getItemAt(0)?.uri } returns contentUri1
        every { intent.clipData?.getItemAt(1)?.uri } returns contentUri2
        every { viewModel.uploadMutipleFile(mutableListOf(uri)) } returns Unit
        // act
        viewModel.onSelectedUploadFileResult(intent)

        // assert
        verify(exactly = 1) { completeUploadUseCase.execute(any()) }
        // testObserver.assertHasValue()
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
        val testObserver = TestObserver.test(viewModel.onShowStorage)
        coEvery { getStorageSpaceUseCase.execute() } coAnswers { flowOf(response) }
        coEvery {
            loginManagerInterface.isLoggedIn()
        } returns true

        // act
        viewModel.checkAuthenticationState()

        // assert
        verify(exactly = 1) { getStorageSpaceUseCase.execute() }
        testObserver.assertHasValue()
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

        coEvery { getStorageSpaceUseCase.execute() } coAnswers { flowOf(response) }
        val slotListener = slot<AuthLoginListener>()
        every { loginManagerInterface.isLoggedIn() } returns false
        every { loginManagerInterface.login(capture(slotListener), any(), any()) } just runs

        // act
        viewModel.checkAuthenticationState()

        // assert
        slotListener.captured.onLoginSuccess()
        verify(exactly = 1) { loginManagerInterface.login(any(), any(), any()) }
    }

    @Test
    fun testGetStorage() {
        // arrange
        val context = mockk<Context>()
        val testObserver = TestObserver.test(viewModel.onShowStorage)
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
        testObserver.assertHasValue()
    }

    @Test
    fun `test completeUpload error`() = runTest {
        // arrange
        val trueCloudV3Model = TrueCloudV3Model()
        coEvery {
            completeUploadUseCase.execute(any())
        } coAnswers {
            flowOf(trueCloudV3Model)
        }
        coEvery { removeTaskUseCase.execute(any()) } returns Unit

        // act
        viewModel.completeUpload(0)

        // assert
        coVerify(exactly = 1) { completeUploadUseCase.execute(any()) }
        coVerify(exactly = 1) { removeTaskUseCase.execute(any()) }
    }

    @Test
    fun `test createFolder success`() = runTest {
        // arrange
        val observer = TestObserver.test(viewModel.onShowSnackbarComplete)
        val trueCloudV3Object = TrueCloudV3Model()
        coEvery { createFolderUserCase.execute(any(), any()) } coAnswers {
            flowOf(trueCloudV3Object)
        }
        every {
            contextDataProviderWrapper.get()
                .getString(R.string.true_cloudv3_added_new_folder)
        } returns "true_cloudv3_added_new_folder"
        viewModel.rootFolderId = "rootFolderId_test"
        // act
        viewModel.createFolder("folder_name_test")

        // assert
        observer.assertHasValue()
        coVerify(exactly = 1) { createFolderUserCase.execute(any(), any()) }
    }

    @Test
    fun `test createFolder failed`() = runTest {
        // arrange
        val observer = TestObserver.test(viewModel.onShowSnackbarError)
        coEvery {
            createFolderUserCase.execute(
                any(),
                any()
            )
        } coAnswers { flow { error("mock error") } }
        every {
            contextDataProviderWrapper.get()
                .getString(R.string.true_cloudv3_can_not_add_new_folder)
        } returns "true_cloudv3_can_not_add_new_folder"

        viewModel.rootFolderId = "rootFolderId_test"
        // act
        viewModel.createFolder("folder_name_test")

        // assert
        observer.assertHasValue()
        coVerify(exactly = 1) { createFolderUserCase.execute(any(), any()) }
    }

    @Test
    fun `test createFolder rootFolderId is null`() = runTest {
        // arrange
        viewModel.rootFolderId = null
        // act
        viewModel.createFolder("folder_name_test")

        // assert
        assertNull(viewModel.rootFolderId)
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
        viewModel.uploadMutipleFile(mutableListOf(uri))

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test uploadFile isMultipleUpload false error`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onUploadError)
        coEvery {
            uploadFileUseCase.execute(
                any(),
                any(),
            )
        } coAnswers { flow { error("mock error") } }
        viewModel.isMultipleUpload = false

        // act
        viewModel.uploadMutipleFile(mutableListOf(uri))

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test uploadFile isMultipleUpload true multipleUploadErrorCount is 2 error`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onUploadError)
        coEvery {
            uploadFileUseCase.execute(
                any(),
                any(),
            )
        } coAnswers { flow { error("mock error") } }
        viewModel.isMultipleUpload = true
        viewModel.multipleUploadErrorCount = 2

        // act
        viewModel.uploadMutipleFile(mutableListOf(uri))

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test uploadFile isMultipleUpload true multipleUploadErrorCount is 3 error`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onUploadError)
        coEvery {
            uploadFileUseCase.execute(
                any(),
                any(),
            )
        } coAnswers { flow { error("mock error") } }
        viewModel.isMultipleUpload = true
        viewModel.multipleUploadErrorCount = 3

        // act
        viewModel.uploadMutipleFile(mutableListOf(uri))

        // assert
        testObserver.assertNoValue()
    }

    @Test
    fun `test uploadWithTransferUtility fail success`() = runTest {
        // arrange
        val testObserver = TestObserver.test(viewModel.onShowSnackbarComplete)
        val transferObserver = mockk<TrueCloudV3TransferObserver>(relaxed = true)

        coEvery { uploadFileUseCase.execute(any(), any()) } coAnswers {
            flowOf(transferObserver)
        }
        coEvery { updateTaskUploadStatusUseCase.execute(any(), TaskStatusType.FAILED) } returns Unit

        every { transferObserver.getState() } returns TrueCloudV3TransferState.FAILED
        viewModel.rootFolderId = "rootFolderId_test"
        // act
        viewModel.uploadMutipleFile(mutableListOf(uri))

        // assert
        testObserver.assertNoValue()
        coVerify(exactly = 1) { updateTaskUploadStatusUseCase.execute(any(), any()) }
    }

    @Test
    fun testTransferlistenerServiceOnStateChangedComplete() {
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
        coEvery { completeUploadUseCase.execute(any()) } coAnswers {
            flowOf(trueCloudV3Model)
        }
        coEvery { removeTaskUseCase.execute(any()) } returns Unit
        every {
            contextDataProviderWrapper.get()
                .getString(R.string.true_cloudv3_migration_toast_success)
        } returns "true_cloudv3_migration_toast_success"

        // act
        viewModel.transferlistener.onStateChanged(1, TrueCloudV3TransferState.COMPLETED)

        // assert
        coVerify(exactly = 1) {
            removeTaskUseCase.execute(any())
            completeUploadUseCase.execute(any())
        }
        testObserver.assertHasValue()
    }

    @Test
    fun testTransferlistenerServiceOnStateChangedCanceled() {
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
    fun testTransferlistenerServiceOnStateChangedPause() {
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
    fun testTransferlistenerServiceOnStateChangedResumeWaiting() {
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
    fun testTransferlistenerServiceOnStateChangedOther() {
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
    fun testTransferlistenerServiceOnStateChangedFailed() {
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
    fun testTransferlistenerServiceOnError() {
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
    fun testTransferlistenerServiceOnProgressChange() {
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
    fun testCheckRetryState() {
        // arrange
        coEvery {
            getStorageSpaceUseCase.execute()
        } returns flowOf()
        // act
        viewModel.checkRetryState("GET_STORAGE")
        // assert
        coVerify(exactly = 1) {
            getStorageSpaceUseCase.execute()
        }
    }

    @Test
    fun testCheckRetryStateActionNull() {
        // act
        viewModel.checkRetryState("")
        // assert
        coVerify(exactly = 0) {
            getStorageSpaceUseCase.execute()
        }
    }

    @Test
    fun testOnCreated() {
        // act
        every { analyticManagerInterface.trackScreen(any()) } just runs
        viewModel.onViewCreated()
        // assert
        coVerify(exactly = 1) {
            analyticManagerInterface.trackScreen(any())
        }
    }

    @Test
    fun `test onResume complete`() = runTest {
        // arrange
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 0L,
        )
        val trueCloudV3Model = TrueCloudV3Model()
        val contextDataProvider = mockk<ContextDataProvider>()
        val context = mockk<Context>()
        val testObserver = TestObserver.test(viewModel.onShowSnackbarComplete)
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
        coEvery {
            completeUploadUseCase.execute(any())
        } returns flowOf(trueCloudV3Model)
        coEvery {
            removeTaskUseCase.execute(any())
        } returns Unit
        coEvery {
            dataStoreUtil.getSinglePreference(
                stringPreferencesKey(AutoBackupViewModel.BACKUP_STATE_KEY),
                "false"
            )
        } returns "false"
        coEvery {
            dataStoreUtil.getSinglePreference(
                stringPreferencesKey(AutoBackupViewModel.lastTimeStamp),
                "0"
            )
        } returns "0"
        every {
            contextDataProviderWrapper.get()
        } returns contextDataProvider
        every {
            contextDataProvider.getDataContext()
        } returns context
        every {
            contextDataProvider.getString(any())
        } returns "Successfully Uploaded"

        // act
        viewModel.onResume()

        // assert
        testObserver.assertValue("Successfully Uploaded")
    }

    @Test
    fun `test onResume transfer is null call uploadQueue complete`() = runTest {
        // arrange
        val taskUploadModel = TaskUploadModel(
            id = 0,
            path = "abc",
            status = TaskStatusType.IN_QUEUE,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 0L,
            objectId = "object1"
        )
        val trueCloudV3Model = TrueCloudV3Model()
        val contextDataProvider = mockk<ContextDataProvider>()
        val context = mockk<Context>()
        val testObserver = TestObserver.test(viewModel.onShowSnackbarComplete)
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
        } returns null

        coEvery {
            completeUploadUseCase.execute(any())
        } returns flowOf(trueCloudV3Model)
        coEvery {
            getStorageSpaceUseCase.execute()
        } returns flowOf()
        coEvery {
            removeTaskUseCase.execute(any())
        } returns Unit
        coEvery {
            dataStoreUtil.getSinglePreference(
                stringPreferencesKey(AutoBackupViewModel.BACKUP_STATE_KEY),
                "false"
            )
        } returns "false"
        coEvery {
            dataStoreUtil.getSinglePreference(
                stringPreferencesKey(AutoBackupViewModel.lastTimeStamp),
                "0"
            )
        } returns "0"
        every {
            contextDataProviderWrapper.get()
        } returns contextDataProvider
        every {
            contextDataProvider.getDataContext()
        } returns context
        every {
            contextDataProvider.getString(any())
        } returns "Successfully Uploaded"

        // act
        viewModel.onResume()

        // assert
        testObserver.assertValue("Successfully Uploaded")
        verify {
            getStorageSpaceUseCase.execute()
        }
    }

    @Test
    fun `test onResume IN_PROGRESS success`() = runTest {
        // arrange
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 0L,
        )
        val contextDataProvider = mockk<ContextDataProvider>()
        val context = mockk<Context>()
        every {
            contextDataProviderWrapper.get()
        } returns contextDataProvider
        every {
            contextDataProvider.getDataContext()
        } returns context
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
        } returns TrueCloudV3TransferState.IN_PROGRESS
        coEvery {
            dataStoreUtil.getSinglePreference(
                stringPreferencesKey(AutoBackupViewModel.BACKUP_STATE_KEY),
                "false"
            )
        } returns "false"
        coEvery {
            dataStoreUtil.getSinglePreference(
                stringPreferencesKey(AutoBackupViewModel.lastTimeStamp),
                "0"
            )
        } returns "0"
        // act
        viewModel.onResume()

        // assert
        coVerify(exactly = 1) {
            trueCloudV3TransferUtilityProvider.resumeTransferById(any(), any())
        }
    }

    @Test
    fun `test onResume RESUMED_WAITING success`() = runTest {
        // arrange
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 0L,
        )
        val contextDataProvider = mockk<ContextDataProvider>()
        val context = mockk<Context>()
        every {
            contextDataProviderWrapper.get()
        } returns contextDataProvider
        every {
            contextDataProvider.getDataContext()
        } returns context
        coEvery {
            getUploadTaskListUseCase.execute()
        } returns flowOf(mutableListOf(taskUploadModel))

        val transfer: TrueCloudV3TransferObserver = mockk(relaxed = true)
        coEvery {
            trueCloudV3TransferUtilityProvider.getTrueCloudV3TransferObserverById(any(), any())
        } returns transfer
        coEvery {
            transfer.getState()
        } returns TrueCloudV3TransferState.RESUMED_WAITING
        coEvery {
            updateTaskUploadStatusUseCase.execute(any(), any())
        } returns Unit
        coEvery {
            trueCloudV3TransferUtilityProvider.resumeTransferById(any(), any())
        } returns transfer
        coEvery {
            dataStoreUtil.getSinglePreference(
                stringPreferencesKey(AutoBackupViewModel.BACKUP_STATE_KEY),
                "false"
            )
        } returns "false"
        coEvery {
            dataStoreUtil.getSinglePreference(
                stringPreferencesKey(AutoBackupViewModel.lastTimeStamp),
                "0"
            )
        } returns "0"
        // act
        viewModel.onResume()

        // assert
        coVerify(exactly = 1) {
            updateTaskUploadStatusUseCase.execute(any(), any())
            trueCloudV3TransferUtilityProvider.resumeTransferById(any(), any())
        }
    }

    @Test
    fun `test onResume WAITING success`() = runTest {
        // arrange
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 0L,
        )
        val contextDataProvider = mockk<ContextDataProvider>()
        val context = mockk<Context>()
        every {
            contextDataProviderWrapper.get()
        } returns contextDataProvider
        every {
            contextDataProvider.getDataContext()
        } returns context
        coEvery {
            getUploadTaskListUseCase.execute()
        } returns flowOf(mutableListOf(taskUploadModel))

        val transfer: TrueCloudV3TransferObserver = mockk(relaxed = true)
        coEvery {
            trueCloudV3TransferUtilityProvider.getTrueCloudV3TransferObserverById(any(), any())
        } returns transfer
        coEvery {
            transfer.getState()
        } returns TrueCloudV3TransferState.WAITING
        coEvery {
            updateTaskUploadStatusUseCase.execute(any(), any())
        } returns Unit
        coEvery {
            trueCloudV3TransferUtilityProvider.resumeTransferById(any(), any())
        } returns transfer
        coEvery {
            dataStoreUtil.getSinglePreference(
                stringPreferencesKey(AutoBackupViewModel.BACKUP_STATE_KEY),
                "false"
            )
        } returns "false"
        coEvery {
            dataStoreUtil.getSinglePreference(
                stringPreferencesKey(AutoBackupViewModel.lastTimeStamp),
                "0"
            )
        } returns "0"
        // act
        viewModel.onResume()

        // assert
        coVerify(exactly = 1) {
            updateTaskUploadStatusUseCase.execute(any(), any())
            trueCloudV3TransferUtilityProvider.resumeTransferById(any(), any())
        }
    }

    @Test
    fun `test onResume WAITING_FOR_NETWORK success`() = runTest {
        // arrange
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 0L,
        )
        val contextDataProvider = mockk<ContextDataProvider>()
        val context = mockk<Context>()
        every {
            contextDataProviderWrapper.get()
        } returns contextDataProvider
        every {
            contextDataProvider.getDataContext()
        } returns context
        coEvery {
            getUploadTaskListUseCase.execute()
        } returns flowOf(mutableListOf(taskUploadModel))

        val transfer: TrueCloudV3TransferObserver = mockk(relaxed = true)
        coEvery {
            trueCloudV3TransferUtilityProvider.getTrueCloudV3TransferObserverById(any(), any())
        } returns transfer
        coEvery {
            transfer.getState()
        } returns TrueCloudV3TransferState.WAITING_FOR_NETWORK
        coEvery {
            updateTaskUploadStatusUseCase.execute(any(), any())
        } returns Unit
        coEvery {
            trueCloudV3TransferUtilityProvider.resumeTransferById(any(), any())
        } returns transfer
        coEvery {
            dataStoreUtil.getSinglePreference(
                stringPreferencesKey(AutoBackupViewModel.BACKUP_STATE_KEY),
                "false"
            )
        } returns "false"
        coEvery {
            dataStoreUtil.getSinglePreference(
                stringPreferencesKey(AutoBackupViewModel.lastTimeStamp),
                "0"
            )
        } returns "0"
        // act
        viewModel.onResume()

        // assert
        coVerify(exactly = 1) {
            updateTaskUploadStatusUseCase.execute(any(), any())
            trueCloudV3TransferUtilityProvider.resumeTransferById(any(), any())
        }
    }

    @Test
    fun `test onResume FAILED success`() = runTest {
        // arrange
        val taskUploadModel = TaskUploadModel(
            id = 1,
            path = "abc",
            status = TaskStatusType.IN_PROGRESS,
            name = "xyz.jpg",
            size = "100",
            type = FileMimeType.IMAGE,
            updateAt = 0L,
        )
        val contextDataProvider = mockk<ContextDataProvider>()
        val context = mockk<Context>()
        every {
            contextDataProviderWrapper.get()
        } returns contextDataProvider
        every {
            contextDataProvider.getDataContext()
        } returns context
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
        coEvery {
            dataStoreUtil.getSinglePreference(
                stringPreferencesKey(AutoBackupViewModel.BACKUP_STATE_KEY),
                "false"
            )
        } returns "false"
        coEvery {
            dataStoreUtil.getSinglePreference(
                stringPreferencesKey(AutoBackupViewModel.lastTimeStamp),
                "0"
            )
        } returns "0"
        // act
        viewModel.onResume()

        // assert
        coVerify(exactly = 1) {
            updateTaskUploadStatusUseCase.execute(any(), any())
        }
    }

    @Test
    fun `test gotoTrashPage success`() = runTest {
        // arrange
        every { router.execute(any(), any()) } just runs

        // act
        viewModel.trashClicked()

        // assert
        verify(exactly = 1) { router.execute(MainToTrash, any()) }
    }

    @Test
    fun `test check Auto Backup return true`() = runTest {
        val testObserver = TestObserver.test(viewModel.onAutoBackupState)
        coEvery {
            dataStoreUtil.getSinglePreference(
                stringPreferencesKey(AutoBackupViewModel.BACKUP_STATE_KEY),
                "false"
            )
        } returns "true"
        coEvery {
            dataStoreUtil.getSinglePreference(
                stringPreferencesKey(AutoBackupViewModel.lastTimeStamp),
                "0"
            )
        } returns "0"
        // act
        viewModel.checkAutoBackup()

        // assert
        testObserver.assertHasValue()
    }

    @Test
    fun `test initBackup success`() = runTest {
        // arrange
        val contentList = arrayListOf<String>("video_backup")
        val mockFiles = mutableListOf(Uri.parse("file2"), Uri.parse("file3"))

        coEvery {
            dataStoreUtil.getSinglePreference(
                stringPreferencesKey(AutoBackupViewModel.imageBackup),
                "false"
            )
        } returns "true"
        coEvery {
            dataStoreUtil.getSinglePreference(
                stringPreferencesKey(AutoBackupViewModel.videoBackup),
                "false"
            )
        } returns "true"
        coEvery {
            dataStoreUtil.getSinglePreference(
                stringPreferencesKey(AutoBackupViewModel.audioBackup),
                "false"
            )
        } returns "true"
        coEvery {
            dataStoreUtil.getSinglePreference(
                stringPreferencesKey(AutoBackupViewModel.otherBackup),
                "false"
            )
        } returns "true"
        coEvery {
            dataStoreUtil.getSinglePreference(
                stringPreferencesKey(AutoBackupViewModel.lastTimeStamp),
                "123456789"
            )
        } returns "123456789"
        coEvery {
            scanFileUseCase.execute(any(), any())
        } returns flowOf(mockFiles)
        coEvery {
            getCurrentDateTimeUseCase.execute(any())
        } returns flowOf(123456789L)

        // act
        viewModel.initBackup()

        // assert
        coVerify(exactly = 1) {
            scanFileUseCase.execute(any(), any())
        }
    }
}
