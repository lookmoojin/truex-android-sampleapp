package com.truedigital.features.music.domain.addsong.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.truedigital.features.music.domain.addsong.model.MusicSearchResultModel
import com.truedigital.features.music.presentation.addsong.pagingsource.SearchSongDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface GetSearchSongPagingUseCase {
    fun execute(query: String): Flow<PagingData<MusicSearchResultModel>>
}

class GetSearchSongPagingUseCaseImpl @Inject constructor(
    private val searchSongStreamUseCase: SearchSongStreamUseCase
) : GetSearchSongPagingUseCase {
    companion object {
        const val PAGE_SIZE = 20
        const val PREFETCH_DISTANCE = 5
    }

    override fun execute(query: String): Flow<PagingData<MusicSearchResultModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                enablePlaceholders = false,
                prefetchDistance = PREFETCH_DISTANCE,
                initialLoadSize = PAGE_SIZE
            ),
            pagingSourceFactory = { SearchSongDataSource(searchSongStreamUseCase, query) }
        ).flow
    }
}
