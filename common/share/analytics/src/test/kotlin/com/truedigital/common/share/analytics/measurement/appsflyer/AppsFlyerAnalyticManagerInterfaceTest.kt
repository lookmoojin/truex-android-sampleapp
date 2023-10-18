package com.truedigital.common.share.analytics.measurement.appsflyer

import com.truedigital.common.share.analytics.measurement.AnalyticManager
import com.truedigital.common.share.analytics.measurement.base.platform.PlatformAnalyticModel
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AppsFlyerAnalyticManagerInterfaceTest {

    private val mockAnalyticManager: AnalyticManager = mockk()
    private val appsFlyerAnalyticsManager: AppsFlyerAnalyticsManager = mockk()
    private lateinit var appsFlyerAnalyticManager: AppsFlyerAnalyticManagerInterface

    @BeforeEach
    fun setUp() {
        appsFlyerAnalyticManager = AppsFlyerAnalyticManagerInterface(mockAnalyticManager, appsFlyerAnalyticsManager)
    }

    @Test
    fun `test trackEvent`() {
        val event: HashMap<String, Any> = hashMapOf(
            "eventName" to "ExampleEvent",
            "eventValue" to 12345
        )

        justRun { appsFlyerAnalyticsManager.trackEvent(event) }

        appsFlyerAnalyticManager.trackEvent(event)

        verify { appsFlyerAnalyticsManager.trackEvent(event) }
    }

    @Test
    fun `test trackUserProperties`() {
        val event: HashMap<String, String?> = hashMapOf(
            "property1" to "value1",
            "property2" to "value2"
        )
        val function = "ExampleFunction"

        justRun { mockAnalyticManager.trackUserProperties(event, function) }

        appsFlyerAnalyticManager.trackUserProperties(event, function)

        verify { mockAnalyticManager.trackUserProperties(event, function) }
    }

    @Test
    fun `test trackScreen`() {
        val analyticModel = PlatformAnalyticModel().apply {
            screenName = "ExampleScreen"
        }

        justRun { mockAnalyticManager.trackScreen(analyticModel) }

        appsFlyerAnalyticManager.trackScreen(analyticModel)

        verify { mockAnalyticManager.trackScreen(analyticModel) }
    }
}
