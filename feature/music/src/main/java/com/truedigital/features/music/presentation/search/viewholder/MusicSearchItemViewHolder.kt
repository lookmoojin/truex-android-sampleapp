package com.truedigital.features.music.presentation.search.viewholder

import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.music.domain.search.model.MusicSearchModel
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.databinding.ViewholderMusicSeachItemBinding
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.load
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible

class MusicSearchItemViewHolder(
    private val binding: ViewholderMusicSeachItemBinding,
    private val onMusicClicked: ((MusicSearchModel) -> Unit)?
) : RecyclerView.ViewHolder(binding.root) {

    private var itemModel: MusicSearchModel.MusicItemModel? = null

    init {
        binding.root.onClick {
            itemModel?.let {
                onMusicClicked?.invoke(it)
            }
        }
    }

    fun bind(item: MusicSearchModel.MusicItemModel?) = with(binding) {
        itemModel = item

        musicSearchItemTitleTextView.text = item?.title
        musicSearchItemDescriptionTextView.text = item?.description

        when (item?.description.isNullOrEmpty()) {
            true -> musicSearchItemDescriptionTextView.gone()
            false -> musicSearchItemDescriptionTextView.visible()
        }

        musicSearchItemIconImageView.load(
            root.context,
            item?.thumb,
            R.drawable.placeholder_new_trueid_white_square,
            ImageView.ScaleType.CENTER_CROP
        )

        item?.musicTheme?.let { themeModel ->
            musicSearchItemTitleTextView.setTextColor(
                ContextCompat.getColor(itemView.context, themeModel.textTitleColor)
            )
            musicSearchItemDescriptionTextView.setTextColor(
                ContextCompat.getColor(itemView.context, themeModel.textDescriptionColor)
            )
            musicSearchItemDivider.background = ContextCompat.getDrawable(
                binding.root.context,
                themeModel.dividerColor
            )
        }
    }
}
