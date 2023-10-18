package com.truedigital.common.share.analytics.di

import android.content.Context
import com.appsflyer.AppsFlyerLib
import com.google.firebase.analytics.FirebaseAnalytics
import com.truedigital.common.share.analytics.measurement.AnalyticManager
import com.truedigital.common.share.analytics.measurement.AnalyticManagerInterface
import com.truedigital.common.share.analytics.measurement.AnalyticManagerInterfaceImpl
import com.truedigital.common.share.analytics.measurement.appsflyer.AppsFlyerAnalyticManagerInterface
import com.truedigital.common.share.analytics.measurement.appsflyer.AppsFlyerAnalyticsManager
import com.truedigital.common.share.analytics.measurement.firebase.FirebaseAnalyticManager
import com.truedigital.common.share.analytics.measurement.newrelic.NewRelicManager
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.common.share.datalegacy.domain.other.usecase.EncryptLocationUseCase
import com.truedigital.core.data.repository.DeviceRepository
import com.truedigital.core.utils.SharedPrefsUtils
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
class AnalyticsModule {

    companion object {
        const val FIREBASE_ANALYTIC = "firebase_analytic"
        const val APPSFLYER_ANALYTIC = "appsflyer_analytic"
    }

    @Provides
    @Singleton
    fun providesFirebaseAnalytics(context: Context): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(context)
    }

    @Provides
    @Singleton
    fun providesFirebaseAnalyticManager(firebaseAnalytics: FirebaseAnalytics): FirebaseAnalyticManager {
        return FirebaseAnalyticManager(firebaseAnalytics)
    }

    @Provides
    @Singleton
    fun providesAppsFlyerLib(): AppsFlyerLib {
        return AppsFlyerLib.getInstance()
    }

    @Provides
    @Singleton
    fun providesNewRelicManager(): NewRelicManager {
        return NewRelicManager()
    }

    @Provides
    @Singleton
    fun provideAnalyticManager(
        encryptLocationUseCase: EncryptLocationUseCase,
        userRepository: UserRepository,
        deviceRepository: DeviceRepository,
        sharedPrefsUtils: SharedPrefsUtils,
        firebaseAnalyticManager: FirebaseAnalyticManager,
        newRelicManager: NewRelicManager
    ): AnalyticManager {
        return AnalyticManager(
            encryptLocationUseCase,
            userRepository,
            deviceRepository,
            sharedPrefsUtils,
            firebaseAnalyticManager,
            newRelicManager
        )
    }

    @Provides
    @Singleton
    fun providesAppsFlyerAnalyticsManager(
        appsFlyerLib: AppsFlyerLib,
        context: Context
    ): AppsFlyerAnalyticsManager {
        return AppsFlyerAnalyticsManager(appsFlyerLib, context)
    }

    @Provides
    @Singleton
    @Named(FIREBASE_ANALYTIC)
    fun provideFirebaseAnalyticManagerInterface(analyticManager: AnalyticManager): AnalyticManagerInterface =
        AnalyticManagerInterfaceImpl(analyticManager)

    @Provides
    @Singleton
    @Named(APPSFLYER_ANALYTIC)
    fun provideAppFlyerAnalyticManagerInterface(
        analyticManager: AnalyticManager,
        appsFlyerAnalyticsManager: AppsFlyerAnalyticsManager
    ): AnalyticManagerInterface =
        AppsFlyerAnalyticManagerInterface(analyticManager, appsFlyerAnalyticsManager)
}
