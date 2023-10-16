package com.truedigital.common.share.componentv3.widget.viewpageautoscroll

import android.content.Context
import android.os.CountDownTimer
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.ViewPager2.SCROLL_STATE_IDLE
import com.google.android.material.tabs.TabLayout
import com.truedigital.common.share.componentv3.R
import com.truedigital.common.share.componentv3.databinding.WidgetViewpagerLoopAutoScrollBinding
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.adapter.CountdownBannerLoopItemAdapter
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.domain.BannerBaseItemModel
import com.truedigital.foundation.extension.gone

open class CountdownViewPagerLoopAndAutoScroll : ConstraintLayout {

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

    companion object {
        private const val DEFAULT_TIME_BANNER = 3000L
        private const val DEFAULT_BANNER_WIDTH = 16
        private const val DEFAULT_BANNER_HEIGHT = 9
        private const val STAGE_NOT_SCROLL = 0L
    }

    private lateinit var adapter: CountdownBannerLoopItemAdapter
    private var scrollPosition = 0
    private var interval = 0L
    private var timer: CountDownTimer? = null
    private var viewPager: ViewPager2? = null
    private var pagerIndicatorTabLayout: TabLayout? = null
    private val widgetViewpagerLoopAutoScrollBinding: WidgetViewpagerLoopAutoScrollBinding by lazy {
        WidgetViewpagerLoopAutoScrollBinding.inflate(LayoutInflater.from(context), this, false)
    }

    var onBannerClicked: ((index: Int) -> Unit)? = null

    fun setupBannerAutoScroll(
        listItem: MutableList<BannerBaseItemModel>,
        width: Int = DEFAULT_BANNER_WIDTH,
        height: Int = DEFAULT_BANNER_HEIGHT,
        intervalTime: Long = DEFAULT_TIME_BANNER,
    ) {
        setBannerItem(listItem)
        val layoutParams =
            viewPager?.layoutParams as? LayoutParams
        layoutParams?.dimensionRatio = "H,$width:$height"
        startAutoScroll(intervalTime)
    }

    private fun setBannerItem(listItem: MutableList<BannerBaseItemModel>) {
        adapter?.setItemBanner(listItem)
        adapter?.notifyDataSetChanged()
        viewPager?.setCurrentItem(getIndexOfCenter(), false)
        setPageIndicator()
    }

    private fun startAutoScroll(intervalTime: Long) {
        interval = intervalTime
        timer = getTimer()
        timer?.start()
    }

    private fun initInflater() {
        removeAllViews()
        addView(widgetViewpagerLoopAutoScrollBinding.root)
        initView(widgetViewpagerLoopAutoScrollBinding.root)
    }

    fun initView(view: View) {
        adapter = CountdownBannerLoopItemAdapter(mutableListOf())
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
            override fun onTick(millisUntilFinished: Long) {
                // Do nothing
            }

            override fun onFinish() {
                viewPager?.setCurrentItem(scrollPosition + 1, true)
                timer?.start()
            }
        }
    }

    private fun setPageIndicator() {

        if (adapter.getItemSize() > 1) {
            pagerIndicatorTabLayout?.removeAllTabs()
            (1..adapter.getItemSize()).forEach {
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
}
