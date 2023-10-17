package com.truedigital.features.music.presentation.landing.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.music.domain.landing.model.MusicForYouItemModel
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.databinding.ItemMusicCircleForYouBinding
import com.truedigital.foundation.extension.load

class MusicForYouItemCircleViewHolder(
    val binding: ItemMusicCircleForYouBinding
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(parent: ViewGroup): MusicForYouItemCircleViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = ItemMusicCircleForYouBinding.inflate(layoutInflater, parent, false)
            return MusicForYouItemCircleViewHolder(view)
        }
    }

    fun bind(musicForYouItemModel: MusicForYouItemModel) = with(binding) {
        when (musicForYouItemModel) {
            is MusicForYouItemModel.ArtistShelfItem -> {
                thumbnailImageView.load(
                    context = binding.root.context,
                    url = musicForYouItemModel.coverImage,
                    placeholder = R.drawable.placeholder_new_trueid_white_square,
                    scaleType = ImageView.ScaleType.CENTER_CROP
                )
                artistTitleTextView.text = musicForYouItemModel.name
            }
            else -> Unit
        }
    }
}
