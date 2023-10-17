package com.truedigital.features.music.presentation.searchtrending.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.music.domain.trending.model.TrendingAlbumModel
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.databinding.ItemMusicSquareBinding
import com.truedigital.foundation.extension.load
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible

class MusicSearchTrendingAlbumViewHolder(
    val binding: ItemMusicSquareBinding,
    private val onTrendingAlbumClicked: (id: Int?) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(
            parent: ViewGroup,
            onTrendingAlbumClicked: (id: Int?) -> Unit
        ): MusicSearchTrendingAlbumViewHolder {
            val view =
                ItemMusicSquareBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return MusicSearchTrendingAlbumViewHolder(view, onTrendingAlbumClicked)
        }
    }

    fun bind(data: TrendingAlbumModel) = with(binding) {
        nameTextView.text = data.name
        descriptionTextView.visible()
        descriptionTextView.text = data.artistName
        coverImageView.load(
            context = binding.root.context,
            url = data.image,
            placeholder = R.drawable.placeholder_new_trueid_white_square,
            scaleType = ImageView.ScaleType.CENTER_CROP
        )
        itemView.onClick {
            onTrendingAlbumClicked.invoke(data.id)
        }
    }
}
