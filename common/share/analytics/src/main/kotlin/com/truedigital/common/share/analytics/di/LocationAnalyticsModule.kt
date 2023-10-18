package com.truedigital.common.share.analytics.di

import com.truedigital.common.share.analytics.measurement.usecase.TrackDeviceResolutionUseCase
import com.truedigital.common.share.analytics.measurement.usecase.TrackDeviceResolutionUseCaseImpl
import com.truedigital.common.share.analytics.measurement.usecase.truemoney.TrackCustomerHasTrueMoneyUseCase
import com.truedigital.common.share.analytics.measurement.usecase.truemoney.TrackCustomerHasTrueMoneyUseCaseImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface LocationAnalyticsModule {

    @Binds
    @Singleton
    fun bindsTrackDeviceResolutionUseCase(
        trackDeviceResolutionUseCaseImpl: TrackDeviceResolutionUseCaseImpl
    ): TrackDeviceResolutionUseCase

    @Binds
    @Singleton
    fun bindsTrackCustomerHasTrueMoneyUseCase(
        trackCustomerHasTrueMoneyUseCaseImpl: TrackCustomerHasTrueMoneyUseCaseImpl
    ): TrackCustomerHasTrueMoneyUseCase
}
