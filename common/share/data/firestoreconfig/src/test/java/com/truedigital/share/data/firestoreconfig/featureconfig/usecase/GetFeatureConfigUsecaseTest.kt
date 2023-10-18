package com.truedigital.share.data.firestoreconfig.featureconfig.usecase

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.truedigital.core.data.device.repository.LocalizationRepository
import com.truedigital.share.data.firestoreconfig.featureconfig.repository.FeatureConfigRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class GetFeatureConfigUsecaseTest {
    private val featureConfigRepository: FeatureConfigRepository = mock()
    private val localizationRepository: LocalizationRepository = mock()
    private lateinit var getFeatureConfigUsecase: GetFeatureConfigUsecase

    @BeforeEach
    fun setup() {
        getFeatureConfigUsecase = GetFeatureConfigUsecaseImpl(
            featureConfigRepository,
            localizationRepository
        )
    }

    @Test
    fun `check method loadInitial was called`() {
        getFeatureConfigUsecase.execute()

        verify(featureConfigRepository, times(1)).loadInitialAppConfig(
            countryCode = localizationRepository.getAppCountryCode()
        )
    }
}
