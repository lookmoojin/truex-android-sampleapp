package com.truedigital.features.truecloudv3.provider

import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.core.constant.DateFormatConstant
import com.truedigital.core.utils.DataStoreInterface
import com.truedigital.features.truecloudv3.data.api.TrueCloudV3Interface
import com.truedigital.features.truecloudv3.data.model.SecureTokenServiceDataResponse
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import org.jetbrains.annotations.VisibleForTesting
import org.json.JSONObject
import timber.log.Timber
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject

interface SecureTokenServiceProvider {
    fun getSTS(): Flow<SecureTokenServiceDataResponse>
}

class SecureTokenServiceProviderImpl @Inject constructor(
    private val trueCloudV3Interface: TrueCloudV3Interface,
    private val userRepository: UserRepository,
    private val dataStoreInterface: DataStoreInterface,
    private val gsonProvider: GsonProvider
) : SecureTokenServiceProvider {

    companion object {
        private const val KEY_TURE_CLOUD_V3_STS = "KEY_TURE_CLOUD_V3_STS"
        private const val ERROR_GET_STS = "get sts error"
        private const val DELAY_TIME_MILLI = 500L
    }

    @VisibleForTesting
    var loading = false

    override fun getSTS(): Flow<SecureTokenServiceDataResponse> {
        return flow {
            val stsdata = getSTSDataStore()
            if (stsdata == null || datetimeIsPass(stsdata.expiresAt)) {
                emit(getSTSNetworkFlow().first())
            } else {
                emit(stsdata)
            }
        }
    }

    private suspend fun getSTSDataStore(): SecureTokenServiceDataResponse? {
        val rawData =
            dataStoreInterface.getSinglePreference(
                stringPreferencesKey(KEY_TURE_CLOUD_V3_STS),
                ""
            )
        return if (rawData.isEmpty()) {
            null
        } else {
            gsonProvider.getDataClass(rawData, SecureTokenServiceDataResponse::class.java)
        }
    }

    private fun datetimeIsPass(dateTime: String?): Boolean {
        var response = true
        dateTime?.let {
            try {
                val simpleDateFormat =
                    SimpleDateFormat(
                        DateFormatConstant.yyyy_MM_dd_T_HH_mm_ss_Z,
                        Locale.getDefault()
                    )
                val timeZone = TimeZone.getDefault()
                simpleDateFormat.timeZone = TimeZone.getTimeZone(timeZone.displayName)

                val nowDate = Calendar.getInstance().time
                val endDate = simpleDateFormat.parse(dateTime)

                response = nowDate.time > endDate.time
            } catch (e: ParseException) {
                Timber.e(e)
                response = true
            }
        }
        return response
    }

    @VisibleForTesting
    suspend fun updateSTS(stsData: SecureTokenServiceDataResponse) {
        dataStoreInterface.apply {
            putPreference(
                stringPreferencesKey(KEY_TURE_CLOUD_V3_STS),
                Gson().toJson(stsData)
            )
        }
    }

    @VisibleForTesting
    fun getSTSNetworkFlow(): Flow<SecureTokenServiceDataResponse> {
        if (!loading) {
            loading = true
            return flow {
                trueCloudV3Interface.getSecureTokenService(
                    ssoid = userRepository.getSsoId(),
                    obj = JSONObject()
                ).run {
                    val responseBody = body()
                    loading = false
                    if (isSuccessful && responseBody != null && responseBody.data != null) {
                        val stsResponse = responseBody.data
                        updateSTS(stsResponse)
                        emit(stsResponse)
                    } else {
                        error(ERROR_GET_STS)
                    }
                }
            }
        } else {
            return flow {
                var stsdata = getSTSDataStore()
                while (stsdata == null || datetimeIsPass(stsdata.expiresAt)) {
                    delay(DELAY_TIME_MILLI)
                    stsdata = getSTSDataStore()
                }
                emit(stsdata)
            }
        }
    }
}
