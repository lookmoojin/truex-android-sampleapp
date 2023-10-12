package com.truedigital.features.truecloudv3.presentation.viewmodel

import android.os.Bundle
import com.truedigital.common.share.analytics.di.AnalyticsModule
import com.truedigital.common.share.analytics.measurement.AnalyticManagerInterface
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_CONTACT_PHONE_LABEL_DATA
import com.truedigital.features.truecloudv3.common.TrueCloudV3TrackAnalytic
import com.truedigital.features.truecloudv3.domain.model.CustomPhoneLabelModel
import com.truedigital.features.truecloudv3.navigation.ContactSelectLabelBottomSheetToCustomLabelDialog
import com.truedigital.features.truecloudv3.navigation.router.ContactSelectLabelRouterUseCase
import com.truedigital.foundation.extension.SingleLiveEvent
import javax.inject.Inject
import javax.inject.Named

class CreateContactSelectLabelBottomSheetDialogViewModel @Inject constructor(
    private val router: ContactSelectLabelRouterUseCase,
    @Named(AnalyticsModule.FIREBASE_ANALYTIC)
    private val analyticManagerInterface: AnalyticManagerInterface
) : ScopedViewModel() {
    val onSetupView = SingleLiveEvent<CustomPhoneLabelModel>()
    val onSelected = SingleLiveEvent<CustomPhoneLabelModel>()
    var customPhoneLabelModel: CustomPhoneLabelModel? = null
    fun setPhoneLabel(model: CustomPhoneLabelModel) {
        customPhoneLabelModel = model
        onSetupView.value = model
    }

    fun onClickLabel(lable: String) {
        customPhoneLabelModel?.label = lable
        onSelected.value = customPhoneLabelModel
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.EVENT_CLICK,
                MeasurementConstant.Key.KEY_EVENT_NAME to TrueCloudV3TrackAnalytic.LINK_TYPE_BOTTOM_SHEET,
                MeasurementConstant.Key.KEY_LINK_DESC to lable
            )
        )
    }
    fun onCompleteCustomLabel(model: CustomPhoneLabelModel) {
        customPhoneLabelModel = model
        onSelected.value = customPhoneLabelModel
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.EVENT_CLICK,
                MeasurementConstant.Key.KEY_EVENT_NAME to TrueCloudV3TrackAnalytic.LINK_TYPE_BOTTOM_SHEET,
                MeasurementConstant.Key.KEY_LINK_DESC to TrueCloudV3TrackAnalytic.LINK_DESC_CUSTOM_LABEL
            )
        )
    }

    fun onClickCustom() {
        router.execute(
            ContactSelectLabelBottomSheetToCustomLabelDialog,
            Bundle().apply {
                putParcelable(
                    KEY_BUNDLE_TRUE_CLOUD_CONTACT_PHONE_LABEL_DATA,
                    customPhoneLabelModel
                )
            }
        )
    }
}
