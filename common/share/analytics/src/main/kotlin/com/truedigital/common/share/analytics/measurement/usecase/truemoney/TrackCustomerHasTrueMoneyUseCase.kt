package com.truedigital.common.share.analytics.measurement.usecase.truemoney

import com.truedigital.common.share.analytics.measurement.AnalyticManager
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant.Key.KEY_CUSTOMER_HAS_TRUE_MONEY
import com.truedigital.common.share.datalegacy.wrapper.AuthManagerWrapper
import javax.inject.Inject

interface TrackCustomerHasTrueMoneyUseCase {
    fun execute()
}

class TrackCustomerHasTrueMoneyUseCaseImpl @Inject constructor(
    private val authManagerWrapper: AuthManagerWrapper,
    private val analyticManager: AnalyticManager
) : TrackCustomerHasTrueMoneyUseCase, TrackCustomerHasTrueMoneyAbstract() {

    override fun execute() {
        authManagerWrapper.getProfileCache()?.ascend.let { ascend ->
            validateHasTrueMoney(ascend?.status ?: 0).let { status ->
                analyticManager.trackUserProperties(
                    KEY_CUSTOMER_HAS_TRUE_MONEY,
                    status.toString()
                )
            }
        }
    }
}
