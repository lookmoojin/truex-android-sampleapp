package com.truedigital.navigation.adapter

import androidx.recyclerview.widget.DiffUtil
import com.truedigital.core.data.ShelfSkeleton

class ShelfDiffCallback(
    private val oldItems: List<ShelfSkeleton>?,
    private val newItems: List<ShelfSkeleton>?
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldItems?.size ?: 0

    override fun getNewListSize(): Int = newItems?.size ?: 0

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems?.getOrNull(oldItemPosition)?.shelfId == newItems?.getOrNull(newItemPosition)?.shelfId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems?.getOrNull(oldItemPosition) == newItems?.getOrNull(newItemPosition)
    }
}
