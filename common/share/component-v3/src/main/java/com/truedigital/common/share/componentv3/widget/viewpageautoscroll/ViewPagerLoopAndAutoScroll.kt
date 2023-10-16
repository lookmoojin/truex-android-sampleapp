package com.truedigital.common.share.componentv3.widget.viewpageautoscroll

import android.content.Context
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_IDLE
import com.google.android.material.tabs.TabLayout
import com.truedigital.common.share.componentv3.R
import com.truedigital.common.share.componentv3.databinding.WidgetViewpagerLoopAutoScrollBinding
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.adapter.BannerLoopItemAdapter
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.adapter.BannerLoopType
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.domain.BannerBaseItemModel
import com.truedigital.foundation.extension.gone

open class ViewPagerLoopAndAutoScroll : ConstraintLayout {

    constructor(context: Context) : super(context) {
        initInflateDefault()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initInflateDefault()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initInflateDefault()
    }

    private val DEFAULT_TIME_BANNER = 3000L
    private val DEFAULT_BANNER_WIDTH = 16
    private val DEFAULT_BANNER_HIGHT = 9
    private val STAGE_NOT_SCROLL = 0L

    private var adapter: BannerLoopItemAdapter? = null
    private var scrollPosition = 0
    private var interval = 0L
    private var timer: CountDownTimer? = null
    private var viewPager: ViewPager2? = null
    private var pagerIndicatorTabLayout: TabLayout? = null
    private val widgetViewpagerLoopAutoScrollBinding: WidgetViewpagerLoopAutoScrollBinding by lazy {
        WidgetViewpagerLoopAutoScrollBinding.inflate(LayoutInflater.from(context), this, false)
    }

    var onBannerClicked: ((index: Int) -> Unit)? = null
    var onBannerScroll: ((index: Int) -> Unit)? = null

    fun setupBannerAutoScroll(
        listItem: MutableList<BannerBaseItemModel>,
        width: Int = DEFAULT_BANNER_WIDTH,
        height: Int = DEFAULT_BANNER_HIGHT,
        intervalTime: Long = DEFAULT_TIME_BANNER,
        bannerLoopType: BannerLoopType = BannerLoopType.TYPE_MULTIPLE_SQUARE
    ) {
        setBannerType(bannerLoopType)
        setBannerItem(listItem)
        setRatioViewPager(width, height)
        startAutoScroll(intervalTime)
    }

    fun setupBannerNotScrollBanner(
        listItem: MutableList<BannerBaseItemModel>,
        width: Int = DEFAULT_BANNER_WIDTH,
        height: Int = DEFAULT_BANNER_HIGHT,
        bannerLoopType: BannerLoopType = BannerLoopType.TYPE_DEFAULT
    ) {
        setBannerType(bannerLoopType)
        setBannerItem(listItem)
        setRatioViewPager(width, height)
        startAutoScroll(STAGE_NOT_SCROLL)
    }

    fun setupBannerNotScrollBannerNotRatio(
        listItem: MutableList<BannerBaseItemModel>,
        bannerLoopType: BannerLoopType = BannerLoopType.TYPE_DEFAULT
    ) {
        setBannerType(bannerLoopType)
        setBannerItem(listItem)
        startAutoScroll(STAGE_NOT_SCROLL)
    }

    fun setupBannerAutoScrollNotRatio(
        listItem: MutableList<BannerBaseItemModel>,
        bannerLoopType: BannerLoopType = BannerLoopType.TYPE_DEFAULT,
        intervalTime: Long = DEFAULT_TIME_BANNER,
    ) {
        setBannerType(bannerLoopType)
        setBannerItem(listItem)
        startAutoScroll(intervalTime)
    }

    fun getPositionScroll(): Int {
        return adapter?.let { _adapter -> scrollPosition % _adapter.getItemSize() } ?: 0
    }

