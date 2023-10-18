package com.truedigital.share.data.firestoreconfig.initialappconfig.usecase

import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.share.data.firestoreconfig.initialappconfig.repository.InitialAppConfigRepository
import javax.inject.Inject

interface GetInitialAppConfigUsecase {
    fun execute()
}

class GetInitialAppConfigUsecaseImpl @Inject constructor(
    val initialAppConfigRepository: InitialAppConfigRepository,
    private val localizationRepository: LocalizationRepository
) : GetInitialAppConfigUsecase {
    override fun execute() {
        initialAppConfigRepository.loadInitialAppConfig(
            countryCode = localizationRepository.getAppCountryCode()
        )
    }
}
