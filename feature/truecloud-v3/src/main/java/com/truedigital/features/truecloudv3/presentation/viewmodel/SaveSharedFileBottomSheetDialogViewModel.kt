package com.truedigital.features.truecloudv3.presentation.viewmodel

import com.truedigital.common.share.analytics.di.AnalyticsModule
import com.truedigital.common.share.analytics.measurement.AnalyticManagerInterface
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.features.truecloudv3.common.TrueCloudV3TrackAnalytic
import com.truedigital.foundation.extension.SingleLiveEvent
import javax.inject.Inject
import javax.inject.Named

class SaveSharedFileBottomSheetDialogViewModel @Inject constructor(
    @Named(AnalyticsModule.FIREBASE_ANALYTIC)
    private val analyticManagerInterface: AnalyticManagerInterface
) : ScopedViewModel() {

    val onSaveToDevice = SingleLiveEvent<Pair<String?, String?>>()
    val onSaveToCloud = SingleLiveEvent<Pair<String?, String?>>()

    var sharedId: String? = null
    var sharedToken: String? = null

    fun setId(id: String) {
        sharedId = id
    }

    fun setToken(token: String) {
        sharedToken = token
    }

    fun onSaveToDeviceClick() {
        onSaveToDevice.value = Pair(sharedId, sharedToken)
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.EVENT_CLICK,
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.LINK_TYPE_BOTTOM_SHEET,
                MeasurementConstant.Key.KEY_LINK_DESC to TrueCloudV3TrackAnalytic.LINK_DESC_SAVE_TO_DEVICE
            )
        )
    }

    fun onSaveToCloudClick() {
        onSaveToCloud.value = Pair(sharedId, sharedToken)
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.EVENT_CLICK,
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.LINK_TYPE_BOTTOM_SHEET,
                MeasurementConstant.Key.KEY_LINK_DESC to TrueCloudV3TrackAnalytic.LINK_DESC_SAVE_TO_CLOUD
            )
        )
    }
}
