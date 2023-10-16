package com.truedigital.common.share.data.coredata.deeplink

import com.truedigital.foundation.extension.test.LiveDataTestObserver
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import com.truedigital.share.mock.reactivex.RxImmediateSchedulerExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(InstantTaskExecutorExtension::class, RxImmediateSchedulerExtension::class)
internal class OneLinkLiveDataTest {

    @Test
    fun `test OneLinkLiveData send url string`() {
        val url = "ABC"
        val deepLinkObserver = LiveDataTestObserver.test(OneLinkLiveData.deepLinkFromOneLink)

        val historySize = deepLinkObserver.valueHistory().size
        OneLinkLiveData.openDeepLinkFromOneLink(url)

        deepLinkObserver.assertValue(url)
        deepLinkObserver.assertHistorySize(historySize + 1)
    }

    @Test
    fun `test OneLinkLiveData send url null`() {
        val deepLinkObserver = LiveDataTestObserver.test(OneLinkLiveData.deepLinkFromOneLink)

        val historySize = deepLinkObserver.valueHistory().size
        OneLinkLiveData.openDeepLinkFromOneLink(null)

        deepLinkObserver.assertHistorySize(historySize)
    }

    @Test
    fun `test OneLinkLiveData send url empty`() {
        val deepLinkObserver = LiveDataTestObserver.test(OneLinkLiveData.deepLinkFromOneLink)

        val historySize = deepLinkObserver.valueHistory().size
        OneLinkLiveData.openDeepLinkFromOneLink("")

        deepLinkObserver.assertHistorySize(historySize)
    }
}
