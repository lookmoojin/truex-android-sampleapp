package com.truedigital.features.truecloudv3.presentation.viewmodel

import android.net.Uri
import androidx.core.os.bundleOf
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.tdg.truecloud.R
import com.truedigital.common.share.datalegacy.wrapper.ContextDataProviderWrapper
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.extensions.launchSafe
import com.truedigital.core.extensions.launchSafeIn
import com.truedigital.features.truecloudv3.common.TaskStatusType
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_PHOTO_EDITOR_IMAGE
import com.truedigital.features.truecloudv3.common.TrueCloudV3TransferState
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3TransferObserver
import com.truedigital.features.truecloudv3.domain.usecase.CompleteReplaceUploadUseCase
import com.truedigital.features.truecloudv3.domain.usecase.DownloadType
import com.truedigital.features.truecloudv3.domain.usecase.DownloadUseCase
import com.truedigital.features.truecloudv3.domain.usecase.RemoveTaskUseCase
import com.truedigital.features.truecloudv3.domain.usecase.ReplaceFileUploadUseCase
import com.truedigital.features.truecloudv3.domain.usecase.UpdateTaskUploadStatusUseCase
import com.truedigital.features.truecloudv3.extension.convertToFilesModel
import com.truedigital.features.truecloudv3.navigation.PhotoEditorToPhotoEditorAdjust
import com.truedigital.features.truecloudv3.navigation.PhotoEditorToPhotoEditorFocus
import com.truedigital.features.truecloudv3.navigation.PhotoEditorToPhotoEditorText
import com.truedigital.features.truecloudv3.navigation.PhotoEditorToPhotoEditorTransform
import com.truedigital.features.truecloudv3.navigation.router.TrueCloudV3FileViewerRouterUseCase
import com.truedigital.features.truecloudv3.provider.FileProvider
import com.truedigital.foundation.extension.SingleLiveEvent
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class TrueCloudV3PhotoEditorViewModel @Inject constructor(
    private val coroutineDispatcher: CoroutineDispatcherProvider,
    private val downloadUseCase: DownloadUseCase,
    private val fileProvider: FileProvider,
    private val contextDataProviderWrapper: ContextDataProviderWrapper,
    private val router: TrueCloudV3FileViewerRouterUseCase,
    private val replaceFileUploadUseCase: ReplaceFileUploadUseCase,
    private val completeReplaceUploadUseCase: CompleteReplaceUploadUseCase,
    private val removeTaskUseCase: RemoveTaskUseCase,
    private val updateTaskUploadStatusUseCase: UpdateTaskUploadStatusUseCase,
) : ScopedViewModel() {

    companion object {
        private const val HISTORY_INIT_INDEX = 0
        private const val PHOTO_CACHE_DIR = "/true_cloud_cache"
        private const val PHOTO_EDITOR_CACHE_DIR = "/true_cloud_cache_photo_editor"
    }

    val imagePreview: LiveData<Uri?>
    val onShowLoading: LiveData<Boolean> get() = _onShowLoading
    val onUploadComplete: LiveData<TrueCloudFilesModel.File> get() = _onUploadComplete
    val onUploadError: LiveData<String> get() = _onUploadError
    private val _historyLocation = MutableLiveData<Int>()
    private val _onShowLoading = SingleLiveEvent<Boolean>()
    private val _onUploadComplete = SingleLiveEvent<TrueCloudFilesModel.File>()
    private val _onUploadError = MutableLiveData<String>()
    private var history = mutableListOf<Uri>()
    private lateinit var trueCloudFilesModel: TrueCloudFilesModel.File

    init {
        imagePreview = _historyLocation.map {
            history.getOrNull(it)
        }
    }

    override fun onCleared() {
        super.onCleared()
        launchSafe(coroutineDispatcher.io()) {
            deleteDirectory(getCacheDirectory())
        }
    }

    fun setObjectFile(fileModel: TrueCloudFilesModel.File) {
        history.clear()
        trueCloudFilesModel = fileModel
        val key = fileModel.id.orEmpty()
        launchSafe(coroutineDispatcher.io()) {
            val file = getCacheDirectory()
            val fileData = fileProvider.getFile(file.path, "$key.jpg")
            downloadPreview(key, fileData)
        }
    }

    fun onTransformClick() {
        router.execute(
            PhotoEditorToPhotoEditorTransform,
            bundleOf(
                KEY_BUNDLE_TRUE_CLOUD_PHOTO_EDITOR_IMAGE to (imagePreview.value ?: return),
            )
        )
    }

    fun onNewImage(uri: Uri) {
        if (_historyLocation.value != history.lastIndex) {
            history = history.subList(
                HISTORY_INIT_INDEX,
                _historyLocation.value?.inc()?.coerceAtMost(history.lastIndex) ?: HISTORY_INIT_INDEX
            )
        }
        history.add(uri)
        _historyLocation.value = history.lastIndex
    }

    fun onAdjustClick() {
        router.execute(
            PhotoEditorToPhotoEditorAdjust,
            bundleOf(
                KEY_BUNDLE_TRUE_CLOUD_PHOTO_EDITOR_IMAGE to (imagePreview.value ?: return),
            )
        )
    }

    fun onFocusClick() {
        router.execute(
            PhotoEditorToPhotoEditorFocus,
            bundleOf(
                KEY_BUNDLE_TRUE_CLOUD_PHOTO_EDITOR_IMAGE to (imagePreview.value ?: return),
            )
        )
    }

    fun onTextClick() {
        router.execute(
            PhotoEditorToPhotoEditorText,
            bundleOf(
                KEY_BUNDLE_TRUE_CLOUD_PHOTO_EDITOR_IMAGE to (imagePreview.value ?: return),
            )
        )
    }

    fun onSaveClick() {
        val filePath = imagePreview.value?.path
        val objectId = trueCloudFilesModel.id
        if (filePath != null && objectId != null) {
            _onShowLoading.value = true
            trueCloudFilesModel.coverImageKey?.let { removeCoverImageCache(it) }
            replaceFileUploadUseCase.execute(filePath, objectId).map {
                it.setTransferListener(object :
                    TrueCloudV3TransferObserver.TrueCloudV3TransferListener {
                    override fun onStateChanged(id: Int, state: TrueCloudV3TransferState?) {
                        when (state) {
                            TrueCloudV3TransferState.COMPLETED -> {
                                completeUpload(id)
                            }

                            TrueCloudV3TransferState.FAILED -> {
                                _onUploadError.postValue(
                                    contextDataProviderWrapper.get().getString(
                                        R.string.true_cloudv3_error_message
                                    )
                                )
                                _onShowLoading.postValue(false)
                                launchSafe {
                                    updateTaskUploadStatusUseCase.execute(
                                        id,
                                        TaskStatusType.FAILED
                                    )
                                }
                            }

                            else -> {
                                // Do Nothing
                            }
                        }
                    }

                    override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                        Timber.i("onProgressChanged")
                    }

                    override fun onError(id: Int, ex: Exception?) {
                        launchSafe {
                            updateTaskUploadStatusUseCase.execute(id, TaskStatusType.FAILED)
                            _onUploadError.postValue(ex?.message.orEmpty())
                            _onShowLoading.postValue(false)
                        }
                    }
                })
            }.catch { e ->
                _onUploadError.postValue(e.message.orEmpty())
                _onShowLoading.postValue(false)
            }
                .flowOn(coroutineDispatcher.io())
                .launchSafeIn(this)
        }
    }

    fun onUndoClick() {
        _historyLocation.value = _historyLocation.value?.dec()?.coerceAtLeast(0)
    }

    fun onRedoClick() {
        _historyLocation.value = _historyLocation.value?.inc()?.coerceAtMost(history.lastIndex)
    }

    private fun removeCoverImageCache(key: String) {
        val cachePath =
            contextDataProviderWrapper.get().getDataContext()
                .cacheDir.absolutePath + "$PHOTO_CACHE_DIR/$key.jpg"
        File(cachePath).delete()
    }

    private fun deleteDirectory(dir: File) {
        if (dir.isDirectory) {
            dir.list()?.let { children ->
                for (child in children) {
                    File(dir, child).delete()
                }
            }
        }
    }

    private fun getCacheDirectory(): File {
        val cacheDir =
            contextDataProviderWrapper.get()
                .getDataContext().cacheDir.absolutePath + PHOTO_EDITOR_CACHE_DIR
        val file = fileProvider.getFile(cacheDir)
        if (!file.exists()) {
            file.mkdir()
        }
        return file
    }

    private fun downloadPreview(key: String, fileData: File) {
        downloadUseCase.execute(key, fileData.absolutePath, DownloadType.SHARE)
            .flowOn(coroutineDispatcher.io())
            .map {
                it.setTransferListener(
                    object : TrueCloudV3TransferObserver.TrueCloudV3TransferListener {
                        override fun onStateChanged(
                            id: Int,
                            state: TrueCloudV3TransferState?
                        ) {
                            if (TrueCloudV3TransferState.COMPLETED == state) {
                                history.add(Uri.fromFile(fileData))
                                _historyLocation.postValue(HISTORY_INIT_INDEX)
                            }
                        }

                        override fun onProgressChanged(
                            id: Int,
                            bytesCurrent: Long,
                            bytesTotal: Long
                        ) {
                            Timber.i("onProgressChanged")
                        }

                        override fun onError(id: Int, ex: Exception?) {
                            Timber.i("onError")
                        }
                    }
                )
            }
            .launchSafeIn(this)
    }

    private fun completeUpload(id: Int) {
        completeReplaceUploadUseCase.execute(id)
            .flowOn(coroutineDispatcher.io())
            .onEach {
                removeTaskUseCase.execute(id)
                _onShowLoading.postValue(false)
                _onUploadComplete.postValue(
                    it.convertToFilesModel(
                        contextDataProviderWrapper.get().getDataContext()
                    )
                )
            }
            .catch { e ->
                _onUploadError.postValue(e.message.orEmpty())
                _onShowLoading.postValue(false)
                updateTaskUploadStatusUseCase.execute(
                    id,
                    TaskStatusType.COMPLETE_API_FAILED
                )
            }
            .launchSafeIn(this)
    }
}
