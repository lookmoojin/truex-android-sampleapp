package com.truedigital.features.tuned.presentation.components

import android.os.Bundle

interface LifecycleComponent {
    fun onStart(arguments: Bundle)
    fun onStop()
    fun onResume()
    fun onPause()
    fun onBack(): Boolean = false
}
