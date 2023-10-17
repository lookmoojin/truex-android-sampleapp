package com.truedigital.features.tuned.presentation.player.presenter

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.domain.facade.playerqueue.PlayerQueueFacade
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

internal class PlayerQueuePresenterTest {
    private val playerQueueFacade: PlayerQueueFacade = mock()
    private val router: PlayerQueuePresenter.RouterSurface = mock()
    private val view: PlayerQueuePresenter.ViewSurface = mock()

    private lateinit var playerQueuePresenter: PlayerQueuePresenter

    @BeforeEach
    fun setUp() {
        playerQueuePresenter = PlayerQueuePresenter(playerQueueFacade)
        playerQueuePresenter.onInject(view, router)
    }

    @Test
    fun onStart_notVerifyAll() {
        playerQueuePresenter.onStart(null)

        verify(view, times(0)).showCurrentPlayingTrack(anyOrNull(), anyOrNull())
        verify(view, times(0)).showItemMoved(any(), any())
        verify(view, times(0)).showItemRemoved(any())
        verify(view, times(0)).toggleRepeat()
        verify(view, times(0)).toggleShuffle()
        verify(view, times(0)).showClearQueueDialog()
        verify(view, times(0)).clearQueue()
        verify(view, times(0)).showUpgradeDialog()
    }

    @Test
    fun onUpdatePlaybackState_trackIdAndIndexInQueueNull_notVerifyShowCurrentPlayingTrack() {
        playerQueuePresenter.onUpdatePlaybackState(null, null)

        verify(view, times(0)).showCurrentPlayingTrack(anyOrNull(), anyOrNull())
    }

    @Test
    fun onUpdatePlaybackState_trackIdAndIndexInQueueNotNull_verifyShowCurrentPlayingTrack() {
        playerQueuePresenter.onUpdatePlaybackState(1, 2)

        verify(view, times(1)).showCurrentPlayingTrack(1, 2)
    }

    @Test
    fun onUpdatePlaybackState_trackIdNullAndIndexInQueueNotNull_verifyShowCurrentPlayingTrack() {
        playerQueuePresenter.onUpdatePlaybackState(null, 2)

        verify(view, times(1)).showCurrentPlayingTrack(null, 2)
    }

    @Test
    fun onUpdatePlaybackState_trackIdNotNullAndIndexInQueueNull_verifyShowCurrentPlayingTrack() {
        playerQueuePresenter.onUpdatePlaybackState(1, null)

        verify(view, times(1)).showCurrentPlayingTrack(1, null)
    }

    @Test
    fun onItemRemoved_verifyShowItemRemovedAsIndex() {
        val mockIndex = 1
        playerQueuePresenter.onItemRemoved(mockIndex)

        verify(view, times(1)).showItemRemoved(mockIndex)
    }

    @Test
    fun onItemMoved_verifyShowItemMovedAsIndex() {
        val mockOldIndex = 1
        val mockNewIndex = 2
        playerQueuePresenter.onItemMoved(mockOldIndex, mockNewIndex)

        verify(view, times(1)).showItemMoved(mockOldIndex, mockNewIndex)
    }

    @Test
    fun onRepeatSelected_verifyToggleRepeat() {
        playerQueuePresenter.onRepeatSelected()

        verify(view, times(1)).toggleRepeat()
    }

    @Test
    fun onShuffleSelected_isShuffleEnabledTrue_hasSequentialPlaybackRightFalse_verifyShowUpgradeDialog() {
        whenever(playerQueueFacade.isShuffleEnabled()).thenReturn(true)
        whenever(playerQueueFacade.hasSequentialPlaybackRight()).thenReturn(false)

        playerQueuePresenter.onShuffleSelected()

        verify(view, times(1)).showUpgradeDialog()
        verify(view, times(0)).toggleShuffle()
    }

    @Test
    fun onShuffleSelected_isShuffleEnabledTrue_hasSequentialPlaybackRightTrue_verifyToggleShuffle() {
        whenever(playerQueueFacade.isShuffleEnabled()).thenReturn(true)
        whenever(playerQueueFacade.hasSequentialPlaybackRight()).thenReturn(true)

        playerQueuePresenter.onShuffleSelected()

        verify(view, times(0)).showUpgradeDialog()
        verify(view, times(1)).toggleShuffle()
    }

    @Test
    fun onShuffleSelected_isShuffleEnabledFalse_hasSequentialPlaybackRightTrue_verifyToggleShuffle() {
        whenever(playerQueueFacade.isShuffleEnabled()).thenReturn(false)
        whenever(playerQueueFacade.hasSequentialPlaybackRight()).thenReturn(true)

        playerQueuePresenter.onShuffleSelected()

        verify(view, times(0)).showUpgradeDialog()
        verify(view, times(1)).toggleShuffle()
    }

    @Test
    fun onShuffleSelected_isShuffleEnabledFalse_hasSequentialPlaybackRightFalse_verifyToggleShuffle() {
        whenever(playerQueueFacade.isShuffleEnabled()).thenReturn(false)
        whenever(playerQueueFacade.hasSequentialPlaybackRight()).thenReturn(false)

        playerQueuePresenter.onShuffleSelected()

        verify(view, times(0)).showUpgradeDialog()
        verify(view, times(1)).toggleShuffle()
    }

    @Test
    fun onClearSelected_verifyShowClearQueueDialog() {
        playerQueuePresenter.onClearSelected()

        verify(view, times(1)).showClearQueueDialog()
    }

    @Test
    fun getHasPlaylistWriteRight_returnHasPlaylistWriteRight() {
        whenever(playerQueueFacade.hasPlaylistWriteRight()).thenReturn(true)
        assertTrue(playerQueuePresenter.getHasPlaylistWriteRight())
    }
}
