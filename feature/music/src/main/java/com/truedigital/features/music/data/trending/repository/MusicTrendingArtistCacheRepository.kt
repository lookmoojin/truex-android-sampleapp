package com.truedigital.features.music.data.trending.repository

import com.truedigital.features.music.domain.trending.model.TrendingArtistModel
import javax.inject.Inject

interface MusicTrendingArtistCacheRepository {
    fun saveCacheTrendingArtist(list: List<TrendingArtistModel>)
    fun loadCacheTrendingArtist(): List<TrendingArtistModel>?
    fun clearCacheTrendingArtist()
}

class MusicTrendingArtistCacheRepositoryImpl @Inject constructor() : MusicTrendingArtistCacheRepository {

    companion object {
        private var trendingArtistList: List<TrendingArtistModel>? = null
    }

    override fun saveCacheTrendingArtist(list: List<TrendingArtistModel>) {
        trendingArtistList = list
    }

    override fun loadCacheTrendingArtist(): List<TrendingArtistModel>? {
        return trendingArtistList
    }

    override fun clearCacheTrendingArtist() {
        trendingArtistList = null
    }
}
