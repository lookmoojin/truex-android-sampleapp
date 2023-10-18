package com.truedigital.features.music.domain.track.usecase

import com.truedigital.features.music.data.playlist.repository.MusicPlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface RemoveTrackUseCase {
    fun execute(playlistId: Int, trackIds: List<Int>): Flow<Boolean>
}

class RemoveTrackUseCaseImpl @Inject constructor(
    private val musicPlaylistRepository: MusicPlaylistRepository
) : RemoveTrackUseCase {
    override fun execute(playlistId: Int, trackIds: List<Int>): Flow<Boolean> {
        return musicPlaylistRepository.removeTrack(playlistId, trackIds)
            .map { it.value }
    }
}
