package com.truedigital.features.truecloudv3.presentation

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.truedigital.common.share.componentv3.common.IconGravity
import com.truedigital.common.share.componentv3.extension.getSavedStateHandle
import com.truedigital.common.share.componentv3.extension.setSavedStateHandle
import com.truedigital.common.share.componentv3.widget.header.BackIconWidget
import com.truedigital.component.base.BaseFragment
import com.truedigital.component.dialog.trueid.DialogIconType
import com.truedigital.component.dialog.trueid.DialogManager
import com.truedigital.component.dialog.trueid.DialogTopNavigationType
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.common.FileCategoryType
import com.truedigital.features.truecloudv3.common.TrueCloudV3MediaType
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey.KEY_FROM_INIT_MIGRATION_PAGE
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey.KEY_PERMISSION_GRANTED_RESULT
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey.KEY_PERMISSION_GRANTED_RESULT_CATEGORY
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_OPTION_MAIN_FILE_ADD_NEW_FOLDER
import com.truedigital.features.truecloudv3.databinding.FragmentTrueCloudv3MainBinding
import com.truedigital.features.truecloudv3.domain.model.DetailDialogModel
import com.truedigital.features.truecloudv3.domain.usecase.NodePermission
import com.truedigital.features.truecloudv3.extension.actionGetContent
import com.truedigital.features.truecloudv3.extension.checkContactStoragePermissionAlready
import com.truedigital.features.truecloudv3.extension.checkStoragePermissionAlready
import com.truedigital.features.truecloudv3.extension.formatBinarySize
import com.truedigital.features.truecloudv3.extension.snackBar
import com.truedigital.features.truecloudv3.injections.TrueCloudV3Component
import com.truedigital.features.truecloudv3.presentation.viewmodel.MainTrueCloudV3ViewModel
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject

class MainTrueCloudV3Fragment : BaseFragment(R.layout.fragment_true_cloudv3_main) {

