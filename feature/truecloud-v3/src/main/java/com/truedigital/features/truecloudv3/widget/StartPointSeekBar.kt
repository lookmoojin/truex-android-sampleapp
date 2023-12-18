package com.truedigital.features.truecloudv3.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import com.truedigital.features.truecloudv3.R

/**
 * Created by vashisthg on 01/04/14.
 */
class StartPointSeekBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var absoluteMinValue: Double
    private var absoluteMaxValue: Double
    private val thumbImage: Bitmap
    private val thumbPressedImage: Bitmap
    private val defaultRangeColor: Int
    private val defaultBackgroundColor: Int
    private val thumbWidth: Float
    private val thumbHalfWidth: Float
    private val thumbHalfHeight: Float
    private val lineHeight: Float
    private val padding: Float
    private val scaledTouchSlop: Int
    private var isDragging = false
    private var isThumbPressed = false
    private var normalizedThumbValue = 0.0
    private val notifyWhileDragging = true
    private var listener: OnSeekBarChangeListener? = null

    /**
     * Callback listener interface to notify about changed range values.
     *
     */
    interface OnSeekBarChangeListener {
        fun onOnSeekBarValueChange(
            bar: StartPointSeekBar?,
            value: Double
        )
    }

    /**
     * Registers given listener callback to notify about changed selected
     * values.
     *
     * @param listener The listener to notify about changed selected values.
     */
    fun setOnSeekBarChangeListener(
        listener: OnSeekBarChangeListener?
    ) {
        this.listener = listener
    }

    private var mDownMotionX = 0f
    private var mActivePointerId = INVALID_POINTER_ID

    init {

        // Attribute initialization
        val a = context.obtainStyledAttributes(
            attrs, R.styleable.StartPointSeekBar,
            defStyleAttr, 0
        )
        var thumbImageDrawable = a.getDrawable(R.styleable.StartPointSeekBar_thumbDrawable)
        if (thumbImageDrawable == null) {
            thumbImageDrawable = resources.getDrawable(R.drawable.seek_thumb_normal)
        }
        thumbImage = (thumbImageDrawable as BitmapDrawable?)!!.bitmap
        var thumbImagePressedDrawable =
            a.getDrawable(R.styleable.StartPointSeekBar_thumbPressedDrawable)
        if (thumbImagePressedDrawable == null) {
            thumbImagePressedDrawable = resources.getDrawable(R.drawable.seek_thumb_pressed)
        }
        thumbPressedImage = (thumbImagePressedDrawable as BitmapDrawable?)!!.bitmap
        absoluteMinValue =
            a.getFloat(R.styleable.StartPointSeekBar_minValue, DEFAULT_MIN_VALUE).toDouble()
        absoluteMaxValue =
            a.getFloat(R.styleable.StartPointSeekBar_maxValue, DEFAULT_MAX_VALUE).toDouble()
        defaultBackgroundColor = a.getColor(
            R.styleable.StartPointSeekBar_defaultBackgroundColor,
            DEFAULT_BACKGROUND_COLOR
        )
        defaultRangeColor = a.getColor(
            R.styleable.StartPointSeekBar_defaultBackgroundRangeColor,
            DEFAULT_RANGE_COLOR
        )
        a.recycle()
        thumbWidth = thumbImage.width.toFloat()
        thumbHalfWidth = 0.5f * thumbWidth
        thumbHalfHeight = 0.5f * thumbImage.height
        lineHeight = 0.3f * thumbHalfHeight
        padding = thumbHalfWidth
        isFocusable = true
        isFocusableInTouchMode = true
        scaledTouchSlop = ViewConfiguration.get(getContext())
            .scaledTouchSlop
    }

    fun setAbsoluteMinMaxValue(absoluteMinValue: Double, absoluteMaxValue: Double) {
        this.absoluteMinValue = absoluteMinValue
        this.absoluteMaxValue = absoluteMaxValue
    }

    @Synchronized
    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {
        var width = 200
        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(widthMeasureSpec)) {
            width = MeasureSpec.getSize(widthMeasureSpec)
        }
        var height = thumbImage.height
        if (MeasureSpec.UNSPECIFIED != MeasureSpec.getMode(heightMeasureSpec)) {
            height = Math.min(height, MeasureSpec.getSize(heightMeasureSpec))
        }
        setMeasuredDimension(width, height)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) return false
        val pointerIndex: Int
        val action = event.action
        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                // Remember where the motion event started
                mActivePointerId = event.getPointerId(event.pointerCount - 1)
                pointerIndex = event.findPointerIndex(mActivePointerId)
                mDownMotionX = event.getX(pointerIndex)
                isThumbPressed = evalPressedThumb(mDownMotionX)

                // Only handle thumb presses.
                if (!isThumbPressed) return super.onTouchEvent(event)
                isPressed = true
                invalidate()
                onStartTrackingTouch()
                trackTouchEvent(event)
                attemptClaimDrag()
            }

            MotionEvent.ACTION_MOVE -> if (isThumbPressed) {
                if (isDragging) {
                    trackTouchEvent(event)
                } else {
                    // Scroll to follow the motion event
                    pointerIndex = event.findPointerIndex(mActivePointerId)
                    val x = event.getX(pointerIndex)
                    if (Math.abs(x - mDownMotionX) > scaledTouchSlop) {
                        isPressed = true
                        invalidate()
                        onStartTrackingTouch()
                        trackTouchEvent(event)
                        attemptClaimDrag()
                    }
                }
                if (notifyWhileDragging && listener != null) {
                    listener!!.onOnSeekBarValueChange(this, normalizedToValue(normalizedThumbValue))
                }
            }

            MotionEvent.ACTION_UP -> {
                if (isDragging) {
                    trackTouchEvent(event)
                    onStopTrackingTouch()
                    isPressed = false
                } else {
                    // Touch up when we never crossed the touch slop threshold
                    // should be interpreted as a tap-seek to that location.
                    onStartTrackingTouch()
                    trackTouchEvent(event)
                    onStopTrackingTouch()
                }
                isThumbPressed = false
                invalidate()
                if (listener != null) {
                    listener!!.onOnSeekBarValueChange(this, normalizedToValue(normalizedThumbValue))
                }
            }

            MotionEvent.ACTION_POINTER_DOWN -> {
                val index = event.pointerCount - 1
                // final int index = ev.getActionIndex();
                mDownMotionX = event.getX(index)
                mActivePointerId = event.getPointerId(index)
                invalidate()
            }

            MotionEvent.ACTION_POINTER_UP -> {
                onSecondaryPointerUp(event)
                invalidate()
            }

            MotionEvent.ACTION_CANCEL -> {
                if (isDragging) {
                    onStopTrackingTouch()
                    isPressed = false
                }
                invalidate() // see above explanation
            }
        }
        return true
    }

    private fun onSecondaryPointerUp(ev: MotionEvent) {
        val pointerIndex = ev.action and ACTION_POINTER_INDEX_MASK shr ACTION_POINTER_INDEX_SHIFT
        val pointerId = ev.getPointerId(pointerIndex)
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose
            // a new active pointer and adjust accordingly.
            // TODO: Make this decision more intelligent.
            val newPointerIndex = if (pointerIndex == 0) 1 else 0
            mDownMotionX = ev.getX(newPointerIndex)
            mActivePointerId = ev.getPointerId(newPointerIndex)
        }
    }

    /**
     * Tries to claim the user's drag motion, and requests disallowing any
     * ancestors from stealing events in the drag.
     */
    private fun attemptClaimDrag() {
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(true)
        }
    }

    private fun trackTouchEvent(event: MotionEvent) {
        val pointerIndex = event.findPointerIndex(mActivePointerId)
        val x = event.getX(pointerIndex)
        setNormalizedValue(screenToNormalized(x))
    }

    /**
     * Converts screen space x-coordinates into normalized values.
     *
     * @param screenCoord The x-coordinate in screen space to convert.
     * @return The normalized value.
     */
    private fun screenToNormalized(screenCoord: Float): Double {
        val width = width
        return if (width <= 2 * padding) {
            // prevent division by zero, simply return 0.
            0.0
        } else {
            val result = ((screenCoord - padding) / (width - 2 * padding)).toDouble()
            Math.min(1.0, Math.max(0.0, result))
        }
    }

    /**
     * Converts a normalized value to a Number object in the value space between
     * absolute minimum and maximum.
     *
     * @param normalized
     * @return
     */
    private fun normalizedToValue(normalized: Double): Double {
        return absoluteMinValue + normalized * (absoluteMaxValue - absoluteMinValue)
    }
    /**
     * Converts the given Number value to a normalized double.
     *
     * @param value The Number value to normalize.
     * @return The normalized double.
     */
    private fun valueToNormalized(value: Double): Double {
        return if (0.0 == absoluteMaxValue - absoluteMinValue) {
            // prevent division by zero, simply return 0.
            0.0
        } else {
            (
                (value - absoluteMinValue) /
                    (absoluteMaxValue - absoluteMinValue)
                )
        }
    }

    /**
     * Sets normalized max value to value so that 0 <= normalized min value <=
     * value <= 1. The View will get invalidated when calling this method.
     *
     * @param value The new normalized max value to set.
     */
    private fun setNormalizedValue(value: Double) {
        normalizedThumbValue = Math.max(0.0, value)
        invalidate()
    }

    /**
     * Sets value of seekbar to the given value
     * @param value The new value to set
     */
    fun setProgress(value: Double) {
        val newThumbValue = valueToNormalized(value)
        require(!(newThumbValue > absoluteMaxValue || newThumbValue < absoluteMinValue)) { "Value should be in the middle of max and min value" }
        normalizedThumbValue = newThumbValue
        invalidate()
    }

    /**
     * This is called when the user has started touching this widget.
     */
    fun onStartTrackingTouch() {
        isDragging = true
    }

    /**
     * This is called when the user either releases his touch or the touch is
     * canceled.
     */
    fun onStopTrackingTouch() {
        isDragging = false
    }

    /**
     * Decides which (if any) thumb is touched by the given x-coordinate.
     *
     * @param touchX The x-coordinate of a touch event in screen space.
     * @return The pressed thumb or null if none has been touched.
     */
    private fun evalPressedThumb(touchX: Float): Boolean {
        return isInThumbRange(touchX, normalizedThumbValue)
    }

    /**
     * Decides if given x-coordinate in screen space needs to be interpreted as
     * "within" the normalized thumb x-coordinate.
     *
     * @param touchX               The x-coordinate in screen space to check.
     * @param normalizedThumbValue The normalized x-coordinate of the thumb to check.
     * @return true if x-coordinate is in thumb range, false otherwise.
     */
    private fun isInThumbRange(touchX: Float, normalizedThumbValue: Double): Boolean {
        return Math.abs(touchX - normalizedToScreen(normalizedThumbValue)) <= thumbHalfWidth
    }

    /**
     * Converts a normalized value into screen space.
     *
     * @param normalizedCoord The normalized value to convert.
     * @return The converted value in screen space.
     */
    private fun normalizedToScreen(normalizedCoord: Double): Float {
        return (padding + normalizedCoord * (width - 2 * padding)).toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // draw seek bar background line
        val rect = RectF(
            padding,
            0.5f * (height - lineHeight), width - padding,
            0.5f * (height + lineHeight)
        )
        paint.color = defaultBackgroundColor
        canvas.drawRect(rect, paint)

        // draw seek bar active range line
        if (normalizedToScreen(valueToNormalized(0.0)) < normalizedToScreen(normalizedThumbValue)) {
            Log.d(VIEW_LOG_TAG, "thumb: right")
            rect.left = normalizedToScreen(valueToNormalized(0.0))
            rect.right = normalizedToScreen(normalizedThumbValue)
        } else {
            Log.d(VIEW_LOG_TAG, "thumb: left")
            rect.right = normalizedToScreen(valueToNormalized(0.0))
            rect.left = normalizedToScreen(normalizedThumbValue)
        }
        paint.color = defaultRangeColor
        canvas.drawRect(rect, paint)
        drawThumb(
            normalizedToScreen(normalizedThumbValue),
            isThumbPressed, canvas
        )
        Log.d(VIEW_LOG_TAG, "thumb: " + normalizedToValue(normalizedThumbValue))
    }

    /**
     * Draws the "normal" resp. "pressed" thumb image on specified x-coordinate.
     *
     * @param screenCoord The x-coordinate in screen space where to draw the image.
     * @param pressed     Is the thumb currently in "pressed" state?
     * @param canvas      The canvas to draw upon.
     */
    private fun drawThumb(screenCoord: Float, pressed: Boolean, canvas: Canvas) {
        canvas.drawBitmap(
            if (pressed) thumbPressedImage else thumbImage,
            screenCoord -
                thumbHalfWidth,
            (0.5f * height - thumbHalfHeight), paint
        )
    }

    companion object {
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        private val DEFAULT_RANGE_COLOR = Color.argb(0xFF, 0x33, 0xB5, 0xE5)
        private const val DEFAULT_BACKGROUND_COLOR = Color.GRAY
        private const val DEFAULT_MIN_VALUE = -100f
        private const val DEFAULT_MAX_VALUE = +100f

        /**
         * An invalid pointer id.
         */
        const val INVALID_POINTER_ID = 255

        // Localized constants from MotionEvent for compatibility
        // with API < 8 "Froyo".
        const val ACTION_POINTER_UP = 0x6
        const val ACTION_POINTER_INDEX_MASK = 0x0000ff00
        const val ACTION_POINTER_INDEX_SHIFT = 8
    }
}
