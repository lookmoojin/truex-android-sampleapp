package com.truedigital.common.share.componentv3.widget.header

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.truedigital.common.share.analytics.measurement.AnalyticManager
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant.Calling.LinkDesc.LINK_DESC_HEADER_CALLING
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant.Calling.LinkType.LINK_TYPE_HEADER_CALLING
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant.Event.EVENT_CLICK
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant.Key.KEY_EVENT_NAME
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant.Key.KEY_LINK_DESC
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant.Key.KEY_LINK_TYPE
import com.truedigital.common.share.componentv3.R
import com.truedigital.common.share.componentv3.databinding.ViewCallIconBinding
import com.truedigital.common.share.componentv3.injections.ComponentV3Component
import com.truedigital.foundation.extension.onClick
import javax.inject.Inject

class CallIconWidget : FrameLayout {

    private val binding: ViewCallIconBinding by lazy {
        ViewCallIconBinding.inflate(LayoutInflater.from(context), this, false)
    }

    @Inject
    lateinit var analyticManager: AnalyticManager

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    init {
        ComponentV3Component.getInstance().inject(this)

        addView(binding.root)
        setTheme(false)
    }

    fun setTheme(isDark: Boolean) {
        binding.callShapeableImageView.setImageResource(if (isDark) R.drawable.common_ic_call_dark else R.drawable.common_ic_call_light)
    }

    fun setOnClick(block: () -> Unit) {
        binding.callShapeableImageView.onClick {
            sentAnalyticsClickEvent()
            block.invoke()
        }
    }

    private fun sentAnalyticsClickEvent() {
        analyticManager.trackEvent(
            hashMapOf(
                KEY_EVENT_NAME to EVENT_CLICK,
                KEY_LINK_TYPE to LINK_TYPE_HEADER_CALLING,
                KEY_LINK_DESC to LINK_DESC_HEADER_CALLING
            )
        )
    }
}
