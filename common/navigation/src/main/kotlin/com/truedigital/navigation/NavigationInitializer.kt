package com.truedigital.navigation

import android.content.Context
import androidx.startup.Initializer
import com.truedigital.common.share.analytics.AnalyticsInitializer
import com.truedigital.common.share.analytics.injections.AnalyticsComponent
import com.truedigital.common.share.componentv3.injections.ComponentV3Component
import com.truedigital.common.share.data.coredata.CoreDataInitializer
import com.truedigital.common.share.data.coredata.injections.CoreDataComponent
import com.truedigital.common.share.datalegacy.DataLegacyInitializer
import com.truedigital.common.share.datalegacy.injections.DataLegacyComponent
import com.truedigital.core.CoreInitializer
import com.truedigital.core.injections.CoreComponent
import com.truedigital.navigation.injections.DaggerNavigationComponent
import com.truedigital.navigation.injections.NavigationComponent

class NavigationInitializer : Initializer<NavigationComponent> {

    override fun create(context: Context): NavigationComponent {
        return DaggerNavigationComponent.factory().create(
            AnalyticsComponent.getInstance().getAnalyticsSubComponent(),
            ComponentV3Component.getInstance().getComponentV3SubComponent(),
            CoreDataComponent.getInstance().getCoreDataSubComponent(),
            CoreComponent.getInstance().getCoreSubComponent(),
            DataLegacyComponent.getInstance().getDataLegacySubComponent()
        ).apply {
            NavigationComponent.initialize(this)
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(
        AnalyticsInitializer::class.java,
        CoreDataInitializer::class.java,
        CoreInitializer::class.java,
        DataLegacyInitializer::class.java
    )
}
