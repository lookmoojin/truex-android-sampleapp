package com.truedigital.features.music.presentation.mylibrary

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.music.domain.usecase.router.MusicRouterUseCase
import com.truedigital.features.music.navigation.router.MusicCreateNewPlaylist
import com.truedigital.features.music.navigation.router.MusicMyLibraryToMyPlaylist
import com.truedigital.navigation.domain.usecase.SetRouterSecondaryToNavControllerUseCase
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class MusicMyLibraryViewModelTest {

    private val router: MusicRouterUseCase = mock()
    private val setRouterSecondaryToNavControllerUseCase: SetRouterSecondaryToNavControllerUseCase =
        mockk()
    private lateinit var musicMyLibraryViewModel: MusicMyLibraryViewModel

    @BeforeEach
    fun setUp() {
        musicMyLibraryViewModel =
            MusicMyLibraryViewModel(router, setRouterSecondaryToNavControllerUseCase)
    }

    @Test
    fun navigateToMyPlaylist_routerIsCalled() {
        whenever(router.getLastDestination()).thenReturn(MusicMyLibraryToMyPlaylist)
        musicMyLibraryViewModel.navigateToMyPlaylist(1)

        assertEquals(MusicMyLibraryToMyPlaylist, router.getLastDestination())
        verify(router, times(1)).execute(destination = any(), bundle = anyOrNull())
    }

    @Test
    fun navigateToCreateNewPlaylist_routerIsCalled() {
        whenever(router.getLastDestination()).thenReturn(MusicCreateNewPlaylist)
        musicMyLibraryViewModel.navigateToCreateNewPlaylist()

        assertEquals(MusicCreateNewPlaylist, router.getLastDestination())
        verify(router, times(1)).execute(MusicCreateNewPlaylist)
    }
}
