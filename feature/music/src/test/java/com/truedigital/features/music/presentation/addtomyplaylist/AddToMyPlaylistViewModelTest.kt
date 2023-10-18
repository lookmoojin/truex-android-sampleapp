package com.truedigital.features.music.presentation.addtomyplaylist

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.core.coroutines.TestCoroutineDispatcherProvider
import com.truedigital.features.music.domain.addsong.usecase.AddSongUseCase
import com.truedigital.features.music.domain.myplaylist.model.MusicMyPlaylistModel
import com.truedigital.features.music.domain.myplaylist.usecase.GetMyPlaylistShelfUseCase
import com.truedigital.features.utils.MockDataModel
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.coroutines.TestCoroutinesExtension
import com.truedigital.share.mock.livedata.getOrAwaitValue
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension

interface AddToMyPlaylistViewModelTestCase {
    fun handlerReloadMyPlaylist_myPlaylistWasCreated_loadMyPlaylist()
    fun handlerReloadMyPlaylist_myPlaylistWasNotCreated_doNothing()
    fun loadMyPlaylist_success_showMyPlaylistData()
    fun loadMyPlaylist_fail_showAddMyPlaylist()
    fun addSongToMyPlaylist_success_showAddSongSuccessMessage()
    fun addSongToMyPlaylist_parameterIsNotNull_fail_showAddSongFailMessage()
    fun addSongToMyPlaylist_parameterIsNull_fail_showAddSongFailMessage()
}

@ExtendWith(InstantTaskExecutorExtension::class)
internal class AddToMyPlaylistViewModelTest : AddToMyPlaylistViewModelTestCase {

    @RegisterExtension
    @JvmField
    val testCoroutine = TestCoroutinesExtension()

    private val coroutineDispatcher = TestCoroutineDispatcherProvider(testCoroutine.dispatcher)
    private val addSongUseCase: AddSongUseCase = mock()
    private val getMyPlaylistShelfUseCase: GetMyPlaylistShelfUseCase = mock()
    private lateinit var addToMyPlaylistViewModel: AddToMyPlaylistViewModel

    private val mockMyPlaylistModel = MusicMyPlaylistModel.MyPlaylistModel(
        playlistId = 10,
        title = "title",
        coverImage = "coverImage",
        trackCount = 100
    )

    @BeforeEach
    fun setUp() {
        addToMyPlaylistViewModel = AddToMyPlaylistViewModel(
            coroutineDispatcher = coroutineDispatcher,
            addSongUseCase = addSongUseCase,
            getMyPlaylistShelfUseCase = getMyPlaylistShelfUseCase
        )
    }

    @Test
    override fun handlerReloadMyPlaylist_myPlaylistWasCreated_loadMyPlaylist() {
        // Given
        whenever(getMyPlaylistShelfUseCase.execute()).thenReturn(flowOf(listOf(mockMyPlaylistModel)))

        // When
        addToMyPlaylistViewModel.handlerReloadMyPlaylist(true)

        // Then
        verify(getMyPlaylistShelfUseCase, times(1)).execute()
    }

    @Test
    override fun handlerReloadMyPlaylist_myPlaylistWasNotCreated_doNothing() {
        // When
        addToMyPlaylistViewModel.handlerReloadMyPlaylist(false)

        // Then
        verify(getMyPlaylistShelfUseCase, times(0)).execute()
    }

    @Test
    override fun loadMyPlaylist_success_showMyPlaylistData() {
        // Given
        whenever(getMyPlaylistShelfUseCase.execute()).thenReturn(flowOf(listOf(mockMyPlaylistModel)))

        // When
        addToMyPlaylistViewModel.loadMyPlaylist()

        // Then
        assertEquals(Unit, addToMyPlaylistViewModel.onShowLoading().getOrAwaitValue())
        assertEquals(Unit, addToMyPlaylistViewModel.onHideLoading().getOrAwaitValue())
        addToMyPlaylistViewModel.onShowMyPlaylist().getOrAwaitValue().also {
            val headerData = it[0]
            assertTrue(headerData is MusicMyPlaylistModel.CreateMyPlaylistModel)

            val contentData = it[1] as MusicMyPlaylistModel.MyPlaylistModel
            assertEquals(mockMyPlaylistModel.playlistId, contentData.playlistId)
            assertEquals(mockMyPlaylistModel.title, contentData.title)
            assertEquals(mockMyPlaylistModel.coverImage, contentData.coverImage)
            assertEquals(mockMyPlaylistModel.trackCount, contentData.trackCount)
        }
        verify(getMyPlaylistShelfUseCase, times(1)).execute()
    }

    @Test
    override fun loadMyPlaylist_fail_showAddMyPlaylist() {
        // Given
        whenever(getMyPlaylistShelfUseCase.execute()).thenReturn(flow { error("error") })

        // When
        addToMyPlaylistViewModel.loadMyPlaylist()

        // Then
        assertEquals(Unit, addToMyPlaylistViewModel.onShowLoading().getOrAwaitValue())
        assertEquals(Unit, addToMyPlaylistViewModel.onHideLoading().getOrAwaitValue())
        addToMyPlaylistViewModel.onShowMyPlaylist().getOrAwaitValue().also {
            val headerData = it[0]
            assertTrue(headerData is MusicMyPlaylistModel.CreateMyPlaylistModel)
            assertTrue(it.size == 1)
        }
        verify(getMyPlaylistShelfUseCase, times(1)).execute()
    }

    @Test
    override fun addSongToMyPlaylist_success_showAddSongSuccessMessage() {
        // Given
        whenever(addSongUseCase.execute(any(), any())).thenReturn(
            flowOf(MockDataModel.mockPlaylist)
        )

        // When
        addToMyPlaylistViewModel.addSongToMyPlaylist("playlistId", 10)

        // Then
        assertEquals(Unit, addToMyPlaylistViewModel.onShowLoading().getOrAwaitValue())
        assertEquals(Unit, addToMyPlaylistViewModel.onHideLoading().getOrAwaitValue())
        assertEquals(Unit, addToMyPlaylistViewModel.onShowAddSongSuccessMessage().getOrAwaitValue())
    }

    @Test
    override fun addSongToMyPlaylist_parameterIsNotNull_fail_showAddSongFailMessage() {
        // Given
        whenever(addSongUseCase.execute(any(), any())).thenReturn(
            flow { error("error") }
        )

        // When
        addToMyPlaylistViewModel.addSongToMyPlaylist("playlistId", 10)

        // Then
        assertEquals(Unit, addToMyPlaylistViewModel.onShowLoading().getOrAwaitValue())
        assertEquals(Unit, addToMyPlaylistViewModel.onHideLoading().getOrAwaitValue())
        assertEquals(Unit, addToMyPlaylistViewModel.onShowAddSongFailMessage().getOrAwaitValue())
    }

    @Test
    override fun addSongToMyPlaylist_parameterIsNull_fail_showAddSongFailMessage() {
        // Given
        whenever(addSongUseCase.execute(any(), any())).thenReturn(
            flow { error("error") }
        )

        // When
        addToMyPlaylistViewModel.addSongToMyPlaylist(null, null)

        // Then
        assertEquals(Unit, addToMyPlaylistViewModel.onShowLoading().getOrAwaitValue())
        assertEquals(Unit, addToMyPlaylistViewModel.onHideLoading().getOrAwaitValue())
        assertEquals(Unit, addToMyPlaylistViewModel.onShowAddSongFailMessage().getOrAwaitValue())
    }
}
