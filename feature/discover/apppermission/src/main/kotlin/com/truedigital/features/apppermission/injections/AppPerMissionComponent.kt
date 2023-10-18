package com.truedigital.features.apppermission.injections

import com.truedigital.common.share.analytics.injections.AnalyticsSubComponent
import com.truedigital.core.injections.CoreSubComponent
import com.truedigital.features.apppermission.di.AppPerMissionModule
import com.truedigital.features.apppermission.di.AppPerMissionViewModelsModule
import com.truedigital.features.apppermission.presenation.AppPermissionGuideFragment
import com.truedigital.features.onboarding.injections.OnBoardingSubComponent
import com.truedigital.share.data.firestoreconfig.injections.FirestoreConfigSubComponent
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppPerMissionModule::class,
        AppPerMissionViewModelsModule::class,
    ],
    dependencies = [
        AnalyticsSubComponent::class,
        CoreSubComponent::class,
        FirestoreConfigSubComponent::class,
        OnBoardingSubComponent::class
    ]
)
interface AppPerMissionComponent {

    companion object {

        private lateinit var appPerMissionComponent: AppPerMissionComponent

        fun initialize(appPerMissionComponent: AppPerMissionComponent) {
            this.appPerMissionComponent = appPerMissionComponent
        }

        fun getInstance(): AppPerMissionComponent {
            if (!(::appPerMissionComponent.isInitialized)) {
                error("AppPerMissionComponent not initialize")
            }
            return appPerMissionComponent
        }
    }

    @Component.Factory
    interface Factory {
        fun create(
            analyticsSubComponent: AnalyticsSubComponent,
            coreSubComponent: CoreSubComponent,
            firestoreConfigSubComponent: FirestoreConfigSubComponent,
            onBoardingSubComponent: OnBoardingSubComponent
        ): AppPerMissionComponent
    }

    fun inject(fragment: AppPermissionGuideFragment)
}
