package com.truedigital.features.music.presentation.landing.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.music.domain.landing.model.MusicForYouItemModel
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.databinding.ItemMusicSongForYouBinding
import com.truedigital.foundation.extension.load

class MusicForYouItemGridViewHolder(
    val binding: ItemMusicSongForYouBinding
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(parent: ViewGroup): MusicForYouItemGridViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = ItemMusicSongForYouBinding.inflate(layoutInflater, parent, false)
            return MusicForYouItemGridViewHolder(view)
        }
    }

    fun bind(musicForYouItemModel: MusicForYouItemModel) = with(binding) {
        when (musicForYouItemModel) {
            is MusicForYouItemModel.TrackPlaylistShelf -> {
                positionTextView.apply {
                    isVisible = musicForYouItemModel.position?.isNotEmpty() ?: false
                    text = musicForYouItemModel.position
                }
                artistNameTextView.text = musicForYouItemModel.artist
                songNameTextView.text = musicForYouItemModel.name
                coverImageView.load(
                    context = root.context,
                    url = musicForYouItemModel.coverImage,
                    placeholder = R.drawable.placeholder_new_trueid_white_square,
                    scaleType = ImageView.ScaleType.CENTER_CROP
                )
            }
            else -> Unit
        }
    }
}
