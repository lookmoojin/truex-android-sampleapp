package com.truedigital.features.music.domain.myplaylist.model

data class MyPlaylistModel(
    val id: Int? = null,
    val coverImage: String = "",
    val playlistName: String = "",
    val count: Int = 0,
    val itemId: Int? = null,
    val itemType: MyPlaylistItemType = MyPlaylistItemType.HEADER,
    val isTrackNotEmpty: Boolean = false
)
