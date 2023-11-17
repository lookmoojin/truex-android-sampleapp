package com.truedigital.features.truecloudv3.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.truedigital.common.share.componentv3.extension.getSavedStateHandle
import com.truedigital.common.share.componentv3.extension.setSavedStateHandle
import com.truedigital.component.base.BaseFragment
import com.truedigital.component.dialog.trueid.DialogIconType
import com.truedigital.component.dialog.trueid.DialogManager
import com.truedigital.component.dialog.trueid.DialogTopNavigationType
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.common.SortType
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey
import com.truedigital.features.truecloudv3.databinding.FragmentTrashTrueCloudV3Binding
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.features.truecloudv3.extension.snackBar
import com.truedigital.features.truecloudv3.injections.TrueCloudV3Component
import com.truedigital.features.truecloudv3.presentation.adapter.FilesAdapter
import com.truedigital.features.truecloudv3.presentation.viewmodel.FilesTrueCloudViewModel
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.visible
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject

class TrashTrueCloudV3Fragment : BaseFragment(R.layout.fragment_trash_true_cloud_v3) {

    companion object {
        private const val EMPTY_DECORATION_SIZE = 0
        private const val FIRST_INDEX = 0
        private const val DEFAULT_SPAN_COUNT_GRID = 2
    }

    private val binding by viewBinding(FragmentTrashTrueCloudV3Binding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: FilesTrueCloudViewModel by viewModels { viewModelFactory }

    @Suppress("TooManyFunctions")
    private val filesAdapter = FilesAdapter(object : FilesAdapter.OnActionClickListener {
        override fun onPauseClicked(model: TrueCloudFilesModel.Upload) {
            // do nothing
        }
        override fun onRetryClicked(model: TrueCloudFilesModel.Upload) {
            // do nothing
        }
        override fun onCancelClicked(model: TrueCloudFilesModel.Upload) {
            // do nothing
        }
        override fun onFileClicked(model: TrueCloudFilesModel.File) {
            viewModel.onClickTrashFileOption(model)
        }
        override fun onMoreClicked(model: TrueCloudFilesModel.File) {
            viewModel.onClickTrashFileOption(model)
        }
        override fun onLongClicked(model: TrueCloudFilesModel.File) {
            // do nothing
        }
        override fun onCancelAllClicked() {
            // do nothing
        }
        override fun onUploadExpandClicked(status: Boolean) {
            // do nothing
        }
        override fun onFolderClicked(model: TrueCloudFilesModel.Folder) {
            viewModel.onClickTrashFolderOption(model)
        }
        override fun onFolderMoreClicked(model: TrueCloudFilesModel.Folder) {
            viewModel.onClickTrashFolderOption(model)
        }
        override fun onAutoBackupExpandClicked(status: Boolean) {
            // do nothing
        }
        override fun onCancelAllBackupClicked() {
            // do nothing
        }
        override fun onPauseAllBackupClicked() {
            // do nothing
        }
        override fun onResumeAllBackupClicked() {
            // do nothing
        }
    })

    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            viewModel.onScrolled(dy, recyclerView.layoutManager as? GridLayoutManager)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        TrueCloudV3Component.getInstance().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val folderId = arguments?.getString(TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_FOLDER_ID).orEmpty()
        viewModel.onGetTrashData(folderId)
        initViews()
        initListener()
        observeViewModel()
    }

