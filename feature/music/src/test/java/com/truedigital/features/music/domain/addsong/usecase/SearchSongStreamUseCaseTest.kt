package com.truedigital.features.music.domain.addsong.usecase

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.music.data.search.model.response.Artist
import com.truedigital.features.music.data.search.model.response.Hit
import com.truedigital.features.music.data.search.model.response.Hits
import com.truedigital.features.music.data.search.model.response.Meta
import com.truedigital.features.music.data.search.model.response.MusicSearchResponse
import com.truedigital.features.music.data.search.model.response.MusicSearchResponseItem
import com.truedigital.features.music.data.search.model.response.Results
import com.truedigital.features.music.data.search.model.response.Source
import com.truedigital.features.music.data.search.repository.MusicSearchRepository
import com.truedigital.features.utils.MockDataModel
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

internal class SearchSongStreamUseCaseTest {

    private lateinit var searchSongStreamUseCase: SearchSongStreamUseCase
    private val musicSearchRepository: MusicSearchRepository = mock()

    private val mockArtist = Artist(
        name = "name"
    )
    private val mockMeta = Meta(
        albumImage = "albumImage"
    )
    private val mockSource = Source(
        artists = listOf(mockArtist),
        id = 1,
        meta = listOf(mockMeta),
        translationsList = listOf(MockDataModel.mockTranslationTH)
    )
    private val mockHit = Hit(
        id = "123",
        source = mockSource
    )
    private val mockHits = Hits(
        hits = listOf(mockHit)
    )
    private val mockResults = Results(
        hits = mockHits
    )
    private val mockMusicSearchResponseItem = MusicSearchResponseItem(
        key = "key",
        results = mockResults
    )

    @BeforeEach
    fun setUp() {
        searchSongStreamUseCase = SearchSongStreamUseCaseImpl(musicSearchRepository)
    }

    @Test
    fun execute_responseIsNotNull_returnListData() = runTest {
        // Given
        whenever(musicSearchRepository.getSongQueryStream(any(), any(), any())).thenReturn(
            MusicSearchResponse().apply { add(mockMusicSearchResponseItem) }
        )

        // When
        val response = searchSongStreamUseCase.execute("query", "1", "20").first()

        // Then
        assertEquals(mockHit.id, response.id?.toString())
        assertEquals(MockDataModel.mockTranslationTH.value, response.songName)
        assertEquals(mockArtist.name, response.artistName)
        assertEquals(mockMeta.albumImage, response.coverImage)
        verify(musicSearchRepository, times(1)).getSongQueryStream(any(), any(), any())
    }

    @Test
    fun execute_responseIsNotNull_sourceIsNull_returnListDataSomeFieldIsEmpty() = runTest {
        // Given
        val mockHitValue = mockHit.copy(source = null)
        val mockHitsValue = mockHits.copy(hits = listOf(mockHitValue))
        val mockResultsValue = mockResults.copy(hits = mockHitsValue)
        val mockMusicSearchResponseItemValue = mockMusicSearchResponseItem.copy(
            results = mockResultsValue
        )
        whenever(musicSearchRepository.getSongQueryStream(any(), any(), any())).thenReturn(
            MusicSearchResponse().apply { add(mockMusicSearchResponseItemValue) }
        )

        // When
        val response = searchSongStreamUseCase.execute("query", "1", "20").first()

        // Then
        assertEquals(mockHit.id, response.id?.toString())
        assertTrue(response.songName.isEmpty())
        assertTrue(response.artistName.isEmpty())
        assertTrue(response.coverImage.isEmpty())
        verify(musicSearchRepository, times(1)).getSongQueryStream(any(), any(), any())
    }

    @Test
    fun execute_responseIsNotNull_ArtistIsNull_metaIsNull_returnListDataArtistAndCoverImageIsNull() =
        runTest {
            // Given
            val mockSourceValue = mockSource.copy(artists = null, meta = null)
            val mockHitValue = mockHit.copy(source = mockSourceValue)
            val mockHitsValue = mockHits.copy(hits = listOf(mockHitValue))
            val mockResultsValue = mockResults.copy(hits = mockHitsValue)
            val mockMusicSearchResponseItemValue = mockMusicSearchResponseItem.copy(
                results = mockResultsValue
            )
            whenever(musicSearchRepository.getSongQueryStream(any(), any(), any())).thenReturn(
                MusicSearchResponse().apply { add(mockMusicSearchResponseItemValue) }
            )

            // When
            val response = searchSongStreamUseCase.execute("query", "1", "20").first()

            // Then
            assertEquals(mockHit.id, response.id?.toString())
            assertEquals(MockDataModel.mockTranslationTH.value, response.songName)
            assertTrue(response.artistName.isEmpty())
            assertTrue(response.coverImage.isEmpty())
            verify(musicSearchRepository, times(1)).getSongQueryStream(any(), any(), any())
        }

    @Test
    fun execute_responseIsNull_returnEmptyList() = runTest {
        // Given
        whenever(musicSearchRepository.getSongQueryStream(any(), any(), any())).thenReturn(null)

        // When
        val response = searchSongStreamUseCase.execute("query", "1", "20")

        // Then
        assertTrue(response.isEmpty())
        verify(musicSearchRepository, times(1)).getSongQueryStream(any(), any(), any())
    }

    @Test
    fun execute_resultsIsNull_returnEmptyList() = runTest {
        // Given
        val mockMusicSearchResponseItemValue = mockMusicSearchResponseItem.copy(results = null)
        whenever(musicSearchRepository.getSongQueryStream(any(), any(), any())).thenReturn(
            MusicSearchResponse().apply { add(mockMusicSearchResponseItemValue) }
        )

        // When
        val response = searchSongStreamUseCase.execute("query", "1", "20")

        // Then
        assertTrue(response.isEmpty())
        verify(musicSearchRepository, times(1)).getSongQueryStream(any(), any(), any())
    }

    @Test
    fun execute_hitsIsNull_returnEmptyList() = runTest {
        // Given
        val mockResultsValue = mockResults.copy(hits = null)
        val mockMusicSearchResponseItemValue = mockMusicSearchResponseItem.copy(
            results = mockResultsValue
        )
        whenever(musicSearchRepository.getSongQueryStream(any(), any(), any())).thenReturn(
            MusicSearchResponse().apply { add(mockMusicSearchResponseItemValue) }
        )

        // When
        val response = searchSongStreamUseCase.execute("query", "1", "20")

        // Then
        assertTrue(response.isEmpty())
        verify(musicSearchRepository, times(1)).getSongQueryStream(any(), any(), any())
    }

    @Test
    fun execute_hitsListIsNull_returnEmptyList() = runTest {
        // Given
        val mockHitsValue = mockHits.copy(hits = null)
        val mockResultsValue = mockResults.copy(hits = mockHitsValue)
        val mockMusicSearchResponseItemValue = mockMusicSearchResponseItem.copy(
            results = mockResultsValue
        )
        whenever(musicSearchRepository.getSongQueryStream(any(), any(), any())).thenReturn(
            MusicSearchResponse().apply { add(mockMusicSearchResponseItemValue) }
        )

        // When
        val response = searchSongStreamUseCase.execute("query", "1", "20")

        // Then
        assertTrue(response.isEmpty())
        verify(musicSearchRepository, times(1)).getSongQueryStream(any(), any(), any())
    }
}
