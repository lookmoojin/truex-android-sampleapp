package com.truedigital.features.truecloudv3.presentation.viewmodel

import android.os.Bundle
import android.os.Environment
import androidx.core.os.bundleOf
import com.truedigital.common.share.analytics.di.AnalyticsModule
import com.truedigital.common.share.analytics.measurement.AnalyticManagerInterface
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.extensions.launchSafeIn
import com.truedigital.features.truecloudv3.common.FileMimeType
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_FILE_INFO_MODEL
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_FILE_VIEW
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_OPTION_FILE_MODEL
import com.truedigital.features.truecloudv3.common.TrueCloudV3TrackAnalytic
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
import com.truedigital.foundation.extension.SingleLiveEvent
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import java.io.File
import javax.inject.Inject
import javax.inject.Named

class OptionFileBottomSheetDialogViewModel @Inject constructor(
    private val router: FileTrueCloudRouterUseCase,
    private val coroutineDispatcher: CoroutineDispatcherProvider,
    private val downloadUseCase: DownloadUseCase,
    private val fileProvider: FileProvider,
    @Named(AnalyticsModule.FIREBASE_ANALYTIC)
    private val analyticManagerInterface: AnalyticManagerInterface
) : ScopedViewModel() {

    val onRename = SingleLiveEvent<TrueCloudFilesModel.File?>()
    val onDelete = SingleLiveEvent<TrueCloudFilesModel.File?>()
    val onDownload = SingleLiveEvent<TrueCloudFilesModel.File?>()
    val onSetShowViewFile = SingleLiveEvent<Boolean>()
    val onSetShowPhotoEditor = SingleLiveEvent<Boolean>()
    val onShowProgressLoading = SingleLiveEvent<Boolean>()
    val onStartViewFile = SingleLiveEvent<Pair<File, String>>()
    private var trueCloudFilesModel: TrueCloudFilesModel.File? = null

    fun setFileModel(trueCloudFilesModel: TrueCloudFilesModel.File) {
        this.trueCloudFilesModel = trueCloudFilesModel
        setupView(trueCloudFilesModel)
    }

    fun onReceiveFileName(trueCloudFilesModel: TrueCloudFilesModel.File) {
        onRename.value = trueCloudFilesModel
    }

    fun onClickRename() {
        router.execute(
            OptionFileToRenameDialog,
            Bundle().apply {
                putParcelable(
                    KEY_BUNDLE_TRUE_CLOUD_OPTION_FILE_MODEL,
                    trueCloudFilesModel
                )
            }
        )
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_EVENT_NAME to TrueCloudV3TrackAnalytic.EVENT_CLICK,
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.LINK_TYPE_BOTTOM_SHEET,
                MeasurementConstant.Key.KEY_LINK_DESC to TrueCloudV3TrackAnalytic.LINK_DESC_RENAME
            )
        )
    }

    fun onClickShare() {
        router.execute(
            OptionFileToShareBottomSheet,
            Bundle().apply {
                putParcelable(
                    KEY_BUNDLE_TRUE_CLOUD_OPTION_FILE_MODEL,
                    trueCloudFilesModel
                )
            }
        )
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_EVENT_NAME to TrueCloudV3TrackAnalytic.EVENT_CLICK,
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.LINK_TYPE_BOTTOM_SHEET,
                MeasurementConstant.Key.KEY_LINK_DESC to TrueCloudV3TrackAnalytic.LINK_DESC_SHARE
            )
        )
    }

    fun onClickSeeInfo() {
        router.execute(
            OptionFileToFileInfoBottomSheet,
            Bundle().apply {
                putParcelable(
                    KEY_BUNDLE_TRUE_CLOUD_FILE_INFO_MODEL,
                    trueCloudFilesModel
                )
            }
        )
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_EVENT_NAME to TrueCloudV3TrackAnalytic.EVENT_CLICK,
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.LINK_TYPE_BOTTOM_SHEET,
                MeasurementConstant.Key.KEY_LINK_DESC to TrueCloudV3TrackAnalytic.LINK_DESC_SEE_INFO
            )
        )
    }

    fun onClickViewFile() {
        val trueCloudFile = trueCloudFilesModel
        if (trueCloudFile != null) {
            onShowProgressLoading.value = true
            val path =
                Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS
                ).path + "/TrueCloud/" + trueCloudFile.name?.replace(
                    " ",
                    "_"
                )
            val file = fileProvider.getFile(path)
            val mimeType = trueCloudFilesModel?.mimeType.orEmpty()
            if (file.exists()) {
                startViewFile(file, mimeType)
            } else {
                val key = trueCloudFile.id.orEmpty()
                downloadFile(key, path, mimeType)
            }
        }
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_EVENT_NAME to TrueCloudV3TrackAnalytic.EVENT_CLICK,
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.LINK_TYPE_BOTTOM_SHEET,
                MeasurementConstant.Key.KEY_LINK_DESC to TrueCloudV3TrackAnalytic.LINK_DESC_VIEW_FILE
            )
        )
    }

    fun onClickPhotoEditor() {
        router.execute(
            OptionFileToPhotoEditor,
            bundleOf(KEY_BUNDLE_TRUE_CLOUD_FILE_VIEW to trueCloudFilesModel)
        )
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_EVENT_NAME to TrueCloudV3TrackAnalytic.EVENT_CLICK,
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.LINK_TYPE_BOTTOM_SHEET,
                MeasurementConstant.Key.KEY_LINK_DESC to TrueCloudV3TrackAnalytic.LINK_DESC_EDIT_IMAGE
            )
        )
    }

    fun onClickDelete() {
        onDelete.value = trueCloudFilesModel
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_EVENT_NAME to TrueCloudV3TrackAnalytic.EVENT_CLICK,
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.LINK_TYPE_BOTTOM_SHEET,
                MeasurementConstant.Key.KEY_LINK_DESC to TrueCloudV3TrackAnalytic.LINK_DESC_REMOVE_TO_TRASH
            )
        )
    }

    fun onClickDownload() {
        onDownload.value = trueCloudFilesModel
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_EVENT_NAME to TrueCloudV3TrackAnalytic.EVENT_CLICK,
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.LINK_TYPE_BOTTOM_SHEET,
                MeasurementConstant.Key.KEY_LINK_DESC to TrueCloudV3TrackAnalytic.LINK_DESC_DOWNLOAD
            )
        )
    }

    private fun setupView(trueCloudFilesModel: TrueCloudFilesModel.File) {
        this.onSetShowViewFile.value = when (trueCloudFilesModel.fileMimeType) {
            FileMimeType.IMAGE -> false
            FileMimeType.VIDEO -> false
            FileMimeType.AUDIO -> false
            FileMimeType.CONTACT -> false
            FileMimeType.OTHER -> true
            FileMimeType.UNSUPPORTED_FORMAT -> true
        }
        this.onSetShowPhotoEditor.value = when (trueCloudFilesModel.fileMimeType) {
            FileMimeType.IMAGE -> true
            FileMimeType.VIDEO,
            FileMimeType.AUDIO,
            FileMimeType.CONTACT,
            FileMimeType.OTHER,
            FileMimeType.UNSUPPORTED_FORMAT -> false
        }
    }

    private fun startViewFile(file: File, mimeType: String) {
        onShowProgressLoading.value = false
        onStartViewFile.value = Pair(file, mimeType)
    }

    private fun downloadFile(key: String, path: String, mimeType: String) {
        downloadUseCase.execute(key, path, DownloadType.SHARE)
            .flowOn(coroutineDispatcher.io())
            .onEach { transferObserver ->
                transferObserver.setTransferListener(
                    object : TrueCloudV3TransferObserver.TrueCloudV3TransferListener {
                        override fun onStateChanged(
                            id: Int,
                            state: TrueCloudV3TransferState?
                        ) {
                            if (state == TrueCloudV3TransferState.COMPLETED) {
                                startViewFile(fileProvider.getFile(path), mimeType)
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
            .launchSafeIn(this)
    }
}
