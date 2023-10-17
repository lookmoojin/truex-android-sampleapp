package com.truedigital.features.tuned.data.device.repository

import com.truedigital.features.tuned.data.device.model.Device
import io.reactivex.Single

interface DeviceRepository {
    fun getUniqueId(): String
    fun getToken(): String
    fun get(): Device
    fun isWifiConnected(): Boolean
    fun getLikesCount(): Int
    fun isNetworkConnected(): Boolean
    fun isEmulator(): Boolean
    fun isArtistHintShown(): Boolean
    fun setArtistHintStatus(isShown: Boolean)
    fun getLSID(): Single<String>
}
