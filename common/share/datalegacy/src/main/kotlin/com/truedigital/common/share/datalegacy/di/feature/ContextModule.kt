package com.truedigital.common.share.datalegacy.di.feature

import com.truedigital.common.share.datalegacy.wrapper.ContextDataProviderWrapper
import com.truedigital.common.share.datalegacy.wrapper.ContextDataProviderWrapperImpl
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.provider.ContextDataProvider
import dagger.Module
import dagger.Provides

@Module
object ContextModule {

    @Provides
    fun provideContextDataProviderWrapper(
        contextDataProvider: ContextDataProvider,
        localizationRepository: LocalizationRepository
    ): ContextDataProviderWrapper {
        return ContextDataProviderWrapperImpl(
            contextDataProvider = contextDataProvider,
            localizationRepository = localizationRepository
        )
    }
}
