package com.truedigital.features.music.domain.landing.usecase

import com.truedigital.features.music.data.player.repository.MusicPlayerCacheRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class GetRadioMediaAssetIdUseCaseTest {

    @MockK
    private lateinit var musicPlayerCacheRepository: MusicPlayerCacheRepository

    private lateinit var getRadioMediaAssetIdUseCase: GetRadioMediaAssetIdUseCase

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        getRadioMediaAssetIdUseCase = GetRadioMediaAssetIdUseCaseImpl(musicPlayerCacheRepository)
    }

    @Test
    fun execute_notFoundIndex_saveAndReturnAssetId() {
        // Given
        every { musicPlayerCacheRepository.getRadioMediaAssetIdList() } returns listOf("id")
        every { musicPlayerCacheRepository.setRadioMediaAssetId(any()) } just runs

        // When
        val result = getRadioMediaAssetIdUseCase.execute("id01")

        // Then
        assertEquals(0, result)
        verify(exactly = 2) { musicPlayerCacheRepository.getRadioMediaAssetIdList() }
        verify(exactly = 1) { musicPlayerCacheRepository.setRadioMediaAssetId(any()) }
    }

    @Test
    fun execute_foundIndex_returnAssetId() {
        // Given
        every { musicPlayerCacheRepository.getRadioMediaAssetIdList() } returns listOf("id01")

        // When
        val result = getRadioMediaAssetIdUseCase.execute("id01")

        // Then
        assertEquals(0, result)
        verify(exactly = 1) { musicPlayerCacheRepository.getRadioMediaAssetIdList() }
    }
}
