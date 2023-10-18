package com.truedigital.common.share.componentv3.widget.viewpageautoscroll.adapter.viewholder

import android.view.View
import com.truedigital.common.share.componentv3.databinding.ItemBannerDefaultBinding
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.domain.BannerBaseItemModel
import com.truedigital.foundation.extension.RESIZE_NONE
import com.truedigital.foundation.extension.load

class PermissionViewHolder(itemView: View) : AbstractBannerViewHolder(itemView) {
    val binding: ItemBannerDefaultBinding by lazy {
        ItemBannerDefaultBinding.bind(itemView)
    }

    override fun render(bannerItem: BannerBaseItemModel) {
        binding.apply {
            bannerImageView.transitionName = " - $bindingAdapterPosition}"
            if (bannerItem.thum.isNotEmpty()) {
                bannerImageView.load(
                    context = itemView.context,
                    url = bannerItem.thum,
                    resizeType = RESIZE_NONE
                )
            }
        }
    }
}
