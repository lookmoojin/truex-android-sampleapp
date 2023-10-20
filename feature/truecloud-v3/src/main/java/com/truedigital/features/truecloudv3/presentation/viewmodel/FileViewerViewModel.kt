package com.truedigital.features.truecloudv3.presentation.viewmodel

import android.os.Bundle
import android.os.Environment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.workDataOf
import com.truedigital.features.truecloudv3.R
import com.truedigital.common.share.analytics.di.AnalyticsModule
import com.truedigital.common.share.analytics.measurement.AnalyticManagerInterface
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant
import com.truedigital.common.share.datalegacy.wrapper.ContextDataProviderWrapper
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.extensions.launchSafeIn
import com.truedigital.features.truecloudv3.common.FileCategoryType
import com.truedigital.features.truecloudv3.common.FileCategoryTypeManager
import com.truedigital.features.truecloudv3.common.FileMimeType
import com.truedigital.features.truecloudv3.common.SortType
import com.truedigital.features.truecloudv3.common.StorageType
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle
import com.truedigital.features.truecloudv3.common.TrueCloudV3TrackAnalytic
import com.truedigital.features.truecloudv3.data.worker.TrueCloudV3DownloadWorker
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3Model
import com.truedigital.features.truecloudv3.domain.usecase.GetStorageListUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetStorageListWithCategoryUseCase
import com.truedigital.features.truecloudv3.domain.usecase.MoveToTrashUseCase
import com.truedigital.features.truecloudv3.domain.usecase.RenameFileUseCase
import com.truedigital.features.truecloudv3.extension.convertToFilesModel
import com.truedigital.features.truecloudv3.extension.getNotExistsPath
import com.truedigital.features.truecloudv3.navigation.FileViewerToOptionFileBottomSheet
import com.truedigital.features.truecloudv3.navigation.router.TrueCloudV3FileViewerRouterUseCase
import com.truedigital.foundation.extension.SingleLiveEvent
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Named

