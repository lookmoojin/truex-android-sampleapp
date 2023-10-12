package com.truedigital.features.truecloudv3.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.truedigital.features.truecloudv3.databinding.TrueCloudv3HeaderTitleViewBinding
import com.truedigital.foundation.extension.onClick

class TrueCloudV3HeaderTitleView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val DISPLAY_LIST = 0
        private const val DISPLAY_GRID = 1
    }

    private val binding: TrueCloudv3HeaderTitleViewBinding =
        TrueCloudv3HeaderTitleViewBinding.inflate(
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

    fun setOnClickChangeLayout(onChangeLayout: () -> Unit) {
        binding.viewSwitcherChangeLayout.apply {
            onClick {
                displayedChild = when (displayedChild) {
                    DISPLAY_LIST -> DISPLAY_GRID
                    else -> DISPLAY_LIST
                }
                onChangeLayout.invoke()
            }
        }
    }

    fun setOnClickUpload(onUpload: () -> Unit) {
        binding.trueCloudImageViewUpload.onClick {
            onUpload.invoke()
        }
    }

    fun setOnClickMoreOption(onMore: () -> Unit) {
        binding.trueCloudImageViewMore.onClick {
            onMore.invoke()
        }
    }

    fun setUploadViewVisibility(isVisible: Boolean) {
        binding.trueCloudImageViewUpload.isVisible = isVisible
    }
}
