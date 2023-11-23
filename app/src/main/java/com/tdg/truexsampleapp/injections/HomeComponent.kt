package com.tdg.truexsampleapp.injections

import com.tdg.login.injections.LoginComponent
import com.tdg.truexsampleapp.MainActivity
import com.tdg.truexsampleapp.MainFragment
import com.tdg.truexsampleapp.di.HomeViewModelsModule
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