class FileViewerViewModel @Inject constructor(
    private val router: TrueCloudV3FileViewerRouterUseCase,
    private val coroutineDispatcher: CoroutineDispatcherProvider,
    private val getStorageListUseCase: GetStorageListUseCase,
    private val getStorageListWithCategoryUseCase: GetStorageListWithCategoryUseCase,
    private val workManager: WorkManager,
    private val renameObjectUseCase: RenameFileUseCase,
    private val moveToTrashUseCase: MoveToTrashUseCase,
    private val contextDataProviderWrapper: ContextDataProviderWrapper,
    @Named(AnalyticsModule.FIREBASE_ANALYTIC)
    private val analyticManagerInterface: AnalyticManagerInterface
) : ScopedViewModel() {
    companion object {
        private const val MINIMUM_PAGE_SIZE = 1
    }

    private val _onDeleted = SingleLiveEvent<TrueCloudFilesModel.File>()
    private val _onBackPressed = SingleLiveEvent<Boolean>()
    private val _onSetTitle = SingleLiveEvent<String>()
    val onDeleted: LiveData<TrueCloudFilesModel.File> get() = _onDeleted
    val onBackPressed: LiveData<Boolean> get() = _onBackPressed
    val onSetTitle: LiveData<String> get() = _onSetTitle
    val onAddFileList = MutableLiveData<List<TrueCloudFilesModel.File>>()
    val onSetCurrentPosition = SingleLiveEvent<Int>()
    val onShowSnackbarComplete = SingleLiveEvent<String>()

    private lateinit var folderId: String
    private lateinit var categoryType: FileCategoryType
    private lateinit var sortType: SortType
    private lateinit var trueCloudFilesModel: MutableList<TrueCloudFilesModel.File>

    private var selectedFilesModel: TrueCloudFilesModel.File? = null
    private var page: Int = 0
    private var itemSize: Int = 0
    private var startPosition: Int = 0

    fun onViewCreated(
        folderId: String,
        categoryType: String,
        sortType: SortType,
        selectedFilesModel: TrueCloudFilesModel.File?,
        filesListModel: MutableList<TrueCloudFilesModel.File>?,
        page: Int
    ) {
        this.folderId = folderId
        this.categoryType = FileCategoryTypeManager.getCategoryType(categoryType)
        this.sortType = sortType
        this.selectedFilesModel = selectedFilesModel
        this.trueCloudFilesModel = filesListModel ?: mutableListOf()
        this.page = page
        itemSize = trueCloudFilesModel.size
        onAddFileList.value = trueCloudFilesModel
        startPosition = trueCloudFilesModel.indexOf(selectedFilesModel)
        onSetCurrentPosition.value = startPosition
    }

    fun onClickBack() {
        _onBackPressed.value = true
    }

    fun onClickMore() {
        router.execute(
            FileViewerToOptionFileBottomSheet,
            Bundle().apply {
                putParcelable(
                    TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_OPTION_FILE_MODEL,
                    selectedFilesModel
                )
            }
        )
    }

    fun onPageScrolled(position: Int) {
        selectedFilesModel = trueCloudFilesModel[position]
        _onSetTitle.value = selectedFilesModel?.name ?: ""
        if ((itemSize - 2) == position) {
            loadFilesList()
        }
    }

    fun onDeleteFile(filesModel: TrueCloudFilesModel.File) {
        filesModel.id?.let { _id ->
            moveToTrashUseCase.execute(listOf(_id))
                .flowOn(coroutineDispatcher.io())
                .onEach {
                    trueCloudFilesModel.remove(filesModel)
                    _onDeleted.value = filesModel
                    onShowSnackbarComplete.value = contextDataProviderWrapper.get()
                        .getString(R.string.true_cloudv3_delete_item_successfully)
                }
                .launchSafeIn(this)
        }
        trackEventDelete(filesModel.fileMimeType)
    }

    fun onDownloadFile(trueCloudFilesModel: TrueCloudFilesModel.File) {
        val path =
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            ).path + "/TrueCloud/" + trueCloudFilesModel.name?.replace(
                " ",
                "_"
            )
        trueCloudFilesModel.id?.let {
            doDownloadWorker(it, path.getNotExistsPath())
        }
        trackEventDownload()
    }

    fun rename(trueCloudFilesModel: TrueCloudFilesModel.File) {
        val id = trueCloudFilesModel.id.orEmpty()
        id.isNotEmpty().let {
            val name = trueCloudFilesModel.name.orEmpty()
            renameObjectUseCase.execute(id, name)
                .flowOn(coroutineDispatcher.io())
                .launchSafeIn(this)
        }
    }

    private fun loadFilesList() {
        if (categoryType != FileCategoryType.UNSUPPORTED_FORMAT) {
            getFileCategoryStorageList()
        } else {
            getAllFileStorageList()
        }
    }

    private fun getAllFileStorageList() {
        getStorageListUseCase.execute(folderId, sortType, page)
            .flowOn(coroutineDispatcher.io())
            .filter {
                it.isNotEmpty()
            }
            .onEach { _result ->
                val list = getFileList(_result)
                itemSize = trueCloudFilesModel.size
                onAddFileList.value = list
                page += MINIMUM_PAGE_SIZE
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
            .filter {
                it.isNotEmpty()
            }
            .onEach { _result ->
                val list = getFileList(_result)
                itemSize = trueCloudFilesModel.size
                onAddFileList.value = list
                page += MINIMUM_PAGE_SIZE
            }
            .launchSafeIn(this)
    }

    private fun getFileList(listTrueCloudV3Model: List<TrueCloudV3Model>): List<TrueCloudFilesModel.File> {
        val processedIds = trueCloudFilesModel.map { it.id }.toSet()
        val result = listTrueCloudV3Model.filterNot {
            StorageType.FILE.type != it.objectType || processedIds.contains(it.id)
        }.map {
            it.convertToFilesModel(contextDataProviderWrapper.get().getDataContext())
        }
        trueCloudFilesModel.addAll(result)
        return result
    }

    private fun doDownloadWorker(key: String, path: String) {
        val data = workDataOf(
            TrueCloudV3DownloadWorker.KEY to key,
            TrueCloudV3DownloadWorker.PATH to path
        )
        val getDownloadWorker: WorkRequest =
            OneTimeWorkRequest.Builder(TrueCloudV3DownloadWorker::class.java)
                .setInputData(data)
                .build()
        workManager.enqueue(getDownloadWorker)
    }

    private fun trackEventDownload() {
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_EVENT_NAME to TrueCloudV3TrackAnalytic.EVENT_DOWNLOAD,
                TrueCloudV3TrackAnalytic.PARAMS_FILE_TYPE to TrueCloudV3TrackAnalytic.LINK_DESC_IMAGES
            )
        )
    }

    private fun trackEventDelete(fileMimeType: FileMimeType) {
        val linkDesc = TrueCloudV3TrackAnalytic.getLinkDescFromMimeType(fileMimeType)
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_EVENT_NAME to TrueCloudV3TrackAnalytic.EVENT_DELETE,
                TrueCloudV3TrackAnalytic.PARAMS_FILE_TYPE to linkDesc
            )
        )
    }
}
