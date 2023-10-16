package com.truedigital.common.share.data.repository

import com.google.android.gms.ads.admanager.AdManagerAdView
import com.truedigital.common.share.data.coredata.data.repository.PreLoadAdsCacheRepository
import com.truedigital.common.share.data.coredata.data.repository.PreLoadAdsCacheRepositoryImpl
import io.mockk.mockk
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PreLoadAdsCacheRepositoryTest {
    private lateinit var preLoadAdsCacheRepository: PreLoadAdsCacheRepository

    @BeforeEach
    fun setUp() {
        preLoadAdsCacheRepository = PreLoadAdsCacheRepositoryImpl()
    }

    @Test
    fun testPreLoadAdsCacheRepository_whenSaveNewAd() {
        val adId = "adId"
        val adView = AdManagerAdView(mockk())

        preLoadAdsCacheRepository.addAdsCache(adId = adId, adView = adView)
        assertEquals(adView, preLoadAdsCacheRepository.getAds(adId))
    }

    @Test
    fun testPreLoadAdsCacheRepository_whenGetAd() {
        val adId = "adId"
        val adView = AdManagerAdView(mockk())

        preLoadAdsCacheRepository.addAdsCache(adId = adId, adView = adView)
        val result = preLoadAdsCacheRepository.getAds(adId)
        assertEquals(adView, result)
    }

    @Test
    fun testPreLoadAdsCacheRepository_whenGetAdButWrongAdId() {
        val adId = "adId"
        val adView = AdManagerAdView(mockk())
        val adId2 = "adId2"

        preLoadAdsCacheRepository.addAdsCache(adId = adId, adView = adView)
        val result = preLoadAdsCacheRepository.getAds(adId2)
        assertNull(result)
    }
}
