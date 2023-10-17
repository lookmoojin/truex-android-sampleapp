package com.truedigital.features.tuned.presentation.common

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.truedigital.features.tuned.R

class HorizontalSeparator @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    View(context, attrs) {
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(widthMeasureSpec, resolveSize(1, heightMeasureSpec))
    }

    init {
        setBackgroundResource(R.color.line_separator)
    }
}
