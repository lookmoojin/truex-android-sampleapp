package com.truedigital.features.music.presentation.myplaylist.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.newrelic.agent.android.NewRelic
import com.truedigital.features.music.domain.myplaylist.model.MyPlaylistItemType
import com.truedigital.features.music.domain.myplaylist.model.MyPlaylistModel
import com.truedigital.features.music.presentation.myplaylist.viewholder.MyPlaylistEmptyViewHolder
import com.truedigital.features.music.presentation.myplaylist.viewholder.MyPlaylistHeaderViewHolder
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class MyPlaylistHeaderAdapter @AssistedInject constructor(
    @Assisted("onAddSongClicked") private val onAddSongClicked: () -> Unit,
    @Assisted("onShuffleClicked") private val onShuffleClicked: () -> Unit
) : ListAdapter<MyPlaylistModel, RecyclerView.ViewHolder>(MyPlaylistHeaderDiffCallback()) {

    companion object {
        private const val ITEM_HEADER = 100
        private const val ITEM_PLAYLIST_EMPTY = 200
    }

    @AssistedFactory
    interface MyPlaylistHeaderAdapterFactory {
        fun create(
            @Assisted("onAddSongClicked") onAddSongClicked: () -> Unit,
            @Assisted("onShuffleClicked") onShuffleClicked: () -> Unit
        ): MyPlaylistHeaderAdapter
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).itemType) {
            MyPlaylistItemType.HEADER -> ITEM_HEADER
            MyPlaylistItemType.PLAYLIST_EMPTY -> ITEM_PLAYLIST_EMPTY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_HEADER -> {
                MyPlaylistHeaderViewHolder.from(parent)
            }
            ITEM_PLAYLIST_EMPTY -> {
                MyPlaylistEmptyViewHolder.from(parent)
            }
            else -> {
                NewRelic.recordHandledException(
                    Exception("Invalid view type $viewType isn't supported in MyPlaylistHeaderAdapter")
                )
                throw Exception("view type $viewType isn't supported in MyPlaylistHeaderAdapter")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MyPlaylistHeaderViewHolder -> {
                holder.bind(getItem(position), onShuffleClicked, onAddSongClicked)
            }
        }
    }

    class MyPlaylistHeaderDiffCallback : DiffUtil.ItemCallback<MyPlaylistModel>() {
        override fun areItemsTheSame(oldItem: MyPlaylistModel, newItem: MyPlaylistModel): Boolean {
            return oldItem.itemId == newItem.itemId
        }

        override fun areContentsTheSame(
            oldItem: MyPlaylistModel,
            newItem: MyPlaylistModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}
