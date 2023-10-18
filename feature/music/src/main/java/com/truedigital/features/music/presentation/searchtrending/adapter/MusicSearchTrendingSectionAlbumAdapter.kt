package com.truedigital.features.music.presentation.searchtrending.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.truedigital.features.music.domain.trending.model.TrendingAlbumModel
import com.truedigital.features.music.presentation.searchtrending.viewholder.MusicSearchTrendingAlbumViewHolder
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class MusicSearchTrendingSectionAlbumAdapter @AssistedInject constructor(
    @Assisted private val onTrendingArtistClicked: (id: Int?) -> Unit
) : ListAdapter<TrendingAlbumModel,
    MusicSearchTrendingAlbumViewHolder>(MusicSearchTrendingSectionAlbumDiffCallback()) {

    @AssistedFactory
    interface MusicSearchTrendingSectionAlbumAdapterFactory {
        fun create(
            onTrendingArtistClicked: (id: Int?) -> Unit
        ): MusicSearchTrendingSectionAlbumAdapter
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MusicSearchTrendingAlbumViewHolder {
        return MusicSearchTrendingAlbumViewHolder.from(parent, onTrendingArtistClicked)
    }

    override fun onBindViewHolder(holder: MusicSearchTrendingAlbumViewHolder, position: Int) {
        val data = getItem(position)
        return holder.bind(data)
    }

    class MusicSearchTrendingSectionAlbumDiffCallback :
        DiffUtil.ItemCallback<TrendingAlbumModel>() {

        override fun areItemsTheSame(
            oldItem: TrendingAlbumModel,
            newItem: TrendingAlbumModel
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: TrendingAlbumModel,
            newItem: TrendingAlbumModel
        ): Boolean {
            return oldItem.id == newItem.id &&
                oldItem.name == newItem.name &&
                oldItem.image == newItem.image
        }
    }
}
