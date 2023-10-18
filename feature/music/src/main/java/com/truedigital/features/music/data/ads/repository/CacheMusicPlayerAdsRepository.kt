package com.truedigital.features.music.data.ads.repository

import javax.inject.Inject

interface CacheMusicPlayerAdsRepository {
    fun updateFirstTime()
    fun isFirstTime(): Boolean
    fun resetFirstTime()

    fun action()
    fun getAction(): Boolean
    fun resetAction()

    fun countAds()
    fun getCountAds(): Int
    fun resetCountAds()
}

class CacheMusicPlayerAdsRepositoryImpl @Inject constructor() : CacheMusicPlayerAdsRepository {

    companion object {
        private var firstTime = true
        private var action = false
        private var countAds = 0
    }

    override fun updateFirstTime() {
        firstTime = false
    }

    override fun isFirstTime(): Boolean {
        return firstTime
    }

    override fun resetFirstTime() {
        firstTime = true
    }

    override fun action() {
        action = true
    }

    override fun getAction(): Boolean = action

    override fun resetAction() {
        action = false
    }

    override fun countAds() {
        countAds++
    }

    override fun getCountAds(): Int = countAds

    override fun resetCountAds() {
        countAds = 0
    }
}
