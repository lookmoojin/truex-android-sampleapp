package com.truedigital.foundation.extension

import android.app.Activity
import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat

fun Context?.isAvailable(): Boolean {
    if (this == null) {
        return false
    }

    when (this) {
        is Application -> {
            return true
        }
        is Activity -> {
            if (this.isDestroyed || this.isFinishing) {
                return false
            }
        }
    }

    return true
}

fun Context?.getAppDrawable(resId: Int): Drawable? {
    if (this == null) return null
    return ContextCompat.getDrawable(this, resId)
}

fun Context?.getAppColor(resId: Int): Int {
    if (this == null) return 0
    return ContextCompat.getColor(this, resId)
}
