package com.truedigital.features.music.domain.myplaylist.usecase

import com.truedigital.features.music.data.playlist.repository.MusicPlaylistRepository
import com.truedigital.features.music.domain.myplaylist.model.MyPlaylistItemType
import com.truedigital.features.music.domain.myplaylist.model.MyPlaylistModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface GetMyPlaylistUseCase {
    fun execute(playlistId: String): Flow<MyPlaylistModel>
}

class GetMyPlaylistUseCaseImpl @Inject constructor(
    private val musicPlaylistRepository: MusicPlaylistRepository
) : GetMyPlaylistUseCase {

    companion object {
        const val EN = "en"
    }

    override fun execute(playlistId: String): Flow<MyPlaylistModel> {
        return musicPlaylistRepository.getPlaylist(playlistId).map { playlist ->

            val coverImage = playlist.coverImage.find { title ->
                title.language == EN
            }?.value.orEmpty()

            val name = playlist.name.find { title ->
                title.language == EN
            }?.value.orEmpty()

            MyPlaylistModel(
                id = playlist.id,
                coverImage = coverImage,
                playlistName = name,
                count = playlist.trackCount,
                itemId = 1,
                itemType = MyPlaylistItemType.HEADER
            )
        }
    }
}
