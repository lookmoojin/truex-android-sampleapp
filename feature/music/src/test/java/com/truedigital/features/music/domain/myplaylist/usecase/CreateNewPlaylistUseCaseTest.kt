package com.truedigital.features.music.domain.myplaylist.usecase

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.music.data.playlist.repository.MusicPlaylistRepository
import com.truedigital.features.music.data.playlist.repository.MusicPlaylistRepositoryImpl
import com.truedigital.features.utils.MockDataModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CreateNewPlaylistUseCaseTest {
    private lateinit var createNewPlaylistUseCase: CreateNewPlaylistUseCase
    private val musicPlaylistRepository: MusicPlaylistRepository = mock()

    @BeforeEach
    fun setup() {
        createNewPlaylistUseCase = CreateNewPlaylistUseCaseImpl(musicPlaylistRepository)
    }

    @Test
    fun testCreateNewPlaylist_whenSuccess_returnPlaylistId() = runTest {
        // Given
        whenever(musicPlaylistRepository.postNewPlaylist(any())).thenReturn(
            flowOf(MockDataModel.mockPlaylist)
        )

        // When
        val result = createNewPlaylistUseCase.execute("playlist 01")

        // Then
        result.collect {
            assertEquals(MockDataModel.mockPlaylist.id, it)
        }
    }

    @Test
    fun testCreateNewPlaylist_whenFail_returnError() = runTest {
        // Given
        whenever(musicPlaylistRepository.postNewPlaylist(any())).thenReturn(
            flow { error(MusicPlaylistRepositoryImpl.ERROR_CREATE_NEW_PLAYLIST) }
        )

        // When
        val result = createNewPlaylistUseCase.execute("playlist 01")

        // Then
        result.catch { exception ->
            assertTrue {
                exception.message == MusicPlaylistRepositoryImpl.ERROR_CREATE_NEW_PLAYLIST
            }
        }.collect()
    }
}
