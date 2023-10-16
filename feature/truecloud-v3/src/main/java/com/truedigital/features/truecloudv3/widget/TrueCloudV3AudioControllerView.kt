package com.truedigital.features.truecloudv3.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.ViewFlipper
import com.google.android.exoplayer2.ui.PlayerControlView
import com.tdg.truecloud.R

class TrueCloudV3AudioControllerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : PlayerControlView(context, attrs, defStyleAttr), PlayerControlView.ProgressUpdateListener {

    private var playPauseViewFlipper: ViewFlipper? = null
    private var playButton: ImageView? = null
    private var pauseButton: ImageView? = null
    private var seekbar: TrueCloudV3PlayerSeekBarView? = null

    init {
        playPauseViewFlipper = findViewById(R.id.playPauseBackgroundViewFlipper)
        playButton = findViewById(R.id.playBackgroundImageView)
        pauseButton = findViewById(R.id.pauseBackgroundImageView)
        seekbar = findViewById(R.id.trueCloudPlayerSeekBar)
        showTimeoutMs = 0
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        playButton?.setOnClickListener {
            player?.play()
        }
        pauseButton?.setOnClickListener {
            player?.pause()
        }
        seekbar?.onSeekListener = object : TrueCloudV3PlayerSeekBarView.OnSeekListener {
            override fun onSeekTo(progress: Long) {
                player?.seekTo(progress)
            }
        }

        setProgressUpdateListener(this)
    }

    override fun onProgressUpdate(position: Long, bufferedPosition: Long) {
        val duration = player?.contentDuration?.coerceAtLeast(0) ?: 0L
        seekbar?.setPlaybackTime(position, duration)
    }

    fun setShowPlay() {
        playPauseViewFlipper?.displayedChild = 0
    }

    fun setShowPause() {
        playPauseViewFlipper?.displayedChild = 1
    }

    fun reset() {
        playPauseViewFlipper?.displayedChild = 0
        seekbar?.reset()
        player?.playWhenReady = false
        player?.seekTo(0)
    }
}
