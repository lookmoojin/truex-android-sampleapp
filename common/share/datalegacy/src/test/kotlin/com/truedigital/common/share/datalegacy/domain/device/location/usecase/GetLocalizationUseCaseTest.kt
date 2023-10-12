package com.truedigital.common.share.datalegacy.domain.device.location.usecase

import com.nhaarman.mockitokotlin2.atLeastOnce
import com.nhaarman.mockitokotlin2.mock
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.core.domain.usecase.GetLocalizationUseCase
import com.truedigital.core.domain.usecase.GetLocalizationUseCaseImpl
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito

class GetLocalizationUseCaseTest {
    private var localizationRepository: LocalizationRepository = mock()

    private lateinit var getLocalizationUseCase: GetLocalizationUseCase

    @BeforeEach
    fun setUp() {
        getLocalizationUseCase = GetLocalizationUseCaseImpl(localizationRepository)
    }

    @Test
    fun `check method was called`() {
        getLocalizationUseCase.execute()
        Mockito.verify(localizationRepository, atLeastOnce()).getAppLocalization()
    }

    @Test
    fun `when change language EN`() {
        Mockito.`when`(localizationRepository.getAppLocalization())
            .thenReturn(LocalizationRepository.Localization.EN)
        assert(getLocalizationUseCase.execute() == LocalizationRepository.Localization.EN)
    }
}
