package com.truedigital.common.share.analytics.measurement.usecase.truemoney

abstract class TrackCustomerHasTrueMoneyAbstract {

    companion object {
        private const val CUSTOMER_HAS_TRUE_MONEY = 1
    }

    fun validateHasTrueMoney(status: Int): Boolean {
        return status == CUSTOMER_HAS_TRUE_MONEY
    }
}
