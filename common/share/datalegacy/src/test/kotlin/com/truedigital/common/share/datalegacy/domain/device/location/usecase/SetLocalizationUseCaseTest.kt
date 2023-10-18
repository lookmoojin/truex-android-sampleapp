package com.truedigital.common.share.datalegacy.domain.device.location.usecase

import com.nhaarman.mockitokotlin2.atLeastOnce
import com.nhaarman.mockitokotlin2.mock
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.domain.usecase.SetLocalizationUseCase
import com.truedigital.core.domain.usecase.SetLocalizationUseCaseImpl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.verify

class SetLocalizationUseCaseTest {
    private var localizationRepository: LocalizationRepository = mock()

    private lateinit var setLocalizationUseCase: SetLocalizationUseCase

    @BeforeEach
    fun setUp() {
        setLocalizationUseCase = SetLocalizationUseCaseImpl(localizationRepository)
    }

    @Test
    fun `check method was called`() {
        val countryCode = "TH"
        val languageCode = "en"
        setLocalizationUseCase.execute(countryCode, languageCode)
        verify(localizationRepository, atLeastOnce()).setLocalization(countryCode, languageCode)
    }
}
