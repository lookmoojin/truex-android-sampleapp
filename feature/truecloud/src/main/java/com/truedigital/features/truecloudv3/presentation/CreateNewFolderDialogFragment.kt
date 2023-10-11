package com.truedigital.features.truecloudv3.presentation

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.truedigital.common.share.componentv3.extension.setSavedStateHandle
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey
import com.truedigital.features.truecloudv3.databinding.FragmentTrueCloudv3AddNewFolderBinding
import com.truedigital.foundation.extension.onClick

class CreateNewFolderDialogFragment :
    DialogFragment(R.layout.fragment_true_cloudv3_add_new_folder) {

    init {
        setStyle(STYLE_NORMAL, R.style.FullNoBackgroundDialog)
    }

    private val binding by viewBinding(FragmentTrueCloudv3AddNewFolderBinding::bind)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(requireContext(), theme) {
            override fun onBackPressed() {
                dismiss()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() = with(binding) {
        trueCloudCancelTextView.onClick {
            dismiss()
        }
        trueCloudCreateFolderTextView.onClick {
            val folderName = trueCloudNameTextInputLayout.editText?.text.toString().trim()
            if (folderName.isNotEmpty()) {
                findNavController().setSavedStateHandle(
                    TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_OPTION_MAIN_FILE_ADD_NEW_FOLDER,
                    folderName
                )
                dismiss()
            }
        }
    }
}
