package com.truedigital.features.tuned.presentation.productlist.view

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.daimajia.swipe.SimpleSwipeListener
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.common.extensions.find
import com.truedigital.features.tuned.common.extensions.toast
import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.album.model.Release
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.download.ImageManager
import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.product.model.Product
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.tag.model.Tag
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.databinding.ItemMusicLoadingBinding
import com.truedigital.foundation.extension.onClick

abstract class ProductListBaseAdapter(
    val imageManager: ImageManager,
    val numItemsToDisplay: Int? = null,
    val onClickListener: (Product) -> Unit,
    val onLongClickListener: ((Product) -> Unit)? = null,
    val onMoreSelectedListener: ((Product) -> Unit)? = null,
    val onPageLoadListener: (() -> Unit)? = null,
    val onRemoveListener: ((Product) -> Unit)? = null
) : RecyclerSwipeAdapter<RecyclerView.ViewHolder>() {

    companion object {
        const val ITEM_TYPE_LOADING = 0
        const val ITEM_TYPE_TRACK = 1
        const val ITEM_TYPE_ARTIST = 2
        const val ITEM_TYPE_ALBUM = 3
        const val ITEM_TYPE_RELEASE = 4
        const val ITEM_TYPE_PLAYLIST = 5
        const val ITEM_TYPE_STATION = 6
        const val ITEM_TYPE_VIDEO = 7
        const val ITEM_TYPE_TAG = 8
    }

    var items: List<Product> = mutableListOf()
        set(value) {
            (items as MutableList).clear()
            (items as MutableList).addAll(value)
            notifyDataSetChanged()
        }

    private var hasPaging = onPageLoadListener != null
    var morePages = false

    // region adapterFunctions

    override fun getItemViewType(position: Int): Int = if (position == items.size) {
        ITEM_TYPE_LOADING
    } else {
        when (items[position]) {
            is Track ->
                if ((items[position] as Track).isVideo)
                    ITEM_TYPE_VIDEO
                else
                    ITEM_TYPE_TRACK

            is Artist -> ITEM_TYPE_ARTIST
            is Album -> ITEM_TYPE_ALBUM
            is Release -> ITEM_TYPE_RELEASE
            is Playlist -> ITEM_TYPE_PLAYLIST
            is Station -> ITEM_TYPE_STATION
            is Tag -> ITEM_TYPE_TAG
            else -> throw IllegalArgumentException("Invalid product type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ProductListViewHolder) {
            val viewType = getItemViewType(position)
            val itemView = holder.itemViewBinding
            bindListItem(viewType, itemView, position)
        } else {
            onPageLoadListener?.invoke()
        }
    }

    override fun getItemCount(): Int {
        val size = items.size + if (morePages && hasPaging) 1 else 0
        return if (numItemsToDisplay != null && size > numItemsToDisplay) numItemsToDisplay else size
    }

    override fun getSwipeLayoutResourceId(position: Int) = R.id.swipeView

    private fun bindListItem(viewType: Int, itemViewBinding: ViewBinding, position: Int) {
        when (viewType) {
            ITEM_TYPE_TRACK -> bindTrack(itemViewBinding, position)
            ITEM_TYPE_ARTIST -> bindArtist(itemViewBinding, position)
            ITEM_TYPE_ALBUM -> createAlbum(itemViewBinding, position)
            ITEM_TYPE_RELEASE -> bindRelease(itemViewBinding, position, null)
            ITEM_TYPE_PLAYLIST -> bindPlaylist(itemViewBinding, position)
            ITEM_TYPE_STATION -> bindStation(itemViewBinding, position)
            ITEM_TYPE_VIDEO -> bindVideo(itemViewBinding, position)
            ITEM_TYPE_TAG -> bindTag(itemViewBinding, position)
        }
    }

    private fun createAlbum(itemView: ViewBinding, position: Int) {
        val album = items[position] as Album
        bindRelease(itemView, position, album)
    }

    abstract fun bindTrack(viewBinding: ViewBinding, position: Int)
    abstract fun bindArtist(viewBinding: ViewBinding, position: Int)
    abstract fun bindRelease(viewBinding: ViewBinding, position: Int, album: Album?)
    abstract fun bindPlaylist(viewBinding: ViewBinding, position: Int)
    abstract fun bindStation(viewBinding: ViewBinding, position: Int)
    abstract fun bindVideo(viewBinding: ViewBinding, position: Int)
    abstract fun bindTag(viewBinding: ViewBinding, position: Int)

    inner class ProductListViewHolder(
        val itemViewBinding: ViewBinding,
        onClickListener: (Product) -> Unit,
        onLongClickListener: ((Product) -> Unit)? = null,
        onMoreSelectedListener: ((Product) -> Unit)? = null,
        onRemoveListener: ((Product) -> Unit)? = null
    ) : RecyclerView.ViewHolder(itemViewBinding.root) {
        init {
            val clickView = itemViewBinding.root.findViewById(R.id.surfaceViewHolder)
                ?: itemViewBinding.root
            clickView.onClick {
                val item = items[layoutPosition]
                if (!(item is Track && !item.allowStream)) onClickListener(item)
                else itemViewBinding.root.context.toast(R.string.track_not_available_message)
                closeAllItems()
            }
            clickView.setOnLongClickListener {
                val item = items[layoutPosition]
                if (!(item is Track && !item.allowStream)) onLongClickListener?.invoke(item)
                else itemViewBinding.root.context.toast(R.string.track_not_available_message)
                closeAllItems()
                true
            }
            itemViewBinding.root.find<View>(R.id.moreButton)?.onClick {
                val item = items[layoutPosition]
                if (!(item is Track && !item.allowStream)) onMoreSelectedListener?.invoke(item)
                else itemViewBinding.root.context.toast(R.string.track_not_available_message)
                closeAllItems()
            }
            itemViewBinding.root.find<ImageView>(R.id.deleteImageView)?.onClick {
                onRemoveListener?.invoke(items[layoutPosition])
                closeAllItems()
            }
            val swipeLayout = itemViewBinding.root.findViewById(R.id.swipeView) as? SwipeLayout
            swipeLayout?.isSwipeEnabled = onRemoveListener != null
            swipeLayout?.isClickToClose = true
            swipeLayout?.addSwipeListener(object : SimpleSwipeListener() {
                override fun onStartOpen(layout: SwipeLayout?) {
                    closeAllExcept(layout)
                }
            })
        }
    }

    inner class LoadingViewHolder(binding: ItemMusicLoadingBinding, isGrid: Boolean) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            if (isGrid) {
                binding.root.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
                binding.root.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            } else {
                binding.root.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            }
            binding.root.requestLayout()
        }
    }
}
