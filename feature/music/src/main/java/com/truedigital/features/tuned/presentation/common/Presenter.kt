package com.truedigital.features.tuned.presentation.common

import android.os.Bundle

interface Presenter {
    fun onStart(arguments: Bundle? = null) {
        Unit
    }

    fun onStop() {
        Unit
    }

    fun onResume() {
        Unit
    }

    fun onPause() {
        Unit
    }
}
