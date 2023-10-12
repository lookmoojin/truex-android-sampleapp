package com.truedigital.common.share.analytics.measurement

import com.truedigital.common.share.analytics.measurement.base.BaseAnalyticModel
import com.truedigital.common.share.analytics.measurement.base.platform.PlatformAnalyticModel
import com.truedigital.common.share.analytics.measurement.firebase.FirebaseAnalyticManager
import com.truedigital.common.share.analytics.measurement.newrelic.NewRelicManager
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.common.share.datalegacy.domain.other.usecase.EncryptLocationUseCase
import com.truedigital.core.constant.SharedPrefsKeyConstant.PRASARN_PPID_KEY
import com.truedigital.core.data.repository.DeviceRepository
import com.truedigital.core.manager.location.LocationManagerImpl
import com.truedigital.core.utils.SharedPrefsUtils
import com.truedigital.foundation.extension.addTo
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class AnalyticManager @Inject constructor(
    private val encryptLocationUseCase: EncryptLocationUseCase,
    private val userRepository: UserRepository,
    private val deviceRepository: DeviceRepository,
    private val sharedPrefsUtils: SharedPrefsUtils,
    private val firebaseAnalyticManager: FirebaseAnalyticManager,
    private val newRelicManager: NewRelicManager
) {
    init {
        LocationManagerImpl.instance.onLocationChange()
            .subscribeOn(Schedulers.computation())
            .subscribe(
                {
                    updateLocation()
                },
                {}
            )
            .addTo(CompositeDisposable())
    }

    fun clearUserProperties() {
        firebaseAnalyticManager.clearUserProperties()
    }

    fun getCustomDimenFirstParamWithDeviceID(): String {
        val ssoId = userRepository.getSsoId()
        val deviceId = deviceRepository.getAndroidId()
        val adsId: String = firebaseAnalyticManager.platformAnalyticModel?.advertisingId ?: ""
        return if (ssoId.isEmpty()) {
            "nologin|$deviceId|$adsId"
        } else {
            "$ssoId|$deviceId|$adsId"
        }
    }

    // function track FirebaseAnalytics and HuaweiAnalytics
    fun trackScreen(analyticModel: PlatformAnalyticModel) {
        firebaseAnalyticManager.trackScreen(analyticModel)

        newRelicManager.trackScreen(analyticModel.screenName)
    }

    fun trackEvent(event: HashMap<String, Any>) {
        updatePPID()

        firebaseAnalyticManager.trackEvent(event)

        newRelicManager.trackEvent(event)
    }

    fun trackUserProperties(event: HashMap<String, String?>?, function: String) {
        firebaseAnalyticManager.trackUserProperties(event, function)
    }

    fun trackUserProperties(key: String, value: String) {
        firebaseAnalyticManager.setUserProperty(key, value)
    }

    fun setAnalyticModel(analyticModel: BaseAnalyticModel, function: String) {
        when (analyticModel) {
            is PlatformAnalyticModel -> {
                firebaseAnalyticManager.platformAnalyticModel = analyticModel
                firebaseAnalyticManager.trackUserProperties(null, function)
            }
        }
    }

    fun setUserId() {
        val userId = userRepository.getSsoId()

        firebaseAnalyticManager.platformAnalyticModel?.let {
            it.userId = userId.ifEmpty { " " }
        }
    }

    private fun updatePPID() {
        firebaseAnalyticManager.platformAnalyticModel?.let {
            it.ppid = sharedPrefsUtils.get(PRASARN_PPID_KEY, "")
        }
    }

    private fun updateLocation() {
        firebaseAnalyticManager.platformAnalyticModel?.location =
            encryptLocationUseCase.getEncryptLocation()
    }
}
