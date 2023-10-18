package com.truedigital.common.share.data.coredata.data.repository

import com.google.android.gms.ads.admanager.AdManagerAdView
import javax.inject.Inject

interface PreLoadAdsCacheRepository {
    fun addAdsCache(adId: String, adView: AdManagerAdView)
    fun getAds(adId: String): AdManagerAdView?
}

class PreLoadAdsCacheRepositoryImpl @Inject constructor() : PreLoadAdsCacheRepository {
    private var cacheAdViewList: HashMap<String, AdManagerAdView> = hashMapOf()
    override fun addAdsCache(adId: String, adView: AdManagerAdView) {
        cacheAdViewList[adId] = adView
    }

    override fun getAds(adId: String): AdManagerAdView? {
        return cacheAdViewList[adId]
    }
}
