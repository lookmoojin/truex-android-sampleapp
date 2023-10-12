package com.truedigital.features.truecloudv3.navigation.router

import android.os.Bundle
import com.truedigital.features.truecloudv3.navigation.TrueCloudV3ConfigNavigate
import com.truedigital.navigation.data.repository.router.NavigationRouterRepository
import com.truedigital.navigation.router.Destination
import com.truedigital.navigation.usecase.GetNavigationControllerRepository
import javax.inject.Inject

interface IntroTrueCloudRouterUseCase {
    fun execute(destination: Destination, bundle: Bundle? = null)
}

class IntroTrueCloudRouterUseCaseImpl @Inject constructor(
    private val navigationRouterRepository: NavigationRouterRepository,
    private val getNavigationControllerRepository: GetNavigationControllerRepository,
) : IntroTrueCloudRouterUseCase {

    override fun execute(destination: Destination, bundle: Bundle?) {
        navigationRouterRepository.routeTo(destination, bundle)
        TrueCloudV3ConfigNavigate[destination]?.let { _navigationId ->
            getNavigationControllerRepository.getRouterToNavController()
                ?.navigate(_navigationId, bundle)
        }
    }
}
