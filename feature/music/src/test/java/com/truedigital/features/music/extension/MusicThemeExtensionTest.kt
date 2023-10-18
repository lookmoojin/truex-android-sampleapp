package com.truedigital.features.music.extension

import com.truedigital.features.music.domain.search.model.ThemeType
import com.truedigital.features.music.extensions.applyMusicItemTheme
import com.truedigital.features.tuned.R
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MusicThemeExtensionTest {

    @Test
    fun testThemeLight_mapData_returnMusicThemeModel() {
        val theme = ThemeType.LIGHT
        val musicThemeModel = applyMusicItemTheme(theme)
        assertEquals(R.color.white, musicThemeModel.backgroundColor)
        assertEquals(R.color.discover_shelf_text, musicThemeModel.textTitleColor)
        assertEquals(R.color.bg_text_tag_item_search, musicThemeModel.textDescriptionColor)
        assertEquals(R.color.divider_light, musicThemeModel.dividerColor)
        assertEquals(theme, musicThemeModel.type)
    }

    @Test
    fun testThemeDark_mapData_returnMusicThemeModel() {
        val theme = ThemeType.DARK
        val musicThemeModel = applyMusicItemTheme(theme)
        assertEquals(R.color.bg_search_result, musicThemeModel.backgroundColor)
        assertEquals(R.color.white, musicThemeModel.textTitleColor)
        assertEquals(R.color.bg_text_tag_item_search, musicThemeModel.textDescriptionColor)
        assertEquals(R.color.ripple_material_dark, musicThemeModel.dividerColor)
        assertEquals(theme, musicThemeModel.type)
    }
}
