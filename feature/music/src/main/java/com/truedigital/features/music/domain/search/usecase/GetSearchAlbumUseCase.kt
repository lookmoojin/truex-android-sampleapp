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

interface GetSearchAlbumUseCase {
    fun execute(
        query: String,
        theme: ThemeType,
        offset: String
    ): Flow<List<MusicSearchModel>>
}

class GetSearchAlbumUseCaseImpl @Inject constructor(
    private val musicSearchRepository: MusicSearchRepository
) : GetSearchAlbumUseCase {

    companion object {
        private const val COUNT = "20"
    }

    override fun execute(
        query: String,
        theme: ThemeType,
        offset: String
    ): Flow<List<MusicSearchModel>> {
        return musicSearchRepository.getAlbumQuery(
            query = query,
            offset = offset,
            count = COUNT
        )
            .map { response ->
                val albumList = mutableListOf<MusicSearchModel>()
                response?.firstOrNull()?.results?.hits?.hits?.let { hits ->
                    for (hit in hits) {
                        val model = MusicSearchModel.MusicItemModel(
                            id = hit.id,
                            title = hit.source?.name,
                            description = getArtistName(hit.source?.artists),
                            thumb = hit.source?.meta?.firstOrNull()?.image.orEmpty(),
                            type = MusicType.ALBUM,
                            musicTheme = applyMusicItemTheme(theme)
                        )
                        albumList.add(model)
                    }
                }
                albumList
            }
    }

    private fun getArtistName(artists: List<Artist>?): String? {
        return artists?.joinToString(", ") {
            it.name.orEmpty()
        }
    }
}
