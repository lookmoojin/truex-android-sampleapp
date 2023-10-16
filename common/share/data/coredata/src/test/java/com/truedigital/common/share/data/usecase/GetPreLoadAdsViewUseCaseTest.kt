package com.truedigital.common.share.data.usecase

import com.google.android.gms.ads.admanager.AdManagerAdView
import com.truedigital.common.share.data.coredata.data.repository.PreLoadAdsCacheRepository
import com.truedigital.common.share.data.coredata.domain.usecase.GetPreLoadAdsViewUseCase
import com.truedigital.common.share.data.coredata.domain.usecase.GetPreLoadAdsViewUseCaseImpl
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GetPreLoadAdsViewUseCaseTest {
    private lateinit var getPreLoadAdsViewUseCase: GetPreLoadAdsViewUseCase
    private val preLoadAdsCacheRepository: PreLoadAdsCacheRepository = mockk()

    @BeforeEach
    fun setUp() {
        getPreLoadAdsViewUseCase = GetPreLoadAdsViewUseCaseImpl(
            preLoadAdsCacheRepository = preLoadAdsCacheRepository
        )
    }

    @Test
    fun testGetPreLoadAdsView_Success_Should_ReturnAdsView() {
        val adView = AdManagerAdView(mockk())
        every { preLoadAdsCacheRepository.getAds("adId") } returns adView

        assertEquals(adView, getPreLoadAdsViewUseCase.execute("adId"))
    }

    @Test
    fun testGetPreLoadAdsView_Failed_Should_ReturnNull() {
        every { preLoadAdsCacheRepository.getAds("adId") } returns null
        assertEquals(null, getPreLoadAdsViewUseCase.execute("adId"))
    }
}
