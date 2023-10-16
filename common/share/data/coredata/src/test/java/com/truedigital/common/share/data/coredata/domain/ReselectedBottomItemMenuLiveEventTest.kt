package com.truedigital.common.share.data.coredata.domain

import com.truedigital.foundation.extension.test.LiveDataTestObserver
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.reactivex.RxImmediateSchedulerExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals

@ExtendWith(InstantTaskExecutorExtension::class, RxImmediateSchedulerExtension::class)
internal class ReselectedBottomItemMenuLiveEventTest {
    @Test
    fun `test ReselectedBottomItemMenuLiveEvent setReselectedHomeMenu`() {
        val reselectedBottomItemMenuObserver =
            LiveDataTestObserver.test(ReselectedBottomItemMenuLiveEvent.reselectedHomeMenuLiveEvent)

        ReselectedBottomItemMenuLiveEvent.setReselectedHomeMenu()

        reselectedBottomItemMenuObserver.assertValue(Unit)
    }

    @Test
    fun `test ReselectedBottomItemMenuLiveEvent setReselectedWatchMenu`() {
        val reselectedBottomItemMenuObserver =
            LiveDataTestObserver.test(ReselectedBottomItemMenuLiveEvent.reselectedWatchMenuLiveEvent)

        ReselectedBottomItemMenuLiveEvent.setReselectedWatchMenu()

        reselectedBottomItemMenuObserver.assertValue(Unit)
    }

    @Test
    fun `test ReselectedBottomItemMenuLiveEvent setReselectedPrivilegeMenu`() {
        val reselectedBottomItemMenuObserver =
            LiveDataTestObserver.test(ReselectedBottomItemMenuLiveEvent.reselectedPrivilegeMenuLiveEvent)

        ReselectedBottomItemMenuLiveEvent.setReselectedPrivilegeMenu()

        reselectedBottomItemMenuObserver.assertValue(Unit)
    }

    @Test
    fun `test ReselectedBottomItemMenuLiveEvent setReselectedCommunityMenu`() {
        val reselectedBottomItemMenuObserver =
            LiveDataTestObserver.test(ReselectedBottomItemMenuLiveEvent.reselectedCommunityMenuLiveEvent)

        ReselectedBottomItemMenuLiveEvent.setReselectedCommunityMenu()

        reselectedBottomItemMenuObserver.assertValue(Unit)
    }

    @Test
    fun `test ReselectedBottomItemMenuLiveEvent setReselectedReadMenu`() {
        val reselectedBottomItemMenuObserver =
            LiveDataTestObserver.test(ReselectedBottomItemMenuLiveEvent.reselectedCallChatMenuLiveEvent)

        ReselectedBottomItemMenuLiveEvent.setReselectedReadMenu()

        reselectedBottomItemMenuObserver.assertValue(Unit)
    }

    @Test
    fun `test ReselectedBottomItemMenuLiveEvent reselectedLiveEvent`() {
        val reselectedLiveEventObserver =
            LiveDataTestObserver.test(ReselectedBottomItemMenuLiveEvent.reselectedLiveEvent)
        val index = 2

        ReselectedBottomItemMenuLiveEvent.setReselected(index)

        reselectedLiveEventObserver.assertValue(index)
    }

    @Test
    fun `test ReselectedBottomItemMenuLiveEvent set get tabIndexSelect`() {
        val index = 2
        ReselectedBottomItemMenuLiveEvent.tabIndexSelect = index

        assertEquals(index, ReselectedBottomItemMenuLiveEvent.tabIndexSelect)
    }
}
