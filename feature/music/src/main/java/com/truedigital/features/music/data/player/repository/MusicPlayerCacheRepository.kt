package com.truedigital.features.music.data.player.repository

import javax.inject.Inject

interface MusicPlayerCacheRepository {
    fun setLandingOnListenScope(isLandingOnListenScope: Boolean)
    fun setRadioMediaAssetId(radioId: String)
    fun setMusicPlayerVisible(isVisible: Boolean)
    fun getLandingOnListenScope(): Boolean
    fun getRadioMediaAssetIdList(): List<String>
    fun getMusicPlayerVisible(): Boolean
    fun clearCache()
}

class MusicPlayerCacheRepositoryImpl @Inject constructor() : MusicPlayerCacheRepository {

    private var isLandingOnListenScope: Boolean = false
    private var isMusicPlayerVisible: Boolean = false
    private val radioMediaAssetIdList: ArrayList<String> = arrayListOf()

    override fun setLandingOnListenScope(isLandingOnListenScope: Boolean) {
        this.isLandingOnListenScope = isLandingOnListenScope
    }

    override fun setRadioMediaAssetId(radioId: String) {
        radioMediaAssetIdList.add(radioId)
    }

    override fun setMusicPlayerVisible(isVisible: Boolean) {
        this.isMusicPlayerVisible = isVisible
    }

    override fun getLandingOnListenScope(): Boolean = isLandingOnListenScope

    override fun getRadioMediaAssetIdList(): List<String> = radioMediaAssetIdList

    override fun getMusicPlayerVisible(): Boolean = isMusicPlayerVisible

    override fun clearCache() {
        isLandingOnListenScope = false
        radioMediaAssetIdList.clear()
    }
}
