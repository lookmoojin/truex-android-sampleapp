package com.truedigital.features.music.presentation.mylibrary.mymusic.viewholder

import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.listens.share.constant.MusicConstant
import com.truedigital.features.tuned.databinding.ItemAddNewListBinding
import com.truedigital.foundation.extension.clickAsFlow
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MyPlaylistCreateViewHolder(
    val binding: ItemAddNewListBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(onItemClick: () -> Unit) {
        itemView.clickAsFlow()
            .debounce(com.truedigital.features.listens.share.constant.MusicConstant.DelayTime.DELAY_500)
            .onEach {
                onItemClick()
            }.launchIn(MainScope())
    }
}
