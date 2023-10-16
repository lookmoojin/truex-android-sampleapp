package com.tdg.truex_android_sampleapp.injections

import com.tdg.login.injections.LoginComponent
import com.tdg.truex_android_sampleapp.MainActivity
import com.tdg.truex_android_sampleapp.MainFragment
import com.tdg.truex_android_sampleapp.di.HomeViewModelsModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        HomeViewModelsModule::class
    ],
    dependencies = [
        LoginComponent::class,
    ]
)
interface HomeComponent {

    companion object {

        private lateinit var homeComponent: HomeComponent

        fun initialize(homeComponent: HomeComponent) {
            this.homeComponent = homeComponent
        }

        fun getInstance(): HomeComponent {
            if (!(::homeComponent.isInitialized)) {
                error("HomeComponent not initialize")
            }
            return homeComponent
        }
    }

    @Component.Factory
    interface Factory {
        fun create(
            loginComponent: LoginComponent
        ): HomeComponent
    }

    fun inject(activity: MainActivity)
    fun inject(fragment: MainFragment)
}
