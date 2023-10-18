package com.truedigital.features.tuned.presentation.track

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.common.Constants.FLOAT_0_5F
import com.truedigital.features.tuned.common.Constants.FLOAT_16F
import com.truedigital.features.tuned.common.Constants.FLOAT_32F
import com.truedigital.features.tuned.common.extensions.dp
import com.truedigital.features.tuned.common.extensions.toast
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.databinding.ItemDiskHeaderBinding
import com.truedigital.features.tuned.databinding.ItemLoadingEmptyBinding
import com.truedigital.features.tuned.databinding.ItemTrackBinding
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible
import com.truedigital.foundation.extension.visibleOrGone

class TrackAdapter(
    val context: Context,
    val onClickListener: (Track, Int) -> Unit,
    val onLongClickListener: (Track) -> Unit = {},
    val onMoreSelectedListener: (Track) -> Unit = {},
    val onPageLoadListener: (() -> Unit)? = null,
    val numItemsToDisplay: Int? = null,
    val useVolumeIndex: Boolean = false
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    init {
        if (onPageLoadListener != null && numItemsToDisplay != null)
            throw IllegalStateException("cannot have paging and limit display together")
    }

    companion object {
        private const val ITEM_TYPE_HEADING = 0
        private const val ITEM_TYPE_TRACK = 1
        private const val ITEM_TYPE_LOADING = 2
        private const val ITEM_BG_FADE_DURATION = 10000L
    }

    var hasMoreThanOneVolume = false
    var currentPlayingTrackId = -1
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    private var shouldBeAnimated = true
    var selectedTrackId = 0
        set(value) {
            field = value
            shouldBeAnimated = true
        }

    private val hasPaging = onPageLoadListener != null
    var morePages = false

    /**
     * @property items The list of items for this adapter, should only be initialised with Track objects,
     *                  otherwise an exception is thrown
     * @throws ClassCastException
     */
    var items: List<Any> = mutableListOf()
        set(value) {
            value.forEach {
                if (it::class != Track::class) {
                    throw ClassCastException("items must be of type List<Track> when set externally")
                }
                // cannot have volume and paging together
                if ((it as Track).volumeNumber > 1 && !hasPaging) {
                    hasMoreThanOneVolume = true
                }
            }
            (items as MutableList).clear()
            (items as MutableList).addAll(value)
            if (hasMoreThanOneVolume && useVolumeIndex) {
                var previousVolumeIndex = 0
                var i = 0
                while (i < items.size) {
                    val track = items[i] as Track
                    if (track.volumeNumber != previousVolumeIndex) {
                        (items as MutableList<Any>).add(
                            i,
                            context.getString(R.string.disc_title, track.volumeNumber)
                        )
                        previousVolumeIndex = track.volumeNumber
                        i++
                    }
                    i++
                }
            }
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ITEM_TYPE_LOADING ->
                LoadingViewHolder(
                    ItemLoadingEmptyBinding.inflate(
                        inflater,
                        parent,
                        false
                    )
                )

            ITEM_TYPE_HEADING ->
                HeadingViewHolder(
                    ItemDiskHeaderBinding.inflate(
                        inflater,
                        parent,
                        false
                    )
                )

            else ->
                TrackViewHolder(
                    ItemTrackBinding.inflate(
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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is LoadingViewHolder -> onPageLoadListener?.invoke()
            is TrackViewHolder -> holder.bind(items[position] as Track, position)
            is HeadingViewHolder -> holder.bind(items[position] as String, position)
        }
    }

    private fun getTrackNumberInVolume(position: Int): Int {
        var count = 1
        while (items[position - count] !is String) {
            count++
            if (position < count) break
        }
        return count
    }

    private fun fadeItemViewBackgroundColor(itemView: View, startColor: Int, endColor: Int) {
        val fadeColor = ObjectAnimator.ofObject(
            itemView,
            "backgroundColor",
            ArgbEvaluator(),
            startColor,
            endColor
        )
        fadeColor.duration = ITEM_BG_FADE_DURATION
        fadeColor.start()
        shouldBeAnimated = false
    }

    override fun getItemViewType(position: Int): Int =
        if (position == items.size)
            ITEM_TYPE_LOADING
        else
            when (items[position]) {
                is Track -> ITEM_TYPE_TRACK
                else -> ITEM_TYPE_HEADING
            }

    override fun getItemCount(): Int {
        val size = items.size + if (morePages && hasPaging) 1 else 0
        return if (numItemsToDisplay != null && size > numItemsToDisplay) numItemsToDisplay else size
    }

    fun getItemPosition(trackId: Int) = items.indexOfFirst { it is Track && it.id == trackId }

    fun updateDownloadStatus(position: Int, currTrackProgress: Long, currTrackSize: Long) {
        val track = items[position] as Track
        if (currTrackProgress == currTrackSize) {
            track.isDownloaded = true
            track.isCached = true
            notifyItemChanged(position)
        }
    }

    inner class HeadingViewHolder(private val binding: ItemDiskHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String, position: Int) = with(binding) {
            val params = tvTitle.layoutParams as LinearLayout.LayoutParams
            params.topMargin =
                if (position == 0) {
                    0
                } else {
                    root.resources.getDimensionPixelSize(R.dimen.dynamic_content_heading_top_spacing)
                }
            tvTitle.layoutParams = params
            tvTitle.text = item
        }
    }

    inner class LoadingViewHolder(binding: ItemLoadingEmptyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            binding.root.requestLayout()
        }
    }

    inner class TrackViewHolder(
        private val binding: ItemTrackBinding,
        onClickListener: (Track, Int) -> Unit,
        onLongClickListener: (Track) -> Unit,
        onMoreSelectedListener: (Track) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            with(binding) {
                val content = root.context
                root.onClick {
                    val item = items[layoutPosition] as Track
                    if (item.allowStream) onClickListener(item, layoutPosition)
                    else content.toast(R.string.track_not_available_message)
                }
                root.setOnLongClickListener {
                    val item = items[layoutPosition] as Track
                    if (item.allowStream) onLongClickListener(item)
                    else content.toast(R.string.track_not_available_message)
                    true
                }
                btnMore.onClick {
                    val item = items[layoutPosition] as Track
                    if (item.allowStream) onMoreSelectedListener(item)
                    else content.toast(R.string.track_not_available_message)
                }
            }
        }

        fun bind(track: Track, position: Int) = with(binding) {
            setTextValue(track, position)
            setVisibility(track)

            val hasLyrics = track.hasLyrics
            val hasVideo = track.video != null || track.isVideo
            handlerDisplayLyricsAndVideo(hasLyrics, hasVideo)

            setBackgroundItem(track.id)
        }

        private fun getIndexToDisplay(position: Int): String {
            return if (hasMoreThanOneVolume && useVolumeIndex) {
                getTrackNumberInVolume(position).toString()
            } else {
                (position + 1).toString()
            }
        }

        private fun setTextValue(track: Track, position: Int) = with(binding) {
            root.alpha = if (track.allowStream) 1f else FLOAT_0_5F
            textViewIndex.text = getIndexToDisplay(position)
            textViewName.text = track.name
            textViewSingleName.text = track.name
            textViewDuration.text = track.formattedDuration

            // Reset view to default XML state
            reset()

            textViewArtist.text = track.getArtistString(
                context.getString(R.string.various_artists_title)
            )
        }

        private fun reset() = with(binding) {
            imageViewExplicit.gone()
            imageViewOffline.gone()
            layoutDetailsContainer.visible()
            textViewSingleName.gone()
            textViewArtist.visible()
        }

        private fun setVisibility(track: Track) = with(binding) {
            imageViewExplicit.visibleOrGone(show = track.isExplicit)
            imageViewOffline.visibleOrGone(show = track.isCached)
            textViewIndex.visibleOrGone(show = track.id != currentPlayingTrackId)
            ivCurrentTrack.visibleOrGone(show = track.id == currentPlayingTrackId)
        }

        private fun handlerDisplayLyricsAndVideo(hasLyrics: Boolean, hasVideo: Boolean) {
            with(binding) {
                val nameLp = textViewName.layoutParams as FrameLayout.LayoutParams
                when {
                    hasLyrics && hasVideo -> {
                        nameLp.marginEnd = context.resources.dp(FLOAT_32F)
                        val videoLp = imageViewVideo.layoutParams as FrameLayout.LayoutParams
                        videoLp.marginEnd = context.resources.dp(FLOAT_16F)
                        imageViewLyrics.visible()
                        imageViewVideo.visible()
                    }

                    hasLyrics || hasVideo -> {
                        nameLp.marginEnd = context.resources.dp(FLOAT_16F)
                        imageViewLyrics.visibleOrGone(show = hasLyrics)
                        imageViewVideo.visibleOrGone(show = hasVideo)
                    }

                    else -> {
                        nameLp.marginEnd = 0
                        imageViewLyrics.gone()
                        imageViewVideo.gone()
                    }
                }
            }
        }

        private fun setBackgroundItem(trackId: Int) = with(binding) {
            if (selectedTrackId == trackId) {
                root.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
                if (shouldBeAnimated) {
                    fadeItemViewBackgroundColor(
                        root,
                        ContextCompat.getColor(context, R.color.primary_transparent),
                        ContextCompat.getColor(context, R.color.content_background)
                    )
                }
            } else {
                root.setBackgroundColor(ContextCompat.getColor(context, R.color.white))
            }
        }
    }
}
