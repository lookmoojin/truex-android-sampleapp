package com.truedigital.features.music.widget.player

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import com.truedigital.features.music.injections.MusicComponent
import com.truedigital.features.music.manager.player.MusicPlayerActionManager
import javax.inject.Inject

class MusicPlayPauseWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageButton(context, attrs, defStyleAttr) {

    @Inject
    lateinit var mediaSession: MediaSessionCompat

    @Inject
    lateinit var musicPlayerActionManager: MusicPlayerActionManager

    private var isReplayMode = false

    init {
        MusicComponent.getInstance().getInstanceComponent().inject(this)

        setOnClickListener { view ->
            if (view.isSelected) {
                if (isReplayMode) {
                    musicPlayerActionManager.actionReplay()
                    isReplayMode = false
                } else {
                    musicPlayerActionManager.actionPlay()
                }
            } else {
                musicPlayerActionManager.actionPause()
            }
            view.isSelected = !view.isSelected
        }
    }

    @SuppressLint("SwitchIntDef")
    fun updateState() {
        when (mediaSession.controller?.playbackState?.state) {
            PlaybackStateCompat.STATE_PLAYING -> {
                isSelected = false
            }
            PlaybackStateCompat.STATE_STOPPED,
            PlaybackStateCompat.STATE_PAUSED,
            PlaybackStateCompat.STATE_ERROR -> {
                isSelected = true
            }
        }
    }

    fun setPlayState() {
        isSelected = false
    }

    fun setPauseState() {
        isSelected = true
    }

    fun setReplayState() {
        isSelected = true
        isReplayMode = true
    }

    fun setNotReplayState() {
        isReplayMode = false
    }
}
