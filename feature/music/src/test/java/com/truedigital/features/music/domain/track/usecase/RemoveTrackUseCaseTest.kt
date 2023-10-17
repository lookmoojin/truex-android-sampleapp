package com.truedigital.features.music.domain.track.usecase

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.music.data.playlist.model.response.RemoveTrackResponse
import com.truedigital.features.music.data.playlist.repository.MusicPlaylistRepository
import com.truedigital.features.music.data.playlist.repository.MusicPlaylistRepositoryImpl
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class RemoveTrackUseCaseTest {

    private val musicPlaylistRepository: MusicPlaylistRepository = mock()
    private lateinit var removeTrackUseCase: RemoveTrackUseCase

    @BeforeEach
    fun setUp() {
        removeTrackUseCase = RemoveTrackUseCaseImpl(musicPlaylistRepository)
    }

    @Test
    fun execute_success_returnData() = runTest {
        // Given
        val mockRemoveTrackResponse = RemoveTrackResponse(value = true)
        whenever(musicPlaylistRepository.removeTrack(any(), any())).thenReturn(
            flowOf(mockRemoveTrackResponse)
        )

        // When
        val result = removeTrackUseCase.execute(1, listOf(1, 2))

        // Then
        result.collect { response ->
            assertTrue(response)
        }
        verify(musicPlaylistRepository, times(1)).removeTrack(any(), any())
    }

    @Test
    fun execute_fail_returnError() = runTest {
        // Given
        whenever(musicPlaylistRepository.removeTrack(any(), any())).thenReturn(
            flow { error(MusicPlaylistRepositoryImpl.ERROR_REMOVE_TRACK) }
        )

        // When
        val result = removeTrackUseCase.execute(1, listOf(1, 2))

        // Then
        result.catch {
            assertEquals(
                MusicPlaylistRepositoryImpl.ERROR_REMOVE_TRACK,
                it.message
            )
        }.collect()
        verify(musicPlaylistRepository, times(1)).removeTrack(any(), any())
    }
}
