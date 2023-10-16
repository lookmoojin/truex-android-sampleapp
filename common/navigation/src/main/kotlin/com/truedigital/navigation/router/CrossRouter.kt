package com.truedigital.navigation.router

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.core.net.toUri
import androidx.navigation.NavOptions
import com.truedigital.common.share.data.coredata.deeplink.DeepLinkStateLiveData
import com.truedigital.common.share.data.coredata.deeplink.SwitchBottomTabLiveData
import com.truedigital.common.share.data.coredata.deeplink.constants.DeepLinkContentNotFound
import com.truedigital.common.share.data.coredata.deeplink.constants.DeepLinkDefaultError
import com.truedigital.common.share.data.coredata.deeplink.constants.DeepLinkFeatureOff
import com.truedigital.common.share.data.coredata.deeplink.constants.DeepLinkSuccess
import com.truedigital.common.share.data.coredata.deeplink.usecase.DecodeShortLinkUseCase
import com.truedigital.core.coroutines.CoroutineDispatcherProvider
import com.truedigital.core.extensions.launchSafe
import com.truedigital.core.extensions.navigateSafe
import com.truedigital.navigation.deeplink.NavigateHostDeeplinkUseCase
import com.truedigital.navigation.deeplink.TrackFirebaseAnalyticsDeeplinkUseCase
import com.truedigital.navigation.usecase.GetNavigationControllerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

interface CrossRouter {
    var activity: Activity?
    fun routeWithDeeplink(stringUrl: String, navOptions: NavOptions? = null)
}

class CrossRouterImpl @Inject constructor(
    private val context: Context,
    private val decodeShortLinkUseCase: DecodeShortLinkUseCase,
    private val navigateHostDeeplinkUseCase: NavigateHostDeeplinkUseCase,
    private val trackFirebaseAnalyticsDeeplinkUseCase: TrackFirebaseAnalyticsDeeplinkUseCase,
    private val getNavigationControllerRepository: GetNavigationControllerRepository,
    private val coroutineDispatcher: CoroutineDispatcherProvider
) : CrossRouter {

    private var deeplinkURL = ""
    override var activity: Activity? = null

    override fun routeWithDeeplink(stringUrl: String, navOptions: NavOptions?) {

        val coroutineScope: CoroutineScope = object : CoroutineScope {
            override val coroutineContext: CoroutineContext =
                coroutineDispatcher.io() // no job added i.e + SupervisorJob()
        }

        coroutineScope.launchSafe {
            decodeShortLinkUseCase.execute(stringUrl, context)
                .map { deeplink ->
                    deeplinkURL = deeplink
                    navigateHostDeeplinkUseCase.execute(deeplink)
                }
                .flowOn(coroutineDispatcher.io())
                .collect { (deepLinkResult, _) ->
                    trackFirebaseAnalyticsDeeplinkUseCase.execute(true, deeplinkURL)

                    when (deepLinkResult) {
                        is DeepLinkSuccess -> {
                            if (deepLinkResult.tab.isNotEmpty()) {
                                SwitchBottomTabLiveData.switchBottomTabFromDeeplink(
                                    Pair(deepLinkResult.url, deepLinkResult.tab)
                                )
                            } else {
                                if (deepLinkResult.url.isNotBlank()) {
                                    Handler(Looper.getMainLooper()).post {
                                        getNavigationControllerRepository.getCrossRouterNavController()
                                            ?.let {
                                                it.navigateSafe(
                                                    deepLinkResult.url.toUri(),
                                                    navOptions
                                                )
                                                DeepLinkStateLiveData.isActive = true
                                            }
                                    }
                                }
                            }
                        }
                        DeepLinkContentNotFound -> {
                            DeepLinkStateLiveData.showContentNotFound()
                            trackFirebaseAnalyticsDeeplinkUseCase.execute(false, deeplinkURL)
                        }
                        DeepLinkFeatureOff -> {
                            DeepLinkStateLiveData.showFeatureOff()
                            trackFirebaseAnalyticsDeeplinkUseCase.execute(false, deeplinkURL)
                        }
                        DeepLinkDefaultError -> {
                            DeepLinkStateLiveData.showDefaultError()
                            trackFirebaseAnalyticsDeeplinkUseCase.execute(false, deeplinkURL)
                        }
                        else -> {
                            // do nothing
                        }
                    }
                }
        }
    }
}
