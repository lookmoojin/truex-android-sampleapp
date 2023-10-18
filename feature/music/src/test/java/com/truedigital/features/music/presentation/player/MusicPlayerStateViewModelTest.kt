package com.truedigital.features.music.presentation.player

import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.livedata.getOrAwaitValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExtendWith(InstantTaskExecutorExtension::class)
class MusicPlayerStateViewModelTest {

    private lateinit var musicPlayerStateViewModel: MusicPlayerStateViewModel

    @BeforeEach
    fun setup() {
        musicPlayerStateViewModel = MusicPlayerStateViewModel()
    }

    @Test
    fun setCollapsedPlayer_onCollapsedPlayerIsCalled() {
        musicPlayerStateViewModel.setCollapsedPlayerState()

        assertEquals(musicPlayerStateViewModel.onCollapsedPlayerState().getOrAwaitValue(), Unit)
    }

    @Test
    fun setExpandedPlayer_onCollapsedPlayerIsCalled() {
        musicPlayerStateViewModel.setExpandedPlayerState()

        assertEquals(musicPlayerStateViewModel.onExpandedPlayerState().getOrAwaitValue(), Unit)
    }

    @Test
    fun onPlayerStateChange_isExpandedIsTrue_expandedPlayerStateIsCalled() {
        musicPlayerStateViewModel.onPlayerStateChange(true)

        assertEquals(musicPlayerStateViewModel.onExpandedPlayerState().getOrAwaitValue(), Unit)
    }

    @Test
    fun onPlayerStateChange_isExpandedIsFalse_collapsedPlayerStateIsCalled() {
        musicPlayerStateViewModel.onPlayerStateChange(false)

        assertEquals(musicPlayerStateViewModel.onCollapsedPlayerState().getOrAwaitValue(), Unit)
    }

    @Test
    fun setVisibilityPlayerStatus_visibilityPlayerChangeIsCalled() {
        musicPlayerStateViewModel.setVisibilityPlayerStatus(true)

        assertTrue(musicPlayerStateViewModel.onVisibilityPlayerChange().getOrAwaitValue())
    }
}
