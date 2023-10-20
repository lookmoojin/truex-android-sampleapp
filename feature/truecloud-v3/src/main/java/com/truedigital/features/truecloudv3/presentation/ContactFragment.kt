package com.truedigital.features.truecloudv3.presentation

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.databinding.FragmentTrueCloudv3ContactBinding
import com.truedigital.common.share.componentv3.extension.getSavedStateHandle
import com.truedigital.component.base.BaseFragment
import com.truedigital.component.dialog.trueid.DialogIconType
import com.truedigital.component.dialog.trueid.DialogManager
import com.truedigital.component.dialog.trueid.DialogTopNavigationType
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.truecloudv3.common.TrueCloudV3MediaType
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_CONTACT_CAll
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_CONTACT_DATA
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_CONTACT_DELETE_ALL
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_CONTACT_EXPORT_TO_DEVICE
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_CONTACT_SELECT
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_CONTACT_SYNC_ALL
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_DELETE_CONTACT
import com.truedigital.features.truecloudv3.domain.model.ContactPhoneNumberModel
import com.truedigital.features.truecloudv3.domain.model.ContactTrueCloudModel
import com.truedigital.features.truecloudv3.domain.model.DetailDialogModel
import com.truedigital.features.truecloudv3.domain.usecase.NodePermission
import com.truedigital.features.truecloudv3.extension.checkContactStoragePermissionAlready
import com.truedigital.features.truecloudv3.extension.snackBar
import com.truedigital.features.truecloudv3.injections.TrueCloudV3Component
import com.truedigital.features.truecloudv3.presentation.adapter.ContactAdapter
import com.truedigital.features.truecloudv3.presentation.viewmodel.ContactViewModel
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.visible
import com.truedigital.foundation.presentations.ViewModelFactory
import com.wafflecopter.multicontactpicker.LimitColumn
import com.wafflecopter.multicontactpicker.MultiContactPicker
import javax.inject.Inject

class ContactFragment : BaseFragment(R.layout.fragment_true_cloudv3_contact) {
    companion object {
        private const val TEL = "tel"
        private const val CONTACT_PICKER_REQUEST = 1011
    }

    private val binding by viewBinding(FragmentTrueCloudv3ContactBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: ContactViewModel by viewModels { viewModelFactory }

    private var contactAdapter = ContactAdapter()

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
        getContacts()
    }

    override fun onResume() {
        super.onResume()
        findNavController()
            .getSavedStateHandle<ContactTrueCloudModel>(KEY_TRUE_CLOUD_CONTACT_DATA)
            ?.observe(
                viewLifecycleOwner
            ) { contactTrueCloudModel ->
                viewModel.updateContact(contactTrueCloudModel)
            }
    }

