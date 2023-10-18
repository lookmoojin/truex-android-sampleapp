package com.truedigital.features.music.domain.queue.usecase

import com.truedigital.features.music.data.queue.repository.CacheTrackQueueRepository
import javax.inject.Inject

interface ClearCacheTrackQueueUseCase {
    fun execute()
}

class ClearCacheTrackQueueUseCaseImpl @Inject constructor(
    private val cacheTrackQueueRepository: CacheTrackQueueRepository
) : ClearCacheTrackQueueUseCase {

    override fun execute() {
        cacheTrackQueueRepository.clearCacheTrackQueue()
    }
}
