package com.truedigital.core.extensions.compose

import androidx.compose.ui.graphics.Color

fun String.hexToJetpackColor(defaultColor: Color = Color.White): Color {
    return try {
        Color(android.graphics.Color.parseColor(this))
    } catch (e: NumberFormatException) {
        defaultColor
    }
}
