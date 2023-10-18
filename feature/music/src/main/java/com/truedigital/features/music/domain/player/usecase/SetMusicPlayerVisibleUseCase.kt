package com.truedigital.features.music.domain.player.usecase

import com.truedigital.features.music.data.player.repository.MusicPlayerCacheRepository
import javax.inject.Inject

interface SetMusicPlayerVisibleUseCase {
    fun execute(isVisible: Boolean)
}

class SetMusicPlayerVisibleUseCaseImpl @Inject constructor(
    private val musicPlayerCacheRepository: MusicPlayerCacheRepository
) : SetMusicPlayerVisibleUseCase {

    override fun execute(isVisible: Boolean) {
        musicPlayerCacheRepository.setMusicPlayerVisible(isVisible)
    }
}
