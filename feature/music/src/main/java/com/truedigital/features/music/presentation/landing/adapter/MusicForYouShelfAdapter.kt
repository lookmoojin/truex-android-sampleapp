package com.truedigital.features.music.presentation.landing.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.component.presentation.viewholder.EmptyViewHolder
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.extensions.launchSafe
import com.truedigital.features.music.domain.landing.model.MusicForYouItemModel
import com.truedigital.features.music.domain.landing.model.MusicForYouShelfModel
import com.truedigital.features.music.domain.landing.model.MusicLandingFASelectContentModel
import com.truedigital.features.music.presentation.landing.viewholder.MusicForYouAdsBannerViewHolder
import com.truedigital.features.music.presentation.landing.viewholder.MusicForYouHeroBannerViewHolder
import com.truedigital.features.music.presentation.landing.viewholder.MusicForYouShelfViewHolder
import com.truedigital.features.tuned.data.productlist.model.ProductListType
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext

class MusicForYouShelfAdapter @AssistedInject constructor(
    private val coroutineDispatcher: CoroutineDispatcherProvider,
    @Assisted("onItemClicked") private val onItemClicked: (MusicForYouItemModel, MusicLandingFASelectContentModel) -> Unit,
    @Assisted("onSeeAllClicked") private val onSeeAllClicked: (MusicForYouShelfModel) -> Unit,
    @Assisted("onScrollShelves") private val onScrollShelves: (MusicForYouShelfModel) -> Unit
) : ListAdapter<MusicForYouShelfModel, RecyclerView.ViewHolder>(MusicForYouShelfDiffCallback()) {

    companion object {
        const val ITEM_SHELF = 100
        const val ITEM_HERO_BANNER = 101
        const val ITEM_ADS = 102
        const val ITEM_EMPTY = 200
    }

    @AssistedFactory
    interface MusicForYouShelfAdapterFactory {
        fun create(
            @Assisted("onItemClicked") onItemClicked: (MusicForYouItemModel, MusicLandingFASelectContentModel) -> Unit,
            @Assisted("onSeeAllClicked") onSeeAllClicked: (MusicForYouShelfModel) -> Unit,
            @Assisted("onScrollShelves") onScrollShelves: (MusicForYouShelfModel) -> Unit
        ): MusicForYouShelfAdapter
    }

    private val adapterScope = CoroutineScope(coroutineDispatcher.io())

    fun addItems(items: MutableList<MusicForYouShelfModel>) {
        adapterScope.launchSafe {
            val itemsSorted = items.sortedBy { it.index }

            withContext(coroutineDispatcher.main()) {
                submitList(itemsSorted)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).productListType) {
            ProductListType.TAGGED_ALBUMS,
            ProductListType.TAGGED_ARTISTS,
            ProductListType.TAGGED_PLAYLISTS,
            ProductListType.TRACKS_PLAYLIST,
            ProductListType.TAGGED_RADIO,
            ProductListType.CONTENT -> ITEM_SHELF
            ProductListType.TAGGED_ADS -> ITEM_ADS
            ProductListType.TAGGED_USER -> ITEM_HERO_BANNER
            else -> {
                ITEM_EMPTY
            }
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)

        val data = getItem(holder.layoutPosition)
        onScrollShelves.invoke(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            ITEM_ADS -> MusicForYouAdsBannerViewHolder.from(parent)
            ITEM_SHELF -> MusicForYouShelfViewHolder.from(parent, onItemClicked, onSeeAllClicked)
            ITEM_HERO_BANNER -> MusicForYouHeroBannerViewHolder.from(parent, onItemClicked)
            else -> EmptyViewHolder(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = getItem(position)
        when (holder) {
            is MusicForYouShelfViewHolder -> {
                holder.bind(data)
            }
            is MusicForYouHeroBannerViewHolder -> {
                holder.bind(data)
            }
            is MusicForYouAdsBannerViewHolder -> {
                holder.bind(data.itemList.firstOrNull())
            }
        }
    }

    class MusicForYouShelfDiffCallback : DiffUtil.ItemCallback<MusicForYouShelfModel>() {
        override fun areItemsTheSame(
            oldItem: MusicForYouShelfModel,
            newItem: MusicForYouShelfModel
        ): Boolean {
            return oldItem.index == newItem.index &&
                oldItem.productListType == newItem.productListType
        }

        override fun areContentsTheSame(
            oldItem: MusicForYouShelfModel,
            newItem: MusicForYouShelfModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}