    private fun initViews() = with(binding) {
        trueCloudFilesRecyclerView.apply {
            layoutManager = GridLayoutManager(
                context,
                DEFAULT_SPAN_COUNT_GRID,
                GridLayoutManager.VERTICAL,
                false
            ).apply {
                spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int {
                        val isShowHorizontalLayout = filesAdapter.isShowHorizontalLayout
                        return viewModel.getSpanSize(position, isShowHorizontalLayout)
                    }
                }
            }
            adapter = filesAdapter
            addOnScrollListener(onScrollListener)
        }
        trueCloudHeaderTitle.apply {
            setUploadViewVisibility(false)
            setTitle(getString(R.string.true_cloudv3_menu_trash))
            setOnClickBack { fragmentPopBackStack() }
            setOnClickChangeLayout { viewModel.onClickChangeLayout() }
            setOnClickMoreOption {
                viewModel.onClickHeaderTrashMoreOption(true)
            }
        }
        trueCloudHeaderSelect.apply {
            setTitle(getString(R.string.true_cloudv3_d_selected, 0))
            setOnClickClose {
                viewModel.closeSelectedItem()
            }
            setOnClickSelectAll {
                viewModel.selectAllItem()
            }
            setOnClickSelectOption {
                viewModel.onClickSelectOption(true)
            }
        }
    }

    private fun observeViewModel() {
        binding.apply {
            viewModel.onSetFileList.observe(viewLifecycleOwner) { _fileList ->
                filesAdapter.setTrashMode()
                filesAdapter.refreshItemList(_fileList)
            }

            viewModel.onAddFileList.observe(viewLifecycleOwner) { _list ->
                filesAdapter.addFiles(_list)
            }

            viewModel.onRefreshData.observe(viewLifecycleOwner) { _list ->
                filesAdapter.refreshItemList(_list)
            }

            viewModel.onLoadCoverFinished.observe(viewLifecycleOwner) {
                filesAdapter.notifyId()
            }

            viewModel.onTaskUpdateStatusType.observe(viewLifecycleOwner) {
                filesAdapter.setUpdateFileAtIndex(it.first, it.second)
            }

            viewModel.onSelectUpdate.observe(viewLifecycleOwner) {
                trueCloudHeaderSelect.setTitle(getString(R.string.true_cloudv3_d_selected, it))
            }

            viewModel.onSelectAll.observe(viewLifecycleOwner) {
                trueCloudHeaderSelect.setSelectAll(it)
                refreshRecyclerView()
            }
            viewModel.onCloseSelectAll.observe(viewLifecycleOwner) {
                trueCloudHeaderSelect.visibility = View.GONE
                trueCloudHeaderTitle.visibility = View.VISIBLE
                filesAdapter.closeMultipleSelect()
                refreshRecyclerView()
            }

            viewModel.onDeleteFileSuccess.observe(viewLifecycleOwner) { model ->
                filesAdapter.deleteObject(model)
            }

            viewModel.onClearList.observe(viewLifecycleOwner) {
                filesAdapter.refreshItemList(it)
            }
            viewModel.onAddDecoration.observe(viewLifecycleOwner) { _filesDecoration ->
                trueCloudFilesRecyclerView.addItemDecoration(_filesDecoration)
                filesAdapter.run {
                    isShowHorizontalLayout = false
                    notifyItemRangeChanged(FIRST_INDEX, filesAdapter.itemCount)
                }
            }

            viewModel.onRemoveDecoration.observe(viewLifecycleOwner) {
                if (trueCloudFilesRecyclerView.itemDecorationCount > EMPTY_DECORATION_SIZE) {
                    trueCloudFilesRecyclerView.removeItemDecorationAt(FIRST_INDEX)
                }
                filesAdapter.run {
                    isShowHorizontalLayout = true
                    notifyItemRangeChanged(FIRST_INDEX, filesAdapter.itemCount)
                }
            }

            viewModel.onBackPressed.observe(viewLifecycleOwner) {
                findNavController().navigateUp() || fragmentPopBackStack()
            }

            viewModel.onShowSelectMode.observe(viewLifecycleOwner) {
                trueCloudHeaderSelect.visibility = View.VISIBLE
                trueCloudHeaderTitle.visibility = View.GONE
                filesAdapter.setMultipleSelect(onSelectItem)
                refreshRecyclerView()
            }

            viewModel.onShowDataEmpty.observe(viewLifecycleOwner) {
                trueCloudFilesRecyclerView.gone()
                trueCloudDataEmpty.visible()
            }
            viewModel.onShowFileList.observe(viewLifecycleOwner) {
                trueCloudFilesRecyclerView.visible()
                trueCloudDataEmpty.gone()
            }

            viewModel.onShowSnackbarComplete.observe(viewLifecycleOwner) {
                binding.root.snackBar(
                    it,
                    R.color.true_cloudv3_color_toast_success
                )
            }
            viewModel.onShowSnackbarError.observe(viewLifecycleOwner) {
                binding.root.snackBar(
                    it,
                    R.color.true_cloudv3_color_toast_error
                )
            }

            viewModel.onGetListError.observe(viewLifecycleOwner) { (message: String, action: String) ->
                showErrorStateFullScreenDialog(message = message, action = action)
            }
            viewModel.onShowConfirmDialogDelete.observe(viewLifecycleOwner) {
                // showConfirmDialogDelete()
            }
            viewModel.onMoveToTrashSuccess.observe(viewLifecycleOwner) {
                filesAdapter.deleteMultipleObject(it)
            }
        }
    }

    private val onSelectItem = object : FilesAdapter.OnItemSelect {
        override fun onSelectItem(trueCloudV3Object: TrueCloudFilesModel) {
            viewModel.addSelectItem(trueCloudV3Object)
        }

        override fun onDeselectItem(trueCloudV3Object: TrueCloudFilesModel) {
            viewModel.removeSelectItem(trueCloudV3Object)
        }

        override fun getSelectedList(): MutableList<TrueCloudFilesModel> {
            return viewModel.getSelectedItem()
        }
    }
    private fun refreshRecyclerView() {
        binding.trueCloudFilesRecyclerView.adapter = null
        binding.trueCloudFilesRecyclerView.adapter = filesAdapter
    }

    private fun initListener() {
        findNavController()
            .getSavedStateHandle<Unit>(TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_OPTION_MAIN_FILE_EMPTY_TRASH)
            ?.observe(
                viewLifecycleOwner
            ) { _ ->
                viewModel.emptyTrash()
            }

        findNavController()
            .getSavedStateHandle<MutableList<TrueCloudFilesModel.File>>(
                TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_OPTION_FILE_PERMANENCE_DELETE
            )
            ?.observe(
                viewLifecycleOwner
            ) { _file ->
                viewModel.permanenceDelete(_file)
            }

        findNavController()
            .getSavedStateHandle<MutableList<TrueCloudFilesModel.File>>(
                TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_OPTION_FILE_RESTORE
            )
            ?.observe(
                viewLifecycleOwner
            ) { _file ->
                viewModel.restoreFile(_file)
            }

        findNavController()
            .getSavedStateHandle<Boolean>(
                TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_OPTION_MAIN_FILE_SELECT_MODE
            )
            ?.observe(
                viewLifecycleOwner
            ) {
                viewModel.onOptionClickSelect(it)
            }
        findNavController()
            .getSavedStateHandle<SortType>(
                TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_OPTION_MAIN_FILE_SORT_BY
            )
            ?.observe(
                viewLifecycleOwner
            ) { _sortType ->
                viewModel.onSortByClick(_sortType)
            }

        findNavController()
            .getSavedStateHandle<SortType>(
                TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_OPTION_MAIN_FILE_SORT_BY
            )
            ?.observe(
                viewLifecycleOwner
            ) { _sortType ->
                viewModel.onTrashSortByClick(_sortType)
            }
    }

    private fun fragmentPopBackStack(): Boolean {
        return (parentFragment as? NavHostFragment)
            ?.findNavController()
            ?.popBackStack() ?: false
    }

    private fun showErrorStateFullScreenDialog(message: String, action: String) {
        DialogManager.getFullScreenDialog(
            context = requireContext(),
            icon = DialogIconType.WARNING,
            title = getString(R.string.true_cloudv3_dialog_title_sorry),
            subTitle = getString(R.string.true_cloudv3_dialog_subtitle_something_wrong, message)
        ) {
            setTopNavigationType(DialogTopNavigationType.BACK_BUTTON)
            setBackButtonListener {
                it.dismiss()
            }
            setPrimaryButton(getString(R.string.true_cloudv3_button_retry)) { dialog ->
                dialog.dismiss()
                viewModel.checkRetryState(action)
            }
        }.show(childFragmentManager)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        findNavController().setSavedStateHandle(
            TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_OPTION_FILE_DOWNLOAD,
            null
        )
        findNavController().setSavedStateHandle(
            TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_OPTION_MAIN_FILE_SORT_BY,
            null
        )
        findNavController().setSavedStateHandle(
            TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_OPTION_MAIN_FILE_ADD_NEW_FOLDER,
            null
        )
        findNavController().setSavedStateHandle(
            TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_OPTION_FILE_RENAME,
            null
        )
        findNavController().setSavedStateHandle(
            TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_OPTION_FILE_DELETE,
            null
        )
        findNavController().setSavedStateHandle(
            TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_OPTION_MAIN_FILE_SELECT_MODE,
            false
        )
        findNavController().setSavedStateHandle(
            TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_OPTION_FILE_GROUP_DELETE,
            null
        )
    }
}
