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
import com.tdg.truecloud.databinding.FragmentTrueCloudv3BottomsheetMigratedBinding
import com.truedigital.core.extensions.viewBinding
import com.truedigital.foundation.extension.onClick

class MigratedBottomSheetDialogFragment(private val onMigratedClick: OnMigratedClick) :
    BottomSheetDialogFragment() {
    private val binding by viewBinding(FragmentTrueCloudv3BottomsheetMigratedBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(
            R.layout.fragment_true_cloudv3_bottomsheet_migrated,
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
        trueCloudCancelTextView.onClick {
            dismiss()
        }
        trueCloudOpenAllfileTextView.onClick {
            dismiss()
            onMigratedClick.onAllFile()
        }
    }

    interface OnMigratedClick {
        fun onAllFile()
    }
}
