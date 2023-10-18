package com.truedigital.features.music.presentation.searchtrending.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.newrelic.agent.android.NewRelic
import com.truedigital.features.music.domain.trending.model.TrendingAlbumModel
import com.truedigital.features.music.domain.trending.model.TrendingArtistModel
import com.truedigital.features.music.domain.trending.model.TrendingPlaylistModel
import com.truedigital.features.music.presentation.searchtrending.viewholder.MusicSearchTrendingHeaderViewHolder
import com.truedigital.features.music.presentation.searchtrending.viewholder.MusicSearchTrendingSectionAlbumViewHolder
import com.truedigital.features.music.presentation.searchtrending.viewholder.MusicSearchTrendingSectionArtistViewHolder
import com.truedigital.features.music.presentation.searchtrending.viewholder.MusicSearchTrendingSectionPlaylistViewHolder
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class MusicSearchTrendingAdapter @AssistedInject constructor(
    @Assisted("onTrendingArtistClicked") private val onTrendingArtistClicked: (id: Int?) -> Unit,
    @Assisted("onTrendingPlaylistClicked") private val onTrendingPlaylistClicked: (id: Int?) -> Unit,
    @Assisted("onTrendingAlbumClicked") private val onTrendingAlbumClicked: (id: Int?) -> Unit
) : ListAdapter<MusicSearchTrendingAdapter.DataItem, RecyclerView.ViewHolder>(
    MusicSearchTrendingDiffCallback()
) {

    companion object {
        const val KEY_ITEM_HEADER = 10
        const val KEY_ITEM_CONTENT_ARTIST = 11
        const val KEY_ITEM_CONTENT_PLAYLIST = 12
        const val KEY_ITEM_CONTENT_ALBUM = 13

        const val KEY_ITEM_HEADER_ARTIST_ID = 20
        const val KEY_ITEM_HEADER_PLAYLIST_ID = 21

        const val KEY_ITEM_CONTENT_ARTIST_ID = 30
        const val KEY_ITEM_CONTENT_PLAYLIST_ID = 31

        const val KEY_ITEM_HEADER_ALBUM_ID = 40
        const val KEY_ITEM_CONTENT_ALBUM_ID = 41
    }

    @AssistedFactory
    interface MusicSearchTrendingAdapterFactory {
        fun create(
            @Assisted("onTrendingArtistClicked") onTrendingArtistClicked: (id: Int?) -> Unit,
            @Assisted("onTrendingPlaylistClicked") onTrendingPlaylistClicked: (id: Int?) -> Unit,
            @Assisted("onTrendingAlbumClicked") onTrendingAlbumClicked: (id: Int?) -> Unit
        ): MusicSearchTrendingAdapter
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem((position))) {
            is DataItem.TrendingHeaderItem -> KEY_ITEM_HEADER
            is DataItem.TrendingArtistItem -> KEY_ITEM_CONTENT_ARTIST
            is DataItem.TrendingPlaylistItem -> KEY_ITEM_CONTENT_PLAYLIST
            is DataItem.TrendingAlbumItem -> KEY_ITEM_CONTENT_ALBUM
            else -> {
                NewRelic.recordHandledException(
                    Exception("Unknown item position $position in MusicSearchTrendingAdapter")
                )
                throw Exception("Unknown item position $position in MusicSearchTrendingAdapter")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            KEY_ITEM_HEADER -> MusicSearchTrendingHeaderViewHolder.from(parent)
            KEY_ITEM_CONTENT_ARTIST -> {
                MusicSearchTrendingSectionArtistViewHolder.from(parent, onTrendingArtistClicked)
            }
            KEY_ITEM_CONTENT_PLAYLIST -> {
                MusicSearchTrendingSectionPlaylistViewHolder.from(parent, onTrendingPlaylistClicked)
            }
            KEY_ITEM_CONTENT_ALBUM -> {
                MusicSearchTrendingSectionAlbumViewHolder.from(parent, onTrendingAlbumClicked)
            }
            else -> {
                NewRelic.recordHandledException(
                    Exception("Invalid view type $viewType isn't supported in MusicSearchTrendingAdapter")
                )
                throw Exception("view type $viewType isn't supported in MusicSearchTrendingAdapter")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MusicSearchTrendingHeaderViewHolder -> {
                val headerItem = getItem(position) as DataItem.TrendingHeaderItem
                holder.bind(headerItem.title)
            }
            is MusicSearchTrendingSectionArtistViewHolder -> {
                val artistItem = getItem(position) as DataItem.TrendingArtistItem
                holder.bind(artistItem.trendingArtistList)
            }
            is MusicSearchTrendingSectionPlaylistViewHolder -> {
                val playlistItem = getItem(position) as DataItem.TrendingPlaylistItem
                holder.bind(playlistItem.trendingPlaylistList)
            }
            is MusicSearchTrendingSectionAlbumViewHolder -> {
                val albumItem = getItem(position) as DataItem.TrendingAlbumItem
                holder.bind(albumItem.trendingAlbumList)
            }
        }
    }

    class MusicSearchTrendingDiffCallback : DiffUtil.ItemCallback<DataItem>() {
        override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem == newItem
        }
    }

    sealed class DataItem {
        abstract val id: Int

        data class TrendingArtistItem(
            val contentId: Int,
            val trendingArtistList: List<TrendingArtistModel>
        ) : DataItem() {
            override val id: Int = contentId
        }

        data class TrendingPlaylistItem(
            val contentId: Int,
            val trendingPlaylistList: List<TrendingPlaylistModel>
        ) : DataItem() {
            override val id: Int = contentId
        }

        data class TrendingAlbumItem(
            val contentId: Int,
            val trendingAlbumList: List<TrendingAlbumModel>
        ) : DataItem() {
            override val id: Int = contentId
        }

        data class TrendingHeaderItem(val headerId: Int, val title: String) : DataItem() {
            override val id: Int = headerId
        }
    }
}
