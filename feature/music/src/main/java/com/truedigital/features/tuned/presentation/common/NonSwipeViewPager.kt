package com.truedigital.features.tuned.presentation.common

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class NonSwipeViewPager(context: Context, attrs: AttributeSet) : ViewPager(context, attrs) {

    var swipePagingEnabled: Boolean = false

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (this.swipePagingEnabled) {
            super.onTouchEvent(event)
        } else false
    }

    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        return if (this.swipePagingEnabled) {
            super.onInterceptTouchEvent(event)
        } else false
    }
}
