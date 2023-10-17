package com.truedigital.features.music.data.ads.repository

import io.mockk.MockKAnnotations
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CacheMusicPlayerAdsRepositoryTest {

    lateinit var repository: CacheMusicPlayerAdsRepository

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        repository = CacheMusicPlayerAdsRepositoryImpl()
    }

    @Test
    fun updateFirstTime_returnFalse() {
        repository.updateFirstTime()

        assertEquals(false, repository.isFirstTime())
    }

    @Test
    fun resetFirstTime_returnTrue() {
        repository.resetFirstTime()

        assertEquals(true, repository.isFirstTime())
    }

    @Test
    fun action_returnTrue() {
        repository.action()

        assertEquals(true, repository.getAction())
    }

    @Test
    fun resetAction_returnFalse() {
        repository.resetAction()

        assertEquals(false, repository.getAction())
    }

    @Test
    fun countAds_returnCountAds() {
        repository.countAds()

        assertEquals(1, repository.getCountAds())
    }

    @Test
    fun resetCountAds_returnCountAdsDefault() {
        repository.resetCountAds()

        assertEquals(0, repository.getCountAds())
    }
}
