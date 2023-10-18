package com.truedigital.features.music.presentation.myplaylist.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.music.domain.myplaylist.model.MyPlaylistModel
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.databinding.ItemHeaderMyPlaylistBinding
import com.truedigital.foundation.extension.load

class MyPlaylistHeaderViewHolder(
    val binding: ItemHeaderMyPlaylistBinding
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(
            parent: ViewGroup
        ): MyPlaylistHeaderViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = ItemHeaderMyPlaylistBinding.inflate(layoutInflater, parent, false)
            return MyPlaylistHeaderViewHolder(view)
        }
    }

    fun bind(
        myPlaylist: MyPlaylistModel,
        onShuffleClicked: () -> Unit,
        onAddSongClicked: () -> Unit
    ) = with(binding) {
        shuffleButton.isEnabled = myPlaylist.isTrackNotEmpty
        playlistNameTextView.text = myPlaylist.playlistName
        coverImageView.load(
            context = binding.root.context,
            url = myPlaylist.coverImage,
            placeholder = R.drawable.placeholder_new_trueid_white_square,
            scaleType = ImageView.ScaleType.CENTER_CROP
        )

        shuffleButton.setOnClickListener {
            onShuffleClicked.invoke()
        }
        addMyPlaylistButton.setOnClickListener {
            onAddSongClicked.invoke()
        }
    }
}
