package com.truedigital.features.music.presentation.addsong.pagingsource

import androidx.paging.PagingSource
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.music.domain.addsong.model.MusicSearchResultModel
import com.truedigital.features.music.domain.addsong.usecase.SearchSongStreamUseCase
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class SearchSongDataSourceTest {

    private val searchSongStreamUseCase: SearchSongStreamUseCase = mock()
    private lateinit var searchSongDataSource: SearchSongDataSource

    private val mockMusicSearchResultModel = MusicSearchResultModel(
        id = 1,
        songName = "songName",
        artistName = "artistName",
        coverImage = "coverImage"
    )

    @BeforeEach
    fun setUp() {
        searchSongDataSource = SearchSongDataSource(
            searchSongStreamUseCase = searchSongStreamUseCase,
            query = "query"
        )
    }

    @Test
    fun load_keyIsNull_listIsNotEmpty_returnPageDataPrevKeyIsNull() = runTest {
        // Given
        val mockLoadSize = 2
        val mockMusicSearchResultModel1 = mockMusicSearchResultModel.copy(id = 1)
        val mockMusicSearchResultModel2 = mockMusicSearchResultModel.copy(id = 2)
        val searchResultList = listOf(mockMusicSearchResultModel1, mockMusicSearchResultModel2)
        val expectResult = PagingSource.LoadResult.Page(
            data = searchResultList,
            prevKey = null,
            nextKey = mockLoadSize + SearchSongDataSource.STARTING_INDEX
        )

        whenever(searchSongStreamUseCase.execute(any(), any(), any())).thenReturn(searchResultList)

        // When
        val actualResult = searchSongDataSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = mockLoadSize,
                placeholdersEnabled = false
            )
        )

        // Then
        assertEquals(expectResult, actualResult)
    }

    @Test
    fun load_keyIsNotNull_listIsNotEmpty_returnPageDataPrevKeyIsNotNull() = runTest {
        // Given
        val mockLoadSize = 2
        val mockKey = 3
        val mockMusicSearchResultModel1 = mockMusicSearchResultModel.copy(id = 1)
        val mockMusicSearchResultModel2 = mockMusicSearchResultModel.copy(id = 2)
        val searchResultList = listOf(mockMusicSearchResultModel1, mockMusicSearchResultModel2)
        val expectResult = PagingSource.LoadResult.Page(
            data = searchResultList,
            prevKey = mockKey - mockLoadSize,
            nextKey = mockKey + mockLoadSize
        )

        whenever(searchSongStreamUseCase.execute(any(), any(), any())).thenReturn(searchResultList)

        // When
        val actualResult = searchSongDataSource.load(
            PagingSource.LoadParams.Refresh(
                key = mockKey,
                loadSize = mockLoadSize,
                placeholdersEnabled = false
            )
        )

        // Then
        assertEquals(expectResult, actualResult)
    }

    @Test
    fun load_keyIsNull_listIsEmpty_returnPageDataPreKeyAndNextKeyIsNull() = runTest {
        // Given
        val mockLoadSize = 2
        val searchResultList = listOf<MusicSearchResultModel>()
        val expectResult = PagingSource.LoadResult.Page(
            data = searchResultList,
            prevKey = null,
            nextKey = null
        )

        whenever(searchSongStreamUseCase.execute(any(), any(), any())).thenReturn(searchResultList)

        // When
        val actualResult = searchSongDataSource.load(
            PagingSource.LoadParams.Refresh(
                key = null,
                loadSize = mockLoadSize,
                placeholdersEnabled = false
            )
        )

        // Then
        assertEquals(expectResult, actualResult)
    }
}
