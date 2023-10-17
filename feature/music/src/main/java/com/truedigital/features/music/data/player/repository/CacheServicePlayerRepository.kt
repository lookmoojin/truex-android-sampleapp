package com.truedigital.features.music.data.player.repository

import javax.inject.Inject

interface CacheServicePlayerRepository {
    fun getServiceRunning(): Boolean
    fun saveServiceRunning()
    fun clearServiceRunning()
}

class CacheServicePlayerRepositoryImpl @Inject constructor() : CacheServicePlayerRepository {

    companion object {
        private var isServiceRunning = false
    }

    override fun getServiceRunning(): Boolean = isServiceRunning

    override fun saveServiceRunning() {
        isServiceRunning = true
    }

    override fun clearServiceRunning() {
        isServiceRunning = false
    }
}
