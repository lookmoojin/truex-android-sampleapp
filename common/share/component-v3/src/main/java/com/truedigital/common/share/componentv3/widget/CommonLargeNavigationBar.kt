package com.truedigital.common.share.componentv3.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import androidx.appcompat.widget.LinearLayoutCompat
import com.truedigital.common.share.componentv3.R
import com.truedigital.common.share.componentv3.common.IconGravity
import com.truedigital.common.share.componentv3.databinding.CommonNavigationBarLargeBinding

class CommonLargeNavigationBar : FrameLayout {

    private var largeTitle = ""

    private val binding by lazy {
        CommonNavigationBarLargeBinding.inflate(LayoutInflater.from(context))
    }

    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(attrs)
    }

    private fun init(attrs: AttributeSet?) {
        setupStyleable(attrs)
        setupView()
    }

    private fun setupView() {
        addView(binding.root)
        binding.titleAppCompatTextView.text = largeTitle
    }

    private fun setupStyleable(attrs: AttributeSet?) {
        attrs?.let { _attrs ->
            val typedArray =
                context?.obtainStyledAttributes(_attrs, R.styleable.CommonLargeNavigationBar)
            largeTitle = typedArray?.getString(
                R.styleable.CommonLargeNavigationBar_cln_title
            ).orEmpty()
            typedArray?.recycle()
        }
    }

    private fun viewSpace(): View {
        return View(context).apply {
            layoutParams = LinearLayoutCompat.LayoutParams(0, 1, 1f)
        }
    }

    fun addIconView(listIconView: List<Pair<View, IconGravity>>) {
        val countIconLeft = listIconView
            .filter { (_, iconGravity) ->
                iconGravity == IconGravity.LEFT
            }.size
        listIconView
            .sortedBy { IconGravity.LEFT }
            .forEachIndexed { index, (iconView, _) ->
                if (index == countIconLeft) binding.iconLinearLayoutCompat.addView(viewSpace())
                binding.iconLinearLayoutCompat.addView(iconView)
            }
    }

    fun setTitle(title: String) {
        largeTitle = title
        binding.titleAppCompatTextView.text = largeTitle
    }
}
