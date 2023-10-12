package com.truedigital.common.share.datalegacy

import android.content.Context
import androidx.startup.Initializer
import com.truedigital.common.share.datalegacy.injections.DataLegacyComponent
import com.truedigital.common.share.datalegacy.utils.FirebaseUtil

class FirebaseInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        DataLegacyComponent.getInstance().getDataLegacySubComponent().getUserRepository().let {
            FirebaseUtil.instance.registerApps(context, it.getSsoId())
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return listOf(DataLegacyInitializer::class.java)
    }
}
