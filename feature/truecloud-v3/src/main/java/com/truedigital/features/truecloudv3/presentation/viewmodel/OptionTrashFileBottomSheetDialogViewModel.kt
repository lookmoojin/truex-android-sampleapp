package com.truedigital.features.truecloudv3.presentation.viewmodel

import com.truedigital.common.share.analytics.di.AnalyticsModule
import com.truedigital.common.share.analytics.measurement.AnalyticManagerInterface
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.features.truecloudv3.common.FileMimeType
import com.truedigital.features.truecloudv3.common.TrueCloudV3TrackAnalytic
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.foundation.extension.SingleLiveEvent
import javax.inject.Inject
import javax.inject.Named

class OptionTrashFileBottomSheetDialogViewModel @Inject constructor(
    @Named(AnalyticsModule.FIREBASE_ANALYTIC)
    private val analyticManagerInterface: AnalyticManagerInterface
) : ScopedViewModel() {

    val clickDelete = SingleLiveEvent<MutableList<TrueCloudFilesModel.File>>()
    val onConfirmDelete = SingleLiveEvent<Unit>()
    val onRestore = SingleLiveEvent<MutableList<TrueCloudFilesModel.File>>()
    private var selectedItemList = ArrayList<TrueCloudFilesModel.File>()

    fun init(
        itemList: ArrayList<TrueCloudFilesModel.File>,
    ) {
        selectedItemList = itemList
    }

    fun onClickRestore() {
        onRestore.value = selectedItemList
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_EVENT_NAME to TrueCloudV3TrackAnalytic.EVENT_CLICK,
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.LINK_TYPE_BOTTOM_SHEET,
                MeasurementConstant.Key.KEY_LINK_DESC to TrueCloudV3TrackAnalytic.LINK_DESC_RESTORE
            )
        )
    }

    fun onClickDelete() {
        onConfirmDelete.value = Unit
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_EVENT_NAME to TrueCloudV3TrackAnalytic.EVENT_CLICK,
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.LINK_TYPE_BOTTOM_SHEET,
                MeasurementConstant.Key.KEY_LINK_DESC to TrueCloudV3TrackAnalytic.LINK_DESC_DELETE_PERMANENT
            )
        )
    }

    fun deleteSelectedItem() {
        val filterData = selectedItemList.filter { it.fileMimeType != FileMimeType.CONTACT }.toMutableList()
        clickDelete.value = filterData
    }
}
