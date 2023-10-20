package com.truedigital.features.truecloudv3.presentation

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.databinding.TrueCloudv3CreateNewBottomSheetDialogBinding
import com.truedigital.core.extensions.viewBinding
import com.truedigital.foundation.extension.onClick

class CreateNewBottomSheetDialogFragment(private val onCreateFolderClick: OnCreateFolderClick) :
    BottomSheetDialogFragment() {
    private val binding by viewBinding(TrueCloudv3CreateNewBottomSheetDialogBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.true_cloudv3_create_new_bottom_sheet_dialog,
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
                    behaviour.isDraggable = false
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() = with(binding) {
        trueCloudAddFolderTextView.onClick {
            dismiss()
            onCreateFolderClick.onCreateFolder()
        }
        trueCloudUploadTextView.onClick {
            dismiss()
            onCreateFolderClick.onUpload()
        }
    }

    interface OnCreateFolderClick {
        fun onCreateFolder()
        fun onUpload()
    }
}
