package com.truedigital.features.music.presentation.search

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.validateMockitoUsage
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.music.domain.search.model.MusicType
import com.truedigital.features.music.domain.search.model.ThemeType
import com.truedigital.features.music.domain.search.model.TopMenuModel
import com.truedigital.features.music.domain.search.usecase.GetSearchAlbumUseCase
import com.truedigital.features.music.domain.search.usecase.GetSearchAllUseCase
import com.truedigital.features.music.domain.search.usecase.GetSearchArtistUseCase
import com.truedigital.features.music.domain.search.usecase.GetSearchPlaylistUseCase
import com.truedigital.features.music.domain.search.usecase.GetSearchSongUseCase
import com.truedigital.features.music.domain.search.usecase.GetSearchTopMenuUseCase
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.livedata.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@ExtendWith(InstantTaskExecutorExtension::class)
class MusicSearchViewModelTest {

    private val dispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
    private val testScope = TestCoroutineScope(dispatcher)

    private lateinit var viewModel: MusicSearchViewModel
    private val getSearchTopMenuUseCase: GetSearchTopMenuUseCase = mock()
    private val getSearchAllUseCase: GetSearchAllUseCase = mock()
    private val getSearchAlbumUseCase: GetSearchAlbumUseCase = mock()
    private val getSearchArtistUseCase: GetSearchArtistUseCase = mock()
    private val getSearchPlaylistUseCase: GetSearchPlaylistUseCase = mock()
    private val getSearchSongUseCase: GetSearchSongUseCase = mock()

    @BeforeEach
    fun setUp() {
        viewModel = MusicSearchViewModel(
            getSearchTopMenuUseCase,
            getSearchAllUseCase,
            getSearchAlbumUseCase,
            getSearchArtistUseCase,
            getSearchPlaylistUseCase,
            getSearchSongUseCase
        )
        Dispatchers.setMain(dispatcher)
    }

    @AfterEach
    fun tearDown() {
        validateMockitoUsage()
        Dispatchers.resetMain()
    }

    @Test
    fun testGetCurrentTheme_setThemeType_returnThemeType() {
        val mockThemeType = ThemeType.LIGHT

        viewModel.setTheme(mockThemeType)

        assert(viewModel.getCurrentTheme() == mockThemeType)
    }

    @Test
    fun testGetCurrentTheme_notSetThemeType_returnThemeTypeDefault() {
        assert(viewModel.getCurrentTheme() == ThemeType.DARK)
    }

    @Test
    fun testGetCurrentTopMenuPosition_currentTypeIsAll_returnPosition0() {
        val mockMusicType = MusicType.ALL

        viewModel.updateCurrentTopMenuType(mockMusicType)

        assert(viewModel.getCurrentTopMenuPosition() == 0)
    }

    @Test
    fun testGetCurrentTopMenuPosition_currentTypeIsSong_returnPosition1() {
        val mockMusicType = MusicType.SONG

        viewModel.updateCurrentTopMenuType(mockMusicType)

        assert(viewModel.getCurrentTopMenuPosition() == 1)
    }

    @Test
    fun testGetCurrentTopMenuPosition_currentTypeIsArtist_returnPosition2() {
        val mockMusicType = MusicType.ARTIST

        viewModel.updateCurrentTopMenuType(mockMusicType)

        assert(viewModel.getCurrentTopMenuPosition() == 2)
    }

    @Test
    fun testGetCurrentTopMenuPosition_currentTypeIsAlbum_returnPosition3() {
        val mockMusicType = MusicType.ALBUM

        viewModel.updateCurrentTopMenuType(mockMusicType)

        assert(viewModel.getCurrentTopMenuPosition() == 3)
    }

    @Test
    fun testGetCurrentTopMenuPosition_currentTypeIsPlaylist_returnPosition1() {
        val mockMusicType = MusicType.PLAYLIST

        viewModel.updateCurrentTopMenuType(mockMusicType)

        assert(viewModel.getCurrentTopMenuPosition() == 4)
    }

    @Test
    fun testGetCurrentTopMenuPosition_currentTypeIsNull_returnPosition0() {
        viewModel.updateCurrentTopMenuType(null)

        assert(viewModel.getCurrentTopMenuPosition() == 0)
    }

    @Test
    fun testFetchTopMenu_returnTopMenuList() = runTest {
        val mockTopMenuList = listOf(
            TopMenuModel().apply {
                id = "1"
                name = "name1"
                type = MusicType.ALL
                isActive = true
            }
        )

        whenever(getSearchTopMenuUseCase.execute(any(), any())).thenReturn(mockTopMenuList)

        viewModel.fetchTopMenu()

        assert(viewModel.onMusicSearchTopMenuList().getOrAwaitValue() == mockTopMenuList)
    }

    @Test
    fun testOnClickedTopMenu_updateCurrentTopMenuType_returnUnit() {
        viewModel.onClickedTopMenu(MusicType.ALL)

        assert(viewModel.getCurrentTopMenuPosition() == 0)
        assert(viewModel.onSelectedTopMenu().value == Unit)
    }

    @Test
    fun testResetLoadData_returnFalse() {
        viewModel.resetLoadData()
        assertEquals(
            false,
            viewModel.isLoadData()
        )
    }

    @Test
    fun testOnRenderMusicList_returnTransform() = testScope.runTest {
        whenever(getSearchAllUseCase.execute(any(), any())).thenReturn(flowOf(listOf()))
        whenever(getSearchAlbumUseCase.execute(any(), any(), any())).thenReturn(flowOf(listOf()))
        whenever(getSearchArtistUseCase.execute(any(), any(), any())).thenReturn(flowOf(listOf()))
        whenever(getSearchPlaylistUseCase.execute(any(), any(), any())).thenReturn(flowOf(listOf()))
        whenever(getSearchSongUseCase.execute(any(), any(), any())).thenReturn(flowOf(listOf()))

        viewModel.searchWithCurrentTopMenu("key")
        assertNotNull(viewModel.onRenderMusicList())
        assertNotNull(viewModel.onShowLoading())
        assertNotNull(viewModel.onHideLoading())
        assertNotNull(viewModel.onShowError())
        assertNotNull(viewModel.onHideError())
    }
}
