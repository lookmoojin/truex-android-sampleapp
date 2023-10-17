package com.truedigital.features.music.domain.addsong.model

data class MusicSearchResultModel(
    val id: Int? = -1,
    val songName: String = "",
    val artistName: String = "",
    val coverImage: String = "",
    val isSelected: Boolean = false
)
