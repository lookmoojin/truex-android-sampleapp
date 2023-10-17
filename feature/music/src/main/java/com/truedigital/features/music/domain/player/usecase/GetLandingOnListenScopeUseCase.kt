package com.truedigital.features.music.domain.player.usecase

import com.truedigital.features.music.data.player.repository.MusicPlayerCacheRepository
import javax.inject.Inject

interface GetLandingOnListenScopeUseCase {
    fun execute(): Boolean
}

class GetLandingOnListenScopeUseCaseImpl @Inject constructor(
    private val musicPlayerCacheRepository: MusicPlayerCacheRepository
) : GetLandingOnListenScopeUseCase {

    override fun execute(): Boolean {
        return musicPlayerCacheRepository.getLandingOnListenScope()
    }
}
