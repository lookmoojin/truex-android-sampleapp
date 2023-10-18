package com.truedigital.common.share.analytics.measurement.usecase.country

import com.truedigital.common.share.analytics.measurement.AnalyticManager
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant.Key.KEY_COUNTRY_FLAG
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TrackCountryUserPropertiesUseCaseTest {

    @MockK
    lateinit var mockAnalyticManager: AnalyticManager

    lateinit var trackCountryUserPropertiesUseCase: TrackCountryUserPropertiesUseCase

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        trackCountryUserPropertiesUseCase = TrackCountryUserPropertiesUseCaseImpl(mockAnalyticManager)
    }

    @Test
    fun `execute should call trackUserProperties on analyticManager with correct arguments`() {
        val country = "USA"
        every { mockAnalyticManager.trackUserProperties(KEY_COUNTRY_FLAG, country) } returns Unit

        trackCountryUserPropertiesUseCase.execute(country)

        verify { mockAnalyticManager.trackUserProperties(KEY_COUNTRY_FLAG, country) }
    }
}
