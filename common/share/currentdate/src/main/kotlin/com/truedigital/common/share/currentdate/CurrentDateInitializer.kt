package com.truedigital.common.share.currentdate

import android.content.Context
import androidx.startup.Initializer
import com.jakewharton.threetenabp.AndroidThreeTen
import com.truedigital.common.share.currentdate.injections.CurrentDateComponent
import com.truedigital.common.share.currentdate.injections.DaggerCurrentDateComponent
import com.truedigital.common.share.datalegacy.DataLegacyInitializer
import com.truedigital.common.share.datalegacy.injections.DataLegacyComponent
import com.truedigital.core.CoreInitializer
import com.truedigital.core.injections.CoreComponent

class CurrentDateInitializer : Initializer<CurrentDateComponent> {

    override fun create(context: Context): CurrentDateComponent {
        return DaggerCurrentDateComponent.factory().create(
            CoreComponent.getInstance().getCoreSubComponent(),
            DataLegacyComponent.getInstance().getDataLegacySubComponent()
        ).apply {
            CurrentDateComponent.initialize(this)
        }.also {
            AndroidThreeTen.init(context)
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(
        CoreInitializer::class.java,
        DataLegacyInitializer::class.java
    )
}
