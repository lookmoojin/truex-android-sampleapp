package com.truedigital.features.tuned.data.track.repository

import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.track.model.request.TrackRequestType
import com.truedigital.features.tuned.data.track.model.response.RawLyricString
import io.reactivex.Single

interface TrackRepository {
    fun get(id: Int): Single<Track>
    fun getList(ids: List<Int>): Single<List<Track>>
    fun validateTracks(ids: List<Int>): Single<List<Int>>

    fun isFavourited(id: Int): Single<Boolean>
    fun removeFavourite(id: Int): Single<Any>
    fun addFavourite(id: Int): Single<Any>
    fun getFavourited(offset: Int, count: Int, type: TrackRequestType): Single<List<Track>>
    fun getFavouritedSongsCount(): Single<Int>
    fun getFavouritedVideosCount(): Single<Int>

    fun getLyric(id: Int): Single<RawLyricString>
}
