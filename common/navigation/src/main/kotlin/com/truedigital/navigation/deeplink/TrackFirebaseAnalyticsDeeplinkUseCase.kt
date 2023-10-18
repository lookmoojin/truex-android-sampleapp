package com.truedigital.navigation.deeplink

import com.truedigital.common.share.analytics.measurement.AnalyticManager
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant
import com.truedigital.component.constant.DynamicLinkConstant.Companion.APPSFLYER_KEY_LINK
import com.truedigital.core.utils.SharedPrefsUtils
import javax.inject.Inject

interface TrackFirebaseAnalyticsDeeplinkUseCase {
    suspend fun execute(resultStatus: Boolean, deeplink: String)
}

class TrackFirebaseAnalyticsDeeplinkUseCaseImpl @Inject constructor(
    private val sharedPrefs: SharedPrefsUtils,
    private val analyticManager: AnalyticManager
) : TrackFirebaseAnalyticsDeeplinkUseCase {
    override suspend fun execute(resultStatus: Boolean, deeplink: String) {
        if (deeplink.isNotEmpty() && checkDeepLinkValue()) {
            if (resultStatus) {
                analyticManager.trackEvent(
                    hashMapOf(
                        MeasurementConstant.Key.KEY_EVENT_NAME to MeasurementConstant.Deeplink.Event.DEEPLINK_EVENT_DEEP_LINKING_OPEN,
                        MeasurementConstant.Key.KEY_LINK_DESC to deeplink.take(100)
                    )
                )
            } else {
                analyticManager.trackEvent(
                    hashMapOf(
                        MeasurementConstant.Key.KEY_EVENT_NAME to MeasurementConstant.Event.EVENT_ERROR_NOT_SHOW_VIEW,
                        MeasurementConstant.Key.KEY_ERROR_NAME to MeasurementConstant.Deeplink.Event.DEEPLINK_EVENT_DEEP_LINKING_ERROR,
                        MeasurementConstant.Key.KEY_TITLE to MeasurementConstant.Deeplink.Title.DEEPLINK_TITLE,
                        MeasurementConstant.Key.KEY_ERROR_DESC to deeplink.take(100)
                    )
                )
            }
        }
    }

    private fun checkDeepLinkValue(): Boolean {
        return sharedPrefs.get(APPSFLYER_KEY_LINK, "").isNotEmpty()
    }
}
