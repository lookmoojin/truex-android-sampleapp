package com.truedigital.common.share.componentv3.widget.ads.presentation

import com.google.android.gms.ads.admanager.AdManagerAdView
import com.truedigital.common.share.data.coredata.domain.usecase.GetPreLoadAdsViewUseCase
import com.truedigital.share.data.prasarn.manager.PrasarnManager
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals

@ExtendWith(InstantTaskExecutorExtension::class)
class AdsWidgetViewModelTest {
    private lateinit var viewModel: AdsWidgetViewModel
    private val prasarnManager: PrasarnManager = mockk()
    private val getPreLoadAdsViewUseCase: GetPreLoadAdsViewUseCase = mockk()

    @BeforeEach
    fun setUp() {
        viewModel = AdsWidgetViewModel(
            prasarnManager = prasarnManager,
            getPreLoadAdsViewUseCase = getPreLoadAdsViewUseCase
        )
    }

    @Test
    fun testAdsWidgetViewModel_GetPPID_Should_Return_String() = runTest {
        every { prasarnManager.getPPID() } returns "abc"
        val result = viewModel.getAdsPPID()
        assertEquals("abc", result)
    }

    @Test
    fun testAdsWidgetViewModel_GetPPID_Return_Empty() = runTest {
        every { prasarnManager.getPPID() } returns ""
        val result = viewModel.getAdsPPID()
        assertEquals("", result)
    }

    @Test
    fun testAdsWidgetViewModel_GetCacheAdsView_Has_Cache_Should_Return_AdsView() = runTest {
        val adViewMock = AdManagerAdView(mockk())
        every { getPreLoadAdsViewUseCase.execute("abc") } returns adViewMock
        val result = viewModel.getCacheAdsView("abc")
        assertEquals(adViewMock, result)
    }

    @Test
    fun testAdsWidgetViewModel_GetCacheAdsView_NotHave_Cache_Should_Return_Null() = runTest {
        every { getPreLoadAdsViewUseCase.execute("abc") } returns null
        val result = viewModel.getCacheAdsView("abc")
        assertEquals(null, result)
    }
}
