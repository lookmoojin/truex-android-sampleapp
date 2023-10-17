package com.truedigital.features.tuned.presentation.popup

import android.os.Bundle
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.domain.facade.lostnetwork.LossOfNetworkFacade
import com.truedigital.features.tuned.presentation.popups.presenter.LossOfNetworkPresenter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito

class LossOfNetworkPresenterTest {

    private lateinit var lossOfNetworkPresenter: LossOfNetworkPresenter
    private val lossOfNetworkFacade: LossOfNetworkFacade = mock()
    private val router: LossOfNetworkPresenter.RouterSurface = mock()
    private val view: LossOfNetworkPresenter.ViewSurface = mock()
    private val bundle = Mockito.mock(Bundle::class.java)

    @BeforeEach
    fun setup() {
        lossOfNetworkPresenter = LossOfNetworkPresenter(
            lossOfNetworkFacade
        )
        lossOfNetworkPresenter.onInject(view, router)
    }

    @Test
    fun testOnStart_whenUserAllowedOffline() {
        // given
        whenever(lossOfNetworkFacade.isUserAllowedOffline()).thenReturn(
            true
        )

        // when
        lossOfNetworkPresenter.onStart(bundle)

        // then
        verify(view, times(1)).showUserOfflineAllowed()
    }

    @Test
    fun testOnStart_whenNetworkReconnecting() {
        // given
        whenever(lossOfNetworkFacade.isUserAllowedOffline()).thenReturn(
            false
        )

        // when
        lossOfNetworkPresenter.onStart(bundle)

        // then
        verify(view, times(1)).showNetworkReconnecting()
    }

    @Test
    fun testOnGoOffline() {
        // given
        whenever(lossOfNetworkFacade.hasOfflineRight()).thenReturn(
            false
        )

        // when
        lossOfNetworkPresenter.onGoOffline()

        // then
        verify(view, times(1)).showUpgradeDialog()
    }
}
