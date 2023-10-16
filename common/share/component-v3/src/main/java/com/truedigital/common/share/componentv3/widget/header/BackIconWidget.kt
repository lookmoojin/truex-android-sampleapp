package com.truedigital.common.share.componentv3.widget.header

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import com.truedigital.common.share.componentv3.R
import com.truedigital.common.share.componentv3.databinding.ViewBackIconBinding
import com.truedigital.foundation.extension.onClick

class BackIconWidget : FrameLayout {
    private val binding: ViewBackIconBinding by lazy {
        ViewBackIconBinding.inflate(LayoutInflater.from(context), this, false)
    }

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        addView(binding.root)
        setTheme(false)
    }

    fun setTheme(isDark: Boolean) {
        binding.backImageView.apply {
            if (isDark) {
                ImageViewCompat.setImageTintList(
                    this,
                    ColorStateList.valueOf(
                        ContextCompat.getColor(context, R.color.white)
                    )
                )
            } else {
                ImageViewCompat.setImageTintList(
                    this,
                    ColorStateList.valueOf(
                        ContextCompat.getColor(context, R.color.black)
                    )
                )
            }
        }
    }

    fun setOnClick(block: () -> Unit) {
        binding.backImageView.onClick {
            block.invoke()
        }
    }

    fun setContentDescription(contentDescription: String) {
        binding.backImageView.contentDescription = contentDescription
    }
}
