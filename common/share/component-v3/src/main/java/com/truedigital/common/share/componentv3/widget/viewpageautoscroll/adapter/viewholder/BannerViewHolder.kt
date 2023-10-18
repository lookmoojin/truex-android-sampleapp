package com.truedigital.common.share.componentv3.widget.viewpageautoscroll.adapter.viewholder

import android.view.View
import androidx.core.content.ContextCompat
import com.truedigital.common.share.componentv3.R
import com.truedigital.common.share.componentv3.databinding.ItemBannerDefaultBinding
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.domain.BannerBaseItemModel
import com.truedigital.foundation.extension.RESIZE_LARGE
import com.truedigital.foundation.extension.RESIZE_NONE
import com.truedigital.foundation.extension.load

class BannerViewHolder(itemView: View) : AbstractBannerViewHolder(itemView) {

    val binding: ItemBannerDefaultBinding by lazy {
        ItemBannerDefaultBinding.bind(itemView)
    }

    override fun render(bannerItem: BannerBaseItemModel) {
        binding.apply {
            bannerImageView.transitionName = " - $bindingAdapterPosition}"
            bannerImageView.contentDescription = bannerItem.imageContentDescription

            if (bannerItem.thum.isEmpty() && bannerItem.thumResource != null) {
                bannerItem.thumResource?.let {
                    bannerImageView.setImageDrawable(
                        ContextCompat.getDrawable(
                            itemView.context,
                            it
                        )
                    )
                }
            } else {
                checkTutorial(bannerItem)
            }
        }
    }

    private fun ItemBannerDefaultBinding.checkTutorial(
        bannerItem: BannerBaseItemModel
    ) {
        if (bannerItem.isTutorial) {
            bannerImageView.load(
                context = itemView.context,
                url = bannerItem.thum,
                resizeType = RESIZE_NONE

            )
        } else {
            bannerImageView.load(
                context = itemView.context,
                url = bannerItem.thum,
                placeholder = R.mipmap.placeholder_trueidwhite_horizontal,
                resizeType = RESIZE_LARGE
            )
        }
    }
}
