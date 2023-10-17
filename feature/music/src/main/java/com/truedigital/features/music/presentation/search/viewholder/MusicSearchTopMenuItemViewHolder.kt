package com.truedigital.features.music.presentation.search.viewholder

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.music.domain.search.model.TopMenuModel
import com.truedigital.features.tuned.databinding.ViewholderMusicTopMenuItemBinding
import com.truedigital.foundation.extension.onClick

class MusicSearchTopMenuItemViewHolder(
    private val binding: ViewholderMusicTopMenuItemBinding,
    onSearchTopMenuClicked: ((TopMenuModel, Int) -> Unit)?
) : RecyclerView.ViewHolder(binding.root) {

    private var itemModel: TopMenuModel? = null

    init {
        binding.titleTextView.onClick {
            itemModel?.let { searchItemModel ->
                onSearchTopMenuClicked?.invoke(searchItemModel, bindingAdapterPosition)
            }
        }
    }

    fun bind(item: TopMenuModel?) = with(binding) {
        itemModel = item
        titleTextView.text = item?.name.orEmpty()
        item?.let {
            val textColorRes = if (it.isActive) it.textActiveColor else it.textInactiveColor
            val textBackgroundRes =
                if (it.isActive) it.buttonActiveDrawable else it.buttonInactiveDrawable

            titleTextView.setTextColor(
                ContextCompat.getColor(itemView.context, textColorRes)
            )
            titleTextView.setBackgroundResource(textBackgroundRes)
        }
    }
}
