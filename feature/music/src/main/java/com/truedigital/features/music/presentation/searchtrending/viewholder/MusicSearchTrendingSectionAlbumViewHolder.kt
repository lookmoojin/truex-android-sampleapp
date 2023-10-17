package com.truedigital.features.music.presentation.searchtrending.viewholder

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.music.domain.trending.model.TrendingAlbumModel
import com.truedigital.features.music.injections.MusicComponent
import com.truedigital.features.music.presentation.searchtrending.adapter.MusicSearchTrendingSectionAlbumAdapter
import com.truedigital.features.tuned.databinding.ItemTrendingSectionBinding
import javax.inject.Inject

class MusicSearchTrendingSectionAlbumViewHolder(
    val binding: ItemTrendingSectionBinding,
    val context: Context,
    private val onTrendingAlbumClicked: (id: Int?) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    @Inject
    lateinit var adapterFactory: MusicSearchTrendingSectionAlbumAdapter.MusicSearchTrendingSectionAlbumAdapterFactory

    private val musicSearchTrendingSectionAlbumAdapter: MusicSearchTrendingSectionAlbumAdapter by lazy {
        adapterFactory.create(onTrendingAlbumClicked)
    }

    companion object {
        fun from(
            parent: ViewGroup,
            onTrendingAlbumClicked: (id: Int?) -> Unit
        ): MusicSearchTrendingSectionAlbumViewHolder {
            val view = ItemTrendingSectionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return MusicSearchTrendingSectionAlbumViewHolder(
                view,
                parent.context,
                onTrendingAlbumClicked
            )
        }
    }

    init {
        MusicComponent.getInstance().inject(this)
    }

    fun bind(trendingAlbumList: List<TrendingAlbumModel>) = with(binding) {
        trendingSectionRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
            adapter = musicSearchTrendingSectionAlbumAdapter
        }
        musicSearchTrendingSectionAlbumAdapter.submitList(trendingAlbumList)
    }
}
