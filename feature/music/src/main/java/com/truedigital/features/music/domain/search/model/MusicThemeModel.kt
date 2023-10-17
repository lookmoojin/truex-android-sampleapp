package com.truedigital.features.music.domain.search.model

import androidx.annotation.ColorRes
import com.truedigital.features.tuned.R

class MusicThemeModel {
    @ColorRes
    var backgroundColor: Int = android.R.color.transparent

    @ColorRes
    var textTitleColor: Int = android.R.color.black

    @ColorRes
    var textDescriptionColor: Int = R.color.header_gray

    @ColorRes
    var dividerColor: Int = R.color.header_gray

    var type: ThemeType = ThemeType.LIGHT
}

enum class ThemeType {
    LIGHT, DARK;

    companion object {
        fun getThemeType(name: String): ThemeType {
            return when (name) {
                DARK.name -> DARK
                LIGHT.name -> LIGHT
                else -> DARK
            }
        }
    }
}
