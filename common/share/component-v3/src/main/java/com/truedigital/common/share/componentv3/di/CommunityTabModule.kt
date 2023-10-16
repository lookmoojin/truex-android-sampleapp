package com.truedigital.common.share.componentv3.di

import com.truedigital.common.share.componentv3.widget.ads.domain.usecase.GetAdsPreLoadConfigUseCase
import com.truedigital.common.share.componentv3.widget.ads.domain.usecase.GetAdsPreLoadConfigUseCaseImpl
import com.truedigital.common.share.componentv3.widget.feedmenutab.data.GetCommunityTabConfigRepository
import com.truedigital.common.share.componentv3.widget.feedmenutab.data.GetCommunityTabConfigRepositoryImpl
import com.truedigital.common.share.componentv3.widget.feedmenutab.domain.usecase.GetAmityConfigUseCase
import com.truedigital.common.share.componentv3.widget.feedmenutab.domain.usecase.GetAmityConfigUseCaseImpl
import com.truedigital.common.share.componentv3.widget.feedmenutab.domain.usecase.GetCommunityTabConfigUseCase
import com.truedigital.common.share.componentv3.widget.feedmenutab.domain.usecase.GetCommunityTabConfigUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
interface CommunityTabModule {

    @Binds
    fun bindsGetCommunityTabConfigRepository(
        getCommunityTabConfigRepositoryImpl: GetCommunityTabConfigRepositoryImpl
    ): GetCommunityTabConfigRepository

    @Binds
    fun bindsGetAmityConfigUseCase(
        getAmityConfigUseCaseImpl: GetAmityConfigUseCaseImpl
    ): GetAmityConfigUseCase

    @Binds
    fun bindsGetCommunityTabConfigUseCase(
        getCommunityTabConfigUseCaseImpl: GetCommunityTabConfigUseCaseImpl
    ): GetCommunityTabConfigUseCase

    @Binds
    fun bindGetAdsPreLoadConfigUseCase(
        getAdsPreLoadConfigUseCaseImpl: GetAdsPreLoadConfigUseCaseImpl
    ): GetAdsPreLoadConfigUseCase
}
