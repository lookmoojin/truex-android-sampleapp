package com.truedigital.features.music.domain.usecase.router

import android.content.Context
import android.net.Uri
import android.os.Bundle
import androidx.navigation.NavOptions
import com.truedigital.common.share.webview.externalbrowser.utils.customtabs.CustomTabsHelper
import com.truedigital.core.extensions.navigateSafe
import com.truedigital.features.music.navigation.config.ConfigMusicNavigate
import com.truedigital.features.music.navigation.router.MusicLandingToExternalBrowser
import com.truedigital.navigation.data.repository.router.NavigationRouterRepository
import com.truedigital.navigation.router.Destination
import com.truedigital.navigation.usecase.GetNavigationControllerRepository
import javax.inject.Inject

interface MusicRouterUseCase {
    fun execute(destination: Destination, bundle: Bundle? = null)
    fun execute(stringUrl: String, navOptions: NavOptions? = null)
    fun getLastDestination(): Destination?
}

class MusicRouterUseCaseImpl @Inject constructor(
    private val context: Context,
    private val customTabsHelper: CustomTabsHelper,
    private val navigationRouterRepository: NavigationRouterRepository,
    private val getNavigationControllerRepository: GetNavigationControllerRepository,
) : MusicRouterUseCase {

    companion object {
        const val EXTRA_EXTERNAL_BROWSER = "extra_external_browser"
    }

    override fun execute(destination: Destination, bundle: Bundle?) {
        navigationRouterRepository.routeTo(destination, bundle)

        when (destination) {
            MusicLandingToExternalBrowser -> {
                customTabsHelper.openCustomTab(
                    context,
                    Uri.parse(bundle?.getString(EXTRA_EXTERNAL_BROWSER)),
                )
            }
            else -> {
                ConfigMusicNavigate[destination]?.let { _navigationId ->
                    getNavigationControllerRepository.getRouterSecondaryToNavController()
                        ?.navigateSafe(_navigationId, bundle)
                }
            }
        }
    }

    override fun execute(stringUrl: String, navOptions: NavOptions?) {
        navigationRouterRepository.routeWithDeeplink(stringUrl, navOptions)
    }

    override fun getLastDestination(): Destination? {
        return navigationRouterRepository.getLastDestination()
    }
}
