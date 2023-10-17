package com.truedigital.features.music.domain.search.model

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.truedigital.features.tuned.R

enum class MusicType {
    ALL, SONG, ARTIST, ALBUM, PLAYLIST
}

class TopMenuModel {
    var id: String? = ""
    var name: String? = ""
    var nameEn: String? = ""
    var type: MusicType? = null
    var isActive: Boolean = false

    @ColorRes
    var textActiveColor: Int = android.R.color.white

    @ColorRes
    var textInactiveColor: Int = R.color.button_state_neutral

    @DrawableRes
    var buttonActiveDrawable: Int = R.drawable.bg_button_round_red

    @DrawableRes
    var buttonInactiveDrawable: Int = R.drawable.bg_button_round_inactive_light
}
