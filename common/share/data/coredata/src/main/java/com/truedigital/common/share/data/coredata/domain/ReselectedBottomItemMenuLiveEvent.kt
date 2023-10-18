package com.truedigital.common.share.data.coredata.domain

import androidx.lifecycle.MutableLiveData
import com.truedigital.foundation.extension.SingleLiveEvent

object ReselectedBottomItemMenuLiveEvent {
    val reselectedHomeMenuLiveEvent = SingleLiveEvent<Unit>()
    val reselectedWatchMenuLiveEvent = SingleLiveEvent<Unit>()
    val reselectedPrivilegeMenuLiveEvent = SingleLiveEvent<Unit>()
    val reselectedCommunityMenuLiveEvent = SingleLiveEvent<Unit>()
    val reselectedCallChatMenuLiveEvent = SingleLiveEvent<Unit>()
    val reselectedLiveEvent = MutableLiveData<Int>()
    var tabIndexSelect: Int = 0
        get() = field
        set(value) {
            field = value
        }

    fun setReselectedHomeMenu() {
        reselectedHomeMenuLiveEvent.value = Unit
    }

    fun setReselectedWatchMenu() {
        reselectedWatchMenuLiveEvent.value = Unit
    }

    fun setReselectedPrivilegeMenu() {
        reselectedPrivilegeMenuLiveEvent.value = Unit
    }

    fun setReselectedCommunityMenu() {
        reselectedCommunityMenuLiveEvent.value = Unit
    }

    fun setReselectedReadMenu() {
        reselectedCallChatMenuLiveEvent.value = Unit
    }

    fun setReselected(id: Int) {
        reselectedLiveEvent.value = id
    }
}
