package com.truedigital.features.truecloudv3.presentation

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tdg.truecloud.R
import com.tdg.truecloud.databinding.FragmentMigrateDataBinding
import com.truedigital.common.share.componentv3.extension.setSavedStateHandle
import com.truedigital.component.base.BaseFragment
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey
import com.truedigital.features.truecloudv3.extension.snackBar
import com.truedigital.features.truecloudv3.injections.TrueCloudV3Component
import com.truedigital.features.truecloudv3.presentation.viewmodel.MigrateDataViewModel
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject

class MigrateDataFragment : BaseFragment(R.layout.fragment_migrate_data) {

    private val binding by viewBinding(FragmentMigrateDataBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: MigrateDataViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        TrueCloudV3Component.getInstance().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        initBackButton()
        observeViewModel()
        viewModel.callMigrate()
    }

    private fun initViews() = with(binding) {
        trueCloudBackImageView.onClick {
            closeMigrationPage()
        }
        trueCloudCancelTextView.onClick {
            val migrationBottomSheetCancelDialogFragment =
                CreateMigrationBottomSheetCancelDialogFragment(onMigrateCancelClick)
            migrationBottomSheetCancelDialogFragment.show(
                childFragmentManager,
                migrationBottomSheetCancelDialogFragment.tag
            )
        }
    }

    private val onMigrateCancelClick =
        object : CreateMigrationBottomSheetCancelDialogFragment.OnMigrateCancelClick {
            override fun onCancelIt() {
                viewModel.callMigrateCancel()
            }
        }

    private fun observeViewModel() {
        binding.apply {
            viewModel.onMigrating.observe(viewLifecycleOwner) {
                migratingTitleTextView.text =
                    getString(R.string.true_cloudv3_migration_data_migrating)
                migratingSubtitleTextView.text = it
                migratingSubtitleTextView.visible()
            }
            viewModel.onMigrateCanceled.observe(viewLifecycleOwner) {
                closeMigrationPage()
            }

            viewModel.onMigrateCancelFailed.observe(viewLifecycleOwner) {
                root.snackBar(
                    it,
                    R.color.true_cloudv3_color_toast_error
                )
            }
        }
    }

    private fun initBackButton() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    closeMigrationPage()
                }
            }
        )
    }

    fun closeMigrationPage() {
        findNavController().setSavedStateHandle(
            TrueCloudV3SaveStateKey.KEY_FROM_MIGRATION_PAGE,
            true
        )

        findNavController().navigateUp()
    }
}
