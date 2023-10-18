package com.truedigital.core.utils

import android.view.Window
import android.view.WindowManager
import android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_UNSPECIFIED
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

fun Window?.getSoftInputMode(): Int {
    return this?.attributes?.softInputMode ?: SOFT_INPUT_STATE_UNSPECIFIED
}

class InputModeLifecycleObserver(
    private var window: Window?,
    private val mode: Mode = Mode.ADJUST_RESIZE
) : LifecycleObserver {

    private var originalMode: Int = SOFT_INPUT_STATE_UNSPECIFIED

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    private fun setNewSoftInputMode() {
        window?.let {
            originalMode = it.getSoftInputMode()

            it.setSoftInputMode(
                when (mode) {
                    Mode.ADJUST_RESIZE -> WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                    Mode.ADJUST_PAN -> WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                }
            )
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    private fun restoreOriginalSoftInputMode() {
        if (originalMode != SOFT_INPUT_STATE_UNSPECIFIED) {
            window?.setSoftInputMode(originalMode)
        }
        window = null
    }

    enum class Mode {
        ADJUST_RESIZE, ADJUST_PAN
    }
}
