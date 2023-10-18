package com.truedigital.features.music.domain.myplaylist.usecase

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.music.data.playlist.repository.MusicPlaylistRepository
import com.truedigital.features.music.domain.myplaylist.model.MyPlaylistItemType
import com.truedigital.features.tuned.data.util.LocalisedString
import com.truedigital.features.utils.MockDataModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GetMyPlaylistUseCaseTest {

    private val musicPlaylistRepository: MusicPlaylistRepository = mock()
    private lateinit var getMyPlaylistUseCase: GetMyPlaylistUseCase

    @BeforeEach
    fun setUp() {
        getMyPlaylistUseCase = GetMyPlaylistUseCaseImpl(musicPlaylistRepository)
    }

    @Test
    fun execute_success_coverImageIsEN_nameIsEN_returnMyPlaylistData() = runTest {
        val mockCoverImage = LocalisedString(GetMyPlaylistUseCaseImpl.EN, "coverImage")
        val mockName = LocalisedString(GetMyPlaylistUseCaseImpl.EN, "nameEn")

        val mockPlaylist = MockDataModel.mockPlaylist.copy(
            id = 10,
            coverImage = listOf(mockCoverImage),
            name = listOf(mockName)
        )

        whenever(musicPlaylistRepository.getPlaylist(any())).thenReturn(flowOf(mockPlaylist))

        val result = getMyPlaylistUseCase.execute("playlistId")
        result.collect {
            assertEquals(it.id, mockPlaylist.id)
            assertEquals(it.coverImage, mockCoverImage.value)
            assertEquals(it.playlistName, mockName.value)
            assertEquals(it.itemId, 1)
            assertEquals(it.itemType, MyPlaylistItemType.HEADER)
        }
    }

    @Test
    fun execute_success_coverImageIsTH_nameIsTH_returnMyPlaylistData() = runTest {
        val mockCoverImage = LocalisedString("th", "coverImage")
        val mockName = LocalisedString("th", "nameEn")

        val mockPlaylist = MockDataModel.mockPlaylist.copy(
            id = 10,
            coverImage = listOf(mockCoverImage),
            name = listOf(mockName)
        )

        whenever(musicPlaylistRepository.getPlaylist(any())).thenReturn(flowOf(mockPlaylist))

        val result = getMyPlaylistUseCase.execute("playlistId")
        result.collect {
            assertEquals(it.id, mockPlaylist.id)
            assertTrue(it.coverImage.isEmpty())
            assertTrue(it.playlistName.isEmpty())
            assertEquals(it.itemId, 1)
            assertEquals(it.itemType, MyPlaylistItemType.HEADER)
        }
    }

    @Test
    fun execute_fail_returnError() = runTest {
        val errorMessage = "Something went wrong."

        whenever(musicPlaylistRepository.getPlaylist(any())).thenReturn(
            flow { Exception(errorMessage) }
        )

        val result = getMyPlaylistUseCase.execute("playlistId")
        result.catch { e ->
            assertEquals(e.localizedMessage, errorMessage)
        }.collect()
    }
}
