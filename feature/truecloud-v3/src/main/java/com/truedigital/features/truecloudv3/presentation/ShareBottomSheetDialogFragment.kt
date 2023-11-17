package com.truedigital.features.truecloudv3.presentation

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.truedigital.common.share.componentv3.extension.setSavedStateHandle
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey
import com.truedigital.features.truecloudv3.databinding.TrueCloudv3ShareBottomSheetDialogBinding
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.features.truecloudv3.extension.snackBar
import com.truedigital.features.truecloudv3.injections.TrueCloudV3Component
import com.truedigital.features.truecloudv3.presentation.viewmodel.ShareBottomSheetDialogViewModel
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject

class ShareBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private val binding by viewBinding(TrueCloudv3ShareBottomSheetDialogBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: ShareBottomSheetDialogViewModel by viewModels { viewModelFactory }

    override fun onAttach(context: Context) {
        TrueCloudV3Component.getInstance().inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.true_cloudv3_share_bottom_sheet_dialog, container, false)
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
        observeViewModel()
        arguments?.apply {
            getParcelable<TrueCloudFilesModel.File>(TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_OPTION_FILE_MODEL)?.let {
                viewModel.setFileModel(it)
            }
        }
    }

    private fun observeViewModel() {
        viewModel.onStartShare.observe(viewLifecycleOwner) { (_file, _mimeType) ->
            val fileUri: Uri = Uri.fromFile(_file)
            val intent = Intent(Intent.ACTION_SEND)
            intent.setType(_mimeType)
            intent.putExtra(Intent.EXTRA_STREAM, fileUri)
            startActivity(Intent.createChooser(intent, "Share File"))
        }
        viewModel.onSetTitle.observe(viewLifecycleOwner) { _title ->
            binding.titleFileName.text = _title
        }
        viewModel.onSetTitleStatus.observe(viewLifecycleOwner) { (_resIcon, _status) ->
            binding.icStatus.setImageResource(_resIcon)
            binding.txtStatus.text = _status
            binding.icStatus.visible()
            binding.txtStatus.visible()
        }
        viewModel.onShowProgressLoading.observe(viewLifecycleOwner) { _isShow ->
            if (_isShow) binding.shareProgressBar.visible() else binding.shareProgressBar.gone()
        }
        viewModel.onCopyLink.observe(viewLifecycleOwner) { _shareLink ->
            val clipboardManager =
                context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            clipboardManager.setPrimaryClip(ClipData.newPlainText(_shareLink, _shareLink))
            binding.root.rootView.snackBar(
                getString(R.string.true_cloudv3_copied_share_link),
                R.color.true_cloudv3_color_toast_success
            )
        }
    }

    private fun onCloseShare() {
        findNavController()
            .setSavedStateHandle(
                TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_CLOSE_SHARE,
                true
            )
        if (viewModel.onCopyLink.value != null) {
            findNavController().setSavedStateHandle(
                TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_RELOAD_AFTER_SHARE,
                true
            )
        }
        findNavController().navigateUp()
    }

    private fun initViews() = with(binding) {
        collapseImageView.onClick {
            dismiss()
        }
        shareContainer.onClick {
            viewModel.onClickShare()
        }
        copyLinkContainer.onClick {
            viewModel.onClickShareLink()
        }
        trueCloudControlShareAccessContainer.onClick {
            viewModel.onClickControlShare()
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onCloseShare()
    }
}
