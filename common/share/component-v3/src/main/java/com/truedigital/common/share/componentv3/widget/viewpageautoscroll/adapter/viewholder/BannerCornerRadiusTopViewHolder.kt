package com.truedigital.common.share.componentv3.widget.viewpageautoscroll.adapter.viewholder

import android.view.View
import com.truedigital.common.share.componentv3.R
import com.truedigital.common.share.componentv3.databinding.ItemBannerCornerRadiusTopBinding
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.domain.BannerBaseItemModel
import com.truedigital.foundation.extension.RESIZE_LARGE
import com.truedigital.foundation.extension.load

class BannerCornerRadiusTopViewHolder(itemView: View) : AbstractBannerViewHolder(itemView) {
    val binding: ItemBannerCornerRadiusTopBinding by lazy {
        ItemBannerCornerRadiusTopBinding.bind(itemView)
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
