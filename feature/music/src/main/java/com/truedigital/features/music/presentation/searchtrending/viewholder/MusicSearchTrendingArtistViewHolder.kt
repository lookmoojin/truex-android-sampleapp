package com.truedigital.features.music.presentation.searchtrending.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.music.domain.trending.model.TrendingArtistModel
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.databinding.ItemTrendingArtistBinding
import com.truedigital.foundation.extension.load
import com.truedigital.foundation.extension.onClick

class MusicSearchTrendingArtistViewHolder(
    val binding: ItemTrendingArtistBinding,
    private val onTrendingArtistClicked: (id: Int?) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(
            parent: ViewGroup,
            onTrendingArtistClick: (id: Int?) -> Unit
        ): MusicSearchTrendingArtistViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = ItemTrendingArtistBinding.inflate(layoutInflater, parent, false)
            return MusicSearchTrendingArtistViewHolder(view, onTrendingArtistClick)
        }
    }

    fun bind(data: TrendingArtistModel) = with(binding) {
        artistNameTextView.text = data.name
        artistImageView.load(
            context = binding.root.context,
            url = data.image,
            placeholder = R.drawable.placeholder_new_trueid_white_square,
            scaleType = ImageView.ScaleType.CENTER_CROP
        )
        itemView.onClick {
            onTrendingArtistClicked.invoke(data.id)
        }
    }
}
