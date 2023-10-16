package com.truedigital.component.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.viewpager.widget.ViewPager

class SwipeViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {

    private var swipeabled: Boolean = false
    private var smoothScroll: Boolean = true

    private var mCurrentView: View? = null

    init {
        this.swipeabled = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var newHeightMeasureSpec = heightMeasureSpec
        if (mCurrentView == null) {
            super.onMeasure(widthMeasureSpec, newHeightMeasureSpec)
            return
        }
        var height = 0
        mCurrentView!!.measure(
            widthMeasureSpec,
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        val h = mCurrentView!!.measuredHeight
        if (h > height) height = h
        newHeightMeasureSpec = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY)

        super.onMeasure(widthMeasureSpec, newHeightMeasureSpec)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (this.swipeabled) {
            super.onTouchEvent(event)
        } else {
            false
        }
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (this.swipeabled) {
            super.onInterceptTouchEvent(event)
        } else {
            false
        }
    }

    override fun setCurrentItem(item: Int) {
        super.setCurrentItem(item, this.smoothScroll)
    }

    override fun setCurrentItem(item: Int, smoothScroll: Boolean) {
        super.setCurrentItem(item, this.smoothScroll)
    }

    fun setPagingEnabled(enabled: Boolean) {
        this.swipeabled = enabled
    }

    fun setSmoothScroll(smoothScroll: Boolean) {
        this.smoothScroll = smoothScroll
    }

    fun measureCurrentView(currentView: View) {
        mCurrentView = currentView
        requestLayout()
    }
}