    fun setBannerPosition(position: Int) {
        adapter?.let { _adapter ->
            val viewPagerLastIndex = _adapter.itemCount - 1
            if (viewPagerLastIndex > RecyclerView.NO_POSITION && position <= viewPagerLastIndex) {
                viewPager?.setCurrentItem(position, false)
            }
        }
    }

    private fun setBannerType(bannerLoopType: BannerLoopType) {
        adapter?.setBannerLoopType(bannerLoopType)
    }

    private fun setBannerItem(listItem: MutableList<BannerBaseItemModel>) {
        adapter?.setItemBanner(listItem)
        adapter?.notifyDataSetChanged()
        viewPager?.setCurrentItem(getIndexOfCenter(), false)
        setPageIndicator()
    }

    private fun setRatioViewPager(width: Int, height: Int) {
        val layoutParams =
            viewPager?.layoutParams as? LayoutParams
        layoutParams?.dimensionRatio = "H,$width:$height"
    }

    private fun startAutoScroll(intervalTime: Long) {
        interval = intervalTime
        timer = getTimer()
        timer?.start()
    }

    private fun initInflateDefault() {
        removeAllViews()
        addView(widgetViewpagerLoopAutoScrollBinding.root)
        initView(widgetViewpagerLoopAutoScrollBinding.root)
    }

    fun initView(view: View) {
        adapter = BannerLoopItemAdapter(mutableListOf())
        adapter?.apply {
            onItemClicked = { index ->
                onBannerClicked?.invoke(index)
            }
        }
        viewPager = view.findViewById(R.id.viewPager)
        pagerIndicatorTabLayout = view.findViewById(R.id.pagerIndicatorTabLayout)
        viewPager?.adapter = adapter
        viewPager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                scrollPosition = position
                if (position == 0 || position == Int.MAX_VALUE - 1) {
                    viewPager?.setCurrentItem(getIndexOfCenter(), false)
                }
                adapter?.let { _adapter ->
                    val checkItemPosition = position % _adapter.getItemSize()
                    if (_adapter.checkTempCmsId(checkItemPosition)) {
                        onBannerScroll?.invoke(checkItemPosition)
                    }
                    val pagerIndicatorTabLayout =
                        view.findViewById<TabLayout>(R.id.pagerIndicatorTabLayout)
                    val tab = pagerIndicatorTabLayout?.getTabAt(position % _adapter.getItemSize())
                    tab?.select()
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                if (state == SCROLL_STATE_IDLE) {
                    timer?.start()
                } else {
                    timer?.cancel()
                }
            }
        })
    }

    fun addTempCmsId(position: Int) {
        adapter?.addTempCmsId(position)
    }

    private fun getIndexOfCenter(): Int {
        adapter?.let { _adapter ->
            if (_adapter.getItemSize() != 0) {
                val numberOfInterval = (Int.MAX_VALUE / _adapter.getItemSize()) / 2
                return numberOfInterval * _adapter.getItemSize()
            }
        }
        return 0
    }

    private fun getTimer(): CountDownTimer? {
        return if (interval == STAGE_NOT_SCROLL) {
            null
        } else object : CountDownTimer(interval, interval) {
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                viewPager?.setCurrentItem(scrollPosition + 1, true)
                timer?.start()
            }
        }
    }

    private fun setPageIndicator() {
        adapter?.let { _adapter ->
            if (_adapter.getItemSize() > 1) {
                pagerIndicatorTabLayout?.removeAllTabs()
                for (item in 1.._adapter.getItemSize()) {
                    pagerIndicatorTabLayout?.let {
                        it.addTab(it.newTab())
                    }
                }
            } else {
                pagerIndicatorTabLayout?.gone()
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        onBannerScroll?.invoke(0)
        timer?.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        timer?.cancel()
    }

    fun setIndicatorContentDescription(contentDescription: String) {
        pagerIndicatorTabLayout?.contentDescription = contentDescription
    }
}
