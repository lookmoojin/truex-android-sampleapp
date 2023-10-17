package com.truedigital.features.music.presentation.mylibrary.mymusic.viewholder

import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.music.domain.myplaylist.model.MusicMyPlaylistModel
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.databinding.ItemMyPlaylistBinding
import com.truedigital.foundation.extension.load

class MyPlaylistViewHolder(
    private val binding: ItemMyPlaylistBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(
        playlist: MusicMyPlaylistModel,
        onItemClick: (MusicMyPlaylistModel.MyPlaylistModel) -> Unit
    ) = with(binding) {
        if (playlist is MusicMyPlaylistModel.MyPlaylistModel) {
            with(playlist) {
                imageViewPlaylistCover.load(
                    context = binding.root.context,
                    url = coverImage,
                    placeholder = R.drawable.placeholder_new_trueid_white_square,
                    scaleType = ImageView.ScaleType.CENTER_CROP
                )
                textViewPlaylistName.text = playlist.title
                textViewSongCount.text =
                    binding.root.context.resources.getQuantityString(
                        R.plurals.playlist_number_of_tracks,
                        playlist.trackCount,
                        playlist.trackCount
                    )
            }
            this.root.setOnClickListener {
                onItemClick(playlist)
            }
        }
    }
}
