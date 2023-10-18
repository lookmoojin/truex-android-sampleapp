package com.truedigital.features.truecloudv3.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.tdg.truecloud.R
import com.tdg.truecloud.databinding.FragmentTrueCloudv3AutoBackupSettingBinding
import com.truedigital.common.share.componentv3.extension.getSavedStateHandle
import com.truedigital.component.base.BaseFragment
import com.truedigital.component.dialog.trueid.DialogIconType
import com.truedigital.component.dialog.trueid.DialogManager
import com.truedigital.core.extensions.viewBinding
import com.truedigital.core.manager.permission.PermissionManager
import com.truedigital.core.manager.permission.PermissionManagerImpl
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey
import com.truedigital.features.truecloudv3.domain.model.DataUsageModel
import com.truedigital.features.truecloudv3.domain.model.DetailDialogModel
import com.truedigital.features.truecloudv3.domain.usecase.NodePermission
import com.truedigital.features.truecloudv3.extension.checkStoragePermissionAlready
import com.truedigital.features.truecloudv3.extension.formatBinarySize
import com.truedigital.features.truecloudv3.extension.getStoragePermissions
import com.truedigital.features.truecloudv3.extension.snackBar
import com.truedigital.features.truecloudv3.injections.TrueCloudV3Component
import com.truedigital.features.truecloudv3.presentation.viewmodel.AutoBackupViewModel
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.presentations.ViewModelFactory
import timber.log.Timber
import javax.inject.Inject

class AutoBackupSettingFragment : BaseFragment(R.layout.fragment_true_cloudv3_auto_backup_setting) {

    private val binding by viewBinding(FragmentTrueCloudv3AutoBackupSettingBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: AutoBackupViewModel by viewModels { viewModelFactory }

    private val permissionManager: PermissionManager.PermissionAction by lazy {
        PermissionManagerImpl(fragmentActivity = this.requireActivity())
    }

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
        viewModel.init()
        initView()
        initListener()
        observeViewModel()
        arguments?.apply {
            getParcelable<DataUsageModel>(TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_DATA_USAGE)?.let {
                viewModel.setDataUsage(it)
            }
        }
    }

    private fun initView() = with(binding) {
        trueCloudBackImageView.onClick {
            activity?.onBackPressed()
        }
        backupActiveSwitch.setOnClickListener {
            if (checkStoragePermissionAlready()) {
                viewModel.setBackupState(backupActiveSwitch.isChecked)
            } else {
                permissionManager.requestPermission(
                    getStoragePermissions(),
                    object : PermissionManager.PermissionAskListener {
                        override fun onPermissionDisabled() {
                            backupActiveSwitch.isChecked = false
                            viewModel.setBackupState(false)
                            showSettingDialog(
                                DetailDialogModel(
                                    nodePermission = NodePermission.STORAGE,
                                    iconType = DialogIconType.FOLDER,
                                    title = getString(R.string.true_cloudv3_dialog_title_file),
                                    subTitle = getString(R.string.true_cloudv3_dialog_subtitle_file)
                                )
                            )
                            Timber.i("onPermissionDisabled")
                        }

                        override fun onPermissionGranted() {
                            viewModel.setBackupState(backupActiveSwitch.isChecked)
                        }

                        override fun onPermissionDenied() {
                            backupActiveSwitch.isChecked = false
                            viewModel.setBackupState(false)
                            Timber.i("onPermissionDenied")
                        }
                    }
                )
            }
        }
        imageActiveSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setContentBackUpState(AutoBackupViewModel.imageBackup, isChecked)
        }
        videoActiveSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setContentBackUpState(AutoBackupViewModel.videoBackup, isChecked)
        }
        audioActiveSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setContentBackUpState(AutoBackupViewModel.audioBackup, isChecked)
        }
        otherActiveSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.setContentBackUpState(AutoBackupViewModel.otherBackup, isChecked)
        }
    }

    private fun observeViewModel() {
        viewModel.onShowStorage.observe(viewLifecycleOwner) { dataStorage ->
            binding.apply {
                trueCloudImagesSizeTextView.text =
                    dataStorage.images.formatBinarySize(requireContext())
                trueCloudVideoSizeTextView.text =
                    dataStorage.videos.formatBinarySize(requireContext())
                trueCloudAudioSizeTextView.text =
                    dataStorage.audio.formatBinarySize(requireContext())
                trueCloudFileSizeTextView.text =
                    dataStorage.others.formatBinarySize(requireContext())
            }
        }
        viewModel.onUpdateBackupState.observe(viewLifecycleOwner) { isActive ->
            binding.apply {
                imageActiveSwitch.isVisible = isActive
                videoActiveSwitch.isVisible = isActive
                audioActiveSwitch.isVisible = isActive
                otherActiveSwitch.isVisible = isActive
            }
        }
        viewModel.onGetBackupState.observe(viewLifecycleOwner) { isActive ->
            binding.backupActiveSwitch.isChecked = isActive
        }
        viewModel.onGetImageState.observe(viewLifecycleOwner) { isActive ->
            binding.imageActiveSwitch.isChecked = isActive
        }
        viewModel.onGetVideoState.observe(viewLifecycleOwner) { isActive ->
            binding.videoActiveSwitch.isChecked = isActive
        }
        viewModel.onGetAudioState.observe(viewLifecycleOwner) { isActive ->
            binding.audioActiveSwitch.isChecked = isActive
        }
        viewModel.onGetOtherState.observe(viewLifecycleOwner) { isActive ->
            binding.otherActiveSwitch.isChecked = isActive
        }
        viewModel.onActiveBackupSuccess.observe(viewLifecycleOwner) {
            binding.root.snackBar(
                it,
                R.color.true_cloudv3_color_toast_success
            )
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

    private fun actionApplicationSetting() {
        activity?.startActivity(
            Intent().apply {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data =
                    Uri.fromParts(AutoBackupViewModel.PACKAGE, requireContext().packageName, null)
            }
        )
    }

    private fun showSettingDialog(detailDialogModel: DetailDialogModel) {
        DialogManager.getBottomSheetDialog(
            context = requireContext(),
            icon = detailDialogModel.iconType,
            title = detailDialogModel.title,
            subTitle = detailDialogModel.subTitle
        ) {
            setPrimaryButton(getString(R.string.true_cloudv3_button_setting)) { dialog ->
                dialog.dismiss()
                actionApplicationSetting()
            }
            setSecondaryButton(getString(R.string.true_cloudv3_button_later)) { dialog ->
                dialog.dismiss()
            }
        }.show(childFragmentManager)
    }
}
