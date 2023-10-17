package com.truedigital.features.music.data.landing.repository

import com.truedigital.features.music.domain.landing.model.MusicForYouShelfModel
import com.truedigital.features.tuned.data.productlist.model.ProductListType
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CacheMusicLandingRepositoryTest {

    private lateinit var cacheMusicLandingRepository: CacheMusicLandingRepository
    private val mockMusicForYouShelfModel = MusicForYouShelfModel(
        index = 10,
        shelfIndex = 11,
        title = "title",
        productListType = ProductListType.ARTIST_ALBUM
    )

    @BeforeEach
    fun setUp() {
        cacheMusicLandingRepository = CacheMusicLandingRepositoryImpl()
    }

    @Test
    fun savePathApiForYouShelf_pathApiIsSave() {
        // Given
        val mockPath = "path"
        cacheMusicLandingRepository.savePathApiForYouShelf(mockPath)

        // When
        val data = cacheMusicLandingRepository.loadPathApiForYouShelf()

        // Then
        assertEquals(mockPath, data)
    }

    @Test
    fun saveMusicShelfData_musicShelfDataIsSaved() {
        // Given
        val mockBaseShelfId = "baseShelfId"
        cacheMusicLandingRepository.saveMusicShelfData(
            mockBaseShelfId,
            mutableListOf(mockMusicForYouShelfModel)
        )
        cacheMusicLandingRepository.saveMusicShelfData(
            mockBaseShelfId,
            mutableListOf(mockMusicForYouShelfModel)
        )

        // When
        val data = cacheMusicLandingRepository.loadMusicShelfData(mockBaseShelfId)

        // Then
        assertEquals(mockBaseShelfId, data?.first)
        assertEquals(mockMusicForYouShelfModel, data?.second?.first())
    }

    @Test
    fun saveMusicShelfData_notFoundShelfIndexFromCache_musicShelfDataIsSaved() {
        // Given
        val mockBaseShelfId = "baseShelfId"
        cacheMusicLandingRepository.clearCache()
        cacheMusicLandingRepository.saveMusicShelfData(
            mockBaseShelfId,
            mutableListOf(mockMusicForYouShelfModel)
        )

        // When
        val data = cacheMusicLandingRepository.loadMusicShelfData(mockBaseShelfId)

        // Then
        assertEquals(mockBaseShelfId, data?.first)
        assertEquals(mockMusicForYouShelfModel, data?.second?.first())
    }

    @Test
    fun saveFAShelf_notFoundShelfIndexFromCache_faShelfListIsSaved() {
        // Given
        val mockShelfIndex = 1
        val mockBaseShelfId = "baseShelfId"
        cacheMusicLandingRepository.saveFAShelf(mockBaseShelfId, mockShelfIndex)

        // When
        val data = cacheMusicLandingRepository.getFAShelfList(mockBaseShelfId)

        // Then
        assertEquals(mockShelfIndex, data.first())
    }

    @Test
    fun saveFAShelf_foundShelfIndexFromCache_faShelfListIsSaved() {
        // Given
        val mockShelfIndex = 1
        val mockBaseShelfId = "baseShelfId"
        cacheMusicLandingRepository.saveFAShelf(mockBaseShelfId, mockShelfIndex)
        cacheMusicLandingRepository.saveFAShelf(mockBaseShelfId, mockShelfIndex)

        // When
        val data = cacheMusicLandingRepository.getFAShelfList(mockBaseShelfId)

        // Then
        assertEquals(mockShelfIndex, data.first())
    }

    @Test
    fun clearCacheIfCountryOrLanguageChange_countryIsNotChange_languageIsNotChange_cacheIsNotCleared() {
        // Given
        val mockShelfIndex = 1
        val mockBaseShelfId = "baseShelfId"
        cacheMusicLandingRepository.clearCacheIfCountryOrLanguageChange("en", "en")
        cacheMusicLandingRepository.saveFAShelf(mockBaseShelfId, mockShelfIndex)

        // When
        cacheMusicLandingRepository.clearCacheIfCountryOrLanguageChange("en", "en")

        // Then
        val data = cacheMusicLandingRepository.getFAShelfList(mockBaseShelfId).first()
        assertEquals(mockShelfIndex, data)
    }

    @Test
    fun clearCacheIfCountryOrLanguageChange_countryIsNotChange_languageIsChange_cacheIsCleared() {
        // Given
        val mockShelfIndex = 1
        val mockBaseShelfId = "baseShelfId"
        cacheMusicLandingRepository.clearCacheIfCountryOrLanguageChange("en", "en")
        cacheMusicLandingRepository.saveFAShelf(mockBaseShelfId, mockShelfIndex)

        // When
        cacheMusicLandingRepository.clearCacheIfCountryOrLanguageChange("en", "th")

        // Then
        val data = cacheMusicLandingRepository.getFAShelfList(mockBaseShelfId)
        assertTrue(data.size == 0)
    }

    @Test
    fun clearCacheIfCountryOrLanguageChange_countryIsNotChange_cacheIsCleared() {
        // Given
        val mockShelfIndex = 1
        val mockBaseShelfId = "baseShelfId"
        cacheMusicLandingRepository.clearCacheIfCountryOrLanguageChange("en", "en")
        cacheMusicLandingRepository.saveFAShelf(mockBaseShelfId, mockShelfIndex)

        // When
        cacheMusicLandingRepository.clearCacheIfCountryOrLanguageChange("th", "th")

        // Then
        val data = cacheMusicLandingRepository.getFAShelfList(mockBaseShelfId)
        assertTrue(data.size == 0)
    }
}
