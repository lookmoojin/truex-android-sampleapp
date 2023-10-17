package com.truedigital.features.music.widget.player

import android.annotation.SuppressLint
import android.content.Context
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import com.truedigital.features.music.domain.player.model.MusicRepeatState
import com.truedigital.features.music.injections.MusicComponent
import com.truedigital.features.music.manager.player.MusicPlayerActionManager
import com.truedigital.features.tuned.R
import javax.inject.Inject

class MusicRepeatWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageButton(context, attrs, defStyleAttr) {

    @Inject
    lateinit var mediaSession: MediaSessionCompat

    @Inject
    lateinit var musicPlayerActionManager: MusicPlayerActionManager

    private var currentState: MusicRepeatState? = null

    init {
        MusicComponent.getInstance().getInstanceComponent().inject(this)

        this.setOnClickListener {
            onStateChange()
            musicPlayerActionManager.actionRepeat()
        }
    }

    fun updateState() {
        when (mediaSession.controller?.repeatMode) {
            PlaybackStateCompat.REPEAT_MODE_NONE -> {
                setActiveModeNone()
            }
            PlaybackStateCompat.REPEAT_MODE_ALL -> {
                setActiveModeAll()
            }
            PlaybackStateCompat.REPEAT_MODE_ONE -> {
                setActiveModeOne()
            }
            else -> {
                setActiveModeNone()
            }
        }
    }

    @SuppressLint("SwitchIntDef")
    fun onStateChange() {
        when (currentState) {
            MusicRepeatState.MODE_NONE -> {
                setActiveModeAll()
            }
            MusicRepeatState.MODE_ALL -> {
                setActiveModeOne()
            }
            MusicRepeatState.MODE_ONE -> {
                setActiveModeNone()
            }
            else -> {
                setActiveModeNone()
            }
        }
    }

    @SuppressLint("SwitchIntDef")
    fun setActiveModeNone() {
        currentState = MusicRepeatState.MODE_NONE
        this.setImageResource(R.drawable.music_ic_repeat_none)
    }

    @SuppressLint("SwitchIntDef")
    fun setActiveModeAll() {
        currentState = MusicRepeatState.MODE_ALL
        this.setImageResource(R.drawable.music_ic_repeat_all)
    }

    @SuppressLint("SwitchIntDef")
    fun setActiveModeOne() {
        currentState = MusicRepeatState.MODE_ONE
        this.setImageResource(R.drawable.music_ic_repeat_one)
    }
}
