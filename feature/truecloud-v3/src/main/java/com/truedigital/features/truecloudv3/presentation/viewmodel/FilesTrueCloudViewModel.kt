package com.truedigital.features.truecloudv3.presentation.viewmodel

import android.Manifest
import android.app.NotificationManager
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType
import com.truedigital.common.share.analytics.di.AnalyticsModule
import com.truedigital.common.share.analytics.measurement.AnalyticManagerInterface
import com.truedigital.common.share.analytics.measurement.base.platform.PlatformAnalyticModel
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant
import com.truedigital.common.share.datalegacy.wrapper.ContextDataProviderWrapper
import com.truedigital.component.dialog.trueid.DialogIconType
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.extensions.launchSafe
import com.truedigital.core.extensions.launchSafeIn
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.common.FileCategoryType
import com.truedigital.features.truecloudv3.common.FileCategoryTypeManager
import com.truedigital.features.truecloudv3.common.FileMimeType
import com.truedigital.features.truecloudv3.common.FileMimeTypeManager
import com.truedigital.features.truecloudv3.common.HeaderTitle
import com.truedigital.features.truecloudv3.common.SortType
import com.truedigital.features.truecloudv3.common.StorageType
import com.truedigital.features.truecloudv3.common.TaskActionType
import com.truedigital.features.truecloudv3.common.TaskStatusType
import com.truedigital.features.truecloudv3.common.TrueCloudV3ErrorMessage
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_CATEGORY
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_FILE_LIST
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_FILE_PAGE
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_FILE_SELECTED
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_FILE_VIEW
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_FOLDER_ID
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_OPTION_FILE_MODEL
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_OPTION_FOLDER
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_OPTION_MAIN_TRASH
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_SORT_TYPE
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_TRASH
import com.truedigital.features.truecloudv3.common.TrueCloudV3MediaType
import com.truedigital.features.truecloudv3.common.TrueCloudV3TrackAnalytic
import com.truedigital.features.truecloudv3.common.TrueCloudV3TrackAnalytic.LINK_DESC_UPLOAD
import com.truedigital.features.truecloudv3.common.TrueCloudV3TrackAnalytic.getLinkDescFromMimeType
import com.truedigital.features.truecloudv3.common.TrueCloudV3TransferState
import com.truedigital.features.truecloudv3.data.repository.FileRepositoryImpl
import com.truedigital.features.truecloudv3.data.repository.UploadFileRepositoryImpl.Companion.ERROR_INIT_UPLOAD_DATA_EXCEED_LIMIT
import com.truedigital.features.truecloudv3.data.worker.TrueCloudV3DownloadWorker
import com.truedigital.features.truecloudv3.data.worker.TrueCloudV3DownloadWorker.Companion.KEY
import com.truedigital.features.truecloudv3.data.worker.TrueCloudV3DownloadWorker.Companion.PATH
import com.truedigital.features.truecloudv3.domain.model.DetailDialogModel
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
import com.truedigital.features.truecloudv3.domain.usecase.EmptyTrashDataUseCase
import com.truedigital.features.truecloudv3.domain.usecase.FileLocatorUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetStorageListUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetStorageListWithCategoryUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetTrashListUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetUploadTaskListUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetUploadTaskUseCase
import com.truedigital.features.truecloudv3.domain.usecase.MoveToTrashUseCase
import com.truedigital.features.truecloudv3.domain.usecase.NodePermission
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
import com.truedigital.features.truecloudv3.extension.convertBackupToUpload
import com.truedigital.features.truecloudv3.extension.convertFileToFolder
import com.truedigital.features.truecloudv3.extension.convertFolderToFileModel
import com.truedigital.features.truecloudv3.extension.convertToFilesModel
import com.truedigital.features.truecloudv3.extension.convertToFolderModel
import com.truedigital.features.truecloudv3.extension.getNotExistsPath
import com.truedigital.features.truecloudv3.extension.isComplete
import com.truedigital.features.truecloudv3.navigation.AllFileToImageViewer
import com.truedigital.features.truecloudv3.navigation.AllFileToMainOptionBottomSheet
import com.truedigital.features.truecloudv3.navigation.AllFileToOptionFileBottomSheet
import com.truedigital.features.truecloudv3.navigation.AllFileToPermission
import com.truedigital.features.truecloudv3.navigation.SelectFileToOptionFileSelectedBottomSheet
import com.truedigital.features.truecloudv3.navigation.TrashToMainOptionBottomSheet
import com.truedigital.features.truecloudv3.navigation.TrashToTrashBottomSheet
import com.truedigital.features.truecloudv3.navigation.router.FileTrueCloudRouterUseCase
import com.truedigital.features.truecloudv3.presentation.FilesTrueCloudFragment
import com.truedigital.features.truecloudv3.presentation.IntroPermissionFragment
import com.truedigital.features.truecloudv3.provider.TrueCloudV3TransferUtilityProvider
import com.truedigital.features.truecloudv3.widget.decoration.FilesDecoration
import com.truedigital.foundation.extension.SingleLiveEvent
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.jetbrains.annotations.VisibleForTesting
import timber.log.Timber
import java.io.File
import javax.inject.Inject
import javax.inject.Named

