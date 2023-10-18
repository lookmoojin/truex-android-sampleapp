package com.truedigital.common.share.analytics.di

import com.truedigital.common.share.analytics.measurement.usecase.country.TrackCountryEventUseCase
import com.truedigital.common.share.analytics.measurement.usecase.country.TrackCountryEventUseCaseImpl
import com.truedigital.common.share.analytics.measurement.usecase.country.TrackCountryUserPropertiesUseCase
import com.truedigital.common.share.analytics.measurement.usecase.country.TrackCountryUserPropertiesUseCaseImpl
import com.truedigital.common.share.analytics.measurement.usecase.language.TrackLanguageUserPropertiesUseCase
import com.truedigital.common.share.analytics.measurement.usecase.language.TrackLanguageUserPropertiesUseCaseImpl
import dagger.Binds
import dagger.Module

@Module
interface UserAnalyticsModule {

    @Binds
    fun bindsTrackCountryEventUseCase(
        trackCountryEventUseCaseImpl: TrackCountryEventUseCaseImpl
    ): TrackCountryEventUseCase

    @Binds
    fun bindsTrackCountryUserPropertiesUseCase(
        trackCountryUserPropertiesUseCaseImpl: TrackCountryUserPropertiesUseCaseImpl
    ): TrackCountryUserPropertiesUseCase

    @Binds
    fun bindsTrackLanguageUserPropertiesUseCase(
        trackLanguageUserPropertiesUseCaseImpl: TrackLanguageUserPropertiesUseCaseImpl
    ): TrackLanguageUserPropertiesUseCase
}
