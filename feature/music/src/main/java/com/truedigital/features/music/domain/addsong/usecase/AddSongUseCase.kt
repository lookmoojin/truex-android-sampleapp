package com.truedigital.features.music.domain.addsong.usecase

import com.truedigital.features.music.data.addsong.repository.AddSongRepository
import com.truedigital.features.tuned.data.playlist.model.Playlist
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface AddSongUseCase {
    fun execute(playlistId: String, list: List<Int>): Flow<Playlist>
}

class AddSongUseCaseImpl @Inject constructor(
    private val addSongRepository: AddSongRepository
) : AddSongUseCase {
    override fun execute(
        playlistId: String,
        list: List<Int>
    ): Flow<Playlist> {
        return addSongRepository.addTracksToPlaylist(playlistId, list)
    }
}
