package com.truedigital.features.music.presentation.landing.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.music.domain.landing.model.MusicForYouItemModel
import com.truedigital.features.music.domain.landing.model.MusicForYouShelfModel
import com.truedigital.features.music.domain.landing.model.MusicLandingFASelectContentModel
import com.truedigital.features.tuned.databinding.ViewMusicHeroBannerBinding

class MusicForYouHeroBannerViewHolder(
    val binding: ViewMusicHeroBannerBinding,
    private val onItemClicked: (MusicForYouItemModel, MusicLandingFASelectContentModel) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(
            parent: ViewGroup,
            onItemClicked: (MusicForYouItemModel, MusicLandingFASelectContentModel) -> Unit
        ): MusicForYouHeroBannerViewHolder {
            return MusicForYouHeroBannerViewHolder(
                ViewMusicHeroBannerBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ),
                onItemClicked
            )
        }
    }

    fun bind(musicForYouShelfModel: MusicForYouShelfModel) = with(binding) {
        musicHeroBannerWidget.setupBannerList(musicForYouShelfModel.itemList)
        musicHeroBannerWidget.onItemClick = { musicForYouItem, itemIndex ->
            musicForYouItem?.let { itemModel ->
                onItemClicked.invoke(
                    itemModel,
                    MusicLandingFASelectContentModel(
                        shelfName = musicForYouShelfModel.titleFA,
                        shelfIndex = musicForYouShelfModel.shelfIndex,
                        itemIndex = itemIndex
                    )
                )
            }
        }
    }
}
