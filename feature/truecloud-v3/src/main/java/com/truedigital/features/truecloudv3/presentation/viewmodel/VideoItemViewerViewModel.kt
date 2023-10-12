package com.truedigital.features.truecloudv3.presentation.viewmodel

import android.content.pm.ActivityInfo
import androidx.lifecycle.LiveData
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.video.VideoSize
import com.truedigital.core.base.BaseViewModel
import com.truedigital.foundation.extension.SingleLiveEvent
import javax.inject.Inject

class VideoItemViewerViewModel @Inject constructor() : BaseViewModel() {

    companion object {
        private const val LANDSCAPE_ASPECT_RATIO = 1.0F
        private const val ZERO_SIZE = 0
        private const val ROTATION_PORTRAIT = 90
        private const val ROTATION_REVERSE_PORTRAIT = 270
    }

    private val _onShowPlay = SingleLiveEvent<Unit>()
    private val _onShowPause = SingleLiveEvent<Unit>()
    private val _onSetOrientation = SingleLiveEvent<Int>()
    private val _onHideProgress = SingleLiveEvent<Unit>()
    private val _onResetPlayer = SingleLiveEvent<Unit>()
    val onShowPlay: LiveData<Unit> get() = _onShowPlay
    val onShowPause: LiveData<Unit> get() = _onShowPause
    val onSetOrientation: LiveData<Int> get() = _onSetOrientation
    val onHideProgress: LiveData<Unit> get() = _onHideProgress
    val onResetPlayer: LiveData<Unit> get() = _onResetPlayer

    fun onPlayWhenReadyChanged(playWhenReady: Boolean) {
        if (playWhenReady) {
            _onShowPause.value = Unit
        } else {
            _onShowPlay.value = Unit
        }
    }

    fun onEnterFullScreenClick(videoSize: VideoSize?) {
        val videoAspectRatio = getVideoAspectRatio(videoSize ?: VideoSize.UNKNOWN)
        val orientation = if (videoAspectRatio > LANDSCAPE_ASPECT_RATIO) {
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        }
        _onSetOrientation.value = orientation
    }

    fun onPlaybackStateChanged(playbackState: Int) {
        when (playbackState) {
            Player.STATE_READY -> {
                _onHideProgress.value = Unit
            }

            Player.STATE_ENDED -> {
                _onResetPlayer.value = Unit
            }
        }
    }

    private fun getVideoAspectRatio(videoSize: VideoSize): Float {
        val width = videoSize.width
        val height = videoSize.height
        val unappliedRotationDegrees = videoSize.unappliedRotationDegrees
        val videoAspectRatio = if (height == ZERO_SIZE || width == ZERO_SIZE) {
            0F
        } else {
            if (unappliedRotationDegrees == ROTATION_PORTRAIT ||
                unappliedRotationDegrees == ROTATION_REVERSE_PORTRAIT
            ) {
                height * videoSize.pixelWidthHeightRatio / width
            } else {
                width * videoSize.pixelWidthHeightRatio / height
            }
        }
        return videoAspectRatio
    }
}
