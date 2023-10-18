package com.truedigital.features.tuned.data.album.repository

import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.album.model.Release
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.track.model.request.TrackRequestType
import io.reactivex.Single

interface AlbumRepository {
    fun get(id: Int, refresh: Boolean = false): Single<Album>
    fun getTracks(releaseId: Int, type: TrackRequestType): Single<List<Track>>
    fun getTrending(offset: Int, count: Int): Single<List<Album>>
    fun getNewReleases(offset: Int, count: Int): Single<List<Album>>
    fun getMoreFromArtist(id: Int): Single<List<Album>>
    fun isFavourited(id: Int): Single<Boolean>
    fun removeFavourite(id: Int): Single<Any>
    fun addFavourite(id: Int): Single<Any>
    fun getFavourited(offset: Int, count: Int): Single<List<Release>>
    fun getFavouritedCount(): Single<Int>
}
