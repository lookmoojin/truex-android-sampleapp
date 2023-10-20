package com.truedigital.features.truecloudv3.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.truedigital.features.truecloudv3.databinding.TrueCloudv3TrashEmptyViewBinding

class TrueCloudV3TrashEmptyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val binding: TrueCloudv3TrashEmptyViewBinding =
        TrueCloudv3TrashEmptyViewBinding.inflate(
            LayoutInflater.from(context),
            this,
            false
        )

    init {
        addView(binding.root)
    }
}
