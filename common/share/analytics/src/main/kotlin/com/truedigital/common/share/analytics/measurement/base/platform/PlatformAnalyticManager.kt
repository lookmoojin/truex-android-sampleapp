package com.truedigital.common.share.analytics.measurement.base.platform

import android.os.Bundle
import androidx.core.os.bundleOf
import com.google.firebase.analytics.FirebaseAnalytics
import com.truedigital.common.share.analytics.injections.AnalyticsComponent
import com.truedigital.common.share.analytics.measurement.base.MeasurementAnalyticInterface
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant.Key.KEY_ERROR_CODE
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant.Key.KEY_ERROR_DESC
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant.Key.KEY_TITLE
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant.Player.Key.KEY_CONTENT_DURATION_MS
import com.truedigital.common.share.datalegacy.wrapper.AuthManagerWrapper
import javax.inject.Inject

abstract class PlatformAnalyticManager :
    MeasurementAnalyticInterface<PlatformAnalyticModel, HashMap<String, Any>> {

    abstract fun logEvent(eventKey: String, bundle: Bundle)
    abstract fun setUserId(userId: String)
    abstract fun setUserProperty(key: String, value: String?)

    @Inject
    lateinit var authManagerWrapper: AuthManagerWrapper

    companion object {
        private const val KEY_ANALYTIC_SCREEN_NAME = "screen_name"
        private const val KEY_ANALYTIC_SCREEN_CLASS = "screen_class"
        private const val KEY_ANALYTIC_EVENT_NAME = "event_name"
        private const val KEY_ANALYTIC_EVENT_ERROR = "error_not_show"
        private const val KEY_TAB = "tab"

        private const val KEY_ANALYTIC_LATITUDE_LONGITUDE = "lat_long"
        private const val KEY_ANALYTIC_AD_ID = "ad_id"
    }

    var platformAnalyticModel: PlatformAnalyticModel? = null

    init {
        AnalyticsComponent.getInstance().inject(this)
    }

    override fun trackScreen(analyticModel: PlatformAnalyticModel) {
        trackEvent(
            HashMap<String, Any>().apply {
                put(KEY_ANALYTIC_SCREEN_NAME, analyticModel.screenName)
                put(KEY_ANALYTIC_SCREEN_CLASS, analyticModel.screenClass)
                put(KEY_TAB, analyticModel.tab)
                put(KEY_ANALYTIC_EVENT_NAME, FirebaseAnalytics.Event.SCREEN_VIEW)
            }
        )
    }

    override fun trackEvent(event: HashMap<String, Any>) {
        val bundle = Bundle()
        platformAnalyticModel?.let {
            bundle.putString(KEY_ANALYTIC_LATITUDE_LONGITUDE, it.location)
            bundle.putString(KEY_ANALYTIC_AD_ID, it.ppid.take(100))
        }
        for ((key, value) in event) {
            if (key != KEY_ANALYTIC_EVENT_NAME && value is MutableList<*>) {
                val bundleList = mutableListOf<Bundle>()
                value.forEach {
                    val subHashMap = it as? HashMap<String, Any>
                    if (subHashMap != null) {
                        val item = Bundle()
                        for ((subKey, subValue) in subHashMap) {
                            item.putString(subKey, "$subValue")
                        }
                        bundleList.add(item)
                    }
                }
                bundle.putParcelableArray(
                    MeasurementConstant.Key.KEY_ITEMS,
                    bundleList.toTypedArray()
                )
            } else if (key != KEY_ANALYTIC_EVENT_NAME) {
                if (key != KEY_CONTENT_DURATION_MS) {
                    val modifyValue = value.toString().take(100)
                    bundle.putAll(bundleOf(key to modifyValue))
                } else {
                    bundle.putAll(bundleOf(key to value))
                }
            }
        }
        val eventKey = event[KEY_ANALYTIC_EVENT_NAME] as? String ?: ""
        logEvent(eventKey, bundle)
    }

    fun clearUserProperties() {
        setUserId(" ")
        setUserProperty(MeasurementConstant.Key.KEY_SSO_ID, " ")
        setUserProperty(MeasurementConstant.Key.KEY_IS_TMH, " ")
        setUserProperty(MeasurementConstant.Key.KEY_CARD_TIER, " ")
        setUserProperty(MeasurementConstant.Key.KEY_TRUE_POINT, " ")
        setUserProperty(MeasurementConstant.Key.KEY_SUBSCRIPTION_TIERS, " ")
        setUserProperty(MeasurementConstant.Key.KEY_TRUEVISIONS, " ")
        setUserProperty(MeasurementConstant.Key.KEY_CUSTOMER_HAS_TRUE_MONEY, " ")
        setUserProperty(MeasurementConstant.Key.KEY_USER_PROFILE_ID, " ")
        setUserProperty(MeasurementConstant.Key.KEY_TRUE_PRODUCT, " ")
        setUserProperty(MeasurementConstant.Key.KEY_DEFAULT_SCREEN, " ")
    }

    fun trackUserProperties(event: HashMap<String, String?>?, functionName: String) {
        if (authManagerWrapper.isLoggedIn()) {
            platformAnalyticModel?.let {
                if (it.userId.isEmpty()) {
                    trackEvent(
                        HashMap<String, Any>().apply {
                            trackEvent(
                                HashMap<String, Any>().apply {
                                    put(KEY_ANALYTIC_EVENT_NAME, KEY_ANALYTIC_EVENT_ERROR)
                                    put(KEY_TITLE, "sso_id is null or empty")
                                    put(KEY_ERROR_CODE, " ")
                                    put(KEY_ERROR_DESC, functionName)
                                }
                            )
                        }
                    )
                } else {
                    setUserId(it.userId)
                    setUserProperty(MeasurementConstant.Key.KEY_SSO_ID, it.userId)
                    setUserProperty(MeasurementConstant.Key.KEY_DEVICE_ID, it.deviceId)
                    setUserProperty(MeasurementConstant.Key.KEY_APP_FLYER_ID, it.appsFlyerId)
                    setUserProperty(MeasurementConstant.Key.KEY_CARRIER, it.carrier)
                    setUserProperty(MeasurementConstant.Key.KEY_WIFI, it.wifiName)
                    event?.let { eventNonNull ->
                        for ((key, value) in eventNonNull) {
                            setUserProperty(key, value)
                        }
                    }
                }
            }
        } else {
            platformAnalyticModel?.let {
                setUserProperty(MeasurementConstant.Key.KEY_DEVICE_ID, it.deviceId)
                setUserProperty(MeasurementConstant.Key.KEY_APP_FLYER_ID, it.appsFlyerId)
                setUserProperty(MeasurementConstant.Key.KEY_CARRIER, it.carrier)
                setUserProperty(MeasurementConstant.Key.KEY_WIFI, it.wifiName)
                setUserProperty(MeasurementConstant.Key.KEY_LOCATION_LAT_LONG, it.location)
            }
        }
    }
}
