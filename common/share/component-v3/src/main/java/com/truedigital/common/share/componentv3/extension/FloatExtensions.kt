package com.truedigital.common.share.componentv3.extension

import android.content.Context
import android.util.DisplayMetrics

fun Float.convertDpToPixel(context: Context): Float {
    val resources = context.resources
    val metrics = resources.displayMetrics
    return this * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
}
