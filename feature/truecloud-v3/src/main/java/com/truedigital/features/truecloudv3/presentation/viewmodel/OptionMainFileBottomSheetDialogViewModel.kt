package com.truedigital.features.truecloudv3.presentation.viewmodel

import android.os.Bundle
import com.truedigital.common.share.analytics.di.AnalyticsModule
import com.truedigital.common.share.analytics.measurement.AnalyticManagerInterface
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.features.truecloudv3.common.SortType
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_SORT_TYPE
import com.truedigital.features.truecloudv3.common.TrueCloudV3TrackAnalytic
import com.truedigital.features.truecloudv3.navigation.OptingMainToCreateNewFolder
import com.truedigital.features.truecloudv3.navigation.OptingMainToSortByBottomSheet
import com.truedigital.features.truecloudv3.navigation.router.FileTrueCloudRouterUseCase
import com.truedigital.foundation.extension.SingleLiveEvent
import javax.inject.Inject
import javax.inject.Named

class OptionMainFileBottomSheetDialogViewModel @Inject constructor(
    private val router: FileTrueCloudRouterUseCase,
    @Named(AnalyticsModule.FIREBASE_ANALYTIC)
    private val analyticManagerInterface: AnalyticManagerInterface
) : ScopedViewModel() {

    val onSortBy = SingleLiveEvent<SortType>()
    val onCreateNewFolder = SingleLiveEvent<String>()
    val onEmptyTrash = SingleLiveEvent<Unit>()
    val onConfirmEmpty = SingleLiveEvent<Unit>()

    private var sortType = SortType.SORT_DATE_DESC
    fun setSortType(sortType: SortType) {
        this.sortType = sortType
    }

    fun onClickSortBy() {
        router.execute(
            OptingMainToSortByBottomSheet,
            Bundle().apply {
                putParcelable(
                    KEY_BUNDLE_TRUE_CLOUD_SORT_TYPE,
                    sortType
                )
            }
        )
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.EVENT_CLICK,
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.LINK_TYPE_BOTTOM_SHEET,
                MeasurementConstant.Key.KEY_LINK_DESC to TrueCloudV3TrackAnalytic.LINK_DESC_SORT_BY
            )
        )
    }
    fun onClickCreateNewFolder() {
        router.execute(OptingMainToCreateNewFolder)
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.EVENT_CLICK,
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.LINK_TYPE_BOTTOM_SHEET,
                MeasurementConstant.Key.KEY_LINK_DESC to TrueCloudV3TrackAnalytic.LINK_DESC_NEW_FOLDER
            )
        )
    }

    fun onReceiveSortBy(sortType: SortType) {
        onSortBy.value = sortType
    }
    fun onReceiveCreateFolder(folderName: String) {
        onCreateNewFolder.value = folderName
    }

    fun onConfirmEmpty() {
        onConfirmEmpty.value = Unit
    }
    fun onClickEmptyTrash() {
        onEmptyTrash.value = Unit
    }
}
