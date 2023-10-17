package com.truedigital.features.music.data.trending.repository

import com.truedigital.features.music.domain.trending.model.TrendingAlbumModel
import javax.inject.Inject

interface MusicTrendingAlbumCacheRepository {
    fun saveCacheTrendingAlbum(list: List<TrendingAlbumModel>)
    fun loadCacheTrendingAlbum(): List<TrendingAlbumModel>?
    fun clearCacheTrendingAlbum()
}

class MusicTrendingAlbumCacheRepositoryImpl @Inject constructor() : MusicTrendingAlbumCacheRepository {

    companion object {
        private var trendingAlbumList: List<TrendingAlbumModel>? = null
    }

    override fun saveCacheTrendingAlbum(list: List<TrendingAlbumModel>) {
        trendingAlbumList = list
    }

    override fun loadCacheTrendingAlbum(): List<TrendingAlbumModel>? {
        return trendingAlbumList
    }

    override fun clearCacheTrendingAlbum() {
        trendingAlbumList = null
    }
}
