package com.truedigital.features.music.presentation.landing.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.music.domain.landing.model.MusicForYouItemModel
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.databinding.ItemMusicSquareGridBinding
import com.truedigital.foundation.extension.load

class MusicContentItemSquareGridViewHolder(
    val binding: ItemMusicSquareGridBinding
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(parent: ViewGroup): MusicContentItemSquareGridViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = ItemMusicSquareGridBinding.inflate(layoutInflater, parent, false)
            return MusicContentItemSquareGridViewHolder(view)
        }
    }

    fun bind(data: MusicForYouItemModel) = with(binding) {
        when (data) {
            is MusicForYouItemModel.RadioShelfItem -> {
                thumbnailImageView.load(
                    context = binding.root.context,
                    url = data.thumbnail,
                    placeholder = R.drawable.placeholder_new_trueid_white_square,
                    scaleType = ImageView.ScaleType.CENTER_CROP
                )
                titleTextView.text = data.title
            }
            else -> Unit
        }
    }
}
