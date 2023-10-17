package com.truedigital.features.music.widget.herobanner

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.ViewPagerLoopAndAutoScroll
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.adapter.BannerLoopType
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.domain.BannerBaseItemModel
import com.truedigital.component.databinding.WidgetViewpagerLoopAutoScrollBinding
import com.truedigital.core.extensions.ifNotEmpty
import com.truedigital.features.music.domain.landing.model.MusicForYouItemModel
import com.truedigital.features.tuned.R
import com.truedigital.foundation.extension.gone

class MusicHeroBannerWidget : ViewPagerLoopAndAutoScroll {

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

    private val widgetViewpagerLoopAutoScrollBinding: WidgetViewpagerLoopAutoScrollBinding by lazy {
        WidgetViewpagerLoopAutoScrollBinding.inflate(LayoutInflater.from(context), this, false)
    }
    private var bannerBaseList: List<MusicForYouItemModel>? = null
    var onItemClick: ((model: MusicForYouItemModel?, itemIndex: Int) -> Unit)? = null

    private fun initInflater() {
        removeAllViews()
        this.onBannerClicked = { index ->
            onItemClick?.invoke(getBannerAtIndex(index), index)
        }
        addView(widgetViewpagerLoopAutoScrollBinding.root)
        initView(widgetViewpagerLoopAutoScrollBinding.root)
        widgetViewpagerLoopAutoScrollBinding.pagerIndicatorTabLayout.gone()
    }

    fun setupBannerList(bannerList: List<MusicForYouItemModel>) {
        bannerBaseList = bannerList
        bannerList.map {
            BannerBaseItemModel().apply {
                thum = (it as MusicForYouItemModel.MusicHeroBannerShelfItem).coverImage.orEmpty()
                contentDescription = context.getString(R.string.music_for_you_item_hero_banner)
                imageContentDescription =
                    context.getString(R.string.music_for_you_imageview_hero_banner)
            }
        }.ifNotEmpty { list ->
            setupBannerAutoScroll(
                listItem = list.toMutableList(),
                width = 16,
                height = 9,
                intervalTime = 6000L,
                bannerLoopType = BannerLoopType.TYPE_DEFAULT
            )
        }
    }

    private fun getBannerAtIndex(index: Int): MusicForYouItemModel? {
        return bannerBaseList?.getOrNull(index)
    }
}
