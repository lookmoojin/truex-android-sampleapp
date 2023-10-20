package com.truedigital.features.truecloudv3.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.databinding.FragmentTrueCloudv3SettingBinding
import com.truedigital.common.share.componentv3.extension.getSavedStateHandle
import com.truedigital.component.base.BaseFragment
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_DATA_USAGE
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_MIGRATE_STATUS
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_STORAGE_DETAIL
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey
import com.truedigital.features.truecloudv3.domain.model.DataUsageModel
import com.truedigital.features.truecloudv3.injections.TrueCloudV3Component
import com.truedigital.features.truecloudv3.presentation.viewmodel.SettingTrueCloudV3ViewModel
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject

class SettingTrueCloudV3Fragment : BaseFragment(R.layout.fragment_true_cloudv3_setting) {

    private val binding by viewBinding(FragmentTrueCloudv3SettingBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: SettingTrueCloudV3ViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        TrueCloudV3Component.getInstance().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.onViewCreated()
        initView()
        initListener()
        observeViewModel()
        arguments?.apply {
            getString(KEY_BUNDLE_TRUE_CLOUD_MIGRATE_STATUS, "")?.let {
                viewModel.setMigrateStatus(it)
            }
            getString(KEY_BUNDLE_TRUE_CLOUD_STORAGE_DETAIL, "")?.let {
                viewModel.setStorageDetail(it)
            }
            getParcelable<DataUsageModel>(KEY_BUNDLE_TRUE_CLOUD_DATA_USAGE)?.let {
                viewModel.setDataUsageDetail(it)
            }
        }
    }

    private fun initView() = with(binding) {
        trueCloudBackImageView.onClick {
            activity?.onBackPressed()
        }

        autoBackupContainerLayout.onClick {
            viewModel.autoBackupClick()
        }
    }

    private fun observeViewModel() {
        viewModel.onSetStorage.observe(viewLifecycleOwner) { storageDetail ->
            binding.trueCloudStorageDetailTextView.setText(storageDetail)
        }
        viewModel.onShowMigratingStatus.observe(viewLifecycleOwner) {
            binding.containerMigrateLayout.visible()
            binding.trueCloudMigrateTextView.background = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.truecloudv3_round_gray
            )
        }
        viewModel.onShowMigratePendingStatus.observe(viewLifecycleOwner) {
            binding.containerMigrateLayout.visible()
            binding.trueCloudMigrateTextView.onClick {
                viewModel.migrateClicked()
            }
        }
        viewModel.onHideMigrationStatus.observe(viewLifecycleOwner) {
            binding.containerMigrateLayout.gone()
        }
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
