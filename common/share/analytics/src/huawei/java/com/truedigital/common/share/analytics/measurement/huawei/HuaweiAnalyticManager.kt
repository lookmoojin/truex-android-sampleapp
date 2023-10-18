package com.truedigital.common.share.analytics.measurement.huawei

import android.os.Bundle
import com.huawei.hms.analytics.HiAnalytics
import com.huawei.hms.analytics.HiAnalyticsInstance
import com.huawei.hms.analytics.HiAnalyticsTools
import com.truedigital.common.share.analytics.measurement.base.platform.PlatformAnalyticManager
import com.truedigital.foundation.FoundationApplication

object HuaweiAnalyticManager : PlatformAnalyticManager() {

    init {
        HiAnalyticsTools.enableLog()
    }

    private val hiAnalytics: HiAnalyticsInstance by lazy {
        HiAnalytics.getInstance(FoundationApplication.appContext)
    }

    override fun logEvent(eventKey: String, bundle: Bundle) {
        hiAnalytics.onEvent(eventKey, bundle)
    }

    override fun setUserId(userId: String) {
        hiAnalytics.setUserId(userId)
    }

    override fun setUserProperty(key: String, value: String?) {
        hiAnalytics.setUserProfile(key, value)
    }
}
