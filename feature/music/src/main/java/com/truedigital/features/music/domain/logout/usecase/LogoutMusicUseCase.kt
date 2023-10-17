package com.truedigital.features.music.domain.logout.usecase

import com.truedigital.features.music.data.player.repository.CacheServicePlayerRepository
import com.truedigital.features.music.manager.player.MusicPlayerActionManager
import javax.inject.Inject

interface LogoutMusicUseCase {
    fun execute()
}

class LogoutMusicUseCaseImpl @Inject constructor(
    private val cacheServicePlayerRepository: CacheServicePlayerRepository,
    private val musicPlayerActionManager: MusicPlayerActionManager
) : LogoutMusicUseCase {

    override fun execute() {
        if (cacheServicePlayerRepository.getServiceRunning()) {
            musicPlayerActionManager.actionClose()
        }
    }
}
