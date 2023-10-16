package com.truedigital.common.share.componentv3.widget.viewpageautoscroll.adapter.viewholder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.domain.BannerBaseItemModel

abstract class AbstractBannerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun render(bannerItem: BannerBaseItemModel)
}
