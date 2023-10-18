package com.truedigital.features.tuned.presentation.playlist.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.common.extensions.valueForSystemLanguage
import com.truedigital.features.tuned.data.download.ImageManager
import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.databinding.ItemLoadingEmptyBinding
import com.truedigital.features.tuned.databinding.ItemPlaylistBinding
import com.truedigital.foundation.extension.load
import com.truedigital.foundation.extension.onClick

class PlaylistAdapter(
    val imageManager: ImageManager,
    val onClickListener: (Playlist) -> Unit,
    val onPageLoadListener: (() -> Unit)? = null,
    val onLongClickListener: ((Playlist) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val ITEM_TYPE_LOADING = 0
        const val ITEM_TYPE_PLAYLIST = 1
    }

    var items: List<Playlist> = mutableListOf()
        set(value) {
            (items as MutableList).clear()
            (items as MutableList).addAll(value)
            notifyDataSetChanged()
        }

    private var hasPaging = onPageLoadListener != null
    var morePages = false

    override fun getItemViewType(position: Int): Int =
        when (position) {
            items.size -> ITEM_TYPE_LOADING
            else -> ITEM_TYPE_PLAYLIST
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is PlaylistViewHolder -> holder.bind(position)
            else -> onPageLoadListener?.invoke()
        }
    }

    override fun getItemCount() = items.size + if (morePages && hasPaging) 1 else 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ITEM_TYPE_PLAYLIST -> {
                PlaylistViewHolder(
                    ItemPlaylistBinding.inflate(
                        inflater
                    ),
                    onClickListener,
                    onLongClickListener
                )
            }

            else -> {
                LoadingViewHolder(ItemLoadingEmptyBinding.inflate(inflater, parent, false))
            }
        }
    }

    inner class PlaylistViewHolder(
        private val binding: ItemPlaylistBinding,
        onClickListener: (Playlist) -> Unit,
        onLongClickListener: ((Playlist) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.onClick { onClickListener(items[layoutPosition]) }
            binding.root.setOnLongClickListener {
                onLongClickListener?.invoke(items[layoutPosition])
                true
            }
        }

        fun bind(position: Int) = with(binding) {
            val playlist = items[position]

            val image = playlist.coverImage.valueForSystemLanguage(itemView.context)
            image?.let {
                playlistImageView.load(
                    context = binding.root.context,
                    url = it,
                    placeholder = R.drawable.placeholder_trueidwhite_square
                )
            }
            playlistNameTextView.text = playlist.name.valueForSystemLanguage(itemView.context)
            playlistDescriptionTextView.text = itemView.context.getString(
                R.string.playlist_description,
                playlist.trackCount
            )
        }
    }

    inner class LoadingViewHolder(binding: ItemLoadingEmptyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.layoutParams.width =
                itemView.resources.getDimensionPixelSize(R.dimen.playlist_image_size)
            binding.root.requestLayout()
        }
    }
}