class FilesTrueCloudViewModel @Inject constructor(
    private val router: FileTrueCloudRouterUseCase,
    private val coroutineDispatcher: CoroutineDispatcherProvider,
    private val getStorageListUseCase: GetStorageListUseCase,
    private val getStorageListWithCategoryUseCase: GetStorageListWithCategoryUseCase,
    private val getUploadTaskListUseCase: GetUploadTaskListUseCase,
    private val getUploadTaskUseCase: GetUploadTaskUseCase,
    private val uploadQueueUseCase: UploadQueueUseCase,
    private val uploadFileUseCase: UploadFileUseCase,
    private val uploadFileWithPathUseCase: UploadFileWithPathUseCase,
    private val removeTaskUseCase: RemoveTaskUseCase,
    private val removeAllTaskUseCase: RemoveAllTaskUseCase,
    private val completeUploadUseCase: CompleteUploadUseCase,
    private val updateTaskUploadStatusUseCase: UpdateTaskUploadStatusUseCase,
    private val createFolderUserCase: CreateFolderUserCase,
    private val workManager: WorkManager,
    private val downloadCoverImageUseCase: DownloadCoverImageUseCase,
    private val retryUploadUseCase: RetryUploadUseCase,
    private val renameFileUseCase: RenameFileUseCase,
    private val deleteFileUseCase: DeleteFileUseCase,
    private val fileLocatorUseCase: FileLocatorUseCase,
    private val moveToTrashUseCase: MoveToTrashUseCase,
    private val getTrashListUseCase: GetTrashListUseCase,
    private val restoreTrashDataUseCase: RestoreTrashDataUseCase,
    private val deleteTrashDataUseCase: DeleteTrashDataUseCase,
    private val emptyTrashDataUseCase: EmptyTrashDataUseCase,
    private val trueCloudV3TransferUtilityProvider: TrueCloudV3TransferUtilityProvider,
    private val contextDataProviderWrapper: ContextDataProviderWrapper,
    @Named(AnalyticsModule.FIREBASE_ANALYTIC)
    private val analyticManagerInterface: AnalyticManagerInterface,
    private val searchInAllFilesUseCase: SearchInAllFilesUseCase,
    private val searchWithCategoryUseCase: SearchWithCategoryUseCase
) : ScopedViewModel() {

    companion object {
        private const val DEFAULT_START_PAGE = 0
        private const val DEFAULT_VERTICAL_SCROLL = 0
        private const val DEFAULT_UPLOAD_ID = 0
        private const val EMPTY_ITEM = 0
        private const val FIRST_INDEX = 0
        private const val NO_RES_TITLE = 0
        private const val MINIMUM_PAGE_SIZE = 1
        private const val MINIMUM_FOLDER_ITEM_SIZE = 1
        private const val HEADER_SIZE = 1
        private const val ONE_INDEX = 1
        private const val EMPTY_STRING = ""
        private const val EMPTY_HEADER_UPLOAD = "Upload"
        private const val EMPTY_HEADER_AUTO_BACKUP = "Auto Backup"
        private const val SPAN_COUNT_LIST = 1
        private const val SPAN_COUNT_GRID = 2
        private const val MAXIMUM_TOAST_ERROR = 3
        private const val ITEM_LIMIT = 20
        private const val ACTION_GET_ALL_FILE_LIST = "ACTION_GET_ALL_FILE_LIST"
        private const val ACTION_GET_CATEGORY_FILE_LIST = "ACTION_GET_CATEGORY_FILE_LIST"
        private const val ACTION_GET_TRASH_FILE_LIST = "ACTION_GET_TRASH_FILE_LIST"
        private const val FOLDER_OBJECT_TYPE = "FOLDER"

        @VisibleForTesting
        val stackFolderIdsCopy = mutableListOf<Pair<String, String>>()

        @VisibleForTesting
        val stackFolderIds = mutableListOf<Pair<String, String>>()
    }

    val onIntentActionGetContent = SingleLiveEvent<Array<String>>()
    val onShowUploadTaskList = MutableLiveData<List<TrueCloudFilesModel>>()
    val onShowProgressChange = MutableLiveData<Pair<List<TrueCloudFilesModel>, Int>>()
    val onLoadCoverFinished = MutableLiveData<Boolean>()
    val onTaskUpdateStatusType = MutableLiveData<Pair<List<TrueCloudFilesModel>, Int>>()
    val onBackPressed = MutableLiveData<Boolean>()
    val onShowPreview = MutableLiveData<String>()
    val onSelectUpdate = MutableLiveData<Int>()
    val onSelectAll = MutableLiveData<Boolean>()
    val onCloseSelectAll = MutableLiveData<Boolean>()
    val onShowNotificationIsOff = MutableLiveData<String>()
    val onDeleteFileSuccess = MutableLiveData<TrueCloudFilesModel>()
    val onRenameFileSuccess = MutableLiveData<Pair<List<TrueCloudFilesModel>, Int>>()
    val onClearList = MutableLiveData<List<TrueCloudFilesModel>>()
    val onAddDecoration = SingleLiveEvent<FilesDecoration>()
    val onRemoveDecoration = SingleLiveEvent<Unit>()
    val onSetExpandState = MutableLiveData<Boolean>()
    val onSetExpandBackupState = MutableLiveData<Boolean>()
    val onSetFileList = MutableLiveData<List<TrueCloudFilesModel>>()
    val onAddFileList = MutableLiveData<List<TrueCloudFilesModel>>()
    val onSetCategoryName = MutableLiveData<String>()
    val onSetHeaderTitle = MutableLiveData<String>()
    val onOpenSelectFile = SingleLiveEvent<FileCategoryType>()
    val onShowFileList = MutableLiveData<Unit>()
    val onShowDataEmpty = MutableLiveData<Unit>()
    val onShowSearchResultEmpty = MutableLiveData<Unit>()
    val onUploadError = MutableLiveData<String>()
    val onShowSnackbarComplete = SingleLiveEvent<String>()
    val onShowSnackbarError = SingleLiveEvent<String>()
    val onRefreshData = MutableLiveData<List<TrueCloudFilesModel>>()
    val onShowSelectMode = SingleLiveEvent<Unit>()
    val onShowLoading = SingleLiveEvent<Boolean>()
    val onGetListError = SingleLiveEvent<Pair<String, String>>()
    val onShowConfirmDialogDelete = SingleLiveEvent<Unit>()
    val onLocateFileSuccess = SingleLiveEvent<String>()
    val onLocateFileError = SingleLiveEvent<String>()
    val onChangeSelectOption = SingleLiveEvent<Boolean>()
    val onMoveToTrashSuccess = MutableLiveData<List<TrueCloudFilesModel>>()

    private val fileItemList = mutableListOf<TrueCloudFilesModel.File>()
    private val folderItemList = mutableListOf<TrueCloudFilesModel.Folder>()
    private var sortType = SortType.SORT_DATE_DESC

    private var page: Int = 0
    private var uploadItemSize: Int = 0
    private var itemUploadList = mutableListOf<TrueCloudFilesModel>()
    private var fileHeader: TrueCloudFilesModel.Header? = null
    private var isMultipleUpload = false
    private var multipleUploadErrorCount = 0
    private var isFirstPage: Boolean = true
    var searchString = ""

    @VisibleForTesting
    var folderItemSize: Int = 0

    @VisibleForTesting
    val transferObserverMap = mutableMapOf<Int, Boolean>()

    @VisibleForTesting
    var isListLayout: Boolean = true

    @VisibleForTesting
    var itemList = mutableListOf<TrueCloudFilesModel>()

    @VisibleForTesting
    var items = mutableListOf<TrueCloudFilesModel>()

    @VisibleForTesting
    var isSelectMode: Boolean = false

    @VisibleForTesting
    var selectAllState = false

    @VisibleForTesting
    val selectItemList = mutableListOf<TrueCloudFilesModel>()

    @VisibleForTesting
    var categoryType = FileCategoryType.UNSUPPORTED_FORMAT

    @VisibleForTesting
    var type: String = ""

    @VisibleForTesting
    val itemListId = ArrayList<String>()

    @VisibleForTesting
    var checkType: String = ""

    @VisibleForTesting
    var itemBackupList = mutableListOf<TrueCloudFilesModel>()

    fun onRequireReload(reloadRequired: Boolean?) {
        if (reloadRequired != null && reloadRequired) {
            getFilesListFirstPage()
        }
    }

    fun performClickIntroPermission() {
        router.execute(AllFileToPermission, getBundlePermissionMedia())
    }

    fun performClickToImageViewer(trueCloudFilesModel: TrueCloudFilesModel.File) {
        router.execute(
            AllFileToImageViewer,
            Bundle().apply {
                putParcelable(
                    KEY_BUNDLE_TRUE_CLOUD_FILE_VIEW,
                    trueCloudFilesModel
                )
                putParcelableArrayList(
                    KEY_BUNDLE_TRUE_CLOUD_FILE_LIST,
                    ArrayList(fileItemList)
                )
                putParcelable(
                    KEY_BUNDLE_TRUE_CLOUD_SORT_TYPE,
                    sortType
                )
                putString(
                    KEY_BUNDLE_TRUE_CLOUD_FOLDER_ID,
                    stackFolderIds.last().first
                )
                putString(
                    KEY_BUNDLE_TRUE_CLOUD_CATEGORY,
                    categoryType.type
                )
                putInt(
                    KEY_BUNDLE_TRUE_CLOUD_FILE_PAGE,
                    (folderItemSize + fileItemList.size) / ITEM_LIMIT
                )
            }
        )
    }

    fun onViewCreated(cateName: String, folderId: String) {
        categoryType = FileCategoryTypeManager.getCategoryType(cateName)
        addFolderId(folderId, contextDataProviderWrapper.get().getString(categoryType.resTitle))
        getUploadTask()
        getFilesListFirstPage()
        updateHeaderTitle()
        trackScreen(categoryType)
    }

    fun setSelectedList(itemList: ArrayList<String>, type: String, categoryName: String) {
        itemListId.addAll(itemList)
        this.type = type
        this.checkType = categoryName
    }

    fun getItemList(): ArrayList<String> {
        return itemListId
    }

    fun onClickLocate() {
        if (itemListId.isEmpty() || type == "") {
            onLocateFileError.value = when (type) {
                FileRepositoryImpl.COPY -> contextDataProviderWrapper.get()
                    .getString(R.string.true_cloudv3_file_locator_copy_error)

                FileRepositoryImpl.MOVE -> contextDataProviderWrapper.get()
                    .getString(R.string.true_cloudv3_file_locator_move_error)

                else -> ""
            }
            return
        }
        fileLocatorUseCase.execute(stackFolderIds.last().first, itemListId, type)
            .flowOn(coroutineDispatcher.io())
            .onEach {
                onLocateFileSuccess.value = when (type) {
                    FileRepositoryImpl.COPY -> contextDataProviderWrapper.get()
                        .getString(R.string.true_cloudv3_file_locator_copy_success)

                    FileRepositoryImpl.MOVE -> contextDataProviderWrapper.get()
                        .getString(R.string.true_cloudv3_file_locator_move_success)

                    else -> ""
                }
            }
            .catch {
                onLocateFileError.value = when (type) {
                    FileRepositoryImpl.COPY -> contextDataProviderWrapper.get()
                        .getString(R.string.true_cloudv3_file_locator_copy_error)

                    FileRepositoryImpl.MOVE -> contextDataProviderWrapper.get()
                        .getString(R.string.true_cloudv3_file_locator_move_error)

                    else -> ""
                }
            }
            .launchSafeIn(this)
    }

    fun getLocatorType() = this.type

    fun onGetTrashData(folderId: String) {
        addFolderId(folderId, contextDataProviderWrapper.get().getString(categoryType.resTitle))
        getTrashStorageList()
    }

    fun removeLastFolder() {
        stackFolderIds.removeAt(stackFolderIds.lastIndex)
    }

    fun addSelectItem(trueCloudV3Object: TrueCloudFilesModel) {
        selectItemList.add(trueCloudV3Object)
        updateSelectedList()
    }

    fun selectAllItem() {
        if (selectAllState) {
            selectAllState = false
            unselectAllItem()
        } else {
            selectAllState = true
            selectItemList.clear()
            selectItemList.addAll(fileItemList)
            selectItemList.addAll(folderItemList)
            updateSelectedList()
            onSelectAll.value = true
        }
    }

    fun onClickChangeLayout() {
        when {
            isListLayout -> {
                val filesDecorator = FilesDecoration(
                    context = contextDataProviderWrapper.get().getDataContext(),
                    resId = R.dimen.true_cloudv3_spacing_s,
                    uploadItemSize = uploadItemSize,
                    folderItemSize = folderItemSize
                )
                onAddDecoration.value = filesDecorator
            }

            else -> {
                onRemoveDecoration.value = Unit
            }
        }
        isListLayout = !isListLayout
    }

    fun onClickHeaderMoreOption() {
        router.execute(
            AllFileToMainOptionBottomSheet,
            Bundle().apply {
                putParcelable(
                    KEY_BUNDLE_TRUE_CLOUD_SORT_TYPE,
                    sortType
                )
            }
        )
    }

    fun onClickHeaderTrashMoreOption(isTrashView: Boolean = false) {
        router.execute(
            TrashToMainOptionBottomSheet,
            Bundle().apply {
                putParcelable(
                    KEY_BUNDLE_TRUE_CLOUD_SORT_TYPE,
                    sortType
                )
                putBoolean(
                    KEY_BUNDLE_TRUE_CLOUD_TRASH,
                    isTrashView
                )
            }
        )
    }

    fun onClickFolderMoreOption(folder: TrueCloudFilesModel.Folder) {
        openOptionFileBottomSheet(
            folder.convertFolderToFileModel(
                contextDataProviderWrapper.get().getDataContext()
            ),
            isFolder = true
        )
    }

    fun onClickItemMoreOption(file: TrueCloudFilesModel.File) {
        openOptionFileBottomSheet(file)
    }

    fun onLongClickItemMoreOption(file: TrueCloudFilesModel.File) {
        openOptionFileBottomSheet(file)
    }

    fun onClickTrashFileOption(file: TrueCloudFilesModel.File) {
        openTrashBottomSheet(mutableListOf(file))
    }

    fun onClickTrashFolderOption(folder: TrueCloudFilesModel.Folder) {
        openTrashBottomSheet(mutableListOf(folder))
    }

    private fun openTrashBottomSheet(file: MutableList<TrueCloudFilesModel>) {
        val data = file.filterIsInstance<TrueCloudFilesModel.File>()
            .toMutableList()
        file.filterIsInstance<TrueCloudFilesModel.Folder>()
            .map { it.convertFolderToFileModel(contextDataProviderWrapper.get().getDataContext()) }
            .forEach { data.add(it) }
        router.execute(
            TrashToTrashBottomSheet,
            Bundle().apply {
                putParcelableArrayList(
                    KEY_BUNDLE_TRUE_CLOUD_OPTION_MAIN_TRASH,
                    ArrayList(data)
                )
            }
        )
    }

    fun onOptionClickSelect(status: Boolean) {
        if (status) {
            checkSelectOptionVisibility()
            onShowSelectMode.value = Unit
            isSelectMode = true
        }
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_EVENT_NAME to TrueCloudV3TrackAnalytic.EVENT_CLICK,
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.LINK_TYPE_BOTTOM_SHEET,
                MeasurementConstant.Key.KEY_LINK_DESC to TrueCloudV3TrackAnalytic.LINK_DESC_SELECT
            )
        )
    }

    fun onLocateFinish(status: Boolean) {
        if (!status) {
            onRestoreStackIds()
        }
        closeSelectedItem()
    }

    fun checkSelectOptionVisibility() {
        onChangeSelectOption.value = categoryType != FileCategoryType.CONTACT
    }

    fun unselectAllItem() {
        selectItemList.clear()
        updateSelectedList()
        onSelectAll.value = false
    }

    fun onClickDelete() {
        onShowConfirmDialogDelete.value = Unit
    }

    fun removeSelectItem(trueCloudV3Object: TrueCloudFilesModel) {
        selectItemList.remove(trueCloudV3Object)
        updateSelectedList()
    }

    fun getSelectedItem(): MutableList<TrueCloudFilesModel> {
        return selectItemList
    }

    fun closeSelectedItem() {
        selectAllState = false
        unselectAllItem()
        onCloseSelectAll.value = true
        isSelectMode = false
    }

    fun onRestoreStackIds() {
        stackFolderIds.clear()
        stackFolderIds.addAll(stackFolderIdsCopy)
        getFilesListFirstPage()
    }

    fun onClickSelectOption(isTrashView: Boolean = false) {
        stackFolderIdsCopy.clear()
        stackFolderIdsCopy.addAll(stackFolderIds)
        if (isTrashView) {
            openTrashBottomSheet(selectItemList)
        }
        openFileSelectedBottomSheet(selectItemList)
    }

    private fun openFileSelectedBottomSheet(selectItemList: MutableList<TrueCloudFilesModel>) {
        val data = selectItemList.filterIsInstance<TrueCloudFilesModel.File>()
            .toMutableList()
        selectItemList.filterIsInstance<TrueCloudFilesModel.Folder>()
            .map { it.convertFolderToFileModel(contextDataProviderWrapper.get().getDataContext()) }
            .forEach { data.add(it) }
        router.execute(
            SelectFileToOptionFileSelectedBottomSheet,
            Bundle().apply {
                putParcelableArrayList(
                    KEY_BUNDLE_TRUE_CLOUD_FILE_SELECTED,
                    ArrayList(data)
                )
                putString(
                    KEY_BUNDLE_TRUE_CLOUD_FOLDER_ID,
                    stackFolderIds.first().first
                )
                putString(
                    KEY_BUNDLE_TRUE_CLOUD_CATEGORY,
                    categoryType.type
                )
            }
        )
    }

    fun createFolder(folderName: String?) {
        folderName?.let { _folderName ->
            createFolderUserCase.execute(stackFolderIds.last().first, _folderName)
                .flowOn(coroutineDispatcher.io())
                .onEach {
                    getFilesListFirstPage()
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

    fun onClickUpload() {
        onOpenSelectFile.value = categoryType
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_EVENT_NAME to TrueCloudV3TrackAnalytic.EVENT_CLICK,
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.LINK_TYPE_BROWSE,
                MeasurementConstant.Key.KEY_LINK_DESC to LINK_DESC_UPLOAD
            )
        )
    }

    fun onSortByClick(selectedSortType: SortType?) {
        selectedSortType?.let {
            sortType = selectedSortType
            getFilesListFirstPage()
        }
    }

    fun onSearchStringInput(searchInput: String) {
        searchString = searchInput
        items.clear()
        itemList.clear()
        isFirstPage = true
        getFilesListFirstPage()
    }

    fun onTrashSortByClick(selectedSortType: SortType?) {
        selectedSortType?.let {
            sortType = selectedSortType
            getTrashStorageList()
        }
    }

    private fun getFilesListFirstPage() {
        setFirstPage()
        getFilesList()
    }

    fun getNextActionIntent(trueCloudMediaType: TrueCloudV3MediaType?) {
        if (trueCloudMediaType == null) return

        when (trueCloudMediaType) {
            is TrueCloudV3MediaType.TypeImage,
            is TrueCloudV3MediaType.TypeVideo,
            is TrueCloudV3MediaType.TypeAudio,
            is TrueCloudV3MediaType.TypeFile,
            is TrueCloudV3MediaType.TypeAllFile -> {
                onIntentActionGetContent.value = trueCloudMediaType.mimeType
            }

            else -> {
                // Do Nothing
            }
        }
    }

    fun onScrolled(dy: Int, layoutManager: GridLayoutManager?) {
        layoutManager?.let {
            if (
                dy > DEFAULT_VERTICAL_SCROLL &&
                layoutManager.findLastCompletelyVisibleItemPosition() + ONE_INDEX ==
                layoutManager.itemCount
            ) {
                isFirstPage = false
                getFilesList()
            }
        }
    }

    fun onClickPause(taskUploadModel: TrueCloudFilesModel.Upload) {
        trueCloudV3TransferUtilityProvider.pauseTransferById(
            contextDataProviderWrapper.get().getDataContext(),
            taskUploadModel.id
        )
        launchSafe {
            updateTaskUploadStatusUseCase.execute(
                taskUploadModel.id,
                TaskStatusType.PAUSE
            )
            refreshTask()
        }
    }

    fun onClickCancel(taskUploadModel: TrueCloudFilesModel.Upload) {
        trueCloudV3TransferUtilityProvider.cancelTransferById(
            contextDataProviderWrapper.get().getDataContext(),
            taskUploadModel.id
        )
        flow {
            removeTaskUseCase.execute(taskUploadModel.id)
            refreshTask()
            emit(Unit)
        }
            .flowOn(coroutineDispatcher.io())
            .launchSafeIn(this)

        taskUploadModel.objectId?.let { _id ->
            deleteFileUseCase.execute(_id)
                .launchSafeIn(this)
        }
    }

    fun onClickCancelAll() {
        trueCloudV3TransferUtilityProvider.getDefaultTransferUtility(
            contextDataProviderWrapper.get().getDataContext()
        )
            .cancelAllWithType(
                TransferType.UPLOAD
            )
        flow {
            removeAllTaskUseCase.execute()
            refreshTask()
            emit(Unit)
        }
            .flowOn(coroutineDispatcher.io())
            .launchSafeIn(this)

        getUploadTaskListUseCase.execute()
            .flowOn(coroutineDispatcher.io())
            .filterNotNull()
            .map { taskList ->
                for (task in taskList) {
                    task.objectId?.let { _id ->
                        deleteFileUseCase.execute(_id)
                            .launchSafeIn(this)
                    }
                }
            }.launchSafeIn(this)
    }

    fun onClickPauseAllBackup() {
        itemBackupList.filterIsInstance<TrueCloudFilesModel.AutoBackup>()
            .forEach {
                onClickPause(it.convertBackupToUpload())
            }
    }

    fun onClickCancelAllBackup() {
        itemBackupList.filterIsInstance<TrueCloudFilesModel.AutoBackup>()
            .forEach {
                onClickCancel(it.convertBackupToUpload())
            }
    }

    fun onClickResumeAllBackup() {
        itemBackupList.filterIsInstance<TrueCloudFilesModel.AutoBackup>()
            .forEach {
                onClickRetry(it.convertBackupToUpload())
            }
    }

    fun onExpandClicked(status: Boolean) {
        onSetExpandState.value = !status
    }

    fun onExpandBackupClicked(status: Boolean) {
        onSetExpandBackupState.value = !status
    }

    fun onClickRetry(taskUploadModel: TrueCloudFilesModel.Upload) {
        val taskData = trueCloudV3TransferUtilityProvider.getTrueCloudV3TransferObserverById(
            contextDataProviderWrapper.get().getDataContext(),
            taskUploadModel.id
        )
        if (taskData != null) {
            when (taskUploadModel.status) {
                TaskStatusType.PAUSE -> {
                    if (
                        taskUploadModel.actionType == TaskActionType.UPLOAD ||
                        taskUploadModel.actionType == TaskActionType.AUTO_BACKUP
                    ) {
                        trueCloudV3TransferUtilityProvider.resumeTransferById(
                            contextDataProviderWrapper.get().getDataContext(),
                            taskUploadModel.id
                        )?.setTransferListener(transferlistener)
                    }
                    launchSafe {
                        updateTaskUploadStatusUseCase.execute(
                            taskUploadModel.id,
                            TaskStatusType.IN_PROGRESS
                        )
                        refreshTask()
                    }
                }

                TaskStatusType.COMPLETE_API_FAILED -> {
                    completeUpload(taskUploadModel.id)
                }

                else -> {
                    trueCloudV3TransferUtilityProvider.cancelTransferById(
                        contextDataProviderWrapper.get().getDataContext(),
                        taskUploadModel.id
                    )
                    taskUploadModel.objectId?.let {
                        retryUpload(it)
                    } ?: uploadWithPath(taskUploadModel.path)
                }
            }
        } else {
            taskUploadModel.objectId?.let {
                retryUpload(it)
            } ?: uploadWithPath(taskUploadModel.path)
        }
    }

    fun onFolderItemClick(trueCloudV3Object: TrueCloudFilesModel.Folder) {
        trueCloudV3Object.id?.let {
            addFolderId(it, trueCloudV3Object.name ?: EMPTY_STRING)
            getFilesListFirstPage()
        }
    }

    fun onDownloadFile(trueCloudV3Object: TrueCloudFilesModel.File?) {
        trueCloudV3Object?.let { _trueCloudV3Object ->
            val path =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .path + "/TrueCloud/" + _trueCloudV3Object.name?.replace(
                    " ",
                    "_"
                )

            _trueCloudV3Object.id?.let {
                download(it, path.getNotExistsPath())
            }
            trackEventDownload(_trueCloudV3Object.fileMimeType)
        }
    }

    fun onClickBack() {
        if (isSelectMode) {
            closeSelectedItem()
        } else {
            handleOnBack()
        }
    }

    fun handleOnBack() {
        if (stackFolderIds.size == MINIMUM_FOLDER_ITEM_SIZE) {
            stackFolderIds.clear()
            onBackPressed.value = true
        } else {
            removeLastFolder()
            getFilesListFirstPage()
        }
    }

    fun completeUpload(id: Int) {
        completeUploadUseCase.execute(id)
            ?.flowOn(coroutineDispatcher.io())
            ?.onEach {
                removeTaskUseCase.execute(id)
                onShowSnackbarComplete.value = contextDataProviderWrapper.get()
                    .getString(R.string.true_cloudv3_migration_toast_success)
            }
            ?.map {
                if (stackFolderIds.last().first.equals(it.parentObjectId)) {
                    getFilesListFirstPage()
                }
                trackEventUploadSuccess(it.mimeType)
            }
            ?.catch {
                updateTaskUploadStatusUseCase.execute(
                    id,
                    TaskStatusType.COMPLETE_API_FAILED
                )
            }
            ?.launchSafeIn(this)
    }

    fun rename(trueCloudFilesModel: TrueCloudFilesModel.File?) {
        trueCloudFilesModel?.id?.let { _id ->
            val name = trueCloudFilesModel.name ?: EMPTY_STRING
            renameFileUseCase.execute(_id, name)
                .flowOn(coroutineDispatcher.io())
                .map {
                    onRenameFileSuccess.value = getListUpdateFileName(it.getFilesModel())
                }
                .launchSafeIn(this)
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
                for (i in FIRST_INDEX until clipData.itemCount) {
                    clipData.getItemAt(i).uri.let { uri ->
                        uploadUriList.add(uri)
                    }
                }
                uploadMutipleFile(uploadUriList)
                emit(Unit)
            }.flowOn(coroutineDispatcher.io()).launchSafeIn(this)
        }
    }

    fun onDeleteGroupFile(fileModel: MutableList<TrueCloudFilesModel.File>) {
        val fileList = mutableListOf<TrueCloudFilesModel>()
        fileModel.forEach { _file ->
            fileList.add(
                if (_file.objectType == FOLDER_OBJECT_TYPE) {
                    _file.convertFileToFolder(contextDataProviderWrapper.get().getDataContext())
                } else {
                    _file
                }
            )
            trackEventDelete(_file.fileMimeType)
        }
        val filesId = fileModel.mapNotNull { it.id }
        moveToTrashUseCase.execute(filesId)
            .flowOn(coroutineDispatcher.io())
            .onEach {
                items.clear()
                itemList.clear()
                onMoveToTrashSuccess.value = fileList
                getFilesListFirstPage()
                if (fileList.size > 1) {
                    onShowSnackbarComplete.value = String.format(
                        contextDataProviderWrapper.get()
                            .getString(R.string.true_cloudv3_trash_multiple_file),
                        fileList.size
                    )
                } else {
                    onShowSnackbarComplete.value = contextDataProviderWrapper.get()
                        .getString(R.string.true_cloudv3_delete_item_successfully)
                }
            }
            .launchSafeIn(this)
        closeSelectedItem()
    }

    fun onDeleteFile(fileModel: TrueCloudFilesModel.File?) {
        fileModel?.let { _fileModel ->
            _fileModel.id?.let { _id ->
                deleteFileUseCase.execute(_id)
                    .onEach {
                        items.remove(_fileModel)
                        itemList.remove(_fileModel)
                        onDeleteFileSuccess.value = _fileModel
                        onShowSnackbarComplete.value = contextDataProviderWrapper.get()
                            .getString(R.string.true_cloudv3_delete_item_successfully)
                        setViewState(items.count())
                    }
                    .flowOn(coroutineDispatcher.main())
                    .launchSafeIn(this)
            }
            trackEventDelete(_fileModel.fileMimeType)
        }
    }

    fun emptyTrash() {
        if (items.isEmpty()) {
            onShowSnackbarError.value = contextDataProviderWrapper.get()
                .getString(R.string.true_cloudv3_empty_trash_empty)
            return
        }
        val fileId = mutableListOf<String>()
        items.forEach { _file ->
            when (_file) {
                is TrueCloudFilesModel.File -> fileId.add(_file.id!!)
                is TrueCloudFilesModel.Folder -> fileId.add(_file.id!!)
                else -> {}
            }
        }
        emptyTrashDataUseCase.execute(fileId.toList())
            .flowOn(coroutineDispatcher.io())
            .onEach {
                onMoveToTrashSuccess.value = items
                items.clear()
                itemList.clear()
                onShowSnackbarComplete.value = contextDataProviderWrapper.get()
                    .getString(R.string.true_cloudv3_empty_trash_success)
                setViewState(items.count())
            }.catch {
                onShowSnackbarError.value = contextDataProviderWrapper.get()
                    .getString(R.string.true_cloudv3_empty_trash_error)
            }
            .launchSafeIn(this)
    }

    fun permanenceDelete(fileModel: MutableList<TrueCloudFilesModel.File>) {
        val fileList = mutableListOf<TrueCloudFilesModel>()
        fileModel.forEach { _file ->
            fileList.add(
                if (_file.objectType == FOLDER_OBJECT_TYPE) {
                    _file.convertFileToFolder(contextDataProviderWrapper.get().getDataContext())
                } else {
                    _file
                }
            )
        }
        val filesId = fileModel.mapNotNull { it.id }
        deleteTrashDataUseCase.execute(filesId)
            .flowOn(coroutineDispatcher.io())
            .onEach {
                fileList.forEach { _file ->
                    items.remove(_file)
                    itemList.remove(_file)
                }
                onMoveToTrashSuccess.value = fileList
                if (fileList.size > 1) {
                    onShowSnackbarComplete.value = String.format(
                        contextDataProviderWrapper.get()
                            .getString(R.string.true_cloudv3_empty_trash_delete_permanent_multiple_success),
                        fileList.size
                    )
                } else {
                    onShowSnackbarComplete.value = contextDataProviderWrapper.get()
                        .getString(R.string.true_cloudv3_empty_trash_delete_permanent_success)
                }
                setViewState(items.count())
            }
            .catch {
                onShowSnackbarError.value = contextDataProviderWrapper.get()
                    .getString(R.string.true_cloudv3_empty_trash_delete_permanent_failed)
            }
            .launchSafeIn(this)
        closeSelectedItem()
    }

    fun restoreFile(fileModel: MutableList<TrueCloudFilesModel.File>) {
        val fileList = mutableListOf<TrueCloudFilesModel>()
        fileModel.forEach { _file ->
            fileList.add(
                if (_file.objectType == FOLDER_OBJECT_TYPE) {
                    _file.convertFileToFolder(contextDataProviderWrapper.get().getDataContext())
                } else {
                    _file
                }
            )
        }
        val filesId = fileModel.mapNotNull { it.id }
        restoreTrashDataUseCase.execute(filesId)
            .flowOn(coroutineDispatcher.io())
            .onEach {
                fileList.forEach { _file ->
                    items.remove(_file)
                    itemList.remove(_file)
                }
                onMoveToTrashSuccess.value = fileList
                if (fileList.size > 1) {
                    onShowSnackbarComplete.value = String.format(
                        contextDataProviderWrapper.get()
                            .getString(R.string.true_cloudv3_empty_trash_restore_multiple_success),
                        fileList.size
                    )
                } else {
                    onShowSnackbarComplete.value = contextDataProviderWrapper.get()
                        .getString(R.string.true_cloudv3_empty_trash_restore_success)
                }
                setViewState(items.count())
            }
            .catch {
                onShowSnackbarError.value = contextDataProviderWrapper.get()
                    .getString(R.string.true_cloudv3_empty_trash_restore_error)
            }
            .launchSafeIn(this)
        closeSelectedItem()
    }

    fun getSpanSize(position: Int, isShowHorizontalLayout: Boolean): Int {
        if (
            items.isNotEmpty() &&
            items.size > position
        ) {
            return when (items[position]) {
                is TrueCloudFilesModel.Upload -> {
                    if (isShowHorizontalLayout) SPAN_COUNT_GRID else SPAN_COUNT_LIST
                }

                is TrueCloudFilesModel.Folder -> {
                    if (isShowHorizontalLayout) SPAN_COUNT_GRID else SPAN_COUNT_LIST
                }

                is TrueCloudFilesModel.File -> {
                    if (isShowHorizontalLayout) SPAN_COUNT_GRID else SPAN_COUNT_LIST
                }

                is TrueCloudFilesModel.AutoBackup -> {
                    if (isShowHorizontalLayout) SPAN_COUNT_GRID else SPAN_COUNT_LIST
                }

                is TrueCloudFilesModel.Header -> SPAN_COUNT_GRID
            }
        } else {
            return if (isShowHorizontalLayout) SPAN_COUNT_GRID else SPAN_COUNT_LIST
        }
    }

    fun checkRetryState(action: String) {
        when (action) {
            ACTION_GET_ALL_FILE_LIST -> getAllFileStorageList()
            ACTION_GET_CATEGORY_FILE_LIST -> getFileCategoryStorageList()
            ACTION_GET_TRASH_FILE_LIST -> getTrashStorageList()
            else -> {
                // Do nothing
            }
        }
    }

    @VisibleForTesting
    fun downloadCoverImage(key: String) {
        val cachePath =
            contextDataProviderWrapper.get()
                .getDataContext().cacheDir.absolutePath + "/true_cloud_cache/$key.jpg"
        if (File(cachePath).exists()) return
        downloadCoverImageUseCase.execute(
            key,
            cachePath
        )
            .flowOn(coroutineDispatcher.io())
            .onEach { transferObserver ->
                transferObserver.setTransferListener(
                    object : TrueCloudV3TransferObserver.TrueCloudV3TransferListener {
                        override fun onStateChanged(
                            id: Int,
                            state: TrueCloudV3TransferState?
                        ) {
                            if (state == TrueCloudV3TransferState.COMPLETED) {
                                itemList.filterIsInstance<TrueCloudFilesModel.File>()
                                    .filter { it.coverImageKey == key }
                                    .map {
                                        it.imageCoverAlready = true
                                    }
                                onLoadCoverFinished.value = true
                            }
                        }

                        override fun onProgressChanged(
                            id: Int,
                            bytesCurrent: Long,
                            bytesTotal: Long
                        ) {
                            // DO NOTHING
                        }

                        override fun onError(id: Int, ex: Exception?) {
                            // DO NOTHING
                        }
                    }
                )
            }
            .catch { ex ->
                ex.printStackTrace()
            }.launchSafeIn(this)
    }

    private fun openOptionFileBottomSheet(
        trueCloudFilesModel: TrueCloudFilesModel.File,
        isFolder: Boolean = false
    ) {
        router.execute(
            AllFileToOptionFileBottomSheet,
            Bundle().apply {
                putParcelable(
                    KEY_BUNDLE_TRUE_CLOUD_OPTION_FILE_MODEL,
                    trueCloudFilesModel
                )
                putBoolean(
                    KEY_BUNDLE_TRUE_CLOUD_OPTION_FOLDER,
                    isFolder
                )
            }
        )
    }

    private fun addFolderId(folderId: String, title: String) {
        if (stackFolderIds.contains(Pair(folderId, title))) return
        stackFolderIds.add(Pair(folderId, title))
        onClearList.value = getFileListNewFolder()
    }

    private fun setViewState(itemCount: Int) {
        if (itemCount > EMPTY_ITEM) {
            onShowFileList.value = Unit
        } else {
            if (getCurrentPage() > DEFAULT_START_PAGE) {
                setFirstPage()
            }
            if (searchString.isEmpty()) {
                onShowDataEmpty.value = Unit
            } else {
                onShowSearchResultEmpty.value = Unit
            }
        }
    }

    private fun areNotificationsEnabled(notificationManager: NotificationManagerCompat) = when {
        notificationManager.areNotificationsEnabled().not() -> false
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
            notificationManager.notificationChannels.firstOrNull { channel ->
                channel.importance == NotificationManager.IMPORTANCE_NONE
            } == null
        }

        else -> true
    }

    private fun updateHeaderTitle() {
        when {
            categoryType != FileCategoryType.UNSUPPORTED_FORMAT -> {
                onSetHeaderTitle.value =
                    contextDataProviderWrapper.get().getDataContext()
                        .getString(categoryType.resTitle)
            }

            else -> {
                onSetHeaderTitle.value = stackFolderIds.last().second ?: EMPTY_STRING
            }
        }
    }

    @VisibleForTesting
    fun getUploadTask() {
        getUploadTaskListUseCase.execute()
            .flowOn(coroutineDispatcher.io())
            .onEach { result ->
                val itemList = result?.map {
                    if (it.actionType == TaskActionType.UPLOAD) {
                        it.getUploadFilesModel()
                    } else {
                        it.getAutoBackupFileModel()
                    }
                }?.toMutableList() ?: mutableListOf()
                uploadItemSize = if (itemList.isNotEmpty()) {
                    itemList.size + HEADER_SIZE
                } else {
                    EMPTY_ITEM
                }
                refreshGridDecoration()
                itemList.filterIsInstance<TrueCloudFilesModel.Upload>()
                    .let { list ->
                        onShowUploadTaskList.value = getUploadList(list)
                        list.filter { !transferObserverMap.containsKey(it.id) && it.id != DEFAULT_UPLOAD_ID }
                        list.map {
                            setTransferState(it.id)
                            transferObserverMap[it.id] = true
                        }
                    }
                itemList.filterIsInstance<TrueCloudFilesModel.AutoBackup>()
                    .let { list ->
                        onShowUploadTaskList.value = getAutoBackupList(list)
                        list.filter { !transferObserverMap.containsKey(it.id) && it.id != DEFAULT_UPLOAD_ID }
                        list.map {
                            setTransferState(it.id)
                            transferObserverMap[it.id] = true
                        }
                    }
                setViewState(items.count())
                uploadQueueList(result)
                if (itemList.isEmpty()) {
                    transferObserverMap.clear()
                    clearAllUploadTask()
                }
            }.launchSafeIn(this)
    }

    private fun uploadQueueList(uploadModels: MutableList<TaskUploadModel>?) {
        val task = uploadModels?.filter {
            it.status == TaskStatusType.IN_QUEUE ||
                it.status == TaskStatusType.WAITING || it.id == 0
        }
        task ?: return
        onShowLoading.value = true
        uploadQueueUseCase.execute(task).map {
            Timber.i("HIT OUT  ${it.getState()} id ${it.getId()}")
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
            val taskUploadModel = getUploadTaskUseCase.execute(it.getId())
            when (taskUploadModel?.status) {
                TaskStatusType.PAUSE -> {
                    trueCloudV3TransferUtilityProvider.pauseTransferById(
                        contextDataProviderWrapper.get().getDataContext(),
                        taskUploadModel.id
                    )
                }

                else -> {
                    // Do Nothing
                }
            }
            it.setTransferListener(transferlistener)
            refreshTask()
            onShowLoading.value = false
        }.catch { ex ->
            if (!isMultipleUpload || (isMultipleUpload && multipleUploadErrorCount < MAXIMUM_TOAST_ERROR)) {
                multipleUploadErrorCount++
                val message = if (ERROR_INIT_UPLOAD_DATA_EXCEED_LIMIT == ex.message) {
                    contextDataProviderWrapper.get()
                        .getString(R.string.true_cloudv3_select_file_exceeded_storage_size)
                } else {
                    ex.message
                }
                onUploadError.postValue(message.orEmpty())
            }
            onShowLoading.postValue(false)
        }
            .flowOn(coroutineDispatcher.io())
            .launchSafeIn(this)
    }

    private fun getFilesList() {
        if (categoryType != FileCategoryType.UNSUPPORTED_FORMAT) {
            if (searchString.isNotEmpty()) {
                getFileSearchCategoryStorageList()
            } else {
                getFileCategoryStorageList()
            }
        } else {
            if (searchString.isNotEmpty()) {
                getFileSearchStorageList()
            } else {
                getAllFileStorageList()
            }
        }
    }

    private fun getBundlePermissionMedia(): Bundle {
        return Bundle().apply {
            putParcelable(IntroPermissionFragment.KEY_TRUE_CLOUD_TYPE, categoryType.mediaType)
            putStringArray(
                IntroPermissionFragment.KEY_PERMISSION_LIST,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            )
            putParcelable(
                IntroPermissionFragment.KEY_DIALOG_MODEL,
                DetailDialogModel(
                    nodePermission = NodePermission.STORAGE,
                    iconType = DialogIconType.FOLDER,
                    title = contextDataProviderWrapper.get()
                        .getString(R.string.true_cloudv3_dialog_title_file),
                    subTitle = contextDataProviderWrapper.get()
                        .getString(R.string.true_cloudv3_dialog_subtitle_file)
                )
            )
        }
    }

    private fun getFileSearchStorageList() {
        searchInAllFilesUseCase.execute(searchString, sortType, page)
            .flowOn(coroutineDispatcher.io())
            .onEach { result ->
                val itemList = mutableListOf<TrueCloudFilesModel>()
                val folderList = getFolderList(result)
                val fileList = getFileList(result)
                itemList.addAll(folderList)
                itemList.addAll(fileList)
                if (isFirstPage) {
                    folderItemSize = folderList.size
                } else {
                    folderItemSize += folderList.size
                }
                fileItemList.addAll(fileList)
                refreshGridDecoration()
                showFileList(itemList)
                handleCoverImage(fileList)
                page += MINIMUM_PAGE_SIZE
                refreshTask()
            }
            .catch { _error ->
                handleGetAllFlieError(_error.message.orEmpty())
            }
            .launchSafeIn(this)
    }

    private fun getAllFileStorageList() {
        getStorageListUseCase.execute(stackFolderIds.last().first, sortType, page)
            .flowOn(coroutineDispatcher.io())
            .onEach { result ->
                val itemList = mutableListOf<TrueCloudFilesModel>()
                val folderList = getFolderList(result)
                val fileList = getFileList(result)
                itemList.addAll(folderList)
                itemList.addAll(fileList)
                if (isFirstPage) {
                    folderItemSize = folderList.size
                } else {
                    folderItemSize += folderList.size
                }
                fileItemList.addAll(fileList)
                folderItemList.addAll(folderList)
                refreshGridDecoration()
                showFileList(itemList)
                handleCoverImage(fileList)
                page += MINIMUM_PAGE_SIZE
                refreshTask()
            }
            .catch { _error ->
                handleGetAllFlieError(_error.message.orEmpty())
            }
            .launchSafeIn(this)
    }

    private fun getTrashStorageList() {
        getTrashListUseCase.execute()
            .flowOn(coroutineDispatcher.io())
            .onEach { result ->
                val itemList = mutableListOf<TrueCloudFilesModel>()
                val folderList = getFolderList(result)
                val fileList = getFileList(result)
                itemList.addAll(folderList)
                itemList.addAll(fileList)
                if (isFirstPage) {
                    folderItemSize = folderList.size
                } else {
                    folderItemSize += folderList.size
                }
                fileItemList.addAll(fileList)
                folderItemList.addAll(folderList)
                refreshGridDecoration()
                showFileList(itemList)
                handleCoverImage(fileList)
                page += MINIMUM_PAGE_SIZE
                refreshTask()
            }
            .catch { _error ->
                handleGetTrashFileError(_error.message.orEmpty())
            }
            .launchSafeIn(this)
    }

    private fun refreshGridDecoration() {
        if (!isListLayout) {
            onRemoveDecoration.value = Unit
            onAddDecoration.value = FilesDecoration(
                context = contextDataProviderWrapper.get().getDataContext(),
                resId = R.dimen.true_cloudv3_spacing_s,
                uploadItemSize = uploadItemSize,
                folderItemSize = folderItemSize
            )
        }
    }

    private fun handleGetAllFlieError(errorMessage: String) {
        if (errorMessage != TrueCloudV3ErrorMessage.ERROR_STORAGE_NULL) {
            onGetListError.postValue(Pair(errorMessage, ACTION_GET_ALL_FILE_LIST))
        }
    }

    private fun handleGetCategoryFileError(errorMessage: String) {
        if (errorMessage != TrueCloudV3ErrorMessage.ERROR_STORAGE_NULL) {
            onGetListError.postValue(Pair(errorMessage, ACTION_GET_CATEGORY_FILE_LIST))
        }
    }

    private fun handleGetTrashFileError(errorMessage: String) {
        if (errorMessage != TrueCloudV3ErrorMessage.ERROR_STORAGE_NULL) {
            onGetListError.postValue(Pair(errorMessage, ACTION_GET_TRASH_FILE_LIST))
        }
    }

    private fun showFileList(filesModelList: List<TrueCloudFilesModel>) {
        val headerTitle = HeaderTitle(stackFolderIds.last().second)
        val categoryName =
            if (headerTitle.resTitle == NO_RES_TITLE) headerTitle.title else contextDataProviderWrapper.get()
                .getString(
                    headerTitle.resTitle
                )
        if (filesModelList.isNotEmpty()) {
            val folderList = mutableListOf<TrueCloudFilesModel.Folder>()
            val fileList = mutableListOf<TrueCloudFilesModel.File>()
            filesModelList.filterIsInstance<TrueCloudFilesModel.Folder>()
                .let { folder ->
                    folderList.addAll(folder)
                }
            filesModelList.filterIsInstance<TrueCloudFilesModel.File>()
                .let { file ->
                    fileList.addAll(file)
                }
            when {
                getCurrentPage() == DEFAULT_START_PAGE -> onSetFileList.value = getFilesList(
                    categoryName,
                    folderList,
                    fileList
                )

                else -> {
                    itemList.addAll(fileList)
                    items.addAll(fileList)
                    onAddFileList.value = fileList
                }
            }
        } else {
            if (getCurrentPage() == DEFAULT_START_PAGE && searchString.isEmpty()) {
                onRefreshData.value = refreshData()
            } else {
                onAddFileList.value = listOf()
            }
        }
        onSetCategoryName.value = categoryName
        setViewState(items.count())
    }

    private fun getFileSearchCategoryStorageList() {
        searchWithCategoryUseCase.execute(
            searchString = searchString,
            sortType = sortType,
            category = categoryType.type,
            skip = page
        )
            .flowOn(coroutineDispatcher.io())
            .onEach { result ->
                val itemList = mutableListOf<TrueCloudFilesModel>()
                val folderList = getFolderList(result)
                val fileList = getFileList(result)
                itemList.addAll(folderList)
                itemList.addAll(fileList)
                fileItemList.addAll(fileList)
                refreshGridDecoration()
                showFileList(itemList)
                handleCoverImage(fileList)
                page += MINIMUM_PAGE_SIZE
                refreshTask()
            }
            .catch { _error ->
                handleGetCategoryFileError(_error.message.orEmpty())
            }
            .launchSafeIn(this)
    }

    private fun getFileCategoryStorageList() {
        getStorageListWithCategoryUseCase.execute(
            category = categoryType.type,
            sortType = sortType,
            skip = page
        )
            .flowOn(coroutineDispatcher.io())
            .onEach { result ->
                val itemList = mutableListOf<TrueCloudFilesModel>()
                val folderList = getFolderList(result)
                val fileList = getFileList(result)
                itemList.addAll(folderList)
                itemList.addAll(fileList)
                fileItemList.addAll(fileList)
                refreshGridDecoration()
                showFileList(itemList)
                handleCoverImage(fileList)
                page += MINIMUM_PAGE_SIZE
                refreshTask()
            }
            .catch { _error ->
                handleGetCategoryFileError(_error.message.orEmpty())
            }
            .launchSafeIn(this)
    }

    private fun getFolderList(listTrueCloudV3Model: List<TrueCloudV3Model>): List<TrueCloudFilesModel.Folder> {
        return listTrueCloudV3Model.filter { StorageType.FOLDER.type.equals(it.objectType) }
            .map {
                it.convertToFolderModel(contextDataProviderWrapper.get().getDataContext())
            }
    }

    private fun getFileList(listTrueCloudV3Model: List<TrueCloudV3Model>): List<TrueCloudFilesModel.File> {
        val processedIds = fileItemList.map { it.id }.toSet()
        return listTrueCloudV3Model.filterNot {
            StorageType.FILE.type != it.objectType || processedIds.contains(it.id)
        }.map {
            it.convertToFilesModel(contextDataProviderWrapper.get().getDataContext())
        }.map { _fileModel ->
            if (
                StorageType.FILE.type.equals(_fileModel.objectType) &&
                arrayOf(FileMimeType.VIDEO, FileMimeType.IMAGE).contains(_fileModel.fileMimeType)
            ) {
                _fileModel.coverImageKey?.let { _key ->
                    val cachePath =
                        contextDataProviderWrapper.get().getDataContext()
                            .cacheDir.absolutePath + "/true_cloud_cache/$_key.jpg"
                    _fileModel.imageCoverAlready = File(cachePath).isComplete()
                }
            }
            _fileModel
        }
    }

    private fun handleCoverImage(fileList: List<TrueCloudFilesModel.File>) {
        fileList.filter {
            StorageType.FILE.type.equals(it.objectType) &&
                arrayOf(FileMimeType.VIDEO, FileMimeType.IMAGE).contains(it.fileMimeType) &&
                !it.imageCoverAlready
        }.map { _trueCloudV3Model ->
            _trueCloudV3Model.coverImageKey?.let {
                downloadCoverImage(it)
            }
        }
    }

    private fun getFileListNewFolder(): List<TrueCloudFilesModel> {
        items.clear()
        itemList.clear()
        return refreshData()
    }

    private val transferlistener = object :
        TrueCloudV3TransferObserver.TrueCloudV3TransferListener {
        override fun onStateChanged(id: Int, state: TrueCloudV3TransferState?) {
            launchSafe {
                when (state) {
                    TrueCloudV3TransferState.COMPLETED -> {
                        completeUpload(id)
                    }

                    TrueCloudV3TransferState.CANCELED -> {
                        flow {
                            removeTaskUseCase.execute(id)
                            emit(Unit)
                        }
                            .flowOn(coroutineDispatcher.io())
                            .launchSafeIn(viewModelScope)
                    }

                    TrueCloudV3TransferState.IN_PROGRESS -> {
                        onTaskUpdateStatusType.value =
                            getUpdateStatusFile(id, TaskStatusType.IN_PROGRESS)
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
            refreshTask()
        }

        override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
            Timber.i("HIT PROGRESS CHANGE ")
            onShowProgressChange.value =
                getUploadLoadProgress(id, (bytesCurrent / (bytesTotal * 0.01)).toLong())
        }

        override fun onError(id: Int, ex: Exception?) {
            ex?.printStackTrace()
            launchSafe {
                updateTaskUploadStatusUseCase.execute(id, TaskStatusType.FAILED)
                refreshTask()
            }
        }
    }

    private fun getUpdateStatusFile(
        id: Int,
        statusType: TaskStatusType
    ): Pair<List<TrueCloudFilesModel>, Int> {
        var updateIndex = 0
        this.items.apply {
            filterIsInstance<TrueCloudFilesModel.Upload>()
                .map { it as TrueCloudFilesModel.Upload }
                .filter { it.id == id }
                .map { task ->
                    task.status = statusType
                    updateIndex = indexOf(task)
                }
        }
        return Pair(items, updateIndex)
    }

    private fun getUploadLoadProgress(
        id: Int,
        progress: Long
    ): Pair<List<TrueCloudFilesModel>, Int> {
        var updateIndex = 0
        this.items.apply {
            filterIsInstance<TrueCloudFilesModel.Upload>()
                .filter { it.id == id }
                .map { task ->
                    task.progress = if (progress > task.progress) {
                        progress
                    } else {
                        task.progress
                    }
                    task.progressMessage = " ${task.progress}%"
                    updateIndex = indexOf(task)
                }
            filterIsInstance<TrueCloudFilesModel.AutoBackup>()
                .filter { it.id == id }
                .map { task ->
                    task.progress = if (progress > task.progress) {
                        progress
                    } else {
                        task.progress
                    }
                    task.progressMessage = " ${task.progress}%"
                    updateIndex = indexOf(task)
                }
        }
        return Pair(items, updateIndex)
    }

    private fun getListUpdateFileName(file: TrueCloudFilesModel.File): Pair<List<TrueCloudFilesModel>, Int> {
        var updateIndex = 0
        items.apply {
            filter { trueCloudFilesModel ->
                (
                    trueCloudFilesModel is TrueCloudFilesModel.File &&
                        trueCloudFilesModel.id == file.id
                    ) ||
                    (
                        trueCloudFilesModel is TrueCloudFilesModel.Folder &&
                            trueCloudFilesModel.id == file.id
                        )
            }
                .map { trueCloudFilesModel ->
                    when (trueCloudFilesModel) {
                        is TrueCloudFilesModel.File -> trueCloudFilesModel.name = file.name

                        is TrueCloudFilesModel.Folder -> trueCloudFilesModel.name = file.name
                        else -> {}
                    }
                    updateIndex = indexOf(trueCloudFilesModel)
                }
        }
        return Pair(items, updateIndex)
    }

    private fun uploadMutipleFile(
        uriList: List<Uri>,
        folderId: String = stackFolderIds.last().first
    ) {
        onShowLoading.value = true
        uploadFileUseCase.execute(uriList, folderId)
            .map {
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
                val taskUploadModel = getUploadTaskUseCase.execute(it.getId())
                when (taskUploadModel?.status) {
                    TaskStatusType.PAUSE -> {
                        trueCloudV3TransferUtilityProvider.pauseTransferById(
                            contextDataProviderWrapper.get().getDataContext(),
                            taskUploadModel.id
                        )
                    }

                    else -> {
                        // Do Nothing
                    }
                }
                it.setTransferListener(transferlistener)
                refreshTask()
                onShowLoading.value = false
            }.catch { ex ->
                if (!isMultipleUpload || (isMultipleUpload && multipleUploadErrorCount < MAXIMUM_TOAST_ERROR)) {
                    multipleUploadErrorCount++
                    val message = if (ERROR_INIT_UPLOAD_DATA_EXCEED_LIMIT == ex.message) {
                        contextDataProviderWrapper.get()
                            .getString(R.string.true_cloudv3_select_file_exceeded_storage_size)
                    } else {
                        ex.message
                    }
                    onUploadError.postValue(message.orEmpty())
                }
                onShowLoading.postValue(false)
            }
            .flowOn(coroutineDispatcher.io())
            .launchSafeIn(this)
    }

    private fun refreshTransferListener(transferObserver: TrueCloudV3TransferObserver) {
        transferObserver.setTransferListener(transferlistener)
    }

    private fun refreshTask() {
        getUploadTaskListUseCase.execute()
            .flowOn(coroutineDispatcher.io())
            .onEach { result ->
                val itemList =
                    result?.map {
                        if (it.actionType == TaskActionType.UPLOAD) {
                            it.getUploadFilesModel()
                        } else {
                            it.getAutoBackupFileModel()
                        }
                    }?.toMutableList() ?: mutableListOf()
                uploadItemSize = if (itemList.isNotEmpty()) {
                    itemList.size + HEADER_SIZE
                } else {
                    EMPTY_ITEM
                }
                refreshGridDecoration()
                itemList.filterIsInstance<TrueCloudFilesModel.Upload>()
                    .let { list ->
                        onShowUploadTaskList.value = getUploadList(list)
                    }
                itemList.filterIsInstance<TrueCloudFilesModel.AutoBackup>()
                    .let { list ->
                        onShowUploadTaskList.value = getAutoBackupList(list)
                    }
                setViewState(items.count())
                if (itemList.isEmpty()) {
                    transferObserverMap.clear()
                    clearAllUploadTask()
                }
            }
            .launchSafeIn(this)
    }

    private fun getUploadList(filesList: List<TrueCloudFilesModel>): List<TrueCloudFilesModel> {
        itemUploadList.clear()
        if (filesList.isNotEmpty()) {
            itemUploadList.add(
                TrueCloudFilesModel.Header(
                    title = EMPTY_HEADER_UPLOAD,
                    headerType = HeaderType.UPLOAD
                )
            )
            itemUploadList.addAll(filesList)
        }
        return refreshData()
    }

    private fun getAutoBackupList(filesList: List<TrueCloudFilesModel>): List<TrueCloudFilesModel> {
        itemBackupList.clear()
        if (filesList.isNotEmpty()) {
            itemBackupList.add(
                TrueCloudFilesModel.Header(
                    title = EMPTY_HEADER_AUTO_BACKUP,
                    headerType = HeaderType.AUTO_BACKUP
                )
            )
            itemBackupList.addAll(filesList)
        }
        return refreshData()
    }

    private fun getFilesList(
        categoryName: String,
        folderList: List<TrueCloudFilesModel.Folder>,
        filesList: List<TrueCloudFilesModel.File>
    ): List<TrueCloudFilesModel> {
        fileHeader = TrueCloudFilesModel.Header(title = categoryName, headerType = HeaderType.FILE)
        itemList.clear()
        fileHeader?.let {
            itemList.add(it)
        }
        itemList.addAll(folderList)
        if (folderList.isNotEmpty()) {
            itemList.add(
                TrueCloudFilesModel.Header(
                    title = EMPTY_STRING,
                    headerType = HeaderType.OTHER
                )
            )
        }
        itemList.addAll(filesList)
        return refreshData()
    }

    private fun refreshData(): List<TrueCloudFilesModel> {
        items.clear()
        items.addAll(itemUploadList)
        items.addAll(itemBackupList)
        if (itemUploadList.isNotEmpty() || itemBackupList.isNotEmpty()) {
            if (itemList.isNotEmpty() && itemList.first() !is TrueCloudFilesModel.Header) {
                fileHeader?.let {
                    itemList.add(FIRST_INDEX, it)
                }
            }
            items.addAll(itemList)
        } else if (itemList.isNotEmpty()) {
            if (itemList.first() is TrueCloudFilesModel.Header) {
                itemList.removeFirst()
            }
            items.addAll(itemList)
        }
        return items
    }

    private fun download(key: String, path: String) {
        val isenableNotification =
            areNotificationsEnabled(
                NotificationManagerCompat.from(
                    contextDataProviderWrapper.get().getDataContext()
                )
            )
        if (isenableNotification) {
            doDownloadWorker(key, path)
        } else {
            onShowNotificationIsOff.value =
                contextDataProviderWrapper.get()
                    .getString(R.string.true_cloudv3_dialog_subtitle_notification)
        }
    }

    private fun doDownloadWorker(key: String, path: String) {
        val data = workDataOf(
            KEY to key,
            PATH to path
        )
        val getDownloadWorker: WorkRequest =
            OneTimeWorkRequest.Builder(TrueCloudV3DownloadWorker::class.java)
                .setInputData(data)
                .build()
        workManager.enqueue(getDownloadWorker)
    }

    private fun uploadWithPath(path: String, folderId: String = stackFolderIds.last().first) {
        uploadFileWithPathUseCase.execute(path, folderId).map {
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
            refreshTask()
            it.setTransferListener(transferlistener)
        }.catch { ex ->
            Timber.e(ex)
        }.launchSafeIn(this)
    }

    private fun retryUpload(objectId: String) {
        retryUploadUseCase.execute(objectId).map {
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
            refreshTask()
            it.setTransferListener(transferlistener)
        }.catch { ex ->
            Timber.e(ex)
        }.launchSafeIn(this)
    }

    private fun updateSelectedList() {
        onSelectUpdate.value = selectItemList.size
    }

    private fun getCurrentPage() = page

    private fun setFirstPage() {
        isFirstPage = true
        folderItemSize = 0
        fileItemList.clear()
        folderItemList.clear()
        page = DEFAULT_START_PAGE
    }

    private fun trackScreen(categoryType: FileCategoryType) {
        val screenNameValue = when (categoryType) {
            FileCategoryType.RECENT -> TrueCloudV3TrackAnalytic.SCREEN_RECENT_UPLOAD
            FileCategoryType.IMAGE -> TrueCloudV3TrackAnalytic.SCREEN_IMAGES
            FileCategoryType.VIDEO -> TrueCloudV3TrackAnalytic.SCREEN_VIDEOS
            FileCategoryType.AUDIO -> TrueCloudV3TrackAnalytic.SCREEN_AUDIO
            FileCategoryType.OTHER -> TrueCloudV3TrackAnalytic.SCREEN_OTHER
            else -> {
                TrueCloudV3TrackAnalytic.SCREEN_ALL_FILES
            }
        }
        analyticManagerInterface.trackScreen(
            PlatformAnalyticModel().apply {
                screenClass = FilesTrueCloudFragment::class.java.canonicalName as String
                screenName = screenNameValue
            }
        )
    }

    private fun trackEventUploadSuccess(mimeType: String) {
        val fileMimeType = FileMimeTypeManager.getMimeType(mimeType)
        val linkDesc = getLinkDescFromMimeType(fileMimeType)
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_EVENT_NAME to TrueCloudV3TrackAnalytic.EVENT_UPLOAD,
                TrueCloudV3TrackAnalytic.PARAMS_FILE_TYPE to linkDesc
            )
        )
    }

    private fun trackEventDownload(fileMimeType: FileMimeType) {
        val linkDesc = getLinkDescFromMimeType(fileMimeType)
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_EVENT_NAME to TrueCloudV3TrackAnalytic.EVENT_DOWNLOAD,
                TrueCloudV3TrackAnalytic.PARAMS_FILE_TYPE to linkDesc
            )
        )
    }

    private fun trackEventDelete(fileMimeType: FileMimeType) {
        val linkDesc = getLinkDescFromMimeType(fileMimeType)
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_EVENT_NAME to TrueCloudV3TrackAnalytic.EVENT_DELETE,
                TrueCloudV3TrackAnalytic.PARAMS_FILE_TYPE to linkDesc
            )
        )
    }

    private fun clearAllUploadTask() {
        viewModelScope.launchSafe(coroutineDispatcher.io()) {
            removeAllTaskUseCase.execute()
        }
    }

    private suspend fun setTransferState(taskId: Int) {
        val transferObserver =
            trueCloudV3TransferUtilityProvider.getTrueCloudV3TransferObserverById(
                contextDataProviderWrapper.get().getDataContext(),
                taskId
            )
        if (transferObserver != null) {
            when (transferObserver.getState()) {
                TrueCloudV3TransferState.COMPLETED -> {
                    completeUpload(taskId)
                }

                TrueCloudV3TransferState.CANCELED -> {
                    removeTaskUseCase.execute(taskId)
                }

                TrueCloudV3TransferState.IN_PROGRESS -> {
                    refreshTransferListener(transferObserver)
                    trueCloudV3TransferUtilityProvider.resumeTransferById(
                        contextDataProviderWrapper.get().getDataContext(),
                        taskId
                    )
                }

                TrueCloudV3TransferState.WAITING,
                TrueCloudV3TransferState.WAITING_FOR_NETWORK,
                TrueCloudV3TransferState.RESUMED_WAITING -> {
                    updateTaskUploadStatusUseCase.execute(
                        taskId,
                        TaskStatusType.WAITING
                    )
                    refreshTransferListener(transferObserver)
                    trueCloudV3TransferUtilityProvider.resumeTransferById(
                        contextDataProviderWrapper.get().getDataContext(),
                        taskId
                    )
                }

                TrueCloudV3TransferState.PAUSED -> {
                    updateTaskUploadStatusUseCase.execute(
                        taskId,
                        TaskStatusType.PAUSE
                    )
                    refreshTransferListener(transferObserver)
                }

                TrueCloudV3TransferState.FAILED -> {
                    updateTaskUploadStatusUseCase.execute(
                        taskId,
                        TaskStatusType.FAILED
                    )
                    refreshTransferListener(transferObserver)
                }

                else -> {
                    updateTaskUploadStatusUseCase.execute(
                        taskId,
                        TaskStatusType.UNKNOWN
                    )
                    refreshTransferListener(transferObserver)
                }
            }
        }
    }
}
