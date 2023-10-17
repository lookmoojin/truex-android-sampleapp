package com.truedigital.features.tuned.presentation.track

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.swipe.SimpleSwipeListener
import com.daimajia.swipe.SwipeLayout
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.common.Constants.FLOAT_0_5F
import com.truedigital.features.tuned.common.Constants.FLOAT_16F
import com.truedigital.features.tuned.common.Constants.FLOAT_32F
import com.truedigital.features.tuned.common.extensions.dp
import com.truedigital.features.tuned.common.extensions.toast
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.databinding.ItemFooterAutoPlayBinding
import com.truedigital.features.tuned.databinding.ItemMusicLoadingBinding
import com.truedigital.features.tuned.databinding.ItemPlayerQueueBinding
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible
import com.truedigital.foundation.extension.visibleOrGone

class DragDropTrackAdapter(
    val onClickListener: (Int) -> Unit,
    val onDragAndDropListener: (Int, Int) -> Unit,
    val onRemoveListener: (Int) -> Unit,
    val onLongClickListener: (Track, Int) -> Unit,
    val onMoreSelectedListener: (Track, Int) -> Unit,
    val onPageLoadListener: (() -> Unit)? = null,
    val showAutoPlayRow: Boolean = false
) : RecyclerSwipeAdapter<RecyclerView.ViewHolder>() {
    init {
        if (onPageLoadListener != null && showAutoPlayRow)
            throw IllegalStateException("cannot have paging and auto play together")
    }

    companion object {
        private const val ITEM_TYPE_FOOTER = 0
        private const val ITEM_TYPE_TRACK = 1
        private const val ITEM_TYPE_LOADING = 2
    }

    var currentPlayingTrackIdAndIndex = -1 to -1
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var items: List<Track> = mutableListOf()
        set(value) {
            (items as MutableList).clear()
            (items as MutableList).addAll(value)
            notifyDataSetChanged()
        }

    var touchHelper = ItemTouchHelper(DragHelper())

    private val hasPaging = onPageLoadListener != null
    var morePages = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ITEM_TYPE_FOOTER -> {
                FooterViewHolder(
                    ItemFooterAutoPlayBinding.inflate(
                        inflater,
                        parent,
                        false
                    )
                )
            }

            ITEM_TYPE_LOADING -> {
                LoadingViewHolder(
                    ItemMusicLoadingBinding.inflate(
                        inflater,
                        parent,
                        false
                    )
                )
            }

            else -> {
                TrackViewHolder(
                    ItemPlayerQueueBinding.inflate(
                        inflater,
                        parent,
                        false
                    ),
                    onClickListener,
                    onLongClickListener,
                    onMoreSelectedListener
                )
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FooterViewHolder) return

        if (holder is LoadingViewHolder) {
            onPageLoadListener?.invoke()
            return
        }

        val track = items[position]
        (holder as? TrackViewHolder)?.bind(track, position)
    }

    override fun getSwipeLayoutResourceId(position: Int) = R.id.swipeView

    override fun getItemCount(): Int =
        if (showAutoPlayRow || (morePages && hasPaging)) items.size + 1 else items.size

    override fun getItemViewType(position: Int): Int =
        if (position == items.size)
            if (hasPaging)
                ITEM_TYPE_LOADING
            else
                ITEM_TYPE_FOOTER
        else
            ITEM_TYPE_TRACK

    fun getItemPosition(trackId: Int) = items.indexOfFirst { it.id == trackId }

    fun updateDownloadStatus(position: Int, currTrackProgress: Long, currTrackSize: Long) {
        val track = items[position]
        if (currTrackProgress == currTrackSize) {
            track.isDownloaded = true
            track.isCached = true
            notifyItemChanged(position)
        }
    }

    inner class TrackViewHolder(
        private val binding: ItemPlayerQueueBinding,
        onClickListener: (Int) -> Unit,
        onLongClickListener: (Track, Int) -> Unit,
        onMoreSelectedListener: (Track, Int) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            with(binding) {
                itemView
                swipeView.isClickToClose = true
                surfaceViewHolder.onClick {
                    val item = items[layoutPosition]
                    val isCurrentPlayingTrack =
                        currentPlayingTrackIdAndIndex.first == item.id &&
                            (
                                currentPlayingTrackIdAndIndex.second == layoutPosition ||
                                    currentPlayingTrackIdAndIndex.second == -1
                                )
                    if (!isCurrentPlayingTrack) {
                        if (item.allowStream) onClickListener(layoutPosition)
                        else showNotAvailableMessageToast()
                    }
                    closeAllItems()
                }
                deleteImageView.onClick {
                    onRemoveListener(layoutPosition)
                    closeAllItems()
                }
                flDragAndDrop.setOnTouchListener { _, event ->
                    if (event.actionMasked == MotionEvent.ACTION_DOWN) {
                        touchHelper.startDrag(this@TrackViewHolder)
                    }
                    closeAllItems()
                    false
                }
                surfaceViewHolder.setOnLongClickListener {
                    val item = items[layoutPosition]
                    if (item.allowStream) onLongClickListener(item, layoutPosition)
                    else showNotAvailableMessageToast()
                    closeAllItems()
                    true
                }
                (root.findViewById(R.id.moreButton) as? View)?.onClick {
                    val item = items[layoutPosition]
                    if (item.allowStream) onMoreSelectedListener(item, layoutPosition)
                    else showNotAvailableMessageToast()
                    closeAllItems()
                }
                swipeView.addSwipeListener(object : SimpleSwipeListener() {
                    override fun onStartOpen(layout: SwipeLayout?) {
                        closeAllExcept(layout)
                    }
                })
            }
        }

        fun bind(track: Track, position: Int) = with(binding) {
            surfaceViewHolder.alpha = if (track.allowStream) 1f else FLOAT_0_5F
            nameTextView.text = track.name
            durationTextView.text = track.formattedDuration
            artistTextView.text =
                track.getArtistString(root.context.getString(R.string.various_artists_title))

            val isCurrentPlayingTrack = getCurrentPlayingTrack(track.id, position)
            nowPlayingImageView.visibleOrGone(show = isCurrentPlayingTrack)
            dragAndDropImageView.visibleOrGone(show = !isCurrentPlayingTrack)
            explicitImageView.visibleOrGone(show = track.isExplicit)
            offlineImageView.visibleOrGone(show = track.isCached)

            val hasLyrics = track.hasLyrics
            val hasVideo = track.video != null || track.isVideo
            handlerDisplayLyricsAndVideo(binding, hasLyrics, hasVideo)

            // bind view to swipe item manager
            mItemManger.bindView(root, position)
        }

        private fun showNotAvailableMessageToast() {
            binding.root.context.toast(R.string.track_not_available_message)
        }
    }

    private fun handlerDisplayLyricsAndVideo(
        binding: ItemPlayerQueueBinding,
        hasLyrics: Boolean,
        hasVideo: Boolean
    ) {
        with(binding) {
            val nameLp = nameTextView.layoutParams as FrameLayout.LayoutParams
            when {
                hasLyrics && hasVideo -> {
                    nameLp.marginEnd = root.context.resources.dp(FLOAT_32F)
                    val videoLp = videoImageView.layoutParams as FrameLayout.LayoutParams
                    videoLp.marginEnd = root.context.resources.dp(FLOAT_16F)
                    lyricsImageView.visible()
                    videoImageView.visible()
                }

                hasLyrics || hasVideo -> {
                    nameLp.marginEnd = root.context.resources.dp(FLOAT_16F)
                    lyricsImageView.visibleOrGone(show = hasLyrics)
                    videoImageView.visibleOrGone(show = hasVideo)
                }

                else -> {
                    nameLp.marginEnd = 0
                    lyricsImageView.gone()
                    videoImageView.gone()
                }
            }
        }
    }

    private fun getCurrentPlayingTrack(trackId: Int, position: Int): Boolean {
        return currentPlayingTrackIdAndIndex.first == trackId &&
            (
                currentPlayingTrackIdAndIndex.second == position ||
                    currentPlayingTrackIdAndIndex.second == -1
                )
    }

    inner class FooterViewHolder(binding: ItemFooterAutoPlayBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class LoadingViewHolder(binding: ItemMusicLoadingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            binding.root.requestLayout()
        }
    }

    inner class DragHelper : ItemTouchHelper.Callback() {
        var dragFrom = -1
        var dragTo = -1
        override fun getMovementFlags(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder
        ): Int {
            if (viewHolder is FooterViewHolder) {
                return 0
            }
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            val swipeFlags = 0
            return makeMovementFlags(dragFlags, swipeFlags)
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            val fromPosition = viewHolder.adapterPosition
            val toPosition = target.adapterPosition
            if (dragFrom == -1) {
                dragFrom = fromPosition
            }
            dragTo = toPosition
            this@DragDropTrackAdapter.notifyItemMoved(fromPosition, toPosition)
            return true
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            super.clearView(recyclerView, viewHolder)
            if (dragFrom != -1 && dragTo != -1 && dragFrom != dragTo) {
                onDragAndDropListener(dragFrom, dragTo)
            }
            dragFrom = -1
            dragTo = -1
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            Unit
        }

        override fun isLongPressDragEnabled(): Boolean {
            return false
        }
    }
}
