package com.truedigital.features.truecloudv3.presentation

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tdg.truecloud.R
import com.tdg.truecloud.databinding.FragmentTrueCloudv3EditContactBinding
import com.truedigital.common.share.componentv3.extension.getSavedStateHandle
import com.truedigital.common.share.componentv3.extension.setSavedStateHandle
import com.truedigital.component.base.BaseFragment
import com.truedigital.component.dialog.trueid.DialogIconType
import com.truedigital.component.dialog.trueid.DialogManager
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_CONTACT_DATA
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_CONTACT_DATA
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_CONTACT_SELECTED_PHONE_LABEL
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_DELETE_CONTACT
import com.truedigital.features.truecloudv3.databinding.FragmentTrueCloudv3EditContactBinding
import com.truedigital.features.truecloudv3.domain.model.ContactTrueCloudModel
import com.truedigital.features.truecloudv3.domain.model.CustomPhoneLabelModel
import com.truedigital.features.truecloudv3.injections.TrueCloudV3Component
import com.truedigital.features.truecloudv3.presentation.viewmodel.TrueCloudV3ContactEditViewModel
import com.truedigital.features.truecloudv3.widget.TrueCloudV3CustomPhoneLabelView
import com.truedigital.foundation.extension.getBitmap
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.visible
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject

class ContactEditFragment : BaseFragment(R.layout.fragment_true_cloudv3_edit_contact) {

