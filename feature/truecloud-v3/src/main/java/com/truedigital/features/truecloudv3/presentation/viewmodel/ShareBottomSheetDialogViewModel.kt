package com.truedigital.features.truecloudv3.presentation.viewmodel

import android.os.Bundle
import android.os.Environment
import com.truedigital.common.share.analytics.di.AnalyticsModule
import com.truedigital.common.share.analytics.measurement.AnalyticManagerInterface
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant
import com.truedigital.common.share.datalegacy.wrapper.ContextDataProviderWrapper
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.extensions.launchSafeIn
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle
import com.truedigital.features.truecloudv3.common.TrueCloudV3TrackAnalytic
import com.truedigital.features.truecloudv3.common.TrueCloudV3TransferState
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.features.truecloudv3.domain.model.TrueCloudV3TransferObserver
import com.truedigital.features.truecloudv3.domain.usecase.DownloadType
import com.truedigital.features.truecloudv3.domain.usecase.DownloadUseCase
import com.truedigital.features.truecloudv3.domain.usecase.GetShareLinkUseCase
import com.truedigital.features.truecloudv3.navigation.ShareBottomSheetToShareControlAccess
import com.truedigital.features.truecloudv3.navigation.router.ControlAccessRouterUseCase
import com.truedigital.features.truecloudv3.provider.FileProvider
import com.truedigital.foundation.extension.SingleLiveEvent
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import java.io.File
import javax.inject.Inject
import javax.inject.Named

class ShareBottomSheetDialogViewModel @Inject constructor(
    private val coroutineDispatcher: CoroutineDispatcherProvider,
    private val downloadUseCase: DownloadUseCase,
    private val getShareLinkUseCase: GetShareLinkUseCase,
    private val fileProvider: FileProvider,
    private val router: ControlAccessRouterUseCase,
    private val contextDataProviderWrapper: ContextDataProviderWrapper,
    @Named(AnalyticsModule.FIREBASE_ANALYTIC)
    private val analyticManagerInterface: AnalyticManagerInterface
) : ScopedViewModel() {

    val onStartShare = SingleLiveEvent<Pair<File, String>>()
    val onSetTitle = SingleLiveEvent<String>()
    val onSetTitleStatus = SingleLiveEvent<Pair<@androidx.annotation.DrawableRes Int, String>>()
    val onCopyLink = SingleLiveEvent<String>()
    val onShowProgressLoading = SingleLiveEvent<Boolean>()
    private lateinit var trueCloudFilesModel: TrueCloudFilesModel.File

    fun setFileModel(trueCloudFilesModel: TrueCloudFilesModel.File) {
        this.trueCloudFilesModel = trueCloudFilesModel
        setupView(trueCloudFilesModel)
    }

    fun onClickShare() {
        onShowProgressLoading.value = true
        val path =
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            ).path + "/TrueCloud/" + trueCloudFilesModel.name?.replace(
                " ",
                "_"
            )
        val file = fileProvider.getFile(path)
        val mimeType = trueCloudFilesModel.mimeType.orEmpty()
        if (file.exists()) {
            startShare(file, mimeType)
        } else {
            val key = trueCloudFilesModel.id.orEmpty()
            downloadFile(key, path, mimeType)
        }
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.EVENT_CLICK,
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.LINK_TYPE_BOTTOM_SHEET,
                MeasurementConstant.Key.KEY_LINK_DESC to TrueCloudV3TrackAnalytic.LINK_DESC_SHARE
            )
        )
    }

    fun onClickShareLink() {
        getShareLinkUseCase.execute(trueCloudFilesModel.id.orEmpty())
            .flowOn(coroutineDispatcher.io())
            .onEach {
                onCopyLink.value = it
            }
            .launchSafeIn(this)
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.EVENT_CLICK,
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.LINK_TYPE_BOTTOM_SHEET,
                MeasurementConstant.Key.KEY_LINK_DESC to TrueCloudV3TrackAnalytic.LINK_DESC_COPY_LINK
            )
        )
    }

    fun onClickControlShare() {
        router.execute(
            ShareBottomSheetToShareControlAccess,
            Bundle().apply {
                putParcelable(
                    TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_OPTION_FILE_MODEL,
                    trueCloudFilesModel
                )
            }
        )
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.EVENT_CLICK,
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.LINK_TYPE_BOTTOM_SHEET,
                MeasurementConstant.Key.KEY_LINK_DESC to TrueCloudV3TrackAnalytic.LINK_DESC_CONTROL_SHARE
            )
        )
    }

    private fun setupView(trueCloudFilesModel: TrueCloudFilesModel.File) {
        this.onSetTitle.value = trueCloudFilesModel.name
        trueCloudFilesModel.isPrivate?.let { _isPrivate ->
            val status: String
            val icon: Int
            if (_isPrivate) {
                status = contextDataProviderWrapper.get().getString(R.string.true_cloudv3_private)
                icon = R.drawable.ic_baseline_lock_24
            } else {
                status = contextDataProviderWrapper.get().getString(R.string.true_cloudv3_public)
                icon = R.drawable.ic_share_public_24
            }
            this.onSetTitleStatus.value = Pair(icon, status)
        }
    }

    private fun startShare(file: File, mimeType: String) {
        onShowProgressLoading.value = false
        onStartShare.value = Pair(file, mimeType)
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
                                startShare(fileProvider.getFile(path), mimeType)
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
