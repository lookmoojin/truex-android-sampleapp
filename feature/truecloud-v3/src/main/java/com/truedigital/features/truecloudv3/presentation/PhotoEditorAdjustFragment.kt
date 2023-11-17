package com.truedigital.features.truecloudv3.presentation

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatCheckedTextView
import androidx.core.view.forEach
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.truedigital.common.share.componentv3.extension.setSavedStateHandle
import com.truedigital.component.base.BaseFragment
import com.truedigital.component.dialog.trueid.DialogIconType
import com.truedigital.component.dialog.trueid.DialogManager
import com.truedigital.core.extensions.collectSafe
import com.truedigital.core.extensions.launchSafe
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_PHOTO_EDITOR_IMAGE
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_PHOTO_EDITOR_IMAGE
import com.truedigital.features.truecloudv3.databinding.FragmentTrueCloudv3PhotoEditorAdjustBinding
import com.truedigital.features.truecloudv3.extension.snackBar
import com.truedigital.features.truecloudv3.injections.TrueCloudV3Component
import com.truedigital.features.truecloudv3.presentation.viewmodel.TrueCloudV3PhotoEditorAdjustViewModel
import com.truedigital.features.truecloudv3.presentation.viewmodel.TrueCloudV3PhotoEditorFileViewModel
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.presentations.ViewModelFactory
import ja.burhanrashid52.photoeditor.CustomEffect
import ja.burhanrashid52.photoeditor.OnSaveBitmap
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoFilter
import javax.inject.Inject

