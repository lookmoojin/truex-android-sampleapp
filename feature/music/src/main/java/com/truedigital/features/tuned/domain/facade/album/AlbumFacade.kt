package com.truedigital.features.tuned.domain.facade.album

import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.track.model.Track
import io.reactivex.Single

interface AlbumFacade {
    fun loadAlbum(id: Int): Single<Album>
    fun loadMoreFromArtist(id: Int): Single<List<Album>>
    fun loadTrack(id: Int): Single<Track>
    fun loadTracks(album: Album, ids: List<Int>): Single<List<Track>>
    fun toggleFavourite(album: Album): Single<Any>
    fun loadFavourited(id: Int): Single<Boolean>
    fun getAlbumNavigationAllowed(): Boolean
    fun getAllowMobileData(): Boolean
    fun getIsEmulator(): Boolean
    fun getHasAlbumShuffleRight(): Boolean
}
