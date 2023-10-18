package com.truedigital.component

import android.content.Context
import androidx.startup.Initializer
import com.truedigital.common.share.analytics.AnalyticsInitializer
import com.truedigital.common.share.analytics.injections.AnalyticsComponent
import com.truedigital.common.share.currentdate.CurrentDateInitializer
import com.truedigital.common.share.currentdate.injections.CurrentDateComponent
import com.truedigital.common.share.datalegacy.DataLegacyInitializer
import com.truedigital.common.share.datalegacy.injections.DataLegacyComponent
import com.truedigital.common.share.nativeshare.LinkGeneratorInitializer
import com.truedigital.common.share.nativeshare.injections.LinkGeneratorComponent
import com.truedigital.component.injections.DaggerTIDComponent
import com.truedigital.component.injections.TIDComponent
import com.truedigital.core.CoreInitializer
import com.truedigital.core.injections.CoreComponent

class ComponentInitializer : Initializer<TIDComponent> {

    override fun create(context: Context): TIDComponent {
        return DaggerTIDComponent.factory().create(
            AnalyticsComponent.getInstance().getAnalyticsSubComponent(),
            CoreComponent.getInstance().getCoreSubComponent(),
            CurrentDateComponent.getInstance().getCurrentDateSubComponent(),
            DataLegacyComponent.getInstance().getDataLegacySubComponent(),
            LinkGeneratorComponent.getInstance().getLinkGeneratorSubComponent(),
        ).apply {
            TIDComponent.initialize(this)
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(
        AnalyticsInitializer::class.java,
        CoreInitializer::class.java,
        CurrentDateInitializer::class.java,
        DataLegacyInitializer::class.java,
        LinkGeneratorInitializer::class.java,

    )
}
