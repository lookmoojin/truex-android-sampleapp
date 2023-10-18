package com.truedigital.features.music.domain.player.usecase

import com.truedigital.features.music.data.player.repository.MusicPlayerCacheRepository
import javax.inject.Inject

interface SetLandingOnListenScopeUseCase {
    fun execute(isLandingOnListenScope: Boolean)
}

class SetLandingOnListenScopeUseCaseImpl @Inject constructor(
    private val musicPlayerCacheRepository: MusicPlayerCacheRepository
) : SetLandingOnListenScopeUseCase {

    override fun execute(isLandingOnListenScope: Boolean) {
        musicPlayerCacheRepository.setLandingOnListenScope(isLandingOnListenScope)
    }
}
