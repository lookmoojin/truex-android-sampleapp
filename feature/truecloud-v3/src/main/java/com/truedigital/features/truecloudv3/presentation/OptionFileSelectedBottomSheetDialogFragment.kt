package com.truedigital.features.truecloudv3.presentation

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.truedigital.common.share.componentv3.extension.getSavedStateHandle
import com.truedigital.common.share.componentv3.extension.setSavedStateHandle
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_FILE_SELECTED
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey
import com.truedigital.features.truecloudv3.databinding.TrueCloudv3FileSelectedOptionBottomSheetDialogBinding
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.features.truecloudv3.injections.TrueCloudV3Component
import com.truedigital.features.truecloudv3.presentation.viewmodel.OptionFileSelectedBottomSheetDialogViewModel
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject

class OptionFileSelectedBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private val binding by viewBinding(TrueCloudv3FileSelectedOptionBottomSheetDialogBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: OptionFileSelectedBottomSheetDialogViewModel by viewModels { viewModelFactory }

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
            R.layout.true_cloudv3_file_selected_option_bottom_sheet_dialog,
            container,
            false
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setOnShowListener {
                val parentLayout = findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
                parentLayout.let { bottomSheet ->
                    bottomSheet.setBackgroundResource(com.truedigital.component.R.color.transparent)
                    val behaviour = BottomSheetBehavior.from(bottomSheet)
                    behaviour.isDraggable = false
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initListener()
        observeViewModel()
        val folderId =
            arguments?.getString(TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_FOLDER_ID).orEmpty()
        val itemList: ArrayList<TrueCloudFilesModel.File> =
            arguments?.getParcelableArrayList(KEY_BUNDLE_TRUE_CLOUD_FILE_SELECTED) ?: ArrayList()
        val categoryName =
            arguments?.getString(TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_CATEGORY).orEmpty()
        viewModel.init(
            itemList = itemList,
            folderId = folderId,
            categoryType = categoryName
        )
    }

    private fun initViews() = with(binding) {
        trueCloudTextViewMoveTo.onClick {
            viewModel.onClickMoveTo()
        }
        trueCloudCopyToTextView.onClick {
            viewModel.onClickCopy()
        }
        trueCloudDeleteTextView.onClick {
            viewModel.deleteSelectedItem()
        }
        collapseImageView.onClick {
            dismiss()
        }
    }

    private fun initListener() {
        findNavController()
            .getSavedStateHandle<Boolean>(
                TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_FILE_LOCATOR
            )
            ?.observe(
                viewLifecycleOwner
            ) {
                viewModel.onSetDisableSelectFileMode(it)
            }
    }

    private fun observeViewModel() {
        viewModel.onDisableSelectFileMode.observe(viewLifecycleOwner) { boolean ->
            findNavController().setSavedStateHandle(
                TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_FILE_LOCATOR,
                boolean
            )
            findNavController().navigateUp()
        }

        viewModel.clickDelete.observe(viewLifecycleOwner) {
            findNavController().setSavedStateHandle(
                TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_OPTION_FILE_GROUP_DELETE,
                it
            )
            findNavController().navigateUp()
        }

        viewModel.onCheckCategoryType.observe(viewLifecycleOwner) { isAllFile ->
            binding.trueCloudMoveToContainer.isVisible = isAllFile
            binding.trueCloudCopyToContainer.isVisible = isAllFile
        }
    }
}
