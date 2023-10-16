package com.truedigital.features.truecloudv3.presentation

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tdg.truecloud.R
import com.tdg.truecloud.databinding.TrueCloudv3EditContactBottomSheetDialogBinding
import com.truedigital.common.share.componentv3.extension.getSavedStateHandle
import com.truedigital.common.share.componentv3.extension.setSavedStateHandle
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_CONTACT_EDIT_PHONE_LABEL_DATA
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_CONTACT_CUSTOM_PHONE_LABEL
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_CONTACT_SELECTED_PHONE_LABEL
import com.truedigital.features.truecloudv3.databinding.TrueCloudv3EditContactBottomSheetDialogBinding
import com.truedigital.features.truecloudv3.domain.model.ContactTrueCloudModel
import com.truedigital.features.truecloudv3.domain.model.CustomPhoneLabelModel
import com.truedigital.features.truecloudv3.extension.underline
import com.truedigital.features.truecloudv3.injections.TrueCloudV3Component
import com.truedigital.features.truecloudv3.presentation.viewmodel.CreateContactSelectLabelBottomSheetDialogViewModel
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject

class CreateContactSelectLabelBottomSheetDialogFragment : BottomSheetDialogFragment() {

    companion object {
        private const val EMPTY_LABEL = ""
    }

    private val binding by viewBinding(TrueCloudv3EditContactBottomSheetDialogBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: CreateContactSelectLabelBottomSheetDialogViewModel by viewModels {
        viewModelFactory
    }

    override fun onAttach(context: Context) {
        TrueCloudV3Component.getInstance().inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.true_cloudv3_edit_contact_bottom_sheet_dialog,
            container,
            false
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setOnShowListener {
                val parentLayout = findViewById<View>(R.id.design_bottom_sheet) as FrameLayout
                parentLayout.let { bottomSheet ->
                    bottomSheet.setBackgroundResource(R.color.transparent)
                    val behaviour = BottomSheetBehavior.from(bottomSheet)
                    behaviour.isDraggable = true
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        observeViewModel()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(
                KEY_BUNDLE_TRUE_CLOUD_CONTACT_EDIT_PHONE_LABEL_DATA,
                CustomPhoneLabelModel::class.java
            )?.let { model ->
                viewModel.setPhoneLabel(model)
            }
        } else {
            arguments?.getParcelable<CustomPhoneLabelModel>(
                KEY_BUNDLE_TRUE_CLOUD_CONTACT_EDIT_PHONE_LABEL_DATA
            )
                ?.let { model ->
                    viewModel.setPhoneLabel(model)
                }
        }
    }

    override fun onResume() {
        super.onResume()
        findNavController().getSavedStateHandle<CustomPhoneLabelModel>(
            KEY_TRUE_CLOUD_CONTACT_CUSTOM_PHONE_LABEL
        )
            ?.observe(
                viewLifecycleOwner
            ) { _customPhoneLabelModel ->
                viewModel.onCompleteCustomLabel(_customPhoneLabelModel)
            }
    }

    override fun onPause() {
        super.onPause()
        findNavController().previousBackStackEntry?.savedStateHandle?.remove<ContactTrueCloudModel>(
            KEY_TRUE_CLOUD_CONTACT_CUSTOM_PHONE_LABEL
        )
    }

    private fun observeViewModel() {
        viewModel.onSetupView.observe(viewLifecycleOwner) { _customPhoneLabelModel ->
            when (_customPhoneLabelModel.label) {
                getString(R.string.true_cloudv3_dialog_no_label),
                EMPTY_LABEL -> {
                    underlineSelectedLabel(binding.trueCloudNoLabelTextView)
                }
                getString(R.string.true_cloudv3_dialog_mobile) -> {
                    underlineSelectedLabel(binding.trueCloudMobileTextView)
                }
                getString(R.string.true_cloudv3_dialog_work) -> {
                    underlineSelectedLabel(binding.trueCloudWorkTextView)
                }
                getString(R.string.true_cloudv3_dialog_home) -> {
                    underlineSelectedLabel(binding.trueCloudHomeTextView)
                }
                getString(R.string.true_cloudv3_dialog_work_fax) -> {
                    underlineSelectedLabel(binding.trueCloudWorkFaxTextView)
                }
                getString(R.string.true_cloudv3_dialog_home_fax) -> {
                    underlineSelectedLabel(binding.trueCloudHomeFaxTextView)
                }
                getString(R.string.true_cloudv3_dialog_main) -> {
                    underlineSelectedLabel(binding.trueCloudMainTextView)
                }
                getString(R.string.true_cloudv3_dialog_other) -> {
                    underlineSelectedLabel(binding.trueCloudOtherTextView)
                }
                else -> {
                    underlineSelectedLabel(binding.trueCloudCustomTextView)
                }
            }
        }

        viewModel.onSelected.observe(viewLifecycleOwner) { _customPhoneLabelModel ->
            findNavController().setSavedStateHandle(
                KEY_TRUE_CLOUD_CONTACT_SELECTED_PHONE_LABEL,
                _customPhoneLabelModel
            )
            findNavController().navigateUp()
        }
    }

    private fun underlineSelectedLabel(textLabel: AppCompatTextView) {
        textLabel.underline()
        textLabel.setTextColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.black
            )
        )
    }

    private fun initViews() = with(binding) {
        trueCloudNoLabelTextView.onClick {
            viewModel.onClickLabel(EMPTY_LABEL)
        }
        trueCloudMobileTextView.onClick {
            viewModel.onClickLabel(getString(R.string.true_cloudv3_dialog_mobile))
        }
        trueCloudHomeTextView.onClick {
            viewModel.onClickLabel(getString(R.string.true_cloudv3_dialog_home))
        }
        trueCloudWorkTextView.onClick {
            viewModel.onClickLabel(getString(R.string.true_cloudv3_dialog_work))
        }
        trueCloudMainTextView.onClick {
            viewModel.onClickLabel(getString(R.string.true_cloudv3_dialog_main))
        }
        trueCloudWorkFaxTextView.onClick {
            viewModel.onClickLabel(getString(R.string.true_cloudv3_dialog_work_fax))
        }
        trueCloudHomeFaxTextView.onClick {
            viewModel.onClickLabel(getString(R.string.true_cloudv3_dialog_home_fax))
        }
        trueCloudOtherTextView.onClick {
            viewModel.onClickLabel(getString(R.string.true_cloudv3_dialog_other))
        }
        trueCloudCustomTextView.onClick {
            viewModel.onClickCustom()
        }
        collapseImageView.onClick {
            findNavController().navigateUp()
        }
    }
}
