package com.truedigital.features.truecloudv3.presentation

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tdg.truecloud.R
import com.tdg.truecloud.databinding.FragmentTrueCloudv3CancelMigrateDialogBinding
import com.truedigital.core.extensions.viewBinding
import com.truedigital.foundation.extension.onClick

class CreateMigrationBottomSheetCancelDialogFragment(private val onMigrateCancelClick: OnMigrateCancelClick) :
    BottomSheetDialogFragment() {
    private val binding by viewBinding(FragmentTrueCloudv3CancelMigrateDialogBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_true_cloudv3_cancel_migrate_dialog, container, false)
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
        trueCloudDismissTextView.onClick {
            dismiss()
        }
        trueCloudCancelItgTextView.onClick {
            dismiss()
            onMigrateCancelClick.onCancelIt()
        }
    }

    interface OnMigrateCancelClick {
        fun onCancelIt()
    }
}
