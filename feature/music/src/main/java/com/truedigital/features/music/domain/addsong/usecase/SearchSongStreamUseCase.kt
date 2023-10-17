package com.truedigital.features.music.domain.addsong.usecase

import com.truedigital.features.music.data.search.repository.MusicSearchRepository
import com.truedigital.features.music.domain.addsong.model.MusicSearchResultModel
import javax.inject.Inject

interface SearchSongStreamUseCase {
    suspend fun execute(
        query: String,
        offset: String,
        count: String
    ): List<MusicSearchResultModel>
}

class SearchSongStreamUseCaseImpl @Inject constructor(
    private val musicSearchRepository: MusicSearchRepository
) : SearchSongStreamUseCase {
    override suspend fun execute(
        query: String,
        offset: String,
        count: String
    ): List<MusicSearchResultModel> {
        return musicSearchRepository.getSongQueryStream(
            query = query,
            offset = offset,
            count = count
        )?.firstOrNull()?.results?.hits?.hits?.map { song ->
            MusicSearchResultModel(
                id = song.id?.toIntOrNull(),
                songName = song.source?.nameTranslations.orEmpty(),
                artistName = song.source?.artists?.firstOrNull()?.name.orEmpty(),
                coverImage = song.source?.meta?.firstOrNull()?.albumImage.orEmpty()
            )
        } ?: emptyList()
    }
}
