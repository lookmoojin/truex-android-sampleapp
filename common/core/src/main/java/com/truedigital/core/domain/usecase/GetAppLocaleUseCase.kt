package com.truedigital.core.domain.usecase

import com.truedigital.core.data.device.repository.LocalizationRepository
import java.util.Locale

interface GetAppLocaleUseCase {
    fun execute(): Locale
}

class GetAppLocaleUseCaseImpl(
    private val localizationRepository: LocalizationRepository
) : GetAppLocaleUseCase {
    override fun execute(): Locale {
        return localizationRepository.getAppLocale()
    }
}
