package com.truedigital.features.music.domain.queue.usecase

import com.truedigital.features.music.data.queue.repository.CacheTrackQueueRepository
import com.truedigital.features.music.domain.track.usecase.GetTrackListUseCase
import com.truedigital.features.tuned.data.track.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

interface GetAllTrackQueueUseCase {
    fun execute(playlistId: Int): Flow<List<Track>>
}

class GetAllTrackQueueUseCaseImpl @Inject constructor(
    private val getTrackListUseCase: GetTrackListUseCase,
    private val cacheTrackQueueRepository: CacheTrackQueueRepository
) : GetAllTrackQueueUseCase {

    override fun execute(playlistId: Int): Flow<List<Track>> {
        val cacheTrackList = cacheTrackQueueRepository.getCacheTrackQueue(playlistId)
        return if (cacheTrackList.isNotEmpty()) {
            flowOf(cacheTrackList)
        } else {
            getTrackListUseCase.execute(playlistId)
                .map { trackList ->
                    mergeTrackList(playlistId, trackList)
                }
                .onEach { trackList ->
                    cacheTrackQueueRepository.saveCacheTrackQueue(playlistId, trackList)
                }
        }
    }

    private fun mergeTrackList(playlistId: Int, trackList: List<Track>): List<Track> {
        val result = mutableListOf<Track>()
        cacheTrackQueueRepository.getCachePlaylistTrackQueue(playlistId)
            .toMutableList()
            .forEach { track ->
                trackList.find {
                    it.id == track.id
                }?.also {
                    result.add(it)
                } ?: run {
                    result.add(track)
                }
            }
        result.removeAll(trackList)
        result.addAll(trackList)
        return result
    }
}
