package com.truedigital.features.music.domain.landing.model

data class ItemOptionsModel(
    val playlistId: String = "",
    val tag: String = "",
    val limit: String = "",
    val format: String = "",
    val displayTitle: Boolean = false,
    val displayType: String = "",
    val targetTime: Boolean = false
)
