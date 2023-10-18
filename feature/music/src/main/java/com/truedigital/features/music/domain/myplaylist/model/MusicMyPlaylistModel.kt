package com.truedigital.features.music.domain.myplaylist.model

sealed class MusicMyPlaylistModel {
    abstract val id: Int

    data class CreateMyPlaylistModel(
        val playlistId: Int = -1
    ) : MusicMyPlaylistModel() {
        override val id: Int
            get() = playlistId
    }

    data class MyPlaylistModel(
        val playlistId: Int = 0,
        val title: String = "",
        val coverImage: String = "",
        val trackCount: Int = 0,
        val index: Int = 0
    ) : MusicMyPlaylistModel() {
        override val id: Int
            get() = playlistId
    }
}
