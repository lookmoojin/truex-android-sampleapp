package com.truedigital.common.share.componentv3.widget.viewpageautoscroll.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.common.share.componentv3.R
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.adapter.viewholder.AbstractBannerViewHolder
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.adapter.viewholder.AdsBannerViewHolder
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.adapter.viewholder.BannerCornerRadiusTopViewHolder
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.adapter.viewholder.BannerViewHolder
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.adapter.viewholder.CustomInfiniteViewHolder
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.adapter.viewholder.MultipleSquareViewHolder
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.adapter.viewholder.PermissionViewHolder
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.domain.BannerBaseItemModel
import com.truedigital.foundation.extension.onClick

enum class BannerLoopType {
    TYPE_DEFAULT, TYPE_CORNER_RADIUS, TYPE_CORNER_RADIUS_TOP, TYPE_ADS_BANNER,
    TYPE_DEFAULT_NO_CARD_VIEW, TYPE_DEFAULT_PERMISSION, TYPE_MULTIPLE_SQUARE, TYPE_CUSTOM_INFINITE
}

class BannerLoopItemAdapter(private val listItem: MutableList<BannerBaseItemModel>) :
    RecyclerView.Adapter<AbstractBannerViewHolder>() {

    var onItemClicked: ((index: Int) -> Unit)? = null
    private var bannerLoopType: BannerLoopType = BannerLoopType.TYPE_DEFAULT
    private val tempShelfId = mutableListOf<String>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AbstractBannerViewHolder {
        return when (bannerLoopType) {
            BannerLoopType.TYPE_CORNER_RADIUS -> BannerCornerRadiusTopViewHolder(
                LayoutInflater.from(
                    parent.context
                ).inflate(R.layout.item_banner_corner_radius, parent, false)
            )
            BannerLoopType.TYPE_CORNER_RADIUS_TOP -> BannerCornerRadiusTopViewHolder(
                LayoutInflater.from(
                    parent.context
                ).inflate(R.layout.item_banner_corner_radius_top, parent, false)
            )
            BannerLoopType.TYPE_ADS_BANNER -> AdsBannerViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_banner, parent, false
                )
            )
            BannerLoopType.TYPE_DEFAULT_NO_CARD_VIEW -> BannerViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_banner_default, parent, false)
            )
            BannerLoopType.TYPE_DEFAULT_PERMISSION -> PermissionViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_banner_default, parent, false)
            )
            BannerLoopType.TYPE_MULTIPLE_SQUARE -> MultipleSquareViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_banner_multiple_square, parent, false)
            )
            BannerLoopType.TYPE_CUSTOM_INFINITE -> CustomInfiniteViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_custom_infinite_viewpager, parent, false)
            )
            else -> BannerViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.item_banner, parent, false)
            )
        }
    }

    override fun getItemCount(): Int {
        return if (listItem.size > 1) {
            Int.MAX_VALUE
        } else {
            listItem.size
        }
    }

    override fun onBindViewHolder(holder: AbstractBannerViewHolder, position: Int) {
        val index = if (listItem.size > 1) {
            position % listItem.size
        } else {
            position
        }
        val item = listItem[index]
        holder.render(item)
        holder.itemView.contentDescription = item.contentDescription
        holder.itemView.onClick {
            onItemClicked?.invoke(index)
        }
    }

    fun setBannerLoopType(bannerLoopType: BannerLoopType) {
        this.bannerLoopType = bannerLoopType
    }

    fun setItemBanner(list: MutableList<BannerBaseItemModel>) {
        listItem.clear()
        listItem.addAll(list)
    }

    fun getItemSize(): Int {
        return listItem.size
    }

    fun addTempCmsId(position: Int) {
        tempShelfId.add(listItem[position].id)
    }

    fun checkTempCmsId(position: Int): Boolean {
        val checkCmsId = listItem[position].id
        return !tempShelfId.contains(checkCmsId)
    }
}
