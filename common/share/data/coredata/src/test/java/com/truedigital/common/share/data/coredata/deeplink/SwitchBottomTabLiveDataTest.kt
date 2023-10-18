package com.truedigital.common.share.data.coredata.deeplink

import com.truedigital.foundation.extension.test.LiveDataTestObserver
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.reactivex.RxImmediateSchedulerExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertEquals

@ExtendWith(InstantTaskExecutorExtension::class, RxImmediateSchedulerExtension::class)
internal class SwitchBottomTabLiveDataTest {

    @Test
    fun `test SwitchBottomTabLiveData send switchBottomTabFromDeeplink`() {
        val pair = Pair("", "")
        val switchBottomTabObserver =
            LiveDataTestObserver.test(SwitchBottomTabLiveData.switchBottomTabLiveEvent)

        SwitchBottomTabLiveData.switchBottomTabFromDeeplink(pair)

        switchBottomTabObserver.assertValue(pair)
    }

    @Test
    fun `test SwitchBottomTabLiveData send setOnOpenDeeplinkForBottomNav`() {
        val url = ""
        val onOpenDeeplinkForBottomNavObserver =
            LiveDataTestObserver.test(SwitchBottomTabLiveData.onOpenDeeplinkForBottomNav)

        SwitchBottomTabLiveData.setOnOpenDeeplinkForBottomNav(url)

        onOpenDeeplinkForBottomNavObserver.assertValue(url)
    }

    @Test
    fun `test clearDeeplinkForBottomNav`() {
        SwitchBottomTabLiveData.clearDeeplinkForBottomNav()
        assertEquals("", SwitchBottomTabLiveData.onOpenDeeplinkForBottomNav.value)
    }
}
