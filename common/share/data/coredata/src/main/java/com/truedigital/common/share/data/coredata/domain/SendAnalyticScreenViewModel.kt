package com.truedigital.common.share.data.coredata.domain

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.truedigital.common.share.data.coredata.deeplink.constants.DeeplinkConstants
import com.truedigital.foundation.extension.SingleLiveEvent
import javax.inject.Inject

class SendAnalyticScreenViewModel @Inject constructor() : ViewModel() {

    val sendAnalyticScreenHomeLiveEvent = SingleLiveEvent<Unit>()
    val sendAnalyticScreenWatchLiveEvent = SingleLiveEvent<Unit>()
    val sendAnalyticScreenPrivilegeLiveEvent = SingleLiveEvent<Unit>()
    val sendAnalyticScreenListenLiveEvent = SingleLiveEvent<Unit>()
    val sendAnalyticScreenCallChatLiveEvent = SingleLiveEvent<Unit>()
    val sendAnalyticScreenCommunityLiveEvent = SingleLiveEvent<Unit>()
    val sendAnalyticScreenReadLiveEvent = SingleLiveEvent<Unit>()

    fun onSendAnalyticScreenHomeLiveEvent(): LiveData<Unit> = sendAnalyticScreenHomeLiveEvent
    fun onSendAnalyticScreenWatchLiveEvent(): LiveData<Unit> = sendAnalyticScreenWatchLiveEvent
    fun onSendAnalyticScreenPrivilegeLiveEvent(): LiveData<Unit> = sendAnalyticScreenPrivilegeLiveEvent
    fun onSendAnalyticScreenListenLiveEvent(): LiveData<Unit> = sendAnalyticScreenListenLiveEvent
    fun onSendAnalyticScreenCallChatLiveEvent(): LiveData<Unit> = sendAnalyticScreenCallChatLiveEvent
    fun onSendAnalyticScreenCommunityLiveEvent(): LiveData<Unit> = sendAnalyticScreenCommunityLiveEvent
    fun onSendAnalyticScreenReadLiveEvent(): LiveData<Unit> = sendAnalyticScreenReadLiveEvent

    private var currentTab: String = ""

    fun checkTagFragmentAndSendAnalyticScreenName(tagFragment: String?) {
        if (!tagFragment.isNullOrEmpty()) {
            currentTab = tagFragment.orEmpty()

            when (tagFragment) {
                DeeplinkConstants.DeeplinkSwitchTab.HOME_TAP ->
                    sendAnalyticScreenHomeLiveEvent.value = Unit
                DeeplinkConstants.DeeplinkSwitchTab.WATCH_TAP ->
                    sendAnalyticScreenWatchLiveEvent.value = Unit
                DeeplinkConstants.DeeplinkSwitchTab.PRIVILEGE_TAP ->
                    sendAnalyticScreenPrivilegeLiveEvent.value = Unit
                DeeplinkConstants.DeeplinkSwitchTab.LISTEN_TAP ->
                    sendAnalyticScreenListenLiveEvent.value = Unit
                DeeplinkConstants.DeeplinkSwitchTab.COMMUNITY_TAP ->
                    sendAnalyticScreenCommunityLiveEvent.value = Unit
                DeeplinkConstants.DeeplinkSwitchTab.COMMUNICATOR_TAP ->
                    sendAnalyticScreenCallChatLiveEvent.value = Unit
                DeeplinkConstants.DeeplinkSwitchTab.READ_TAP ->
                    sendAnalyticScreenReadLiveEvent.value = Unit
            }
        }
    }

    fun getIsLandingOnReadScope(): Boolean {
        return currentTab == DeeplinkConstants.DeeplinkSwitchTab.READ_TAP
    }
}
