package com.truedigital.common.share.analytics.measurement.newrelic

import com.newrelic.agent.android.NewRelic
import com.truedigital.common.share.analytics.measurement.base.MeasurementAnalyticInterface
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant
import javax.inject.Inject

class NewRelicManager @Inject constructor() : MeasurementAnalyticInterface<String, Any> {

    override fun trackScreen(screen: String) {
        val customEventMap = mapOf("Name" to screen)

        NewRelic.recordCustomEvent("Screen", customEventMap)
    }

    override fun trackEvent(event: Any) {
        when (event) {
            is HashMap<*, *> -> {
                for ((key, value) in event) {
                    trackMapEvent(key as? String, value as? String)
                }
            }
        }
    }

    fun setAttribute(name: String, value: String) {
        NewRelic.setAttribute(name, value)
    }

    fun recordHandledException(exception: Exception, exceptionAttributes: Map<String, Any>) {
        NewRelic.recordHandledException(exception, exceptionAttributes)
    }

    private fun trackMapEvent(key: String?, value: String?) {
        if (key == MeasurementConstant.Key.KEY_EVENT_NAME) {
            val customEventMap = mapOf("Name" to value)

            NewRelic.recordCustomEvent("Event", customEventMap)
        } else {
            val modifyValue = value.toString().take(100)
            val customEventMap = mapOf("Name" to modifyValue)

            NewRelic.recordCustomEvent("Event", customEventMap)
        }
    }
}
