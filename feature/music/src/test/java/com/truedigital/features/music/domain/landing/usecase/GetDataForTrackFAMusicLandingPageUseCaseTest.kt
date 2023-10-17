package com.truedigital.features.music.domain.landing.usecase

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.features.music.data.landing.repository.CacheMusicLandingRepository
import com.truedigital.features.music.domain.landing.model.MusicForYouShelfModel
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

internal class GetDataForTrackFAMusicLandingPageUseCaseTest {

    private val cacheMusicLandingRepository: CacheMusicLandingRepository = mock()
    private val localizationRepository: LocalizationRepository = mock()
    private lateinit var getDataForTrackFAMusicLandingPageUseCase: GetDataForTrackFAMusicLandingPageUseCase

    private val mockMusicForYouShelfModel = MusicForYouShelfModel(
        index = 1,
        shelfIndex = 1,
        title = "title",
        titleFA = "titleFA"
    )

    @BeforeEach
    fun setUp() {
        getDataForTrackFAMusicLandingPageUseCase = GetDataForTrackFAMusicLandingPageUseCaseImpl(
            cacheMusicLandingRepository = cacheMusicLandingRepository,
            localizationRepository = localizationRepository
        )
    }

    @Test
    fun execute_shelfIndexIsNotInCache_returnFAMusicLandingShelfModelAndSaveCache() {
        // Given
        val mockBaseShelfId = "baseShelfId"
        whenever(cacheMusicLandingRepository.getFAShelfList(any())).thenReturn(mutableSetOf(2, 3))
        whenever(localizationRepository.getAppCountryCode()).thenReturn("th")
        whenever(localizationRepository.getAppLanguageCode()).thenReturn("th")

        // When
        val data = getDataForTrackFAMusicLandingPageUseCase.execute(
            mockBaseShelfId,
            mockMusicForYouShelfModel,
            true
        )

        // Then
        assertEquals(mockMusicForYouShelfModel.shelfIndex, data?.shelfIndex)
        assertEquals(mockMusicForYouShelfModel.titleFA, data?.shelfName)
        verify(cacheMusicLandingRepository, times(1))
            .clearCacheIfCountryOrLanguageChange(any(), any())
        verify(cacheMusicLandingRepository, times(1)).getFAShelfList(any())
        verify(cacheMusicLandingRepository, times(1)).saveFAShelf(any(), any())
        verify(localizationRepository, times(1)).getAppCountryCode()
        verify(localizationRepository, times(1)).getAppLanguageCode()
    }

    @Test
    fun execute_shelfIndexIsInCache_returnNull() {
        // Given
        val mockBaseShelfId = "baseShelfId"
        whenever(cacheMusicLandingRepository.getFAShelfList(any())).thenReturn(mutableSetOf(1, 2))

        // When
        val data = getDataForTrackFAMusicLandingPageUseCase.execute(
            mockBaseShelfId,
            mockMusicForYouShelfModel.copy(index = 1),
            false
        )

        // Then
        assertNull(data)
        verify(cacheMusicLandingRepository, times(0))
            .clearCacheIfCountryOrLanguageChange(any(), any())
        verify(cacheMusicLandingRepository, times(1)).getFAShelfList(any())
        verify(cacheMusicLandingRepository, times(0)).saveFAShelf(any(), any())
        verify(localizationRepository, times(0)).getAppCountryCode()
        verify(localizationRepository, times(0)).getAppLanguageCode()
    }
}
