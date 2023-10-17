package com.truedigital.features.music.domain.ads.usecase

import com.truedigital.features.music.data.ads.repository.CacheMusicPlayerAdsRepository
import javax.inject.Inject

interface IsShowMusicPlayerAdsUseCase {
    fun execute(): Boolean
}

class IsShowMusicPlayerAdsUseCaseImpl @Inject constructor(
    private val cacheMusicPlayerAdsRepository: CacheMusicPlayerAdsRepository
) : IsShowMusicPlayerAdsUseCase {

    companion object {
        private const val COUNT_ADS = 3
    }

    override fun execute(): Boolean {
        return if (cacheMusicPlayerAdsRepository.isFirstTime()) {
            cacheMusicPlayerAdsRepository.updateFirstTime()
            true
        } else {
            if (cacheMusicPlayerAdsRepository.getAction()) {
                cacheMusicPlayerAdsRepository.resetAction()
                validateCountAds()
            } else {
                false
            }
        }
    }

    private fun validateCountAds(): Boolean {
        cacheMusicPlayerAdsRepository.countAds()
        return if (cacheMusicPlayerAdsRepository.getCountAds() == COUNT_ADS) {
            cacheMusicPlayerAdsRepository.resetCountAds()
            true
        } else {
            false
        }
    }
}
