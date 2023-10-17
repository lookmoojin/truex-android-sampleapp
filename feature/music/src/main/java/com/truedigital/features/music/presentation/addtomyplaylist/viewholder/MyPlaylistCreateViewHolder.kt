package com.truedigital.features.music.presentation.addtomyplaylist.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.tuned.databinding.ItemMyPlaylistCreateBinding

class MyPlaylistCreateViewHolder(
    val binding: ItemMyPlaylistCreateBinding
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(
            parent: ViewGroup
        ): MyPlaylistCreateViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = ItemMyPlaylistCreateBinding.inflate(layoutInflater, parent, false)
            return MyPlaylistCreateViewHolder(view)
        }
    }
}
