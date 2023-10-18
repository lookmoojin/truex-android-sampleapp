package com.truedigital.component.dialog.trueid

import androidx.annotation.DrawableRes

sealed class DialogIcon

data class AdaptiveIcon(
    @DrawableRes val res: Int
) : DialogIcon()

data class ThemedIcon(
    @DrawableRes val lightRes: Int,
    @DrawableRes val darkRes: Int,
) : DialogIcon() {

    @DrawableRes
    fun getIconByTheme(theme: DialogTheme): Int {
        return when (theme) {
            DialogTheme.LIGHT -> lightRes
            DialogTheme.DARK -> darkRes
        }
    }
}
