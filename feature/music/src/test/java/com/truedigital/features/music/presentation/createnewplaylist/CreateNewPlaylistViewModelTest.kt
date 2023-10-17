package com.truedigital.features.music.presentation.createnewplaylist

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.core.coroutines.TestCoroutineDispatcherProvider
import com.truedigital.features.music.domain.myplaylist.usecase.CreateNewPlaylistUseCase
import com.truedigital.features.music.domain.usecase.router.MusicRouterUseCase
import com.truedigital.features.music.navigation.router.MusicCreateNewPlaylistToMyPlaylist
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.coroutines.TestCoroutinesExtension
import com.truedigital.share.mock.livedata.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension

@ExtendWith(InstantTaskExecutorExtension::class)
internal class CreateNewPlaylistViewModelTest {

    @ExperimentalCoroutinesApi
    @RegisterExtension
    @JvmField
    val testCoroutine = TestCoroutinesExtension()

    private lateinit var mCreateNewPlaylistViewModel: CreateNewPlaylistViewModel
    private val createNewPlaylistUseCase: CreateNewPlaylistUseCase = mock()
    private val router: MusicRouterUseCase = mock()
    private val coroutineDispatcher = TestCoroutineDispatcherProvider(testCoroutine.dispatcher)

    @BeforeEach
    fun setUp() {
        mCreateNewPlaylistViewModel = CreateNewPlaylistViewModel(
            router = router,
            coroutineDispatcher = coroutineDispatcher,
            createNewPlaylistUseCase = createNewPlaylistUseCase
        )
    }

    @Test
    fun createNewPlaylist_success_getPlaylistId() = runTest {
        // Given
        whenever(createNewPlaylistUseCase.execute(any())).thenReturn(
            flowOf(123)
        )

        // When
        mCreateNewPlaylistViewModel.createNewPlaylist("Playlist XX")

        // Then
        assertEquals(mCreateNewPlaylistViewModel.onShowLoading().getOrAwaitValue(), Unit)
        assertEquals(mCreateNewPlaylistViewModel.onHideLoading().getOrAwaitValue(), Unit)
        assertEquals(
            123,
            mCreateNewPlaylistViewModel.onCreateNewPlayListSuccess().getOrAwaitValue()
        )
        verify(createNewPlaylistUseCase, times(1)).execute(any())
    }

    @Test
    fun createNewPlaylist_fail_showErrorDialog() = runTest {
        // Given
        whenever(createNewPlaylistUseCase.execute(any())).thenReturn(
            flow { error("can't creaye new play list") }
        )

        // When
        mCreateNewPlaylistViewModel.createNewPlaylist("Playlist XX")

        // Then
        assertEquals(mCreateNewPlaylistViewModel.onShowLoading().getOrAwaitValue(), Unit)
        assertEquals(mCreateNewPlaylistViewModel.onHideLoading().getOrAwaitValue(), Unit)
        assertEquals(mCreateNewPlaylistViewModel.onShowErrorDialog().getOrAwaitValue(), Unit)
        verify(createNewPlaylistUseCase, times(1)).execute(any())
    }

    @Test
    fun testCreateNewPlaylistSuccess_navigateToMyPlaylist_routerIsCalled() {
        whenever(router.getLastDestination()).thenReturn(MusicCreateNewPlaylistToMyPlaylist)
        mCreateNewPlaylistViewModel.navigateToPlaylist(1)

        assertEquals(MusicCreateNewPlaylistToMyPlaylist, router.getLastDestination())
        verify(router, times(1)).execute(destination = any(), bundle = any())
    }
}
