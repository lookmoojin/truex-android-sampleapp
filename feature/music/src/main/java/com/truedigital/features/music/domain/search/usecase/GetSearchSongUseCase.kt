package com.truedigital.features.music.domain.search.usecase

import com.truedigital.features.music.data.search.model.response.Artist
import com.truedigital.features.music.data.search.repository.MusicSearchRepository
import com.truedigital.features.music.domain.search.model.MusicSearchModel
import com.truedigital.features.music.domain.search.model.MusicType
import com.truedigital.features.music.domain.search.model.ThemeType
import com.truedigital.features.music.extensions.applyMusicItemTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface GetSearchSongUseCase {
    fun execute(
        query: String,
        theme: ThemeType,
        offset: String
    ): Flow<List<MusicSearchModel>>
}

class GetSearchSongUseCaseImpl @Inject constructor(
    private val musicSearchRepository: MusicSearchRepository
) : GetSearchSongUseCase {

    companion object {
        private const val COUNT = "20"
    }

    override fun execute(
        query: String,
        theme: ThemeType,
        offset: String
    ): Flow<List<MusicSearchModel>> {
        return musicSearchRepository.getSongQuery(
            query = query,
            offset = offset,
            count = COUNT
        )
            .map { song ->
                val songList = mutableListOf<MusicSearchModel>()
                song?.firstOrNull()?.results?.hits?.hits?.let { hits ->
                    for (hit in hits) {
                        val model = MusicSearchModel.MusicItemModel(
                            id = hit.id,
                            title = hit.source?.nameTranslations,
                            description = getArtistName(hit.source?.artists),
                            thumb = hit.source?.meta?.firstOrNull()?.albumImage.orEmpty(),
                            type = MusicType.SONG,
                            musicTheme = applyMusicItemTheme(theme)
                        )
                        songList.add(model)
                    }
                }
                songList
            }
    }

    private fun getArtistName(artists: List<Artist>?): String? {
        return artists?.joinToString(", ") {
            it.name.orEmpty()
        }
    }
}
