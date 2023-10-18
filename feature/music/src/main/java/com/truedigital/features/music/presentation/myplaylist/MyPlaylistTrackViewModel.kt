package com.truedigital.features.music.presentation.myplaylist

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.truedigital.features.tuned.common.extensions.trackId
import com.truedigital.foundation.extension.SingleLiveEvent
import javax.inject.Inject

class MyPlaylistTrackViewModel @Inject constructor(
    private val mediaSession: MediaSessionCompat
) : ViewModel() {

    private val mediaControllerCallback by lazy { getMediaControllerCompatCallback() }

    private val trackChange = SingleLiveEvent<Int>()
    fun onTrackChange(): LiveData<Int> = trackChange

    fun register() {
        mediaSession.controller?.registerCallback(mediaControllerCallback)
    }

    fun unRegister() {
        mediaSession.controller?.unregisterCallback(mediaControllerCallback)
    }

    fun updateCurrentPlayingTrack() {
        if (isPlayerActiveState()) {
            val lastPlayingTrackId = trackChange.value
            val currentPlayingTrackId = mediaSession.controller?.metadata?.trackId

            if (lastPlayingTrackId != currentPlayingTrackId) {
                trackChange.value = mediaSession.controller?.metadata?.trackId
            }
        }
    }

    fun getCurrentPlayingTrackId(): Int? {
        return if (isPlayerActiveState()) {
            mediaSession.controller?.metadata?.trackId
        } else {
            null
        }
    }

    private fun getMediaControllerCompatCallback() = object : MediaControllerCompat.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            trackChange.value = metadata?.trackId
        }
    }

    private fun isPlayerActiveState(): Boolean {
        return mediaSession.controller?.playbackState != null || mediaSession.controller?.metadata != null
    }
}
