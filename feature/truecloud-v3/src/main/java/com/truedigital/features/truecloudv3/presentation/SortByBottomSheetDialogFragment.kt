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
import com.tdg.truecloud.databinding.TrueCloudv3SortByBottomSheetDialogBinding
import com.truedigital.common.share.componentv3.extension.setSavedStateHandle
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.truecloudv3.common.SortType
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_SORT_TYPE
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey
import com.truedigital.foundation.extension.onClick

class SortByBottomSheetDialogFragment : BottomSheetDialogFragment() {
    var sortType = SortType.SORT_DATE_DESC

    private val binding by viewBinding(TrueCloudv3SortByBottomSheetDialogBinding::bind)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.true_cloudv3_sort_by_bottom_sheet_dialog, container, false)
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
        arguments?.apply {
            getParcelable<SortType>(KEY_BUNDLE_TRUE_CLOUD_SORT_TYPE)?.let {
                sortType = it
            }
        }
        initViews()
    }

    private fun initViews() = with(binding) {
        when (sortType) {
            SortType.SORT_DATE_ASC -> {
                trueCloudOldToNewRadioButton.isChecked = true
            }

            SortType.SORT_DATE_DESC -> {
                trueCloudNewToOldRadioButton.isChecked = true
            }

            SortType.SORT_NAME_ASC -> {
                trueCloudRadioAtoZRadioButton.isChecked = true
            }

            SortType.SORT_NAME_DESC -> {
                trueCloudZtoARadioButton.isChecked = true
            }

            SortType.SORT_SIZE_ASC -> {
                trueCloudSizeSmallToLargeRadioButton.isChecked = true
            }

            SortType.SORT_SIZE_DESC -> {
                trueCloudSizeLargeToSmallRadioButton.isChecked = true
            }
        }
        trueCloudSortByApplyButton.onClick {
            dismiss()
            checkRadioSelected()
        }
        collapseImageView.onClick {
            dismiss()
        }
    }

    private fun checkRadioSelected() {
        binding.apply {
            var selectedSortType = sortType
            when {
                trueCloudRadioAtoZRadioButton.isChecked -> selectedSortType = SortType.SORT_NAME_ASC
                trueCloudZtoARadioButton.isChecked -> selectedSortType = SortType.SORT_NAME_DESC
                trueCloudNewToOldRadioButton.isChecked -> selectedSortType = SortType.SORT_DATE_DESC
                trueCloudOldToNewRadioButton.isChecked -> selectedSortType = SortType.SORT_DATE_ASC
                trueCloudSizeLargeToSmallRadioButton.isChecked -> {
                    selectedSortType =
                        SortType.SORT_SIZE_DESC
                }

                trueCloudSizeSmallToLargeRadioButton.isChecked -> {
                    selectedSortType = SortType.SORT_SIZE_ASC
                }
            }
            findNavController().setSavedStateHandle(
                TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_OPTION_MAIN_FILE_SORT_BY, selectedSortType
            )
        }
    }
}
