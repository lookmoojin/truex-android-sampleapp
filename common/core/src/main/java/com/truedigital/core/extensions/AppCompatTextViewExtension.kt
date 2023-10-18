package com.truedigital.core.extensions

import android.content.Context
import android.os.Build
import androidx.annotation.StyleRes
import androidx.appcompat.widget.AppCompatTextView

fun AppCompatTextView.setTextStyle(context: Context, @StyleRes style: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        this.setTextAppearance(style)
    } else {
        this.setTextAppearance(context, style)
    }
}
