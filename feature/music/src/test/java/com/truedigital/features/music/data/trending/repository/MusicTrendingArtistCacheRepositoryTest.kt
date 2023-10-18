package com.truedigital.features.music.data.trending.repository

import com.truedigital.features.music.domain.trending.model.TrendingArtistModel
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MusicTrendingArtistCacheRepositoryTest {

    private lateinit var musicTrendingArtistCacheRepository: MusicTrendingArtistCacheRepository

    @BeforeEach
    fun setUp() {
        musicTrendingArtistCacheRepository = MusicTrendingArtistCacheRepositoryImpl()
    }

    @Test
    fun loadCacheTrendingArtist_noCache_returnNull() {
        val trendingArtistCacheData = musicTrendingArtistCacheRepository.loadCacheTrendingArtist()
        MatcherAssert.assertThat(trendingArtistCacheData, `is`(nullValue()))
    }

    @Test
    fun loadCacheTrendingArtist_haveCache_returnCacheData() {
        musicTrendingArtistCacheRepository.saveCacheTrendingArtist(getTrendingArtistModelMockData())
        val trendingArtistCacheData =
            musicTrendingArtistCacheRepository.loadCacheTrendingArtist()?.firstOrNull()
        MatcherAssert.assertThat(trendingArtistCacheData, `is`(not(nullValue())))
        MatcherAssert.assertThat(trendingArtistCacheData?.id, `is`(100))
        MatcherAssert.assertThat(trendingArtistCacheData?.name, `is`("fakeName"))
        MatcherAssert.assertThat(trendingArtistCacheData?.image, `is`("fakeImage"))
    }

    @Test
    fun saveCacheTrendingArtist_success_cacheIsSaved() {
        musicTrendingArtistCacheRepository.saveCacheTrendingArtist(getTrendingArtistModelMockData())
        MatcherAssert.assertThat(
            musicTrendingArtistCacheRepository.loadCacheTrendingArtist(),
            `is`(not(nullValue()))
        )
    }

    @Test
    fun clearCacheTrendingArtist_success_cacheIsCleared() {
        musicTrendingArtistCacheRepository.saveCacheTrendingArtist(getTrendingArtistModelMockData())
        musicTrendingArtistCacheRepository.clearCacheTrendingArtist()
        MatcherAssert.assertThat(
            musicTrendingArtistCacheRepository.loadCacheTrendingArtist(),
            `is`(nullValue())
        )
    }

    private fun getTrendingArtistModelMockData(): List<TrendingArtistModel> {
        return arrayListOf<TrendingArtistModel>().apply {
            add(
                TrendingArtistModel().apply {
                    this.id = 100
                    this.name = "fakeName"
                    this.image = "fakeImage"
                }
            )
        }
    }
}
