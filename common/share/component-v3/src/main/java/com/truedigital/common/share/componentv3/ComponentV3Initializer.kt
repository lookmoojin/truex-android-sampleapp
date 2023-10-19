package com.truedigital.common.share.componentv3

import android.content.Context
import androidx.startup.Initializer
import com.truedigital.common.share.analytics.AnalyticsInitializer
import com.truedigital.common.share.analytics.injections.AnalyticsComponent
import com.truedigital.common.share.componentv3.injections.ComponentV3Component
import com.truedigital.common.share.componentv3.injections.DaggerComponentV3Component
import com.truedigital.common.share.currentdate.CurrentDateInitializer
import com.truedigital.common.share.currentdate.injections.CurrentDateComponent
import com.truedigital.common.share.data.coredata.CoreDataInitializer
import com.truedigital.common.share.data.coredata.injections.CoreDataComponent
import com.truedigital.common.share.datalegacy.DataLegacyInitializer
import com.truedigital.common.share.datalegacy.injections.DataLegacyComponent
import com.truedigital.core.CoreInitializer
import com.truedigital.core.injections.CoreComponent
import com.truedigital.share.data.firestoreconfig.initializer.FirestoreConfigInitializer
import com.truedigital.share.data.firestoreconfig.injections.FirestoreConfigComponent

class ComponentV3Initializer : Initializer<ComponentV3Component> {

    override fun create(context: Context): ComponentV3Component {
        return DaggerComponentV3Component.factory().create(
            AnalyticsComponent.getInstance().getAnalyticsSubComponent(),
            CoreDataComponent.getInstance().getCoreDataSubComponent(),
            CoreComponent.getInstance().getCoreSubComponent(),
            DataLegacyComponent.getInstance().getDataLegacySubComponent(),
            FirestoreConfigComponent.getInstance().getFirestoreConfigSubComponent(),
            CurrentDateComponent.getInstance().getCurrentDateSubComponent()
        ).apply {
            ComponentV3Component.initialize(this)
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(
        AnalyticsInitializer::class.java,
        CoreDataInitializer::class.java,
        CoreInitializer::class.java,
        DataLegacyInitializer::class.java,
        FirestoreConfigInitializer::class.java,
        CurrentDateInitializer::class.java
    )
}
