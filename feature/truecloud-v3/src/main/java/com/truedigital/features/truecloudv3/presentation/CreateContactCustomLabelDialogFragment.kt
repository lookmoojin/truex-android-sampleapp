package com.truedigital.features.truecloudv3.presentation

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.truedigital.common.share.componentv3.extension.setSavedStateHandle
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_CONTACT_PHONE_LABEL_DATA
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_CONTACT_CUSTOM_PHONE_LABEL
import com.truedigital.features.truecloudv3.databinding.FragmentTrueCloudv3CreateLabelBinding
import com.truedigital.features.truecloudv3.domain.model.CustomPhoneLabelModel
import com.truedigital.features.truecloudv3.injections.TrueCloudV3Component
import com.truedigital.features.truecloudv3.presentation.viewmodel.CreateContactCustomLabelDialogViewModel
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject

class CreateContactCustomLabelDialogFragment :
    DialogFragment(R.layout.fragment_true_cloudv3_create_label) {

    init {
        setStyle(STYLE_NORMAL, com.truedigital.common.share.componentv3.R.style.FullNoBackgroundDialog)
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: CreateContactCustomLabelDialogViewModel by viewModels { viewModelFactory }
    private val binding by viewBinding(FragmentTrueCloudv3CreateLabelBinding::bind)

    override fun onAttach(context: Context) {
        TrueCloudV3Component.getInstance().inject(this)
        super.onAttach(context)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = object : Dialog(requireContext(), theme) {
            override fun onBackPressed() {
                dismiss()
            }
        }
        dialog.getWindow()?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeViewModel()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(
                KEY_BUNDLE_TRUE_CLOUD_CONTACT_PHONE_LABEL_DATA,
                CustomPhoneLabelModel::class.java
            )?.let { model ->
                viewModel.setPhoneLabel(model)
            }
        } else {
            arguments?.getParcelable<CustomPhoneLabelModel>(
                KEY_BUNDLE_TRUE_CLOUD_CONTACT_PHONE_LABEL_DATA
            )
                ?.let { model ->
                    viewModel.setPhoneLabel(model)
                }
        }
    }

    private fun observeViewModel() {
        viewModel.onLableCustomFinish.observe(viewLifecycleOwner) { _customPhoneLabelModel ->
            findNavController().setSavedStateHandle(
                KEY_TRUE_CLOUD_CONTACT_CUSTOM_PHONE_LABEL,
                _customPhoneLabelModel
            )
            findNavController().navigateUp()
        }
    }

    private fun initViews() = with(binding) {
        trueCloudCancelTextView.onClick {
            dismiss()
        }
        trueCloudCreateTextView.onClick {
            val labelName = trueCloudNewLabelTextInputLayout.editText?.text.toString()
            viewModel.onClickCreateLabel(labelName)
        }
    }
}
