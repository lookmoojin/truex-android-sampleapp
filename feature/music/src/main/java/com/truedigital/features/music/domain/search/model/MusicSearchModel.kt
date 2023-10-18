package com.truedigital.features.music.domain.search.model

import androidx.annotation.ColorRes

sealed class MusicSearchModel {

    class MusicItemModel(
        var id: String? = "",
        var title: String? = "",
        var description: String? = "",
        var thumb: String? = "",
        var type: MusicType? = null,
        var musicTheme: MusicThemeModel? = null
    ) : MusicSearchModel()

    class MusicHeaderModel(
        var id: String? = "",
        var title: String? = "",
        var type: MusicType? = null,
        @ColorRes
        var textHeaderColor: Int = android.R.color.black,
        @ColorRes
        var textSeemoreColor: Int = android.R.color.black
    ) : MusicSearchModel()
}
