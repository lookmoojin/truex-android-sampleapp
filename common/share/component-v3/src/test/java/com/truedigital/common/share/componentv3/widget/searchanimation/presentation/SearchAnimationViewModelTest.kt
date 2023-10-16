package com.truedigital.common.share.componentv3.widget.searchanimation.presentation

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.validateMockitoUsage
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.common.share.componentv3.widget.searchanimation.model.SearchAnimationData
import com.truedigital.common.share.componentv3.widget.searchanimation.usecase.CheckDateSearchAnimationUseCase
import com.truedigital.common.share.componentv3.widget.searchanimation.usecase.GetSearchAnimationUseCase
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertNotNull
import kotlin.test.assertNull

@ExtendWith(InstantTaskExecutorExtension::class)
class SearchAnimationViewModelTest {

    private lateinit var viewModel: SearchAnimationViewModel
    private var getSearchAnimationUseCase: GetSearchAnimationUseCase = mock()
    private var checkDateSearchAnimationUseCase: CheckDateSearchAnimationUseCase = mock()

    @BeforeEach
    fun setup() {
        RxJavaPlugins.setIoSchedulerHandler { Schedulers.trampoline() }
        RxJavaPlugins.setComputationSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.trampoline() }
        RxAndroidPlugins.setMainThreadSchedulerHandler { Schedulers.trampoline() }

        viewModel = SearchAnimationViewModel(
            getSearchAnimationUseCase = getSearchAnimationUseCase,
            checkDateSearchAnimationUseCase = checkDateSearchAnimationUseCase
        )
    }

    @AfterEach
    fun clean() {
        RxJavaPlugins.reset()
        RxAndroidPlugins.reset()
        validateMockitoUsage()
    }

    @Test
    fun haveAdsUrlAndDateIsValid() {
        val searchAnimationData = SearchAnimationData()
        searchAnimationData.apply {
            adsUrl = "https://fn.dmpcdn.com/TrueIDApp/ads/trueid_ads.json"
            deeplink = "https://news.trueid.net/detail/0lVQWMgoog59"
            searchAnimationTime.startDate = "2021-07-02 17:00:00"
            searchAnimationTime.endDate = "2021-09-02 17:00:00"
        }
        val searchAnimation = Single.just(searchAnimationData)

        whenever(getSearchAnimationUseCase.execute(any())).thenReturn(searchAnimation)
        whenever(checkDateSearchAnimationUseCase.execute(any())).thenReturn(true)
        viewModel.getSearchAnimation("today")
        assertNotNull(viewModel.onShowSearchAnimationData().value)
    }

    @Test
    fun haveAdsUrlAndDateIsNotValid() {
        val searchAnimationData = SearchAnimationData()
        searchAnimationData.apply {
            adsUrl = "https://fn.dmpcdn.com/TrueIDApp/ads/trueid_ads.json"
            deeplink = "https://news.trueid.net/detail/0lVQWMgoog59"
            searchAnimationTime.startDate = "2021-07-02 17:00:00"
            searchAnimationTime.endDate = "2021-08-02 17:00:00"
        }
        val searchAnimation = Single.just(searchAnimationData)

        whenever(getSearchAnimationUseCase.execute(any())).thenReturn(searchAnimation)
        whenever(checkDateSearchAnimationUseCase.execute(any())).thenReturn(false)
        viewModel.getSearchAnimation("today")
        assertNull(viewModel.onShowSearchAnimationData().value)
    }

    @Test
    fun noHaveAdsUrlAndDateIsValid() {
        val searchAnimationData = SearchAnimationData()
        searchAnimationData.apply {
            adsUrl = ""
            deeplink = ""
            searchAnimationTime.startDate = "2021-07-02 17:00:00"
            searchAnimationTime.endDate = "2021-09-02 17:00:00"
        }
        val searchAnimation = Single.just(searchAnimationData)

        whenever(getSearchAnimationUseCase.execute(any())).thenReturn(searchAnimation)
        whenever(checkDateSearchAnimationUseCase.execute(any())).thenReturn(true)
        viewModel.getSearchAnimation("today")
        assertNull(viewModel.onShowSearchAnimationData().value)
    }

    @Test
    fun noHaveAdsUrlAndDateIsNotValid() {
        val searchAnimationData = SearchAnimationData()
        searchAnimationData.apply {
            adsUrl = ""
            deeplink = ""
            searchAnimationTime.startDate = "2021-07-02 17:00:00"
            searchAnimationTime.endDate = "2021-08-02 17:00:00"
        }
        val searchAnimation = Single.just(searchAnimationData)

        whenever(getSearchAnimationUseCase.execute(any())).thenReturn(searchAnimation)
        whenever(checkDateSearchAnimationUseCase.execute(any())).thenReturn(false)
        viewModel.getSearchAnimation("today")
        assertNull(viewModel.onShowSearchAnimationData().value)
    }
}
