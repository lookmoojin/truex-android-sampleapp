package com.truedigital.features.music.presentation.addsong.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.truedigital.features.music.domain.addsong.model.MusicSearchResultModel
import com.truedigital.features.music.domain.addsong.usecase.GetSearchSongPagingUseCaseImpl
import com.truedigital.features.music.domain.addsong.usecase.SearchSongStreamUseCase

class SearchSongDataSource(
    private val searchSongStreamUseCase: SearchSongStreamUseCase,
    private val query: String
) : PagingSource<Int, MusicSearchResultModel>() {

    companion object {
        const val STARTING_INDEX = 1
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MusicSearchResultModel> {
        val position = params.key ?: STARTING_INDEX

        val songList = searchSongStreamUseCase.execute(
            query,
            position.toString(),
            params.loadSize.toString()
        )

        val nextKey = if (songList.isEmpty()) {
            null
        } else {
            position + params.loadSize
        }

        return LoadResult.Page(
            data = songList.toList(),
            prevKey = if (position == STARTING_INDEX) null else position - params.loadSize,
            nextKey = nextKey
        )
    }

    override fun getRefreshKey(state: PagingState<Int, MusicSearchResultModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(
                GetSearchSongPagingUseCaseImpl.PAGE_SIZE
            ) ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(
                GetSearchSongPagingUseCaseImpl.PAGE_SIZE
            )
        }
    }
}
