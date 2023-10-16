package com.truedigital.component.widget.error

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.google.firebase.perf.metrics.resource.ResourceType
import com.truedigital.component.R
import com.truedigital.component.databinding.ViewErrorBinding
import com.truedigital.core.extensions.ifNotNullOrEmpty
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.visible

class ErrorViewWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val binding: ViewErrorBinding by lazy {
        ViewErrorBinding.inflate(LayoutInflater.from(context), this, true)
    }

    fun setupView(
        @DrawableRes drawableImage: Int = R.drawable.maintenance_white,
        title: String? = null,
        detail: String? = null,
        @ResourceType titleStyle: Int = R.style.TrueID_Header2_Bold_DarkGrey,
        @ResourceType detailStyle: Int = R.style.TrueID_Small_DarkGrey,
        @ColorRes backgroundColor: Int = android.R.color.white
    ) {

        binding.errorImageView.setImageResource(drawableImage)
        title?.ifNotNullOrEmpty {
            binding.errorTitleTextView.text = it
            binding.errorTitleTextView.setTextAppearance(titleStyle)
            binding.errorTitleTextView.visible()
        } ?: binding.errorTitleTextView.gone()
        detail?.ifNotNullOrEmpty {
            binding.errorDetailTextView.text = it
            binding.errorDetailTextView.setTextAppearance(detailStyle)
            binding.errorDetailTextView.visible()
        } ?: binding.errorDetailTextView.gone()
        binding.errorRootView.setBackgroundColor(ContextCompat.getColor(context, backgroundColor))
    }
}
