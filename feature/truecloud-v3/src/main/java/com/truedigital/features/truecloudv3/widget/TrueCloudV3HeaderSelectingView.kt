package com.truedigital.features.truecloudv3.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.tdg.truecloud.R
import com.tdg.truecloud.databinding.TrueCloudv3HeaderSelectingViewBinding
import com.truedigital.foundation.extension.onClick

class TrueCloudV3HeaderSelectingView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {
    private val binding: TrueCloudv3HeaderSelectingViewBinding =
        TrueCloudv3HeaderSelectingViewBinding.inflate(
            LayoutInflater.from(context),
            this,
            false
        )

    init {
        addView(binding.root)
    }

    fun setTitle(title: String) {
        binding.trueCloudTextViewSelectTitle.text = title
    }

    fun setOnClickClose(onCloseSelect: () -> Unit) {
        binding.trueCloudImageViewClose.onClick {
            onCloseSelect.invoke()
        }
    }

    fun setOnClickSelectAll(onClickSelectAll: () -> Unit) {
        binding.trueCloudImageViewActionSelectAll.onClick {
            onClickSelectAll.invoke()
        }
    }

    fun setSelectAll(status: Boolean) {
        if (status) {
            binding.trueCloudImageViewActionSelectAll.setImageResource(R.drawable.ic_select_24)
        } else {
            binding.trueCloudImageViewActionSelectAll.setImageResource(R.drawable.ic_deselect)
        }
    }

    fun setOnClickSelectOption(onClickSelectOption: () -> Unit) {
        binding.trueCloudImageViewSelectOption.onClick {
            onClickSelectOption.invoke()
        }
    }

    fun setOnClickDelete(onClickDelete: () -> Unit) {
        binding.trueCloudImageViewTrash.onClick {
            onClickDelete.invoke()
        }
    }

    fun setOptionVisibility(status: Boolean) {
        binding.trueCloudImageViewSelectOption.isVisible = status
    }
}
