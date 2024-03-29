package com.truedigital.common.share.analytics

import android.content.Context
import androidx.startup.Initializer
import com.truedigital.common.share.analytics.injections.AnalyticsComponent
import com.truedigital.common.share.analytics.injections.DaggerAnalyticsComponent
import com.truedigital.common.share.analytics.measurement.AnalyticManager
import com.truedigital.common.share.datalegacy.DataLegacyInitializer
import com.truedigital.common.share.datalegacy.data.repository.profile.UserRepository
import com.truedigital.common.share.datalegacy.domain.other.usecase.EncryptLocationUseCaseImpl
import com.truedigital.common.share.datalegacy.domain.other.usecase.NetworkInfoUseCase
import com.truedigital.common.share.datalegacy.domain.other.usecase.NetworkInfoWrapperUseCase
import com.truedigital.common.share.datalegacy.domain.other.usecase.WifiInfoUseCase
import com.truedigital.common.share.datalegacy.helpers.FirebaseAnalyticsHelper
import com.truedigital.common.share.datalegacy.injections.DataLegacyComponent
import com.truedigital.core.CoreInitializer
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.extensions.launchSafe
import com.truedigital.core.injections.CoreComponent
import com.truedigital.core.manager.location.LocationManagerImpl
import com.truedigital.core.utils.EncryptUtilImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class AnalyticsInitializer : Initializer<AnalyticsComponent> {

    companion object {
        private const val APP_NAME = "TID"
        private const val FUNCTION_NAME = "initFirebaseAnalyticModel"
    }

    private val scope = CoroutineScope(Job() + Dispatchers.IO)

    override fun create(context: Context): AnalyticsComponent {
        return DaggerAnalyticsComponent.factory().create(
            CoreComponent.getInstance().getCoreSubComponent(),
            DataLegacyComponent.getInstance().getDataLegacySubComponent(),
        ).apply {
            AnalyticsComponent.initialize(this)
        }.also {

            CoroutineScope(Dispatchers.IO).launchSafe {
                val dataLegacyComponent = DataLegacyComponent.getInstance().getDataLegacySubComponent()

                val networkInfoWrapperUseCase: NetworkInfoWrapperUseCase =
                    dataLegacyComponent.getNetworkInfoWrapperUseCase()
                val wifiInfoUseCase: WifiInfoUseCase = dataLegacyComponent.getWifiInfoUseCase()
                val networkInfoUseCase: NetworkInfoUseCase = dataLegacyComponent.getNetworkInfoUseCase()
                val userRepository: UserRepository = dataLegacyComponent.getUserRepository()

                val firebaseAnalyticsHelper: FirebaseAnalyticsHelper =
                    dataLegacyComponent.getFirebaseAnalyticsHelper()

                val coroutineDispatcher: CoroutineDispatcherProvider = CoreComponent.getInstance()
                    .getCoreSubComponent().getCoroutineDispatcherProvider()

                val analyticManager: AnalyticManager = AnalyticsComponent.getInstance()
                    .getAnalyticsSubComponent().getAnalyticManager()

                initFirebaseAnalyticsHelper(
                    context,
                    firebaseAnalyticsHelper,
                    userRepository,
                    networkInfoUseCase,
                    wifiInfoUseCase
                )
            }
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = listOf(
        CoreInitializer::class.java,
        DataLegacyInitializer::class.java,
    )

    private fun initFirebaseAnalyticsHelper(
        context: Context,
        firebaseAnalyticsHelper: FirebaseAnalyticsHelper,
        userRepository: UserRepository,
        networkInfoUseCase: NetworkInfoUseCase,
        wifiInfoUseCase: WifiInfoUseCase
    ) {
        firebaseAnalyticsHelper.initializeFirebaseAnalyticsHelper(context, userRepository)
        firebaseAnalyticsHelper.setEncryptLocationUseCase(
            EncryptLocationUseCaseImpl(
                LocationManagerImpl.instance,
                EncryptUtilImpl()
            )
        )
        firebaseAnalyticsHelper.setNetworkInfoUseCase(networkInfoUseCase)
        firebaseAnalyticsHelper.setWifiInfoUseCase(wifiInfoUseCase)
    }
}
