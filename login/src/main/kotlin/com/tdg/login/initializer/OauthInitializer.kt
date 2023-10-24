package com.tdg.login.initializer

import android.content.Context
import androidx.startup.Initializer
import com.tdg.login.injections.DaggerOauthComponent
import com.tdg.login.injections.OauthComponent
import com.truedigital.common.share.datalegacy.DataLegacyInitializer
import com.truedigital.common.share.datalegacy.injections.DataLegacyComponent
import com.truedigital.core.CoreInitializer
import com.truedigital.core.injections.CoreComponent

class OauthInitializer : Initializer<OauthComponent> {
    override fun create(context: Context): OauthComponent {
        return DaggerOauthComponent.factory().create(
            CoreComponent.getInstance().getCoreSubComponent(),
            DataLegacyComponent.getInstance().getDataLegacySubComponent(),
        ).apply {
            OauthComponent.initialize(this)
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(
        CoreInitializer::class.java,
        DataLegacyInitializer::class.java,
    )
}