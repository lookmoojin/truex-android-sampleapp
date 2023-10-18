package com.truedigital.features.truecloudv3.presentation.adapter

import androidx.recyclerview.widget.RecyclerView
import com.tdg.truecloud.databinding.TrueCloudv3ViewholderDataInfoItemBinding

class DataInfoViewHolder(
    private val binding: TrueCloudv3ViewholderDataInfoItemBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: Pair<String, String>) {
        binding.apply {
            infoTitle.text = item.first
            infoValue.text = item.second
        }
    }
}
