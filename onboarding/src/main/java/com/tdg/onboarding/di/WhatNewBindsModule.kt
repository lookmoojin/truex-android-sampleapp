package com.tdg.onboarding.di

import com.tdg.onboarding.data.repository.WhatNewConfigRepository
import com.tdg.onboarding.data.repository.WhatNewConfigRepositoryImpl
import com.tdg.onboarding.domain.usecase.GetWhatNewConfigUseCase
import com.tdg.onboarding.domain.usecase.GetWhatNewConfigUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
interface WhatNewBindsModule {

    @Binds
    fun bindsWhatNewConfigRepository(
        whatNewConfigRepositoryImpl: WhatNewConfigRepositoryImpl
    ): WhatNewConfigRepository

    @Binds
    fun bindsGetWhatNewConfigUseCase(
        getWhatNewConfigUseCaseImpl: GetWhatNewConfigUseCaseImpl
    ): GetWhatNewConfigUseCase
}