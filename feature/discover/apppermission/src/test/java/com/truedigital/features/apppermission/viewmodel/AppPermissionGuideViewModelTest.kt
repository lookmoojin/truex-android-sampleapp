package com.truedigital.features.apppermission.viewmodel

import android.Manifest
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.validateMockitoUsage
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.common.share.analytics.measurement.AnalyticManagerInterface
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant
import com.truedigital.common.share.componentv3.widget.viewpageautoscroll.domain.BannerBaseItemModel
import com.truedigital.features.apppermission.presenation.AppPermissionGuideViewModel
import com.truedigital.features.apppermission.usecase.GetPermissionAgreeButtonUseCase
import com.truedigital.features.apppermission.usecase.GetPermissionDataUseCase
import com.truedigital.features.onboarding.shareviewmodel.OnBoardingShareData
import com.truedigital.features.onboarding.whatsnew.data.repository.DiscoverGetConfigRepository
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals

@ExtendWith(InstantTaskExecutorExtension::class)
class AppPermissionGuideViewModelTest {

    private val getPermissionDataUseCase: GetPermissionDataUseCase = mock()
    private val discoverGetConfigRepository: DiscoverGetConfigRepository = mock()
    private val onBoardingShareData: OnBoardingShareData = mock()
    private val analyticManager: AnalyticManagerInterface = mock()
    private val getPermissionAgreeButtonUseCase: GetPermissionAgreeButtonUseCase = mock()
    private lateinit var appPermissionGuideViewModel: AppPermissionGuideViewModel

    @BeforeEach
    fun setup() {
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setMainThreadSchedulerHandler { Schedulers.trampoline() }
        appPermissionGuideViewModel = AppPermissionGuideViewModel(
            discoverGetConfigRepository = discoverGetConfigRepository,
            getPermissionAgreeButtonUseCase = getPermissionAgreeButtonUseCase,
            getPermissionDataUseCase = getPermissionDataUseCase,
            onBoardingShareData = onBoardingShareData,
            analyticManagerInterface = analyticManager
        )
    }

    @AfterEach
    fun clean() {
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
        validateMockitoUsage()
    }

    @Test
    fun when_have_Permission_No_Granted() {

        val permissions = arrayListOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        whenever(getPermissionDataUseCase.execute(permissions)).thenReturn(
            dataGetPermissionDataUseCaseMock()
        )
        appPermissionGuideViewModel.loadPermissionNotGranted(permissions)

        assert(getPermissionDataUseCase.execute(permissions).isNotEmpty())
        assertEquals(appPermissionGuideViewModel.onShowAppPermission.value?.get(0)?.thum, "url_location")
        assertEquals(appPermissionGuideViewModel.onShowAppPermission.value?.get(1)?.thum, "url_storage")
    }

    @Test
    fun when_not_have_Permission_No_Granted() {

        val permissions = arrayListOf<String>()
        appPermissionGuideViewModel.loadPermissionNotGranted(permissions)

        whenever(getPermissionDataUseCase.execute(permissions)).thenReturn(mutableListOf())

        assert(getPermissionDataUseCase.execute(permissions).isEmpty())
    }

    @Test
    fun loadAgreeButton_Success_ShouldReturnText() {
        whenever(getPermissionAgreeButtonUseCase.execute()).thenReturn("button")
        appPermissionGuideViewModel.onGetAgreeButtonText()
        assert(getPermissionAgreeButtonUseCase.execute().isNotEmpty())
        assertEquals(appPermissionGuideViewModel.onShowAgreeButton.value, "button")
    }

    @Test
    fun checkForceLogin_onBoardingSetFinishPermission() {
        whenever(discoverGetConfigRepository.checkForceLoginWelcomePage()).thenReturn(true)

        appPermissionGuideViewModel.checkForceLogin()
        assertEquals(appPermissionGuideViewModel.isForceLogin.value, true)
        verify(onBoardingShareData, times(1)).setFinishPermission()
    }

    @Test
    fun checkCurrentAndroidVersion_ShouldReturnListOfVersion() {
        appPermissionGuideViewModel.checkCurrentAndroidVersion(arrayListOf("A", "B"))
        assertEquals(appPermissionGuideViewModel.onAndroidBelowAndroidQ.value?.get(0), "A")
        assertEquals(appPermissionGuideViewModel.onAndroidBelowAndroidQ.value?.get(1), "B")
    }

    @Test
    fun testTrackingAnalytic_ShouldSentAnalytic() {
        val eventData = hashMapOf<String, Any>(
            MeasurementConstant.Key.KEY_EVENT_NAME to MeasurementConstant.Event.EVENT_CLICK,
            MeasurementConstant.Key.KEY_LINK_TYPE to MeasurementConstant.Discover.LinkType.LINK_TYPE_BUTTON,
            MeasurementConstant.Key.KEY_LINK_DESC to "linkDesc"
        )
        appPermissionGuideViewModel.firebaseTrackAnalytic("linkDesc")
        verify(analyticManager, times(1)).trackEvent(eventData)
    }

    @Test
    fun testSavePermissionFlag_ShouldSaveFlagPermission() {
        appPermissionGuideViewModel.savePermissionFlag()
        verify(discoverGetConfigRepository, times(1)).saveFlagPermission()
    }

    @Test
    fun testCheckIsFirstOpenPermission_ShouldReturnTrue() {
        whenever(discoverGetConfigRepository.isOpenPermissionAlready()).thenReturn(true)
        val test = appPermissionGuideViewModel.isFirstOpenPermission()
        verify(discoverGetConfigRepository, times(1)).isOpenPermissionAlready()
        Assertions.assertEquals(test, true)
    }

    @Test
    fun testCheckIsNotFirstOpenPermission_ShouldReturnFalse() {
        whenever(discoverGetConfigRepository.isOpenPermissionAlready()).thenReturn(false)
        val test = appPermissionGuideViewModel.isFirstOpenPermission()
        verify(discoverGetConfigRepository, times(1)).isOpenPermissionAlready()
        Assertions.assertEquals(test, false)
    }

    private fun dataGetPermissionDataUseCaseMock() = mutableListOf(
        BannerBaseItemModel().apply {
            this.thum = "url_location"
        },
        BannerBaseItemModel().apply {
            this.thum = "url_storage"
        }
    )
}
