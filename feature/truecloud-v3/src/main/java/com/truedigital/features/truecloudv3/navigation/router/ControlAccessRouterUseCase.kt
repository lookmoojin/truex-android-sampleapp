package com.truedigital.features.truecloudv3.navigation.router

import android.os.Bundle
import com.truedigital.core.extensions.navigateSafe
import com.truedigital.features.truecloudv3.navigation.TrueCloudV3ConfigNavigate
import com.truedigital.navigation.data.repository.router.NavigationRouterRepository
import com.truedigital.navigation.router.Destination
import com.truedigital.navigation.usecase.GetNavigationControllerRepository
import javax.inject.Inject

interface ControlAccessRouterUseCase {
    fun execute(destination: Destination, bundle: Bundle? = null)
}

class ControlAccessRouterUseCaseImpl @Inject constructor(
    private val navigationRouterRepository: NavigationRouterRepository,
    private val getNavigationControllerRepository: GetNavigationControllerRepository,
) : ControlAccessRouterUseCase {

    override fun execute(destination: Destination, bundle: Bundle?) {
        navigationRouterRepository.routeTo(destination, bundle)

        val navigationId = TrueCloudV3ConfigNavigate[destination]
        navigationId?.let { _navigationId ->
            getNavigationControllerRepository.getRouterToNavController()
                ?.navigateSafe(_navigationId, bundle)
        }
    }
}