    private val binding by viewBinding(FragmentTrueCloudv3EditContactBinding::bind)
    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                binding.userThumbnailImageView.setImageURI(uri)
                binding.thumbnailImageView.gone()
                binding.userThumbnailImageView.visible()
            }
        }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: TrueCloudV3ContactEditViewModel by viewModels { viewModelFactory }

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

        initView()
        observeViewModel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(
                KEY_BUNDLE_TRUE_CLOUD_CONTACT_DATA,
                ContactTrueCloudModel::class.java
            )?.let { model ->
                viewModel.setContactData(model)
            }
        } else {
            arguments?.getParcelable<ContactTrueCloudModel>(KEY_BUNDLE_TRUE_CLOUD_CONTACT_DATA)
                ?.let { model ->
                    viewModel.setContactData(model)
                }
        }
    }

    override fun onResume() {
        super.onResume()
        initListener()
    }

    override fun onPause() {
        super.onPause()
        findNavController().previousBackStackEntry?.savedStateHandle?.remove<CustomPhoneLabelModel>(
            KEY_TRUE_CLOUD_CONTACT_SELECTED_PHONE_LABEL
        )
    }

    private fun initListener() {
        findNavController().getSavedStateHandle<CustomPhoneLabelModel>(
            KEY_TRUE_CLOUD_CONTACT_SELECTED_PHONE_LABEL
        )
            ?.observe(
                viewLifecycleOwner
            ) { _customPhoneLabelModel ->
                viewModel.updateCustomPhoneLabel(_customPhoneLabelModel)
            }
    }

    private fun initView() = with(binding) {
        trueCloudHeaderTitle.apply {
            setOnClickClose {
                viewModel.onClickBack()
            }

            setOnClickSave {
                var picture = userThumbnailImageView.getBitmap()
                val firstname = trueCloudFirstNameTextInputLayout.editText?.text.toString()
                val lastname = trueCloudLastNameTextInputLayout.editText?.text.toString()
                val email = trueCloudEmailTextInputLayout.editText?.text.toString()

                viewModel.onClickSave(
                    picture = picture,
                    firstname = firstname,
                    lastname = lastname,
                    email = email
                )
            }
        }
        trueCloudBlockContainer.setOnClickListener {
            viewModel.onClickAddCustomLabel()
        }
        deleteTextView.setOnClickListener {
            viewModel.onClickDeleteContact()
        }
        thumbnailImageView.setOnClickListener {
            viewModel.onThumbnailImageViewClicked()
        }
        userThumbnailImageView.setOnClickListener {
            viewModel.onThumbnailImageViewClicked()
        }
    }

    private fun saveAndClose(contactTrueCloudModel: ContactTrueCloudModel) {
        findNavController().setSavedStateHandle(
            KEY_TRUE_CLOUD_CONTACT_DATA, contactTrueCloudModel
        )
        findNavController().navigateUp()
    }

    private fun deleteContact() {
        findNavController().setSavedStateHandle(
            KEY_TRUE_CLOUD_DELETE_CONTACT, true
        )
        findNavController().navigateUp()
    }

    private fun observeViewModel() {
        viewModel.onBackPressed.observe(viewLifecycleOwner) {
            activity?.onBackPressed()
        }

        viewModel.onSetContactData.observe(viewLifecycleOwner) {
            if (it.picture != null) {
                binding.userThumbnailImageView.setImageBitmap(it.picture)
                binding.thumbnailImageView.gone()
                binding.userThumbnailImageView.visible()
            } else {
                binding.thumbnailImageView.visible()
                binding.userThumbnailImageView.gone()
            }
            binding.trueCloudFirstNameTextInputLayout.editText?.setText(it.firstName)
            binding.trueCloudLastNameTextInputLayout.editText?.setText(it.lastName)
            binding.trueCloudEmailTextInputLayout.editText?.setText(it.email)
        }
        viewModel.onShowSaved.observe(viewLifecycleOwner) {
            saveAndClose(it)
        }
        viewModel.onDeleteContact.observe(viewLifecycleOwner) {
            deleteContact()
        }
        viewModel.showConfirmDialogDelete.observe(viewLifecycleOwner) {
            showConfirmDialogDelete()
        }

        viewModel.addCustomLabel.observe(viewLifecycleOwner) { _customLabel ->
            addCustomLabelView(_customLabel)
        }
        viewModel.addAllCustomLabel.observe(viewLifecycleOwner) {
            it.forEach { customLabel ->
                addCustomLabelView(customLabel)
            }
        }

        viewModel.onRemoveCustomLabel.observe(viewLifecycleOwner) { tagId ->
            val trueCloudV3CustomPhoneLabelView =
                binding.trueCloudCustomLabelContainer.findViewWithTag<TrueCloudV3CustomPhoneLabelView>(
                    tagId
                )
            binding.trueCloudCustomLabelContainer.removeView(trueCloudV3CustomPhoneLabelView)
        }

        viewModel.onUpdatePhoneLabelView.observe(viewLifecycleOwner) {
            val trueCloudV3CustomPhoneLabelView =
                binding.trueCloudCustomLabelContainer.findViewWithTag<TrueCloudV3CustomPhoneLabelView>(
                    it?.tagId
                )
            trueCloudV3CustomPhoneLabelView.updateCustomPhoneLabelModel(it)
        }

        viewModel.onLaunchPickMedia.observe(viewLifecycleOwner) {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
    }

    private fun showConfirmDialogDelete() {
        DialogManager.getBottomSheetDialog(
            context = requireContext(),
            icon = DialogIconType.DELETE,
            title = getString(R.string.true_cloudv3_would_you_like_to_delete_this_contact),
            subTitle = getString(R.string.true_cloudv3_if_you_delete_this_contact)
        ) {
            setSecondaryButton(R.string.cancel) { dialog ->
                dialog.dismiss()
            }
            setPrimaryButton(R.string.true_cloudv3_button_confirm) {
                it.dismiss()
                viewModel.onClickConfirmDeleteContact()
            }
        }.show(childFragmentManager)
    }

    private fun addCustomLabelView(customphoneLabelModel: CustomPhoneLabelModel) {
        binding.trueCloudCustomLabelContainer.addView(
            TrueCloudV3CustomPhoneLabelView(
                requireContext(),
                customPhoneLabelModel = customphoneLabelModel,
                onFocusOut = { customphoneLabelModel ->
                    viewModel.onEditTextNumberFocusOut(customphoneLabelModel)
                }
            ).apply {
                setOnClickLabel { customphoneLabelModel ->
                    viewModel.onClickLabel(customphoneLabelModel.tagId)
                }
                setOnClickRemove { customphoneLabelModel ->
                    viewModel.onClickRemove(customphoneLabelModel)
                }
            }
        )
    }
}
