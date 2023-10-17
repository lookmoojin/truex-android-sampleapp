package com.truedigital.features.music.presentation.landing.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.features.music.domain.landing.model.MusicForYouItemModel
import com.truedigital.features.tuned.databinding.ItemMusicAdsBannerForYouBinding
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.visible

class MusicForYouAdsBannerViewHolder(
    val binding: ItemMusicAdsBannerForYouBinding
) : RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(parent: ViewGroup): MusicForYouAdsBannerViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = ItemMusicAdsBannerForYouBinding.inflate(layoutInflater, parent, false)
            return MusicForYouAdsBannerViewHolder(view)
        }
    }

    fun bind(musicForYouItemModel: MusicForYouItemModel?) = with(binding) {
        when (musicForYouItemModel) {
            is MusicForYouItemModel.AdsBannerShelfItem -> {
                musicForYouItemModel.adsId?.let { adsId ->
                    musicAdsBannerWidget.visible()
                    musicAdsBannerWidget.render(
                        adsId,
                        musicForYouItemModel.mobileSize.orEmpty(),
                        musicForYouItemModel.tabletSize.orEmpty(),
                    )
                } ?: run {
                    musicAdsBannerWidget.gone()
                }
            }
            else -> {
                musicAdsBannerWidget.gone()
            }
        }
    }
}
