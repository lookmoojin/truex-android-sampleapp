package com.truedigital.features.tuned.presentation.album.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.download.ImageManager
import com.truedigital.features.tuned.databinding.ItemAlbumBinding
import com.truedigital.features.tuned.databinding.ItemLoadingEmptyBinding
import com.truedigital.foundation.extension.load
import com.truedigital.foundation.extension.onClick

class AlbumAdapter(
    val imageManager: ImageManager,
    val showDetailedDescription: Boolean,
    val onPageLoadListener: (() -> Unit)? = null,
    val onClickListener: (Album) -> Unit,
    val onLongClickListener: ((Album) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val ITEM_TYPE_LOADING = 0
        const val ITEM_TYPE_ALBUM = 1
    }

    var items: List<Album> = mutableListOf()
        set(value) {
            (items as MutableList).clear()
            (items as MutableList).addAll(value)
            notifyDataSetChanged()
        }

    private var hasPaging = onPageLoadListener != null
    var morePages = false

    override fun getItemViewType(position: Int): Int = if (position == items.size) {
        ITEM_TYPE_LOADING
    } else {
        ITEM_TYPE_ALBUM
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is AlbumViewHolder) {
            with(holder) {
                val album = items[position]

                binding.albumImageView.setImageResource(R.drawable.placeholder_album)
                album.primaryRelease?.image?.let {
                    binding.albumImageView.load(
                        context = binding.root.context,
                        url = it,
                        placeholder = R.drawable.placeholder_trueidwhite_square
                    )
                }

                binding.albumNameTextView.text = album.primaryRelease?.name

                if (showDetailedDescription) {
                    var detailedDescription = holder.itemView.context.resources.getQuantityString(
                        R.plurals.number_of_synced_track_title,
                        album.primaryRelease?.trackIds?.size
                            ?: 0,
                        album.primaryRelease?.trackIds?.size
                    )
                    album.primaryRelease?.releaseYear()
                        ?.let { detailedDescription += " • $it" }
                    binding.albumDescriptionTextView.text = detailedDescription
                } else {
                    binding.albumDescriptionTextView.text =
                        album.primaryRelease?.getArtistString(
                            holder.itemView.context.getString(R.string.various_artists_title)
                        )
                }
            }
        } else {
            onPageLoadListener?.invoke()
        }
    }

    override fun getItemCount() = items.size + if (morePages && hasPaging) 1 else 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        when (viewType) {
            ITEM_TYPE_ALBUM -> {
                val view =
                    ItemAlbumBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                AlbumViewHolder(view, onClickListener, onLongClickListener)
            }

            else -> {
                val view =
                    ItemLoadingEmptyBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                LoadingViewHolder(view)
            }
        }

    inner class AlbumViewHolder(
        val binding: ItemAlbumBinding,
        onClickListener: (Album) -> Unit,
        onLongClickListener: ((Album) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.onClick {
                onClickListener(items[layoutPosition])
            }
            itemView.setOnLongClickListener {
                onLongClickListener?.invoke(items[layoutPosition])
                true
            }
        }
    }

    inner class LoadingViewHolder(val binding: ItemLoadingEmptyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.layoutParams.width =
                itemView.resources.getDimensionPixelSize(R.dimen.album_image_size)
            itemView.requestLayout()
        }
    }
}
