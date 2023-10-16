package com.truedigital.navigation.domain.usecase

import androidx.navigation.NavController
import com.truedigital.navigation.usecase.GetNavigationControllerRepository
import javax.inject.Inject

interface SetRouterSecondaryToNavControllerUseCase {
    fun execute(navController: NavController)
}

class SetRouterSecondaryToNavControllerUseCaseImpl @Inject constructor(
    private val getNavigationControllerRepository: GetNavigationControllerRepository,
) : SetRouterSecondaryToNavControllerUseCase {
    override fun execute(navController: NavController) {
        getNavigationControllerRepository.setRouterSecondaryToNavController(navController)
    }
}
