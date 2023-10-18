package com.truedigital.foundation.extension

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Looper
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.inputmethod.InputMethodManager
import androidx.annotation.CheckResult
import androidx.annotation.RestrictTo
import androidx.core.content.ContextCompat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.disable() {
    this.isEnabled = false
}

fun View.isRevealOnScreen(): Boolean {
    val actualPosition = Rect()
    this.getGlobalVisibleRect(actualPosition)

    val displayMetrics = DisplayMetrics()
    val windowManager = this.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    windowManager.defaultDisplay.getMetrics(displayMetrics)
    val height = displayMetrics.heightPixels
    val width = displayMetrics.widthPixels

    val screen = Rect(0, 0, width, height)

    return actualPosition.intersect(screen)
}

fun View.onClick(doSomething: () -> Unit) {
    addRipple()
    setOnClickListener {
        isClickable = false
        postDelayed({ isClickable = true }, 1500)
        doSomething()
    }
}

fun View.onClickNoDelay(doSomething: () -> Unit) {
    addRipple()
    setOnClickListener {
        doSomething()
    }
}

private fun View.addRipple() = with(TypedValue()) {
    context.theme.resolveAttribute(android.R.attr.selectableItemBackground, this, true)
    foreground = ContextCompat.getDrawable(context, resourceId)
}

fun View.getBitmap(): Bitmap? {
    return try {
        val bitmap =
            Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        this.draw(canvas)
        bitmap
    } catch (e: Exception) {
        null
    }
}

fun View.clearKeyboardFocus() {
    clearFocus()
    val inputMethodManager =
        context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
}

fun View.visibleAnimation(visibility: Int, duration: Long = 200) {
    val alphaAnimation =
        if (visibility == View.VISIBLE) AlphaAnimation(0f, 1f) else AlphaAnimation(1f, 0f)
    alphaAnimation.duration = duration
    alphaAnimation.fillAfter = true
    startAnimation(alphaAnimation)
}

fun View.visibleOrGone(show: Boolean) {
    this.visibility = if (show) View.VISIBLE else View.GONE
}

fun View.visibleOrInvisible(show: Boolean) {
    this.visibility = if (show) View.VISIBLE else View.INVISIBLE
}

/**
 * Ref. from this lib -> https://github.com/ReactiveCircus/FlowBinding
 * */
@CheckResult
@OptIn(ExperimentalCoroutinesApi::class)
fun View.clickAsFlow(): Flow<Unit> = callbackFlow {
    checkMainThread()
    val listener = View.OnClickListener {
        trySend(Unit)
    }
    setOnClickListener(listener)
    awaitClose { setOnClickListener(null) }
}.conflate()

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
fun checkMainThread() {
    check(Looper.myLooper() == Looper.getMainLooper()) {
        "Expected to be called on the main thread but was " + Thread.currentThread().name
    }
}
