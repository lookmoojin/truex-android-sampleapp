package com.truedigital.features.music.domain.track.usecase

import com.truedigital.features.music.data.track.repository.MusicTrackRepository
import com.truedigital.features.tuned.data.track.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface GetTrackListUseCase {
    fun execute(playlistId: Int): Flow<List<Track>>
}

class GetTrackListUseCaseImpl @Inject constructor(
    private val musicTrackRepository: MusicTrackRepository
) : GetTrackListUseCase {

    override fun execute(playlistId: Int): Flow<List<Track>> {
        return musicTrackRepository.getTrackList(
            playlistId = playlistId,
            offset = 1,
            count = 99
        ).map { trackList ->
            trackList.map { track ->
                track.copy(name = track.nameTranslations)
            }
        }
    }
}
