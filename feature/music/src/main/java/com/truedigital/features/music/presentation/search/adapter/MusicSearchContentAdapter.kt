package com.truedigital.features.music.presentation.search.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.newrelic.agent.android.NewRelic
import com.truedigital.features.music.domain.search.model.MusicSearchModel
import com.truedigital.features.music.presentation.search.viewholder.MusicSearchHeaderViewHolder
import com.truedigital.features.music.presentation.search.viewholder.MusicSearchItemViewHolder
import com.truedigital.features.tuned.databinding.ViewholderMusicSeachHeaderBinding
import com.truedigital.features.tuned.databinding.ViewholderMusicSeachItemBinding

class MusicSearchContentAdapter :
    PagedListAdapter<MusicSearchModel, RecyclerView.ViewHolder>(diffItem()) {

    companion object {
        private const val VIEW_HEADER = 1
        private const val VIEW_ITEM = 2

        fun diffItem() = object : DiffUtil.ItemCallback<MusicSearchModel>() {
            override fun areItemsTheSame(
                oldItem: MusicSearchModel,
                newItem: MusicSearchModel
            ): Boolean {
                return when {
                    oldItem is MusicSearchModel.MusicItemModel && newItem is MusicSearchModel.MusicItemModel -> {
                        oldItem.id == newItem.id
                    }
                    oldItem is MusicSearchModel.MusicHeaderModel && newItem is MusicSearchModel.MusicHeaderModel -> {
                        oldItem.id == newItem.id
                    }
                    else -> {
                        false
                    }
                }
            }

            override fun areContentsTheSame(
                oldItem: MusicSearchModel,
                newItem: MusicSearchModel
            ): Boolean {
                return when {
                    oldItem is MusicSearchModel.MusicItemModel && newItem is MusicSearchModel.MusicItemModel -> {
                        oldItem.id == newItem.id &&
                            oldItem.description == newItem.description &&
                            oldItem.title == newItem.title &&
                            oldItem.thumb == newItem.thumb &&
                            oldItem.type == newItem.type
                    }
                    oldItem is MusicSearchModel.MusicHeaderModel && newItem is MusicSearchModel.MusicHeaderModel -> {
                        oldItem.id == newItem.id &&
                            oldItem.title == newItem.title &&
                            oldItem.type == newItem.type
                    }
                    else -> false
                }
            }
        }
    }

    var onMusicClicked: ((MusicSearchModel) -> Unit)? = null

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is MusicSearchModel.MusicHeaderModel -> {
                VIEW_HEADER
            }
            else -> {
                VIEW_ITEM
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            VIEW_HEADER -> {
                val binding = ViewholderMusicSeachHeaderBinding.inflate(inflater, parent, false)
                MusicSearchHeaderViewHolder(binding, onMusicClicked)
            }
            VIEW_ITEM -> {
                val binding = ViewholderMusicSeachItemBinding.inflate(inflater, parent, false)
                MusicSearchItemViewHolder(binding, onMusicClicked)
            }
            else -> {
                NewRelic.recordHandledException(
                    Exception("Invalid view type $viewType isn't supported in MusicSearchContentAdapter")
                )
                throw Exception("view type $viewType isn't supported in MusicSearchContentAdapter")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder.itemViewType) {
            VIEW_HEADER -> {
                (holder as? MusicSearchHeaderViewHolder)?.apply {
                    (getItem(position) as? MusicSearchModel.MusicHeaderModel)?.let {
                        bind(it)
                    }
                }
            }
            VIEW_ITEM -> {
                (holder as? MusicSearchItemViewHolder)?.apply {
                    (getItem(position) as? MusicSearchModel.MusicItemModel)?.let {
                        bind(it)
                    }
                }
            }
        }
    }
}
