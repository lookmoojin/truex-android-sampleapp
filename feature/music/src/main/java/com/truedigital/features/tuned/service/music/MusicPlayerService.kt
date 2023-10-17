package com.truedigital.features.tuned.service.music

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.view.Surface
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.truedigital.features.music.domain.landing.model.MusicForYouItemModel
import com.truedigital.features.tuned.data.player.PlayerSource
import com.truedigital.features.tuned.data.station.model.Stakkar
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.service.music.model.TrackQueueInfo

abstract class MusicPlayerService : Service() {
    abstract fun playStation(station: Station, trackHash: String? = null)
    abstract fun playOfflineStation(station: Station, tracks: List<Track>)
    abstract fun playTracks(
        tracks: List<Track>,
        playerSource: PlayerSource? = null,
        startIndex: Int? = null,
        forceShuffle: Boolean = false,
        forceSequential: Boolean = false,
        isOffline: Boolean = false
    )

    abstract fun playVideo(video: Track)
    abstract fun playVideo(videoId: Int)
    abstract fun playStakkar(stakkar: Stakkar)
    abstract fun playStakkar(stakkarId: Int)
    abstract fun playRadio(radio: MusicForYouItemModel.RadioShelfItem)
    abstract fun playAd(adId: Int, url: String?)
    abstract fun dismissAd(adId: Int)

    abstract fun attachVideoSurface(surface: Surface)
    abstract fun clearVideoSurface()
    abstract fun attachPlayerView(playerView: StyledPlayerView)

    abstract fun getCurrentQueueInfo(): TrackQueueInfo?

    abstract fun addTracks(
        tracks: List<Track>,
        playerSource: PlayerSource? = null,
        isOffline: Boolean = false
    )

    abstract fun removeTrack(index: Int)
    abstract fun moveTrack(oldIndex: Int, newIndex: Int)
    abstract fun clearQueue()
    abstract fun updateQueue(trackList: List<Track>)

    abstract fun getAvailableSkips(): Int
    abstract fun startMusicForegroundService(intent: Intent)

    inner class PlayerBinder : Binder() {
        val service: MusicPlayerService
            get() = this@MusicPlayerService
    }
}
