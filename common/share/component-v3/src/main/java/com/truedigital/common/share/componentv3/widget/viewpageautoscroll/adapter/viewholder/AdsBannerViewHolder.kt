package com.truedigital.common.share.componentv3.widget.viewpageautoscroll.adapter.viewholder

import android.view.View
import com.truedigital.common.share.componentv3.R
import com.truedigital.common.share.componentv3.databinding.ItemBannerBinding
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.domain.BannerBaseItemModel
import com.truedigital.foundation.extension.RESIZE_LARGE
import com.truedigital.foundation.extension.load

class AdsBannerViewHolder(itemView: View) : AbstractBannerViewHolder(itemView) {
    val binding: ItemBannerBinding by lazy {
        ItemBannerBinding.bind(itemView)
    }

    override fun render(bannerItem: BannerBaseItemModel) {
        binding.apply {
            bannerImageView.transitionName = " - $bindingAdapterPosition}"
            bannerImageView.load(
                context = itemView.context,
                url = bannerItem.thum,
                placeholder = R.mipmap.placeholder_trueidwhite_horizontal,
                resizeType = RESIZE_LARGE
            )
        }
    }
}
