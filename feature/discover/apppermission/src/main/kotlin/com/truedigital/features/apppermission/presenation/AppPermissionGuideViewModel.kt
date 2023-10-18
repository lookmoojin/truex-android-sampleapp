package com.truedigital.features.apppermission.presenation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.truedigital.common.share.analytics.di.AnalyticsModule
import com.truedigital.common.share.analytics.measurement.AnalyticManagerInterface
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.domain.BannerBaseItemModel
import com.truedigital.features.apppermission.usecase.GetPermissionAgreeButtonUseCase
import com.truedigital.features.apppermission.usecase.GetPermissionDataUseCase
import com.truedigital.features.onboarding.shareviewmodel.OnBoardingShareData
import com.truedigital.features.onboarding.whatsnew.data.repository.DiscoverGetConfigRepository
import javax.inject.Inject
import javax.inject.Named

class AppPermissionGuideViewModel @Inject constructor(
    private val discoverGetConfigRepository: DiscoverGetConfigRepository,
    private val getPermissionAgreeButtonUseCase: GetPermissionAgreeButtonUseCase,
    private val getPermissionDataUseCase: GetPermissionDataUseCase,
    private val onBoardingShareData: OnBoardingShareData,
    @Named(AnalyticsModule.FIREBASE_ANALYTIC)
    private val analyticManagerInterface: AnalyticManagerInterface
) : ViewModel() {

    val onShowAgreeButton = MutableLiveData<String>()
    var onShowAppPermission = MutableLiveData<MutableList<BannerBaseItemModel>>()
    val isForceLogin = MutableLiveData<Boolean>()
    val onAndroidAboveAndroidQ = MutableLiveData<ArrayList<String>>()
    val onAndroidBelowAndroidQ = MutableLiveData<ArrayList<String>>()

    fun loadPermissionNotGranted(permissionNotGrantedList: ArrayList<String>) {
        onShowAppPermission.value = getPermissionDataUseCase.execute(permissionNotGrantedList)
    }

    fun checkForceLogin() {
        val forceLoginAB = discoverGetConfigRepository.checkForceLoginWelcomePage()
        isForceLogin.value = forceLoginAB
        onBoardingShareData.setFinishPermission()
    }

    fun checkCurrentAndroidVersion(permissionNotGrantedList: java.util.ArrayList<String>) {
        onAndroidBelowAndroidQ.value = permissionNotGrantedList
    }

    fun firebaseTrackAnalytic(linkDes: String) {
        analyticManagerInterface.trackEvent(
            HashMap<String, Any>().apply {
                put(
                    MeasurementConstant.Key.KEY_EVENT_NAME,
                    MeasurementConstant.Event.EVENT_CLICK
                )
                put(
                    MeasurementConstant.Key.KEY_LINK_TYPE,
                    MeasurementConstant.Discover.LinkType.LINK_TYPE_BUTTON
                )
                put(MeasurementConstant.Key.KEY_LINK_DESC, linkDes)
            }
        )
    }

    fun onGetAgreeButtonText() {
        onShowAgreeButton.value = getPermissionAgreeButtonUseCase.execute()
    }

    fun isFirstOpenPermission(): Boolean = discoverGetConfigRepository.isOpenPermissionAlready()

    fun savePermissionFlag() = discoverGetConfigRepository.saveFlagPermission()
}
