package com.truedigital.features.tuned.data.device.repository

import android.content.Context
import android.os.Build
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.truedigital.core.extensions.telephonyManager
import com.truedigital.core.utils.networkconnection.ConnectivityStateHolder
import com.truedigital.features.tuned.BuildConfig
import com.truedigital.features.tuned.common.xmlEncode
import com.truedigital.features.tuned.data.ObfuscatedKeyValueStoreInterface
import com.truedigital.features.tuned.data.device.model.Device
import com.truedigital.features.tuned.data.get
import com.truedigital.features.tuned.data.put
import com.truedigital.features.tuned.injection.module.SharePreferenceModule
import com.truedigital.foundation.extension.getLocal
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.net.URLEncoder
import java.util.Date
import java.util.GregorianCalendar
import java.util.UUID
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

class DeviceRepositoryImpl @Inject constructor(
    private val context: Context,
    @Named(SharePreferenceModule.KVS_DEVICE) private val sharedPreferences: ObfuscatedKeyValueStoreInterface
) : DeviceRepository {
    companion object {
        const val TOKEN_KEY = "token"
        const val UNIQUE_ID_KEY = "unique_id"
        const val REFERRER_KEY = "referrer"
        const val LIKES_COUNT_KEY = "likes_count"
        const val ARTIST_HINT_STATUS_KEY = "artist_hint_status"

        private const val DEVICE_OS = "Android"
    }

    private var uniqueId: String
    private var token: String

    init {
        val storedUniqueId: String? = sharedPreferences.get(UNIQUE_ID_KEY)
        if (storedUniqueId == null) {
            uniqueId = UUID.randomUUID().toString()
            sharedPreferences.put(UNIQUE_ID_KEY, uniqueId)
        } else {
            uniqueId = storedUniqueId
        }

        val storedToken: String? = sharedPreferences.get(TOKEN_KEY)
        if (storedToken == null) {
            token = UUID.randomUUID().toString()
            sharedPreferences.put(TOKEN_KEY, token)
        } else {
            token = storedToken
        }
    }

    override fun getUniqueId() = uniqueId

    override fun getToken() = token

    override fun get(): Device {
        val model = URLEncoder.encode(Build.MODEL, "utf-8")
        val timezone = GregorianCalendar().timeZone
        val daylightSavingsAdjustment =
            if (timezone.inDaylightTime(Date())) timezone.dstSavings else 0
        val timezoneOffset = (timezone.rawOffset + daylightSavingsAdjustment).toLong()
        val timezoneOffsetHours =
            TimeUnit.HOURS.convert(timezoneOffset, TimeUnit.MILLISECONDS).toInt()

        return Device(
            model,
            uniqueId,
            token,
            model.xmlEncode(),
            DEVICE_OS,
            Build.VERSION.RELEASE,
            BuildConfig.VERSION_NAME,
            context.resources.getLocal().country,
            context.resources.getLocal().language,
            Build.MANUFACTURER,
            timezoneOffsetHours,
            context.telephonyManager().networkOperatorName,
            Build.BRAND,
            sharedPreferences.get(REFERRER_KEY)
        )
    }

    override fun isNetworkConnected(): Boolean {
        return ConnectivityStateHolder.isConnected
    }

    override fun isWifiConnected(): Boolean {
        ConnectivityStateHolder.networkStats.find {
            it.isWifi
        }?.let {
            return it.isAvailable
        }
        return false
    }

    override fun isEmulator(): Boolean {
        return Build.FINGERPRINT.startsWith("generic") ||
            Build.FINGERPRINT.startsWith("unknown") ||
            Build.MODEL.contains("google_sdk") ||
            Build.MODEL.contains("Emulator") ||
            Build.MODEL.contains("Android SDK built for x86") ||
            Build.MANUFACTURER.contains("Genymotion") ||
            (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")) ||
            "google_sdk" == Build.PRODUCT
    }

    override fun getLikesCount(): Int {
        val likesCount = sharedPreferences.get(LIKES_COUNT_KEY, 0)
        sharedPreferences.put(LIKES_COUNT_KEY, likesCount + 1)
        return likesCount + 1
    }

    override fun isArtistHintShown(): Boolean =
        sharedPreferences.get(ARTIST_HINT_STATUS_KEY, false)

    override fun setArtistHintStatus(isShown: Boolean) {
        sharedPreferences.put(ARTIST_HINT_STATUS_KEY, isShown)
    }

    override fun getLSID(): Single<String> =
        Single.fromCallable { AdvertisingIdClient.getAdvertisingIdInfo(context) }
            .subscribeOn(Schedulers.io())
            .map {
                if (it.id.isNullOrEmpty() || it.isLimitAdTrackingEnabled) "app:$uniqueId"
                else "gaid:${it.id}"
            }
            .onErrorReturn {
                "app:$uniqueId"
            }
}
