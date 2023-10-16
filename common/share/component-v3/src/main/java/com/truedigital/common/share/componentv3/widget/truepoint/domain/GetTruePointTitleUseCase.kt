package com.truedigital.common.share.componentv3.widget.truepoint.domain

import com.truedigital.common.share.componentv3.widget.truepoint.data.TruePointWidgetConfigRepository
import com.truedigital.core.data.device.repository.LocalizationRepository
import javax.inject.Inject

interface GetTruePointTitleUseCase {
    suspend fun execute(): String?
}

class GetTruePointTitleUseCaseImpl @Inject constructor(
    private val truePointWidgetConfigRepository: TruePointWidgetConfigRepository,
    private val localizationRepository: LocalizationRepository
) : GetTruePointTitleUseCase {

    override suspend fun execute(): String? {
        val appLocalization = localizationRepository.getAppLocalizationForEnTh()
        val config = truePointWidgetConfigRepository.getTruePointConfig(
            countryCode = localizationRepository.getAppCountryCode()
        )
        return if (appLocalization == LocalizationRepository.Localization.TH) {
            config?.titleTh
        } else {
            config?.titleEn
        }
    }
}
