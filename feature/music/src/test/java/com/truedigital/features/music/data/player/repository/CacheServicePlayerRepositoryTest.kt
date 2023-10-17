package com.truedigital.features.music.data.player.repository

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class CacheServicePlayerRepositoryTest {

    private lateinit var cacheServicePlayerRepository: CacheServicePlayerRepository

    @BeforeEach
    fun setUp() {
        cacheServicePlayerRepository = CacheServicePlayerRepositoryImpl()
    }

    @Test
    fun testGetServiceRunning_returnServiceRunning() {
        assertFalse(cacheServicePlayerRepository.getServiceRunning())
    }

    @Test
    fun testSaveServiceRunning_returnServiceRunningTrue() {
        cacheServicePlayerRepository.saveServiceRunning()
        assertTrue(cacheServicePlayerRepository.getServiceRunning())
    }

    @Test
    fun testClearServiceRunning_returnServiceRunningFalse() {
        cacheServicePlayerRepository.saveServiceRunning()
        cacheServicePlayerRepository.clearServiceRunning()
        assertFalse(cacheServicePlayerRepository.getServiceRunning())
    }
}
