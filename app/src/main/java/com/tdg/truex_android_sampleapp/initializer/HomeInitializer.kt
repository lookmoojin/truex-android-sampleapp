package com.tdg.truex_android_sampleapp.initializer

import android.content.Context
import androidx.startup.Initializer
import com.tdg.login.initializer.OauthInitializer
import com.tdg.login.injections.OauthComponent
import com.tdg.truex_android_sampleapp.injections.DaggerHomeComponent
import com.tdg.truex_android_sampleapp.injections.HomeComponent

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