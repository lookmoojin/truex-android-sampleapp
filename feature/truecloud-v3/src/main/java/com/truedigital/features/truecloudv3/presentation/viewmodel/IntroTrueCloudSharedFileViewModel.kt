package com.truedigital.features.truecloudv3.presentation.viewmodel

import android.os.Bundle
import androidx.navigation.NavController
import com.truedigital.core.base.ScopedViewModel
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_SHARED_FILE_ID
import com.truedigital.features.truecloudv3.navigation.IntroSharedToSharedViewer
import com.truedigital.features.truecloudv3.navigation.router.IntroTrueCloudRouterUseCase
import com.truedigital.navigation.domain.usecase.SetRouterToNavControllerUseCase
import javax.inject.Inject

class IntroTrueCloudSharedFileViewModel @Inject constructor(
    private val router: IntroTrueCloudRouterUseCase,
    private val setRouterToNavControllerUseCase: SetRouterToNavControllerUseCase
) : ScopedViewModel() {

    fun navigateToSharedFileViewer(encryptedSharedObjectId: String) {
        val bundle = Bundle().apply {
            putString(KEY_BUNDLE_TRUE_CLOUD_SHARED_FILE_ID, encryptedSharedObjectId)
        }
        router.execute(IntroSharedToSharedViewer, bundle)
    }

    fun setRouterToNavController(navController: NavController) {
        setRouterToNavControllerUseCase.execute(navController)
    }
}
