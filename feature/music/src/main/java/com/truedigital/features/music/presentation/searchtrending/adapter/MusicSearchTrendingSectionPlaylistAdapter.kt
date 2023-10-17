package com.truedigital.features.music.presentation.searchtrending.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.truedigital.features.music.domain.trending.model.TrendingPlaylistModel
import com.truedigital.features.music.presentation.searchtrending.viewholder.MusicSearchTrendingPlaylistViewHolder
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class MusicSearchTrendingSectionPlaylistAdapter @AssistedInject constructor(
    @Assisted private val onTrendingArtistClicked: (id: Int?) -> Unit
) : ListAdapter<TrendingPlaylistModel,
    MusicSearchTrendingPlaylistViewHolder>(MusicSearchTrendingSectionPlaylistDiffCallback()) {

    @AssistedFactory
    interface MusicSearchTrendingSectionPlaylistAdapterFactory {
        fun create(
            onTrendingArtistClicked: (id: Int?) -> Unit
        ): MusicSearchTrendingSectionPlaylistAdapter
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MusicSearchTrendingPlaylistViewHolder {
        return MusicSearchTrendingPlaylistViewHolder.from(parent, onTrendingArtistClicked)
    }

    override fun onBindViewHolder(holder: MusicSearchTrendingPlaylistViewHolder, position: Int) {
        val data = getItem(position)
        return holder.bind(data)
    }

    class MusicSearchTrendingSectionPlaylistDiffCallback :
        DiffUtil.ItemCallback<TrendingPlaylistModel>() {

        override fun areItemsTheSame(
            oldItem: TrendingPlaylistModel,
            newItem: TrendingPlaylistModel
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: TrendingPlaylistModel,
            newItem: TrendingPlaylistModel
        ): Boolean {
            return oldItem.id == newItem.id &&
                oldItem.name == newItem.name &&
                oldItem.image == newItem.image
        }
    }
}
