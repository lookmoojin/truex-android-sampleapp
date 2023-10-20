package com.truedigital.features.truecloudv3.presentation

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.databinding.FragmentTrueCloudv3PhotoEditorBinding
import com.truedigital.common.share.componentv3.extension.getSavedStateHandle
import com.truedigital.component.base.BaseFragment
import com.truedigital.component.dialog.trueid.DialogIconType
import com.truedigital.component.dialog.trueid.DialogManager
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_FILE_VIEW
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_PHOTO_EDITOR_IMAGE
import com.truedigital.features.truecloudv3.domain.model.TrueCloudFilesModel
import com.truedigital.features.truecloudv3.extension.snackBar
import com.truedigital.features.truecloudv3.injections.TrueCloudV3Component
import com.truedigital.features.truecloudv3.presentation.viewmodel.TrueCloudV3PhotoEditorViewModel
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.presentations.ViewModelFactory
import ja.burhanrashid52.photoeditor.PhotoEditor
import javax.inject.Inject

class PhotoEditorFragment : BaseFragment(R.layout.fragment_true_cloudv3_photo_editor) {

    private val binding by viewBinding(FragmentTrueCloudv3PhotoEditorBinding::bind)

    private lateinit var photoEditor: PhotoEditor

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: TrueCloudV3PhotoEditorViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        TrueCloudV3Component.getInstance().inject(this)
        super.onCreate(savedInstanceState)
        arguments?.apply {
            getParcelable<TrueCloudFilesModel.File>(KEY_BUNDLE_TRUE_CLOUD_FILE_VIEW)?.let {
                viewModel.setObjectFile(it)
            }
        }
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

        initView()
        observeViewModel()
    }

    private fun initView() = with(binding) {
        photoEditor =
            PhotoEditor.Builder(requireContext(), binding.photoEditorView).setClipSourceImage(true)
                .build()
        cropTextView.onClick {
            viewModel.onTransformClick()
        }
        adjustTextView.onClick {
            viewModel.onAdjustClick()
        }
        focusTextView.onClick {
            viewModel.onFocusClick()
        }
        textTextView.onClick {
            viewModel.onTextClick()
        }
        discardImageView.onClick {
            DialogManager.showBottomSheetDialog(
                requireContext(),
                childFragmentManager,
                icon = DialogIconType.DELETE,
                title = getString(R.string.true_cloudv3_photo_editor_discard_dialog_title),
                subTitle = getString(R.string.true_cloudv3_photo_editor_discard_dialog_description),
            ) {
                this.setPrimaryButton(R.string.true_cloudv3_photo_editor_discard_dialog_action_discard) {
                    it.dismiss()
                    findNavController().navigateUp()
                }
                this.setSecondaryButton(R.string.cancel) {
                    it.dismiss()
                }
            }
        }
        saveImageView.onClick {
            viewModel.onSaveClick()
        }
        undoImageView.onClick {
            viewModel.onUndoClick()
        }
        redoImageView.onClick {
            viewModel.onRedoClick()
        }
        findNavController().getSavedStateHandle<Uri>(KEY_TRUE_CLOUD_PHOTO_EDITOR_IMAGE)
            ?.observe(viewLifecycleOwner) { _uri ->
                viewModel.onNewImage(_uri)
            }
    }

    private fun observeViewModel() {
        viewModel.imagePreview.observe(viewLifecycleOwner) {
            binding.photoEditorView.source.setImageURI(it)
            binding.trueCloudProgress.gone()
        }
        viewModel.onShowLoading.observe(viewLifecycleOwner) {
            binding.trueCloudProgress.isVisible = it
        }
        viewModel.onUploadComplete.observe(viewLifecycleOwner) {
            binding.root.snackBar(
                getString(R.string.true_cloudv3_migration_toast_success),
                R.color.true_cloudv3_color_toast_success,
            )
            findNavController().navigateUp()
        }
        viewModel.onUploadError.observe(viewLifecycleOwner) {
            binding.root.snackBar(
                it,
                R.color.true_cloudv3_color_toast_error
            )
        }
    }
}
