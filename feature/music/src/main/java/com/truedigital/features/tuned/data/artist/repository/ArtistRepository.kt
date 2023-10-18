package com.truedigital.features.tuned.data.artist.repository

import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.track.model.request.TrackRequestType
import io.reactivex.Single

interface ArtistRepository {
    fun get(id: Int, refresh: Boolean = false): Single<Artist>
    fun getPlayCount(id: Int): Single<Int>
    fun getTrending(offset: Int, count: Int): Single<List<Artist>>
    fun getStationTrending(stationId: Int): Single<List<Artist>>
    fun getSimilar(artistId: Int): Single<List<Artist>>
    fun getFollowed(offset: Int, count: Int): Single<List<Artist>>
    fun getAlbums(artistId: Int, sortType: String?, offset: Int, count: Int): Single<List<Album>>
    fun getAppearsOn(artistId: Int, sortType: String?): Single<List<Album>>
    fun getTracks(
        artistId: Int,
        offset: Int,
        count: Int,
        type: TrackRequestType,
        sortType: String? = null,
        releasedInDays: Int? = null
    ): Single<List<Track>>

    fun getRecommendedArtists(offset: Int, count: Int): Single<List<Artist>>

    fun isFollowing(artistId: Int): Single<Boolean>
    fun removeFollow(artistId: Int): Single<Any>
    fun addFollow(artistId: Int): Single<Any>
    fun getFollowedCount(): Single<Int>
    fun clearArtistVotes(artistId: Int, voteType: String): Single<Any>
}
