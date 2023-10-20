package com.truedigital.features.truecloudv3.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.databinding.TrueCloudv3DataEmptyViewBinding
import com.truedigital.core.extensions.ifNotNullOrEmpty
import com.truedigital.foundation.extension.gone

class TrueCloudV3DataEmptyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val binding: TrueCloudv3DataEmptyViewBinding = TrueCloudv3DataEmptyViewBinding.inflate(
        LayoutInflater.from(context),
        this,
        false
    )

    init {
        addView(binding.root)
    }

    fun setupView(
        drawableImage: Int = R.drawable.maintenance_white,
        detail: String? = null,
    ) {
        binding.emptyImageView.setImageResource(drawableImage)
        detail?.ifNotNullOrEmpty {
            binding.emptyHeaderTextView.text = it
        } ?: binding.emptyHeaderTextView.gone()
    }
}
