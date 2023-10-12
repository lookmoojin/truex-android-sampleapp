package com.truedigital.common.share.datalegacy.domain.other.usecase

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.telephony.CellInfo
import android.telephony.CellInfoLte
import android.telephony.TelephonyManager
import android.telephony.gsm.GsmCellLocation
import androidx.core.content.ContextCompat
import com.newrelic.agent.android.NewRelic
import com.truedigital.core.extensions.decToHex
import com.truedigital.core.extensions.hexToDec
import com.truedigital.core.provider.ActivityProvider
import com.truedigital.core.provider.ContextDataProvider
import org.json.JSONObject
import javax.inject.Inject

@Deprecated("Please change UseCase to Repository, and refactor code")
interface NetworkInfoUseCase {
    fun getNetworkInfo(): List<String>
    fun getCarrier(): String
    fun getLTE(): CellInfoLte?
    fun getGMS(): JSONObject?
}

class NetworkInfoUseCaseImpl @Inject constructor(
    private val contextDataProvider: ContextDataProvider,
    private val telephonyManager: TelephonyManager
) : NetworkInfoUseCase {

    companion object {
        /**
         * These params will use for first array network info
         * */
        private const val IS_REGISTERED = "mRegistered"
        private const val TIME_STAMP_TYPE = "mTimeStampType"
        private const val TIME_STAMP = "mTimeStamp"
        private const val MOBILE_COUNTRY_CODE = "mMcc"
        private const val MOBILE_NETWORK_CODE = "mMnc"

        /**
         * These params will use for second array network info
         * */
        private const val CELL_IDENTITY = "mCi"
        private const val CELL_PHYSICAL_ID = "mPci"
        private const val TRACKING_AREA_CODE = "mTac"
        private const val CELL_SIGNAL_STRENGTH = "ss"
        private const val REFERENCE_SIGNAL_RECEIVED_POWER = "rsrp"
        private const val REFERENCE_SIGNAL_RECEIVED_QUALITY = "rsrq"
        private const val REFERENCE_SIGNAL_RECEIVED_SIGNAL_TO_NOISE_RATIO = "rssnr"
        private const val CHANNEL_QUALITY_INDICATOR = "cpi"
        private const val TIMING_ADVANCE = "ta"

        /**
         * These params will use for third array network info
         * */
        private const val GSM_LOCATION_AREA_CODE = "LAC"
        private const val GSM_CELL_ID = "Cid"
        private const val GSM_PRIMARY_SCRAMBLING_CODE = "Psc"
        private const val GSM_NODE_B_ID = "eNodeB_ID"
    }

    @SuppressLint("MissingPermission")
    override fun getNetworkInfo(): List<String> {
        val outputList = mutableListOf<String>()
        val cellInfoLte = getLTE()
        cellInfoLte?.let { cellInfoLte ->
            val cellInfoToArray = cellInfoLte.toString().split(" ")
            val jsonOutputNetworkCellular1 = JSONObject().apply {
                put(
                    IS_REGISTERED,
                    if (cellInfoLte.isRegistered) {
                        "YES"
                    } else {
                        "NO"
                    }
                )
                put(
                    TIME_STAMP_TYPE,
                    if (cellInfoToArray.size >= 2) {
                        if (cellInfoToArray[1].contains("mTimeStampType=")) {
                            cellInfoToArray[1].replace("mTimeStampType=", "")
                        } else {
                            ""
                        }
                    } else {
                        ""
                    }
                )
                put(TIME_STAMP, cellInfoLte.timeStamp)
                put(MOBILE_COUNTRY_CODE, cellInfoLte.cellIdentity.mcc)
                put(MOBILE_NETWORK_CODE, cellInfoLte.cellIdentity.mnc)
            }

            outputList.add(0, jsonOutputNetworkCellular1.toString())

            val jsonOutputNetworkCellular2 = JSONObject().apply {
                put(CELL_IDENTITY, cellInfoLte.cellIdentity.ci)
                put(CELL_PHYSICAL_ID, cellInfoLte.cellIdentity.pci)
                put(TRACKING_AREA_CODE, cellInfoLte.cellIdentity.tac)
                val cellSignalStrengthToArray = cellInfoLte.cellSignalStrength.toString().split(" ")
                put(
                    CELL_SIGNAL_STRENGTH,
                    if (cellSignalStrengthToArray.size >= 2) {
                        if (cellSignalStrengthToArray[1].contains("ss=")) {
                            cellSignalStrengthToArray[1].replace("ss=", "")
                        } else {
                            ""
                        }
                    } else {
                        ""
                    }
                )
                put(REFERENCE_SIGNAL_RECEIVED_POWER, cellInfoLte.cellSignalStrength.rsrp)
                put(
                    REFERENCE_SIGNAL_RECEIVED_QUALITY,
                    cellInfoLte.cellSignalStrength.rsrq
                )
                put(
                    REFERENCE_SIGNAL_RECEIVED_SIGNAL_TO_NOISE_RATIO,
                    cellInfoLte.cellSignalStrength.rssnr
                )
            }

            outputList.add(1, jsonOutputNetworkCellular2.toString())

            val jsonOutputNetworkCellular3 = JSONObject().apply {
                put(CHANNEL_QUALITY_INDICATOR, cellInfoLte.cellSignalStrength.cqi)
                put(TIMING_ADVANCE, cellInfoLte.cellSignalStrength.timingAdvance)
            }

            val jsonGSM = getGMS()
            jsonGSM?.let { jsonObject ->
                jsonOutputNetworkCellular3.apply {
                    put(GSM_LOCATION_AREA_CODE, jsonObject.get(GSM_LOCATION_AREA_CODE))
                    put(GSM_CELL_ID, jsonObject.get(GSM_CELL_ID))
                    put(GSM_PRIMARY_SCRAMBLING_CODE, jsonObject.get(GSM_PRIMARY_SCRAMBLING_CODE))
                    put(GSM_NODE_B_ID, jsonObject.get(GSM_NODE_B_ID))
                }
            }

            outputList.add(2, jsonOutputNetworkCellular3.toString())
        }

        return outputList
    }

    @SuppressLint("MissingPermission")
    override fun getLTE(): CellInfoLte? {
        if (ContextCompat.checkSelfPermission(
                contextDataProvider.getDataContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            try {
                val cellInfoList: List<CellInfo>? = telephonyManager.allCellInfo

                cellInfoList?.let {
                    for (cellInfo in it) {
                        if (cellInfo is CellInfoLte) {
                            return cellInfo
                        }
                    }
                }
            } catch (exception: Exception) {

                val handlingExceptionMap = mapOf(
                    "Key" to "NetworkInfoUseCase",
                    "Value" to "Try to getLTE() but has unexpected exception is ${exception.localizedMessage}"
                )

                NewRelic.recordHandledException(exception, handlingExceptionMap)
            }
        } else {

            val currentActivity = ActivityProvider.currentActivity
            currentActivity?.let {

                val name = it.javaClass.canonicalName

                val handlingExceptionMap = mapOf(
                    "Key" to "NetworkInfoUseCase",
                    "Value" to "Try to getLTE() but permission denied in current activity is $name"
                )

                NewRelic.recordHandledException(Exception("$name"), handlingExceptionMap)
            }
        }

        return null
    }

    @SuppressLint("MissingPermission")
    override fun getGMS(): JSONObject? {
        if (ContextCompat.checkSelfPermission(
                contextDataProvider.getDataContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            if (telephonyManager.phoneType == TelephonyManager.PHONE_TYPE_GSM) {
                val location = telephonyManager.cellLocation as? GsmCellLocation
                if (location != null) {
                    val cellIdHex = location.cid.decToHex()
                    val subIndex = if (cellIdHex.length > 2) {
                        2
                    } else {
                        0
                    }
                    val eNBHex = cellIdHex.substring(0, cellIdHex.length - subIndex)
                    val eNB = eNBHex.hexToDec()
                    return JSONObject().apply {
                        put(GSM_LOCATION_AREA_CODE, location.lac)
                        put(GSM_CELL_ID, location.cid)
                        put(GSM_PRIMARY_SCRAMBLING_CODE, location.psc)
                        put(GSM_NODE_B_ID, eNB)
                    }
                }
            }
        }
        return null
    }

    override fun getCarrier(): String {
        return telephonyManager.networkOperatorName ?: ""
    }
}
