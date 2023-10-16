package com.truedigital.features.truecloudv3.presentation

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tdg.truecloud.R
import com.tdg.truecloud.databinding.TrueCloudv3ContactBottomSheetDialogBinding
import com.truedigital.common.share.componentv3.extension.setSavedStateHandle
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey
import com.truedigital.foundation.extension.onClick

class CreateOptionContactBottomSheetDialogFragment : BottomSheetDialogFragment() {
    private val binding by viewBinding(TrueCloudv3ContactBottomSheetDialogBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.true_cloudv3_contact_bottom_sheet_dialog, container, false)
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
        trueCloudDeleteAllContactsTextView.onClick {
            findNavController().setSavedStateHandle(
                TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_CONTACT_DELETE_ALL, true
            )
            findNavController().navigateUp()
        }
        trueCloudExportToDeviceTextView.onClick {
            findNavController().setSavedStateHandle(
                TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_CONTACT_EXPORT_TO_DEVICE, true
            )
            findNavController().navigateUp()
        }
        collapseImageView.onClick {
            findNavController().navigateUp()
        }
    }
}
