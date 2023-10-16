package com.truedigital.navigation.usecase

import android.app.Activity
import android.content.Context
import androidx.navigation.NavController
import com.truedigital.component.base.core.CoreActivity
import javax.inject.Inject

interface GetNavigationControllerUseCase {
    fun execute(): NavController?
}

class GetNavigationControllerUseCaseImpl @Inject constructor(
    private val context: Context
) : GetNavigationControllerUseCase {

    override fun execute(): NavController? {
        return if (context is Activity) {
            (context as? CoreActivity)?.getNavController()
        } else {
            null
        }
    }
}
