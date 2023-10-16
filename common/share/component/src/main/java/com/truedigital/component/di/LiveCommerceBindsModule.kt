package com.truedigital.component.di

import com.truedigital.component.widget.livecommerce.data.repository.GetActiveLiveStreamRepository
import com.truedigital.component.widget.livecommerce.data.repository.GetActiveLiveStreamRepositoryImpl
import com.truedigital.component.widget.livecommerce.domain.usecase.ConvertShelfDataToLiveStreamDataUseCase
import com.truedigital.component.widget.livecommerce.domain.usecase.ConvertShelfDataToLiveStreamDataUseCaseImpl
import com.truedigital.component.widget.livecommerce.domain.usecase.CreateCommerceLiveDeeplinkUseCase
import com.truedigital.component.widget.livecommerce.domain.usecase.CreateCommerceLiveDeeplinkUseCaseImpl
import com.truedigital.component.widget.livecommerce.domain.usecase.GetActiveLiveStreamUseCase
import com.truedigital.component.widget.livecommerce.domain.usecase.GetActiveLiveStreamUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
interface LiveCommerceBindsModule {

    @Binds
    fun bindsConvertShelfDataToLiveStreamDataUseCase(
        convertShelfDataToLiveStreamDataUseCaseImpl: ConvertShelfDataToLiveStreamDataUseCaseImpl
    ): ConvertShelfDataToLiveStreamDataUseCase

    @Binds
    fun bindsCreateCommerceLiveDeeplinkUseCase(
        createCommerceLiveDeeplinkUseCaseImpl: CreateCommerceLiveDeeplinkUseCaseImpl
    ): CreateCommerceLiveDeeplinkUseCase

    @Binds
    fun bindsGetActiveLiveStreamUseCase(
        getActiveLiveStreamUseCaseImpl: GetActiveLiveStreamUseCaseImpl
    ): GetActiveLiveStreamUseCase

    @Binds
    fun bindsGetActiveLiveStreamRepository(
        getActiveLiveStreamRepositoryImpl: GetActiveLiveStreamRepositoryImpl
    ): GetActiveLiveStreamRepository
}
