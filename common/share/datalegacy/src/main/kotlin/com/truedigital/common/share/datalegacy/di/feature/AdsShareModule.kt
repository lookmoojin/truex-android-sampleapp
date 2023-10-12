package com.truedigital.common.share.datalegacy.di.feature

import com.truedigital.common.share.datalegacy.domain.ads.usecase.AdvertisingIdUseCase
import com.truedigital.common.share.datalegacy.domain.ads.usecase.AdvertisingIdUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
interface AdsShareModule {

    @Binds
    fun bindsAdvertisingIdUseCase(useCase: AdvertisingIdUseCaseImpl): AdvertisingIdUseCase
}
