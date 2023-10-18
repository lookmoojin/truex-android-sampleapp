package com.truedigital.navigation.domain.usecase

import android.app.Activity
import com.truedigital.navigation.usecase.GetNavigationControllerRepository
import javax.inject.Inject

interface SetCrossRouterNavControllerUseCase {
    fun execute(activity: Activity)
}

class SetCrossRouterNavControllerUseCaseImpl @Inject constructor(
    private val getNavigationControllerRepository: GetNavigationControllerRepository
) : SetCrossRouterNavControllerUseCase {
    override fun execute(activity: Activity) {
        getNavigationControllerRepository.setCrossRouterNavController(activity)
    }
}
