package com.truedigital.common.share.data.coredata.domain

import androidx.lifecycle.MutableLiveData
import com.truedigital.foundation.extension.SingleLiveEvent

object SelectedBottomItemMenuLiveEvent {
    val selectedHomeMenuLiveEvent = SingleLiveEvent<Unit>()
    val selectedWatchMenuLiveEvent = SingleLiveEvent<Unit>()
    val selectedPrivilegeMenuLiveEvent = SingleLiveEvent<Unit>()
    val selectedCommunityMenuLiveEvent = SingleLiveEvent<Unit>()
    val selectedCallChatMenuLiveEvent = SingleLiveEvent<Unit>()
    val selectedLiveEvent = MutableLiveData<Int>()

    fun setSelectedHomeMenu() {
        selectedHomeMenuLiveEvent.value = Unit
    }

    fun setSelectedWatchMenu() {
        selectedWatchMenuLiveEvent.value = Unit
    }

    fun setSelectedPrivilegeMenu() {
        selectedPrivilegeMenuLiveEvent.value = Unit
    }

    fun setSelectedCommunityMenu() {
        selectedCommunityMenuLiveEvent.value = Unit
    }

    fun setSelectedCallChatMenu() {
        selectedCallChatMenuLiveEvent.value = Unit
    }

    fun setSelectedLiveEvent(id: Int) {
        selectedLiveEvent.value = id
    }
}
