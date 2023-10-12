package com.truedigital.core.domain.usecase

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.truedigital.core.data.device.repository.LocalizationRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class SetLocalizationUseCaseTest {

    private lateinit var useCase: SetLocalizationUseCase

    private val localizationRepository: LocalizationRepository = mock()

    @BeforeEach
    fun setUp() {
        useCase = SetLocalizationUseCaseImpl(
            localizationRepository = localizationRepository
        )
    }

    @Test
    fun `Test execute using Localization`() {
        // arrange
        val localize = LocalizationRepository.Localization.TH

        // act
        useCase.execute(localize)

        // assert
        verify(localizationRepository, times(1))
            .setLocalization(localize.countryCode, localize.languageCode)
    }

    @Test
    fun `Test execute using language code and country code`() {
        // arrange
        val country = "TH"
        val language = "th"

        // act
        useCase.execute(country, language)

        // assert
        verify(localizationRepository, times(1)).setLocalization(country, language)
    }
}
