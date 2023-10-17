package com.truedigital.features.truecloudv3.presentation.viewmodel

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.tdg.truecloud.R
import com.truedigital.common.share.analytics.di.AnalyticsModule
import com.truedigital.common.share.analytics.measurement.AnalyticManagerInterface
import com.truedigital.common.share.analytics.measurement.base.platform.PlatformAnalyticModel
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant
import com.truedigital.common.share.currentdate.usecase.GetCurrentDateTimeUseCase
import com.truedigital.common.share.datalegacy.login.LoginManagerInterface
import com.truedigital.common.share.datalegacy.wrapper.AuthLoginListener
import com.truedigital.common.share.datalegacy.wrapper.ContextDataProviderWrapper
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.extensions.launchSafe
import com.truedigital.core.extensions.launchSafeIn
import com.truedigital.core.utils.DataStoreUtil
import com.truedigital.features.truecloudv3.common.FileCategoryType
import com.truedigital.features.truecloudv3.common.MigrationStatus
import com.truedigital.features.truecloudv3.common.TaskActionType
import com.truedigital.features.truecloudv3.common.TaskStatusType
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_CATEGORY
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_DATA_USAGE
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_FOLDER_ID
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_MIGRATE_STATUS
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_STORAGE_DETAIL
import com.truedigital.features.truecloudv3.common.TrueCloudV3MediaType
import com.truedigital.features.truecloudv3.common.TrueCloudV3TrackAnalytic
import com.truedigital.features.truecloudv3.common.TrueCloudV3TrackAnalytic.LINK_DESC_CONTACTS
import com.truedigital.features.truecloudv3.common.TrueCloudV3TrackAnalytic.LINK_DESC_SETTING
import com.truedigital.features.truecloudv3.common.TrueCloudV3TrackAnalytic.LINK_DESC_TRASH
import com.truedigital.features.truecloudv3.common.TrueCloudV3TrackAnalytic.trackFirebaseOnClickEventWithCategory
import com.truedigital.features.truecloudv3.common.TrueCloudV3TransferState
import com.truedigital.features.truecloudv3.domain.model.DataMigrationModel
import com.truedigital.features.truecloudv3.domain.model.DataStorageModel
import com.truedigital.features.truecloudv3.domain.model.TaskUploadModel
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
import com.truedigital.features.truecloudv3.extension.formatBinarySize
import com.truedigital.features.truecloudv3.navigation.MainToContact
import com.truedigital.features.truecloudv3.navigation.MainToCreateNewFolder
import com.truedigital.features.truecloudv3.navigation.MainToFiles
import com.truedigital.features.truecloudv3.navigation.MainToIntroMigrate
import com.truedigital.features.truecloudv3.navigation.MainToMigrate
import com.truedigital.features.truecloudv3.navigation.MainToPermission
import com.truedigital.features.truecloudv3.navigation.MainToSetting
import com.truedigital.features.truecloudv3.navigation.MainToTrash
import com.truedigital.features.truecloudv3.navigation.router.MainTrueCloudV3RouterUseCase
import com.truedigital.features.truecloudv3.presentation.MainTrueCloudV3Fragment
import com.truedigital.features.truecloudv3.provider.TrueCloudV3TransferUtilityProvider
import com.truedigital.foundation.extension.SingleLiveEvent
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.jetbrains.annotations.VisibleForTesting
import timber.log.Timber
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Named

