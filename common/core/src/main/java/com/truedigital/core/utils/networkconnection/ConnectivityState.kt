package com.truedigital.core.utils.networkconnection

import com.newrelic.agent.android.NewRelic

/**
 * Enables synchronous and asynchronous connectivity state checking thanks to LiveData and stored states.
 * @see isConnected to get the instance connectivity state
 * @see NetworkEvents to observe connectivity changes
 */
interface ConnectivityState {
    /**
     * Stored connectivity state of the device
     * True if the device has a available network
     */
    val isConnected: Boolean
        get() {
            return try {
                networkStats.any {
                    it.isAvailable
                }
            } catch (error: ConcurrentModificationException) {
                val handlingExceptionMap = mapOf(
                    "Key" to "ConnectivityState.isConnected()",
                    "Value" to "Unexpected error with $error"
                )
                NewRelic.recordHandledException(error, handlingExceptionMap)
                true
            }
        }

    /**
     * The stats of the networks being used by the device
     */
    val networkStats: Iterable<NetworkState>
}
