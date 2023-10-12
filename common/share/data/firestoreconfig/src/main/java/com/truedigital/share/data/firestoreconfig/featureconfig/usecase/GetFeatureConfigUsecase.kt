package com.truedigital.share.data.firestoreconfig.featureconfig.usecase

import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.share.data.firestoreconfig.featureconfig.repository.FeatureConfigRepository
import javax.inject.Inject

interface GetFeatureConfigUsecase {
    fun execute()
}

class GetFeatureConfigUsecaseImpl @Inject constructor(
    val featureConfigRepository: FeatureConfigRepository,
    private val localizationRepository: LocalizationRepository
) : GetFeatureConfigUsecase {
    override fun execute() {
        featureConfigRepository.loadInitialAppConfig(
            countryCode = localizationRepository.getAppCountryCode()
        )
    }
}
