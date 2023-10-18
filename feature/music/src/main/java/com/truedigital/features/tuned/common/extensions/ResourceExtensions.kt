package com.truedigital.features.tuned.common.extensions

import android.content.res.Resources
import android.util.TypedValue

fun Resources.dp(dp: Float): Int =
    TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, displayMetrics).toInt()
