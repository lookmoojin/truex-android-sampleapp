package com.tdg.login.initializer

import android.content.Context
import androidx.startup.Initializer
import com.tdg.login.injections.DaggerOauthComponent
import com.tdg.login.injections.OauthComponent

class OauthInitializer : Initializer<OauthComponent> {
    override fun create(context: Context): OauthComponent {
        return DaggerOauthComponent.factory().create().apply {
            OauthComponent.initialize(this)
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}