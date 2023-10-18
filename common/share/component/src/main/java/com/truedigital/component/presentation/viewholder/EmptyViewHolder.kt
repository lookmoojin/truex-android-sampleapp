package com.truedigital.component.presentation.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.component.databinding.ItemEmptyBinding

class EmptyViewHolder(viewGroup: ViewGroup) : RecyclerView.ViewHolder(
    ItemEmptyBinding.inflate(
        LayoutInflater.from(viewGroup.context),
        viewGroup,
        false
    ).root
)
