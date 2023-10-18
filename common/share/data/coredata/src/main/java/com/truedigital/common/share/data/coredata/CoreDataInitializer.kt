package com.truedigital.common.share.data.coredata

import android.content.Context
import androidx.startup.Initializer
import com.truedigital.common.share.data.coredata.injections.CoreDataComponent
import com.truedigital.common.share.data.coredata.injections.DaggerCoreDataComponent
import com.truedigital.common.share.datalegacy.DataLegacyInitializer
import com.truedigital.common.share.datalegacy.injections.DataLegacyComponent
import com.truedigital.core.CoreInitializer
import com.truedigital.core.injections.CoreComponent
import com.truedigital.share.data.firestoreconfig.initializer.FirestoreConfigInitializer
import com.truedigital.share.data.firestoreconfig.injections.FirestoreConfigComponent
import com.truedigital.share.data.prasarn.PrasarnInitializer
import com.truedigital.share.data.prasarn.injections.PrasarnComponent

class CoreDataInitializer : Initializer<CoreDataComponent> {

    override fun create(context: Context): CoreDataComponent {
        return DaggerCoreDataComponent.factory().create(
            CoreComponent.getInstance().getCoreSubComponent(),
            DataLegacyComponent.getInstance().getDataLegacySubComponent(),
            FirestoreConfigComponent.getInstance().getFirestoreConfigSubComponent(),
            PrasarnComponent.getInstance().getPrasarnSubComponent()
        ).apply {
            CoreDataComponent.initialize(this)
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(
        CoreInitializer::class.java,
        DataLegacyInitializer::class.java,
        FirestoreConfigInitializer::class.java,
        PrasarnInitializer::class.java
    )
}
