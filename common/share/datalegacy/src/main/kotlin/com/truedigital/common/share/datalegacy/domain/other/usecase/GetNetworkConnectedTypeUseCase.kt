package com.truedigital.common.share.datalegacy.domain.other.usecase

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import javax.inject.Inject

interface GetNetworkConnectedTypeUseCase {
    fun execute(): String
}

class GetNetworkConnectedTypeUseCaseImpl @Inject constructor(private val context: Context) :
    GetNetworkConnectedTypeUseCase {

    companion object {
        const val CELLULAR = "cellular"
        const val WIFI = "wifi"
        const val NETWORK_ERROR = "error"
    }

    override fun execute(): String {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            ?.let { network ->
                when {
                    network.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> CELLULAR
                    network.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> WIFI
                    else -> NETWORK_ERROR
                }
            } ?: NETWORK_ERROR
    }
}
