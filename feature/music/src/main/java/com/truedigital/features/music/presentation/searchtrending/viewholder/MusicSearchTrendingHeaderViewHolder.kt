package com.truedigital.features.music.presentation.searchtrending.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.tuned.databinding.ItemTrendingHeaderBinding

class MusicSearchTrendingHeaderViewHolder(
    val binding: ItemTrendingHeaderBinding
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(parent: ViewGroup): MusicSearchTrendingHeaderViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = ItemTrendingHeaderBinding.inflate(layoutInflater, parent, false)
            return MusicSearchTrendingHeaderViewHolder(view)
        }
    }

    fun bind(title: String) = with(binding) {
        titleTextView.text = title
    }
}
