package com.truedigital.features.music.domain.myplaylist.usecase

import com.truedigital.features.music.data.playlist.model.CreateNewPlaylistRequest
import com.truedigital.features.music.data.playlist.repository.MusicPlaylistRepository
import com.truedigital.features.music.data.trending.model.response.playlist.Translation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface CreateNewPlaylistUseCase {
    fun execute(playlistName: String): Flow<Int>
}

class CreateNewPlaylistUseCaseImpl @Inject constructor(
    private val musicPlaylistRepository: MusicPlaylistRepository
) : CreateNewPlaylistUseCase {

    companion object {
        private const val LANG_EN = "en"
    }

    override fun execute(playlistName: String): Flow<Int> {
        val request = CreateNewPlaylistRequest(
            name = listOf(
                Translation(
                    language = LANG_EN,
                    value = playlistName
                )
            )
        )
        return musicPlaylistRepository.postNewPlaylist(request).map { it.id }
    }
}
