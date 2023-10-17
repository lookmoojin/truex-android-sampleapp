package com.truedigital.features.music.presentation.searchtrending.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.music.domain.trending.model.TrendingPlaylistModel
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.databinding.ItemMusicSquareBinding
import com.truedigital.foundation.extension.load
import com.truedigital.foundation.extension.onClick

class MusicSearchTrendingPlaylistViewHolder(
    val binding: ItemMusicSquareBinding,
    private val onTrendingPlaylistClicked: (id: Int?) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(
            parent: ViewGroup,
            onTrendingPlaylistClicked: (id: Int?) -> Unit
        ): MusicSearchTrendingPlaylistViewHolder {
            val view =
                ItemMusicSquareBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return MusicSearchTrendingPlaylistViewHolder(view, onTrendingPlaylistClicked)
        }
    }

    fun bind(data: TrendingPlaylistModel) = with(binding) {
        nameTextView.text = data.name
        coverImageView.load(
            context = binding.root.context,
            url = data.image,
            placeholder = R.drawable.placeholder_new_trueid_white_square,
            scaleType = ImageView.ScaleType.CENTER_CROP
        )
        itemView.onClick {
            onTrendingPlaylistClicked.invoke(data.id)
        }
    }
}
