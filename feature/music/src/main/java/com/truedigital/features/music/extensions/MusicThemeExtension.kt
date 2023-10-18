package com.truedigital.features.music.extensions

import com.truedigital.features.music.domain.search.model.MusicThemeModel
import com.truedigital.features.music.domain.search.model.ThemeType
import com.truedigital.features.tuned.R

fun applyMusicItemTheme(themeType: ThemeType): MusicThemeModel {
    return when (themeType) {
        ThemeType.LIGHT -> {
            MusicThemeModel().apply {
                this.backgroundColor = R.color.white
                this.textTitleColor = R.color.discover_shelf_text
                this.textDescriptionColor = R.color.bg_text_tag_item_search
                this.dividerColor = R.color.divider_light
                this.type = themeType
            }
        }
        ThemeType.DARK -> {
            MusicThemeModel().apply {
                this.backgroundColor = R.color.bg_search_result
                this.textTitleColor = R.color.white
                this.textDescriptionColor = R.color.bg_text_tag_item_search
                this.dividerColor = R.color.ripple_material_dark
                this.type = themeType
            }
        }
    }
}
