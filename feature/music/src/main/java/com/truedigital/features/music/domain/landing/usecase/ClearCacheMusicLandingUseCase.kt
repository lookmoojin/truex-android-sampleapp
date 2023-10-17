package com.truedigital.features.music.domain.landing.usecase

import com.truedigital.features.music.data.landing.repository.CacheMusicLandingRepository
import javax.inject.Inject

interface ClearCacheMusicLandingUseCase {
    fun execute()
}

class ClearCacheMusicLandingUseCaseImpl @Inject constructor(
    private val cacheMusicLandingRepository: CacheMusicLandingRepository
) : ClearCacheMusicLandingUseCase {
    override fun execute() {
        cacheMusicLandingRepository.clearCache()
    }
}
