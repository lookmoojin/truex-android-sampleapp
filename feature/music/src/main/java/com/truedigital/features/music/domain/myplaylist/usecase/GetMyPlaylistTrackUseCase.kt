package com.truedigital.features.music.domain.myplaylist.usecase

import com.truedigital.features.music.data.track.repository.MusicTrackRepository
import com.truedigital.features.tuned.data.track.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface GetMyPlaylistTrackUseCase {
    fun execute(playlistId: Int, count: Int = 99): Flow<List<Track>>
}

class GetMyPlaylistTrackUseCaseImpl @Inject constructor(
    private val musicTrackRepository: MusicTrackRepository
) : GetMyPlaylistTrackUseCase {
    override fun execute(playlistId: Int, count: Int): Flow<List<Track>> {
        return musicTrackRepository.getMyPlaylistTrackList(
            playlistId = playlistId,
            offset = 1,
            count = count
        ).map { trackList ->
            trackList.map { track ->
                track.copy(name = track.nameTranslations)
            }
        }
    }
}
