package com.truedigital.features.tuned.data.api

import android.content.Context
import android.content.Intent
import com.truedigital.features.tuned.data.ObfuscatedKeyValueStoreInterface
import com.truedigital.features.tuned.data.device.repository.DeviceRepository
import com.truedigital.features.tuned.data.user.repository.MusicUserRepositoryImpl
import com.truedigital.features.tuned.presentation.common.TunedActivity.Companion.LOSS_OF_NETWORK_INTENT
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class ConnectivityInterceptor constructor(
    private val context: Context,
    private val sharedPreferences: ObfuscatedKeyValueStoreInterface,
    private val deviceRepository: DeviceRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val userExists = sharedPreferences.contains(MusicUserRepositoryImpl.CURRENT_USER_KEY)
        if (userExists && !deviceRepository.isNetworkConnected()) {
            // send broadcast for activities which register to this and show offline dialog
            context.sendBroadcast(Intent(LOSS_OF_NETWORK_INTENT))
            // throw exception as well to block calls
            // rx error block can check this exception to avoid double handling of error
            throw NoConnectivityException()
        }
        return chain.proceed(chain.request())
    }

    class NoConnectivityException : IOException() {
        override val message: String
            get() = "No network connected"
    }
}
