package com.truedigital.features.music.presentation.search.viewholder

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.music.domain.search.model.MusicSearchModel
import com.truedigital.features.tuned.databinding.ViewholderMusicSeachHeaderBinding
import com.truedigital.foundation.extension.onClick

class MusicSearchHeaderViewHolder(
    private val binding: ViewholderMusicSeachHeaderBinding,
    private val onMusicClicked: ((MusicSearchModel) -> Unit)?
) : RecyclerView.ViewHolder(binding.root) {

    private var itemModel: MusicSearchModel.MusicHeaderModel? = null

    init {
        binding.musicSearchHeaderSeeAllTextView.onClick {
            itemModel?.let {
                onMusicClicked?.invoke(it)
            }
        }
    }

    fun bind(item: MusicSearchModel.MusicHeaderModel?) = with(binding) {
        itemModel = item

        musicSearchHeaderTitleTextView.text = item?.title

        item?.textHeaderColor?.let { color ->
            musicSearchHeaderTitleTextView.setTextColor(
                ContextCompat.getColor(itemView.context, color),
            )
        }
        item?.textSeemoreColor?.let { color ->
            musicSearchHeaderSeeAllTextView.setTextColor(
                ContextCompat.getColor(itemView.context, color),
            )
        }
    }
}
