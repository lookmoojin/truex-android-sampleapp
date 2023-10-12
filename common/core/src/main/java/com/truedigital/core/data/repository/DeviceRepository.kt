package com.truedigital.core.data.repository

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import com.truedigital.core.BuildConfig
import com.truedigital.core.R
import com.truedigital.core.provider.ContextDataProvider
import java.util.UUID
import javax.inject.Inject

/***************************************************************************************************
 *
 * The `DeviceRepository` class represents the device's information
 * such as
 * - AndroidId
 * - AppVersion
 * - DeviceName
 * - NetworkOperatorName
 * - OsVersion
 * - SimOperatorName
 *
 * Next Development plan:
 * - Remove systemUtil: [com.tdcm.trueidapp.features.util.SystemUtil.SystemInterface] as dependency
 *
 **************************************************************************************************/
interface DeviceRepository {
    fun getAndroidId(): String
    fun getAppVersion(): String
    fun getAppVersionWithVersionCode(): String
    fun getDeviceName(): String
    fun getEnvironment(): String
    fun getScreenSize(): String
    fun getModelName(): String
    fun getOSVersion(): String
    fun getSimOperatorName(): String
    fun getUserAgent(): String
}

class DeviceRepositoryImpl @Inject constructor(
    private val contextDataProvider: ContextDataProvider
) : DeviceRepository {

    @SuppressLint("HardwareIds")
    override fun getAndroidId(): String {
        return try {
            /** Logic from "auth-module" */
            val androidID = Settings.Secure.getString(
                contextDataProvider.getDataContext().contentResolver, Settings.Secure.ANDROID_ID
            )
            if (androidID.isNullOrEmpty()) {
                val buildID = "${Build.BOARD.length % 10}" +
                    "${Build.BRAND.length % 10}" +
                    "${Build.DEVICE.length % 10}" +
                    "${Build.MANUFACTURER.length % 10}" +
                    "${Build.MODEL.length % 10}" +
                    "${Build.PRODUCT.length % 10}"
                val serial = Build::class.java.getField("SERIAL").get(null).toString()
                UUID(buildID.hashCode().toLong(), serial.hashCode().toLong()).toString()
            } else {
                androidID
            }
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    override fun getAppVersion(): String {
        return BuildConfig.VERSION_NAME
    }

    override fun getAppVersionWithVersionCode(): String {
        return BuildConfig.VERSION_NAME + " (" + BuildConfig.VERSION_CODE + ")"
    }

    override fun getDeviceName(): String {
        val manufacturer = Build.MANUFACTURER
        val model = Build.MODEL
        return if (model.startsWith(manufacturer)) {
            model.capitalize()
        } else {
            "${manufacturer.capitalize()} $model"
        }
    }

    override fun getEnvironment(): String {
        return BuildConfig.ENVIRONMENT
    }

    override fun getScreenSize(): String {
        return contextDataProvider.getString(R.string.size_image_url)
    }

    override fun getModelName(): String {
        return Build.MODEL
    }

    override fun getOSVersion(): String {
        return Build.VERSION.RELEASE
    }

    override fun getSimOperatorName(): String {
        val telephonyManager =
            contextDataProvider.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return if (telephonyManager.simOperatorName.isBlank()) "UNKNOWN" else telephonyManager.simOperatorName
    }

    override fun getUserAgent(): String {
        return "(${getDeviceName()}; ${getModelName()}; SDK ${Build.VERSION.SDK_INT}; Android ${getOSVersion()})"
    }
}
