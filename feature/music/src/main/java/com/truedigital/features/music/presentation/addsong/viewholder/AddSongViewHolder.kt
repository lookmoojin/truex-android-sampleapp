package com.truedigital.features.music.presentation.addsong.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.listens.share.constant.MusicConstant
import com.truedigital.features.music.domain.addsong.model.MusicSearchResultModel
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.databinding.ItemSearchSongBinding
import com.truedigital.foundation.extension.clickAsFlow
import com.truedigital.foundation.extension.load
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber

class AddSongViewHolder(
    val binding: ItemSearchSongBinding
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(
            parent: ViewGroup
        ): AddSongViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = ItemSearchSongBinding.inflate(layoutInflater, parent, false)
            return AddSongViewHolder(view)
        }
    }

    fun bind(data: MusicSearchResultModel, onAddSongClicked: (Int?) -> Unit) =
        with(binding) {
            coverImageView.load(
                context = binding.root.context,
                url = data.coverImage,
                placeholder = R.drawable.placeholder_new_trueid_white_square,
                scaleType = android.widget.ImageView.ScaleType.CENTER_CROP
            )
            songNameTextView.text = data.songName
            artistNameTextView.text = data.artistName
            addSongImageView.isSelected = data.isSelected
            addSongImageView.clickAsFlow()
                .catch { Timber.e(it) }
                .debounce(com.truedigital.features.listens.share.constant.MusicConstant.DelayTime.DELAY_500)
                .onEach {
                    onAddSongClicked.invoke(data.id)
                }.launchIn(MainScope())
        }
}
