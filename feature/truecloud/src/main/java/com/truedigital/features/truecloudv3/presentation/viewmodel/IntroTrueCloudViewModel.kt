package com.truedigital.features.truecloudv3.presentation.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import com.truedigital.common.share.analytics.di.AnalyticsModule
import com.truedigital.common.share.analytics.measurement.AnalyticManagerInterface
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant
import com.truedigital.common.share.datalegacy.login.LoginManagerInterface
import com.truedigital.common.share.datalegacy.wrapper.AuthLoginListener
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.extensions.launchSafe
import com.truedigital.core.extensions.launchSafeIn
import com.truedigital.features.truecloudv3.common.TrueCloudV3TrackAnalytic
import com.truedigital.features.truecloudv3.domain.usecase.ConfigIntroImageUseCase
import com.truedigital.features.truecloudv3.navigation.IntroTrueCloudToMain
import com.truedigital.features.truecloudv3.navigation.router.IntroTrueCloudRouterUseCase
import com.truedigital.navigation.domain.usecase.SetRouterToNavControllerUseCase
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Named

class IntroTrueCloudViewModel @Inject constructor(
    private val router: IntroTrueCloudRouterUseCase,
    private val loginManagerInterface: LoginManagerInterface,
    private val coroutineDispatcher: CoroutineDispatcherProvider,
    private val configIntroImageUseCase: ConfigIntroImageUseCase,
    @Named(AnalyticsModule.FIREBASE_ANALYTIC)
    private val analyticManagerInterface: AnalyticManagerInterface,
    private val setRouterToNavControllerUseCase: SetRouterToNavControllerUseCase
) : ScopedViewModel() {

    val onDisplayIntroImage = MutableLiveData<String>()
    val onClosePage = MutableLiveData<Unit>()

    private val _onOpenMainTrueCloud = MutableLiveData<Unit>()
    val onOpenMainTrueCloud: LiveData<Unit> = _onOpenMainTrueCloud

    fun checkAuthenticationState(isTablet: Boolean) {
        if (loginManagerInterface.isLoggedIn()) {
            router.execute(IntroTrueCloudToMain)
        } else {
            getIntroImage(isTablet)
        }
    }

    fun onClickCloseButton() {
        onClosePage.value = Unit
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_EVENT_NAME to TrueCloudV3TrackAnalytic.EVENT_CLICK,
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.LINK_TYPE_INTRO,
                MeasurementConstant.Key.KEY_LINK_DESC to TrueCloudV3TrackAnalytic.LINK_DESC_DISMISS
            )
        )
    }

    fun openAuthenticationPage() {
        loginManagerInterface.login(
            object : AuthLoginListener() {
                override fun onLoginSuccess() {
                    _onOpenMainTrueCloud.postValue(Unit)
                }
            },
            false
        )
        analyticManagerInterface.trackEvent(
            hashMapOf(
                MeasurementConstant.Key.KEY_EVENT_NAME to TrueCloudV3TrackAnalytic.EVENT_CLICK,
                MeasurementConstant.Key.KEY_LINK_TYPE to TrueCloudV3TrackAnalytic.LINK_TYPE_INTRO,
                MeasurementConstant.Key.KEY_LINK_DESC to TrueCloudV3TrackAnalytic.LINK_DESC_LOGIN
            )
        )
    }

    fun loginSuccess() {
        launchSafe {
            router.execute(IntroTrueCloudToMain)
        }
    }

    fun setRouterToNavController(navController: NavController) {
        setRouterToNavControllerUseCase.execute(navController)
    }

    private fun getIntroImage(isTablet: Boolean) {
        configIntroImageUseCase.execute(isTablet)
            .flowOn(coroutineDispatcher.io())
            .onEach { introImage ->
                onDisplayIntroImage.value = introImage
            }
            .launchSafeIn(this)
    }
}
