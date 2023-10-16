package com.truedigital.navigation.di

import com.truedigital.navigation.deeplink.TrackFirebaseAnalyticsDeeplinkUseCase
import com.truedigital.navigation.deeplink.TrackFirebaseAnalyticsDeeplinkUseCaseImpl
import com.truedigital.navigation.domain.usecase.SetCrossRouterNavControllerUseCase
import com.truedigital.navigation.domain.usecase.SetCrossRouterNavControllerUseCaseImpl
import com.truedigital.navigation.domain.usecase.SetRouterSecondaryToNavControllerUseCase
import com.truedigital.navigation.domain.usecase.SetRouterSecondaryToNavControllerUseCaseImpl
import com.truedigital.navigation.domain.usecase.SetRouterToNavControllerUseCase
import com.truedigital.navigation.domain.usecase.SetRouterToNavControllerUseCaseImpl
import com.truedigital.navigation.router.CrossRouter
import com.truedigital.navigation.router.CrossRouterImpl
import dagger.Binds
import dagger.Module
import javax.inject.Singleton

@Module
interface NavigationRoutingBindsModule {

    @Binds
    @Singleton
    fun bindsCrossRouter(
        crossRouterImpl: CrossRouterImpl,
    ): CrossRouter

    @Binds
    fun bindsSetRouterToNavControllerUseCase(
        setRouterToNavControllerUseCaseImpl: SetRouterToNavControllerUseCaseImpl,
    ): SetRouterToNavControllerUseCase

    @Binds
    fun bindsSetRouterSecondaryToNavControllerUseCase(
        setRouterSecondaryToNavControllerUseCaseImpl: SetRouterSecondaryToNavControllerUseCaseImpl,
    ): SetRouterSecondaryToNavControllerUseCase

    @Binds
    fun bindsSetCrossRouterNavControllerUseCase(
        setCrossRouterNavControllerUseCaseImpl: SetCrossRouterNavControllerUseCaseImpl,
    ): SetCrossRouterNavControllerUseCase

    @Binds
    fun bindsTrackFirebaseAnalyticsDeeplinkUseCase(
        trackFirebaseAnalyticsDeeplinkUseCaseImpl: TrackFirebaseAnalyticsDeeplinkUseCaseImpl,
    ): TrackFirebaseAnalyticsDeeplinkUseCase
}
