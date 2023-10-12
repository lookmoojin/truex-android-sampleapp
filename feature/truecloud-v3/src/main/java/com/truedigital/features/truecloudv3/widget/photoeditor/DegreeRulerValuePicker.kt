package com.truedigital.features.truecloudv3.widget.photoeditor

import android.content.Context
import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.widget.photoeditor.ObservableHorizontalScrollView.ScrollChangedListener

class DegreeRulerValuePicker : FrameLayout, ScrollChangedListener {

    interface RulerValuePickerListener {
        fun onValueChange(selectedValue: Int)
        fun onIntermediateValueChange(selectedValue: Int)
    }

    companion object {
        private const val DEFAULT_INDICATOR_INTERVAL_WIDTH = 8
        private const val DEFAULT_MAX_VALUE = 0
        private const val DEFAULT_MIN_VALUE = 100
        private const val DEFAULT_SCROLL_VALUE = 0
        private const val DEFAULT_SCROLL_DELAY = 400L
        private const val DIVIDER_HALF = 2
    }

    private var listener: RulerValuePickerListener? = null
    private lateinit var leftSpacer: View
    private lateinit var rightSpacer: View
    private lateinit var degreeRulerView: DegreeRulerView
    private lateinit var horizontalScrollView: ObservableHorizontalScrollView

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        init(attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs)
    }

    override fun onLayout(isChanged: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(isChanged, left, top, right, bottom)
        if (isChanged) {
            val width = width

            val leftParams = leftSpacer.layoutParams
            leftParams.width = width / DIVIDER_HALF
            leftSpacer.layoutParams = leftParams

            val rightParams = rightSpacer.layoutParams
            rightParams.width = width / DIVIDER_HALF
            rightSpacer.layoutParams = rightParams
            invalidate()
        }
    }

    override fun onScrollChanged() {
        listener?.onIntermediateValueChange(getCurrentValue())
    }

    override fun onScrollStopped() {
        makeOffsetCorrection(degreeRulerView.indicatorIntervalWidth)
        listener?.onValueChange(getCurrentValue())
    }

    override fun onSaveInstanceState(): Parcelable? {
        val superState = super.onSaveInstanceState()
        val ss = SavedState(superState)
        ss.value = getCurrentValue()
        return ss
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        val savedState = state as SavedState
        super.onRestoreInstanceState(savedState.superState)
        selectValue(savedState.value)
    }

    @ColorInt
    fun getIndicatorColor(): Int {
        return degreeRulerView.getIndicatorColor()
    }

    fun setIndicatorColor(@ColorInt color: Int) {
        degreeRulerView.setIndicatorColor(color)
    }

    fun setIndicatorColorRes(@ColorRes color: Int) {
        setIndicatorColor(ContextCompat.getColor(context, color))
    }

    fun getMinValue(): Int {
        return degreeRulerView.minValue
    }

    fun getMaxValue(): Int {
        return degreeRulerView.maxValue
    }

    fun setMinMaxValue(minValue: Int, maxValue: Int) {
        degreeRulerView.setValueRange(minValue, maxValue)
        invalidate()
        selectValue(minValue)
    }

    fun getIndicatorIntervalWidth(): Int {
        return degreeRulerView.indicatorIntervalWidth
    }

    fun setIndicatorIntervalDistance(indicatorIntervalPx: Int) {
        degreeRulerView.setIndicatorIntervalDistance(indicatorIntervalPx)
    }

    fun setValuePickerListener(listener: RulerValuePickerListener) {
        this.listener = listener
    }

    fun selectValue(value: Int) {
        horizontalScrollView.postDelayed({
            val valuesToScroll = if (value < degreeRulerView.minValue) {
                DEFAULT_SCROLL_VALUE
            } else if (value > degreeRulerView.maxValue) {
                degreeRulerView.maxValue - degreeRulerView.minValue
            } else {
                value - degreeRulerView.minValue
            }
            horizontalScrollView.smoothScrollTo(
                valuesToScroll * degreeRulerView.indicatorIntervalWidth, DEFAULT_SCROLL_VALUE
            )
        }, DEFAULT_SCROLL_DELAY)
    }

    fun getCurrentValue(): Int {
        val absoluteValue = horizontalScrollView.scrollX / degreeRulerView.indicatorIntervalWidth
        val value = degreeRulerView.minValue + absoluteValue
        return if (value > degreeRulerView.maxValue) {
            degreeRulerView.maxValue
        } else if (value < degreeRulerView.minValue) {
            degreeRulerView.minValue
        } else {
            value
        }
    }

    private fun init(attributeSet: AttributeSet?) {
        addChildViews()
        if (attributeSet != null) {
            val typedArray = context.theme.obtainStyledAttributes(
                attributeSet,
                R.styleable.DegreeRulerValuePicker,
                0,
                0
            )
            try {
                if (typedArray.hasValue(R.styleable.DegreeRulerValuePicker_drvp_indicator_color)) {
                    setIndicatorColor(
                        typedArray.getColor(
                            R.styleable.DegreeRulerValuePicker_drvp_indicator_color,
                            Color.WHITE,
                        )
                    )
                }
                if (typedArray.hasValue(R.styleable.DegreeRulerValuePicker_drvp_indicator_interval)) {
                    setIndicatorIntervalDistance(
                        typedArray.getDimensionPixelSize(
                            R.styleable.DegreeRulerValuePicker_drvp_indicator_interval,
                            DEFAULT_INDICATOR_INTERVAL_WIDTH,
                        )
                    )
                }
                if (typedArray.hasValue(R.styleable.DegreeRulerValuePicker_drvp_min_value) ||
                    typedArray.hasValue(R.styleable.DegreeRulerValuePicker_drvp_max_value)
                ) {
                    setMinMaxValue(
                        typedArray.getInteger(
                            R.styleable.DegreeRulerValuePicker_drvp_min_value,
                            DEFAULT_MIN_VALUE,
                        ),
                        typedArray.getInteger(
                            R.styleable.DegreeRulerValuePicker_drvp_max_value,
                            DEFAULT_MAX_VALUE,
                        )
                    )
                }
            } finally {
                typedArray.recycle()
            }
        }
    }

    private fun addChildViews() {
        horizontalScrollView = ObservableHorizontalScrollView(context, this)
        horizontalScrollView.isHorizontalScrollBarEnabled = false
        val overlay = View(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            background = ContextCompat.getDrawable(context, R.drawable.bg_rotate_degree_overlay)
        }
        val rulerContainer = LinearLayout(context)

        leftSpacer = View(context)
        rulerContainer.addView(leftSpacer)

        degreeRulerView = DegreeRulerView(context)
        rulerContainer.addView(degreeRulerView)

        rightSpacer = View(context)
        rulerContainer.addView(rightSpacer)

        horizontalScrollView.removeAllViews()
        horizontalScrollView.addView(rulerContainer)

        removeAllViews()
        addView(horizontalScrollView)
        addView(overlay)
    }

    private fun makeOffsetCorrection(indicatorInterval: Int) {
        val offsetValue = horizontalScrollView.scrollX % indicatorInterval
        if (offsetValue < indicatorInterval / 2) {
            horizontalScrollView.scrollBy(-offsetValue, 0)
        } else {
            horizontalScrollView.scrollBy(indicatorInterval - offsetValue, 0)
        }
    }

    internal class SavedState : BaseSavedState {
        var value = 0

        internal constructor(superState: Parcelable?) : super(superState)
        private constructor(`in`: Parcel) : super(`in`) {
            value = `in`.readInt()
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeInt(value)
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(`in`: Parcel): SavedState {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState> {
                    return newArray(size)
                }
            }
        }
    }
}
