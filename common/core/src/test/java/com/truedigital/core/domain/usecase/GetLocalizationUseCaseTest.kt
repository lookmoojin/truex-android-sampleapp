package com.truedigital.core.domain.usecase

import com.truedigital.core.data.device.repository.LocalizationRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetLocalizationUseCaseTest {

    private val localizationRepository: LocalizationRepository = mockk()

    private lateinit var getLocalizationUseCase: GetLocalizationUseCase

    @BeforeEach
    fun setUp() {
        getLocalizationUseCase = GetLocalizationUseCaseImpl(localizationRepository)
    }

    @Test
    fun `execute should return app localization`() {

        // Given
        val th: LocalizationRepository.Localization = mockk()

        every { th.countryCode } returns "th"
        every { th.languageCode } returns "TH"

        every { localizationRepository.getAppLocalization() } returns th

        // When
        val result = getLocalizationUseCase.execute()

        // Then
        assertEquals(th, result)

        // Verify that the mocked localizationRepository was called exactly once
        verify(exactly = 1) { localizationRepository.getAppLocalization() }
    }
}
