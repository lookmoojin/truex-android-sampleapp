package com.truedigital.features.music.widget.player

import android.content.Context
import android.support.v4.media.session.MediaControllerCompat
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.SeekBar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.truedigital.features.music.injections.MusicComponent
import com.truedigital.features.music.manager.player.MusicPlayerActionManager
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.common.extensions.duration
import com.truedigital.features.tuned.common.extensions.toDurationString
import com.truedigital.features.tuned.databinding.ViewMusicSeekBarBinding
import javax.inject.Inject

class MusicSeekBarWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val MAX_SEEKBAR = 1000
    }

    private val binding by lazy {
        ViewMusicSeekBarBinding.inflate(LayoutInflater.from(context))
    }

    @Inject
    lateinit var musicPlayerActionManager: MusicPlayerActionManager

    private var blockProgressUpdate = false
    private var lastPlaybackPosition: Long = 0L
    var unableReplayStateListener: (() -> Unit)? = null

    init {
        MusicComponent.getInstance().inject(this)

        addView(binding.root)
        initListener()
    }

    fun start(duration: Long) = with(binding) {
        maxTimeTextView.text = duration.toDurationString()
        currentTimeTextView.text = lastPlaybackPosition.toDurationString()
        musicSeekBar.progress = lastPlaybackPosition.toInt()
        musicSeekBar.max = duration.toInt()
    }

    fun replay(duration: Long) {
        lastPlaybackPosition = 0L
        start(duration)
        musicPlayerActionManager.actionSeek(0)
    }

    fun update(controller: MediaControllerCompat) = with(binding) {
        val playbackState = controller.playbackState ?: return
        // in case the duration is not able to be set before loading the track (API returning duration 1),
        // use the player to fix the duration and update afterwards
        if (musicSeekBar.max == MAX_SEEKBAR) {
            val duration = controller.metadata.duration
            musicSeekBar.max = duration.toInt()
            currentTimeTextView.text = duration.toDurationString()
        }
        if (playbackState.position != lastPlaybackPosition && !blockProgressUpdate) {
            lastPlaybackPosition = playbackState.position
            musicSeekBar.progress = playbackState.position.toInt()
            currentTimeTextView.text = playbackState.position.toDurationString()
        }
    }

    private fun initListener() = with(binding) {
        musicSeekBar.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    if (!fromUser) return
                    currentTimeTextView.text = progress.toLong().toDurationString()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                    blockProgressUpdate = true
                    context?.let { _context ->
                        seekBar?.thumb = ContextCompat.getDrawable(
                            _context,
                            R.drawable.music_ic_seekbar_thumb_pressed
                        )
                    }
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                    blockProgressUpdate = false
                    context?.let { _context ->
                        seekBar?.thumb = ContextCompat.getDrawable(
                            _context,
                            R.drawable.music_ic_seekbar_thumb
                        )
                    }
                    musicPlayerActionManager.actionSeek(seekBar?.progress?.toLong() ?: 0L)
                    unableReplayStateListener?.invoke()
                }
            }
        )
    }
}
