package com.truedigital.common.share.analytics.measurement.firebase

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.truedigital.common.share.analytics.measurement.base.platform.PlatformAnalyticManager
import javax.inject.Inject

class FirebaseAnalyticManager @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics
) : PlatformAnalyticManager() {

    override fun logEvent(eventKey: String, bundle: Bundle) {
        firebaseAnalytics.logEvent(eventKey, bundle)
    }

    override fun setUserId(userId: String) {
        firebaseAnalytics.setUserId(userId)
    }

    override fun setUserProperty(key: String, value: String?) {
        firebaseAnalytics.setUserProperty(key, value)
    }
}
