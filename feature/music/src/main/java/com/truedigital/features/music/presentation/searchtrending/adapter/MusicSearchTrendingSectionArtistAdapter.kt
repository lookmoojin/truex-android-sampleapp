package com.truedigital.features.music.presentation.searchtrending.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.truedigital.features.music.domain.trending.model.TrendingArtistModel
import com.truedigital.features.music.presentation.searchtrending.viewholder.MusicSearchTrendingArtistViewHolder
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class MusicSearchTrendingSectionArtistAdapter @AssistedInject constructor(
    @Assisted private val onTrendingArtistClicked: (id: Int?) -> Unit
) : ListAdapter<TrendingArtistModel,
    MusicSearchTrendingArtistViewHolder>(MusicSearchTrendingSectionArtistDiffCallback()) {

    @AssistedFactory
    interface MusicSearchTrendingSectionArtistAdapterFactory {
        fun create(
            onTrendingArtistClicked: (id: Int?) -> Unit
        ): MusicSearchTrendingSectionArtistAdapter
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MusicSearchTrendingArtistViewHolder {
        return MusicSearchTrendingArtistViewHolder.from(parent, onTrendingArtistClicked)
    }

    override fun onBindViewHolder(holder: MusicSearchTrendingArtistViewHolder, position: Int) {
        val data = getItem(position)
        return holder.bind(data)
    }

    class MusicSearchTrendingSectionArtistDiffCallback :
        DiffUtil.ItemCallback<TrendingArtistModel>() {

        override fun areItemsTheSame(
            oldItem: TrendingArtistModel,
            newItem: TrendingArtistModel
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: TrendingArtistModel,
            newItem: TrendingArtistModel
        ): Boolean {
            return oldItem.id == newItem.id &&
                oldItem.name == newItem.name &&
                oldItem.image == newItem.image
        }
    }
}
