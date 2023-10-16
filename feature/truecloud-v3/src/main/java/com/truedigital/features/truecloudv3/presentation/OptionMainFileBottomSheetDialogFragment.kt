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
import com.tdg.truecloud.databinding.TrueCloudv3MoreOptionBottomSheetDialogBinding
import com.truedigital.common.share.componentv3.extension.getSavedStateHandle
import com.truedigital.common.share.componentv3.extension.setSavedStateHandle
import com.truedigital.component.dialog.trueid.DialogIconType
import com.truedigital.component.dialog.trueid.DialogManager
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.truecloudv3.common.SortType
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_SORT_TYPE
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_TRASH
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_OPTION_MAIN_FILE_ADD_NEW_FOLDER
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_OPTION_MAIN_FILE_EMPTY_TRASH
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_OPTION_MAIN_FILE_SELECT_MODE
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_OPTION_MAIN_FILE_SORT_BY
import com.truedigital.features.truecloudv3.injections.TrueCloudV3Component
import com.truedigital.features.truecloudv3.presentation.viewmodel.OptionMainFileBottomSheetDialogViewModel
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject

class OptionMainFileBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private val binding by viewBinding(TrueCloudv3MoreOptionBottomSheetDialogBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: OptionMainFileBottomSheetDialogViewModel by viewModels { viewModelFactory }

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
            R.layout.true_cloudv3_more_option_bottom_sheet_dialog,
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
        initListener()
        observeViewModel()
        arguments?.apply {
            getParcelable<SortType>(KEY_BUNDLE_TRUE_CLOUD_SORT_TYPE)?.let {
                viewModel.setSortType(it)
            }
            setTrashView(getBoolean(KEY_BUNDLE_TRUE_CLOUD_TRASH, false))
        }
    }

    private fun observeViewModel() {
        viewModel.onSortBy.observe(viewLifecycleOwner) { _selectedSortType ->
            findNavController()
                .setSavedStateHandle(KEY_TRUE_CLOUD_OPTION_MAIN_FILE_SORT_BY, _selectedSortType)
            findNavController().navigateUp()
        }
        viewModel.onCreateNewFolder.observe(viewLifecycleOwner) { _folderName ->
            findNavController().setSavedStateHandle(
                KEY_TRUE_CLOUD_OPTION_MAIN_FILE_ADD_NEW_FOLDER, _folderName
            )
            findNavController().navigateUp()
        }
        viewModel.onEmptyTrash.observe(viewLifecycleOwner) {
            findNavController().setSavedStateHandle(
                KEY_TRUE_CLOUD_OPTION_MAIN_FILE_EMPTY_TRASH,
                null
            )
            findNavController().navigateUp()
        }
        viewModel.onConfirmEmpty.observe(viewLifecycleOwner) {
            showConfirmDialogDelete()
        }
    }

    private fun initListener() {
        findNavController()
            .getSavedStateHandle<SortType>(KEY_TRUE_CLOUD_OPTION_MAIN_FILE_SORT_BY)
            ?.observe(
                viewLifecycleOwner
            ) { _selectedSortType ->
                viewModel.onReceiveSortBy(_selectedSortType)
            }
        findNavController()
            .getSavedStateHandle<String>(KEY_TRUE_CLOUD_OPTION_MAIN_FILE_ADD_NEW_FOLDER)
            ?.observe(
                viewLifecycleOwner
            ) { _folderName ->
                viewModel.onReceiveCreateFolder(_folderName)
            }
    }

    private fun initViews() = with(binding) {
        trueCloudSelectTextView.onClick {
            findNavController().setSavedStateHandle(
                KEY_TRUE_CLOUD_OPTION_MAIN_FILE_SELECT_MODE, true
            )
            findNavController().navigateUp()
        }
        trueCloudSortByTextView.onClick {
            viewModel.onClickSortBy()
        }
        trueCloudAddNewFolderTextView.onClick {
            viewModel.onClickCreateNewFolder()
        }
        trueCloudTrashTextView.onClick {
            viewModel.onConfirmEmpty()
        }
        collapseImageView.onClick {
            dismiss()
        }
    }

    private fun setTrashView(boolean: Boolean) {
        if (boolean) {
            binding.trueCloudTrashContainer.visible()
            binding.trueCloudAddNewFolderContainer.gone()
        } else {
            binding.trueCloudTrashContainer.gone()
            binding.trueCloudAddNewFolderContainer.visible()
        }
    }

    private fun showConfirmDialogDelete() {
        DialogManager.getBottomSheetDialog(
            context = requireContext(),
            icon = DialogIconType.DELETE,
            title = getString(R.string.true_cloudv3_empty_trash_title),
            subTitle = getString(R.string.true_cloudv3_empty_trash_desc)
        ) {
            setSecondaryButton(R.string.cancel) { dialog ->
                dialog.dismiss()
            }
            setPrimaryButton(R.string.true_cloudv3_button_confirm) {
                it.dismiss()
                viewModel.onClickEmptyTrash()
            }
        }.show(childFragmentManager)
    }
}
