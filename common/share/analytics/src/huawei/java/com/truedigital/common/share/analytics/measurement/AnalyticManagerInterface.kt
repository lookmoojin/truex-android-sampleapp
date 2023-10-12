package com.truedigital.common.share.analytics.measurement

import com.truedigital.common.share.analytics.measurement.base.platform.PlatformAnalyticModel
import javax.inject.Inject

interface AnalyticManagerInterface {
    fun trackEvent(event: HashMap<String, Any>)
    fun trackUserProperties(event: HashMap<String, String?>?, function: String)
    fun trackScreen(analyticModel: PlatformAnalyticModel)
}

class AnalyticManagerInterfaceImpl @Inject constructor(
    private val analyticManager: AnalyticManager
) : AnalyticManagerInterface {
    override fun trackEvent(event: HashMap<String, Any>) {
        analyticManager.trackEvent(event)
    }

    override fun trackUserProperties(event: HashMap<String, String?>?, function: String) {
        analyticManager.trackUserProperties(event, function)
    }

    override fun trackScreen(analyticModel: PlatformAnalyticModel) {
        analyticManager.trackScreen(analyticModel)
    }
}
