package com.truedigital.features.music.di

import com.truedigital.features.music.domain.forceloginbanner.usecase.GetLoginBannerUseCase
import com.truedigital.features.music.domain.forceloginbanner.usecase.GetLoginBannerUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
interface MusicForceLoginBannerModule {

    @Binds
    fun bindsGetLoginBannerUseCase(
        getLoginBannerUseCaseImpl: GetLoginBannerUseCaseImpl
    ): GetLoginBannerUseCase
}
