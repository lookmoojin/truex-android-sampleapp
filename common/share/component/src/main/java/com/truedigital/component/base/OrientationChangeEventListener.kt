package com.truedigital.component.base

import android.content.Context
import android.hardware.SensorManager
import android.provider.Settings
import android.view.OrientationEventListener

interface OrientationChangeEventListener {
    fun onChangeToPortrait()
    fun onChangeToLandscape()
}

class OrientationChangeEvent(
    val context: Context,
    val orientationChangeEventListener: OrientationChangeEventListener
) : OrientationEventListener(context, SensorManager.SENSOR_DELAY_NORMAL) {

    enum class OrientationState {
        PORTRAIT, LANDSCAPE
    }

    private var currentOrientationState = OrientationState.PORTRAIT

    override fun onOrientationChanged(orientation: Int) {
        if (orientation >= 0) {
            if (currentOrientationState == OrientationState.PORTRAIT) {
                // state is PORTRAIT
                if ((orientation in 255..285) || (orientation in 75..105)) {
                    // check if auto rotate screen is on
                    if (isAutoRotateScreenOn()) {
                        // change state to LANDSCAPE
                        orientationChangeEventListener.onChangeToLandscape()
                        currentOrientationState = OrientationState.LANDSCAPE
                    }
                }
            } else {
                // state is LANDSCAPE
                if ((orientation in 345..359) || (orientation in 0..15)) {
                    // check if auto rotate screen is on
                    if (isAutoRotateScreenOn()) {
                        // change state to PORTRAIT
                        orientationChangeEventListener.onChangeToPortrait()
                        currentOrientationState = OrientationState.PORTRAIT
                    }
                }
            }
        }
    }

    fun changeOrientationState(state: OrientationState) {
        currentOrientationState = state
    }

    private fun isAutoRotateScreenOn(): Boolean {
        return Settings.System.getInt(
            context.contentResolver,
            Settings.System.ACCELEROMETER_ROTATION,
            0
        ) == 1
    }
}
