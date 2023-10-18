package com.truedigital.features.tuned.domain.facade.playlist

import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.track.model.Track
import io.reactivex.Single

interface PlaylistFacade {
    fun getPlaylist(id: Int): Single<Playlist>
    fun getPlaylistTracks(playlistId: Int, offset: Int, count: Int): Single<List<Track>>
    fun toggleFavourite(id: Int): Single<Any>
    fun loadFavourited(id: Int): Single<Boolean>
    fun isOwner(creatorId: Int): Single<Boolean>
    fun hasPlaylistWriteRight(): Boolean
}
