package com.truedigital.features.music.domain.myplaylist.usecase

import com.truedigital.features.music.data.playlist.repository.MusicPlaylistRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface DeleteMyPlaylistUseCase {
    fun execute(playlistId: Int): Flow<Any>
}

class DeleteMyPlaylistUseCaseImpl @Inject constructor(
    private val musicPlaylistRepository: MusicPlaylistRepository
) : DeleteMyPlaylistUseCase {

    override fun execute(playlistId: Int): Flow<Any> =
        musicPlaylistRepository.deletePlaylist(playlistId)
}
