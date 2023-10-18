package com.truedigital.features.music.data.geoblock.repository

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CacheMusicGeoBlockRepositoryTest {

    private lateinit var cacheMusicGeoBlockRepository: CacheMusicGeoBlockRepository

    @BeforeEach
    fun setup() {
        cacheMusicGeoBlockRepository = CacheMusicGeoBlockRepositoryImpl()
    }

    @Test
    fun saveCache_returnSameData() {
        cacheMusicGeoBlockRepository.saveCache(true)

        Assertions.assertEquals(cacheMusicGeoBlockRepository.loadCache(), true)
    }

    @Test
    fun loadCache_beforeSaveCache_returnNull() {
        cacheMusicGeoBlockRepository.loadCache()

        Assertions.assertEquals(cacheMusicGeoBlockRepository.loadCache(), null)
    }
}
