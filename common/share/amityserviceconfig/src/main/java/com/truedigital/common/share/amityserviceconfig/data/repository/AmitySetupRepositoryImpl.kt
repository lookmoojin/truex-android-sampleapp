package com.truedigital.common.share.amityserviceconfig.data.repository

import com.amity.socialcloud.sdk.AmityCoreClient
import com.amity.socialcloud.sdk.video.AmityStreamBroadcasterClient
import com.amity.socialcloud.sdk.video.AmityStreamPlayerClient
import com.amity.socialcloud.uikit.social.AmitySocialUISettings
import com.truedigital.common.share.amityserviceconfig.BuildConfig.ENV_TYPE
import com.truedigital.common.share.amityserviceconfig.domain.repository.AmitySetupRepository
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.extensions.launchSafe
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import javax.inject.Inject

class AmitySetupRepositoryImpl @Inject constructor(
    private val coroutineDispatcher: CoroutineDispatcherProvider,
    private val amitySocialUISettings: AmitySocialUISettings
) : AmitySetupRepository {

    companion object {
        private const val TIME_DELAY = 3000L
    }

    override fun amitySetupApiKey(
        amityKeyByCountry: String
    ) {
        // work around wait amity fix it
        CoroutineScope(coroutineDispatcher.io()).launchSafe {
            delay(TIME_DELAY)
            amitySocialUISettings.setup(amityKeyByCountry, ENV_TYPE)
        }

        AmityStreamBroadcasterClient.setup(AmityCoreClient.getConfiguration())
        AmityStreamPlayerClient.setup(AmityCoreClient.getConfiguration())
    }
}
