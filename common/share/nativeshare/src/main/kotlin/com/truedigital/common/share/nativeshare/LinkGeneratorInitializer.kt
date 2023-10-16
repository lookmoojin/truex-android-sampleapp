package com.truedigital.common.share.nativeshare

import android.content.Context
import androidx.startup.Initializer
import com.truedigital.common.share.datalegacy.DataLegacyInitializer
import com.truedigital.common.share.datalegacy.injections.DataLegacyComponent
import com.truedigital.common.share.nativeshare.injections.DaggerLinkGeneratorComponent
import com.truedigital.common.share.nativeshare.injections.LinkGeneratorComponent
import com.truedigital.core.CoreInitializer
import com.truedigital.core.injections.CoreComponent

class LinkGeneratorInitializer : Initializer<LinkGeneratorComponent> {

    override fun create(context: Context): LinkGeneratorComponent {
        return DaggerLinkGeneratorComponent.factory().create(
            CoreComponent.getInstance().getCoreSubComponent(),
            DataLegacyComponent.getInstance().getDataLegacySubComponent()
        ).apply {
            LinkGeneratorComponent.initialize(this)
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(
        CoreInitializer::class.java,
        DataLegacyInitializer::class.java
    )
}
