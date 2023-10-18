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

interface GetSearchArtistUseCase {
    fun execute(
        query: String,
        theme: ThemeType,
        offset: String
    ): Flow<List<MusicSearchModel>>
}

class GetSearchArtistUseCaseImpl @Inject constructor(
    private val musicSearchRepository: MusicSearchRepository
) : GetSearchArtistUseCase {

    companion object {
        private const val COUNT = "20"
    }

    override fun execute(
        query: String,
        theme: ThemeType,
        offset: String
    ): Flow<List<MusicSearchModel>> {

        return musicSearchRepository.getSearchArtistQuery(
            query = query,
            offset = offset,
            count = COUNT
        )
            .map { musicSearchResponse ->
                val artistList = mutableListOf<MusicSearchModel>()
                if (musicSearchResponse != null) {
                    for (i in musicSearchResponse) {
                        i.results?.hits?.hits?.let { items ->
                            for (item in items) {
                                artistList.add(getArtistItem(item, theme))
                            }
                        }
                    }
                }
                artistList
            }
    }

    private fun getArtistItem(music: Hit, theme: ThemeType): MusicSearchModel.MusicItemModel {
        return MusicSearchModel.MusicItemModel(
            id = music.id,
            title = music.source?.name,
            description = "",
            thumb = music.source?.meta?.firstOrNull()?.image.orEmpty(),
            type = MusicType.ARTIST,
            musicTheme = applyMusicItemTheme(theme)
        )
    }
}
