package com.truedigital.features.music.domain.myplaylist.usecase

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.music.data.playlist.repository.MusicPlaylistRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class DeleteMyPlaylistUseCaseTest {
    private lateinit var deleteMyPlaylistUseCase: DeleteMyPlaylistUseCase
    private val musicPlaylistRepository: MusicPlaylistRepository = mock()

    @BeforeEach
    fun setup() {
        deleteMyPlaylistUseCase = DeleteMyPlaylistUseCaseImpl(musicPlaylistRepository)
    }

    @Test
    fun testDeletePlaylist_whenSuccess_verifyDeletePlaylist() = runTest {
        whenever(musicPlaylistRepository.deletePlaylist(any())).thenReturn(flowOf(Any()))

        val result = deleteMyPlaylistUseCase.execute(1)

        result.collect()
        verify(musicPlaylistRepository, times(1)).deletePlaylist(1)
    }
}
