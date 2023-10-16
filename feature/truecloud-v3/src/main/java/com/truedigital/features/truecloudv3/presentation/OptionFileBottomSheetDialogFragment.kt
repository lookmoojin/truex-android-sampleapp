package com.truedigital.features.truecloudv3.presentation

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import com.tdg.truecloud.R
import com.tdg.truecloud.databinding.TrueCloudv3FileBottomSheetDialogBinding
import com.truedigital.common.share.componentv3.extension.getSavedStateHandle
import com.truedigital.common.share.componentv3.extension.setSavedStateHandle
import com.truedigital.core.databinding.setViewGone
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_OPTION_FILE_MODEL
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_OPTION_FOLDER
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.features.truecloudv3.injections.TrueCloudV3Component
import com.truedigital.features.truecloudv3.presentation.viewmodel.OptionFileBottomSheetDialogViewModel
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject

class OptionFileBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private val binding by viewBinding(TrueCloudv3FileBottomSheetDialogBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: OptionFileBottomSheetDialogViewModel by viewModels { viewModelFactory }

    override fun onAttach(context: Context) {
        TrueCloudV3Component.getInstance().inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.true_cloudv3_file_bottom_sheet_dialog, container, false)
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
            getParcelable<TrueCloudFilesModel.File>(KEY_BUNDLE_TRUE_CLOUD_OPTION_FILE_MODEL)?.let {
                viewModel.setFileModel(it)
            }
            setFolderView(getBoolean(KEY_BUNDLE_TRUE_CLOUD_OPTION_FOLDER, false))
        }
    }

    private fun observeViewModel() {
        viewModel.onRename.observe(viewLifecycleOwner) { _fileName ->
            findNavController()
                .setSavedStateHandle(
                    TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_OPTION_FILE_RENAME,
                    _fileName
                )
            findNavController().navigateUp()
        }
        viewModel.onDelete.observe(viewLifecycleOwner) { _trueCloudFilesModel ->
            findNavController()
                .setSavedStateHandle(
                    TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_OPTION_FILE_DELETE,
                    _trueCloudFilesModel
                )
            findNavController().navigateUp()
        }
        viewModel.onDownload.observe(viewLifecycleOwner) { _trueCloudFilesModel ->
            findNavController()
                .setSavedStateHandle(
                    TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_OPTION_FILE_DOWNLOAD,
                    _trueCloudFilesModel
                )
            findNavController().navigateUp()
        }
        viewModel.onSetShowViewFile.observe(viewLifecycleOwner) { _isShowViewFile ->
            binding.trueCloudViewFileLayout.isVisible = _isShowViewFile
        }
        viewModel.onSetShowPhotoEditor.observe(viewLifecycleOwner) { _isShowPhotoEditor ->
            binding.trueCloudPhotoEditorContainer.isVisible = _isShowPhotoEditor
        }
        viewModel.onShowProgressLoading.observe(viewLifecycleOwner) { _isShowProgress ->
            binding.viewFileProgressBar.isVisible = _isShowProgress
        }
        viewModel.onStartShare.observe(viewLifecycleOwner) { _file ->
            val fileUri: Uri = Uri.fromFile(_file)
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "*/*"
            intent.putExtra(Intent.EXTRA_STREAM, fileUri)
            startActivity(Intent.createChooser(intent, "Share File"))
        }
    }

    private fun initListener() {
        findNavController()
            .getSavedStateHandle<TrueCloudFilesModel.File>(
                TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_OPTION_FILE_RENAME
            )
            ?.observe(
                viewLifecycleOwner
            ) { _trueCloudFilesModel ->
                viewModel.onReceiveFileName(_trueCloudFilesModel)
            }
        findNavController()
            .getSavedStateHandle<TrueCloudFilesModel.File>(
                TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_OPTION_FILE_INFO
            )
            ?.observe(
                viewLifecycleOwner
            ) {
                findNavController().navigateUp()
            }
        findNavController()
            .getSavedStateHandle<Boolean>(
                TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_CLOSE_SHARE
            )
            ?.observe(
                viewLifecycleOwner
            ) {
                findNavController().navigateUp()
            }
    }

    private fun initViews() = with(binding) {
        trueCloudDeleteTextView.onClick {
            viewModel.onClickDelete()
        }
        trueCloudTextViewRename.onClick {
            viewModel.onClickRename()
        }
        trueCloudDownloadTextView.onClick {
            viewModel.onClickDownload()
        }
        trueCloudSeeInfoTextView.onClick {
            setViewGone(root, true)
            viewModel.onClickSeeInfo()
        }
        trueCloudShareTextView.onClick {
            setViewGone(root, true)
            viewModel.onClickShare()
        }
        trueCloudViewFileTextView.onClick {
            viewModel.onClickViewFile()
        }
        trueCloudTextViewPhotoEditor.onClick {
            viewModel.onClickPhotoEditor()
        }
        collapseImageView.onClick {
            dismiss()
        }
    }

    private fun setFolderView(isFolder: Boolean) {
        if (isFolder) {
            binding.trueCloudShareContainer.gone()
            binding.trueCloudDownLoadContainer.gone()
            binding.trueCloudInfoContainer.gone()
        } else {
            binding.trueCloudShareContainer.visible()
            binding.trueCloudDownLoadContainer.visible()
            binding.trueCloudInfoContainer.visible()
        }
    }
}
