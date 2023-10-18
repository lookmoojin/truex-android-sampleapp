package com.truedigital.features.music.domain.ads.usecase

import com.truedigital.features.music.data.ads.repository.CacheMusicPlayerAdsRepository
import javax.inject.Inject

interface ClearCacheMusicPlayerAdsUseCase {
    fun execute()
}

class ClearCacheMusicPlayerAdsUseCaseImpl @Inject constructor(
    private val cacheMusicPlayerAdsRepository: CacheMusicPlayerAdsRepository
) : ClearCacheMusicPlayerAdsUseCase {

    override fun execute() {
        cacheMusicPlayerAdsRepository.resetCountAds()
        cacheMusicPlayerAdsRepository.resetFirstTime()
    }
}
