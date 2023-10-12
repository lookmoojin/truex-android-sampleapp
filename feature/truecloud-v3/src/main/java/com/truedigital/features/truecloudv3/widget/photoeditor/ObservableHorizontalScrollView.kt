package com.truedigital.features.truecloudv3.widget.photoeditor

import android.annotation.SuppressLint
import android.content.Context
import android.widget.HorizontalScrollView

@SuppressLint("ViewConstructor")
internal class ObservableHorizontalScrollView(
    context: Context,
    private val scrollChangedListener: ScrollChangedListener
) : HorizontalScrollView(context) {

    internal interface ScrollChangedListener {
        fun onScrollChanged()
        fun onScrollStopped()
    }

    companion object {
        private const val NEW_CHECK_DURATION = 100L
    }

    private val mScrollerTask: Runnable = object : Runnable {
        override fun run() {
            if (System.currentTimeMillis() - lastScrollUpdateMills > NEW_CHECK_DURATION) {
                lastScrollUpdateMills = -1
                scrollChangedListener.onScrollStopped()
            } else {
                postDelayed(this, NEW_CHECK_DURATION)
            }
        }
    }
    private var lastScrollUpdateMills: Long = -1

    override fun onScrollChanged(
        horizontalOrigin: Int,
        verticalOrigin: Int,
        oldHorizontalOrigin: Int,
        oldVerticalOrigin: Int
    ) {
        super.onScrollChanged(
            horizontalOrigin,
            verticalOrigin,
            oldHorizontalOrigin,
            oldVerticalOrigin
        )
        scrollChangedListener.onScrollChanged()
        if (lastScrollUpdateMills == -1L) postDelayed(mScrollerTask, NEW_CHECK_DURATION)
        lastScrollUpdateMills = System.currentTimeMillis()
    }
}
