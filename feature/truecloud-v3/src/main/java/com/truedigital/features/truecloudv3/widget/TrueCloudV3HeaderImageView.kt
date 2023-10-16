package com.truedigital.features.truecloudv3.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.tdg.truecloud.databinding.TrueCloudv3HeaderImageViewBinding
import com.truedigital.foundation.extension.onClick

class TrueCloudV3HeaderImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val binding: TrueCloudv3HeaderImageViewBinding =
        TrueCloudv3HeaderImageViewBinding.inflate(
            LayoutInflater.from(context),
            this,
            false
        )

    init {
        addView(binding.root)
    }

    fun setTitle(title: String) {
        binding.trueCloudTextViewTitle.text = title
    }

    fun setOnClickBack(onBack: () -> Unit) {
        binding.trueCloudImageViewBack.onClick {
            onBack.invoke()
        }
    }

    fun setOnClickMoreOption(onMore: () -> Unit) {
        binding.trueCloudImageViewerMore.onClick {
            onMore.invoke()
        }
    }
}
