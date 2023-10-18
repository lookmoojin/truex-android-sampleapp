package com.truedigital.features.music.domain.player.usecase

import com.truedigital.features.music.data.player.repository.MusicPlayerCacheRepository
import javax.inject.Inject

interface GetMusicPlayerVisibleUseCase {
    fun execute(): Boolean
}

class GetMusicPlayerVisibleUseCaseImpl @Inject constructor(
    private val musicPlayerCacheRepository: MusicPlayerCacheRepository
) : GetMusicPlayerVisibleUseCase {

    override fun execute(): Boolean {
        return musicPlayerCacheRepository.getMusicPlayerVisible()
    }
}
