package com.truedigital.common.share.componentv3.widget.viewpageautoscroll

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.truedigital.common.share.componentv3.databinding.WidgetViewpagerMultipleSquareBinding
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.adapter.BannerLoopType
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.domain.BannerBaseItemModel

class MultipleSquareViewPager : ViewPagerLoopAndAutoScroll {

    companion object {
        private const val DEFAULT_BANNER_WIDTH_HEIGHT = 1
    }
    private val widgetViewpagerMultipleSquareBinding: WidgetViewpagerMultipleSquareBinding by lazy {
        WidgetViewpagerMultipleSquareBinding.inflate(LayoutInflater.from(context), this, false)
    }
    var onItemClick: ((bannerItemModel: BannerBaseItemModel, index: Int) -> Unit)? = null
    private var bannerItemModel: List<BannerBaseItemModel>? = null

    constructor(context: Context) : super(context) {
        initInflater()
    }

    private fun initInflater() {
        removeAllViews()
        this.onBannerClicked = { index ->
            onItemClick?.invoke(getBannerAtIndex(index), index)
        }
        addView(widgetViewpagerMultipleSquareBinding.root)
        initView(widgetViewpagerMultipleSquareBinding.root)
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

    fun setupBannerList(
        bannerItemModel: MutableList<BannerBaseItemModel>,
        width: Int = DEFAULT_BANNER_WIDTH_HEIGHT,
        height: Int = DEFAULT_BANNER_WIDTH_HEIGHT
    ) {
        this.bannerItemModel = bannerItemModel
        setupBannerAutoScroll(
            listItem = bannerItemModel,
            width = width, height = height,
            bannerLoopType = BannerLoopType.TYPE_MULTIPLE_SQUARE
        )
    }

    private fun getBannerAtIndex(index: Int): BannerBaseItemModel {
        return bannerItemModel?.getOrNull(index) ?: BannerBaseItemModel()
    }
}
