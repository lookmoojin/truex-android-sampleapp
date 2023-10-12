package com.truedigital.core.utils.networkconnection

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.annotation.RequiresPermission
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class NetworkTrackerWidget(context: Context) : DefaultLifecycleObserver,
    ConnectivityManager.NetworkCallback() {
    companion object {
        const val CONNECTION_STATE_AVAILABLE = "available"
        const val CONNECTION_STATE_LOST = "lost"
        const val CONNECTION_STATE_UNAVAILABLE = "unavailable"
    }

    private lateinit var lifeCycleOwner: LifecycleOwner
    private val connectivityManager: ConnectivityManager by lazy {
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private val connectionPool = mutableListOf<Network>()
    private var lastUpdatedConnection: String = ""
    var onConnectionChanged: ((isConnected: Boolean, reason: String?) -> Unit)? = null

    fun start(lifecycleOwner: LifecycleOwner) {
        lifecycleOwner.lifecycle.addObserver(this)
        this.lifeCycleOwner = lifecycleOwner
    }

    fun stop() {
        lifeCycleOwner.lifecycle.removeObserver(this)
    }

    @RequiresPermission(value = "android.permission.ACCESS_NETWORK_STATE")
    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        registerNetworkCallback()
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        unregisterNetworkCallback()
    }

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        connectionPool.add(network)
        if (connectionPool.size == 1) {
            notifyConnectionStateChanged(CONNECTION_STATE_AVAILABLE)
        }
    }

    override fun onLost(network: Network) {
        super.onLost(network)

        if (connectionPool.size == 1) {
            notifyConnectionStateChanged(CONNECTION_STATE_LOST)
        }
        connectionPool.remove(network)
    }

    override fun onUnavailable() {
        super.onUnavailable()
        notifyConnectionStateChanged(CONNECTION_STATE_UNAVAILABLE)
    }

    private fun notifyConnectionStateChanged(connectionState: String) {
        if (lastUpdatedConnection.isNotEmpty() && lastUpdatedConnection != connectionState) {
            val isConnected = connectionState == CONNECTION_STATE_AVAILABLE
            onConnectionChanged?.invoke(isConnected, connectionState)
        }
        lastUpdatedConnection = connectionState
    }

    @RequiresPermission(value = "android.permission.ACCESS_NETWORK_STATE")
    private fun registerNetworkCallback() {
        val networkRequest = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
        connectivityManager.registerNetworkCallback(networkRequest, this)
    }

    private fun unregisterNetworkCallback() {
        connectivityManager.unregisterNetworkCallback(this)
    }
}
