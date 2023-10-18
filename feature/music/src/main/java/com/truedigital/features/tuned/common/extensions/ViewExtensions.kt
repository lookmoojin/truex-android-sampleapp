package com.truedigital.features.tuned.common.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Activity
import android.app.Dialog
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.DecelerateInterpolator
import androidx.annotation.IdRes
import com.wang.avi.AVLoadingIndicatorView

fun <T : View> Activity.find(@IdRes res: Int): T? = findViewById(res)

fun <T : View> View.find(@IdRes res: Int): T? = findViewById(res)

fun <T : View> View.find(tag: String): T? = findViewWithTag(tag)

fun <T : View> Dialog.find(@IdRes res: Int): T? = findViewById(res)

// solves the issue where visibility = gone not working
fun AVLoadingIndicatorView.visibilityGone() {
    Handler(Looper.getMainLooper()).post { visibility = View.GONE }
}

// Animated view visibility control
fun View.fadeIn(duration: Long = 500, fromAlpha: Float = 0f) {
    val alpha = ObjectAnimator.ofFloat(this, "alpha", fromAlpha, 1f)
    alpha.duration = duration
    alpha.interpolator = DecelerateInterpolator()
    alpha.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator) {
            visibility = View.VISIBLE
        }
    })
    alpha.setAutoCancel(true)
    alpha.start()
}

fun View.fadeOut(duration: Long = 500, toAlpha: Float = 0f) {
    val alpha = ObjectAnimator.ofFloat(this, "alpha", alpha, toAlpha)
    alpha.duration = duration
    alpha.interpolator = DecelerateInterpolator()
    alpha.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
            if (visibility == View.VISIBLE) {
                if (toAlpha == 0f) {
                    visibility = View.INVISIBLE
                    setAlpha(1f)
                }
            } else {
                setAlpha(1f)
            }
        }
    })
    alpha.setAutoCancel(true)
    alpha.start()
}

fun View.onLayoutFinish(invoke: () -> Unit) {
    val globalLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            invoke()
        }
    }
    viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)
}
