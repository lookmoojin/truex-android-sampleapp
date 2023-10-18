package com.truedigital.common.share.analytics.measurement.usecase.language

import com.truedigital.common.share.analytics.measurement.AnalyticManager
import com.truedigital.core.data.device.repository.LocalizationRepository
import javax.inject.Inject
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant.Key as Key

interface TrackLanguageUserPropertiesUseCase {
    fun execute()
}

class TrackLanguageUserPropertiesUseCaseImpl @Inject constructor(
    private val analyticManager: AnalyticManager,
    private val localizationRepository: LocalizationRepository
) : TrackLanguageUserPropertiesUseCase {

    override fun execute() {
        analyticManager.trackUserProperties(
            Key.KEY_LANGUAGE, localizationRepository.getAppLanguageCode()
        )
    }
}