    override fun onPause() {
        super.onPause()
        findNavController().previousBackStackEntry?.savedStateHandle?.remove<ContactTrueCloudModel>(
            KEY_TRUE_CLOUD_CONTACT_DATA
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        findNavController().previousBackStackEntry?.savedStateHandle?.remove<Boolean>(
            KEY_TRUE_CLOUD_CONTACT_SELECT
        )
        findNavController().previousBackStackEntry?.savedStateHandle?.remove<Boolean>(
            KEY_TRUE_CLOUD_CONTACT_SYNC_ALL
        )
        findNavController().previousBackStackEntry?.savedStateHandle?.remove<ContactPhoneNumberModel>(
            KEY_TRUE_CLOUD_CONTACT_CAll
        )
        findNavController().previousBackStackEntry?.savedStateHandle?.remove<Boolean>(
            KEY_TRUE_CLOUD_CONTACT_EXPORT_TO_DEVICE
        )
        findNavController().previousBackStackEntry?.savedStateHandle?.remove<Boolean>(
            KEY_TRUE_CLOUD_CONTACT_DELETE_ALL
        )
    }

    private fun getContacts() {
        viewModel.getContactList()
    }

    private fun initView() = with(binding) {
        trueCloudHeaderTitle.apply {
            setOnClickBack {
                viewModel.onClickBack()
            }
            setOnClickSync {
                viewModel.onClickSync()
            }
            setOnClickMoreOption {
                viewModel.onClickMoreOption()
            }
        }

        trueCloudContactRecyclerView.apply {
            itemAnimator = null
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
            adapter = contactAdapter
        }

        trueCloudAlphabetScrollerWidget.apply {
            setAttachedRecyclerView(trueCloudContactRecyclerView)
            onScrollListener = {
                // Do Nothing
            }
        }
    }

    private fun initListener() {
        contactAdapter.onItemClicked = {
            viewModel.onContactClicked(it)
        }
        findNavController().getSavedStateHandle<Boolean>(KEY_TRUE_CLOUD_CONTACT_SYNC_ALL)
            ?.observe(
                viewLifecycleOwner
            ) {
                viewModel.onSelectSyncAllContact()
            }
        findNavController().getSavedStateHandle<Boolean>(KEY_TRUE_CLOUD_CONTACT_SELECT)
            ?.observe(
                viewLifecycleOwner
            ) {
                if (checkContactStoragePermissionAlready()) {
                    viewModel.selectContactPermissionAlready()
                } else {
                    viewModel.performClickIntroPermission(
                        getBundlePermissionMedia(TrueCloudV3MediaType.TypeContact)
                    )
                }
            }

        findNavController().getSavedStateHandle<Boolean>(KEY_TRUE_CLOUD_DELETE_CONTACT)
            ?.observe(
                viewLifecycleOwner
            ) {
                viewModel.deleteContact()
            }
        findNavController().getSavedStateHandle<Boolean>(KEY_TRUE_CLOUD_CONTACT_DELETE_ALL)
            ?.observe(
                viewLifecycleOwner
            ) {
                viewModel.deleteAllContact()
            }
        findNavController().getSavedStateHandle<Boolean>(KEY_TRUE_CLOUD_CONTACT_EXPORT_TO_DEVICE)
            ?.observe(
                viewLifecycleOwner
            ) {
                viewModel.exportToDevice()
            }
        findNavController().getSavedStateHandle<ContactPhoneNumberModel>(KEY_TRUE_CLOUD_CONTACT_CAll)
            ?.observe(
                viewLifecycleOwner
            ) {
                viewModel.onClickCall(it)
            }
    }

    private fun observeViewModel() {
        viewModel.updateContactData.observe(viewLifecycleOwner) { contacts ->
            contactAdapter.setContactList(contacts)
            binding.trueCloudloadingProgressBar.root.gone()
            binding.trueCloudDataEmpty.gone()
            binding.trueCloudContactRecyclerView.visible()
        }
        viewModel.showEmptyContact.observe(viewLifecycleOwner) {
            contactAdapter.setContactList(listOf())
            binding.trueCloudloadingProgressBar.root.gone()
            binding.trueCloudDataEmpty.visible()
            binding.trueCloudContactRecyclerView.gone()
        }

        viewModel.groupAlphabetLiveData.observe(viewLifecycleOwner) { alphabets ->
            binding.trueCloudAlphabetScrollerWidget.submitList(alphabets)
        }

        viewModel.onBackPressed.observe(viewLifecycleOwner) {
            activity?.onBackPressed()
        }

        viewModel.onContactNotfound.observe(viewLifecycleOwner) {
            binding.trueCloudDataEmpty.visible()
            binding.trueCloudloadingProgressBar.root.gone()
        }
        viewModel.onGetContactError.observe(viewLifecycleOwner) { (message: String, action: String) ->
            showErrorStateFullScreenDialog(message = message, action = action)
        }

        viewModel.onIntentActionGetContact.observe(viewLifecycleOwner) {
            MultiContactPicker.Builder(this@ContactFragment)
                .hideScrollbar(false)
                .showTrack(true)
                .setChoiceMode(MultiContactPicker.CHOICE_MODE_MULTIPLE)
                .setLoadingType(MultiContactPicker.LOAD_ASYNC)
                .limitToColumn(LimitColumn.NONE)
                .setActivityAnimations(
                    android.R.anim.fade_in,
                    android.R.anim.fade_out,
                    android.R.anim.fade_in,
                    android.R.anim.fade_out
                )
                .showPickerForResult(CONTACT_PICKER_REQUEST)
        }

        viewModel.callToNumber.observe(viewLifecycleOwner) { number ->
            val intent = Intent(
                Intent.ACTION_DIAL,
                Uri.fromParts(TEL, number, null)
            )
            context?.startActivity(intent)
        }

        viewModel.onShowSnackbarSuccess.observe(viewLifecycleOwner) { message ->
            binding.root.snackBar(
                message,
                R.color.true_cloudv3_color_toast_success
            )
        }
        viewModel.onShowSnackbarExportContactError.observe(viewLifecycleOwner) { message ->
            binding.root.snackBar(
                message,
                R.color.true_cloudv3_color_toast_error
            )
        }
        viewModel.onShowSnackbarSuccess.observe(viewLifecycleOwner) { message ->
            binding.root.snackBar(
                message,
                R.color.true_cloudv3_color_toast_success
            )
        }
        viewModel.onShowSnackbarError.observe(viewLifecycleOwner) { message ->
            binding.root.snackBar(
                message,
                R.color.true_cloudv3_color_toast_error
            )
        }
        viewModel.onShowDialogDeleteAll.observe(viewLifecycleOwner) { (_title, _subtitle) ->
            showConfirmDialogDeleteAll(_title, _subtitle)
        }
        viewModel.onShowDialogSyncAll.observe(viewLifecycleOwner) { (_title, _subtitle) ->
            showConfirmDialogSyncAll(_title, _subtitle)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CONTACT_PICKER_REQUEST && resultCode == RESULT_OK) {
            viewModel.onActivityResult(data)
        }
    }

    private fun showConfirmDialogDeleteAll(title: String, subtitle: String) {
        DialogManager.getBottomSheetDialog(
            context = requireContext(),
            icon = DialogIconType.DELETE,
            title = title,
            subTitle = subtitle
        ) {
            setSecondaryButton(R.string.cancel) { dialog ->
                dialog.dismiss()
            }
            setPrimaryButton(R.string.true_cloudv3_button_confirm) {
                it.dismiss()
                viewModel.onClickConfirmDeleteAllDialog()
            }
        }.show(childFragmentManager)
    }

    private fun showConfirmDialogSyncAll(title: String, subtitle: String) {
        DialogManager.getBottomSheetDialog(
            context = requireContext(),
            icon = DialogIconType.CONTACT,
            title = title,
            subTitle = subtitle
        ) {
            setPrimaryButton(R.string.true_cloudv3_button_confirm) {
                it.dismiss()
                viewModel.onClickConfirmSyncAllDialog()
            }
            setSecondaryButton(getString(R.string.cancel)) { dialog ->
                dialog.dismiss()
            }
        }.show(childFragmentManager)
    }

    private fun getBundlePermissionMedia(type: TrueCloudV3MediaType?): Bundle {
        return Bundle().apply {
            putParcelable(IntroPermissionFragment.KEY_TRUE_CLOUD_TYPE, type)
            putStringArray(
                IntroPermissionFragment.KEY_PERMISSION_LIST,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_CONTACTS
                )
            )
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
}
