package com.truedigital.common.share.analytics.measurement.appsflyer

import android.content.Context
import com.appsflyer.AppsFlyerLib
import com.truedigital.common.share.analytics.measurement.base.MeasurementAnalyticInterface
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant.Key.KEY_EVENT_NAME
import javax.inject.Inject

class AppsFlyerAnalyticsManager @Inject constructor(
    private val appsFlyerLib: AppsFlyerLib,
    private val context: Context
) : MeasurementAnalyticInterface<String, Map<String, Any>> {

    override fun trackScreen(screen: String) {
        // Do noting
    }

    override fun trackEvent(event: Map<String, Any>) {
        val eventName = event[KEY_EVENT_NAME].toString()
        appsFlyerLib.logEvent(context, eventName, event)
    }
}
