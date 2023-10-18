package com.truedigital.features.music.domain.search.usecase

import com.truedigital.features.music.data.search.model.response.Hit
import com.truedigital.features.music.data.search.repository.MusicSearchRepository
import com.truedigital.features.music.domain.search.model.MusicSearchModel
import com.truedigital.features.music.domain.search.model.MusicType
import com.truedigital.features.music.domain.search.model.ThemeType
import com.truedigital.features.music.extensions.applyMusicItemTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface GetSearchPlaylistUseCase {
    fun execute(
        query: String,
        theme: ThemeType,
        offset: String
    ): Flow<List<MusicSearchModel>>
}

class GetSearchPlaylistUseCaseImpl @Inject constructor(
    private val musicSearchRepository: MusicSearchRepository
) : GetSearchPlaylistUseCase {

    companion object {
        private const val COUNT = "20"
    }

    override fun execute(
        query: String,
        theme: ThemeType,
        offset: String
    ): Flow<List<MusicSearchModel>> {

        return musicSearchRepository.getSearchPlaylistQuery(
            query = query,
            offset = offset,
            count = COUNT
        )
            .map { musicSearchResponse ->
                val playlistList = mutableListOf<MusicSearchModel>()
                if (musicSearchResponse != null) {
                    for (i in musicSearchResponse) {
                        i.results?.hits?.hits?.let { items ->
                            for (item in items) {
                                playlistList.add(getPlaylistItem(item, theme))
                            }
                        }
                    }
                }
                playlistList
            }
    }

    private fun getPlaylistItem(playlist: Hit, themeType: ThemeType): MusicSearchModel {
        return MusicSearchModel.MusicItemModel(
            id = playlist.id,
            title = playlist.source?.name,
            description = "",
            thumb = playlist.source?.images?.firstOrNull()?.value.orEmpty(),
            type = MusicType.PLAYLIST,
            musicTheme = applyMusicItemTheme(themeType)
        )
    }
}
