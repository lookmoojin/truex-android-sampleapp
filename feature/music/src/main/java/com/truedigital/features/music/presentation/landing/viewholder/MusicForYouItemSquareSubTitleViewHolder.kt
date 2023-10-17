package com.truedigital.features.music.presentation.landing.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.music.domain.landing.model.MusicForYouItemModel
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.databinding.ItemMusicSquareSubTitleForYouBinding
import com.truedigital.foundation.extension.load

class MusicForYouItemSquareSubTitleViewHolder(
    val binding: ItemMusicSquareSubTitleForYouBinding
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(parent: ViewGroup): MusicForYouItemSquareSubTitleViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = ItemMusicSquareSubTitleForYouBinding.inflate(layoutInflater, parent, false)
            return MusicForYouItemSquareSubTitleViewHolder(view)
        }
    }

    fun bind(musicForYouItemModel: MusicForYouItemModel) = with(binding) {
        when (musicForYouItemModel) {
            is MusicForYouItemModel.AlbumShelfItem -> {
                thumbnailImageView.load(
                    context = binding.root.context,
                    url = musicForYouItemModel.coverImage,
                    placeholder = R.drawable.placeholder_new_trueid_white_square,
                    scaleType = ImageView.ScaleType.CENTER_CROP
                )
                titleTextView.text = musicForYouItemModel.albumName
                subTitleTextView.text = musicForYouItemModel.artistName
            }
            else -> Unit
        }
    }
}
