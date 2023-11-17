package com.truedigital.features.truecloudv3.presentation

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.truedigital.common.share.componentv3.extension.setSavedStateHandle
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_PHOTO_EDITOR_TEXT
import com.truedigital.features.truecloudv3.databinding.TrueCloudv3PhotoEditorTextInputDialogBinding
import com.truedigital.foundation.extension.onClick

class PhotoEditorTextInputDialogFragment :
    DialogFragment(R.layout.true_cloudv3_photo_editor_text_input_dialog) {

    private val binding by viewBinding(TrueCloudv3PhotoEditorTextInputDialogBinding::bind)

    init {
        setStyle(STYLE_NO_TITLE, com.truedigital.common.share.componentv3.R.style.FullNoBackgroundDialog)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = object : Dialog(requireContext(), theme) {
            override fun onBackPressed() {
                findNavController().setSavedStateHandle(
                    KEY_TRUE_CLOUD_PHOTO_EDITOR_TEXT,
                    null,
                )
                dismiss()
            }
        }
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
    }

    private fun initView() = with(binding) {
        discardImageView.onClick {
            findNavController().setSavedStateHandle(
                KEY_TRUE_CLOUD_PHOTO_EDITOR_TEXT,
                null,
            )
            dismiss()
        }
        confirmImageView.onClick {
            findNavController().setSavedStateHandle(
                KEY_TRUE_CLOUD_PHOTO_EDITOR_TEXT,
                textEditText.text?.toString().orEmpty(),
            )
            dismiss()
        }
        binding.textEditText.requestFocus()
        val inputMethodManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        inputMethodManager?.showSoftInput(
            binding.textEditText,
            InputMethodManager.SHOW_FORCED,
        )
    }
}
