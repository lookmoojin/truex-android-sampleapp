package com.truedigital.features.truecloudv3.presentation.adapter

import android.graphics.Typeface
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.databinding.TrueCloudv3ItemAlphabetScrollBinding
import com.truedigital.features.truecloudv3.domain.model.AlphabetItemModel
import com.truedigital.foundation.extension.onClick

class AlphabetViewHolder(
    private val binding: TrueCloudv3ItemAlphabetScrollBinding,
    private val onItemClicked: ((item: AlphabetItemModel) -> Unit)? = null
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: AlphabetItemModel) {
        binding.trueCloudTitleTextView.text = item.alphabet
        if (item.isActive) {
            val activeColor = ContextCompat.getColor(binding.root.context, R.color.true_cloudv3_color_black)
            binding.trueCloudTitleTextView.setTextColor(activeColor)
            binding.trueCloudTitleTextView.setTypeface(null, Typeface.BOLD)
        } else {
            val inactiveColor = ContextCompat.getColor(binding.root.context, R.color.true_cloudv3_color_gray_30)
            binding.trueCloudTitleTextView.setTextColor(inactiveColor)
            binding.trueCloudTitleTextView.setTypeface(null, Typeface.NORMAL)
        }

        binding.root.onClick {
            onItemClicked?.invoke(item)
        }
    }
}
