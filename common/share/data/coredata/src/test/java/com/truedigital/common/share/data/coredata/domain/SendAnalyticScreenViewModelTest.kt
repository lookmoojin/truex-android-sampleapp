package com.truedigital.common.share.data.coredata.domain

import com.truedigital.common.share.data.coredata.deeplink.constants.DeeplinkConstants
import com.truedigital.share.mock.arch.InstantTaskExecutorExtension
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@ExtendWith(InstantTaskExecutorExtension::class)
class SendAnalyticScreenViewModelTest {

    private lateinit var sendAnalyticScreenViewModel: SendAnalyticScreenViewModel

    @BeforeEach
    fun setUp() {
        sendAnalyticScreenViewModel = SendAnalyticScreenViewModel()
    }

    @Test
    fun testCheckTagFragmentAndSendAnalyticScreenName_tagFragmentForHomeFragment() {
        sendAnalyticScreenViewModel.checkTagFragmentAndSendAnalyticScreenName(DeeplinkConstants.DeeplinkSwitchTab.HOME_TAP)

        assertNotNull(sendAnalyticScreenViewModel.onSendAnalyticScreenHomeLiveEvent().value)
        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenWatchLiveEvent().value)
        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenPrivilegeLiveEvent().value)
        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenListenLiveEvent().value)
        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenCommunityLiveEvent().value)
        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenCallChatLiveEvent().value)
    }

    @Test
    fun testCheckTagFragmentAndSendAnalyticScreenName_tagFragmentForWatchFragment() {
        sendAnalyticScreenViewModel.checkTagFragmentAndSendAnalyticScreenName(DeeplinkConstants.DeeplinkSwitchTab.WATCH_TAP)

        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenHomeLiveEvent().value)
        assertNotNull(sendAnalyticScreenViewModel.onSendAnalyticScreenWatchLiveEvent().value)
        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenPrivilegeLiveEvent().value)
        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenListenLiveEvent().value)
        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenCommunityLiveEvent().value)
        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenCallChatLiveEvent().value)
    }

    @Test
    fun testCheckTagFragmentAndSendAnalyticScreenName_tagFragmentForPrivilegeFragment() {
        sendAnalyticScreenViewModel.checkTagFragmentAndSendAnalyticScreenName(DeeplinkConstants.DeeplinkSwitchTab.PRIVILEGE_TAP)

        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenHomeLiveEvent().value)
        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenWatchLiveEvent().value)
        assertNotNull(sendAnalyticScreenViewModel.onSendAnalyticScreenPrivilegeLiveEvent().value)
        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenListenLiveEvent().value)
        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenCommunityLiveEvent().value)
        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenCallChatLiveEvent().value)
    }

    @Test
    fun testCheckTagFragmentAndSendAnalyticScreenName_tagFragmentForCommunityFragment() {
        sendAnalyticScreenViewModel.checkTagFragmentAndSendAnalyticScreenName(DeeplinkConstants.DeeplinkSwitchTab.COMMUNITY_TAP)

        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenHomeLiveEvent().value)
        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenWatchLiveEvent().value)
        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenPrivilegeLiveEvent().value)
        assertNotNull(sendAnalyticScreenViewModel.onSendAnalyticScreenCommunityLiveEvent().value)
        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenCallChatLiveEvent().value)
    }

    @Test
    fun testCheckTagFragmentAndSendAnalyticScreenName_tagFragmentForCommunicatorFragment() {
        sendAnalyticScreenViewModel.checkTagFragmentAndSendAnalyticScreenName(DeeplinkConstants.DeeplinkSwitchTab.COMMUNICATOR_TAP)

        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenHomeLiveEvent().value)
        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenWatchLiveEvent().value)
        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenPrivilegeLiveEvent().value)
        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenListenLiveEvent().value)
        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenCommunityLiveEvent().value)
        assertNotNull(sendAnalyticScreenViewModel.onSendAnalyticScreenCallChatLiveEvent().value)
    }

    @Test
    fun testCheckTagFragmentAndSendAnalyticScreenName_tagFragmentForReadFragment() {
        sendAnalyticScreenViewModel.checkTagFragmentAndSendAnalyticScreenName(DeeplinkConstants.DeeplinkSwitchTab.READ_TAP)

        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenHomeLiveEvent().value)
        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenWatchLiveEvent().value)
        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenPrivilegeLiveEvent().value)
        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenListenLiveEvent().value)
        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenCommunityLiveEvent().value)
        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenCallChatLiveEvent().value)
        assertNotNull(sendAnalyticScreenViewModel.onSendAnalyticScreenReadLiveEvent().value)
    }

    @Test
    fun testCheckTagFragmentAndSendAnalyticScreenName_tagFragmentNull() {
        sendAnalyticScreenViewModel.checkTagFragmentAndSendAnalyticScreenName(null)

        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenHomeLiveEvent().value)
        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenWatchLiveEvent().value)
        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenPrivilegeLiveEvent().value)
        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenListenLiveEvent().value)
        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenCommunityLiveEvent().value)
        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenCallChatLiveEvent().value)
    }

    @Test
    fun testCheckTagFragmentAndSendAnalyticScreenName_tagFragmentEmpty() {
        sendAnalyticScreenViewModel.checkTagFragmentAndSendAnalyticScreenName("")

        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenHomeLiveEvent().value)
        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenWatchLiveEvent().value)
        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenPrivilegeLiveEvent().value)
        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenListenLiveEvent().value)
        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenCommunityLiveEvent().value)
        assertNull(sendAnalyticScreenViewModel.onSendAnalyticScreenCallChatLiveEvent().value)
    }

    @Test
    fun getIsLandingOnReadScope_currentTabIsRead_returnTrue() {
        sendAnalyticScreenViewModel.checkTagFragmentAndSendAnalyticScreenName(DeeplinkConstants.DeeplinkSwitchTab.READ_TAP)

        val result = sendAnalyticScreenViewModel.getIsLandingOnReadScope()
        assertTrue(result)
    }

    @Test
    fun getIsLandingOnReadScope_currentTabIsNotRead_returnFalse() {
        sendAnalyticScreenViewModel.checkTagFragmentAndSendAnalyticScreenName(DeeplinkConstants.DeeplinkSwitchTab.HOME_TAP)

        val result = sendAnalyticScreenViewModel.getIsLandingOnReadScope()
        assertFalse(result)
    }
}
