package com.truedigital.common.share.analytics.injections

import com.truedigital.common.share.analytics.di.AnalyticsModule
import com.truedigital.common.share.analytics.di.LocationAnalyticsModule
import com.truedigital.common.share.analytics.di.UserAnalyticsModule
import com.truedigital.common.share.analytics.measurement.AnalyticManager
import com.truedigital.common.share.analytics.measurement.AnalyticManagerInterface
import com.truedigital.common.share.analytics.measurement.base.platform.PlatformAnalyticManager
import com.truedigital.common.share.analytics.measurement.firebase.FirebaseAnalyticManager
import com.truedigital.common.share.analytics.measurement.newrelic.NewRelicManager
import com.truedigital.common.share.analytics.measurement.usecase.TrackDeviceResolutionUseCase
import com.truedigital.common.share.analytics.measurement.usecase.country.TrackCountryEventUseCase
import com.truedigital.common.share.analytics.measurement.usecase.country.TrackCountryUserPropertiesUseCase
import com.truedigital.common.share.analytics.measurement.usecase.language.TrackLanguageUserPropertiesUseCase
import com.truedigital.common.share.analytics.measurement.usecase.truemoney.TrackCustomerHasTrueMoneyUseCase
import com.truedigital.common.share.datalegacy.injections.DataLegacySubComponent
import com.truedigital.core.injections.CoreSubComponent
import dagger.Component
import dagger.Subcomponent
import javax.inject.Named
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        UserAnalyticsModule::class,
        AnalyticsModule::class,
        LocationAnalyticsModule::class
    ],
    dependencies = [
        CoreSubComponent::class,
        DataLegacySubComponent::class,

    ]
)
interface AnalyticsComponent {

    companion object {
        private lateinit var analyticsComponent: AnalyticsComponent

        fun initialize(analyticsComponent: AnalyticsComponent) {
            this.analyticsComponent = analyticsComponent
        }

        fun getInstance(): AnalyticsComponent {
            if (!(::analyticsComponent.isInitialized)) {
                error("AnalyticsComponent not initialize")
            }
            return analyticsComponent
        }
    }

    @Component.Factory
    interface Factory {
        fun create(
            coreSubComponent: CoreSubComponent,
            dataLegacySubComponent: DataLegacySubComponent,
        ): AnalyticsComponent
    }

    fun getAnalyticsSubComponent(): AnalyticsSubComponent

    fun inject(platformAnalyticManager: PlatformAnalyticManager)
    fun inject(analyticManager: AnalyticManager)
}

@Subcomponent
interface AnalyticsSubComponent {
    @Named(AnalyticsModule.FIREBASE_ANALYTIC)
    fun getFirebaseAnalyticManagerInterface(): AnalyticManagerInterface

    @Named(AnalyticsModule.APPSFLYER_ANALYTIC)
    fun getAppFlyerAnalyticManagerInterface(): AnalyticManagerInterface

    fun getAnalyticManager(): AnalyticManager
    fun getFirebaseAnalyticManager(): FirebaseAnalyticManager
    fun getNewRelicManager(): NewRelicManager

    // Usa cases
    fun getTrackLanguageUserPropertiesUseCase(): TrackLanguageUserPropertiesUseCase
    fun getTrackDeviceResolutionUseCase(): TrackDeviceResolutionUseCase
    fun getTrackCustomerHasTrueMoneyUseCase(): TrackCustomerHasTrueMoneyUseCase
    fun getTrackCountryUserPropertiesUseCase(): TrackCountryUserPropertiesUseCase
    fun getTrackCountryEventUseCase(): TrackCountryEventUseCase
}
