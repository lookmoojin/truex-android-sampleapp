package com.truedigital.common.share.analytics.measurement.base

interface MeasurementAnalyticInterface<SCREEN, EVENT> {
    fun trackScreen(screen: SCREEN)
    fun trackEvent(event: EVENT)
}
