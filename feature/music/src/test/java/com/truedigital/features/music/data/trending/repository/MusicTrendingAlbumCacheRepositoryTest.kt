package com.truedigital.features.music.data.trending.repository

import com.truedigital.features.music.domain.trending.model.TrendingAlbumModel
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MusicTrendingAlbumCacheRepositoryTest {

    private lateinit var musicTrendingAlbumCacheRepository: MusicTrendingAlbumCacheRepository

    @BeforeEach
    fun setUp() {
        musicTrendingAlbumCacheRepository = MusicTrendingAlbumCacheRepositoryImpl()
    }

    @Test
    fun loadCacheTrendingAlbum_noCache_returnNull() {
        val trendingAlbumCacheData = musicTrendingAlbumCacheRepository.loadCacheTrendingAlbum()
        MatcherAssert.assertThat(trendingAlbumCacheData, `is`(nullValue()))
    }

    @Test
    fun loadCacheTrendingAlbum_haveCache_returnCacheData() {
        musicTrendingAlbumCacheRepository.saveCacheTrendingAlbum(getTrendingAlbumModelMockData())
        val trendingAlbumCacheData =
            musicTrendingAlbumCacheRepository.loadCacheTrendingAlbum()?.firstOrNull()
        MatcherAssert.assertThat(trendingAlbumCacheData, `is`(not(nullValue())))
        MatcherAssert.assertThat(trendingAlbumCacheData?.id, `is`(1))
        MatcherAssert.assertThat(trendingAlbumCacheData?.name, `is`("name"))
        MatcherAssert.assertThat(trendingAlbumCacheData?.image, `is`("image"))
    }

    @Test
    fun saveCacheTrendingAlbum_success_cacheIsSaved() {
        musicTrendingAlbumCacheRepository.saveCacheTrendingAlbum(getTrendingAlbumModelMockData())
        MatcherAssert.assertThat(
            musicTrendingAlbumCacheRepository.loadCacheTrendingAlbum(),
            `is`(not(nullValue()))
        )
    }

    @Test
    fun clearCacheTrendingAlbum_success_cacheIsCleared() {
        musicTrendingAlbumCacheRepository.saveCacheTrendingAlbum(getTrendingAlbumModelMockData())
        musicTrendingAlbumCacheRepository.clearCacheTrendingAlbum()
        MatcherAssert.assertThat(
            musicTrendingAlbumCacheRepository.loadCacheTrendingAlbum(),
            `is`(nullValue())
        )
    }

    private fun getTrendingAlbumModelMockData(): List<TrendingAlbumModel> {
        return arrayListOf<TrendingAlbumModel>().apply {
            add(
                TrendingAlbumModel().apply {
                    this.id = 1
                    this.name = "name"
                    this.image = "image"
                    this.artistName = "artistName"
                }
            )
        }
    }
}
