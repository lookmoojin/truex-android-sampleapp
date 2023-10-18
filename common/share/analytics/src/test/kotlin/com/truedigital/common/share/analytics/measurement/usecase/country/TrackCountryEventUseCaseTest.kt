package com.truedigital.common.share.analytics.measurement.usecase.country

import com.truedigital.common.share.analytics.measurement.AnalyticManager
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant.Event
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant.Key
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TrackCountryEventUseCaseTest {

    @MockK(relaxUnitFun = true)
    lateinit var mockAnalyticManager: AnalyticManager

    private lateinit var trackCountryEventUseCase: TrackCountryEventUseCase

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        trackCountryEventUseCase = TrackCountryEventUseCaseImpl(mockAnalyticManager)
    }

    @Test
    fun `execute - should track change country event`() {
        // Given
        val oldCountry = "Brazil"
        val newCountry = "USA"

        // When
        trackCountryEventUseCase.execute(oldCountry, newCountry)

        // Then
        verify {
            mockAnalyticManager.trackEvent(
                hashMapOf(
                    Key.KEY_EVENT_NAME to Event.EVENT_CHANGE_COUNTRY,
                    Key.KEY_CHANGE_COUNTRY_CURRENT_SETTING to oldCountry,
                    Key.KEY_CHANGE_COUNTRY_NEW_SETTING to newCountry
                )
            )
        }
    }
}
