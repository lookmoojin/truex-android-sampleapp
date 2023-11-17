package com.truedigital.features.truecloudv3.presentation

import android.os.Bundle
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.truedigital.component.base.BaseFragment
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.injections.TrueCloudV3Component
import com.truedigital.features.truecloudv3.presentation.viewmodel.IntroTrueCloudSharedFileViewModel
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject

class IntroTrueCloudSharedFileFragment :
    BaseFragment(R.layout.fragment_true_cloudv3_intro_shared_file) {

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: IntroTrueCloudSharedFileViewModel by viewModels { viewModelFactory }
    private val args: IntroTrueCloudSharedFileFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        TrueCloudV3Component.getInstance().inject(this)
        super.onCreate(savedInstanceState)
        viewModel.setRouterToNavController(findNavController())
        viewModel.navigateToSharedFileViewer(args.encryptedSharedObjectId)
    }
}
