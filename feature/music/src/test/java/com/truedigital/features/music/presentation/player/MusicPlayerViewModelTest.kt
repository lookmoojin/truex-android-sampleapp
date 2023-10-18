package com.truedigital.features.music.presentation.player

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.music.domain.player.usecase.GetLandingOnListenScopeUseCase
import com.truedigital.features.music.domain.player.usecase.SetLandingOnListenScopeUseCase
import com.truedigital.features.music.domain.player.usecase.SetMusicPlayerVisibleUseCase
import com.truedigital.features.music.manager.player.MusicPlayerActionManager
import com.truedigital.features.tuned.data.player.model.MediaType
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.livedata.getOrAwaitValue
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@ExtendWith(InstantTaskExecutorExtension::class)
class MusicPlayerViewModelTest {

    private lateinit var musicPlayerViewModel: MusicPlayerViewModel
    private val musicPlayerActionManager: MusicPlayerActionManager = mock()
    private val getLandingOnListenScopeUseCase: GetLandingOnListenScopeUseCase = mock()
    private val setLandingOnListenScopeUseCase: SetLandingOnListenScopeUseCase = mock()
    private val setMusicPlayerVisibleUseCase: SetMusicPlayerVisibleUseCase = mock()
    private val mockURL = "URL"

    @BeforeEach
    fun setup() {
        musicPlayerViewModel = MusicPlayerViewModel(
            musicPlayerActionManager = musicPlayerActionManager,
            getLandingOnListenScopeUseCase = getLandingOnListenScopeUseCase,
            setLandingOnListenScopeUseCase = setLandingOnListenScopeUseCase,
            setMusicPlayerVisibleUseCase = setMusicPlayerVisibleUseCase
        )
    }

    @Test
    fun testSetOnAddMarginBottom_bottomNavVisible_actionComplete() {
        // Given
        musicPlayerViewModel.setPrivateData(miniPlayerVisible = false)

        // When
        musicPlayerViewModel.setOnAddMarginBottom(isBottomNavVisible = true)

        // Then
        assert(musicPlayerViewModel.onAddMarginBottom().getOrAwaitValue() == Unit)
        assertTrue(musicPlayerViewModel.getMiniPlayerVisible())
    }

    @Test
    fun testSetOnAddMarginBottom_bottomNavIsNotVisible_doNothing() {
        // Given
        musicPlayerViewModel.setPrivateData(miniPlayerVisible = false)

        // When
        musicPlayerViewModel.setOnAddMarginBottom(isBottomNavVisible = false)

        // Then
        assertFalse(musicPlayerViewModel.getMiniPlayerVisible())
    }

    @Test
    fun testSetOnHideMusicPlayer_actionComplete() {
        // When
        musicPlayerViewModel.setOnHideMusicPlayer()

        // Then
        assert(musicPlayerViewModel.onHideMusicPlayer().getOrAwaitValue() == Unit)
        assertFalse(musicPlayerViewModel.getMiniPlayerVisible())
    }

    @Test
    fun testSetIsListenScope_actionComplete() {
        // When
        musicPlayerViewModel.setIsListenScope()

        // Then
        assert(musicPlayerViewModel.onListenScope().getOrAwaitValue() == Unit)
        assertTrue(musicPlayerViewModel.getMiniPlayerVisible())
        verify(setLandingOnListenScopeUseCase, times(1)).execute(any())
    }

    @Test
    fun testSetCollapsePlayer_collapsePlayerIsCalled() {
        // When
        musicPlayerViewModel.setCollapsePlayer()

        // Then
        assert(musicPlayerViewModel.onCollapsePlayer().getOrAwaitValue() == Unit)
    }

    @Test
    fun testActionPrevious_actionComplete() {
        // When
        musicPlayerViewModel.actionPrevious()

        // Then
        assert(musicPlayerViewModel.actionPrevious() == Unit)
    }

    @Test
    fun testActionNext_actionComplete() {
        // When
        musicPlayerViewModel.actionNext()

        // Then
        assert(musicPlayerViewModel.actionNext() == Unit)
    }

    @Test
    fun testActionClose_actionComplete() {
        // When
        musicPlayerViewModel.actionClose()

        // Then
        assert(musicPlayerViewModel.actionClose() == Unit)
    }

    @Test
    fun actionPause_playerIsActiveState_actionPauseIsCalled() {
        // When
        musicPlayerViewModel.actionPause(true)

        // Then
        verify(musicPlayerActionManager, times(1)).actionPause()
    }

    @Test
    fun actionPause_playerIsNotActiveState_doNothing() {
        // When
        musicPlayerViewModel.actionPause(false)

        // Then
        verify(musicPlayerActionManager, times(0)).actionPause()
    }

    @Test
    fun testMiniPlayerVisible_returnTrue() {
        // When
        musicPlayerViewModel.setOnAddMarginBottom()

        // Then
        assertEquals(true, musicPlayerViewModel.getMiniPlayerVisible())
    }

    @Test
    fun testMiniPlayerVisible_returnFalse() {
        // When
        musicPlayerViewModel.setOnHideMusicPlayer()

        // Then
        assertEquals(false, musicPlayerViewModel.getMiniPlayerVisible())
    }

    @Test
    fun handlerBottomMarginMiniPlayer_isCollapsedIsTrue_isLandingOnListenScopeIsTrue_onListenScopeIsCalled() {
        // Given
        whenever(getLandingOnListenScopeUseCase.execute()).thenReturn(true)

        // When
        musicPlayerViewModel.handlerBottomMarginMiniPlayer(true)

        // Then
        assertEquals(musicPlayerViewModel.onListenScope().getOrAwaitValue(), Unit)
        assertTrue(musicPlayerViewModel.getMiniPlayerVisible())
        verify(getLandingOnListenScopeUseCase, times(1)).execute()
    }

