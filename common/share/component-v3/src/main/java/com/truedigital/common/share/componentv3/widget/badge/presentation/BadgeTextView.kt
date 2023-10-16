package com.truedigital.common.share.componentv3.widget.badge.presentation

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.shapes.RoundRectShape
import android.graphics.drawable.shapes.Shape
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.res.use
import com.truedigital.common.share.componentv3.R
import kotlin.math.max
import kotlin.math.min

class BadgeTextView : AppCompatTextView {

    private var backgroundBadgeColor = 0
    private var previousTextLength = 0
    private var maxBadgeNumber = DEFAULT_MAX_BADGE_NUMBER

    companion object {
        const val DEFAULT_MAX_BADGE_NUMBER = 99
        const val DEFAULT_ONE_DIGIT_PADDING = 6F
        const val DEFAULT_MORE_DIGIT_VERTICAL_PADDING = 0F
        const val DEFAULT_MORE_DIGIT_HORIZONTAL_PADDING = 4F
    }

    private var badgeNumber: Int = 0
        set(value) {
            field = value
            visibility = if (value > 0) View.VISIBLE else View.INVISIBLE
            text = if (value > maxBadgeNumber) {
                String.format("%d+", maxBadgeNumber)
            } else {
                value.toString()
            }
        }

    constructor(context: Context) : super(context) {
        initView(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAtt: Int) : super(
        context,
        attrs,
        defStyleAtt
    ) {
        initView(attrs)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val currentTextLength = text.length
        if (currentTextLength != previousTextLength) {
            refreshBackground()
            previousTextLength = currentTextLength
        }
    }

    private fun initView(attrs: AttributeSet?) {
        attrs?.let { attributeSet ->
            context.obtainStyledAttributes(attributeSet, R.styleable.BadgeTextView).use { array ->
                backgroundBadgeColor = array.getColor(R.styleable.BadgeTextView_backgroundColor, 0)
                maxBadgeNumber = array.getInt(
                    R.styleable.BadgeTextView_maxBadgeNumber,
                    DEFAULT_MAX_BADGE_NUMBER
                )
            }
        }
    }

    private fun setBadgePadding(verticalPadding: Float, horizontalPadding: Float) {
        setPadding(0, 0, 0, 0)
        val paddingStart = max(paddingStart, dp2px(horizontalPadding))
        val paddingTop = max(paddingTop, dp2px(verticalPadding))
        val paddingEnd = max(paddingEnd, dp2px(horizontalPadding))
        val paddingBottom = max(paddingBottom, dp2px(verticalPadding))
        setPadding(paddingStart, paddingTop, paddingEnd, paddingBottom)
    }

    private fun dp2px(dpValue: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dpValue,
            resources.displayMetrics
        ).toInt()
    }

    private fun refreshBackground() {
        val text = text ?: return
        val shape = if (text.length == 1) {
            CircleShape()
        } else {
            val radius = (height / 2).toFloat()
            val outerRadii =
                floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
            RoundRectShape(outerRadii, null, null)
        }

        background = StrokeShapeDrawable(shape, backgroundBadgeColor)
    }

    fun setPaddingByDigit() {
        val text = text ?: return
        if (text.length == 1) {
            setBadgePadding(DEFAULT_ONE_DIGIT_PADDING, DEFAULT_ONE_DIGIT_PADDING)
        } else {
            setBadgePadding(
                DEFAULT_MORE_DIGIT_VERTICAL_PADDING,
                DEFAULT_MORE_DIGIT_HORIZONTAL_PADDING
            )
        }
    }

    inner class CircleShape : OvalShape() {

        override fun draw(canvas: Canvas, paint: Paint) {
            val width = (this@BadgeTextView.width / 2).toFloat()
            val height = (this@BadgeTextView.height / 2).toFloat()
            canvas.drawCircle(width, height, min(width, height), paint)
        }
    }

    inner class StrokeShapeDrawable(
        shape: Shape?,
        fillColor: Int
    ) : ShapeDrawable(shape) {
        private val fillPaint: Paint = Paint(this.paint)
        private val strokePaint: Paint

        init {
            fillPaint.color = fillColor
            strokePaint = Paint(fillPaint)
        }

        override fun onDraw(shape: Shape, canvas: Canvas, paint: Paint) {
            shape.resize(
                canvas.clipBounds.right.toFloat(),
                canvas.clipBounds.bottom.toFloat()
            )
            shape.draw(canvas, fillPaint)
        }
    }
}
