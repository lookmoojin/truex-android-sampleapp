package com.truedigital.core.utils.networkconnection

sealed class Event {
    class ConnectivityEvent(val isConnected: Boolean) : Event()
}
