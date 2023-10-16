package com.truedigital.common.share.data.coredata.domain

import com.truedigital.foundation.extension.test.LiveDataTestObserver
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.reactivex.RxImmediateSchedulerExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantTaskExecutorExtension::class, RxImmediateSchedulerExtension::class)
class SelectedBottomItemMenuLiveEventTest {
    @Test
    fun `test SelectedBottomItemMenuLiveEvent setSelectedHomeMenu`() {
        val selectedBottomItemMenuObserver =
            LiveDataTestObserver.test(SelectedBottomItemMenuLiveEvent.selectedHomeMenuLiveEvent)

        SelectedBottomItemMenuLiveEvent.setSelectedHomeMenu()

        selectedBottomItemMenuObserver.assertValue(Unit)
    }

    @Test
    fun `test SelectedBottomItemMenuLiveEvent setSelectedWatchMenu`() {
        val selectedBottomItemMenuObserver =
            LiveDataTestObserver.test(SelectedBottomItemMenuLiveEvent.selectedWatchMenuLiveEvent)

        SelectedBottomItemMenuLiveEvent.setSelectedWatchMenu()

        selectedBottomItemMenuObserver.assertValue(Unit)
    }

    @Test
    fun `test SelectedBottomItemMenuLiveEvent setSelectedPrivilegeMenu`() {
        val selectedBottomItemMenuObserver =
            LiveDataTestObserver.test(SelectedBottomItemMenuLiveEvent.selectedPrivilegeMenuLiveEvent)

        SelectedBottomItemMenuLiveEvent.setSelectedPrivilegeMenu()

        selectedBottomItemMenuObserver.assertValue(Unit)
    }

    @Test
    fun `test SelectedBottomItemMenuLiveEvent setSelectedCommunityMenu`() {
        val selectedBottomItemMenuObserver =
            LiveDataTestObserver.test(SelectedBottomItemMenuLiveEvent.selectedCommunityMenuLiveEvent)

        SelectedBottomItemMenuLiveEvent.setSelectedCommunityMenu()

        selectedBottomItemMenuObserver.assertValue(Unit)
    }

    @Test
    fun `test SelectedBottomItemMenuLiveEvent setSelectedReadMenu`() {
        val selectedBottomItemMenuObserver =
            LiveDataTestObserver.test(SelectedBottomItemMenuLiveEvent.selectedCallChatMenuLiveEvent)

        SelectedBottomItemMenuLiveEvent.setSelectedCallChatMenu()

        selectedBottomItemMenuObserver.assertValue(Unit)
    }
}