class MainTrueCloudV3ViewModel @Inject constructor(
    private val router: MainTrueCloudV3RouterUseCase,
    private val coroutineDispatcher: CoroutineDispatcherProvider,
    private val trueCloudLoginManagerInterface: LoginManagerInterface,
    private val getStorageSpaceUseCase: GetStorageSpaceUseCase,
    private val getMigrateFailDisplayTimeUseCase: GetMigrateFailDisplayTimeUseCase,
    private val setMigrateFailDisplayTimeUseCase: SetMigrateFailDisplayTimeUseCase,
    private val completeUploadUseCase: CompleteUploadUseCase,
    private val uploadFileUseCase: UploadFileUseCase,
    private val removeTaskUseCase: RemoveTaskUseCase,
    private val createFolderUserCase: CreateFolderUserCase,
    private val updateTaskUploadStatusUseCase: UpdateTaskUploadStatusUseCase,
    private val getUploadTaskListUseCase: GetUploadTaskListUseCase,
    private val scanFileUseCase: ScanFileUseCase,
    private val trueCloudV3TransferUtilityProvider: TrueCloudV3TransferUtilityProvider,
    private val contextDataProviderWrapper: ContextDataProviderWrapper,
    private val dataStoreUtil: DataStoreUtil,
    private val getCurrentDateTimeUseCase: GetCurrentDateTimeUseCase,
    @Named(AnalyticsModule.FIREBASE_ANALYTIC)
    private val analyticManagerInterface: AnalyticManagerInterface
) : ScopedViewModel() {

    companion object {
        private const val DEFAULT_DURATION = 0L
        private const val START_POSITION = 0
        private const val ZERO = "0"
        private const val SECOND_IN_MILLISEC = 1000
        private const val MAXIMUM_TOAST_ERROR = 3
        private const val ACTION_GET_STORAGE = "GET_STORAGE"
    }

    val onShowStorage = SingleLiveEvent<DataStorageModel>()
    val onGetStorageError = SingleLiveEvent<Pair<String, String>>()
    val onShowMigrated = SingleLiveEvent<Boolean>()
    val onShowMigrateFail = SingleLiveEvent<Boolean>()
    val onShowSnackbarComplete = SingleLiveEvent<String>()
    val onShowSnackbarError = SingleLiveEvent<String>()
    val onIntentActionGetContent = SingleLiveEvent<Array<String>>()
    val onUploadFile = SingleLiveEvent<Unit>()
    val onUploadError = MutableLiveData<String>()
    val onAutoBackupState = SingleLiveEvent<Boolean>()
    val onUpdateBackupUi = SingleLiveEvent<Boolean>()
    val onShowInitAutoBackup = SingleLiveEvent<Unit>()

    private var migrationStatus = ""
    private var storageDetail = ""
    private var autoBackupActive: Boolean = false
    private var storageData: DataStorageModel? = null

    @VisibleForTesting
    var multipleUploadErrorCount = 0

    @VisibleForTesting
    var isMultipleUpload = false

    @VisibleForTesting
    var rootFolderId: String? = null

    fun categoryClicked(fileCategoryType: FileCategoryType) {
        router.execute(
            MainToFiles,
            Bundle().apply {
                putString(KEY_BUNDLE_TRUE_CLOUD_FOLDER_ID, rootFolderId)
                putString(KEY_BUNDLE_TRUE_CLOUD_CATEGORY, fileCategoryType.type)
            }
        )
        val linkDesc = trackFirebaseOnClickEventWithCategory(fileCategoryType)
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_EVENT_NAME to TrueCloudV3TrackAnalytic.EVENT_CLICK,
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.LINK_TYPE_BROWSE,
                MeasurementConstant.Key.KEY_LINK_DESC to linkDesc
            )
        )
    }

    fun contactClicked(categoryName: String) {
        router.execute(
            MainToContact,
            Bundle().apply {
                putString(KEY_BUNDLE_TRUE_CLOUD_FOLDER_ID, rootFolderId)
                putString(KEY_BUNDLE_TRUE_CLOUD_CATEGORY, categoryName)
            }
        )
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_EVENT_NAME to TrueCloudV3TrackAnalytic.EVENT_CLICK,
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.LINK_TYPE_BROWSE,
                MeasurementConstant.Key.KEY_LINK_DESC to LINK_DESC_CONTACTS
            )
        )
    }

    fun createNewFolder() {
        router.execute(MainToCreateNewFolder)
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.EVENT_CLICK,
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.LINK_TYPE_BOTTOM_SHEET,
                MeasurementConstant.Key.KEY_LINK_DESC to TrueCloudV3TrackAnalytic.LINK_DESC_NEW_FOLDER
            )
        )
    }

    fun toIntroMigrationPage() {
        router.execute(MainToIntroMigrate)
    }

    fun migrateClicked() {
        router.execute(MainToMigrate)
    }

    fun settingClicked() {
        router.execute(
            MainToSetting,
            Bundle().apply {
                putString(KEY_BUNDLE_TRUE_CLOUD_MIGRATE_STATUS, migrationStatus)
                putString(KEY_BUNDLE_TRUE_CLOUD_STORAGE_DETAIL, storageDetail)
                putParcelable(KEY_BUNDLE_TRUE_CLOUD_DATA_USAGE, storageData?.dataUsage)
            }
        )
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_EVENT_NAME to TrueCloudV3TrackAnalytic.EVENT_CLICK,
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.LINK_TYPE_BROWSE,
                MeasurementConstant.Key.KEY_LINK_DESC to LINK_DESC_SETTING
            )
        )
    }

    fun trashClicked() {
        router.execute(
            MainToTrash,
            Bundle().apply {
                putString(KEY_BUNDLE_TRUE_CLOUD_FOLDER_ID, rootFolderId)
            }
        )
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_EVENT_NAME to TrueCloudV3TrackAnalytic.EVENT_CLICK,
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.LINK_TYPE_BROWSE,
                MeasurementConstant.Key.KEY_LINK_DESC to LINK_DESC_TRASH
            )
        )
    }

    fun onClickUpload() {
        onUploadFile.value = Unit
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_EVENT_NAME to TrueCloudV3TrackAnalytic.EVENT_CLICK,
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.LINK_TYPE_BROWSE,
                MeasurementConstant.Key.KEY_LINK_DESC to TrueCloudV3TrackAnalytic.LINK_DESC_UPLOAD
            )
        )
    }

    fun performClickIntroPermission(bundle: Bundle) {
        router.execute(MainToPermission, bundle)
    }

    fun checkAuthenticationState() {
        when {
            trueCloudLoginManagerInterface.isLoggedIn() -> {
                getStorage()
            }

            else -> {
                openAuthenticationPage()
            }
        }
    }

    fun checkRetryState(action: String) {
        if (action == ACTION_GET_STORAGE) getStorage()
    }

    fun createFolder(folderName: String?) {
        folderName?.let { _folderName ->
            rootFolderId?.let { _rootFolderId ->
                createFolderUserCase.execute(_rootFolderId, _folderName)
                    .flowOn(coroutineDispatcher.io())
                    .onEach {
                        onShowSnackbarComplete.value = contextDataProviderWrapper.get()
                            .getString(R.string.true_cloudv3_added_new_folder)
                    }
                    .catch {
                        onShowSnackbarError.value = contextDataProviderWrapper.get()
                            .getString(R.string.true_cloudv3_can_not_add_new_folder)
                    }
                    .launchSafeIn(this)
            }
        }
    }

    fun getStorage() {
        getStorageSpaceUseCase.execute()
            .flowOn(coroutineDispatcher.io())
            .onEach { result ->
                storageData = result
                onShowStorage.value = result
                migrationStatus = result.migration?.status.orEmpty()
                rootFolderId = result.rootFolder?.id.orEmpty()
                checkMigration(result.migration)
                val totalUsed =
                    result.dataUsage?.total?.formatBinarySize(
                        contextDataProviderWrapper.get().getDataContext()
                    )
                        ?: ZERO
                storageDetail = contextDataProviderWrapper.get().getResources().getString(
                    R.string.true_cloudv3_storage_detail,
                    totalUsed,
                    result.quota.formatBinarySize(contextDataProviderWrapper.get().getDataContext())
                )
                if (!autoBackupActive) {
                    autoBackupActive = true
                    checkAutoBackup()
                }
            }
            .catch { error ->
                onGetStorageError.value = Pair(error.message.orEmpty(), ACTION_GET_STORAGE)
            }
            .launchSafeIn(this)
    }

    @VisibleForTesting
    fun checkMigration(migration: DataMigrationModel?) {
        migration?.let {
            when (it.status) {
                MigrationStatus.INIT.key -> {
                    toIntroMigrationPage()
                }

                MigrationStatus.MIGRATED.key -> {
                    onShowMigrated.value = true
                }

                MigrationStatus.FAILED.key -> {
                    launchSafe {
                        it.failedDisplayTime?.let {
                            var displayTime = getMigrateFailDisplayTimeUseCase.execute()
                            if (displayTime == DEFAULT_DURATION) {
                                val estimateTime =
                                    Calendar.getInstance().timeInMillis + (it * SECOND_IN_MILLISEC)
                                setMigrateFailDisplayTimeUseCase.execute(estimateTime)
                                displayTime = estimateTime
                            }
                            onShowMigrateFail.value =
                                displayTime > Calendar.getInstance().timeInMillis
                        }
                    }
                }

                else -> {
                    // Do nothing
                }
            }
        }
    }

    fun onSelectedUploadFileResult(intent: Intent?) {
        intent?.data?.let {
            isMultipleUpload = false
            uploadMutipleFile(listOf(it))
        } ?: intent?.clipData?.let { clipData ->
            flow {
                multipleUploadErrorCount = 0
                isMultipleUpload = true
                val uploadUriList = mutableListOf<Uri>()
                for (i in START_POSITION until clipData.itemCount) {
                    clipData.getItemAt(i).uri.let { uri ->
                        uploadUriList.add(uri)
                    }
                }
                uploadMutipleFile(uploadUriList)
                emit(Unit)
            }.flowOn(coroutineDispatcher.io()).launchSafeIn(this)
        }
    }

    fun onViewCreated() {
        analyticManagerInterface.trackScreen(
            PlatformAnalyticModel().apply {
                screenClass = MainTrueCloudV3Fragment::class.java.canonicalName as String
                screenName = TrueCloudV3TrackAnalytic.SCREEN_TRUE_CLOUD
            }
        )
    }

    fun completeUpload(id: Int) {
        completeUploadUseCase.execute(id)
            ?.flowOn(coroutineDispatcher.io())
            ?.onEach {
                removeTaskUseCase.execute(id)
                onShowSnackbarComplete.value =
                    contextDataProviderWrapper.get()
                        .getString(R.string.true_cloudv3_migration_toast_success)
                getStorage()
            }?.launchSafeIn(this)
    }

    fun onResume() {
        getUploadTaskListUseCase.execute()
            .flowOn(coroutineDispatcher.io())
            .onEach { _uploadModels ->
                updateUploadTask(_uploadModels)
                if (rootFolderId != null) {
                    checkAutoBackup()
                }
            }.launchSafeIn(this)
    }

    @VisibleForTesting
    val transferlistener = object : TrueCloudV3TransferObserver.TrueCloudV3TransferListener {
        override fun onStateChanged(id: Int, state: TrueCloudV3TransferState?) {
            launchSafe {
                when (state) {
                    TrueCloudV3TransferState.COMPLETED -> {
                        completeUpload(id)
                    }

                    TrueCloudV3TransferState.CANCELED -> {
                        removeTaskUseCase.execute(id)
                    }

                    TrueCloudV3TransferState.WAITING,
                    TrueCloudV3TransferState.WAITING_FOR_NETWORK,
                    TrueCloudV3TransferState.RESUMED_WAITING -> {
                        updateTaskUploadStatusUseCase.execute(
                            id,
                            TaskStatusType.WAITING
                        )
                    }

                    TrueCloudV3TransferState.PAUSED -> {
                        updateTaskUploadStatusUseCase.execute(
                            id,
                            TaskStatusType.PAUSE
                        )
                    }

                    TrueCloudV3TransferState.FAILED -> {
                        updateTaskUploadStatusUseCase.execute(
                            id,
                            TaskStatusType.FAILED
                        )
                    }

                    else -> {
                        updateTaskUploadStatusUseCase.execute(
                            id,
                            TaskStatusType.UNKNOWN
                        )
                    }
                }
            }
        }

        override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
            Timber.i("id : $id, onProgressChanged bytesCurrent : $bytesCurrent , bytesTotal : $bytesTotal")
        }

        override fun onError(id: Int, ex: Exception?) {
            launchSafe {
                updateTaskUploadStatusUseCase.execute(id, TaskStatusType.FAILED)
            }
        }
    }

    @VisibleForTesting
    fun uploadMutipleFile(uriList: List<Uri>, uploadType: TaskActionType = TaskActionType.UPLOAD) {
        uploadFileUseCase.execute(uriList, rootFolderId.orEmpty(), uploadType).map {
            when (it.getState()) {
                TrueCloudV3TransferState.COMPLETED -> {
                    completeUpload(it.getId())
                }

                TrueCloudV3TransferState.FAILED -> {
                    updateTaskUploadStatusUseCase.execute(
                        it.getId(),
                        TaskStatusType.FAILED
                    )
                }

                else -> {
                    // Do nothing
                }
            }
            it.setTransferListener(transferlistener)
        }.catch { ex ->
            if (!isMultipleUpload || (isMultipleUpload && multipleUploadErrorCount < MAXIMUM_TOAST_ERROR)) {
                multipleUploadErrorCount++
                onUploadError.value = ex.message
            }
        }.launchSafeIn(this)
    }

    private fun openAuthenticationPage() {
        trueCloudLoginManagerInterface.login(
            object : AuthLoginListener() {
                override fun onLoginSuccess() {
                    getStorage()
                }
            },
            true
        )
    }

    fun getNextActionIntent(trueCloudMediaType: TrueCloudV3MediaType?) {
        if (
            trueCloudMediaType == null ||
            trueCloudMediaType.mediaType.isEmpty() ||
            trueCloudMediaType is TrueCloudV3MediaType.TypeContact
        ) return

        onIntentActionGetContent.value = trueCloudMediaType.mimeType
    }

    fun getNextActionCategoryIntent(trueCloudMediaType: TrueCloudV3MediaType?) {
        if (trueCloudMediaType == null) return

        var fileCategoryType = FileCategoryType.UNSUPPORTED_FORMAT
        when (trueCloudMediaType) {
            TrueCloudV3MediaType.TypeImage ->
                fileCategoryType = FileCategoryType.IMAGE

            TrueCloudV3MediaType.TypeVideo ->
                fileCategoryType = FileCategoryType.VIDEO

            TrueCloudV3MediaType.TypeAudio ->
                fileCategoryType = FileCategoryType.AUDIO

            TrueCloudV3MediaType.TypeFile ->
                fileCategoryType = FileCategoryType.OTHER

            TrueCloudV3MediaType.TypeAllFile ->
                fileCategoryType = FileCategoryType.UNSUPPORTED_FORMAT

            TrueCloudV3MediaType.TypeContact ->
                fileCategoryType = FileCategoryType.CONTACT

            TrueCloudV3MediaType.TypeOther ->
                fileCategoryType = FileCategoryType.RECENT

            else -> {
                // Do nothing
            }
        }

        if (fileCategoryType == FileCategoryType.CONTACT) {
            contactClicked(fileCategoryType.type)
        } else {
            categoryClicked(fileCategoryType)
        }
    }

    private suspend fun updateUploadTask(taskUploadModels: MutableList<TaskUploadModel>?) {
        taskUploadModels?.filter {
            it.actionType == TaskActionType.UPLOAD ||
                    it.actionType == TaskActionType.AUTO_BACKUP
        }
            ?.forEach { _taskUplaodModel ->
                val transferObserver =
                    trueCloudV3TransferUtilityProvider.getTrueCloudV3TransferObserverById(
                        contextDataProviderWrapper.get().getDataContext(),
                        _taskUplaodModel.id
                    )
                if (transferObserver != null) {
                    when (transferObserver.getState()) {
                        TrueCloudV3TransferState.COMPLETED -> {
                            completeUpload(_taskUplaodModel.id)
                        }

                        TrueCloudV3TransferState.IN_PROGRESS -> {
                            resumeTask(_taskUplaodModel.id, transferObserver)
                        }

                        TrueCloudV3TransferState.WAITING,
                        TrueCloudV3TransferState.WAITING_FOR_NETWORK,
                        TrueCloudV3TransferState.RESUMED_WAITING -> {
                            updateTaskStatus(_taskUplaodModel.id, TaskStatusType.WAITING)
                            resumeTask(_taskUplaodModel.id, transferObserver)
                        }

                        TrueCloudV3TransferState.FAILED -> {
                            updateTaskStatus(_taskUplaodModel.id, TaskStatusType.FAILED)
                        }

                        else -> {
                            // Do nothing
                        }
                    }
                } else {
                    completeUpload(_taskUplaodModel.id)
                }
            }
    }

    private suspend fun updateTaskStatus(id: Int, taskStatusType: TaskStatusType) {
        updateTaskUploadStatusUseCase.execute(
            id,
            taskStatusType
        )
    }

    private fun resumeTask(id: Int, transferObserver: TrueCloudV3TransferObserver) {
        transferObserver.setTransferListener(transferlistener)
        trueCloudV3TransferUtilityProvider.resumeTransferById(
            contextDataProviderWrapper.get().getDataContext(),
            id
        )
    }

    fun checkAutoBackup() {
        viewModelScope.launch {
            if (!dataStoreUtil.getSinglePreference(
                    stringPreferencesKey(AutoBackupViewModel.BACKUP_STATE_KEY),
                    "false"
                ).toBoolean() &&
                dataStoreUtil.getSinglePreference(
                    stringPreferencesKey(AutoBackupViewModel.lastTimeStamp),
                    "0"
                ).toLong() <= 0L
            ) {
                onShowInitAutoBackup.value = Unit
            } else {
                onAutoBackupState.value =
                    dataStoreUtil.getSinglePreference(
                        stringPreferencesKey(AutoBackupViewModel.BACKUP_STATE_KEY),
                        "false"
                    ).toBoolean()
            }
        }
    }

    fun initBackup() {
        viewModelScope.launch {
            val contentList = arrayListOf<String>()
            if (dataStoreUtil.getSinglePreference(
                    stringPreferencesKey(AutoBackupViewModel.imageBackup),
                    "false"
                ).toBoolean()
            ) {
                contentList.add(AutoBackupViewModel.imageBackup)
            }
            if (dataStoreUtil.getSinglePreference(
                    stringPreferencesKey(AutoBackupViewModel.videoBackup),
                    "false"
                ).toBoolean()
            ) {
                contentList.add(AutoBackupViewModel.videoBackup)
            }
            if (dataStoreUtil.getSinglePreference(
                    stringPreferencesKey(AutoBackupViewModel.audioBackup),
                    "false"
                ).toBoolean()
            ) {
                contentList.add(AutoBackupViewModel.audioBackup)
            }
            if (dataStoreUtil.getSinglePreference(
                    stringPreferencesKey(AutoBackupViewModel.otherBackup),
                    "false"
                ).toBoolean()
            ) {
                contentList.add(AutoBackupViewModel.otherBackup)
            }
            if (contentList.isEmpty()) return@launch
            scanFileUseCase.execute(
                contentList,
                dataStoreUtil.getSinglePreference(
                    stringPreferencesKey(AutoBackupViewModel.lastTimeStamp),
                    "0"
                ).toLong()
            )
                .flowOn(coroutineDispatcher.io())
                .onEach { files ->
                    if (files.isEmpty()) {
                        onUpdateBackupUi.value = false
                        return@onEach
                    }
                    uploadMutipleFile(
                        files,
                        TaskActionType.AUTO_BACKUP
                    )
                    dataStoreUtil.putPreference(
                        stringPreferencesKey(AutoBackupViewModel.lastTimeStamp),
                        getCurrentDateTimeUseCase.execute().firstOrNull().toString()
                    )
                    onUpdateBackupUi.value = true
                }
                .launchSafeIn(this)
        }
    }
}
