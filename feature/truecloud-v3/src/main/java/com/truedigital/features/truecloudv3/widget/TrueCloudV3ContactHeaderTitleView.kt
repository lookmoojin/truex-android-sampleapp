package com.truedigital.features.truecloudv3.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.truedigital.features.truecloudv3.databinding.TrueCloudv3ContactHeaderTitleViewBinding
import com.truedigital.foundation.extension.onClick

class TrueCloudV3ContactHeaderTitleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val binding: TrueCloudv3ContactHeaderTitleViewBinding = TrueCloudv3ContactHeaderTitleViewBinding.inflate(
        LayoutInflater.from(context),
        this,
        false
    )

    init {
        addView(binding.root)
    }

    fun setTitle(title: String) {
        binding.trueCloudTitleTextView.text = title
    }

    fun setOnClickBack(onBack: () -> Unit) {
        binding.trueCloudBackImageView.onClick {
            onBack.invoke()
        }
    }

    fun setOnClickSync(onSync: () -> Unit) {
        binding.trueCloudUploadImageView.onClick {
            onSync.invoke()
        }
    }

    fun setOnClickMoreOption(onMore: () -> Unit) {
        binding.trueCloudMoreImageView.onClick {
            onMore.invoke()
        }
    }
}
