package com.truedigital.common.share.datalegacy.domain.ads.usecase

import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.truedigital.core.provider.ContextDataProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject

interface AdvertisingIdUseCase {
    fun loadAdvertisingId(): Flow<String>
}

class AdvertisingIdUseCaseImpl @Inject constructor(val contextDataProvider: ContextDataProvider) :
    AdvertisingIdUseCase {

    companion object {
        private const val ERROR_MESSAGE = "Cannot load Advertising Id"
    }

    override fun loadAdvertisingId(): Flow<String> {
        return flow {
            if (GoogleApiAvailability.getInstance()
                .isGooglePlayServicesAvailable(contextDataProvider.getDataContext()) == ConnectionResult.SUCCESS
            ) {
                var advertisingInfo: AdvertisingIdClient.Info? = null
                var advertisingId: String? = null

                try {
                    advertisingInfo =
                        AdvertisingIdClient.getAdvertisingIdInfo(contextDataProvider.getDataContext())
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                } catch (e: GooglePlayServicesNotAvailableException) {
                    e.printStackTrace()
                } catch (e: GooglePlayServicesRepairableException) {
                    e.printStackTrace()
                }

                advertisingInfo?.let { info ->
                    advertisingId = info.id
                }

                advertisingId?.let { id ->
                    emit(id)
                } ?: run {
                    error(Throwable(ERROR_MESSAGE))
                }
            } else {
                error(Throwable(ERROR_MESSAGE))
            }
        }
    }
}
