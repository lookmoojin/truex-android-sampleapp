package com.truedigital.common.share.analytics.measurement.appsflyer

import com.truedigital.common.share.analytics.BuildConfig
import com.truedigital.common.share.analytics.measurement.AnalyticManager
import com.truedigital.common.share.analytics.measurement.AnalyticManagerInterface
import com.truedigital.common.share.analytics.measurement.base.platform.PlatformAnalyticModel
import javax.inject.Inject

class AppsFlyerAnalyticManagerInterface @Inject constructor(
    private val analyticManager: AnalyticManager,
    private val appsFlyerAnalyticsManager: AppsFlyerAnalyticsManager
) : AnalyticManagerInterface {
    override fun trackEvent(event: HashMap<String, Any>) {
        BuildConfig.BUILD_TYPE
        appsFlyerAnalyticsManager.trackEvent(event)
    }

    override fun trackUserProperties(event: HashMap<String, String?>?, function: String) {
        analyticManager.trackUserProperties(event, function)
    }

    override fun trackScreen(analyticModel: PlatformAnalyticModel) {
        analyticManager.trackScreen(analyticModel)
    }
}
