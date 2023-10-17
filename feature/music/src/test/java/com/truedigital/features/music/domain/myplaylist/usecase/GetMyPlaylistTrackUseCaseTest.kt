package com.truedigital.features.music.domain.myplaylist.usecase

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.music.data.track.repository.MusicTrackRepository
import com.truedigital.features.utils.MockDataModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class GetMyPlaylistTrackUseCaseTest {

    private val musicTrackRepository: MusicTrackRepository = mock()
    private lateinit var getMyPlaylistTrackUseCase: GetMyPlaylistTrackUseCase

    @BeforeEach
    fun setUp() {
        getMyPlaylistTrackUseCase = GetMyPlaylistTrackUseCaseImpl(musicTrackRepository)
    }

    @Test
    fun execute_success_returnTrackList() = runTest {
        val track01 = MockDataModel.mockTrack.copy(id = 1, image = "image01")
        val track02 = MockDataModel.mockTrack.copy(id = 2, image = "image02")

        whenever(musicTrackRepository.getMyPlaylistTrackList(any(), any(), any())).thenReturn(
            flowOf(listOf(track01, track02))
        )

        val result = getMyPlaylistTrackUseCase.execute(1)
        result.collect {
            val trackResult01 = it[0]
            assertEquals(trackResult01.id, track01.id)
            assertEquals(trackResult01.image, track01.image)

            val trackResult02 = it[1]
            assertEquals(trackResult02.id, track02.id)
            assertEquals(trackResult02.image, track02.image)
        }
    }

    @Test
    fun execute_fail_returnError() = runTest {
        val mockError = "Something went wrong."

        whenever(musicTrackRepository.getMyPlaylistTrackList(any(), any(), any())).thenReturn(
            flow { Exception(mockError) }
        )

        val result = getMyPlaylistTrackUseCase.execute(1)
        result.catch { e ->
            assertEquals(e.localizedMessage, mockError)
        }.collect()
    }
}
