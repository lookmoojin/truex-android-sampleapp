package com.truedigital.features.music.presentation.myplaylist.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.tuned.databinding.ItemEmptyMyPlaylistBinding

class MyPlaylistEmptyViewHolder(
    private val binding: ItemEmptyMyPlaylistBinding
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(
            parent: ViewGroup
        ): MyPlaylistEmptyViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = ItemEmptyMyPlaylistBinding.inflate(layoutInflater, parent, false)
            return MyPlaylistEmptyViewHolder(view)
        }
    }
}
