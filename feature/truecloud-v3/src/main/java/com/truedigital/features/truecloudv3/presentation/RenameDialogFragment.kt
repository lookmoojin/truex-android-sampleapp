package com.truedigital.features.truecloudv3.presentation

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.tdg.truecloud.R
import com.tdg.truecloud.databinding.FragmentTrueCloudv3RenameFileBinding
import com.truedigital.common.share.componentv3.extension.setSavedStateHandle
import com.truedigital.core.extensions.viewBinding
import com.truedigital.core.utils.KeyboardUtils
import com.truedigital.features.truecloudv3.common.StorageType
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_OPTION_FILE_MODEL
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.features.truecloudv3.injections.TrueCloudV3Component
import com.truedigital.features.truecloudv3.presentation.viewmodel.RenameDialogViewModel
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject

class RenameDialogFragment : DialogFragment(R.layout.fragment_true_cloudv3_rename_file) {

    companion object {
        private const val DOT_SPLIT = "."
        private const val FIRST_INDEX = 0
    }

    private val binding by viewBinding(FragmentTrueCloudv3RenameFileBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: RenameDialogViewModel by viewModels { viewModelFactory }

    init {
        setStyle(STYLE_NORMAL, R.style.FullNoBackgroundDialog)
    }

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
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeViewModel()
        arguments?.apply {
            getParcelable<TrueCloudFilesModel.File>(KEY_BUNDLE_TRUE_CLOUD_OPTION_FILE_MODEL)?.let {
                viewModel.onViewCreated(it)
            }
        }
    }

    private fun initViews() = with(binding) {
        trueCloudCancelTextView.onClick {
            dismiss()
        }
        trueCloudRenameTextView.onClick {
            val fileName = trueCloudNameTextInputLayout.editText?.text.toString()
            viewModel.onClickRename(fileName)
        }
    }

    private fun observeViewModel() {
        viewModel.onSetUpView.observe(viewLifecycleOwner) { _trueCloudFilesModel ->
            _trueCloudFilesModel.name?.let { name ->
                binding.trueCloudNameTextInputLayout.editText?.setText(name)
                if (StorageType.FILE.type.equals(_trueCloudFilesModel.objectType)) {
                    val lastIndex = name.lastIndexOf(DOT_SPLIT)
                    binding.trueCloudNameTextInputEditText.setSelection(FIRST_INDEX, lastIndex)
                } else {
                    binding.trueCloudNameTextInputEditText.setSelectAllOnFocus(true)
                }
            }
            binding.trueCloudNameTextInputLayout.requestFocus()
            KeyboardUtils.forceKeyboard(binding.trueCloudNameTextInputLayout, true)
        }
        viewModel.onSetFileName.observe(viewLifecycleOwner) { _frueCloudFilesModel ->
            findNavController()
                .setSavedStateHandle(
                    TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_OPTION_FILE_RENAME, _frueCloudFilesModel
                )
        }
    }
}
