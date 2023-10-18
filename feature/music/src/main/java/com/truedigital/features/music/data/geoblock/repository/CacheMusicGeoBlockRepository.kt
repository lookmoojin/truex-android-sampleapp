package com.truedigital.features.music.data.geoblock.repository

import javax.inject.Inject

interface CacheMusicGeoBlockRepository {
    fun saveCache(isBlocked: Boolean?)
    fun loadCache(): Boolean?
}

class CacheMusicGeoBlockRepositoryImpl @Inject constructor() : CacheMusicGeoBlockRepository {

    companion object {
        private var isGeoBlock: Boolean? = null
    }

    override fun saveCache(isBlocked: Boolean?) {
        isGeoBlock = isBlocked
    }

    override fun loadCache(): Boolean? = isGeoBlock
}
