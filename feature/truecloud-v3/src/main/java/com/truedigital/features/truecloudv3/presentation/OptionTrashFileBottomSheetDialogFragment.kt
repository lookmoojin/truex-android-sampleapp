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
import com.tdg.truecloud.databinding.TrueCloudv3TrashOptionBottomSheetDialogBinding
import com.truedigital.common.share.componentv3.extension.setSavedStateHandle
import com.truedigital.component.dialog.trueid.DialogIconType
import com.truedigital.component.dialog.trueid.DialogManager
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_OPTION_MAIN_TRASH
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.features.truecloudv3.injections.TrueCloudV3Component
import com.truedigital.features.truecloudv3.presentation.viewmodel.OptionTrashFileBottomSheetDialogViewModel
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject

class OptionTrashFileBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private val binding by viewBinding(TrueCloudv3TrashOptionBottomSheetDialogBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: OptionTrashFileBottomSheetDialogViewModel by viewModels { viewModelFactory }

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
            R.layout.true_cloudv3_trash_option_bottom_sheet_dialog,
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
        val itemList: ArrayList<TrueCloudFilesModel.File> =
            arguments?.getParcelableArrayList(KEY_BUNDLE_TRUE_CLOUD_OPTION_MAIN_TRASH)
                ?: ArrayList()
        viewModel.init(
            itemList = itemList
        )
    }

    private fun initViews() = with(binding) {
        trueCloudDeleteTextView.onClick {
            viewModel.onClickDelete()
        }
        trueCloudRestoreTextView.onClick {
            viewModel.onClickRestore()
        }
        collapseImageView.onClick {
            dismiss()
        }
    }

    private fun initListener() {
        // no
    }

    private fun observeViewModel() {
        viewModel.clickDelete.observe(viewLifecycleOwner) {
            findNavController().setSavedStateHandle(
                TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_OPTION_FILE_PERMANENCE_DELETE,
                it
            )
            findNavController().navigateUp()
        }

        viewModel.onConfirmDelete.observe(viewLifecycleOwner) {
            showConfirmDialogDelete()
        }
        viewModel.onRestore.observe(viewLifecycleOwner) {
            findNavController().setSavedStateHandle(
                TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_OPTION_FILE_RESTORE,
                it
            )
            findNavController().navigateUp()
        }
    }

    private fun showConfirmDialogDelete() {
        DialogManager.getBottomSheetDialog(
            context = requireContext(),
            icon = DialogIconType.DELETE,
            title = getString(R.string.true_cloudv3_empty_trash_delete_permanent_title),
            subTitle = getString(R.string.true_cloudv3_empty_trash_delete_permanent_desc)
        ) {
            setSecondaryButton(R.string.cancel) { dialog ->
                dialog.dismiss()
            }
            setPrimaryButton(R.string.true_cloudv3_button_confirm) {
                it.dismiss()
                viewModel.deleteSelectedItem()
            }
        }.show(childFragmentManager)
    }
}
