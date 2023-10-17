package com.truedigital.features.music.presentation.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.truedigital.features.music.domain.search.model.TopMenuModel
import com.truedigital.features.music.presentation.search.viewholder.MusicSearchTopMenuItemViewHolder
import com.truedigital.features.tuned.databinding.ViewholderMusicTopMenuItemBinding

class MusicSearchTopMenuAdapter :
    ListAdapter<TopMenuModel, MusicSearchTopMenuItemViewHolder>(diffItem()) {

    companion object {
        private fun diffItem(): DiffUtil.ItemCallback<TopMenuModel> {
            return object : DiffUtil.ItemCallback<TopMenuModel>() {
                override fun areItemsTheSame(
                    oldItem: TopMenuModel,
                    newItem: TopMenuModel
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(
                    oldItem: TopMenuModel,
                    newItem: TopMenuModel
                ): Boolean {
                    return oldItem.id == newItem.id &&
                        oldItem.isActive == newItem.isActive &&
                        oldItem.nameEn == newItem.nameEn &&
                        oldItem.name == newItem.name &&
                        oldItem.type == newItem.type
                }
            }
        }
    }

    var onSearchTopMenuClicked: ((TopMenuModel, Int) -> Unit)? = null
    private var currentActivePosition = 0

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MusicSearchTopMenuItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ViewholderMusicTopMenuItemBinding.inflate(inflater, parent, false)
        return MusicSearchTopMenuItemViewHolder(binding, onSearchTopMenuClicked)
    }

    override fun onBindViewHolder(holder: MusicSearchTopMenuItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun updateActivePosition(newPosition: Int, onUpdatePosition: (() -> Unit)? = null) {

        if (newPosition != currentActivePosition && newPosition >= 0) {
            for (i in 0 until itemCount) {
                val item = getItem(i)
                item.isActive = false
            }

            val currentItem = getItem(newPosition)
            currentItem.isActive = true
            currentActivePosition = newPosition

            notifyDataSetChanged()
            onUpdatePosition?.invoke()
        }
    }
}
