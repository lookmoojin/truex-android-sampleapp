package com.truedigital.features.music.data.trending.repository

import com.truedigital.features.music.domain.trending.model.TrendingPlaylistModel
import javax.inject.Inject

interface MusicTrendingPlaylistCacheRepository {
    fun saveCacheTrendingPlaylist(list: List<TrendingPlaylistModel>)
    fun loadCacheTrendingPlaylist(): List<TrendingPlaylistModel>?
    fun clearCacheTrendingPlaylist()
}

class MusicTrendingPlaylistCacheRepositoryImpl @Inject constructor() : MusicTrendingPlaylistCacheRepository {

    companion object {
        private var trendingPlaylistList: List<TrendingPlaylistModel>? = null
    }

    override fun saveCacheTrendingPlaylist(list: List<TrendingPlaylistModel>) {
        trendingPlaylistList = list
    }

    override fun loadCacheTrendingPlaylist(): List<TrendingPlaylistModel>? {
        return trendingPlaylistList
    }

    override fun clearCacheTrendingPlaylist() {
        trendingPlaylistList = null
    }
}