class PhotoEditorAdjustFragment :
    BaseFragment(R.layout.fragment_true_cloudv3_photo_editor_adjust) {

    companion object {
        private const val DEFAULT_SEEK_BAR_MIN = -100.0
        private const val DEFAULT_SEEK_BAR_MAX = 100.0
        private const val DEFAULT_TEMPERATURE_SEEK_BAR_MIN = -50.0
        private const val DEFAULT_TEMPERATURE_SEEK_BAR_MAX = 50.0
        private const val DEFAULT_SHARPNESS_SEEK_BAR_MIN = 0.0
        private const val DEFAULT_SHARPNESS_SEEK_BAR_MAX = 100.0
        private const val TEMPERATURE_DEFAULT = 0.5F
    }

    private val binding by viewBinding(FragmentTrueCloudv3PhotoEditorAdjustBinding::bind)

    private lateinit var photoEditor: PhotoEditor

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: TrueCloudV3PhotoEditorAdjustViewModel by viewModels { viewModelFactory }
    private val fileViewModel: TrueCloudV3PhotoEditorFileViewModel by viewModels { viewModelFactory }

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
                binding.photoEditorView.source.setImageURI(it)
                fileViewModel.setObjectFile(it)
            }
        }

        initView()
        observeViewModel()
    }

    private fun initView() = with(binding) {
        photoEditor =
            PhotoEditor.Builder(requireContext(), photoEditorView).setClipSourceImage(true).build()
        adjustValueSeekBar.setOnSeekBarChangeListener { _, progress ->
            viewModel.setScale(progress)
        }
        adjustValueSeekBar.setProgress(0.0)
        initAdjustListener()
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
                this.setSecondaryButton(com.truedigital.component.R.string.cancel) {
                    it.dismiss()
                }
            }
        }
        confirmImageView.onClick {
            photoEditor.saveAsBitmap(object : OnSaveBitmap {
                override fun onBitmapReady(saveBitmap: Bitmap?) {
                    val realBitmap = (photoEditorView.source.drawable as? BitmapDrawable)?.bitmap
                    if (realBitmap != null) {
                        fileViewModel.onConfirmClick(realBitmap)
                    } else {
                        val errorMessage = "Error getting bitmap"
                        binding.root.snackBar(
                            errorMessage,
                            R.color.true_cloudv3_color_toast_error
                        )
                    }
                }

                override fun onFailure(e: Exception?) {
                    val errorMessage = e?.message ?: ""
                    binding.root.snackBar(
                        errorMessage,
                        R.color.true_cloudv3_color_toast_error
                    )
                }
            })
        }
    }

    private fun initAdjustListener() = with(binding) {
        adjustResetTextView.setOnClickListener {
            selectFilter(null)
            resetSeekbar(DEFAULT_SEEK_BAR_MIN, DEFAULT_SEEK_BAR_MAX)
            viewModel.setScale(0.0)
            viewModel.setEffect(null)
        }
        adjustBrightnessTextView.setOnClickListener {
            selectFilter(it.id)
            resetSeekbar(DEFAULT_SEEK_BAR_MIN, DEFAULT_SEEK_BAR_MAX)
            viewModel.setScale(0.0)
            viewModel.setEffect(TrueCloudV3PhotoEditorAdjustViewModel.UiEffect.BRIGHTNESS)
        }
        adjustContrastTextView.setOnClickListener {
            selectFilter(it.id)
            resetSeekbar(DEFAULT_SEEK_BAR_MIN, DEFAULT_SEEK_BAR_MAX)
            viewModel.setScale(0.0)
            viewModel.setEffect(TrueCloudV3PhotoEditorAdjustViewModel.UiEffect.CONTRAST)
        }
        adjustSaturationTextView.setOnClickListener {
            selectFilter(it.id)
            resetSeekbar(DEFAULT_SEEK_BAR_MIN, DEFAULT_SEEK_BAR_MAX)
            viewModel.setScale(0.0)
            viewModel.setEffect(TrueCloudV3PhotoEditorAdjustViewModel.UiEffect.SATURATION)
        }
        adjustTemperatureTextView.setOnClickListener {
            selectFilter(it.id)
            resetSeekbar(DEFAULT_TEMPERATURE_SEEK_BAR_MIN, DEFAULT_TEMPERATURE_SEEK_BAR_MAX)
            viewModel.setScale(0.0)
            viewModel.setEffect(TrueCloudV3PhotoEditorAdjustViewModel.UiEffect.TEMPERATURE)
        }
        adjustSharpnessTextView.setOnClickListener {
            selectFilter(it.id)
            resetSeekbar(DEFAULT_SHARPNESS_SEEK_BAR_MIN, DEFAULT_SHARPNESS_SEEK_BAR_MAX)
            viewModel.setScale(0.0)
            viewModel.setEffect(TrueCloudV3PhotoEditorAdjustViewModel.UiEffect.SHARPNESS)
        }
    }

    private fun selectFilter(id: Int?) {
        binding.adjustModeLayout.forEach { v ->
            if (v is AppCompatCheckedTextView) {
                v.isChecked = v.id == id
            }
        }
    }

    private fun resetSeekbar(min: Double, max: Double) {
        binding.adjustValueSeekBar.setProgress(0.0)
        binding.adjustValueSeekBar.setAbsoluteMinMaxValue(min, max)
    }

    private fun observeViewModel() {
        fileViewModel.onGenerateUri.observe(viewLifecycleOwner) { _uri ->
            findNavController().setSavedStateHandle(
                KEY_TRUE_CLOUD_PHOTO_EDITOR_IMAGE,
                _uri
            )
            findNavController().navigateUp()
        }
        lifecycleScope.launchSafe {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentEffect.collectSafe { (_effect, _scale) ->
                    val scale = _scale.toFloat()
                    val parameterValue: Float? = when (_effect) {
                        TrueCloudV3PhotoEditorAdjustViewModel.UiEffect.BRIGHTNESS -> {
                            scale / DEFAULT_SEEK_BAR_MAX.toFloat() + 1.0F
                        }

                        TrueCloudV3PhotoEditorAdjustViewModel.UiEffect.CONTRAST -> {
                            scale / DEFAULT_SEEK_BAR_MAX.toFloat() + 1.0F
                        }

                        TrueCloudV3PhotoEditorAdjustViewModel.UiEffect.SATURATION -> {
                            scale / DEFAULT_SEEK_BAR_MAX.toFloat()
                        }

                        TrueCloudV3PhotoEditorAdjustViewModel.UiEffect.SHARPNESS -> {
                            scale / DEFAULT_SHARPNESS_SEEK_BAR_MAX.toFloat()
                        }

                        TrueCloudV3PhotoEditorAdjustViewModel.UiEffect.TEMPERATURE -> {
                            scale / DEFAULT_TEMPERATURE_SEEK_BAR_MAX.toFloat() + TEMPERATURE_DEFAULT
                        }

                        null -> {
                            null
                        }
                    }
                    if (parameterValue != null) {
                        val customEffect = CustomEffect.Builder(_effect!!.factoryName)
                            .setParameter(_effect.parameterKey, parameterValue)
                            .build()
                        photoEditor.setFilterEffect(customEffect)
                    } else {
                        photoEditor.setFilterEffect(PhotoFilter.NONE)
                    }
                }
            }
        }
    }
}
