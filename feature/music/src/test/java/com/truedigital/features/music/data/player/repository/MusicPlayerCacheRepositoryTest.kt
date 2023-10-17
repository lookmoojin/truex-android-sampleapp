package com.truedigital.features.music.data.player.repository

import io.mockk.MockKAnnotations
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class MusicPlayerCacheRepositoryTest {

    private lateinit var musicPlayerCacheRepository: MusicPlayerCacheRepository

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        musicPlayerCacheRepository = MusicPlayerCacheRepositoryImpl()
    }

    @Test
    fun setLandingOnListenScope_landingOnListenScopeIsSaved() {
        // When
        musicPlayerCacheRepository.setLandingOnListenScope(true)

        // Then
        assertTrue(musicPlayerCacheRepository.getLandingOnListenScope())
    }

    @Test
    fun getLandingOnListenScope_returnLandingOnListenScope() {
        // Given
        musicPlayerCacheRepository.setLandingOnListenScope(false)

        // When
        val result = musicPlayerCacheRepository.getLandingOnListenScope()

        // Then`
        assertFalse(result)
    }

    @Test
    fun setRadioMediaAssetId_radioMediaAssetIdIsSaved() {
        // Given
        val mediaAsset = "assetId01"
        musicPlayerCacheRepository.clearCache()

        // When
        musicPlayerCacheRepository.setRadioMediaAssetId(mediaAsset)

        // Then
        assertEquals(mediaAsset, musicPlayerCacheRepository.getRadioMediaAssetIdList().first())
    }

    @Test
    fun getRadioMediaAssetIdList_returnRadioMediaAssetIdList() {
        // Given
        val mediaAsset = "assetId01"
        musicPlayerCacheRepository.clearCache()
        musicPlayerCacheRepository.setRadioMediaAssetId(mediaAsset)

        // When
        val result = musicPlayerCacheRepository.getRadioMediaAssetIdList()

        // Then`
        assertEquals(mediaAsset, musicPlayerCacheRepository.getRadioMediaAssetIdList().first())
        assertEquals(1, result.size)
    }

    @Test
    fun setMusicPlayerVisible_musicPlayerVisibleIsSaved() {
        // When
        musicPlayerCacheRepository.setMusicPlayerVisible(true)

        // Then
        assertTrue(musicPlayerCacheRepository.getMusicPlayerVisible())
    }

    @Test
    fun getMusicPlayerVisible_returnMusicPlayerVisibleStatus() {
        // Given
        musicPlayerCacheRepository.setMusicPlayerVisible(false)

        // When
        val result = musicPlayerCacheRepository.getMusicPlayerVisible()

        // Then`
        assertFalse(result)
    }
}
