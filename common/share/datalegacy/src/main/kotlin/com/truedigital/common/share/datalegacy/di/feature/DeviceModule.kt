package com.truedigital.common.share.datalegacy.di.feature

import android.content.Context
import android.net.wifi.WifiManager
import android.telephony.TelephonyManager
import com.truedigital.common.share.datalegacy.domain.other.usecase.GetNetworkConnectedTypeUseCase
import com.truedigital.common.share.datalegacy.domain.other.usecase.GetNetworkConnectedTypeUseCaseImpl
import com.truedigital.common.share.datalegacy.domain.other.usecase.NetworkInfoUseCase
import com.truedigital.common.share.datalegacy.domain.other.usecase.NetworkInfoUseCaseImpl
import com.truedigital.common.share.datalegacy.domain.other.usecase.NetworkInfoWrapperUseCase
import com.truedigital.common.share.datalegacy.domain.other.usecase.NetworkInfoWrapperUseCaseImpl
import com.truedigital.common.share.datalegacy.domain.other.usecase.WifiInfoUseCase
import com.truedigital.common.share.datalegacy.domain.other.usecase.WifiInfoUseCaseImpl
import com.truedigital.core.provider.ContextDataProvider
import dagger.Module
import dagger.Provides

@Module
object DeviceModule {

    @Provides
    fun provideNetworkInfoUseCase(
        contextDataProvider: ContextDataProvider,
        telephonyManager: TelephonyManager
    ): NetworkInfoUseCase {
        return NetworkInfoUseCaseImpl(
            contextDataProvider = contextDataProvider,
            telephonyManager = telephonyManager
        )
    }

    @Provides
    fun provideNetworkInfoWrapperUseCase(networkInfoUseCase: NetworkInfoUseCase): NetworkInfoWrapperUseCase {
        return NetworkInfoWrapperUseCaseImpl(networkInfoUseCase)
    }

    @Provides
    fun provideWifiInfoUseCase(wifiManager: WifiManager): WifiInfoUseCase {
        return WifiInfoUseCaseImpl(wifiManager)
    }

    @Provides
    fun provideTelephonyManager(context: Context): TelephonyManager {
        return context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }

    @Provides
    fun provideWifiManager(context: Context): WifiManager {
        return context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    @Provides
    fun provideGetNetworkConnectedTypeUseCase(context: Context): GetNetworkConnectedTypeUseCase {
        return GetNetworkConnectedTypeUseCaseImpl(context)
    }
}
