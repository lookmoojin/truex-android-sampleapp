package com.truedigital.features.music.presentation.searchtrending.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.music.domain.trending.model.TrendingPlaylistModel
import com.truedigital.features.music.injections.MusicComponent
import com.truedigital.features.music.presentation.searchtrending.adapter.MusicSearchTrendingSectionPlaylistAdapter
import com.truedigital.features.tuned.databinding.ItemTrendingSectionBinding
import javax.inject.Inject

class MusicSearchTrendingSectionPlaylistViewHolder(
    val binding: ItemTrendingSectionBinding,
    private val onTrendingPlaylistClicked: (id: Int?) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    @Inject
    lateinit var adapterFactory: MusicSearchTrendingSectionPlaylistAdapter.MusicSearchTrendingSectionPlaylistAdapterFactory

    private val musicSearchTrendingSectionArtistAdapter: MusicSearchTrendingSectionPlaylistAdapter by lazy {
        adapterFactory.create(onTrendingPlaylistClicked)
    }

    companion object {
        fun from(
            parent: ViewGroup,
            onTrendingPlaylistClicked: (id: Int?) -> Unit
        ): MusicSearchTrendingSectionPlaylistViewHolder {
            val view = ItemTrendingSectionBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            return MusicSearchTrendingSectionPlaylistViewHolder(view, onTrendingPlaylistClicked)
        }
    }

    init {
        MusicComponent.getInstance().inject(this)
    }

    fun bind(trendingPlaylistList: List<TrendingPlaylistModel>) = with(binding) {
        trendingSectionRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
            adapter = musicSearchTrendingSectionArtistAdapter
        }
        musicSearchTrendingSectionArtistAdapter.submitList(trendingPlaylistList)
    }
}
