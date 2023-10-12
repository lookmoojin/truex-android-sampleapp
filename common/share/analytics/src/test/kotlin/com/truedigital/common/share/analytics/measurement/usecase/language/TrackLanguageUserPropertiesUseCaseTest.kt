package com.truedigital.common.share.analytics.measurement.usecase.language

import com.truedigital.common.share.analytics.measurement.AnalyticManager
import com.truedigital.core.data.device.repository.LocalizationRepository
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant.Key as Key

class TrackLanguageUserPropertiesUseCaseTest {

    // Mock dependencies
    private val analyticManager: AnalyticManager = mockk()
    private val localizationRepository: LocalizationRepository = mockk()

    private lateinit var useCase: TrackLanguageUserPropertiesUseCaseImpl

    @BeforeEach
    fun setUp() {
        useCase = TrackLanguageUserPropertiesUseCaseImpl(analyticManager, localizationRepository)
    }

    @Test
    fun `execute should call trackUserProperties with the app language code`() {
        // Given
        val appLanguageCode = "en-US"
        every { localizationRepository.getAppLanguageCode() } returns appLanguageCode

        justRun { analyticManager.trackUserProperties(Key.KEY_LANGUAGE, appLanguageCode) }

        // When
        useCase.execute()

        // Then
        verify(exactly = 1) {
            analyticManager.trackUserProperties(
                Key.KEY_LANGUAGE,
                appLanguageCode
            )
        }
    }
}
