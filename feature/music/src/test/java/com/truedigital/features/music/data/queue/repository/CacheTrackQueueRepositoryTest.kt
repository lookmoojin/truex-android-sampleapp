package com.truedigital.features.music.data.queue.repository

import com.truedigital.features.music.data.landing.model.response.playlisttrack.PlaylistTrackResponse
import com.truedigital.features.music.data.trending.model.response.playlist.Translation
import com.truedigital.features.utils.MockDataModel
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CacheTrackQueueRepositoryTest {

    private lateinit var cacheTrackQueueRepository: CacheTrackQueueRepository
    private val mockTrackList = listOf(MockDataModel.mockTrack)

    @BeforeEach
    fun setUp() {
        cacheTrackQueueRepository = CacheTrackQueueRepositoryImpl()
    }

    @Test
    fun testSaveCacheTrackQueue_cacheEmpty_returnTrackList() {
        val mockPlaylistId = 1
        cacheTrackQueueRepository.saveCacheTrackQueue(mockPlaylistId, mockTrackList)

        assertEquals(mockTrackList, cacheTrackQueueRepository.getCacheTrackQueue(mockPlaylistId))
    }

    @Test
    fun testSaveCacheTrackQueue_samePlaylistId_returnTrackList() {
        val mockPlaylistId = 1
        cacheTrackQueueRepository.saveCacheTrackQueue(
            mockPlaylistId,
            listOf(
                MockDataModel.mockTrack.copy(
                    name = "name1"
                )
            )
        )
        cacheTrackQueueRepository.saveCacheTrackQueue(
            mockPlaylistId,
            listOf(
                MockDataModel.mockTrack.copy(
                    name = "name2"
                )
            )
        )

        assertEquals("name2", cacheTrackQueueRepository.getCacheTrackQueue(mockPlaylistId)[0].name)
    }

    @Test
    fun testGetCacheTrackQueue_notMatchPlaylistId_returnEmptyList() {
        val mockPlaylistId = 1
        cacheTrackQueueRepository.saveCacheTrackQueue(mockPlaylistId, mockTrackList)

        assertEquals(emptyList(), cacheTrackQueueRepository.getCacheTrackQueue(2))
    }

    @Test
    fun testSaveCachePlaylistTrackQueue_getCacheTrackList_returnTrackList() {
        val mockPlaylistId = 1
        val mockPlaylistTrackList = listOf(
            PlaylistTrackResponse.Result(
                trackId = null,
                playlistTrackId = null,
                songId = null,
                releaseId = null,
                releaseName = null,
                artist = null,
                name = null,
                isExplicit = null,
                duration = null,
                image = null,
                hasLyrics = null,
                isVideo = null,
                translations = listOf(),
                owner = null,
                label = null,
                contentLanguage = null,
                genreList = null
            )
        )
        cacheTrackQueueRepository.saveCachePlaylistTrackQueue(mockPlaylistId, mockPlaylistTrackList)

        assertEquals(1, cacheTrackQueueRepository.getCachePlaylistTrackQueue(mockPlaylistId).size)
    }

    @Test
    fun testSaveCachePlaylistTrackQueue_mapArtistNullValue_returnTrackList() {
        val mockPlaylistId = 1
        val mockPlaylistTrackList = listOf(
            PlaylistTrackResponse.Result(
                trackId = null,
                playlistTrackId = null,
                songId = null,
                releaseId = null,
                releaseName = null,
                artist = listOf(
                    PlaylistTrackResponse.Result.Artist(
                        artistId = null,
                        name = null
                    )
                ),
                name = null,
                isExplicit = null,
                duration = null,
                image = null,
                hasLyrics = null,
                isVideo = null,
                translations = listOf(),
                owner = null,
                label = null,
                contentLanguage = null,
                genreList = null
            )
        )
        cacheTrackQueueRepository.saveCachePlaylistTrackQueue(mockPlaylistId, mockPlaylistTrackList)

        val artists = cacheTrackQueueRepository.getCachePlaylistTrackQueue(mockPlaylistId)
            .first().artists.first()
        assertEquals(0, artists.id)
        assertEquals("", artists.name)
    }

    @Test
    fun testSaveCachePlaylistTrackQueue_mapTrackModel_returnTrackList() {
        val mockPlaylistId = 1
        val mockPlaylistTrackList = listOf(
            PlaylistTrackResponse.Result(
                trackId = 1,
                playlistTrackId = 1,
                songId = 1,
                releaseId = 1,
                releaseName = "releaseName",
                artist = listOf(
                    PlaylistTrackResponse.Result.Artist(
                        artistId = 2,
                        name = "artistName"
                    )
                ),
                name = "name",
                isExplicit = true,
                duration = 200,
                image = "image",
                hasLyrics = true,
                isVideo = true,
                translations = listOf(
                    Translation(
                        language = "TH",
                        value = "translationsTh"
                    )
                ),
                owner = "owner",
                label = "label",
                contentLanguage = "contentLanguage",
                genreList = listOf("genre1", "genre2")
            )
        )
        cacheTrackQueueRepository.saveCachePlaylistTrackQueue(mockPlaylistId, mockPlaylistTrackList)

        assertEquals(1, cacheTrackQueueRepository.getCachePlaylistTrackQueue(mockPlaylistId).size)
        val result = cacheTrackQueueRepository.getCachePlaylistTrackQueue(mockPlaylistId).first()
        assertEquals(1, result.id)
        assertEquals(1, result.playlistTrackId)
        assertEquals(1, result.songId)
        assertEquals(1, result.releaseId)
        assertEquals("releaseName", result.releaseName)
        assertEquals(2, result.artists.first().id)
        assertEquals("artistName", result.artists.first().name)
        assertEquals("translationsTh", result.name)
        assertEquals(true, result.isExplicit)
        assertEquals(200, result.duration)
        assertEquals("image", result.image)
        assertEquals(true, result.hasLyrics)
        assertEquals(true, result.isVideo)
        assertEquals("TH", result.translationsList.first().language)
        assertEquals("translationsTh", result.translationsList.first().value)
        assertEquals("owner", result.owner)
        assertEquals("label", result.label)
        assertEquals("contentLanguage", result.contentLanguage)
        assertEquals(2, result.genreList?.size)
    }

    @Test
    fun testClearCacheTrackQueue() {
        val mockPlaylistId = 1
        cacheTrackQueueRepository.saveCacheTrackQueue(mockPlaylistId, mockTrackList)
        cacheTrackQueueRepository.clearCacheTrackQueue()

        assertEquals(emptyList(), cacheTrackQueueRepository.getCacheTrackQueue(mockPlaylistId))
        assertEquals(
            emptyList(),
            cacheTrackQueueRepository.getCachePlaylistTrackQueue(mockPlaylistId)
        )
    }
}
