package com.tdg.login.injections

import com.tdg.login.di.NetworkModule
import com.tdg.login.di.OauthApiModule
import com.tdg.login.di.OauthBaseModule
import com.tdg.login.di.OauthModule
import dagger.Component
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
}
