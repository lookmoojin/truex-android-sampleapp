package com.truedigital.component.widget.progress

import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.ProgressBar

class ProgressBarAnimation : Animation {

    private var mProgressBar: ProgressBar
    private var mStepDuration: Long
    private var mTo: Int = 0
    private var mFrom: Int = 0

    constructor(progressBar: ProgressBar, fullDuration: Long) {
        mProgressBar = progressBar
        mStepDuration = fullDuration / progressBar.max
    }

    fun setProgress(progress: Int) {
        var progress = progress
        if (progress < 0) {
            progress = 0
        }

        if (progress > mProgressBar.max) {
            progress = mProgressBar.max
        }

        mTo = progress

        mFrom = mProgressBar.progress
        duration = Math.abs(mTo - mFrom) * mStepDuration
        mProgressBar.startAnimation(this)
    }

    override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
        val value = mFrom + (mTo - mFrom) * interpolatedTime
        mProgressBar.progress = value.toInt()
    }
}
