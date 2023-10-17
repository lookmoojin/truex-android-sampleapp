package com.truedigital.features.music.widget.player

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout
import com.truedigital.features.tuned.R
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.visible

class MusicPlayerMotionLayout(
    context: Context,
    attributeSet: AttributeSet? = null
) : MotionLayout(context, attributeSet) {

    private val viewToDetectTouch by lazy { findViewById<View>(R.id.playerBackgroundCardView) }
    private val transitionListenerList = mutableListOf<TransitionListener?>()
    private val viewRect = Rect()
    private var hasTouchStarted = false
    private var currentConstrainId: Int? = null
    private var isClosePlayer: Boolean = false
    var onPlayerStateChange: ((Boolean) -> Unit)? = null

    init {
        addTransitionListener(object : TransitionListener {
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
                // nothing
            }

            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
                // nothing
            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
                // nothing
            }

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                hasTouchStarted = false
            }
        })

        super.setTransitionListener(object : TransitionListener {
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
                // nothing
            }

            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
                // nothing
            }

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
                transitionListenerList.filterNotNull()
                    .forEach { it.onTransitionChange(p0, p1, p2, p3) }
            }

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
                currentConstrainId = p1
                transitionListenerList.filterNotNull().forEach {
                    it.onTransitionCompleted(p0, p1)
                }
                val isExpand = isExpand()
                onPlayerStateChange?.invoke(isExpand)

                if (isExpand) {
                    this@MusicPlayerMotionLayout.apply {
                        isFocusableInTouchMode = true
                        requestFocus()
                    }
                } else if (isClosePlayer) {
                    isClosePlayer = false
                    setMiniPlayerVisibility(false)
                    refreshView()
                }
            }
        })
    }

    override fun setTransitionListener(listener: TransitionListener?) {
        addTransitionListener(listener)
    }

    override fun addTransitionListener(listener: TransitionListener?) {
        transitionListenerList += listener
    }

    private val gestureDetector = GestureDetector(
        context,
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                transitionToEnd()
                return false
            }
        }
    )

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.onTouchEvent(event) // This ensures the Mini Player is maximised on single tap
        when (event.actionMasked) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                hasTouchStarted = false
                return super.onTouchEvent(event)
            }
        }
        if (!hasTouchStarted) {
            viewToDetectTouch.getHitRect(viewRect)
            hasTouchStarted = viewRect.contains(event.x.toInt(), event.y.toInt())
        }
        return hasTouchStarted && super.onTouchEvent(event)
    }

    fun refreshView() {
        this@MusicPlayerMotionLayout.apply {
            gone()
            visible()
        }
    }

    fun closePlayerView() {
        isClosePlayer = true
        transitionToStart()
    }

    fun isExpand(): Boolean {
        return currentConstrainId == R.id.end_full_player
    }

    fun addBottomMarginMiniPlayer(isAdd: Boolean) {
        getConstraintSet(R.id.start_mini_player)?.apply {
            var bottomMarginSize = 0
            if (isAdd) {
                bottomMarginSize = resources.getDimensionPixelSize(R.dimen.bottom_nav_height_size)
            }
            getConstraint(R.id.playerBackgroundCardView).layout.bottomMargin = bottomMarginSize
        }
    }

    fun setMiniPlayerVisibility(
        isVisible: Boolean,
        onVisibilityViewChange: ((Boolean) -> Unit)? = null
    ) {
        onVisibilityViewChange?.invoke(isVisible)

        getConstraintSet(R.id.start_mini_player)?.apply {
            if (isVisible) {
                setVisibility(R.id.playerBackgroundCardView, View.VISIBLE)
                setVisibility(R.id.thumbnailImageView, View.VISIBLE)
                setVisibility(R.id.playPauseBackgroundImageView, View.VISIBLE)
                setVisibility(R.id.playPauseButton, View.VISIBLE)
                setVisibility(R.id.shadowTopView, View.VISIBLE)
                setVisibility(R.id.shadowBottomView, View.VISIBLE)
                setVisibility(R.id.miniSongNameTextView, View.VISIBLE)
                setVisibility(R.id.miniArtistNameTextView, View.VISIBLE)
                applyTo(this@MusicPlayerMotionLayout)
            } else {
                setVisibility(R.id.playerBackgroundCardView, View.GONE)
                setVisibility(R.id.thumbnailImageView, View.GONE)
                setVisibility(R.id.playPauseBackgroundImageView, View.GONE)
                setVisibility(R.id.playPauseButton, View.GONE)
                setVisibility(R.id.shadowTopView, View.GONE)
                setVisibility(R.id.shadowBottomView, View.GONE)
                setVisibility(R.id.miniSongNameTextView, View.GONE)
                setVisibility(R.id.miniArtistNameTextView, View.GONE)
                applyTo(this@MusicPlayerMotionLayout)
            }
        }
    }

    fun setFullPlayerStyle(isTrackStyle: Boolean) {
        getConstraintSet(R.id.end_full_player)?.apply {
            setVisibility(R.id.queueButton, getVisibleOrInvisible(isTrackStyle))
            setVisibility(R.id.shareButton, getVisibleOrInvisible(isTrackStyle))
            setVisibility(R.id.favoriteButton, getVisibleOrInvisible(isTrackStyle))
            setVisibility(R.id.repeatButton, getVisibleOrInvisible(isTrackStyle))
            setVisibility(R.id.nextButton, getVisibleOrInvisible(isTrackStyle))
            setVisibility(R.id.previousButton, getVisibleOrInvisible(isTrackStyle))
            setVisibility(R.id.shuffleButton, getVisibleOrInvisible(isTrackStyle))
            setVisibility(R.id.musicSeekBarWidget, getVisibleOrInvisible(isTrackStyle))
            setVisibility(R.id.moreButton, getVisibleOrGone(isTrackStyle))
            applyTo(this@MusicPlayerMotionLayout)
        }
    }

    fun setDisplayFullPlayer(isEnable: Boolean) {
        enableTransition(R.id.musicPlayerTransition, isEnable)
    }

    private fun getVisibleOrInvisible(isVisible: Boolean): Int {
        return if (isVisible) View.VISIBLE else View.INVISIBLE
    }

    private fun getVisibleOrGone(isVisible: Boolean): Int {
        return if (isVisible) View.VISIBLE else View.GONE
    }
}
