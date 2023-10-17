package com.truedigital.features.music.presentation.mylibrary

import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.livedata.getOrAwaitValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals

@ExtendWith(InstantTaskExecutorExtension::class)
internal class MusicMyLibraryNavigationViewModelTest {

    private lateinit var musicMyLibraryNavigationViewModel: MusicMyLibraryNavigationViewModel

    @BeforeEach
    fun setUp() {
        musicMyLibraryNavigationViewModel = MusicMyLibraryNavigationViewModel()
    }

    @Test
    fun showMyPlaylist_showMyPlaylistIsCalled() {
        val mockPlaylistId = 1
        musicMyLibraryNavigationViewModel.showMyPlaylist(mockPlaylistId)

        assertEquals(
            musicMyLibraryNavigationViewModel.onShowMyPlaylist().getOrAwaitValue(),
            mockPlaylistId
        )
    }

    @Test
    fun testShowCreateNewPlaylist() {
        musicMyLibraryNavigationViewModel.createNewPlaylist()

        assertEquals(
            Unit,
            musicMyLibraryNavigationViewModel.onCreateNewPlaylist().getOrAwaitValue()
        )
    }
}
