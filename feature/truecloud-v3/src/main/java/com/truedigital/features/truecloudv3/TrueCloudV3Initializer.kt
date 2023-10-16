package com.truedigital.features.truecloudv3

import android.content.Context
import androidx.startup.Initializer
import com.truedigital.common.share.analytics.AnalyticsInitializer
import com.truedigital.common.share.analytics.injections.AnalyticsComponent
import com.truedigital.common.share.currentdate.CurrentDateInitializer
import com.truedigital.common.share.currentdate.injections.CurrentDateComponent
import com.truedigital.common.share.data.coredata.CoreDataInitializer
import com.truedigital.common.share.data.coredata.injections.CoreDataComponent
import com.truedigital.common.share.datalegacy.DataLegacyInitializer
import com.truedigital.common.share.datalegacy.injections.DataLegacyComponent
import com.truedigital.core.CoreInitializer
import com.truedigital.core.injections.CoreComponent
import com.truedigital.features.truecloudv3.injections.TrueCloudV3Component
import com.truedigital.navigation.NavigationInitializer
import com.truedigital.navigation.injections.NavigationComponent
import com.truedigital.share.data.firestoreconfig.initializer.FirestoreConfigInitializer
import com.truedigital.share.data.firestoreconfig.injections.FirestoreConfigComponent

class TrueCloudV3Initializer : Initializer<TrueCloudV3Component> {

    override fun create(context: Context): TrueCloudV3Component {
        return DaggerTrueCloudV3Component.factory().create(
            AnalyticsComponent.getInstance().getAnalyticsSubComponent(),
            CoreComponent.getInstance().getCoreSubComponent(),
            CoreDataComponent.getInstance().getCoreDataSubComponent(),
            CurrentDateComponent.getInstance().getCurrentDateSubComponent(),
            DataLegacyComponent.getInstance().getDataLegacySubComponent(),
            FirestoreConfigComponent.getInstance().getFirestoreConfigSubComponent(),
            NavigationComponent.getInstance().getNavigationSubComponent()
        ).apply {
            TrueCloudV3Component.initialize(this)
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(
        AnalyticsInitializer::class.java,
        CoreInitializer::class.java,
        CoreDataInitializer::class.java,
        CurrentDateInitializer::class.java,
        DataLegacyInitializer::class.java,
        FirestoreConfigInitializer::class.java,
        NavigationInitializer::class.java
    )
}
