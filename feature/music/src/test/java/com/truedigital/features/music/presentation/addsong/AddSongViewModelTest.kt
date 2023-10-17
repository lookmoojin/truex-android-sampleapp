package com.truedigital.features.music.presentation.addsong

import androidx.paging.LoadState
import androidx.paging.PagingData
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.core.coroutines.TestCoroutineDispatcherProvider
import com.truedigital.features.music.domain.addsong.model.MusicSearchResultModel
import com.truedigital.features.music.domain.addsong.usecase.AddSongUseCase
import com.truedigital.features.music.domain.addsong.usecase.GetSearchSongPagingUseCase
import com.truedigital.features.utils.MockDataModel
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.coroutines.TestCoroutinesExtension
import com.truedigital.share.mock.livedata.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import kotlin.test.assertNotNull

@ExtendWith(InstantTaskExecutorExtension::class)
internal class AddSongViewModelTest {

    @ExperimentalCoroutinesApi
    @RegisterExtension
    @JvmField
    val testCoroutine = TestCoroutinesExtension()

    private lateinit var addSongViewModel: AddSongViewModel
    private val coroutineDispatcher = TestCoroutineDispatcherProvider(testCoroutine.dispatcher)
    private val addSongUseCase: AddSongUseCase = mock()
    private val getSearchSongPagingUseCase: GetSearchSongPagingUseCase = mock()
    private val mockMusicSearchResultModel = MusicSearchResultModel(
        id = 123,
        songName = "songName",
        artistName = "artistName",
        coverImage = "coverImage"
    )

    @BeforeEach
    fun setUp() {
        addSongViewModel = AddSongViewModel(
            coroutineDispatcher = coroutineDispatcher,
            addSongUseCase = addSongUseCase,
            getSearchSongPagingUseCase = getSearchSongPagingUseCase
        )
    }

    @Test
    fun searchSong_queryIsNotEqualsLastQuery_success_returnPagingData() {
        // Given
        val mockPagingData = PagingData.from(listOf(mockMusicSearchResultModel))
        addSongViewModel.setPrivateData(currentQueryValue = "A")
        whenever(getSearchSongPagingUseCase.execute(any())).thenReturn(flowOf(mockPagingData))

        // When
        addSongViewModel.searchSong("B")

        // Then
        assertNotNull(addSongViewModel.onSearchSongResult().getOrAwaitValue())
        verify(getSearchSongPagingUseCase, times(1)).execute(any())
    }

    @Test
    fun searchSong_queryIsNotEqualsLastQuery_fail_showEmptyResult() {
        // Given
        addSongViewModel.setPrivateData(currentQueryValue = "A")
        whenever(getSearchSongPagingUseCase.execute(any())).thenReturn(
            flow { error("Error") }
        )

        // When
        addSongViewModel.searchSong("B")

        // Then
        assertEquals(addSongViewModel.onShowEmptyResult().getOrAwaitValue(), Unit)
        verify(getSearchSongPagingUseCase, times(1)).execute(any())
    }

    @Test
    fun searchSong_queryIsEmpty_doNothing() {
        // When
        addSongViewModel.searchSong("")

        // Then
        verify(getSearchSongPagingUseCase, times(0)).execute(any())
    }

    @Test
    fun searchSong_queryIsEqualsLastQuery_doNothing() {
        // Given
        addSongViewModel.setPrivateData(currentQueryValue = "A")

        // When
        addSongViewModel.searchSong("A")

        // Then
        verify(getSearchSongPagingUseCase, times(0)).execute(any())
    }

    @Test
    fun handlerShowEmptyResult_loadStateIsNotLoading_itemCountEmptyIsTrue_showEmptyResult() {
        // Given
        val loadState = LoadState.NotLoading(true)

        // When
        addSongViewModel.handlerShowEmptyResult(loadState, true)

        // Then
        assertEquals(addSongViewModel.onShowEmptyResult().getOrAwaitValue(), Unit)
    }

    @Test
    fun handlerShowEmptyResult_loadStateIsNotLoading_itemCountEmptyIsFalse_hideEmptyResult() {
        // Given
        val loadState = LoadState.NotLoading(true)

        // When
        addSongViewModel.handlerShowEmptyResult(loadState, false)

        // Then
        assertEquals(addSongViewModel.onHideEmptyResult().getOrAwaitValue(), Unit)
    }

    @Test
    fun handlerShowEmptyResult_loadStateIsLoading_hideEmptyResult() {
        // Given
        val loadState = LoadState.Loading

        // When
        addSongViewModel.handlerShowEmptyResult(loadState, false)

        // Then
        assertEquals(addSongViewModel.onHideEmptyResult().getOrAwaitValue(), Unit)
    }

    @Test
    fun testOnSelectedSong() {
        // Given
        val trackId = 1
        val mockPagingData = PagingData.from(listOf(mockMusicSearchResultModel))
        addSongViewModel.setPrivateData(currentQueryValue = "A")
        whenever(getSearchSongPagingUseCase.execute(any())).thenReturn(flowOf(mockPagingData))
        addSongViewModel.searchSong("B")

        // When
        addSongViewModel.onSelectedSong(trackId)

        // Then
        assertEquals(listOf(1), addSongViewModel.onSongSelected().value)
    }

    @Test
    fun testAddSong_success() {
        // Given
        whenever(addSongUseCase.execute(any(), any())).thenReturn(
            flowOf(MockDataModel.mockPlaylist)
        )

        // When
        addSongViewModel.addSong(1)

        // Then
        assertEquals(Unit, addSongViewModel.onShowProgressAddSong().value)
        assertEquals(Unit, addSongViewModel.onHideProgressAddSong().value)
        assertEquals(Unit, addSongViewModel.onSuccessAddSong().value)
    }

    @Test
    fun testAddSong_error() {
        // Given
        whenever(addSongUseCase.execute(any(), any())).thenReturn(
            flow { error("error") }
        )

        // When
        addSongViewModel.addSong(1)

        // Then
        assertEquals(Unit, addSongViewModel.onErrorAddSong().value)
    }
}
