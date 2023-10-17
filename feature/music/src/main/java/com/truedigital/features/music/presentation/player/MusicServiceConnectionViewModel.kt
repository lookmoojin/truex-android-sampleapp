package com.truedigital.features.music.presentation.player

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.truedigital.features.tuned.common.extensions.duration
import com.truedigital.features.tuned.common.extensions.isFirstTrack
import com.truedigital.features.tuned.common.extensions.isLastTrack
import com.truedigital.features.tuned.common.extensions.trackId
import com.truedigital.foundation.extension.SingleLiveEvent
import javax.inject.Inject

class MusicServiceConnectionViewModel @Inject constructor(
    private val mediaSession: MediaSessionCompat
) : ViewModel() {

    private val mediaControllerCallback by lazy { getMediaControllerCompatCallback() }
    private var lastPlaybackState: Int = -1
    private var lastThumbUrl: String = ""

    private val updateView = MutableLiveData<MediaMetadataCompat?>()
    private val hidePlayer = SingleLiveEvent<Unit>()
    private val updateSeekBar = SingleLiveEvent<MediaControllerCompat>()
    private val playingState = SingleLiveEvent<Unit>()
    private val pauseState = SingleLiveEvent<Unit>()
    private val replayState = SingleLiveEvent<Long>()
    private val shareSong = SingleLiveEvent<String>()
    private val canPausePlayer = SingleLiveEvent<Boolean>()
    private val trackFAShareSong = SingleLiveEvent<MediaMetadataCompat>()

    fun onUpdateView(): LiveData<MediaMetadataCompat?> = updateView
    fun onHidePlayer(): LiveData<Unit> = hidePlayer
    fun onUpdateSeekBar(): LiveData<MediaControllerCompat> = updateSeekBar
    fun onPlayingState(): LiveData<Unit> = playingState
    fun onPauseState(): LiveData<Unit> = pauseState
    fun onReplayState(): LiveData<Long> = replayState
    fun onShareSong(): LiveData<String> = shareSong
    fun onCanPausePlayer(): LiveData<Boolean> = canPausePlayer
    fun onTrackFAShareSong(): LiveData<MediaMetadataCompat> = trackFAShareSong

    fun register() {
        mediaSession.controller?.let { controller ->
            controller.registerCallback(mediaControllerCallback)
            setupVisibility()
        }
    }

    fun unRegister() {
        mediaSession.controller?.unregisterCallback(mediaControllerCallback)
    }

    fun isPlayerInActiveState(): Boolean =
        mediaSession.controller?.playbackState == null || mediaSession.controller?.metadata == null

    fun isPlayerErrorState(): Boolean =
        mediaSession.controller?.playbackState?.state == PlaybackStateCompat.STATE_ERROR

    fun getLastThumbUrl(): String = lastThumbUrl

    fun saveLastThumbUrl(url: String) {
        lastThumbUrl = url
    }

    fun clearLastThumbUrl() {
        lastThumbUrl = ""
    }

    fun getRepeatMode(): Int {
        return mediaSession.controller?.repeatMode ?: PlaybackStateCompat.REPEAT_MODE_INVALID
    }

    fun getShuffleMode(): Int {
        return mediaSession.controller?.shuffleMode ?: PlaybackStateCompat.SHUFFLE_MODE_INVALID
    }

    fun getTrackId(): Int {
        return mediaSession.controller?.metadata?.trackId ?: 0
    }

    fun getPlayPosition(): Long? {
        return mediaSession.controller?.playbackState?.position
    }

    fun isFirstTrack(): Boolean {
        return mediaSession.controller?.metadata?.isFirstTrack ?: false
    }

    fun isLastTrack(): Boolean {
        return mediaSession.controller?.metadata?.isLastTrack ?: false
    }

    fun navigateToShare() {
        mediaSession.controller?.metadata?.let { metaData ->
            trackFAShareSong.value = metaData
        }
        shareSong.value = (mediaSession.controller?.metadata?.trackId ?: 0).toString()
    }

    fun onResumePlayer() {
        if (isPlayerErrorState()) {
            replayState.value = mediaSession.controller?.metadata?.duration ?: 0L
        }
    }

    fun handlerPausePlayer() {
        canPausePlayer.value = isPlayerInActiveState().not()
    }

    private fun setupVisibility() {
        if (isPlayerInActiveState()) {
            hidePlayer.value = Unit
        } else {
            updateView.value = mediaSession.controller?.metadata
        }
    }

    private fun getMediaControllerCompatCallback() = object : MediaControllerCompat.Callback() {
        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            updatePlaybackState()
        }

        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            updateView.postValue(metadata)
        }
    }

    fun updatePlaybackState() {
        mediaSession.controller?.let { controller ->
            val playbackState = controller.playbackState ?: return
            triggerSeekbar(controller)
            if (playbackState.state != lastPlaybackState) {
                lastPlaybackState = playbackState.state
                when (playbackState.state) {
                    PlaybackStateCompat.STATE_PLAYING -> {
                        playingState.value = Unit
                    }
                    PlaybackStateCompat.STATE_STOPPED,
                    PlaybackStateCompat.STATE_PAUSED -> {
                        pauseState.value = Unit
                    }
                    PlaybackStateCompat.STATE_ERROR -> {
                        replayState.value = mediaSession.controller?.metadata?.duration ?: 0L
                    }
                    else -> {
                        // nothing
                    }
                }
            }
        }
    }

    private fun triggerSeekbar(controller: MediaControllerCompat) {
        val state = controller.playbackState.state
        if (state == PlaybackStateCompat.STATE_PLAYING ||
            state == PlaybackStateCompat.STATE_PAUSED ||
            state == PlaybackStateCompat.STATE_STOPPED
        ) {
            updateSeekBar.postValue(controller)
        }
    }
}
