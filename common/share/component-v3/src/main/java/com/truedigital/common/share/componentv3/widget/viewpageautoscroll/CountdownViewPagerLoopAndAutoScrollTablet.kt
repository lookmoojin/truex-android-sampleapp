package com.truedigital.common.share.componentv3.widget.viewpageautoscroll

import android.annotation.SuppressLint
import android.content.Context
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.truedigital.common.share.componentv3.R
import com.truedigital.common.share.componentv3.databinding.ViewCustomInfintePagerBinding
import com.truedigital.common.share.componentv3.extension.convertDpToPixel
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.adapter.CountdownBannerLoopItemAdapter
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.domain.BannerBaseItemModel
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.visible
import kotlin.math.abs

open class CountdownViewPagerLoopAndAutoScrollTablet : ConstraintLayout {

    companion object {
        private const val DEFAULT_PAGE_LIMIT = 3
        private const val PAGE_SCALE_OFFSET = 0.85f
        private const val PAGE_SCALE_RATIO = 0.14f
        private const val DEFAULT_TIME_BANNER = 3000L
        private const val STAGE_NOT_SCROLL = 0L
    }

    private lateinit var bannerAdapter: CountdownBannerLoopItemAdapter
    private var scrollPosition = 0
    private var interval = 0L
    private var timer: CountDownTimer? = null
    private var viewPager: ViewPager2? = null
    private var pagerIndicatorTabLayout: TabLayout? = null
    private val binding: ViewCustomInfintePagerBinding by lazy {
        ViewCustomInfintePagerBinding.inflate(LayoutInflater.from(context), this, false)
    }
    private var bannerItemModel: List<BannerBaseItemModel>? = null
    var onItemClick: ((bannerItemModel: BannerBaseItemModel, index: Int) -> Unit)? = null

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

    private fun initInflater() {
        removeAllViews()
        addView(binding.root)
        initView(binding.root)
    }

    fun initView(view: View) {
        bannerAdapter = CountdownBannerLoopItemAdapter(mutableListOf()).apply {
            onItemClicked = { index ->
                val banner = bannerItemModel?.getOrNull(index) ?: BannerBaseItemModel()
                onItemClick?.invoke(banner, index)
            }
        }
        viewPager = view.findViewById(R.id.viewPager2)
        pagerIndicatorTabLayout = view.findViewById(R.id.pagerIndicatorInfiniteTabLayout)
        viewPager?.apply {
            adapter = bannerAdapter
            offscreenPageLimit = DEFAULT_PAGE_LIMIT
            clipChildren = false
            clipToPadding = false
            getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }
        val compositeTransformer = CompositePageTransformer()
        val defaultMargin = context.resources.getDimension(R.dimen.carousel_default_margin)
        val defaultMarginPx = defaultMargin.convertDpToPixel(context).toInt()
        compositeTransformer.apply {
            addTransformer(MarginPageTransformer(defaultMarginPx))
            addTransformer { page, position ->
                val r = 1 - abs(position)
                page.scaleY = (PAGE_SCALE_OFFSET + r * PAGE_SCALE_RATIO)
            }
        }
        viewPager?.setPageTransformer(compositeTransformer)
        viewPager?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                scrollPosition = position
                if (position == 0 || position == Int.MAX_VALUE - 1) {
                    viewPager?.setCurrentItem(getIndexOfCenter(), false)
                }
                val pagerIndicatorTabLayout =
                    view.findViewById<TabLayout>(R.id.pagerIndicatorInfiniteTabLayout)
                val tab = pagerIndicatorTabLayout?.getTabAt(position % bannerAdapter.getItemSize())
                tab?.select()
            }

            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    timer?.start()
                } else {
                    timer?.cancel()
                }
            }
        })
    }

    fun setupAutoScrollBanner(
        listItem: MutableList<BannerBaseItemModel>,
        intervalTime: Long = DEFAULT_TIME_BANNER,
    ) {
        setItemList(listItem)
        startAutoScroll(intervalTime)
    }

    private fun getIndexOfCenter(): Int {
        if (bannerAdapter.getItemSize() != 0) {
            val numberOfInterval = (Int.MAX_VALUE / bannerAdapter.getItemSize()) / 2
            return numberOfInterval * bannerAdapter.getItemSize()
        }

        return 0
    }

    private fun getTimer(): CountDownTimer? {
        return if (interval == STAGE_NOT_SCROLL) {
            null
        } else object : CountDownTimer(interval, interval) {
            override fun onTick(millisUntilFinished: Long) {
                // Do nothing
            }

            override fun onFinish() {
                viewPager?.setCurrentItem(scrollPosition + 1, true)
                timer?.start()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setItemList(listItem: MutableList<BannerBaseItemModel>) {
        bannerAdapter.setItemBanner(listItem)
        bannerItemModel = listItem
        bannerAdapter.notifyDataSetChanged()
        viewPager?.setCurrentItem(getIndexOfCenter(), false)
        setPageIndicator()
    }

    private fun setPageIndicator() {
        if (bannerAdapter.getItemSize() > 1) {
            pagerIndicatorTabLayout?.visible()
            pagerIndicatorTabLayout?.removeAllTabs()
            (1..bannerAdapter.getItemSize()).forEach {
                pagerIndicatorTabLayout?.let {
                    it.addTab(it.newTab())
                }
            }
        } else {
            pagerIndicatorTabLayout?.gone()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        timer?.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        timer?.cancel()
    }

    private fun startAutoScroll(intervalTime: Long) {
        interval = intervalTime
        timer = getTimer()
        timer?.start()
    }
}
