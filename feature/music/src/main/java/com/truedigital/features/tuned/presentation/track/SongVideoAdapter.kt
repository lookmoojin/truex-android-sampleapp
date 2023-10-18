package com.truedigital.features.tuned.presentation.track

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.common.Constants.FLOAT_0_5F
import com.truedigital.features.tuned.common.extensions.toast
import com.truedigital.features.tuned.data.download.ImageManager
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.databinding.ItemMusicLoadingBinding
import com.truedigital.features.tuned.databinding.ItemSongBinding
import com.truedigital.features.tuned.databinding.ItemVideoBinding
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible

class SongVideoAdapter(
    val imageManager: ImageManager,
    val onPageLoadListener: (() -> Unit)? = null,
    val onClickListener: (Track) -> Unit,
    val onLongClickListener: ((Track) -> Unit)? = null
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val ITEM_TYPE_LOADING = 0
        const val ITEM_TYPE_VIDEO = 1
        const val ITEM_TYPE_SONG = 2
    }

    var items: List<Track> = mutableListOf()
        set(value) {
            (items as MutableList).clear()
            (items as MutableList).addAll(value)
            notifyDataSetChanged()
        }

    private var hasPaging = onPageLoadListener != null
    var morePages = false

    override fun getItemViewType(position: Int): Int =
        when {
            position == items.size -> ITEM_TYPE_LOADING
            items[position].isVideo -> ITEM_TYPE_VIDEO
            else -> ITEM_TYPE_SONG
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ITEM_TYPE_SONG -> {
                SongViewHolder(
                    ItemSongBinding.inflate(
                        inflater,
                        parent,
                        false
                    ),
                    onClickListener,
                    onLongClickListener
                )
            }

            ITEM_TYPE_VIDEO -> {
                VideoViewHolder(
                    ItemVideoBinding.inflate(
                        inflater,
                        parent,
                        false
                    ),
                    onClickListener,
                    onLongClickListener
                )
            }

            else -> {
                LoadingViewHolder(
                    ItemMusicLoadingBinding.inflate(
                        inflater,
                        parent,
                        false
                    )
                )
            }
        }
    }

    override fun getItemCount() = items.size + if (morePages && hasPaging) 1 else 0

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is VideoViewHolder -> holder.bind(items[position], holder.itemView)
            is SongViewHolder -> holder.bind(items[position], holder.itemView)
            else -> onPageLoadListener?.invoke()
        }
    }

    inner class VideoViewHolder(
        private val binding: ItemVideoBinding,
        onClickListener: (Track) -> Unit,
        onLongClickListener: ((Track) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.onClick {
                val item = items[layoutPosition]
                if (item.allowStream) onClickListener(item)
                else binding.root.context.toast(R.string.track_not_available_message)
            }
            binding.root.setOnLongClickListener {
                val item = items[layoutPosition]
                if (item.allowStream) onLongClickListener?.invoke(item)
                else binding.root.context.toast(R.string.track_not_available_message)
                true
            }
        }

        fun bind(video: Track, itemView: View) = with(binding) {
            root.alpha = if (video.allowStream) 1f else FLOAT_0_5F
            val imageSize = root.resources.getDimensionPixelSize(R.dimen.song_video_image_size)
            videoImageView.setImageResource(R.drawable.placeholder_video)
            videoPlayImageView.gone()
            imageManager.init(root)
                .load(video.image)
                .options(imageSize)
                .intoRoundedCorner(videoImageView) {
                    if (it) videoPlayImageView.visible()
                }

            videoName.text = video.name
            videoArtistName.text =
                video.getArtistString(itemView.context.getString(R.string.various_artists_title))
        }
    }

    inner class SongViewHolder(
        private val binding: ItemSongBinding,
        onClickListener: (Track) -> Unit,
        onLongClickListener: ((Track) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            with(binding) {
                val context = root.context
                root.onClick {
                    val item = items[layoutPosition]
                    if (item.allowStream) onClickListener(item)
                    else context.toast(R.string.track_not_available_message)
                }
                root.setOnLongClickListener {
                    val item = items[layoutPosition]
                    if (item.allowStream) onLongClickListener?.invoke(item)
                    else context.toast(R.string.track_not_available_message)
                    true
                }
            }
        }

        fun bind(song: Track, itemView: View) = with(binding) {
            root.alpha = if (song.allowStream) 1f else FLOAT_0_5F
            val imageSize = itemView.resources.getDimensionPixelSize(R.dimen.song_video_image_size)
            songImageView.setImageResource(R.drawable.placeholder_song)
            songPlayImageView.gone()
            imageManager.init(itemView)
                .load(song.image)
                .options(imageSize)
                .intoRoundedCorner(songImageView) {
                    if (it) songPlayImageView.visible()
                }

            songNameTextView.text = song.name
            songArtistNameTextView.text =
                song.getArtistString(itemView.context.getString(R.string.various_artists_title))
        }
    }

    inner class LoadingViewHolder(binding: ItemMusicLoadingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.layoutParams.width =
                itemView.resources.getDimensionPixelSize(R.dimen.song_video_image_size)
            binding.root.requestLayout()
        }
    }
}
