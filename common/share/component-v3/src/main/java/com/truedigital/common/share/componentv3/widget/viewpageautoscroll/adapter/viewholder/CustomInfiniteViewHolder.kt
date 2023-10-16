package com.truedigital.common.share.componentv3.widget.viewpageautoscroll.adapter.viewholder

import android.view.View
import com.truedigital.common.share.componentv3.R
import com.truedigital.common.share.componentv3.databinding.ItemCustomInfiniteViewpagerBinding
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.domain.BannerBaseItemModel
import com.truedigital.foundation.extension.RESIZE_LARGE
import com.truedigital.foundation.extension.load

class CustomInfiniteViewHolder(itemView: View) : AbstractBannerViewHolder(itemView) {

    val binding: ItemCustomInfiniteViewpagerBinding by lazy {
        ItemCustomInfiniteViewpagerBinding.bind(itemView)
    }

    override fun render(bannerItem: BannerBaseItemModel) {
        binding.apply {
            itemAdsImageView.transitionName = " - $bindingAdapterPosition}"
            itemAdsImageView.load(
                context = itemView.context,
                url = bannerItem.thum,
                placeholder = R.mipmap.placeholder_trueidwhite_horizontal,
                resizeType = RESIZE_LARGE
            )
        }
    }
}
