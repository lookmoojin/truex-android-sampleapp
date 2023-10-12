package com.truedigital.features.truecloudv3.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ViewFlipper
import com.google.android.exoplayer2.ui.PlayerControlView
import com.truedigital.features.truecloudv3.R
import com.truedigital.foundation.extension.onClick

class TrueCloudV3VideoControllerView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : PlayerControlView(context, attrs, defStyleAttr), PlayerControlView.ProgressUpdateListener {

    interface OnVideoControlListener {
        fun onEnterFullScreenClick()
        fun onExitFullScreenClick()
    }

    companion object {
        private const val AUTO_HIDE_DURATION = 4000
        private const val VIEW_FLIPPER_CHILD_PLAY = 0
        private const val VIEW_FLIPPER_CHILD_PAUSE = 1
        private const val VIEW_FLIPPER_CHILD_FULL_SCREEN = 0
        private const val VIEW_FLIPPER_CHILD_EXIT_FULL_SCREEN = 1
    }

    private val playerControlViewDoubleTapGestureListener = GestureDetector(
        context,
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(event: MotionEvent): Boolean {
                toggleControl()
                return super.onSingleTapConfirmed(event)
            }
        }
    )

    @SuppressLint("ClickableViewAccessibility")
    private val playerControlViewTouchListener = OnTouchListener { _, event ->
        playerControlViewDoubleTapGestureListener.onTouchEvent(event)
        true
    }

    private var controlLayout: ViewGroup? = null
    private var playPauseBackgroundViewFlipper: ViewFlipper? = null
    private var playButton: View? = null
    private var pauseButton: View? = null
    private var seekbar: TrueCloudV3PlayerSeekBarView? = null
    private var toggleFullscreenViewFlipper: ViewFlipper? = null
    private var fullscreenImageView: ImageView? = null
    private var exitFullscreenImageView: ImageView? = null

    var onVideoControlListener: OnVideoControlListener? = null

    init {
        controlLayout = findViewById(R.id.controlLayout)
        playPauseBackgroundViewFlipper = findViewById(R.id.playPauseBackgroundViewFlipper)
        playButton = findViewById(R.id.playBackgroundImageView)
        pauseButton = findViewById(R.id.pauseBackgroundImageView)
        seekbar = findViewById(R.id.trueCloudPlayerSeekBar)
        toggleFullscreenViewFlipper = findViewById(R.id.toggleFullscreenViewFlipper)
        fullscreenImageView = findViewById(R.id.fullscreenImageView)
        exitFullscreenImageView = findViewById(R.id.exitFullscreenImageView)
        showTimeoutMs = AUTO_HIDE_DURATION
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        initView()
    }

    override fun onProgressUpdate(position: Long, bufferedPosition: Long) {
        val duration = player?.contentDuration?.coerceAtLeast(0) ?: 0L
        seekbar?.setPlaybackTime(position, duration)
    }

    fun toggleControl() {
        if (isVisible) {
            hide()
        } else {
            show()
        }
    }

    fun setShowPause() {
        playPauseBackgroundViewFlipper?.displayedChild = VIEW_FLIPPER_CHILD_PAUSE
    }

    fun setShowPlay() {
        playPauseBackgroundViewFlipper?.displayedChild = VIEW_FLIPPER_CHILD_PLAY
    }

    fun reset() {
        playPauseBackgroundViewFlipper?.displayedChild = VIEW_FLIPPER_CHILD_PLAY
        seekbar?.reset()
        player?.playWhenReady = false
        player?.seekTo(0)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initView() {
        playButton?.onClick {
            togglePlayPauseButtonState()
        }
        pauseButton?.onClick {
            togglePlayPauseButtonState()
        }
        seekbar?.onSeekListener =
            object : TrueCloudV3PlayerSeekBarView.OnSeekListener {
                override fun onSeekTo(progress: Long) {
                    player?.seekTo(progress)
                }
            }
        fullscreenImageView?.onClick {
            onVideoControlListener?.onEnterFullScreenClick()
            toggleFullscreenViewFlipper?.displayedChild = VIEW_FLIPPER_CHILD_EXIT_FULL_SCREEN
        }
        exitFullscreenImageView?.onClick {
            onVideoControlListener?.onExitFullScreenClick()
            toggleFullscreenViewFlipper?.displayedChild = VIEW_FLIPPER_CHILD_FULL_SCREEN
        }

        controlLayout?.setOnTouchListener(playerControlViewTouchListener)
        setProgressUpdateListener(this)
    }

    private fun togglePlayPauseButtonState() {
        if (player?.isPlaying == true) {
            playPauseBackgroundViewFlipper?.displayedChild = VIEW_FLIPPER_CHILD_PLAY
            player?.pause()
        } else {
            playPauseBackgroundViewFlipper?.displayedChild = VIEW_FLIPPER_CHILD_PAUSE
            player?.play()
        }
    }
}
