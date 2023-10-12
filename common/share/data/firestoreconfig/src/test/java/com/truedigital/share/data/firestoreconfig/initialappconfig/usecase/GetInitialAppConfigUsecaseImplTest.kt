package com.truedigital.share.data.firestoreconfig.initialappconfig.usecase

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.share.data.firestoreconfig.initialappconfig.repository.InitialAppConfigRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class GetInitialAppConfigUsecaseImplTest {

    private var initialAppConfigRepository: InitialAppConfigRepository = mock()
    private val localizationRepository: LocalizationRepository = mock()

    private lateinit var getInitialAppConfigUsecase: GetInitialAppConfigUsecase

    @BeforeEach
    fun setUp() {
        getInitialAppConfigUsecase = GetInitialAppConfigUsecaseImpl(
            initialAppConfigRepository = initialAppConfigRepository,
            localizationRepository = localizationRepository
        )
    }

    @Test
    fun `check method loadInitial was called`() {
        getInitialAppConfigUsecase.execute()

        verify(initialAppConfigRepository, times(1)).loadInitialAppConfig(
            countryCode = localizationRepository.getAppCountryCode()
        )
    }
}
