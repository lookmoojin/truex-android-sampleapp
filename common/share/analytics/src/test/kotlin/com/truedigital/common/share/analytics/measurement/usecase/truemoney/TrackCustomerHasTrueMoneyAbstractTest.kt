package com.truedigital.common.share.analytics.measurement.usecase.truemoney

import com.nhaarman.mockitokotlin2.mock
import com.truedigital.common.share.analytics.measurement.AnalyticManager
import com.truedigital.common.share.datalegacy.wrapper.AuthManagerWrapper
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class TrackCustomerHasTrueMoneyAbstractTest {

    private lateinit var trackCustomerHasTrueMoneyAbstract: TrackCustomerHasTrueMoneyAbstract
    private var authManagerWrapper: AuthManagerWrapper = mock()
    private var analyticManager: AnalyticManager = mock()

    @BeforeEach
    fun setUp() {
        trackCustomerHasTrueMoneyAbstract = TrackCustomerHasTrueMoneyUseCaseImpl(
            authManagerWrapper = authManagerWrapper,
            analyticManager = analyticManager
        )
    }

    @Test
    fun `Validate customer has trueMoney`() {
        assertTrue(trackCustomerHasTrueMoneyAbstract.validateHasTrueMoney(1))
    }

    @Test
    fun `Validate customer has not trueMoney`() {
        assertFalse(trackCustomerHasTrueMoneyAbstract.validateHasTrueMoney(0))
    }
}
