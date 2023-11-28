package com.tdg.onboarding.initializer

import android.content.Context
import androidx.startup.Initializer
import com.tdg.onboarding.injections.DaggerWhatNewComponent
import com.tdg.onboarding.injections.WhatNewComponent
import com.truedigital.core.CoreInitializer
import com.truedigital.core.injections.CoreComponent
import com.truedigital.share.data.firestoreconfig.initializer.FirestoreConfigInitializer
import com.truedigital.share.data.firestoreconfig.injections.FirestoreConfigComponent

class WhatNewInitializer : Initializer<WhatNewComponent> {
    override fun create(context: Context): WhatNewComponent {
        return DaggerWhatNewComponent.factory().create(
            CoreComponent.getInstance().getCoreSubComponent(),
            FirestoreConfigComponent.getInstance().getFirestoreConfigSubComponent()
        ).apply {
            WhatNewComponent.initialize(this)
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(
        CoreInitializer::class.java,
        FirestoreConfigInitializer::class.java,
    )
}