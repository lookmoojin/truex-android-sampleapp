package com.truedigital.features.music.presentation.landing.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.component.presentation.viewholder.EmptyViewHolder
import com.truedigital.features.listens.share.constant.MusicConstant
import com.truedigital.features.music.constant.MusicShelfType
import com.truedigital.features.music.domain.landing.model.MusicForYouItemModel
import com.truedigital.features.music.presentation.landing.viewholder.MusicContentItemSquareGridViewHolder
import com.truedigital.features.music.presentation.landing.viewholder.MusicForYouItemCircleViewHolder
import com.truedigital.features.music.presentation.landing.viewholder.MusicForYouItemGridViewHolder
import com.truedigital.features.music.presentation.landing.viewholder.MusicForYouItemSquareSubTitleViewHolder
import com.truedigital.features.music.presentation.landing.viewholder.MusicForYouItemSquareTitleViewHolder
import com.truedigital.features.music.presentation.landing.viewholder.MusicForYouItemSquareViewHolder
import com.truedigital.foundation.extension.clickAsFlow
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class MusicForYouItemAdapter @AssistedInject constructor(
    @Assisted private val onItemClicked: (MusicForYouItemModel, Int) -> Unit
) : ListAdapter<MusicForYouItemModel, RecyclerView.ViewHolder>(MusicForYouItemDiffCallback()) {

    companion object {
        const val ITEM_SQUARE = 100
        const val ITEM_SQUARE_SUB_TITLE = 101
        const val ITEM_CIRCLE = 102
        const val ITEM_GRID = 103
        const val ITEM_SQUARE_TITLE = 104
        const val ITEM_SQUARE_GRID_2 = 200
        const val ITEM_EMPTY = 300
    }

    @AssistedFactory
    interface MusicForYouItemAdapterFactory {
        fun create(onItemClicked: (MusicForYouItemModel, Int) -> Unit): MusicForYouItemAdapter
    }

    override fun getItemViewType(position: Int): Int {
        return when (val data = getItem(position)) {
            is MusicForYouItemModel.ArtistShelfItem -> ITEM_CIRCLE
            is MusicForYouItemModel.PlaylistShelfItem -> ITEM_SQUARE
            is MusicForYouItemModel.AlbumShelfItem -> ITEM_SQUARE_SUB_TITLE
            is MusicForYouItemModel.TrackPlaylistShelf -> ITEM_GRID
            is MusicForYouItemModel.RadioShelfItem -> {
                if (data.shelfType == MusicShelfType.GRID_2) {
                    ITEM_SQUARE_GRID_2
                } else {
                    ITEM_SQUARE_TITLE
                }
            }
            else -> ITEM_EMPTY
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_CIRCLE -> MusicForYouItemCircleViewHolder.from(parent)
            ITEM_GRID -> MusicForYouItemGridViewHolder.from(parent)
            ITEM_SQUARE -> MusicForYouItemSquareViewHolder.from(parent)
            ITEM_SQUARE_SUB_TITLE -> MusicForYouItemSquareSubTitleViewHolder.from(parent)
            ITEM_SQUARE_TITLE -> MusicForYouItemSquareTitleViewHolder.from(parent)
            ITEM_SQUARE_GRID_2 -> MusicContentItemSquareGridViewHolder.from(parent)
            else -> EmptyViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = getItem(position)
        when (holder) {
            is MusicForYouItemCircleViewHolder -> {
                holder.bind(data)
            }
            is MusicForYouItemGridViewHolder -> {
                holder.bind(data)
            }
            is MusicForYouItemSquareViewHolder -> {
                holder.bind(data)
            }
            is MusicForYouItemSquareSubTitleViewHolder -> {
                holder.bind(data)
            }
            is MusicForYouItemSquareTitleViewHolder -> {
                holder.bind(data) {
                    onItemClicked.invoke(it, position)
                }
            }
            is MusicContentItemSquareGridViewHolder -> {
                holder.bind(data)
            }
        }

        holder.itemView.clickAsFlow()
            .debounce(com.truedigital.features.listens.share.constant.MusicConstant.DelayTime.DELAY_500)
            .onEach {
                onItemClicked.invoke(data, position)
            }.launchIn(MainScope())
    }

    class MusicForYouItemDiffCallback : DiffUtil.ItemCallback<MusicForYouItemModel>() {
        override fun areItemsTheSame(
            oldItem: MusicForYouItemModel,
            newItem: MusicForYouItemModel
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: MusicForYouItemModel,
            newItem: MusicForYouItemModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}
