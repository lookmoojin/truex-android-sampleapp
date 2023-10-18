package com.truedigital.core.di

import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.data.device.repository.LocalizationRepositoryImpl
import com.truedigital.core.domain.usecase.GetLocalizationUseCase
import com.truedigital.core.domain.usecase.GetLocalizationUseCaseImpl
import com.truedigital.core.domain.usecase.SetLocalizationUseCase
import com.truedigital.core.domain.usecase.SetLocalizationUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
interface LocalizationModule {

    @Binds
    fun bindsLocalizationRepository(
        localizationRepositoryImpl: LocalizationRepositoryImpl
    ): LocalizationRepository

    @Binds
    fun bindsGetLocalizationUseCase(
        getLocalizationUseCaseImpl: GetLocalizationUseCaseImpl
    ): GetLocalizationUseCase

    @Binds
    fun bindsSetLocalizationUseCase(
        setLocalizationUseCaseImpl: SetLocalizationUseCaseImpl
    ): SetLocalizationUseCase
}
