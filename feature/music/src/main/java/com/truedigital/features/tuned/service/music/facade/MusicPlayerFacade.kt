package com.truedigital.features.tuned.service.music.facade

import com.truedigital.features.tuned.data.player.PlayerSource
import com.truedigital.features.tuned.data.station.model.Stakkar
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.station.model.TrackExtras
import com.truedigital.features.tuned.data.station.model.Vote
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.foundation.player.model.MediaAsset
import io.reactivex.Single

interface MusicPlayerFacade {
    fun loadStakkar(stakkarId: Int): Single<Stakkar>
    fun loadTrack(trackId: Int): Single<Track>

    fun enqueueStation(station: Station, trackHash: String? = null): Single<List<Track>>
    fun streamTrack(track: Track): Single<MediaAsset>
    fun streamStakkar(stakkar: Stakkar): Single<MediaAsset>
    fun enqueueTrackExtras(
        source: PlayerSource,
        nextTrack: Track?,
        previousTrack: Track?,
        includeAds: Boolean
    ): Single<TrackExtras>

    fun checkAdCount(): Boolean
    fun resetAdCount()

    fun addTrackHistory(track: Track): Single<Long>

    fun getTotalSkips(): Single<Int>
    fun checkLocalSkipCount(): Single<Int>
    fun addLocalSkip(): Single<Long>

    fun getTotalPlays(): Single<Int>
    fun addLocalPlay(): Single<Long>

    fun resetPlayTrackLog()
    fun logTrackSkip(
        source: PlayerSource,
        track: Track,
        elapsed: Long,
        sessionId: String?
    ): Single<Any>

    fun logTrackFinish(
        source: PlayerSource,
        track: Track,
        elapsed: Long,
        sessionId: String?
    ): Single<Any>

    fun logTrackPlay(
        source: PlayerSource,
        track: Track,
        elapsed: Long,
        sessionId: String?
    ): Single<Any>

    fun likeTrack(station: Station, track: Track): Single<Vote>
    fun dislikeTrack(station: Station, track: Track): Single<Vote>
    fun removeRating(station: Station, track: Track): Single<Vote>

    fun setShufflePlay(enabled: Boolean)
    fun isShufflePlayEnabled(): Boolean
    fun setRepeatMode(mode: Int)
    fun getRepeatMode(): Int

    fun isStreamingEnabled(): Boolean
    fun hasTrackPlayRight(): Boolean
    fun hasSequentialPlaybackRight(): Boolean
    fun isPlayLimitEnabled(): Boolean
    fun isSkipLimitEnabled(): Boolean

    fun getLikesCount(): Int
}
