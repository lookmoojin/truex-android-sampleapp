package com.truedigital.core.utils.networkconnection.common

import android.app.Activity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.truedigital.core.utils.networkconnection.ConnectivityStateHolder
import com.truedigital.core.utils.networkconnection.Event
import com.truedigital.core.utils.networkconnection.NetworkConnectivityListener
import com.truedigital.core.utils.networkconnection.NetworkEvents

internal object Constants {
    const val ID_KEY = "networkconnection.previousState"
}

internal fun NetworkConnectivityListener.onListenerCreated() {

    NetworkEvents.observe(
        this as LifecycleOwner,
        Observer {
            if (previousState != null)
                networkConnectivityChanged(it)
        }
    )
}

internal fun NetworkConnectivityListener.onListenerResume() {
    if (!shouldBeCalled || !checkOnResume) return

    val previousState = previousState
    val isConnected = ConnectivityStateHolder.isConnected

    this.previousState = isConnected

    val connectionLost = previousState != false && !isConnected
    val connectionBack = previousState == false && isConnected

    if (connectionLost || connectionBack) {
        networkConnectivityChanged(Event.ConnectivityEvent(isConnected))
    }
}

/**
 * This property serves as a flag to detect if this activity lost network
 */
internal var NetworkConnectivityListener.previousState: Boolean?
    get() {
        return when (this) {
            is Fragment -> this.arguments?.previousState
            is Activity -> this.intent.extras?.previousState
            else -> null
        }
    }
    set(value) {
        when (this) {
            is Fragment -> {
                val a = this.arguments ?: Bundle()
                a.previousState = value
                this.arguments = a
            }
            is Activity -> {
                val a = this.intent.extras ?: Bundle()
                a.previousState = value
                this.intent.replaceExtras(a)
            }
        }
    }
internal var Bundle.previousState: Boolean?
    get() = when (getInt(Constants.ID_KEY, -1)) {
        -1 -> null
        0 -> false
        else -> true
    }
    set(value) {
        putInt(Constants.ID_KEY, if (value == true) 1 else 0)
    }