    @Test
    fun handlerBottomMarginMiniPlayer_isCollapsedIsTrue_isLandingOnListenScopeIsFalse_doNothing() {
        // Given
        whenever(getLandingOnListenScopeUseCase.execute()).thenReturn(false)

        // When
        musicPlayerViewModel.handlerBottomMarginMiniPlayer(true)

        // Then
        verify(getLandingOnListenScopeUseCase, times(1)).execute()
        verify(setLandingOnListenScopeUseCase, times(0)).execute(any())
    }

    @Test
    fun handlerBottomMarginMiniPlayer_isCollapsedIsFalse_isLandingOnListenScopeIsFalse_doNothing() {
        // Given
        whenever(getLandingOnListenScopeUseCase.execute()).thenReturn(false)

        // When
        musicPlayerViewModel.handlerBottomMarginMiniPlayer(false)

        // Then
        verify(getLandingOnListenScopeUseCase, times(1)).execute()
        verify(setLandingOnListenScopeUseCase, times(0)).execute(any())
    }

    @Test
    fun handlerBottomMarginMiniPlayer_isCollapsedIsFalse_isLandingOnListenScopeIsTrue_doNothing() {
        // Given
        whenever(getLandingOnListenScopeUseCase.execute()).thenReturn(true)

        // When
        musicPlayerViewModel.handlerBottomMarginMiniPlayer(false)

        // Then
        verify(getLandingOnListenScopeUseCase, times(1)).execute()
        verify(setLandingOnListenScopeUseCase, times(0)).execute(any())
    }

    @Test
    fun pausePlayer_pausePlayerIsCalled() {
        // When
        musicPlayerViewModel.pausePlayer()

        // Then
        assertEquals(Unit, musicPlayerViewModel.onPausePlayer().getOrAwaitValue())
    }

    @Test
    fun handlerLoadBackgroundImage_mediaTypeIsRadio_urlIsNotNull_loadTDGBackgroundImage() {
        // When
        musicPlayerViewModel.handlerLoadBackgroundImage(mockURL, MediaType.RADIO)

        // Then
        assertEquals(mockURL, musicPlayerViewModel.onLoadTDGBackgroundImage().getOrAwaitValue())
    }

    @Test
    fun handlerLoadBackgroundImage_mediaTypeIsRadio_urlIsNull_loadTDGBackgroundImage() {
        // When
        musicPlayerViewModel.handlerLoadBackgroundImage(null, MediaType.RADIO)

        // Then
        assertTrue(musicPlayerViewModel.onLoadTDGBackgroundImage().getOrAwaitValue().isEmpty())
    }

    @Test
    fun handlerLoadBackgroundImage_mediaTypeIsNotRadio_urlIsNotNull_loadTunedBackgroundImage() {
        // When
        musicPlayerViewModel.handlerLoadBackgroundImage(mockURL, MediaType.SONGS)

        // Then
        assertEquals(mockURL, musicPlayerViewModel.onLoadTunedBackgroundImage().getOrAwaitValue())
    }

    @Test
    fun handlerLoadBackgroundImage_mediaTypeIsNotRadio_urlIsNull_loadTunedBackgroundImage() {
        // When
        musicPlayerViewModel.handlerLoadBackgroundImage(null, MediaType.SONGS)

        // Then
        assertTrue(musicPlayerViewModel.onLoadTunedBackgroundImage().getOrAwaitValue().isEmpty())
    }

    @Test
    fun setVisibilityPlayerStatus_setMusicPlayerVisibleUseCaseIsCalled() {
        // When
        musicPlayerViewModel.setVisibilityPlayerStatus(true)

        // Then
        verify(setMusicPlayerVisibleUseCase, times(1)).execute(any())
    }

    @Test
    fun onFavouriteSelect_isFavouritedIsTrue_isSuccessIsTrue_thenShowFavoriteAddSuccess() {
        // When
        musicPlayerViewModel.onFavouriteSelect(isFavourited = true, isSuccess = true)

        // Then
        Assertions.assertEquals(musicPlayerViewModel.onFavoriteAddSuccess().getOrAwaitValue(), Unit)
    }

    @Test
    fun onFavouriteSelect_isFavouritedIsFalse_isSuccessIsTrue_thenShowFavoriteRemoveSuccess() {
        // When
        musicPlayerViewModel.onFavouriteSelect(isFavourited = false, isSuccess = true)

        // Then
        Assertions.assertEquals(
            musicPlayerViewModel.onFavoriteRemoveSuccess().getOrAwaitValue(),
            Unit
        )
    }

    @Test
    fun onFavouriteSelect_isFavouritedIsTrue_isSuccessIsFalse_thenShowFavoriteAddError() {
        // When
        musicPlayerViewModel.onFavouriteSelect(isFavourited = true, isSuccess = false)

        // Then
        Assertions.assertEquals(musicPlayerViewModel.onFavoriteAddError().getOrAwaitValue(), Unit)
    }

    @Test
    fun onFavouriteSelect_isFavouritedIsFalse_isSuccessIsFalse_thenShowFavoriteAddError() {
        // When
        musicPlayerViewModel.onFavouriteSelect(isFavourited = false, isSuccess = false)

        // Then
        Assertions.assertEquals(
            musicPlayerViewModel.onFavoriteRemoveError().getOrAwaitValue(),
            Unit
        )
    }
}
