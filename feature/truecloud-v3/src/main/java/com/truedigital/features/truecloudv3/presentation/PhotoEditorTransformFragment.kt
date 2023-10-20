package com.truedigital.features.truecloudv3.presentation

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatCheckedTextView
import androidx.core.view.forEach
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.databinding.FragmentTrueCloudv3PhotoEditorTransformBinding
import com.truedigital.common.share.componentv3.extension.setSavedStateHandle
import com.truedigital.component.base.BaseFragment
import com.truedigital.component.dialog.trueid.DialogIconType
import com.truedigital.component.dialog.trueid.DialogManager
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_PHOTO_EDITOR_IMAGE
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_PHOTO_EDITOR_IMAGE
import com.truedigital.features.truecloudv3.extension.snackBar
import com.truedigital.features.truecloudv3.injections.TrueCloudV3Component
import com.truedigital.features.truecloudv3.presentation.viewmodel.TrueCloudV3PhotoEditorTransformViewModel
import com.truedigital.features.truecloudv3.widget.photoeditor.DegreeRulerValuePicker
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.presentations.ViewModelFactory
import javax.inject.Inject

class PhotoEditorTransformFragment :
    BaseFragment(R.layout.fragment_true_cloudv3_photo_editor_transform) {

    companion object {
        private const val DEFAULT_ROTATION = 0
        private const val RATIO_16 = 16
        private const val RATIO_9 = 8
        private const val RATIO_4 = 4
        private const val RATIO_3 = 3
        private const val RATIO_2 = 2
        private const val RATIO_1 = 1
        private const val ROTATE_LEFT_DEGREE = -90
    }

    private val binding by viewBinding(FragmentTrueCloudv3PhotoEditorTransformBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: TrueCloudV3PhotoEditorTransformViewModel by viewModels { viewModelFactory }

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
        arguments?.apply {
            getParcelable<Uri>(KEY_BUNDLE_TRUE_CLOUD_PHOTO_EDITOR_IMAGE)?.let {
                binding.cropImageView.setImageUriAsync(it)
            }
        }

        initView()
        observeViewModel()
        if (savedInstanceState == null) {
            binding.rotateDegreePicker.selectValue(DEFAULT_ROTATION)
        }
    }

    private fun initView() = with(binding) {
        cropImageView.setOnCropImageCompleteListener { _, result ->
            viewModel.onCropImageComplete(result)
        }
        initCropListener()
        flipImageView.onClick {
            viewModel.onFlipImage()
        }
        rotateImageView.onClick {
            viewModel.onRotateImage()
        }
        val valuePickerListener = object : DegreeRulerValuePicker.RulerValuePickerListener {
            override fun onValueChange(selectedValue: Int) {
                viewModel.onSetRotation(selectedValue)
            }

            override fun onIntermediateValueChange(selectedValue: Int) {
                viewModel.onSetRotation(selectedValue)
            }
        }
        rotateDegreePicker.setValuePickerListener(valuePickerListener)
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
        confirmImageView.onClick {
            viewModel.onConfirmClick()
        }
    }

    private fun initCropListener() = with(binding) {
        cropResetTextView.setOnClickListener {
            viewModel.onCropReset()
        }
        cropFreeTextView.setOnClickListener {
            selectFilter(it.id)
            viewModel.onSetCrop(null)
        }
        cropSquareTextView.setOnClickListener {
            selectFilter(it.id)
            viewModel.onSetCrop(RATIO_1 to RATIO_1)
        }
        crop169TextView.setOnClickListener {
            selectFilter(it.id)
            viewModel.onSetCrop(RATIO_16 to RATIO_9)
        }
        crop916TextView.setOnClickListener {
            selectFilter(it.id)
            viewModel.onSetCrop(RATIO_9 to RATIO_16)
        }
        crop43TextView.setOnClickListener {
            selectFilter(it.id)
            viewModel.onSetCrop(RATIO_4 to RATIO_3)
        }
        crop34TextView.setOnClickListener {
            selectFilter(it.id)
            viewModel.onSetCrop(RATIO_3 to RATIO_4)
        }
        crop32TextView.setOnClickListener {
            selectFilter(it.id)
            viewModel.onSetCrop(RATIO_3 to RATIO_2)
        }
        crop23TextView.setOnClickListener {
            selectFilter(it.id)
            viewModel.onSetCrop(RATIO_2 to RATIO_3)
        }
    }

    private fun selectFilter(id: Int?) {
        binding.cropModeLayout.forEach { v ->
            if (v is AppCompatCheckedTextView) {
                v.isChecked = v.id == id
            }
        }
    }

    private fun observeViewModel() {
        viewModel.onGenerateUri.observe(viewLifecycleOwner) { _uri ->
            binding.cropImageView.croppedImageAsync(
                saveCompressQuality = 100,
                customOutputUri = _uri
            )
        }
        viewModel.onShowError.observe(viewLifecycleOwner) { _error ->
            binding.root.snackBar(
                _error,
                R.color.true_cloudv3_color_toast_error
            )
        }
        viewModel.onCropComplete.observe(viewLifecycleOwner) { _uri ->
            findNavController().setSavedStateHandle(KEY_TRUE_CLOUD_PHOTO_EDITOR_IMAGE, _uri)
            findNavController().navigateUp()
        }
        viewModel.onFlipImage.observe(viewLifecycleOwner) {
            binding.cropImageView.flipImageHorizontally()
        }
        viewModel.onRotateImage.observe(viewLifecycleOwner) {
            binding.cropImageView.rotateImage(ROTATE_LEFT_DEGREE)
        }
        viewModel.onSetRotation.observe(viewLifecycleOwner) { _rotation ->
            binding.cropImageView.rotatedDegrees = _rotation
            binding.rotateDegreeTextView.text = _rotation.toString()
        }
        viewModel.onCropReset.observe(viewLifecycleOwner) {
            selectFilter(null)
            binding.cropImageView.rotatedDegrees = 0
            if (binding.cropImageView.isFlippedHorizontally) {
                binding.cropImageView.flipImageHorizontally()
            }
            binding.cropImageView.clearAspectRatio()
            binding.cropImageView.cropRect = binding.cropImageView.wholeImageRect
            binding.rotateDegreePicker.selectValue(0)
        }
        viewModel.onSetCrop.observe(viewLifecycleOwner) { _ratio ->
            if (_ratio == null) {
                binding.cropImageView.clearAspectRatio()
            } else {
                binding.cropImageView.setAspectRatio(_ratio.first, _ratio.second)
            }
        }
    }
}
