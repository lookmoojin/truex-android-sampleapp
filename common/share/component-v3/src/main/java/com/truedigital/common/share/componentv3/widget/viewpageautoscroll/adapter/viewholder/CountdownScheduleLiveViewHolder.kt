package com.truedigital.common.share.componentv3.widget.viewpageautoscroll.adapter.viewholder

import android.view.View
import com.truedigital.common.share.componentv3.R
import com.truedigital.common.share.componentv3.databinding.ItemCountdownScheduleLiveBinding
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.domain.BannerBaseItemModel
import com.truedigital.foundation.extension.RESIZE_LARGE
import com.truedigital.foundation.extension.load
import com.truedigital.foundation.extension.visible

class CountdownScheduleLiveViewHolder(itemView: View) : AbstractBannerViewHolder(itemView) {

    val binding: ItemCountdownScheduleLiveBinding by lazy {
        ItemCountdownScheduleLiveBinding.bind(itemView)
    }

    override fun render(bannerItem: BannerBaseItemModel) {
        val resource = binding.root.context.resources
        val isTablet = resource.getBoolean(R.bool.is_tablet)
        binding.apply {
            if (isTablet) parentView.setPadding(0, 0, 0, 0)
            bannerImageView.transitionName = " - $bindingAdapterPosition}"
            bannerImageView.contentDescription = bannerItem.imageContentDescription

            bannerImageView.load(
                context = itemView.context,
                url = bannerItem.thum,
                placeholder = bannerItem.sportCountdownModel.defaultBackground,
                resizeType = RESIZE_LARGE
            )

            liveBadgeImageView.apply {
                visible()
                load(
                    context = itemView.context,
                    url = bannerItem.sportCountdownModel.liveBadge,
                    placeholder = bannerItem.sportCountdownModel.defaultLiveBadge,
                    resizeType = RESIZE_LARGE
                )
            }
            playerHomeImageView.apply {
                visible()
                load(
                    context = itemView.context,
                    url = bannerItem.sportCountdownModel.imagePlayerHome,
                    resizeType = RESIZE_LARGE
                )
            }
            playerAwayImageView.apply {
                visible()
                load(
                    context = itemView.context,
                    url = bannerItem.sportCountdownModel.imagePlayerAway,
                    resizeType = RESIZE_LARGE
                )
            }
            liveChannelImageView.apply {
                visible()
                load(
                    context = itemView.context,
                    url = bannerItem.sportCountdownModel.channelImage,
                    resizeType = RESIZE_LARGE
                )
            }
            homeLogoImageView.apply {
                visible()
                load(
                    context = itemView.context,
                    url = bannerItem.sportCountdownModel.homeLogo,
                    resizeType = RESIZE_LARGE
                )
            }
            awayLogoImageView.apply {
                visible()
                load(
                    context = itemView.context,
                    url = bannerItem.sportCountdownModel.awayLogo,
                    resizeType = RESIZE_LARGE
                )
            }
            homeTeamTextView.apply {
                visible()
                text = bannerItem.sportCountdownModel.homeAka
            }
            awayTeamTextView.apply {
                visible()
                text = bannerItem.sportCountdownModel.awayAka
            }
            matchDateTextView.apply {
                visible()
                text = bannerItem.sportCountdownModel.matchDate
            }
        }
    }
}
