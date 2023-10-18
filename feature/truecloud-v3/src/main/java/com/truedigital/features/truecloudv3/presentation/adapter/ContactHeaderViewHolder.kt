package com.truedigital.features.truecloudv3.presentation.adapter

import androidx.recyclerview.widget.RecyclerView
import com.tdg.truecloud.databinding.TrueCloudv3ViewholderContactHeaderBinding
import com.truedigital.features.truecloudv3.domain.model.HeaderSelectionModel

class ContactHeaderViewHolder(
    private val binding: TrueCloudv3ViewholderContactHeaderBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: HeaderSelectionModel) {
        binding.trueCloudTitleTextView.text = item.key
    }
}
