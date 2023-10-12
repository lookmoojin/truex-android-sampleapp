package com.truedigital.features.truecloudv3.presentation

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.addCallback
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.graphics.ColorUtils
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.skydoves.colorpickerview.listeners.ColorListener
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
import com.truedigital.features.truecloudv3.databinding.FragmentTrueCloudv3PhotoEditorFocusBinding
import com.truedigital.features.truecloudv3.extension.getEditBitmap
import com.truedigital.features.truecloudv3.extension.snackBar
import com.truedigital.features.truecloudv3.injections.TrueCloudV3Component
import com.truedigital.features.truecloudv3.presentation.viewmodel.TrueCloudV3PhotoEditorFileViewModel
import com.truedigital.features.truecloudv3.presentation.viewmodel.TrueCloudV3PhotoEditorFocusViewModel
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.extension.visible
import com.truedigital.foundation.presentations.ViewModelFactory
import ja.burhanrashid52.photoeditor.OnSaveBitmap
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.SaveSettings
import ja.burhanrashid52.photoeditor.shape.ShapeBuilder
import javax.inject.Inject

class PhotoEditorFocusFragment :
    BaseFragment(R.layout.fragment_true_cloudv3_photo_editor_focus) {

    companion object {
        private const val COLOR_PICKER_MIN_CONTRAST = 1.5F
        private const val BRUSH_LAYOUT_TOOL = 0
        private const val BRUSH_LAYOUT_COLOR_PICKER = 1
    }

    private val binding by viewBinding(FragmentTrueCloudv3PhotoEditorFocusBinding::bind)

    private lateinit var photoEditor: PhotoEditor

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: TrueCloudV3PhotoEditorFocusViewModel by viewModels { viewModelFactory }
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
        initListener()
        observeViewModel()
    }

    private fun initView() = with(binding) {
        photoEditor =
            PhotoEditor.Builder(requireContext(), photoEditorView).setClipSourceImage(true).build()
        photoEditor.setBrushDrawingMode(true)
        brushColorTextView.setOnClickListener {
            brushViewFlipper.displayedChild = BRUSH_LAYOUT_COLOR_PICKER
        }
        brushSizeTextView.setOnClickListener {
            brushSizeLayout.visible()
            brushSizeTextView.isChecked = true
        }
        brushSizeSeekBar.setOnSeekBarChangeListener { _, progress ->
            viewModel.setBrushSize(progress)
        }
        initColorPickerListener()
        discardImageView.onClick {
            backPressed()
        }
        confirmImageView.onClick {
            when (brushViewFlipper.displayedChild) {
                BRUSH_LAYOUT_TOOL -> {
                    val saveSettings = SaveSettings.Builder().setClearViewsEnabled(false).build()
                    photoEditor.saveAsBitmap(
                        saveSettings,
                        object : OnSaveBitmap {
                            override fun onBitmapReady(saveBitmap: Bitmap?) {
                                val editBitmap = binding.photoEditorView.getEditBitmap()
                                if (editBitmap != null) {
                                    fileViewModel.onConfirmClick(editBitmap)
                                }
                            }

                            override fun onFailure(e: Exception?) {
                                binding.root.snackBar(
                                    e?.message ?: "",
                                    R.color.true_cloudv3_color_toast_error
                                )
                            }
                        },
                    )
                }

                BRUSH_LAYOUT_COLOR_PICKER -> {
                    resetView()
                }
            }
        }
    }

    private fun initListener() {
        requireActivity().onBackPressedDispatcher.addCallback {
            when (binding.brushViewFlipper.displayedChild) {
                BRUSH_LAYOUT_TOOL -> {
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

                BRUSH_LAYOUT_COLOR_PICKER -> {
                    resetView()
                }
            }
        }
    }

    private fun resetView() = with(binding) {
        binding.colorPickerLayout.gone()
        binding.brushSizeLayout.gone()
        binding.brushSizeTextView.isChecked = false
        binding.brushViewFlipper.displayedChild = BRUSH_LAYOUT_TOOL
    }

    private fun initColorPickerListener() = with(binding) {
        colorPickerCardView.setOnClickListener {
            if (colorPickerLayout.isVisible) {
                colorPickerLayout.gone()
            } else {
                colorPickerLayout.visible()
            }
        }
        gray1CardView.setOnClickListener {
            colorPickerLayout.gone()
            pickColor(gray1CardView.cardBackgroundColor.defaultColor)
        }
        gray2CardView.setOnClickListener {
            colorPickerLayout.gone()
            pickColor(gray2CardView.cardBackgroundColor.defaultColor)
        }
        gray3CardView.setOnClickListener {
            colorPickerLayout.gone()
            pickColor(gray3CardView.cardBackgroundColor.defaultColor)
        }
        gray4CardView.setOnClickListener {
            colorPickerLayout.gone()
            pickColor(gray4CardView.cardBackgroundColor.defaultColor)
        }
        colorPickerView.setColorListener(object : ColorListener {
            override fun onColorSelected(color: Int, fromUser: Boolean) {
                pickColor(color)
            }
        })
        colorPickerView.attachAlphaSlider(alphaSlideBar)
        colorPickerView.attachBrightnessSlider(brightnessSlideBar)
    }

    private fun pickColor(color: Int) = with(binding) {
        viewModel.setBrushColor(color)
        val colorWithoutTransparent = color or -0x1000000
        colorPickerCardView.setCardBackgroundColor(color)
        val contrast = ColorUtils.calculateContrast(Color.BLACK, colorWithoutTransparent)
        if (contrast > COLOR_PICKER_MIN_CONTRAST) {
            colorPickerTextView.setTextColor(Color.BLACK)
            colorPickerTextView.updateDrawableTint(Color.BLACK)
        } else {
            colorPickerTextView.setTextColor(Color.WHITE)
            colorPickerTextView.updateDrawableTint(Color.WHITE)
        }
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
                viewModel.currentBrush.collectSafe { (_color, _size) ->
                    val shapeBuilder = ShapeBuilder().withShapeColor(_color ?: Color.BLACK)
                        .withShapeSize(_size.toFloat())
                    photoEditor.setShape(shapeBuilder)
                }
            }
        }
    }

    private fun TextView.updateDrawableTint(@ColorInt color: Int) {
        compoundDrawables.forEach { drawable ->
            drawable?.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
        }
    }
}
