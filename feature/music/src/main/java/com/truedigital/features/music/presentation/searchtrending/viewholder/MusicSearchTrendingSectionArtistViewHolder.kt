package com.truedigital.features.music.presentation.searchtrending.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.music.domain.trending.model.TrendingArtistModel
import com.truedigital.features.music.injections.MusicComponent
import com.truedigital.features.music.presentation.searchtrending.adapter.MusicSearchTrendingSectionArtistAdapter
import com.truedigital.features.tuned.databinding.ItemTrendingSectionBinding
import javax.inject.Inject

class MusicSearchTrendingSectionArtistViewHolder(
    val binding: ItemTrendingSectionBinding,
    private val onTrendingArtistClicked: (id: Int?) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    @Inject
    lateinit var adapterFactory: MusicSearchTrendingSectionArtistAdapter.MusicSearchTrendingSectionArtistAdapterFactory

    private val musicSearchTrendingSectionArtistAdapter: MusicSearchTrendingSectionArtistAdapter by lazy {
        adapterFactory.create(onTrendingArtistClicked)
    }

    companion object {
        fun from(
            parent: ViewGroup,
            onTrendingArtistClicked: (id: Int?) -> Unit
        ): MusicSearchTrendingSectionArtistViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = ItemTrendingSectionBinding.inflate(layoutInflater, parent, false)
            return MusicSearchTrendingSectionArtistViewHolder(view, onTrendingArtistClicked)
        }
    }

    init {
        MusicComponent.getInstance().inject(this)
    }

    fun bind(trendingArtistList: List<TrendingArtistModel>) = with(binding) {
        trendingSectionRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
            adapter = musicSearchTrendingSectionArtistAdapter
        }
        musicSearchTrendingSectionArtistAdapter.submitList(trendingArtistList)
    }
}
