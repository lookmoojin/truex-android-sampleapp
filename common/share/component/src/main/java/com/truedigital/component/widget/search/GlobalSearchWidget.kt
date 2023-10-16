package com.truedigital.component.widget.search

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.FragmentActivity
import com.truedigital.common.share.analytics.measurement.AnalyticManager
import com.truedigital.common.share.analytics.measurement.base.platform.PlatformAnalyticModel
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant
import com.truedigital.common.share.datalegacy.constant.DataContentConstant
import com.truedigital.common.share.datalegacy.domain.discover.model.shelf.BaseShelfModel
import com.truedigital.common.share.datalegacy.domain.discover.model.shelf.ShelfModel
import com.truedigital.common.share.datalegacy.navigation.NavigationManager
import com.truedigital.common.share.datalegacy.navigation.NavigationRequest
import com.truedigital.component.injections.TIDComponent
import com.truedigital.foundation.extension.onClick
import javax.inject.Inject

class GlobalSearchWidget : AppCompatImageView {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    @Inject
    lateinit var analyticManager: AnalyticManager

    init {
        TIDComponent.getInstance().inject(this)
    }

    fun init(activity: FragmentActivity?, className: String?) {
        onClick {
            openGlobalSearch()

            activity?.let {
                trackAnalytic(it, className ?: "")
            }
        }
    }

    private fun openGlobalSearch() {
        NavigationManager.setActionRequest(
            NavigationRequest().apply {
                baseShelfModel = BaseShelfModel().apply {
                    shelfModel = ShelfModel().apply {
                        type = DataContentConstant.TYPE.GLOBAL_SEARCH
                    }
                }
            }
        )
    }

    private fun trackAnalytic(activity: FragmentActivity, className: String) {
        analyticManager.trackScreen(
            PlatformAnalyticModel().apply {
                this.screenClass = className
            }
        )
        analyticManager.trackEvent(
            HashMap<String, Any>().apply {
                put(
                    MeasurementConstant.Key.KEY_EVENT_NAME,
                    MeasurementConstant.Action.ACTION_CLICK.toLowerCase()
                )
                put(MeasurementConstant.Key.KEY_LINK_TYPE, "top-nav-lv1")
                put(MeasurementConstant.Key.KEY_LINK_DESC, "search")
                put(MeasurementConstant.Key.KEY_INDEX, "0")
            }
        )
    }
}
