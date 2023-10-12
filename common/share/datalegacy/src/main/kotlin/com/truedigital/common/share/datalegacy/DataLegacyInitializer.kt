package com.truedigital.common.share.datalegacy

import android.content.Context
import androidx.startup.Initializer
import com.truedigital.common.share.datalegacy.injections.DaggerDataLegacyComponent
import com.truedigital.common.share.datalegacy.injections.DataLegacyComponent
import com.truedigital.core.CoreInitializer
import com.truedigital.core.injections.CoreComponent
import com.truedigital.share.data.firestoreconfig.initializer.FirestoreConfigInitializer
import com.truedigital.share.data.firestoreconfig.injections.FirestoreConfigComponent

class DataLegacyInitializer : Initializer<DataLegacyComponent> {

    override fun create(context: Context): DataLegacyComponent {
        return DaggerDataLegacyComponent.factory().create(
            CoreComponent.getInstance().getCoreSubComponent(),
            FirestoreConfigComponent.getInstance().getFirestoreConfigSubComponent()
        ).apply {
            DataLegacyComponent.initialize(this)
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(
        CoreInitializer::class.java,
        FirestoreConfigInitializer::class.java
    )
}
