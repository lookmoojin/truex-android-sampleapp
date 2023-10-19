package com.truedigital.navigation.injections

import com.truedigital.common.share.analytics.injections.AnalyticsSubComponent
import com.truedigital.common.share.componentv3.injections.ComponentV3SubComponent
import com.truedigital.common.share.data.coredata.injections.CoreDataSubComponent
import com.truedigital.common.share.datalegacy.injections.DataLegacySubComponent
import com.truedigital.core.injections.CoreSubComponent
import com.truedigital.navigation.data.repository.router.NavigationRouterRepository
import com.truedigital.navigation.deeplink.TrackFirebaseAnalyticsDeeplinkUseCase
import com.truedigital.navigation.di.NavigationMainBindsModule
import com.truedigital.navigation.di.NavigationMultiBindingModule
import com.truedigital.navigation.di.NavigationProvidesModule
import com.truedigital.navigation.di.NavigationRouterBindingModule
import com.truedigital.navigation.di.NavigationRoutingBindsModule
import com.truedigital.navigation.di.NavigationRoutingModule
import com.truedigital.navigation.domain.usecase.SetCrossRouterNavControllerUseCase
import com.truedigital.navigation.domain.usecase.SetRouterSecondaryToNavControllerUseCase
import com.truedigital.navigation.domain.usecase.SetRouterToNavControllerUseCase
import com.truedigital.navigation.router.CrossRouter
import com.truedigital.navigation.usecase.GetNavigationControllerRepository
import dagger.Component
import dagger.Subcomponent
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        NavigationMainBindsModule::class,
        NavigationProvidesModule::class,
        NavigationRouterBindingModule::class,
        NavigationRoutingModule::class,
        NavigationRoutingBindsModule::class,
        NavigationMultiBindingModule::class
    ],
    dependencies = [
        AnalyticsSubComponent::class,
        ComponentV3SubComponent::class,
        CoreDataSubComponent::class,
        CoreSubComponent::class,
        DataLegacySubComponent::class
    ],
)
interface NavigationComponent {

    companion object {
        private lateinit var navigationComponent: NavigationComponent

        fun initialize(navigationComponent: NavigationComponent) {
            this.navigationComponent = navigationComponent
        }

        fun getInstance(): NavigationComponent {
            if (!(::navigationComponent.isInitialized)) {
                error("NavigationComponent not initialize")
            }
            return navigationComponent
        }
    }

    @Component.Factory
    interface Factory {
        fun create(
            analyticsSubComponent: AnalyticsSubComponent,
            componentV3SubComponent: ComponentV3SubComponent,
            coreDataSubComponent: CoreDataSubComponent,
            coreSubComponent: CoreSubComponent,
        ): NavigationComponent
    }

    fun getNavigationSubComponent(): NavigationSubComponent
}

@Subcomponent
interface NavigationSubComponent {

    fun getNavigationRouterRepository(): NavigationRouterRepository
    fun getCrossRouter(): CrossRouter

    // Use cases
    fun getSetRouterToNavControllerUseCase(): SetRouterToNavControllerUseCase
    fun getSetRouterSecondaryToNavControllerUseCase(): SetRouterSecondaryToNavControllerUseCase
    fun getSetCrossRouterNavControllerUseCase(): SetCrossRouterNavControllerUseCase
    fun getTrackFirebaseAnalyticsDeeplinkUseCase(): TrackFirebaseAnalyticsDeeplinkUseCase

    fun getGetNavigationControllerUseCase(): GetNavigationControllerRepository
}
