package com.truedigital.component.utils

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.common.share.analytics.measurement.AnalyticManager
import com.truedigital.common.share.analytics.measurement.base.platform.PlatformAnalyticModel
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant
import com.truedigital.component.R
import com.truedigital.component.databinding.ViewBackToTopBinding
import com.truedigital.component.injections.TIDComponent
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.font.ZawgyiConverter
import com.truedigital.core.provider.ContextDataProvider
import com.truedigital.core.provider.ContextDataProviderImp
import com.truedigital.foundation.extension.onClick
import java.util.Locale
import javax.inject.Inject

class BackToTopView : FrameLayout {

    @Inject
    lateinit var localizationRepository: LocalizationRepository

    @Inject
    lateinit var analyticManager: AnalyticManager

    private val binding: ViewBackToTopBinding by lazy {
        ViewBackToTopBinding.inflate(LayoutInflater.from(context))
    }

    private var contextProvider: ContextDataProvider? = null

    init {
        TIDComponent.getInstance().inject(this)
    }

    constructor(context: Context) : super(context) {
        inflateLayout(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        inflateLayout(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        inflateLayout(context)
    }

    private lateinit var viewGroup: ViewGroup

    fun setViewGroup(viewGroup: ViewGroup) {
        this.viewGroup = viewGroup
    }

    fun moveFollowCompositeView(recyclerView: RecyclerView) {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            private var controlsVisible = true
            private var scrolledDistance = 0
            private val HIDE_THRESHOLD = 20
            private var totalDy = 0

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (isOnTop()) {
                        hide()
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (scrolledDistance > HIDE_THRESHOLD && controlsVisible) {
                    hide()
                    controlsVisible = false
                    scrolledDistance = 0
                } else if (scrolledDistance < -HIDE_THRESHOLD && !controlsVisible) {
                    if (dy < 0) {
                        show()
                        controlsVisible = true
                    }
                    scrolledDistance = 0
                }

                this.totalDy += dy
                if ((controlsVisible && dy > 0) || (!controlsVisible && dy < 0)) {
                    scrolledDistance += dy
                }

                if (isOnTop()) {
                    hide()
                }
            }

            private fun isOnTop(): Boolean {
                return totalDy == 0
            }
        })
    }

    fun onClickListener(recyclerView: RecyclerView) {
        onClick {
            recyclerView.smoothScrollToPosition(0)
        }
    }

    fun hide() {
        when (viewGroup) {
            is FrameLayout -> {
                val layoutParams = layoutParams as FrameLayout.LayoutParams
                updateAnimate(layoutParams.bottomMargin)
            }
            is RelativeLayout -> {
                val layoutParams = layoutParams as RelativeLayout.LayoutParams
                updateAnimate(layoutParams.bottomMargin)
            }
            is LinearLayout -> {
                val layoutParams = layoutParams as LinearLayout.LayoutParams
                updateAnimate(layoutParams.bottomMargin)
            }
            is ConstraintLayout -> {
                val layoutParams = layoutParams as ConstraintLayout.LayoutParams
                updateAnimate(layoutParams.bottomMargin)
            }
        }
    }

    fun show() {
        visibility = View.VISIBLE
        animate().translationY(0f).setInterpolator(DecelerateInterpolator(2f)).start()
    }

    private fun updateAnimate(fabBottomMargin: Int) {
        animate().translationY((height + fabBottomMargin).toFloat())
            .setInterpolator(AccelerateInterpolator(2f))
            .withEndAction { visibility = View.GONE }
            .start()
    }

    private fun inflateLayout(context: Context) {
        addView(binding.root)
        contextProvider = context.applicationContext?.let { ContextDataProviderImp(it) }
        contextProvider?.updateContextLocale(
            localizationRepository.getAppLanguageCode().toLowerCase(Locale.ENGLISH)
        )
        setTextByLocale()
    }

    private fun setTextByLocale() {
        if (localizationRepository.getAppLanguageCode() == LocalizationRepository.Localization.MY.languageCode) {
            binding.messageTextView.text = ZawgyiConverter.convert(
                contextProvider?.getResources()?.getString(R.string.function_button_back_to_top)
            )
            binding.messageTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
            binding.messageTextView.setPadding(0, 4, 0, 0)
        } else {
            binding.messageTextView.text =
                contextProvider?.getResources()?.getString(R.string.function_button_back_to_top)
        }
    }

    private fun firebaseTrackEvent(
        event: HashMap<String, Any>,
        className: String,
        screen: String
    ) {
        analyticManager.trackScreen(
            PlatformAnalyticModel().apply {
                screenClass = className
                screenName = screen
            }
        )
        analyticManager.trackEvent(event)
    }

    fun trackEventClickBackToTop(activity: Activity, className: String, screen: String) {
        val analyticModel = HashMap<String, Any>().apply {
            put(
                MeasurementConstant.Key.KEY_EVENT_NAME,
                MeasurementConstant.Action.ACTION_CLICK.toLowerCase()
            )
            put(
                MeasurementConstant.Key.KEY_LINK_TYPE,
                MeasurementConstant.Chat.LinkType.LINK_TYPE_BUTTON
            )
            put(
                MeasurementConstant.Key.KEY_LINK_DESC,
                MeasurementConstant.Discover.LinkDesc.LINK_DESC_BACK_TO_TOP
            )
        }
        firebaseTrackEvent(analyticModel, className, screen)
    }
}
