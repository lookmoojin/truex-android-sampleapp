package com.truedigital.features.tuned.presentation.components

import android.app.Dialog
import android.content.Context
import android.os.Bundle

open class LifecycleComponentDialog constructor(context: Context) : Dialog(context) {
    protected val lifecycleComponents: MutableList<LifecycleComponent> = mutableListOf()

    override fun onStart() {
        super.onStart()
        lifecycleComponents.forEach { it.onStart(Bundle()) }
    }

    override fun onStop() {
        super.onStop()
        lifecycleComponents.forEach { it.onStop() }
    }
}
