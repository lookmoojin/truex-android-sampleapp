package com.truedigital.features.music.widget.ads

import com.google.android.gms.ads.AdSize
import com.nhaarman.mockitokotlin2.validateMockitoUsage
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.livedata.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals

@ExtendWith(InstantTaskExecutorExtension::class)
internal class MusicAdsBannerViewModelTest {

    private lateinit var musicAdsBannerViewModel: MusicAdsBannerViewModel
    private val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()

    @BeforeEach
    fun setUp() {
        musicAdsBannerViewModel = MusicAdsBannerViewModel()
        Dispatchers.setMain(dispatcher)
    }

    @AfterEach
    fun tearDown() {
        validateMockitoUsage()
        Dispatchers.resetMain()
    }

    @Test
    fun renderAds_currentDeviceIsMobile_returnAdsSizeMobile() = runTest {
        // Given
        val mobileSize = "[FLUID,BANNER,LARGE_BANNER,LEADERBOARD]"
        val tabletSize = "[LEADERBOARD,LARGE_BANNER]"

        // When
        musicAdsBannerViewModel.renderAds(false, mobileSize, tabletSize)

        // Then
        assertEquals(musicAdsBannerViewModel.onShowAdsView().getOrAwaitValue(), true)
        musicAdsBannerViewModel.onRenderAdsView().getOrAwaitValue().also { adsSizeList ->
            assertTrue(adsSizeList.isNotEmpty())
            assertEquals(adsSizeList.getOrNull(0), AdSize.FLUID)
            assertEquals(adsSizeList.getOrNull(1), AdSize.BANNER)
            assertEquals(adsSizeList.getOrNull(2), AdSize.LARGE_BANNER)
            assertEquals(adsSizeList.getOrNull(3), AdSize.LEADERBOARD)
        }
    }

    @Test
    fun renderAds_currentDeviceIsTablet_returnAdsSizeTablet() = runTest {
        // Given
        val mobileSize = "[FLUID,BANNER]"
        val tabletSize = "[LEADERBOARD,LARGE_BANNER,FLUID,BANNER]"

        // When
        musicAdsBannerViewModel.renderAds(true, mobileSize, tabletSize)

        // Then
        assertEquals(musicAdsBannerViewModel.onShowAdsView().getOrAwaitValue(), true)
        musicAdsBannerViewModel.onRenderAdsView().getOrAwaitValue().also { adsSizeList ->
            assertTrue(adsSizeList.isNotEmpty())
            assertEquals(adsSizeList.getOrNull(0), AdSize.LEADERBOARD)
            assertEquals(adsSizeList.getOrNull(1), AdSize.LARGE_BANNER)
            assertEquals(adsSizeList.getOrNull(2), AdSize.FLUID)
            assertEquals(adsSizeList.getOrNull(3), AdSize.BANNER)
        }
    }

    @Test
    fun renderAds_currentDeviceIsMobile_haveCustomSize_returnAdsSizeMobile() = runTest {
        // Given
        val mobileSize = "[LARGE_BANNER,[320,50],[320,100]]"
        val tabletSize = "[LEADERBOARD,LARGE_BANNER]"

        // When
        musicAdsBannerViewModel.renderAds(false, mobileSize, tabletSize)

        // Then
        assertEquals(musicAdsBannerViewModel.onShowAdsView().getOrAwaitValue(), true)
        musicAdsBannerViewModel.onRenderAdsView().getOrAwaitValue().also { adsSizeList ->
            assertTrue(adsSizeList.isNotEmpty())
            assertEquals(adsSizeList.getOrNull(0), AdSize.LARGE_BANNER)

            adsSizeList.getOrNull(1).also { adSize ->
                assertEquals(adSize?.width, 320)
                assertEquals(adSize?.height, 50)
            }

            adsSizeList.getOrNull(2).also { adSize ->
                assertEquals(adSize?.width, 320)
                assertEquals(adSize?.height, 100)
            }
        }
    }

    @Test
    fun renderAds_currentDeviceIsTablet_haveCustomSize_returnAdsSizeTablet() = runTest {
        // Given
        val mobileSize = "[LARGE_BANNER]"
        val tabletSize = "[LEADERBOARD,[320,50],[320,100]]"

        // When
        musicAdsBannerViewModel.renderAds(true, mobileSize, tabletSize)

        // Then
        assertEquals(musicAdsBannerViewModel.onShowAdsView().getOrAwaitValue(), true)
        musicAdsBannerViewModel.onRenderAdsView().getOrAwaitValue().also { adsSizeList ->
            assertTrue(adsSizeList.isNotEmpty())
            assertEquals(adsSizeList.getOrNull(0), AdSize.LEADERBOARD)

            adsSizeList.getOrNull(1).also { adSize ->
                assertEquals(adSize?.width, 320)
                assertEquals(adSize?.height, 50)
            }

            adsSizeList.getOrNull(2).also { adSize ->
                assertEquals(adSize?.width, 320)
                assertEquals(adSize?.height, 100)
            }
        }
    }

    @Test
    fun renderAds_currentDeviceIsMobile_mobileSizeIsEmpty_hideAds() = runTest {
        // Given
        val mobileSize = ""
        val tabletSize = "[LEADERBOARD,LARGE_BANNER]"

        // When
        musicAdsBannerViewModel.renderAds(false, mobileSize, tabletSize)

        // Then
        assertEquals(musicAdsBannerViewModel.onShowAdsView().getOrAwaitValue(), false)
    }

    @Test
    fun renderAds_currentDeviceIsMobile_tabletSizeIsEmpty_hideAds() = runTest {
        // Given
        val mobileSize = "[FLUID,BANNER]"
        val tabletSize = ""

        // When
        musicAdsBannerViewModel.renderAds(true, mobileSize, tabletSize)

        // Then
        assertEquals(musicAdsBannerViewModel.onShowAdsView().getOrAwaitValue(), false)
    }

    @Test
    fun renderAds_currentDeviceIsMobile_canNotCovertCustomSize_hideAds() = runTest {
        // Given
        val mobileSize = "[[w01,h01],[w02,h02]]"
        val tabletSize = ""

        // When
        musicAdsBannerViewModel.renderAds(false, mobileSize, tabletSize)

        // Then
        assertEquals(musicAdsBannerViewModel.onShowAdsView().getOrAwaitValue(), false)
    }

    @Test
    fun renderAds_currentDeviceIsTablet_canNotCovertCustomSize_hideAds() = runTest {
        // Given
        val mobileSize = ""
        val tabletSize = "[[w01,h01],[w02,h02]]"

        // When
        musicAdsBannerViewModel.renderAds(true, mobileSize, tabletSize)

        // Then
        assertEquals(musicAdsBannerViewModel.onShowAdsView().getOrAwaitValue(), false)
    }
}
