package com.truedigital.core.domain.usecase

import com.truedigital.core.data.device.repository.LocalizationRepository
import javax.inject.Inject

interface SetLocalizationUseCase {
    fun execute(language: LocalizationRepository.Localization)
    fun execute(countryCode: String, languageCode: String)
}

class SetLocalizationUseCaseImpl @Inject constructor(
    private val localizationRepository: LocalizationRepository
) : SetLocalizationUseCase {
    override fun execute(language: LocalizationRepository.Localization) {
        localizationRepository.setLocalization(language.countryCode, language.languageCode)
    }

    override fun execute(countryCode: String, languageCode: String) {
        localizationRepository.setLocalization(countryCode, languageCode)
    }
}
