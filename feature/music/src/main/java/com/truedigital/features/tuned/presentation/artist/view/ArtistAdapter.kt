package com.truedigital.features.tuned.presentation.artist.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.download.ImageManager
import com.truedigital.features.tuned.databinding.ItemArtistBinding
import com.truedigital.features.tuned.databinding.ItemLoadingEmptyBinding
import com.truedigital.foundation.extension.load
import com.truedigital.foundation.extension.onClick

class ArtistAdapter(
    val imageManager: ImageManager,
    val onPageLoadListener: (() -> Unit)? = null,
    val onClickListener: (Artist) -> Unit,
    val onLongClickListener: ((Artist) -> Unit)? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val ITEM_TYPE_LOADING = 0
        const val ITEM_TYPE_ARTIST = 1
    }

    var items: List<Artist> = mutableListOf()
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
        ITEM_TYPE_ARTIST
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            ITEM_TYPE_ARTIST -> {
                val view =
                    ItemArtistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ArtistViewHolder(view, onClickListener, onLongClickListener)
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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ArtistViewHolder) {
            val artist = items[position]

            with(holder) {
                binding.artistName.text = artist.name
                binding.artistImage.setImageResource(R.drawable.placeholder_artist)
                artist.image?.let {
                    binding.artistImage.load(
                        context = binding.root.context,
                        url = it,
                        placeholder = R.drawable.placeholder_trueidwhite_square
                    )
                }
            }
        } else {
            onPageLoadListener?.invoke()
        }
    }

    override fun getItemCount(): Int = items.size + if (morePages && hasPaging) 1 else 0

    inner class ArtistViewHolder(
        val binding: ItemArtistBinding,
        onClickListener: (Artist) -> Unit,
        onLongClickListener: ((Artist) -> Unit)?
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
                itemView.resources.getDimensionPixelSize(R.dimen.artist_image_size)
            itemView.requestLayout()
        }
    }
}
