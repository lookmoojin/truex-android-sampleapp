package com.tdg.truexsampleapp.initializer

import android.content.Context
import androidx.startup.Initializer
import com.tdg.login.initializer.OauthInitializer
import com.tdg.login.injections.OauthComponent
import com.tdg.truexsampleapp.injections.DaggerHomeComponent
import com.tdg.truexsampleapp.injections.HomeComponent

class HomeInitializer : Initializer<HomeComponent> {

    override fun create(context: Context): HomeComponent {
        return DaggerHomeComponent.factory().create(
            OauthComponent.getInstance().getLoginComponent()
        ).apply {
            HomeComponent.initialize(this)
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(
        OauthInitializer::class.java
    )
}