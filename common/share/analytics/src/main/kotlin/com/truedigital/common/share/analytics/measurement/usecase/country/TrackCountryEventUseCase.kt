package com.truedigital.common.share.analytics.measurement.usecase.country

import com.truedigital.common.share.analytics.measurement.AnalyticManager
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant.Event
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant.Key
import javax.inject.Inject

interface TrackCountryEventUseCase {
    fun execute(
        oldCountry: String = "",
        newCountry: String
    )
}

class TrackCountryEventUseCaseImpl @Inject constructor(
    val analyticManager: AnalyticManager
) : TrackCountryEventUseCase {

    override fun execute(oldCountry: String, newCountry: String) {
        with(analyticManager) {
            trackEvent(
                hashMapOf(
                    Key.KEY_EVENT_NAME to Event.EVENT_CHANGE_COUNTRY,
                    Key.KEY_CHANGE_COUNTRY_CURRENT_SETTING to oldCountry,
                    Key.KEY_CHANGE_COUNTRY_NEW_SETTING to newCountry
                )
            )
        }
    }
}
