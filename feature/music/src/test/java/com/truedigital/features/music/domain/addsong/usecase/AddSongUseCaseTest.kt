package com.truedigital.features.music.domain.addsong.usecase

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.music.data.addsong.repository.AddSongRepository
import com.truedigital.features.music.data.addsong.repository.AddSongRepositoryImpl
import com.truedigital.features.utils.MockDataModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AddSongUseCaseTest {
    private lateinit var addSongUseCase: AddSongUseCase
    private val addSongRepository: AddSongRepository = mock()

    @BeforeEach
    fun setup() {
        addSongUseCase = AddSongUseCaseImpl(
            addSongRepository
        )
    }

    @Test
    fun addSong_success_returnPlaylist() = runTest {
        whenever(addSongRepository.addTracksToPlaylist(any(), any())).thenReturn(
            flowOf(MockDataModel.mockPlaylist)
        )

        val result = addSongUseCase.execute("1", listOf(111))

        result.collect {
            assertEquals(
                MockDataModel.mockPlaylist.id,
                it.id
            )
        }
    }

    @Test
    fun addSong_fail_returnError() = runTest {
        whenever(addSongRepository.addTracksToPlaylist(any(), any())).thenReturn(
            flow { error(AddSongRepositoryImpl.ERROR_ADD_SONGS_TO_PLAYLIST) }
        )

        val result = addSongUseCase.execute("123", emptyList())

        result.catch {
            assertEquals(
                AddSongRepositoryImpl.ERROR_ADD_SONGS_TO_PLAYLIST,
                it.message
            )
        }.collect()
    }
}
