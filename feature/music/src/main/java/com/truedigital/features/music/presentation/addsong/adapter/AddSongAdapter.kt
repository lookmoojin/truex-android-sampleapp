package com.truedigital.features.music.presentation.addsong.adapter

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.truedigital.features.music.domain.addsong.model.MusicSearchResultModel
import com.truedigital.features.music.presentation.addsong.viewholder.AddSongViewHolder
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class AddSongAdapter @AssistedInject constructor(
    @Assisted private val onAddSongClicked: (Int?) -> Unit
) : PagingDataAdapter<MusicSearchResultModel, AddSongViewHolder>(AddSongDiffCallback()) {

    @AssistedFactory
    interface AddSongAdapterFactory {
        fun create(onAddSongClicked: (Int?) -> Unit): AddSongAdapter
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddSongViewHolder {
        return AddSongViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: AddSongViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it, onAddSongClicked)
        }
    }

    class AddSongDiffCallback : DiffUtil.ItemCallback<MusicSearchResultModel>() {
        override fun areItemsTheSame(
            oldItem: MusicSearchResultModel,
            newItem: MusicSearchResultModel
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: MusicSearchResultModel,
            newItem: MusicSearchResultModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}
