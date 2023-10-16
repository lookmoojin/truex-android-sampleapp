package com.truedigital.features.truecloudv3.presentation

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.tdg.truecloud.R
import com.tdg.truecloud.databinding.FragmentTrueCloudv3FileLocatorBinding
import com.truedigital.common.share.componentv3.extension.getSavedStateHandle
import com.truedigital.common.share.componentv3.extension.setSavedStateHandle
import com.truedigital.component.base.BaseFragment
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.truecloudv3.common.FileCategoryType
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle
import com.truedigital.features.truecloudv3.common.TrueCloudV3MediaType
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey
import com.truedigital.features.truecloudv3.data.repository.FileRepositoryImpl
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.features.truecloudv3.extension.actionGetContent
import com.truedigital.features.truecloudv3.extension.snackBar
import com.truedigital.features.truecloudv3.injections.TrueCloudV3Component
import com.truedigital.features.truecloudv3.presentation.adapter.FilesAdapter
import com.truedigital.features.truecloudv3.presentation.viewmodel.FilesTrueCloudViewModel
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject

class FileLocatorFragment : BaseFragment(R.layout.fragment_true_cloudv3_file_locator) {

    companion object {
        private const val DEFAULT_SPAN_COUNT_GRID = 2
    }

    private val binding by viewBinding(FragmentTrueCloudv3FileLocatorBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: FilesTrueCloudViewModel by viewModels { viewModelFactory }

    @Suppress("TooManyFunctions")
    private val filesAdapter = FilesAdapter(object : FilesAdapter.OnActionClickListener {
        override fun onPauseClicked(model: TrueCloudFilesModel.Upload) {
            // not do everything
        }

        override fun onRetryClicked(model: TrueCloudFilesModel.Upload) {
            // not do everything
        }

        override fun onCancelClicked(model: TrueCloudFilesModel.Upload) {
            // not do everything
        }

        override fun onFileClicked(model: TrueCloudFilesModel.File) {
            // not do everything
        }

        override fun onMoreClicked(model: TrueCloudFilesModel.File) {
            // not do everything
        }

        override fun onLongClicked(model: TrueCloudFilesModel.File) {
            // not do everything
        }

        override fun onCancelAllClicked() {
            // not do everything
        }

        override fun onUploadExpandClicked(status: Boolean) {
            // not do everything
        }

        override fun onFolderClicked(model: TrueCloudFilesModel.Folder) {
            viewModel.onFolderItemClick(model)
        }

        override fun onFolderMoreClicked(model: TrueCloudFilesModel.Folder) {
            // not do everything
        }

        override fun onAutoBackupExpandClicked(status: Boolean) {
            // not do everything
        }

        override fun onCancelAllBackupClicked() {
            // not do everything
        }

        override fun onPauseAllBackupClicked() {
            // not do everything
        }

        override fun onResumeAllBackupClicked() {
            // not do everything
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
        val folderId =
            arguments?.getString(TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_FOLDER_ID).orEmpty()
        val categoryName =
            arguments?.getString(TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_CATEGORY).orEmpty()
        val itemList =
            arguments?.getStringArrayList(TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_FILE_SELECTED)
                ?: ArrayList()
        val type = arguments?.getString(TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_FILE_LOCATE_TYPE)
            .orEmpty()
        viewModel.onViewCreated(
            cateName = FileCategoryType.UNSUPPORTED_FORMAT.type,
            folderId = folderId
        )
        viewModel.setSelectedList(itemList, type, categoryName)
        initBackButton()
        initView()
        observeViewModel()
        initListener()
    }

    private fun initView() = with(binding) {
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
        trueCloudButtonSubmit.onClick {
            viewModel.onClickLocate()
        }
        trueCloudBackImageView.onClick {
            viewModel.onClickBack()
        }
        trueCloudButtonCancel.onClick {
            fragmentPopBackStack(false)
        }
        trueCloudButtonSubmit.text = when (viewModel.getLocatorType()) {
            FileRepositoryImpl.COPY -> getString(R.string.true_cloudv3_file_locator_copy)
            FileRepositoryImpl.MOVE -> getString(R.string.true_cloudv3_file_locator_move)
            else -> ""
        }
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
    }

    private fun observeViewModel() {
        binding.apply {
            viewModel.onSetFileList.observe(viewLifecycleOwner) { _fileList ->
                filesAdapter.setSelectedFile()
                filesAdapter.addFolderSelectedId(viewModel.getItemList())
                filesAdapter.refreshItemList(_fileList)
            }
            viewModel.onAddFileList.observe(viewLifecycleOwner) { _list ->
                filesAdapter.addFiles(_list)
            }
            viewModel.onRefreshData.observe(viewLifecycleOwner) { _list ->
                filesAdapter.refreshItemList(_list)
            }
            viewModel.onSetCategoryName.observe(viewLifecycleOwner) { _categoryName ->
                trueCloudTitleTextView.text = when (viewModel.getLocatorType()) {
                    FileRepositoryImpl.COPY -> String.format(
                        getString(R.string.true_cloudv3_file_locator),
                        getString(R.string.true_cloudv3_dialog_selected_copy_file),
                        _categoryName
                    )

                    FileRepositoryImpl.MOVE -> String.format(
                        getString(R.string.true_cloudv3_file_locator),
                        getString(R.string.true_cloudv3_dialog_selected_move_file),
                        _categoryName
                    )

                    else -> ""
                }
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
            viewModel.onShowDataEmpty.observe(viewLifecycleOwner) {
                trueCloudFilesRecyclerView.gone()
                trueCloudDataEmpty.visible()
            }
            viewModel.onShowFileList.observe(viewLifecycleOwner) {
                trueCloudFilesRecyclerView.visible()
                trueCloudDataEmpty.gone()
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
            viewModel.onShowNotificationIsOff.observe(viewLifecycleOwner) {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
            viewModel.onClearList.observe(viewLifecycleOwner) {
                filesAdapter.refreshItemList(it)
            }
            viewModel.onBackPressed.observe(viewLifecycleOwner) {
                findNavController().navigateUp()
            }
            viewModel.onSetExpandState.observe(viewLifecycleOwner) {
                filesAdapter.setExpandableUpload(it)
            }
            viewModel.onShowFileList.observe(viewLifecycleOwner) {
                trueCloudFilesRecyclerView.visible()
            }
            viewModel.onLocateFileSuccess.observe(viewLifecycleOwner) {
                binding.root.snackBar(
                    it,
                    R.color.true_cloudv3_color_toast_success
                )
                fragmentPopBackStack(true)
            }
            viewModel.onLocateFileError.observe(viewLifecycleOwner) {
                binding.root.snackBar(
                    it,
                    R.color.true_cloudv3_color_toast_error
                )
                fragmentPopBackStack(false)
            }
        }
    }

    private fun fragmentPopBackStack(status: Boolean) {
        findNavController().setSavedStateHandle(
            TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_FILE_LOCATOR, status
        )
        findNavController().navigateUp()
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
}
