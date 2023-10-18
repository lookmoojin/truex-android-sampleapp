package com.truedigital.features.music.data.trending.repository

import com.truedigital.features.music.domain.trending.model.TrendingPlaylistModel
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class MusicTrendingPlaylistCacheRepositoryTest {
    private lateinit var musicTrendingPlaylistCacheRepository: MusicTrendingPlaylistCacheRepository

    @BeforeEach
    fun setUp() {
        musicTrendingPlaylistCacheRepository = MusicTrendingPlaylistCacheRepositoryImpl()
    }

    @Test
    fun loadCacheTrendingPlaylist_noCache_returnNull() {
        val trendingPlaylistCacheData =
            musicTrendingPlaylistCacheRepository.loadCacheTrendingPlaylist()
        assertNull(trendingPlaylistCacheData)
    }

    @Test
    fun loadCacheTrendingArtist_haveCache_returnCacheData() {
        val trendingArtistModelList = listOf(
            TrendingPlaylistModel().apply {
                this.id = 100
                this.name = "name"
                this.image = "image"
            }
        )

        musicTrendingPlaylistCacheRepository.saveCacheTrendingPlaylist(trendingArtistModelList)

        val trendingPlaylistCacheData =
            musicTrendingPlaylistCacheRepository.loadCacheTrendingPlaylist()?.firstOrNull()
        assertNotNull(trendingPlaylistCacheData)
        assertThat(trendingPlaylistCacheData?.id, `is`(trendingArtistModelList.first().id))
        assertThat(trendingPlaylistCacheData?.name, `is`(trendingArtistModelList.first().name))
        assertThat(trendingPlaylistCacheData?.image, `is`(trendingArtistModelList.first().image))
    }

    @Test
    fun saveCacheTrendingPlaylist_success_cacheIsSaved() {
        val trendingArtistModelList = listOf(
            TrendingPlaylistModel().apply {
                this.id = 100
                this.name = "name"
                this.image = "image"
            }
        )

        musicTrendingPlaylistCacheRepository.clearCacheTrendingPlaylist()
        musicTrendingPlaylistCacheRepository.saveCacheTrendingPlaylist(trendingArtistModelList)

        val trendingPlaylistCacheData =
            musicTrendingPlaylistCacheRepository.loadCacheTrendingPlaylist()?.firstOrNull()
        assertNotNull(trendingPlaylistCacheData)
        assertThat(trendingPlaylistCacheData?.id, `is`(trendingArtistModelList.first().id))
        assertThat(trendingPlaylistCacheData?.name, `is`(trendingArtistModelList.first().name))
        assertThat(trendingPlaylistCacheData?.image, `is`(trendingArtistModelList.first().image))
    }

    @Test
    fun clearCacheTrendingArtist_success_cacheIsCleared() {
        val trendingArtistModelList = listOf(TrendingPlaylistModel())
        musicTrendingPlaylistCacheRepository.saveCacheTrendingPlaylist(trendingArtistModelList)
        musicTrendingPlaylistCacheRepository.clearCacheTrendingPlaylist()

        assertNull(musicTrendingPlaylistCacheRepository.loadCacheTrendingPlaylist())
    }
}
