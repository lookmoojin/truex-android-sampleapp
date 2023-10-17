package com.truedigital.features.music.domain.landing.model

data class MusicLandingFASelectContentModel(
    val itemId: String = "",
    val title: String = "",
    val contentType: String = "",
    val shelfName: String = "",
    val itemIndex: Int,
    val shelfIndex: Int
)