    private val binding by viewBinding(FragmentTrueCloudv3MainBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: MainTrueCloudV3ViewModel by viewModels { viewModelFactory }

    private val appChooserResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            viewModel.onSelectedUploadFileResult(result.data)
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
        viewModel.checkAuthenticationState()
        viewModel.onViewCreated()
        initAppBar()
        initViews()
        initListener()
        observeViewModel()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        findNavController().setSavedStateHandle(KEY_PERMISSION_GRANTED_RESULT, null)
        findNavController().setSavedStateHandle(KEY_PERMISSION_GRANTED_RESULT_CATEGORY, null)
        findNavController().setSavedStateHandle(KEY_FROM_INIT_MIGRATION_PAGE, null)
        findNavController().setSavedStateHandle(
            KEY_TRUE_CLOUD_OPTION_MAIN_FILE_ADD_NEW_FOLDER,
            null
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.onResume()
    }

    private fun initListener() {
        findNavController()
            .getSavedStateHandle<String>(KEY_TRUE_CLOUD_OPTION_MAIN_FILE_ADD_NEW_FOLDER)
            ?.observe(
                viewLifecycleOwner
            ) { _folderName ->
                viewModel.createFolder(_folderName)
            }
        findNavController()
            .getSavedStateHandle<TrueCloudV3MediaType>(KEY_PERMISSION_GRANTED_RESULT)?.observe(
                viewLifecycleOwner
            ) { trueCloudMediaType ->
                viewModel.getNextActionIntent(trueCloudMediaType)
            }
        findNavController()
            .getSavedStateHandle<TrueCloudV3MediaType>(KEY_PERMISSION_GRANTED_RESULT_CATEGORY)
            ?.observe(
                viewLifecycleOwner
            ) { trueCloudMediaType ->
                viewModel.getNextActionCategoryIntent(trueCloudMediaType)
            }

        findNavController()
            .getSavedStateHandle<Boolean>(KEY_FROM_INIT_MIGRATION_PAGE)?.observe(
                viewLifecycleOwner
            ) { it ->
                if (it != null && it) {
                    val createMigrationBottomSheetDialogFragment =
                        CreateMigrationBottomSheetDialogFragment(onMigrationClick)
                    createMigrationBottomSheetDialogFragment.show(
                        childFragmentManager,
                        createMigrationBottomSheetDialogFragment.tag
                    )
                }
            }
    }

    private val onMigrationClick =
        object : CreateMigrationBottomSheetDialogFragment.OnMigrationClick {
            override fun onSetting() {
                viewModel.settingClicked()
            }
        }

    private val onMigratedClick = object : MigratedBottomSheetDialogFragment.OnMigratedClick {
        override fun onAllFile() {
            onCheckPermissionForCategory(FileCategoryType.UNSUPPORTED_FORMAT)
        }
    }

    private fun initAppBar() = with(binding) {
        trueCloudMainAppBar.apply {
            addIconView(
                listOf(
                    Triple(backView(), IconGravity.LEFT, View.VISIBLE)
                )
            )
        }
    }

    private fun initViews() = with(binding) {
        floatingButton.onClick {
            val createNewFolderBottomSheetDialogFragment =
                CreateNewBottomSheetDialogFragment(onCreateFolderBottomSheetClick)
            createNewFolderBottomSheetDialogFragment.show(
                childFragmentManager,
                createNewFolderBottomSheetDialogFragment.tag
            )
        }
        trueCloudMenuRecent.onClick {
            onCheckPermissionForCategory(FileCategoryType.RECENT)
        }
        trueCloudMenuImages.onClick {
            onCheckPermissionForCategory(FileCategoryType.IMAGE)
        }
        trueCloudMenuVideos.onClick {
            onCheckPermissionForCategory(FileCategoryType.VIDEO)
        }
        trueCloudMenuAudio.onClick {
            onCheckPermissionForCategory(FileCategoryType.AUDIO)
        }
        trueCloudMenuFiles.onClick {
            onCheckPermissionForCategory(FileCategoryType.OTHER)
        }
        trueCloudMenuContacts.onClick {
            onCheckPermissionForCategory(FileCategoryType.CONTACT)
        }
        trueCloudButton.trueCloudAllfilesButton.onClick {
            onCheckPermissionForCategory(FileCategoryType.UNSUPPORTED_FORMAT)
        }

        trueCloudButton.trueCloudSettingButton.onClick {
            viewModel.settingClicked()
        }

        trueCloudButton.trueCloudTrashButton.onClick {
            viewModel.trashClicked()
        }

        trueCloudStorageBarView.setOnClickMigrate {
            viewModel.migrateClicked()
        }

        trueCloudStorageBarView.setOnClickAutoBackup {
            viewModel.settingClicked()
        }
    }

    private val onCreateFolderBottomSheetClick =
        object : CreateNewBottomSheetDialogFragment.OnCreateFolderClick {
            override fun onCreateFolder() {
                viewModel.createNewFolder()
            }

            override fun onUpload() {
                viewModel.onClickUpload()
            }
        }

    private fun onCheckPermissionForCategory(fileCategoryType: FileCategoryType) {
        when (fileCategoryType) {
            FileCategoryType.IMAGE,
            FileCategoryType.VIDEO,
            FileCategoryType.RECENT,
            FileCategoryType.AUDIO,
            FileCategoryType.OTHER,
            FileCategoryType.UNSUPPORTED_FORMAT -> {
                if (checkStoragePermissionAlready()) {
                    viewModel.categoryClicked(fileCategoryType)
                } else {
                    viewModel.performClickIntroPermission(
                        getBundlePermissionCategory(fileCategoryType.mediaType)
                    )
                }
            }
            FileCategoryType.CONTACT -> {
                if (checkContactStoragePermissionAlready()) {
                    viewModel.contactClicked(fileCategoryType.type)
                } else {
                    viewModel.performClickIntroPermission(
                        getBundlePermissionContact(TrueCloudV3MediaType.TypeContact)
                    )
                }
            }
            else -> {
                /* Nothing to do */
            }
        }
    }

    private fun getBundlePermissionMedia(type: TrueCloudV3MediaType): Bundle {
        return Bundle().apply {
            putParcelable(IntroPermissionFragment.KEY_TRUE_CLOUD_TYPE, type)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                putStringArray(
                    IntroPermissionFragment.KEY_PERMISSION_LIST,
                    arrayOf(
                        Manifest.permission.READ_MEDIA_AUDIO,
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO
                    )
                )
            } else {
                putStringArray(
                    IntroPermissionFragment.KEY_PERMISSION_LIST,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
            }
            putParcelable(
                IntroPermissionFragment.KEY_DIALOG_MODEL,
                DetailDialogModel(
                    nodePermission = NodePermission.STORAGE,
                    iconType = DialogIconType.FOLDER,
                    title = getString(R.string.true_cloudv3_dialog_title_file),
                    subTitle = getString(R.string.true_cloudv3_dialog_subtitle_file)
                )
            )
        }
    }

    private fun getBundlePermissionContact(type: TrueCloudV3MediaType): Bundle {
        return Bundle().apply {
            putParcelable(IntroPermissionFragment.KEY_TRUE_CLOUD_TYPE_CATEGORY, type)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                putStringArray(
                    IntroPermissionFragment.KEY_PERMISSION_LIST,
                    arrayOf(
                        Manifest.permission.READ_MEDIA_AUDIO,
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO,
                        Manifest.permission.READ_CONTACTS
                    )
                )
            } else {
                putStringArray(
                    IntroPermissionFragment.KEY_PERMISSION_LIST,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_CONTACTS
                    )
                )
            }
            putParcelable(
                IntroPermissionFragment.KEY_DIALOG_MODEL,
                DetailDialogModel(
                    nodePermission = NodePermission.CONTACT,
                    iconType = DialogIconType.CONTACT,
                    title = getString(R.string.true_cloudv3_dialog_title_contact),
                    subTitle = getString(R.string.true_cloudv3_dialog_subtitle_contact)
                )
            )
        }
    }

    private fun getBundlePermissionCategory(type: TrueCloudV3MediaType): Bundle {
        return Bundle().apply {
            putParcelable(IntroPermissionFragment.KEY_TRUE_CLOUD_TYPE_CATEGORY, type)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                putStringArray(
                    IntroPermissionFragment.KEY_PERMISSION_LIST,
                    arrayOf(
                        Manifest.permission.READ_MEDIA_AUDIO,
                        Manifest.permission.READ_MEDIA_IMAGES,
                        Manifest.permission.READ_MEDIA_VIDEO
                    )
                )
            } else {
                putStringArray(
                    IntroPermissionFragment.KEY_PERMISSION_LIST,
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.MANAGE_EXTERNAL_STORAGE
                    )
                )
            }
            putParcelable(
                IntroPermissionFragment.KEY_DIALOG_MODEL,
                DetailDialogModel(
                    nodePermission = NodePermission.STORAGE,
                    iconType = DialogIconType.FOLDER,
                    title = getString(R.string.true_cloudv3_dialog_title_file),
                    subTitle = getString(R.string.true_cloudv3_dialog_subtitle_file)
                )
            )
        }
    }

    private fun backView(): View {
        return BackIconWidget(requireContext()).apply {
            setOnClick {
                activity?.onBackPressed()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.onShowStorage.observe(viewLifecycleOwner) { dataStorage ->
            dataStorage?.let {
                binding.apply {
                    trueCloudStorageBarView.setTrueCloudStorage(dataStorage)
                    trueCloudImagesSizeTextView.text =
                        it.dataUsage?.images?.formatBinarySize(requireContext())
                    trueCloudVideosSizeTextView.text =
                        it.dataUsage?.videos?.formatBinarySize(requireContext())
                    trueCloudAudioSizeTextView.text =
                        it.dataUsage?.audio?.formatBinarySize(requireContext())
                    trueCloudOthersSizeTextView.text =
                        it.dataUsage?.others?.formatBinarySize(requireContext())
                    trueCloudContactsSizeTextView.text =
                        it.dataUsage?.contacts?.formatBinarySize(requireContext())
                }
            }
        }

        viewModel.onShowSnackbarComplete.observe(viewLifecycleOwner) {
            binding.root.snackBar(
                it,
                R.color.true_cloudv3_color_toast_success
            )
        }
        viewModel.onShowSnackbarError.observe(viewLifecycleOwner) {
            binding.root.snackBar(
                it,
                R.color.true_cloudv3_color_toast_error
            )
        }
        viewModel.onIntentActionGetContent.observe(
            viewLifecycleOwner
        ) { mimeTypes ->
            appChooserResult.launch(
                Intent.createChooser(
                    actionGetContent(mimeTypes),
                    getString(R.string.true_cloudv3_select_a_file_to_upload)
                )
            )
        }
        viewModel.onShowMigrated.observe(viewLifecycleOwner) {
            val migratedBottomSheetDialogFragment =
                MigratedBottomSheetDialogFragment(onMigratedClick)
            migratedBottomSheetDialogFragment.show(
                childFragmentManager,
                migratedBottomSheetDialogFragment.tag
            )
        }
        viewModel.onShowMigrateFail.observe(viewLifecycleOwner) {
            if (!it) {
                binding.trueCloudStorageBarView.hideMigrationFail()
            }
        }

        viewModel.onUploadError.observe(viewLifecycleOwner) {
            binding.root.snackBar(
                it,
                R.color.true_cloudv3_color_toast_error
            )
            binding.trueCloudStorageBarView.setFiledBackup()
            binding.trueCloudStorageBarView.setOnClickAutoBackup {
                // Disable for 3.28.1
                // viewModel.checkAutoBackup()
            }
        }
        viewModel.onGetStorageError.observe(viewLifecycleOwner) { (message: String, action: String) ->
            showErrorStateFullScreenDialog(message = message, action = action)
        }
        viewModel.onUploadFile.observe(viewLifecycleOwner) { uploadFile() }
        viewModel.onAutoBackupState.observe(viewLifecycleOwner) { isActive ->
            if (isActive) {
                viewModel.initBackup()
            } else {
                binding.trueCloudStorageBarView.hideAutoBackup()
            }
        }
        viewModel.onUpdateBackupUi.observe(viewLifecycleOwner) { isActive ->
            if (isActive) {
                binding.trueCloudStorageBarView.setActiveBackup()
            } else {
                binding.trueCloudStorageBarView.hideAutoBackup()
            }
        }
        viewModel.onShowInitAutoBackup.observe(viewLifecycleOwner) {
            binding.trueCloudStorageBarView.showAutoBackup()
        }
    }

    private fun showErrorStateFullScreenDialog(message: String, action: String) {
        DialogManager.getFullScreenDialog(
            context = requireContext(),
            icon = DialogIconType.WARNING,
            title = getString(R.string.true_cloudv3_dialog_title_sorry),
            subTitle = getString(R.string.true_cloudv3_dialog_subtitle_something_wrong, message)
        ) {
            setTopNavigationType(DialogTopNavigationType.BACK_BUTTON)
            setBackButtonListener {
                it.dismiss()
            }
            setPrimaryButton(getString(R.string.true_cloudv3_button_retry)) { dialog ->
                dialog.dismiss()
                viewModel.checkRetryState(action)
            }
        }.show(childFragmentManager)
    }

    private fun uploadFile() {
        if (checkStoragePermissionAlready()) {
            appChooserResult.launch(
                Intent.createChooser(
                    actionGetContent(
                        TrueCloudV3MediaType.TypeAllFile.mimeType
                    ),
                    getString(R.string.true_cloudv3_select_a_file_to_upload)
                )
            )
        } else {
            viewModel.performClickIntroPermission(
                getBundlePermissionMedia(TrueCloudV3MediaType.TypeAllFile)
            )
        }
    }
}
