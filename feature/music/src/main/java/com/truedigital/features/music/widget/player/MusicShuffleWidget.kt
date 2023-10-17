package com.truedigital.features.music.widget.player

import android.content.Context
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageButton
import com.truedigital.features.music.injections.MusicComponent
import com.truedigital.features.music.manager.player.MusicPlayerActionManager
import javax.inject.Inject

class MusicShuffleWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatImageButton(context, attrs, defStyleAttr) {

    @Inject
    lateinit var mediaSession: MediaSessionCompat

    @Inject
    lateinit var musicPlayerActionManager: MusicPlayerActionManager

    init {
        MusicComponent.getInstance().getInstanceComponent().inject(this)

        setOnClickListener { view ->
            musicPlayerActionManager.actionShuffle()
            view.isSelected = !view.isSelected
        }
    }

    fun updateState() {
        when (mediaSession.controller?.shuffleMode) {
            PlaybackStateCompat.SHUFFLE_MODE_ALL -> {
                isSelected = true
            }
            PlaybackStateCompat.SHUFFLE_MODE_NONE -> {
                isSelected = false
            }
        }
    }
}
