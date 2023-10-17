package com.tdg.login.injections

import com.tdg.login.base.CoroutineDispatcherProvider
import com.tdg.login.di.NetworkModule
import com.tdg.login.di.OauthApiModule
import com.tdg.login.di.OauthBaseModule
import com.tdg.login.di.OauthModule
import com.tdg.login.domain.usecase.LoginUseCase
import dagger.Component
import dagger.Subcomponent
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        NetworkModule::class,
        OauthApiModule::class,
        OauthBaseModule::class,
        OauthModule::class
    ]
)
interface OauthComponent {
    companion object {

        private lateinit var oauthComponent: OauthComponent

        fun initialize(oauthComponent: OauthComponent) {
            this.oauthComponent = oauthComponent
        }

        fun getInstance(): OauthComponent {
            if (!(::oauthComponent.isInitialized)) {
                error("OauthComponent not initialize")
            }
            return oauthComponent
        }
    }

    @Component.Factory
    interface Factory {
        fun create(): OauthComponent
    }

    fun getLoginComponent(): LoginComponent
}

@Subcomponent
interface LoginComponent {
    fun getCoroutineDispatcherProvider(): CoroutineDispatcherProvider
    fun getLoginUseCase(): LoginUseCase
}
