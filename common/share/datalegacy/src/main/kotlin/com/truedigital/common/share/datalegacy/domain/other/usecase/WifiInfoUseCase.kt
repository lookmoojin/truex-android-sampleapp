package com.truedigital.common.share.datalegacy.domain.other.usecase

import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import org.json.JSONObject
import javax.inject.Inject

interface WifiInfoUseCase {
    fun getWifiName(): String
    fun getWifiInfo(): String
    fun getConnectionInfo(): WifiInfo?
}

class WifiInfoUseCaseImpl @Inject constructor(private val wifiManager: WifiManager) : WifiInfoUseCase {

    companion object {
        private const val SERVICE_SET_IDENTIFIER = "SSID"
        private const val BASIC_SERVICE_SET_IDENTIFIER = "BSSID"
        private const val SUPPLICANT_STATE = "Supplicant_state"
        private const val RECEIVED_SIGNAL_STRENGTH_INDICATOR = "RSSI"
        private const val LINK_SPEED = "Link speed"
        private const val NETWORK_ID = "Net_ID"
        private const val FREQUENCY = "Frequency"
        private const val METERED_HINT = "Metered_hint"
        private const val SCORE = "score"
    }

    override fun getWifiInfo(): String {
        val wifiInfo = getConnectionInfo()
        wifiInfo?.let { wifiInfo ->
            val output = JSONObject().apply {
                put(SERVICE_SET_IDENTIFIER, wifiInfo.ssid?.replace("\"", "") ?: "")
                put(BASIC_SERVICE_SET_IDENTIFIER, wifiInfo.bssid ?: "")
                put(SUPPLICANT_STATE, wifiInfo.supplicantState ?: "")
                put(RECEIVED_SIGNAL_STRENGTH_INDICATOR, wifiInfo.rssi)
                put(LINK_SPEED, wifiInfo.linkSpeed)
                put(NETWORK_ID, wifiInfo.networkId)
                put(FREQUENCY, wifiInfo.frequency)
                val wifiInfoToArray = wifiInfo.toString().replace(" ", "").split(",")
                put(
                    METERED_HINT,
                    if (wifiInfoToArray.size >= 8 && wifiInfoToArray[7].contains("Meteredhint:")) {
                        wifiInfoToArray[7].replace("Meteredhint:", "")
                    } else {
                        ""
                    }
                )
                put(
                    SCORE,
                    if (wifiInfoToArray.size >= 9 && wifiInfoToArray[8].contains("score:")) {
                        wifiInfoToArray[8].replace("score:", "")
                    } else {
                        ""
                    }
                )
            }
            return output.toString()
        }
        return ""
    }

    override fun getConnectionInfo(): WifiInfo? {
        if (wifiManager.isWifiEnabled) {
            return wifiManager.connectionInfo
        }
        return null
    }

    override fun getWifiName(): String {
        return wifiManager.connectionInfo.ssid
    }
}
