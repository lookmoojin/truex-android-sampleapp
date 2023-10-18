package com.truedigital.features.tuned.data.station.repository

import com.truedigital.features.tuned.data.ad.model.AdProvider
import com.truedigital.features.tuned.data.station.model.LikedTrack
import com.truedigital.features.tuned.data.station.model.Rating
import com.truedigital.features.tuned.data.station.model.Stakkar
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.station.model.StationVote
import com.truedigital.features.tuned.data.station.model.TrackExtras
import com.truedigital.features.tuned.data.station.model.Vote
import com.truedigital.features.tuned.data.track.model.Track
import io.reactivex.Single

interface StationRepository {
    fun get(id: Int, refresh: Boolean = false): Single<Station>
    fun getLocalMix(userId: Int): Single<Station>
    fun getByArtist(artistId: Int): Single<Station>
    fun getContainingArtist(id: Int): Single<List<Station>>
    fun getSuggested(offset: Int, count: Int): Single<List<Station>>
    fun getSimilar(stationId: Int): Single<List<Station>>
    fun getPopular(): Single<List<Station>>
    fun getRecent(): Single<List<Station>>
    fun getFavourited(offset: Int, count: Int): Single<List<Station>>

    fun getSyncTracks(stationId: Int): Single<List<Track>>
    fun getStakkar(stakkarId: Int): Single<Stakkar>
    fun getTracks(stationId: Int, trackHash: String? = null): Single<List<Track>>
    fun getTrackExtras(
        uniqueId: Int,
        stationId: Int,
        nextTrackId: Int?,
        previousTrackId: Int?,
        includeAds: Boolean = false,
        adProvider: AdProvider,
        lsid: String
    ): Single<TrackExtras>

    fun getVotes(stationId: Int, userId: Int): Single<List<LikedTrack>>
    fun deleteVote(stationId: Int, trackId: Int): Single<List<StationVote>>
    fun deleteVotes(stationId: Int): Single<Any>
    fun putVote(stationId: Int, trackId: Int, rating: Rating): Single<Vote>

    fun isFavourited(stationId: Int): Single<Boolean>
    fun removeFavourite(stationId: Int): Single<Any>
    fun addFavourite(stationId: Int): Single<Any>
    fun getFavouritedCount(): Single<Int>

    enum class PlaybackAction(val action: String) {
        START("Start"),
        END("End"),
        SKIP("Skip"),
        PROGRESS("Progress")
    }
}
