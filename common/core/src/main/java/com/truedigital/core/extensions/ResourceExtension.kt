package com.truedigital.core.extensions

import android.content.res.Resources
import com.truedigital.core.R

private const val NAVIGATION_BAR_HEIGHT = "navigation_bar_height"
private const val DIMEN = "dimen"
private const val ANDROID = "android"

fun Resources.screenHeight(): Int {
    val screenHeight = this.displayMetrics.heightPixels
    val resourceId = this.getIdentifier(NAVIGATION_BAR_HEIGHT, DIMEN, ANDROID)
    val navigationHeight = if (resourceId > 0) {
        this.getDimensionPixelSize(resourceId)
    } else {
        0
    }
    return screenHeight + navigationHeight
}

fun Resources.isTablet(): Boolean = this.getBoolean(R.bool.is_tablet)
