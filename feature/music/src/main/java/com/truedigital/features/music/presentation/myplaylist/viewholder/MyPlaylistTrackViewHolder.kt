package com.truedigital.features.music.presentation.myplaylist.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.databinding.ItemMusicSongBinding
import com.truedigital.foundation.extension.load
import com.truedigital.foundation.extension.visibleOrGone

class MyPlaylistTrackViewHolder(
    val binding: ItemMusicSongBinding
) : RecyclerView.ViewHolder(binding.root) {
    companion object {
        fun from(
            parent: ViewGroup
        ): MyPlaylistTrackViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = ItemMusicSongBinding.inflate(layoutInflater, parent, false)
            return MyPlaylistTrackViewHolder(view)
        }
    }

    fun bind(track: Track, onSeeMoreTrackClicked: (Track) -> Unit) = with(binding) {
        playingTrackImageView.visibleOrGone(track.isPlayingTrack)
        coverImageView.load(
            context = binding.root.context,
            url = track.image,
            placeholder = com.truedigital.features.tuned.R.drawable.placeholder_new_trueid_white_square,
            scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
        )
        songNameTextView.text = track.name
        artistNameTextView.text = track.artists.firstOrNull()?.name
        moreImageView.setOnClickListener {
            onSeeMoreTrackClicked.invoke(track)
        }
    }
}
