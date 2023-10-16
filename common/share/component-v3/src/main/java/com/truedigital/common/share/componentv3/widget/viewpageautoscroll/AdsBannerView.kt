package com.truedigital.common.share.componentv3.widget.viewpageautoscroll

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.adapter.BannerLoopType
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.domain.BannerBaseItemModel
import com.truedigital.component.databinding.WidgetViewpagerLoopAutoScrollBinding

class AdsBannerView : ViewPagerLoopAndAutoScroll {

    private val widgetViewpagerLoopAutoScrollBinding: WidgetViewpagerLoopAutoScrollBinding by lazy {
        WidgetViewpagerLoopAutoScrollBinding.inflate(LayoutInflater.from(context), this, false)
    }

    constructor(context: Context) : super(context) {
        initInflater()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initInflater()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initInflater()
    }

    var onItemClick: ((bannerItemModel: BannerBaseItemModel, index: Int) -> Unit)? = null
    private var bannerItemModel: List<BannerBaseItemModel>? = null

    private fun initInflater() {
        removeAllViews()
        this.onBannerClicked = { index ->
            onItemClick?.invoke(getBannerAtIndex(index), index)
        }
        addView(widgetViewpagerLoopAutoScrollBinding.root)
        initView(widgetViewpagerLoopAutoScrollBinding.root)
    }

    fun setupBannerList(bannerItemModel: MutableList<BannerBaseItemModel>) {
        this.bannerItemModel = bannerItemModel
        setupBannerAutoScroll(
            listItem = bannerItemModel.toMutableList(),
            width = 16,
            height = 9,
            intervalTime = 6000,
            bannerLoopType = BannerLoopType.TYPE_DEFAULT
        )
    }

    private fun getBannerAtIndex(index: Int): BannerBaseItemModel {
        return bannerItemModel?.getOrNull(index) ?: BannerBaseItemModel()
    }
}
