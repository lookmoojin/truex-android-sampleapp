package com.truedigital.common.share.componentv3.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.truedigital.common.share.componentv3.R
import com.truedigital.common.share.componentv3.databinding.CommonBottomDialogBinding
import com.truedigital.core.extensions.viewBinding
import com.truedigital.foundation.extension.onClick

class CommonBottomDialogFragment : BottomSheetDialogFragment() {

    interface CommonBottomDialogListener {
        fun onConfirmClicked()
        fun onCancelClicked()
    }

    companion object {
        val TAG = CommonBottomDialogFragment::class.java.canonicalName as String

        const val DIALOG_TITLE = "dialogTitle"
        const val DIALOG_CONTENT = "dialogContent"
        const val DIALOG_ICON = "dialogIcon"
        const val CONFIRM_BUTTON_TEXT = "confirmButtonText"
        const val CANCEL_BUTTON_TEXT = "cancelButtonText"

        private var dialogTitle: String? = null
        private var dialogContent: String? = null
        private var dialogIcon: Int? = null
        private var confirmButtonText: String? = null
        private var cancelButtonText: String? = null

        fun newInstance(
            dialogTitle: String,
            dialogContent: String,
            dialogIcon: Int,
            confirmButtonText: String,
            cancelButtonText: String
        ): CommonBottomDialogFragment {
            return CommonBottomDialogFragment().apply {
                arguments = Bundle().apply {
                    putString(DIALOG_TITLE, dialogTitle)
                    putString(DIALOG_CONTENT, dialogContent)
                    putString(CONFIRM_BUTTON_TEXT, confirmButtonText)
                    putString(CANCEL_BUTTON_TEXT, cancelButtonText)
                    putInt(DIALOG_ICON, dialogIcon)
                }
            }
        }
    }
    private val binding by viewBinding(CommonBottomDialogBinding::bind)
    private var genericDialogListener: CommonBottomDialogListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        arguments?.let { bundle ->
            restoreArguments(bundle)
        }
        return inflater.inflate(
            R.layout.common_bottom_dialog,
            container,
            false
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialog)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!dialogTitle.isNullOrBlank()) {
            binding.commonBottomDialogTextviewTitle.text = dialogTitle
        }
        if (!dialogContent.isNullOrBlank()) {
            binding.commonBottomDialogTextviewContent.text = dialogContent
        }
        if (!confirmButtonText.isNullOrBlank()) {
            binding.commonBottomDialogButtonConfirm.text = confirmButtonText
        }
        if (!cancelButtonText.isNullOrBlank()) {
            binding.commonBottomDialogButtonCancel.text = cancelButtonText
        }

        dialogIcon?.let {
            binding.commonBottomDialogImageviewIcon.setImageDrawable(AppCompatResources.getDrawable(requireContext(), it))
        }

        binding.commonBottomDialogButtonConfirm.onClick {
            genericDialogListener?.onConfirmClicked()
            dismiss()
        }
        binding.commonBottomDialogButtonCancel.onClick {
            genericDialogListener?.onCancelClicked()
            dismiss()
        }
    }

    private fun restoreArguments(bundle: Bundle) {
        dialogTitle = bundle.getString(DIALOG_TITLE)
        dialogContent = bundle.getString(DIALOG_CONTENT)
        dialogIcon = bundle.getInt(DIALOG_ICON)
        confirmButtonText = bundle.getString(CONFIRM_BUTTON_TEXT)
        cancelButtonText = bundle.getString(CANCEL_BUTTON_TEXT)
    }

    fun setCommonBottomDialogListener(dialogListener: CommonBottomDialogListener) {
        genericDialogListener = dialogListener
    }
}
