package com.truedigital.features.truecloudv3.presentation

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tdg.truecloud.R
import com.tdg.truecloud.databinding.FragmentTrueCloudv3FilesBinding
import com.truedigital.common.share.componentv3.extension.getSavedStateHandle
import com.truedigital.common.share.componentv3.extension.setSavedStateHandle
import com.truedigital.component.base.BaseFragment
import com.truedigital.component.dialog.trueid.DialogIconType
import com.truedigital.component.dialog.trueid.DialogManager
import com.truedigital.component.dialog.trueid.DialogTopNavigationType
import com.truedigital.core.extensions.viewBinding
import com.truedigital.core.manager.permission.PermissionManager
import com.truedigital.core.manager.permission.PermissionManagerImpl
import com.truedigital.features.truecloudv3.common.SortType
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_CATEGORY
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_FOLDER_ID
import com.truedigital.features.truecloudv3.common.TrueCloudV3MediaType
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.features.truecloudv3.extension.actionGetContent
import com.truedigital.features.truecloudv3.extension.actionGetContentWithMimeType
import com.truedigital.features.truecloudv3.extension.checkStoragePermissionAlready
import com.truedigital.features.truecloudv3.extension.getNotificationPermission
import com.truedigital.features.truecloudv3.extension.getStoragePermissions
import com.truedigital.features.truecloudv3.extension.snackBar
import com.truedigital.features.truecloudv3.injections.TrueCloudV3Component
import com.truedigital.features.truecloudv3.presentation.adapter.FilesAdapter
import com.truedigital.features.truecloudv3.presentation.viewmodel.FilesTrueCloudViewModel
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.visible
import com.truedigital.foundation.presentations.ViewModelFactory
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class FilesTrueCloudFragment : BaseFragment(R.layout.fragment_true_cloudv3_files) {

    companion object {
        private const val DEFAULT_SPAN_COUNT_GRID = 2
        private const val DATA_TYPE_IMAGE_JPG = "image/jpg"
        private const val EMPTY_DECORATION_SIZE = 0
        private const val FIRST_INDEX = 0
    }

    private val binding by viewBinding(FragmentTrueCloudv3FilesBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: FilesTrueCloudViewModel by viewModels { viewModelFactory }

    private val permissionManager: PermissionManager.PermissionAction by lazy {
        PermissionManagerImpl(fragmentActivity = this.requireActivity())
    }

    @Suppress("TooManyFunctions")
    private val filesAdapter = FilesAdapter(object : FilesAdapter.OnActionClickListener {
        override fun onPauseClicked(model: TrueCloudFilesModel.Upload) {
            viewModel.onClickPause(model)
        }

        override fun onRetryClicked(model: TrueCloudFilesModel.Upload) {
            viewModel.onClickRetry(model)
        }

        override fun onCancelClicked(model: TrueCloudFilesModel.Upload) {
            viewModel.onClickCancel(model)
        }

        override fun onFileClicked(model: TrueCloudFilesModel.File) {
            permissionManager.requestPermission(
                getStoragePermissions(),
                object : PermissionManager.PermissionAskListener {
                    override fun onPermissionDisabled() {
                        Timber.i("onPermissionDisabled")
                    }

                    override fun onPermissionGranted() {
                        viewModel.performClickToImageViewer(model)
                        clearSearchBar()
                    }

                    override fun onPermissionDenied() {
                        Timber.i("onPermissionDenied")
                    }
                }
            )
        }

        override fun onMoreClicked(model: TrueCloudFilesModel.File) {
            viewModel.onClickItemMoreOption(model)
        }

        override fun onLongClicked(model: TrueCloudFilesModel.File) {
            viewModel.onLongClickItemMoreOption(model)
        }

        override fun onCancelAllClicked() {
            viewModel.onClickCancelAll()
        }

        override fun onUploadExpandClicked(status: Boolean) {
            viewModel.onExpandClicked(status)
        }

        override fun onFolderClicked(model: TrueCloudFilesModel.Folder) {
            viewModel.onFolderItemClick(model)
        }

        override fun onFolderMoreClicked(model: TrueCloudFilesModel.Folder) {
            viewModel.onClickFolderMoreOption(model)
        }

        override fun onAutoBackupExpandClicked(status: Boolean) {
            viewModel.onExpandBackupClicked(status)
        }

        override fun onCancelAllBackupClicked() {
            viewModel.onClickCancelAllBackup()
        }

        override fun onPauseAllBackupClicked() {
            viewModel.onClickPauseAllBackup()
        }

        override fun onResumeAllBackupClicked() {
            viewModel.onClickResumeAllBackup()
        }
    })
    private val appChooserResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            viewModel.onSelectedUploadFileResult(result.data)
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
        val folderId = arguments?.getString(KEY_BUNDLE_TRUE_CLOUD_FOLDER_ID).orEmpty()
        val categoryName = arguments?.getString(KEY_BUNDLE_TRUE_CLOUD_CATEGORY).orEmpty()
        viewModel.onViewCreated(
            cateName = categoryName,
            folderId = folderId
        )
        initBackButton()
        initView()
        observeViewModel()
        initListener()
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
        findNavController().setSavedStateHandle(
            TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_RELOAD_AFTER_SHARE,
            false
        )
    }

    private fun initView() = with(binding) {
        trueCloudHeaderTitle.apply {
            setOnClickBack { viewModel.onClickBack() }
            setOnClickUpload {
                if (checkStoragePermissionAlready()) {
                    viewModel.onClickUpload()
                } else {
                    viewModel.performClickIntroPermission()
                }
            }
            setOnClickChangeLayout { viewModel.onClickChangeLayout() }
            setOnClickMoreOption {
                viewModel.onClickHeaderMoreOption()
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
            setOnClickDelete {
                viewModel.onClickDelete()
            }
            setOnClickSelectOption {
                viewModel.onClickSelectOption()
            }
        }
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
        setupSearchBar()
    }

    private val onScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            viewModel.onScrolled(dy, recyclerView.layoutManager as? GridLayoutManager)
        }
    }

    private fun initListener() {
        findNavController()
            .getSavedStateHandle<TrueCloudV3MediaType>(TrueCloudV3SaveStateKey.KEY_PERMISSION_GRANTED_RESULT)
            ?.observe(
                viewLifecycleOwner
            ) { trueCloudMediaType ->
                viewModel.getNextActionIntent(trueCloudMediaType)
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
            .getSavedStateHandle<String>(
                TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_OPTION_MAIN_FILE_ADD_NEW_FOLDER
            )
            ?.observe(
                viewLifecycleOwner
            ) { _folderName ->
                viewModel.createFolder(_folderName)
            }
        findNavController()
            .getSavedStateHandle<TrueCloudFilesModel.File>(
                TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_OPTION_FILE_RENAME
            )
            ?.observe(
                viewLifecycleOwner
            ) { _trueCloudV3File ->
                viewModel.rename(_trueCloudV3File)
            }
        findNavController()
            .getSavedStateHandle<TrueCloudFilesModel.File>(
                TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_OPTION_FILE_DELETE
            )
            ?.observe(
                viewLifecycleOwner
            ) { _trueCloudV3File ->
                if (_trueCloudV3File != null) viewModel.onDeleteGroupFile(
                    mutableListOf(
                        _trueCloudV3File
                    )
                )
            }
        findNavController()
            .getSavedStateHandle<TrueCloudFilesModel.File>(
                TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_OPTION_FILE_DOWNLOAD
            )
            ?.observe(
                viewLifecycleOwner
            ) { _trueCloudV3File ->
                if (_trueCloudV3File != null) {
                    permissionManager.requestPermission(
                        getNotificationPermission(),
                        object : PermissionManager.PermissionAskListener {
                            override fun onPermissionDisabled() {
                                Timber.i("onPermissionDisabled")
                            }

                            override fun onPermissionGranted() {
                                viewModel.onDownloadFile(_trueCloudV3File)
                            }

                            override fun onPermissionDenied() {
                                Timber.i("onPermissionDenied")
                            }
                        }
                    )
                }
            }
        findNavController()
            .getSavedStateHandle<Boolean>(
                TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_FILE_LOCATOR
            )
            ?.observe(
                viewLifecycleOwner
            ) { _status ->
                viewModel.onLocateFinish(_status)
            }
        findNavController()
            .getSavedStateHandle<MutableList<TrueCloudFilesModel.File>>(
                TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_OPTION_FILE_GROUP_DELETE
            )
            ?.observe(
                viewLifecycleOwner
            ) { _groupFile ->
                if (_groupFile != null) viewModel.onDeleteGroupFile(_groupFile)
            }
        findNavController()
            .getSavedStateHandle<Boolean>(
                TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_RELOAD_AFTER_SHARE
            )
            ?.observe(
                viewLifecycleOwner
            ) { requireReload ->
                viewModel.onRequireReload(requireReload)
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

    private fun observeViewModel() {
        binding.apply {
            viewModel.onSetFileList.observe(viewLifecycleOwner) { _fileList ->
                filesAdapter.refreshItemList(_fileList)
            }

            viewModel.onSetHeaderTitle.observe(viewLifecycleOwner) { headerTitle ->
                trueCloudHeaderTitle.setTitle(headerTitle)
            }

            viewModel.onAddFileList.observe(viewLifecycleOwner) { _list ->
                filesAdapter.addFiles(_list)
            }

            viewModel.onRefreshData.observe(viewLifecycleOwner) { _list ->
                filesAdapter.refreshItemList(_list)
            }

            viewModel.onSetCategoryName.observe(viewLifecycleOwner) { _categoryName ->
                trueCloudHeaderTitle.apply {
                    setTitle(_categoryName)
                }
                trueCloudloadingProgressBar.root.gone()
            }
            viewModel.onShowUploadTaskList.observe(viewLifecycleOwner) {
                filesAdapter.refreshItemList(it)
            }

            viewModel.onShowProgressChange.observe(viewLifecycleOwner) {
                filesAdapter.setUpdateFileAtIndex(it.first, it.second)
            }

            viewModel.onLoadCoverFinished.observe(viewLifecycleOwner) {
                filesAdapter.notifyId()
            }

            viewModel.onTaskUpdateStatusType.observe(viewLifecycleOwner) {
                filesAdapter.setUpdateFileAtIndex(it.first, it.second)
            }

            viewModel.onIntentActionGetContent.observe(
                viewLifecycleOwner
            ) { mimeTypes ->
                appChooserResult.launch(
                    Intent.createChooser(
                        actionGetContent(mimeTypes),
                        getString(R.string.true_cloudv3_select_a_file_to_upload)
                    )
                )
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

            viewModel.onShowNotificationIsOff.observe(viewLifecycleOwner) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }

            viewModel.onDeleteFileSuccess.observe(viewLifecycleOwner) { model ->
                filesAdapter.deleteObject(model)
            }
            viewModel.onRenameFileSuccess.observe(viewLifecycleOwner) {
                filesAdapter.setUpdateFileAtIndex(it.first, it.second)
            }
            viewModel.onClearList.observe(viewLifecycleOwner) {
                clearSearchBar()
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

            viewModel.onShowPreview.observe(viewLifecycleOwner) {
                val intent = Intent()
                intent.setAction(Intent.ACTION_VIEW)
                val uri = Uri.fromFile(File(it))
                intent.setDataAndType(uri, DATA_TYPE_IMAGE_JPG)
                startActivity(intent)
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

            viewModel.onSetExpandState.observe(viewLifecycleOwner) {
                filesAdapter.setExpandableUpload(it)
                refreshRecyclerView()
            }
            viewModel.onSetExpandBackupState.observe(viewLifecycleOwner) {
                filesAdapter.setExpandableAutoBackup(it)
                refreshRecyclerView()
            }
            viewModel.onOpenSelectFile.observe(viewLifecycleOwner) { _categoryType ->
                appChooserResult.launch(
                    Intent.createChooser(
                        actionGetContentWithMimeType(_categoryType),
                        getString(R.string.true_cloudv3_select_a_file_to_upload)
                    )
                )
            }
            viewModel.onShowDataEmpty.observe(viewLifecycleOwner) {
                trueCloudFilesRecyclerView.gone()
                trueCloudDataEmpty.visible()
            }
            viewModel.onShowSearchResultEmpty.observe(viewLifecycleOwner) {
                trueCloudFilesRecyclerView.gone()
                noSearchResultView.visible()
            }
            viewModel.onShowFileList.observe(viewLifecycleOwner) {
                trueCloudFilesRecyclerView.visible()
                trueCloudDataEmpty.gone()
                noSearchResultView.gone()
            }
            viewModel.onUploadError.observe(viewLifecycleOwner) {
                binding.root.snackBar(
                    it,
                    R.color.true_cloudv3_color_toast_error
                )
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
            viewModel.onShowLoading.observe(viewLifecycleOwner) {
                trueCloudloadingProgressBar.root.visibility = if (it) View.VISIBLE else View.GONE
            }
            viewModel.onGetListError.observe(viewLifecycleOwner) { (message: String, action: String) ->
                showErrorStateFullScreenDialog(message = message, action = action)
            }
            viewModel.onChangeSelectOption.observe(viewLifecycleOwner) {
                trueCloudHeaderSelect.setOptionVisibility(it)
            }
            viewModel.onMoveToTrashSuccess.observe(viewLifecycleOwner) {
                filesAdapter.deleteMultipleObject(it)
            }
        }
    }

    private fun fragmentPopBackStack(): Boolean {
        return (parentFragment as? NavHostFragment)
            ?.findNavController()
            ?.popBackStack() ?: false
    }

    private fun initBackButton() {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    viewModel.onClickBack()
                }
            }
        )
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

    private fun clearSearchBar() {
        binding.searchBar.clearSearchInput()
    }

    private fun setupSearchBar() {
        binding.searchBar.setOnInputSearchString { searchString ->
            viewModel.onSearchStringInput(searchString)
        }
    }
}
