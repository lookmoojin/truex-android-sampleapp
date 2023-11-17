package com.truedigital.features.truecloudv3.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.truedigital.features.truecloudv3.databinding.TrueCloudv3HeaderEditContactViewBinding
import com.truedigital.foundation.extension.onClick

class TrueCloudV3EditContactHeaderTitleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val binding: TrueCloudv3HeaderEditContactViewBinding = TrueCloudv3HeaderEditContactViewBinding.inflate(
        LayoutInflater.from(context),
        this,
        false
    )

    init {
        addView(binding.root)
    }

    fun setOnClickClose(onBack: () -> Unit) {
        binding.trueCloudCloseImageView.onClick {
            onBack.invoke()
        }
    }

    fun setOnClickSave(onSave: () -> Unit) {
        binding.trueCloudSaveTextView.onClick {
            onSave.invoke()
        }
    }
}
