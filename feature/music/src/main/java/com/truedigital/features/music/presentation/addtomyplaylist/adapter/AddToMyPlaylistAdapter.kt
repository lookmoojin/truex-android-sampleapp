package com.truedigital.features.music.presentation.addtomyplaylist.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.newrelic.agent.android.NewRelic
import com.truedigital.features.music.domain.myplaylist.model.MusicMyPlaylistModel
import com.truedigital.features.music.presentation.addtomyplaylist.viewholder.MyPlaylistContentViewHolder
import com.truedigital.features.music.presentation.addtomyplaylist.viewholder.MyPlaylistCreateViewHolder
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class AddToMyPlaylistAdapter @AssistedInject constructor(
    @Assisted("onCreateMyPlaylistClicked") private val onCreateMyPlaylistClicked: () -> Unit,
    @Assisted("onAddToMyPlaylistClicked") private val onAddToMyPlaylistClicked: (String?) -> Unit
) : ListAdapter<MusicMyPlaylistModel, RecyclerView.ViewHolder>(AddToMyPlaylistDiffCallback()) {

    companion object {
        private const val CREATE_MY_PLAYLIST = 1
        private const val MY_PLAYLIST = 2
    }

    @AssistedFactory
    interface AddToMyPlaylistAdapterFactory {
        fun create(
            @Assisted("onCreateMyPlaylistClicked") onCreateMyPlaylistClicked: () -> Unit,
            @Assisted("onAddToMyPlaylistClicked") onAddToMyPlaylistClicked: (String?) -> Unit
        ): AddToMyPlaylistAdapter
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is MusicMyPlaylistModel.CreateMyPlaylistModel -> CREATE_MY_PLAYLIST
            is MusicMyPlaylistModel.MyPlaylistModel -> MY_PLAYLIST
            else -> {
                NewRelic.recordHandledException(
                    IllegalArgumentException("Unknown item position $position in AddToMyPlaylistAdapter")
                )
                throw IllegalArgumentException("Unknown item position $position in AddToMyPlaylistAdapter")
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CREATE_MY_PLAYLIST -> MyPlaylistCreateViewHolder.from(parent)
            MY_PLAYLIST -> MyPlaylistContentViewHolder.from(parent)
            else -> {
                NewRelic.recordHandledException(
                    IllegalArgumentException("Invalid view type $viewType isn't supported in AddToMyPlaylistAdapter")
                )
                throw IllegalArgumentException("view type $viewType isn't supported in AddToMyPlaylistAdapter")
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MyPlaylistContentViewHolder -> {
                holder.bind(getItem(position), onAddToMyPlaylistClicked)
            }
            is MyPlaylistCreateViewHolder -> {
                holder.itemView.setOnClickListener {
                    onCreateMyPlaylistClicked.invoke()
                }
            }
        }
    }

    class AddToMyPlaylistDiffCallback : DiffUtil.ItemCallback<MusicMyPlaylistModel>() {
        override fun areItemsTheSame(
            oldItem: MusicMyPlaylistModel,
            newItem: MusicMyPlaylistModel
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: MusicMyPlaylistModel,
            newItem: MusicMyPlaylistModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}
