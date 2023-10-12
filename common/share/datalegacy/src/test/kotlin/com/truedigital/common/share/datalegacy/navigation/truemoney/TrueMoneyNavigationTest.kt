package com.truedigital.common.share.datalegacy.navigation.truemoney

import com.truedigital.foundation.extension.SingleLiveEvent
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantTaskExecutorExtension::class)
internal class TrueMoneyNavigationTest {

    @Test
    fun `test openGameCampaign`() {
        val event = mockk<SingleLiveEvent<TrueMoneyRequest>>(relaxed = true)
        TrueMoneyNavigation.getActionRequest().observeForever(event::setValue)

        TrueMoneyNavigation.openGameCampaign()

        verify(exactly = 1) { event.setValue(TrueMoneyRequest.TRUEMONEY_GAME) }
    }

    @Test
    fun `test openPaymentBarCode`() {
        val event = mockk<SingleLiveEvent<TrueMoneyRequest>>(relaxed = true)
        TrueMoneyNavigation.getActionRequest().observeForever(event::setValue)

        TrueMoneyNavigation.openPaymentBarCode()

        verify(exactly = 1) { event.setValue(TrueMoneyRequest.PAYMENT_BARCODE) }
    }
}
