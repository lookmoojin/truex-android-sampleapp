package com.truedigital.common.share.componentv3.widget.viewpageautoscroll.adapter.viewholder

import android.view.View
import com.truedigital.common.share.componentv3.R
import com.truedigital.common.share.componentv3.databinding.ItemCountdownHighlightBinding
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.domain.BannerBaseItemModel
import com.truedigital.foundation.extension.RESIZE_LARGE
import com.truedigital.foundation.extension.load

class CountdownHighlightViewHolder(itemView: View) : AbstractBannerViewHolder(itemView) {

    val binding: ItemCountdownHighlightBinding by lazy {
        ItemCountdownHighlightBinding.bind(itemView)
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
                placeholder = R.mipmap.placeholder_trueidwhite_horizontal,
                resizeType = RESIZE_LARGE
            )
        }
    }
}
