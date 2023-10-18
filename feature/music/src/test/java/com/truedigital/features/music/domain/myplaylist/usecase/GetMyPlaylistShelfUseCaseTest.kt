package com.truedigital.features.music.domain.myplaylist.usecase

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.music.data.playlist.repository.MusicPlaylistRepository
import com.truedigital.features.tuned.data.util.LocalisedString
import com.truedigital.features.tuned.data.util.PagedResults
import com.truedigital.features.utils.MockDataModel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GetMyPlaylistShelfUseCaseTest {
    private lateinit var getMyPlaylistShelfUseCase: GetMyPlaylistShelfUseCase
    private val musicPlaylistRepository: MusicPlaylistRepository = mock()

    @BeforeEach
    fun setup() {
        getMyPlaylistShelfUseCase = GetMyPlaylistShelfUseCaseImpl(
            musicPlaylistRepository = musicPlaylistRepository
        )
    }

    @Test
    fun testGetMyPlaylist_responseNull_returnEmptyList() = runTest {
        // Given
        whenever(musicPlaylistRepository.getMyPlaylists())
            .thenReturn(flowOf(null))

        // When
        val result = getMyPlaylistShelfUseCase.execute()

        // Then
        result.collect {
            assertTrue {
                it.isEmpty()
            }
        }
    }

    @Test
    fun testGetMyPlaylist_resultsNotNull_returnList() = runTest {
        // Given
        whenever(musicPlaylistRepository.getMyPlaylists())
            .thenReturn(
                flowOf(MockDataModel.mockPlaylistPage)
            )
        // When
        val result = getMyPlaylistShelfUseCase.execute()

        // Then
        result.collect {
            assertTrue {
                it.isNotEmpty()
            }
            assertEquals(0, it.first().index)
            assertEquals(1, it.first().playlistId)
            assertEquals("nameEn", it.first().title)
            assertEquals("", it.first().coverImage)
            assertEquals(10, it.first().trackCount)
        }
    }

    @Test
    fun testGetMyPlaylist_playlistNameEn_returnNameEn() = runTest {
        // Given
        whenever(musicPlaylistRepository.getMyPlaylists())
            .thenReturn(
                flowOf(
                    PagedResults(
                        offset = 1,
                        count = 100,
                        total = 1,
                        results = listOf(
                            MockDataModel.mockPlaylist.apply {
                                name = listOf(
                                    LocalisedString(language = "en", value = "nameEn"),
                                    LocalisedString(language = "th", value = "nameTh")
                                )
                            }
                        )
                    )
                )
            )
        // When
        val result = getMyPlaylistShelfUseCase.execute()

        // Then
        result.collect {
            assertEquals("nameEn", it.first().title)
        }
    }

    @Test
    fun testGetMyPlaylist_playlistNameEnValueNull_returnNameEmpty() = runTest {
        // Given
        whenever(musicPlaylistRepository.getMyPlaylists())
            .thenReturn(
                flowOf(
                    PagedResults(
                        offset = 1,
                        count = 100,
                        total = 1,
                        results = listOf(
                            MockDataModel.mockPlaylist.apply {
                                name = listOf(
                                    LocalisedString(language = "th", value = "nameTh"),
                                    LocalisedString(language = "en", value = null)
                                )
                            }
                        )
                    )
                )
            )
        // When
        val result = getMyPlaylistShelfUseCase.execute()

        // Then
        result.collect {
            assertEquals("", it.first().title)
        }
    }

    @Test
    fun testGetMyPlaylist_playlistNameTh_returnNameEmpty() = runTest {
        // Given
        whenever(musicPlaylistRepository.getMyPlaylists())
            .thenReturn(
                flowOf(
                    PagedResults(
                        offset = 1,
                        count = 100,
                        total = 1,
                        results = listOf(
                            MockDataModel.mockPlaylist.apply {
                                name = listOf(
                                    LocalisedString(language = "th", value = "nameTh")
                                )
                            }
                        )
                    )
                )
            )
        // When
        val result = getMyPlaylistShelfUseCase.execute()

        // Then
        result.collect {
            assertEquals("", it.first().title)
        }
    }

    @Test
    fun testGetMyPlaylist_coverImageEn_returnCoverImageEn() = runTest {
        // Given
        whenever(musicPlaylistRepository.getMyPlaylists())
            .thenReturn(
                flowOf(
                    PagedResults(
                        offset = 1,
                        count = 100,
                        total = 1,
                        results = listOf(
                            MockDataModel.mockPlaylist.apply {
                                coverImage = listOf(
                                    LocalisedString(language = "en", value = "coverImageEn"),
                                    LocalisedString(language = "th", value = "coverImageTh")
                                )
                            }
                        )
                    )
                )
            )
        // When
        val result = getMyPlaylistShelfUseCase.execute()

        // Then
        result.collect {
            assertEquals("coverImageEn", it.first().coverImage)
        }
    }

    @Test
    fun testGetMyPlaylist_coverImageEnValueNull_returnCoverImageEmpty() = runTest {
        // Given
        whenever(musicPlaylistRepository.getMyPlaylists())
            .thenReturn(
                flowOf(
                    PagedResults(
                        offset = 1,
                        count = 100,
                        total = 1,
                        results = listOf(
                            MockDataModel.mockPlaylist.apply {
                                coverImage = listOf(
                                    LocalisedString(language = "th", value = "coverImageTh"),
                                    LocalisedString(language = "en", value = null)
                                )
                            }
                        )
                    )
                )
            )
        // When
        val result = getMyPlaylistShelfUseCase.execute()

        // Then
        result.collect {
            assertEquals("", it.first().coverImage)
        }
    }

    @Test
    fun testGetMyPlaylist_coverImageTh_returnCoverImageEmpty() = runTest {
        // Given
        whenever(musicPlaylistRepository.getMyPlaylists())
            .thenReturn(
                flowOf(
                    PagedResults(
                        offset = 1,
                        count = 100,
                        total = 1,
                        results = listOf(
                            MockDataModel.mockPlaylist.apply {
                                coverImage = listOf(
                                    LocalisedString(language = "th", value = "coverImageTh")
                                )
                            }
                        )
                    )
                )
            )
        // When
        val result = getMyPlaylistShelfUseCase.execute()

        // Then
        result.collect {
            assertEquals("", it.first().coverImage)
        }
    }
}
