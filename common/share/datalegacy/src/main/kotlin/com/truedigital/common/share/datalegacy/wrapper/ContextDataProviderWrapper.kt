package com.truedigital.common.share.datalegacy.wrapper

import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.provider.ContextDataProvider
import java.util.Locale
import javax.inject.Inject

interface ContextDataProviderWrapper {
    fun get(): ContextDataProvider
}

class ContextDataProviderWrapperImpl @Inject constructor(
    private val contextDataProvider: ContextDataProvider,
    localizationRepository: LocalizationRepository
) : ContextDataProviderWrapper {
    init {
        contextDataProvider.updateContextLocale(
            localizationRepository.getAppLanguageCode().toLowerCase(
                Locale.ENGLISH
            )
        )
    }

    override fun get(): ContextDataProvider {
        return contextDataProvider
    }
}
