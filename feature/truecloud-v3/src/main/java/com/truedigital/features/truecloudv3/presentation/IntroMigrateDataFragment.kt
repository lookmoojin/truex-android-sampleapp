package com.truedigital.features.truecloudv3.presentation

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.databinding.FragmentIntroMigrateDataBinding
import com.truedigital.common.share.componentv3.extension.getSavedStateHandle
import com.truedigital.common.share.componentv3.extension.setSavedStateHandle
import com.truedigital.component.base.BaseFragment
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey
import com.truedigital.features.truecloudv3.injections.TrueCloudV3Component
import com.truedigital.features.truecloudv3.presentation.viewmodel.IntroMigrateDataViewModel
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject

class IntroMigrateDataFragment : BaseFragment(R.layout.fragment_intro_migrate_data) {

    private val binding by viewBinding(FragmentIntroMigrateDataBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: IntroMigrateDataViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        TrueCloudV3Component.getInstance().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initBackButton()
        initListener()
        observeViewModel()
    }

    private fun initViews() = with(binding) {
        trueCloudLaterTextView.onClick {
            viewModel.onClickLater()
        }
        trueCloudMigrateTextView.onClick {
            viewModel.onClickMigrate()
        }
    }

    private fun observeViewModel() {
        viewModel.onLaterClicked.observe(viewLifecycleOwner) {
            findNavController().setSavedStateHandle(
                TrueCloudV3SaveStateKey.KEY_FROM_INIT_MIGRATION_PAGE,
                true
            )
            findNavController().navigateUp() || fragmentPopBackStack()
        }
    }

    private fun initBackButton() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.onClickLater()
                }
            }
        )
    }

    fun initListener() {
        findNavController()
            .getSavedStateHandle<Boolean>(TrueCloudV3SaveStateKey.KEY_FROM_MIGRATION_PAGE)?.observe(
                viewLifecycleOwner
            ) {
                findNavController().navigateUp() || fragmentPopBackStack()
            }
    }

    private fun fragmentPopBackStack(): Boolean {
        return (parentFragment as NavHostFragment)
            .findNavController()
            .popBackStack()
    }
}
