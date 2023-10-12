package com.truedigital.features.truecloudv3.di

import com.truedigital.features.truecloudv3.data.repository.ConfigIntroImageRepository
import com.truedigital.features.truecloudv3.data.repository.ConfigIntroImageRepositoryImpl
import com.truedigital.features.truecloudv3.domain.usecase.ConfigIntroImageUseCase
import com.truedigital.features.truecloudv3.domain.usecase.ConfigIntroImageUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
interface TrueCloudV3IntroBindsModule {

    @Binds
    fun bindsConfigIntroImageRepository(
        configIntroImageRepositoryImpl: ConfigIntroImageRepositoryImpl
    ): ConfigIntroImageRepository

    @Binds
    fun bindsConfigIntroImageUseCase(
        configIntroImageUseCaseImpl: ConfigIntroImageUseCaseImpl
    ): ConfigIntroImageUseCase
}
