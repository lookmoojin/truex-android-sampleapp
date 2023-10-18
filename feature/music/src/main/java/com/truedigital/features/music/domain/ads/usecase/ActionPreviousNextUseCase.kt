package com.truedigital.features.music.domain.ads.usecase

import com.truedigital.features.music.data.ads.repository.CacheMusicPlayerAdsRepository
import com.truedigital.features.tuned.service.music.controller.MusicPlayerController.Companion.SKIP_TO_PREVIOUS_THRESHOLD
import javax.inject.Inject

interface ActionPreviousNextUseCase {
    fun execute(isPrevious: Boolean, position: Long? = null)
}

class ActionPreviousNextUseCaseImpl @Inject constructor(
    private val cacheMusicPlayerAdsRepository: CacheMusicPlayerAdsRepository
) : ActionPreviousNextUseCase {

    override fun execute(isPrevious: Boolean, position: Long?) {
        if (isPrevious) {
            position?.let { _position ->
                if (_position < SKIP_TO_PREVIOUS_THRESHOLD) {
                    cacheMusicPlayerAdsRepository.action()
                }
            }
        } else {
            cacheMusicPlayerAdsRepository.action()
        }
    }
}
