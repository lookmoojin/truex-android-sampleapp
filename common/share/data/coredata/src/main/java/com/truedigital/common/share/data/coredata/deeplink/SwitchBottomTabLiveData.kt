@file:OptIn(ObsoleteCoroutinesApi::class)

package com.truedigital.common.share.data.coredata.deeplink

import androidx.lifecycle.MutableLiveData
import com.truedigital.core.extensions.collectSafe
import com.truedigital.foundation.extension.SingleLiveEvent
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.flow.consumeAsFlow
import java.util.concurrent.atomic.AtomicBoolean

object SwitchBottomTabLiveData {
    val switchBottomTabLiveEvent = SingleLiveEvent<Pair<String, String>>()

    val onOpenDeeplinkForBottomNav = MutableLiveData<String>()

    val onOpenCommunicatorDeeplink = SingleDeeplinkEventBus()

    fun switchBottomTabFromDeeplink(pair: Pair<String, String>) {
        this.switchBottomTabLiveEvent.value = pair
    }

    fun setOnOpenDeeplinkForBottomNav(urlDeeplink: String) {
        onOpenDeeplinkForBottomNav.value = urlDeeplink
        onOpenCommunicatorDeeplink.send(urlDeeplink)
    }

    fun clearDeeplinkForBottomNav() {
        onOpenDeeplinkForBottomNav.value = ""
    }
}

class SingleDeeplinkEventBus {
    private val bus: BroadcastChannel<String> = ConflatedBroadcastChannel()
    private val pending = AtomicBoolean(false)

    fun send(message: String) {
        pending.set(true)
        bus.trySend(message)
    }

    suspend fun listen(action: (deeplink: String) -> Unit) {
        bus.openSubscription()
            .consumeAsFlow()
            .collectSafe {
                if (pending.compareAndSet(true, false)) {
                    action(it)
                }
            }
    }
}
