package com.truedigital.features.tuned.data.stream.repository

import com.truedigital.features.tuned.data.station.model.request.OfflinePlaybackState
import com.truedigital.features.tuned.data.station.model.request.PlaybackState
import com.truedigital.foundation.player.model.MediaAsset
import io.reactivex.Single

interface StreamRepository {
    fun get(deviceId: Int, trackId: Int, sessionId: String): Single<MediaAsset>
    fun get(stakkarId: Int): Single<MediaAsset>

    fun putPlaybackState(uniqueId: Int, playbackState: PlaybackState): Single<Any>
    fun putOfflinePlaybackState(
        uniqueId: Int,
        offlinePlaybackStates: List<OfflinePlaybackState>
    ): Single<Any>
}
