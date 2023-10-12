package com.truedigital.common.share.datalegacy.navigation.truemoney

import com.truedigital.foundation.extension.SingleLiveEvent

object TrueMoneyNavigation {

    private val actionRequest = SingleLiveEvent<TrueMoneyRequest>()

    fun getActionRequest(): SingleLiveEvent<TrueMoneyRequest> {
        return actionRequest
    }

    fun openGameCampaign() {
        actionRequest.value = TrueMoneyRequest.TRUEMONEY_GAME
    }

    fun openPaymentBarCode() {
        actionRequest.value = TrueMoneyRequest.PAYMENT_BARCODE
    }

    fun openScanner() {
        actionRequest.value = TrueMoneyRequest.OPEN_SCANNER
    }

    fun openCheckBalance() {
        actionRequest.value = TrueMoneyRequest.CHECK_BALANCE
    }

    fun openVasPackage() {
        actionRequest.value = TrueMoneyRequest.BUY_VAS_PACKAGE
    }

    fun getProfile() {
        actionRequest.value = TrueMoneyRequest.GET_PROFILE
    }
}
