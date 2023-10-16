package com.truedigital.common.share.data.coredata.domain.usecase

import com.google.android.gms.ads.admanager.AdManagerAdView
import com.truedigital.common.share.data.coredata.data.repository.PreLoadAdsCacheRepository
import javax.inject.Inject

interface GetPreLoadAdsViewUseCase {
    fun execute(adId: String): AdManagerAdView?
}

class GetPreLoadAdsViewUseCaseImpl @Inject constructor(
    private val preLoadAdsCacheRepository: PreLoadAdsCacheRepository
) : GetPreLoadAdsViewUseCase {
    override fun execute(adId: String): AdManagerAdView? {
        return preLoadAdsCacheRepository.getAds(adId = adId)
    }
}
