package com.truedigital.features.music.domain.myplaylist.usecase

import com.truedigital.features.music.data.playlist.repository.MusicPlaylistRepository
import com.truedigital.features.music.domain.myplaylist.model.MusicMyPlaylistModel
import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.util.PagedResults
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface GetMyPlaylistShelfUseCase {
    fun execute(): Flow<List<MusicMyPlaylistModel.MyPlaylistModel>>
}

class GetMyPlaylistShelfUseCaseImpl @Inject constructor(
    private val musicPlaylistRepository: MusicPlaylistRepository
) : GetMyPlaylistShelfUseCase {

    companion object {
        private const val LANG_EN = "en"
    }

    override fun execute(): Flow<List<MusicMyPlaylistModel.MyPlaylistModel>> {
        return musicPlaylistRepository.getMyPlaylists()
            .map(::mapMyPlaylistModel)
    }

    private fun mapMyPlaylistModel(response: PagedResults<Playlist>?): List<MusicMyPlaylistModel.MyPlaylistModel> {
        return response?.let { paged ->
            paged.results.mapIndexed { index, playlist ->
                MusicMyPlaylistModel.MyPlaylistModel(
                    playlistId = playlist.id,
                    title = playlist.name.find { it.language == LANG_EN }?.value.orEmpty(),
                    coverImage = playlist.coverImage.find { it.language == LANG_EN }?.value.orEmpty(),
                    trackCount = playlist.trackCount,
                    index = index
                )
            }
        } ?: emptyList()
    }
}
