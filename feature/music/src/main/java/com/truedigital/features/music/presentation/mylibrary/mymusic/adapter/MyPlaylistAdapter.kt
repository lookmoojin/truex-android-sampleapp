package com.truedigital.features.music.presentation.mylibrary.mymusic.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.music.domain.myplaylist.model.MusicMyPlaylistModel
import com.truedigital.features.music.presentation.mylibrary.mymusic.viewholder.MyPlaylistCreateViewHolder
import com.truedigital.features.music.presentation.mylibrary.mymusic.viewholder.MyPlaylistViewHolder
import com.truedigital.features.tuned.databinding.ItemAddNewListBinding
import com.truedigital.features.tuned.databinding.ItemMyPlaylistBinding

class MyPlaylistAdapter(
    private val onCreateNewClick: () -> Unit,
    private val onItemClick: (MusicMyPlaylistModel.MyPlaylistModel) -> Unit
) : ListAdapter<MusicMyPlaylistModel, RecyclerView.ViewHolder>(MusicMyPlaylistDiffCallback()) {

    companion object {
        private const val CREATE_PLAYLIST = 101
        private const val MY_PLAYLIST = 102
    }

    fun update(data: List<MusicMyPlaylistModel>? = null) {
        val musicMyPlaylist = mutableListOf<MusicMyPlaylistModel>()
        musicMyPlaylist.add(MusicMyPlaylistModel.CreateMyPlaylistModel())
        data?.let {
            musicMyPlaylist.addAll(it)
        }

        submitList(musicMyPlaylist)
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is MusicMyPlaylistModel.CreateMyPlaylistModel -> CREATE_PLAYLIST
            else -> MY_PLAYLIST
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            CREATE_PLAYLIST -> MyPlaylistCreateViewHolder(
                ItemAddNewListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
            else -> MyPlaylistViewHolder(
                ItemMyPlaylistBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MyPlaylistCreateViewHolder -> holder.bind(onCreateNewClick)
            is MyPlaylistViewHolder -> holder.bind(getItem(position), onItemClick)
        }
    }

    class MusicMyPlaylistDiffCallback : DiffUtil.ItemCallback<MusicMyPlaylistModel>() {
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
