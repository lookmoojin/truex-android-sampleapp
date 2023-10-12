package com.truedigital.features.truecloudv3.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.truedigital.common.share.componentv3.extension.setSavedStateHandle
import com.truedigital.component.base.BaseFragment
import com.truedigital.component.dialog.trueid.DialogManager
import com.truedigital.core.extensions.viewBinding
import com.truedigital.core.manager.permission.PermissionManager
import com.truedigital.core.manager.permission.PermissionManagerImpl
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.common.TrueCloudV3MediaType
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey.KEY_PERMISSION_GRANTED_RESULT
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey.KEY_PERMISSION_GRANTED_RESULT_CATEGORY
import com.truedigital.features.truecloudv3.databinding.FragmentTrueCloudv3IntroPermissionBinding
import com.truedigital.features.truecloudv3.domain.model.DetailDialogModel
import com.truedigital.features.truecloudv3.injections.TrueCloudV3Component
import com.truedigital.features.truecloudv3.presentation.viewmodel.IntroPermissionViewModel
import com.truedigital.foundation.extension.RESIZE_LARGE
import com.truedigital.foundation.extension.load
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject

class IntroPermissionFragment : BaseFragment(R.layout.fragment_true_cloudv3_intro_permission) {

    companion object {
        const val KEY_TRUE_CLOUD_TYPE = "key_true_cloudV3_type"
        const val KEY_TRUE_CLOUD_TYPE_CATEGORY = "key_true_cloudV3_type_category"
        const val KEY_PERMISSION_LIST = "key_permission_list"
        const val KEY_DIALOG_MODEL = "key_dialog_model"
        private const val EMPTY_STRING = ""
        private const val PACKAGE = "package"
    }

    private val binding by viewBinding(FragmentTrueCloudv3IntroPermissionBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: IntroPermissionViewModel by viewModels { viewModelFactory }
    private val permissionManager: PermissionManager.PermissionAction by lazy {
        PermissionManagerImpl(requireActivity())
    }

    private val appSettingsResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            viewModel.getStorePermissionList()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        TrueCloudV3Component.getInstance().inject(this)
        super.onCreate(savedInstanceState)
        // Check allow permission from everything first.
        arguments?.getStringArray(KEY_PERMISSION_LIST)?.let { permissionList ->
            if (!permissionManager.checkPermission(permissionList)) {
                viewModel.onPermissionGrantedResult()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        arguments?.getStringArray(KEY_PERMISSION_LIST)?.let { permissionList ->
            if (!permissionManager.checkPermission(permissionList)) {
                viewModel.onPermissionGrantedResult()
            }
        }
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
        initViews()
        observeViewModel()

        arguments?.getStringArray(KEY_PERMISSION_LIST)?.let { permissionList ->
            viewModel.setStorePermissionList(permissionList)
        }
        arguments?.getParcelable<DetailDialogModel>(KEY_DIALOG_MODEL)?.let { model ->
            viewModel.setStoreDetailDialogModel(model)
        }
        viewModel.getPermissionImage()
    }

    private fun initViews() = with(binding) {
        allowTextView.onClick {
            viewModel.getStorePermissionList()
        }
    }

    private fun observeViewModel() = with(binding) {
        viewModel.onRequestPermissionList.observe(
            viewLifecycleOwner
        ) { (permission, dialogModel) ->
            requestPermissionBeforeAction(permission, dialogModel)
        }
        viewModel.onShowSettingDialog.observe(
            viewLifecycleOwner
        ) { dialogModel ->
            showSettingDialog(dialogModel)
        }
        viewModel.onClosePage.observe(
            viewLifecycleOwner
        ) {
            findNavController().navigateUp()
        }
        viewModel.onSetMediaType.observe(
            viewLifecycleOwner
        ) {
            val trueCloudType = arguments?.getParcelable(KEY_TRUE_CLOUD_TYPE)
                ?: TrueCloudV3MediaType(EMPTY_STRING, emptyArray())
            findNavController().setSavedStateHandle(
                KEY_PERMISSION_GRANTED_RESULT,
                trueCloudType
            )

            val trueCloudType2 = arguments?.getParcelable(KEY_TRUE_CLOUD_TYPE_CATEGORY)
                ?: TrueCloudV3MediaType(EMPTY_STRING, emptyArray())
            findNavController().setSavedStateHandle(
                KEY_PERMISSION_GRANTED_RESULT_CATEGORY,
                trueCloudType2
            )
        }
        viewModel.onShowButtonAndImage.observe(
            viewLifecycleOwner
        ) { (buttonText, imageUrl) ->
            allowTextView.text = buttonText
            permissionImageView.context?.let { _context ->
                permissionImageView.load(
                    context = _context,
                    url = imageUrl,
                    resizeType = RESIZE_LARGE
                )
            }
        }
    }

    private fun requestPermissionBeforeAction(
        permissionList: Array<String>,
        detailDialogModel: DetailDialogModel
    ) {
        permissionManager.requestPermission(
            permissionList,
            object : PermissionManager.PermissionAskListener {
                override fun onPermissionDisabled() {
                    viewModel.checkFirstDisablePermission(detailDialogModel)
                }

                override fun onPermissionGranted() {
                    viewModel.onPermissionGrantedResult()
                }

                override fun onPermissionDenied() {
                    viewModel.onPermissionDenied()
                }
            }
        )
    }

    private fun actionApplicationSetting() {
        appSettingsResult.launch(
            Intent().apply {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.fromParts(PACKAGE, requireContext().packageName, null)
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
                viewModel.onClickLater()
            }
        }.show(childFragmentManager)
    }
}
