package com.truedigital.common.share.analytics.measurement.usecase.country

import com.truedigital.common.share.analytics.measurement.AnalyticManager
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant.Key.KEY_COUNTRY_FLAG
import javax.inject.Inject

interface TrackCountryUserPropertiesUseCase {
    fun execute(country: String)
}

class TrackCountryUserPropertiesUseCaseImpl @Inject constructor(
    val analyticManager: AnalyticManager
) : TrackCountryUserPropertiesUseCase {

    override fun execute(country: String) {
        with(analyticManager) {
            trackUserProperties(
                KEY_COUNTRY_FLAG, country
            )
        }
    }
}
