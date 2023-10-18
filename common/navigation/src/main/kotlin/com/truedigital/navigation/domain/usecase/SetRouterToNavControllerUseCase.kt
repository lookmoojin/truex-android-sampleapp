package com.truedigital.navigation.domain.usecase

import androidx.navigation.NavController
import com.truedigital.navigation.usecase.GetNavigationControllerRepository
import javax.inject.Inject

interface SetRouterToNavControllerUseCase {
    fun execute(navController: NavController)
}

class SetRouterToNavControllerUseCaseImpl @Inject constructor(
    private val getNavigationControllerRepository: GetNavigationControllerRepository
) : SetRouterToNavControllerUseCase {
    override fun execute(navController: NavController) {
        getNavigationControllerRepository.setRouterToNavController(navController)
    }
}
