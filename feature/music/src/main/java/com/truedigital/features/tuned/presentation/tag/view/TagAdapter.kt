package com.truedigital.features.tuned.presentation.tag.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.common.Constants
import com.truedigital.features.tuned.common.extensions.valueForSystemLanguage
import com.truedigital.features.tuned.data.download.ImageManager
import com.truedigital.features.tuned.data.tag.model.Tag
import com.truedigital.features.tuned.databinding.ItemMusicLoadingBinding
import com.truedigital.features.tuned.databinding.ItemTagBinding
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible

class TagAdapter(
    val imageManager: ImageManager,
    val tagWidth: Int = Constants.UNINITIALIZED,
    val tagHeight: Int = Constants.UNINITIALIZED,
    val displayTitle: Boolean? = true,
    val onPageLoadListener: (() -> Unit)? = null,
    val onClickListener: (Tag) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val ITEM_TYPE_LOADING = 0
        const val ITEM_TYPE_TAG = 1
    }

    var items: List<Tag> = mutableListOf()
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
        ITEM_TYPE_TAG
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is TagViewHolder) {
            val tag = items[position]
            holder.bind(tag)
        } else {
            onPageLoadListener?.let { it() }
        }
    }

    override fun getItemCount() = items.size + if (morePages && hasPaging) 1 else 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            ITEM_TYPE_TAG -> {
                TagViewHolder(
                    ItemTagBinding.inflate(
                        inflater,
                        parent,
                        false
                    ),
                    onClickListener
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

    inner class TagViewHolder(private val binding: ItemTagBinding, onClickListener: (Tag) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.onClick { onClickListener(items[layoutPosition]) }
        }

        fun bind(tag: Tag) = with(binding) {

            val lp = flTag.layoutParams
            lp.width = tagWidth
            lp.height = tagHeight
            val cornerSize = root.resources.getDimensionPixelSize(R.dimen.tag_corner_size)

            tagImageView.setImageResource(R.drawable.music_bg_tag)
            (tag.images?.valueForSystemLanguage(root.context) ?: tag.image)?.let {
                imageManager.init(root)
                    .load(it)
                    .options(tagWidth, tagHeight)
                    .intoRoundedCorner(tagImageView, cornerSize)
            }
            if (displayTitle == true) {
                tagOverlayImageView.visible()
                tagNameTextView.visible()
                tagNameTextView.text = tag.displayName.valueForSystemLanguage(root.context)
            } else {
                tagOverlayImageView.gone()
                tagNameTextView.gone()
            }
        }
    }

    inner class LoadingViewHolder(binding: ItemMusicLoadingBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.layoutParams.width =
                itemView.resources.getDimensionPixelSize(R.dimen.tag_image_width)
            binding.root.requestLayout()
        }
    }
}
