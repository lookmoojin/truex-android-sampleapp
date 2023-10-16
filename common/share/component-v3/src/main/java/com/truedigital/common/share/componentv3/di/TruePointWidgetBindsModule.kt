package com.truedigital.common.share.componentv3.di

import com.truedigital.common.share.componentv3.widget.truepoint.data.TruePointCardStyleCacheRepository
import com.truedigital.common.share.componentv3.widget.truepoint.data.TruePointCardStyleCacheRepositoryImpl
import com.truedigital.common.share.componentv3.widget.truepoint.data.TruePointWidgetConfigRepository
import com.truedigital.common.share.componentv3.widget.truepoint.data.TruePointWidgetConfigRepositoryImpl
import com.truedigital.common.share.componentv3.widget.truepoint.domain.GetTruePointTitleUseCase
import com.truedigital.common.share.componentv3.widget.truepoint.domain.GetTruePointTitleUseCaseImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface TruePointWidgetBindsModule {

    @Binds
    @Singleton
    fun bindsTruePointWidgetConfigRepository(
        truePointWidgetConfigRepositoryImpl: TruePointWidgetConfigRepositoryImpl
    ): TruePointWidgetConfigRepository

    @Binds
    @Singleton
    fun bindsTruePointCardStyleCacheRepository(
        truePointCardStyleCacheRepositoryImpl: TruePointCardStyleCacheRepositoryImpl
    ): TruePointCardStyleCacheRepository

    @Binds
    fun bindsGetTruePointTitleUseCase(
        getTruePointTitleUseCaseImpl: GetTruePointTitleUseCaseImpl
    ): GetTruePointTitleUseCase
}
