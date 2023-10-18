package com.truedigital.features.tuned.data.playlist.repository

import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.track.model.request.TrackRequestType
import io.reactivex.Single

interface PlaylistRepository {
    fun get(id: Int, refresh: Boolean = false): Single<Playlist>
    fun getTrending(offset: Int, count: Int, type: TrackRequestType): Single<List<Playlist>>
    fun getTracks(id: Int, offset: Int = 1, count: Int = 99): Single<List<Track>>
    fun isFavourited(id: Int): Single<Boolean>
    fun removeFavourite(id: Int): Single<Any>
    fun addFavourite(id: Int): Single<Any>
    fun getFavouritedAndOwned(
        offset: Int,
        count: Int,
        currentUserId: Int? = null
    ): Single<List<Playlist>>

    fun getFavourited(offset: Int, count: Int, currentUserId: Int? = null): Single<List<Playlist>>
    fun getFavouritedCount(): Single<Int>
}
