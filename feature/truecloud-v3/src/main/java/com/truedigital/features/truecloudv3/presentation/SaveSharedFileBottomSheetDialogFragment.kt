package com.truedigital.features.truecloudv3.presentation

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tdg.truecloud.R
import com.tdg.truecloud.databinding.TrueCloudv3SaveSharedFileBottomSheetDialogBinding
import com.truedigital.common.share.componentv3.extension.setSavedStateHandle
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_FILE_ACCESSTOKEN
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_SHARED_FILE
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey
import com.truedigital.features.truecloudv3.injections.TrueCloudV3Component
import com.truedigital.features.truecloudv3.presentation.viewmodel.SaveSharedFileBottomSheetDialogViewModel
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject

class SaveSharedFileBottomSheetDialogFragment : BottomSheetDialogFragment() {
    private val binding by viewBinding(TrueCloudv3SaveSharedFileBottomSheetDialogBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: SaveSharedFileBottomSheetDialogViewModel by viewModels { viewModelFactory }

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
            R.layout.true_cloudv3_save_shared_file_bottom_sheet_dialog,
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
        observeViewModel()
        arguments?.apply {
            getString(KEY_BUNDLE_TRUE_CLOUD_SHARED_FILE)?.let {
                viewModel.setId(it)
            }
            getString(KEY_BUNDLE_TRUE_CLOUD_FILE_ACCESSTOKEN)?.let {
                viewModel.setToken(it)
            }
        }
    }

    private fun initViews() = with(binding) {
        collapseImageView.onClick {
            dismiss()
            onCloseDialog()
        }
        saveToCloudContainer.onClick {
            viewModel.onSaveToCloudClick()
        }
        saveToDeviceContainer.onClick {
            viewModel.onSaveToDeviceClick()
        }
    }

    private fun observeViewModel() {
        viewModel.onSaveToDevice.observe(viewLifecycleOwner) {
            findNavController()
                .setSavedStateHandle(
                    TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_SAVE_TO_DEVICE,
                    it
                )
            findNavController().navigateUp()
        }
        viewModel.onSaveToCloud.observe(viewLifecycleOwner) {
            findNavController()
                .setSavedStateHandle(
                    TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_SAVE_TO_CLOUD,
                    it
                )
            findNavController().navigateUp()
        }
    }

    private fun onCloseDialog() {
        findNavController()
            .setSavedStateHandle(
                TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_CLOSE_SHARE,
                true
            )
        findNavController().navigateUp()
    }
}
