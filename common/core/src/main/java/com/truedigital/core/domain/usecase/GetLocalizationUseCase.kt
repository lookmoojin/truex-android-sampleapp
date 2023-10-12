package com.truedigital.core.domain.usecase

import com.truedigital.core.data.device.repository.LocalizationRepository
import javax.inject.Inject

interface GetLocalizationUseCase {
    fun execute(): LocalizationRepository.Localization
}

class GetLocalizationUseCaseImpl @Inject constructor(
    private val localizationRepository: LocalizationRepository
) : GetLocalizationUseCase {
    override fun execute(): LocalizationRepository.Localization {
        return localizationRepository.getAppLocalization()
    }
}
