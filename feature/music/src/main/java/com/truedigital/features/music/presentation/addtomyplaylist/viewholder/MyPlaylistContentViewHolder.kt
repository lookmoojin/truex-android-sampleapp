package com.truedigital.features.music.presentation.addtomyplaylist.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.music.domain.myplaylist.model.MusicMyPlaylistModel
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.databinding.ItemMyPlaylistContentBinding
import com.truedigital.foundation.extension.load

class MyPlaylistContentViewHolder(
    val binding: ItemMyPlaylistContentBinding
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(
            parent: ViewGroup
        ): MyPlaylistContentViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = ItemMyPlaylistContentBinding.inflate(layoutInflater, parent, false)
            return MyPlaylistContentViewHolder(view)
        }
    }

    fun bind(data: MusicMyPlaylistModel, onAddToMyPlaylistClicked: (String?) -> Unit) =
        with(binding) {
            if (data is MusicMyPlaylistModel.MyPlaylistModel) {
                coverImageView.load(
                    context = binding.root.context,
                    url = data.coverImage,
                    placeholder = R.drawable.placeholder_new_trueid_white_square,
                    scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
                )
                playlistNameTextView.text = data.title
                itemView.setOnClickListener {
                    onAddToMyPlaylistClicked.invoke(data.playlistId.toString())
                }
            }
        }
}
