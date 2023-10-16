package com.truedigital.component.dialog.trueid

import androidx.annotation.StyleRes
import com.truedigital.component.R

enum class DialogTheme(
    @StyleRes val default: Int,
    @StyleRes val fullScreen: Int
) {
    LIGHT(
        default = R.style.TrueIdDialogTheme,
        fullScreen = R.style.TrueIdDialogTheme_FullScreen
    ),
    DARK(
        default = R.style.TrueIdDialogTheme_Dark,
        fullScreen = R.style.TrueIdDialogTheme_Dark_FullScreen
    )
}
