package com.truedigital.features.truecloudv3.widget.photoeditor

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorInt
import com.truedigital.features.truecloudv3.R

internal class DegreeRulerView : View {

    companion object {
        private const val DEFAULT_INDICATOR_INTERVAL_WIDTH = 8
        private const val DEFAULT_MAX_VALUE = 0
        private const val DEFAULT_MIN_VALUE = 100
    }

    var indicatorIntervalWidth = DEFAULT_INDICATOR_INTERVAL_WIDTH
        private set

    var minValue = DEFAULT_MIN_VALUE
        private set

    var maxValue = DEFAULT_MAX_VALUE
        private set

    private val barBound = RectF(0f, 0f, 0f, 0f)
    private var viewHeight = 0
    private var indicatorPaint: Paint? = null

    @ColorInt
    private var indicatorColor = Color.WHITE
    private var indicatorWidth = 0
    private var indicatorHeight = 0

    constructor(context: Context) : super(context) {
        parseAttr(null)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        parseAttr(attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        parseAttr(attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        parseAttr(attrs)
    }

    override fun onDraw(canvas: Canvas) {
        for (value in 1 until maxValue - minValue) {
            if (value % 2 == 0) {
                drawLongIndicator(canvas, value)
            }
        }
        super.onDraw(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        viewHeight = MeasureSpec.getSize(heightMeasureSpec)
        val viewWidth = (maxValue - minValue - 1) * indicatorIntervalWidth
        setMeasuredDimension(viewWidth, viewHeight)
    }

    @ColorInt
    fun getIndicatorColor(): Int {
        return indicatorColor
    }

    fun setIndicatorColor(@ColorInt color: Int) {
        indicatorColor = color
        refreshPaint()
    }

    fun setValueRange(minValue: Int, maxValue: Int) {
        this.minValue = minValue
        this.maxValue = maxValue + 1
        invalidate()
    }

    fun setIndicatorIntervalDistance(indicatorIntervalPx: Int) {
        require(indicatorIntervalPx > 0) { "Interval cannot be negative or zero." }
        indicatorIntervalWidth = indicatorIntervalPx
        invalidate()
    }

    private fun parseAttr(attributeSet: AttributeSet?) {
        indicatorWidth = 4.dpToPx()
        indicatorHeight = 3.dpToPx()
        if (attributeSet != null) {
            val a = context.theme.obtainStyledAttributes(
                attributeSet,
                R.styleable.DegreeRulerView,
                0,
                0
            )
            try {
                if (a.hasValue(R.styleable.DegreeRulerView_drv_indicator_color)) {
                    indicatorColor = a.getColor(
                        R.styleable.DegreeRulerView_drv_indicator_color,
                        Color.WHITE
                    )
                }
                if (a.hasValue(R.styleable.DegreeRulerView_drv_indicator_interval)) {
                    indicatorIntervalWidth = a.getDimensionPixelSize(
                        R.styleable.DegreeRulerView_drv_indicator_interval,
                        DEFAULT_INDICATOR_INTERVAL_WIDTH,
                    )
                }
                if (a.hasValue(R.styleable.DegreeRulerView_drv_min_value)) {
                    minValue =
                        a.getInteger(R.styleable.DegreeRulerView_drv_min_value, DEFAULT_MIN_VALUE)
                }
                if (a.hasValue(R.styleable.DegreeRulerView_drv_max_value)) {
                    maxValue =
                        a.getInteger(R.styleable.DegreeRulerView_drv_max_value, DEFAULT_MAX_VALUE)
                }
                setValueRange(minValue, maxValue)
            } finally {
                a.recycle()
            }
        }
        refreshPaint()
    }

    private fun refreshPaint() {
        indicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = indicatorColor
            style = Paint.Style.FILL
        }
        invalidate()
        requestLayout()
    }

    private fun drawLongIndicator(
        canvas: Canvas,
        value: Int
    ) {
        val x = (indicatorIntervalWidth * value).toFloat()
        val y = viewHeight / 2F
        barBound.set(
            x,
            y - (indicatorHeight / 2F),
            x + indicatorWidth,
            y + indicatorHeight,
        )
        indicatorPaint?.let {
            canvas.drawRoundRect(
                barBound,
                barBound.width() / 2F,
                barBound.width() / 2F,
                it
            )
        }
    }

    private fun Int.dpToPx(): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), resources.displayMetrics
        ).toInt()
    }
}
