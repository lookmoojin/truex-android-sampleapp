package com.truedigital.features.tuned.data.stream.repository

import com.truedigital.features.tuned.api.steam.StreamApiInterface
import com.truedigital.features.tuned.data.station.model.request.OfflinePlaybackState
import com.truedigital.features.tuned.data.station.model.request.PlaybackState
import com.truedigital.foundation.player.model.MediaAsset
import io.reactivex.Single
import javax.inject.Inject

class StreamRepositoryImpl @Inject constructor(
    private val streamApi: StreamApiInterface
) : StreamRepository {

    override fun get(deviceId: Int, trackId: Int, sessionId: String): Single<MediaAsset> =
        streamApi.streamLocation(sessionId, deviceId, trackId).map {
            MediaAsset(trackId, it, null, sessionId)
        }

    override fun get(stakkarId: Int): Single<MediaAsset> =
        streamApi.stakkarStreamLocation(stakkarId).map { MediaAsset(stakkarId, it) }

    override fun putPlaybackState(uniqueId: Int, playbackState: PlaybackState): Single<Any> =
        streamApi.logPlay(uniqueId, playbackState)

    override fun putOfflinePlaybackState(
        uniqueId: Int,
        offlinePlaybackStates: List<OfflinePlaybackState>
    ): Single<Any> =
        streamApi.logOfflinePlay(uniqueId, offlinePlaybackStates)
}
