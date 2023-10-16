package com.truedigital.common.share.componentv3.widget.viewpageautoscroll

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.util.AttributeSet
import android.view.LayoutInflater
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant
import com.truedigital.common.share.componentv3.databinding.WidgetViewpagerTutorialBinding
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.adapter.BannerLoopType
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.domain.BannerBaseItemModel
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.domain.TutorialModel
import com.truedigital.core.extensions.ifNotNullOrEmpty
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.onClick

class TutorialViewPager : ViewPagerLoopAndAutoScroll {

    companion object {
        private const val RADIUS_SIZE = 64F
    }

    var onGetStartClick: ((index: Int, button: String) -> Unit)? = null

    private val widgetViewpagerTutorialBinding: WidgetViewpagerTutorialBinding by lazy {
        WidgetViewpagerTutorialBinding.inflate(LayoutInflater.from(context), this, false)
    }
    private var bannerItemModel: List<BannerBaseItemModel>? = null

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
        addView(widgetViewpagerTutorialBinding.root)
        initView(widgetViewpagerTutorialBinding.root)

        widgetViewpagerTutorialBinding.getStartButton.onClick {
            onGetStartClick?.invoke(
                getPositionScroll(),
                MeasurementConstant.Discover.Event.TUTORIAL_GET_STARTED
            )
        }
        widgetViewpagerTutorialBinding.closeButton.onClick {
            onGetStartClick?.invoke(
                getPositionScroll(),
                MeasurementConstant.Discover.Event.TUTORIAL_DISMISS
            )
        }
    }

    fun setupBannerList(bannerItemModel: MutableList<BannerBaseItemModel>) {
        this.bannerItemModel = bannerItemModel
        setupBannerNotScrollBannerNotRatio(
            listItem = bannerItemModel,
            bannerLoopType = BannerLoopType.TYPE_DEFAULT_NO_CARD_VIEW
        )
    }

    fun setupProperty(tutorialModel: TutorialModel) {
        if (tutorialModel.textButton.isEmpty() ||
            tutorialModel.colorButton.isEmpty() ||
            tutorialModel.colorButtonText.isEmpty()
        ) {
            widgetViewpagerTutorialBinding.getStartButton.gone()
        } else {
            val shapeDrawable = getShapeDrawable(tutorialModel)
            widgetViewpagerTutorialBinding.getStartButton.apply {
                text = tutorialModel.textButton
                background = shapeDrawable
                tutorialModel.colorButtonText.ifNotNullOrEmpty { color ->
                    setTextColor(Color.parseColor(color))
                }
            }
        }
    }

    private fun getShapeDrawable(tutorialModel: TutorialModel): ShapeDrawable {
        val roundRectShape =
            RoundRectShape(
                floatArrayOf(
                    RADIUS_SIZE,
                    RADIUS_SIZE,
                    RADIUS_SIZE,
                    RADIUS_SIZE,
                    RADIUS_SIZE,
                    RADIUS_SIZE,
                    RADIUS_SIZE,
                    RADIUS_SIZE
                ),
                null, null
            )
        val shapeDrawable = ShapeDrawable(roundRectShape)
        tutorialModel.colorButton.ifNotNullOrEmpty { color ->
            shapeDrawable.paint.color = Color.parseColor(color)
        }
        return shapeDrawable
    }
}
