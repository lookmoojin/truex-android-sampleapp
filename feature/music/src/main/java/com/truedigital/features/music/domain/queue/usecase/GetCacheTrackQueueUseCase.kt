package com.truedigital.features.music.domain.queue.usecase

import com.truedigital.features.music.data.queue.repository.CacheTrackQueueRepository
import com.truedigital.features.tuned.data.track.model.Track
import javax.inject.Inject

interface GetCacheTrackQueueUseCase {
    fun execute(playlistId: Int): Pair<List<Track>, Boolean>
}

class GetCacheTrackQueueUseCaseImpl @Inject constructor(
    private val cacheTrackQueueRepository: CacheTrackQueueRepository
) : GetCacheTrackQueueUseCase {

    override fun execute(playlistId: Int): Pair<List<Track>, Boolean> {
        val trackList = cacheTrackQueueRepository.getCacheTrackQueue(playlistId)
        return if (trackList.isNotEmpty()) {
            Pair(trackList, false)
        } else {
            Pair(cacheTrackQueueRepository.getCachePlaylistTrackQueue(playlistId), true)
        }
    }
}
