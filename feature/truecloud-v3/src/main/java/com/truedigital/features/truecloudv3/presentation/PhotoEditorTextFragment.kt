package com.truedigital.features.truecloudv3.presentation

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.addCallback
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatCheckedTextView
import androidx.cardview.widget.CardView
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.children
import androidx.core.view.forEach
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.skydoves.colorpickerview.ColorPickerView
import com.skydoves.colorpickerview.listeners.ColorListener
import com.skydoves.colorpickerview.sliders.AlphaSlideBar
import com.skydoves.colorpickerview.sliders.BrightnessSlideBar
import com.truedigital.features.truecloudv3.R
import com.truedigital.features.truecloudv3.databinding.FragmentTrueCloudv3PhotoEditorTextBinding
import com.truedigital.common.share.componentv3.extension.getSavedStateHandle
import com.truedigital.common.share.componentv3.extension.setSavedStateHandle
import com.truedigital.component.base.BaseFragment
import com.truedigital.component.dialog.trueid.DialogIconType
import com.truedigital.component.dialog.trueid.DialogManager
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.truecloudv3.common.TrueCloudV3KeyBundle.KEY_BUNDLE_TRUE_CLOUD_PHOTO_EDITOR_IMAGE
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_PHOTO_EDITOR_IMAGE
import com.truedigital.features.truecloudv3.common.TrueCloudV3SaveStateKey.KEY_TRUE_CLOUD_PHOTO_EDITOR_TEXT
import com.truedigital.features.truecloudv3.extension.getEditBitmap
import com.truedigital.features.truecloudv3.extension.snackBar
import com.truedigital.features.truecloudv3.injections.TrueCloudV3Component
import com.truedigital.features.truecloudv3.presentation.viewmodel.TrueCloudV3PhotoEditorFileViewModel
import com.truedigital.features.truecloudv3.presentation.viewmodel.TrueCloudV3PhotoEditorTextViewModel
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.onClick
import com.truedigital.foundation.presentations.ViewModelFactory
import ja.burhanrashid52.photoeditor.OnSaveBitmap
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.SaveSettings
import ja.burhanrashid52.photoeditor.TextStyleBuilder
import javax.inject.Inject

class PhotoEditorTextFragment :
    BaseFragment(R.layout.fragment_true_cloudv3_photo_editor_text) {

    companion object {
        private const val VIEW_FLIPPER_MAIN_INDEX = 0
        private const val VIEW_FLIPPER_FONT_INDEX = 1
        private const val VIEW_FLIPPER_COLOR_INDEX = 2
        private const val VIEW_FLIPPER_BG_COLOR_INDEX = 3
        private const val VIEW_FLIPPER_ALIGNMENT_INDEX = 4
        private const val COLOR_PICKER_MIN_CONTRAST = 1.5F
        private const val COLOR_TRANSPARENT = -0x1000000
    }

    private val binding by viewBinding(FragmentTrueCloudv3PhotoEditorTextBinding::bind)

    private lateinit var photoEditor: PhotoEditor

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: TrueCloudV3PhotoEditorTextViewModel by viewModels { viewModelFactory }
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
        if (savedInstanceState == null) {
            findNavController().navigate(R.id.action_photoEditorTextFragment_to_photoEditorTextInputDialogFragment)
        }
    }

    private fun initView() = with(binding) {
        photoEditor =
            PhotoEditor.Builder(requireContext(), photoEditorView).setClipSourceImage(true).build()
        textFontTextView.setOnClickListener {
            textViewFlipper.displayedChild = VIEW_FLIPPER_FONT_INDEX
        }
        textColorTextView.setOnClickListener {
            textViewFlipper.displayedChild = VIEW_FLIPPER_COLOR_INDEX
        }
        textBgColorTextView.setOnClickListener {
            textViewFlipper.displayedChild = VIEW_FLIPPER_BG_COLOR_INDEX
        }
        textAlignmentTextView.setOnClickListener {
            textViewFlipper.displayedChild = VIEW_FLIPPER_ALIGNMENT_INDEX
        }
        initFontListener()
        initColorPickerListener(
            colorPickerCardView,
            colorPickerLayout,
            gray1CardView,
            gray2CardView,
            gray3CardView,
            gray4CardView,
            colorPickerView,
            alphaSlideBar,
            brightnessSlideBar,
            colorPickerTextView,
            ColorMode.TEXT,
        )
        initColorPickerListener(
            bgColorPickerCardView,
            bgColorPickerLayout,
            bgGray1CardView,
            bgGray2CardView,
            bgGray3CardView,
            bgGray4CardView,
            bgColorPickerView,
            bgAlphaSlideBar,
            bgBrightnessSlideBar,
            bgColorPickerTextView,
            ColorMode.BACKGROUND,
        )
        textAlignLeftTextView.setOnClickListener {
            pickAlignment(it.id)
        }
        textAlignCenterTextView.setOnClickListener {
            pickAlignment(it.id)
        }
        textAlignRightTextView.setOnClickListener {
            pickAlignment(it.id)
        }
    }

    private fun initListener() {
        binding.discardImageView.onClick {
            backPressed()
        }
        binding.confirmImageView.onClick {
            viewModel.onConfirmClick(binding.textViewFlipper.displayedChild)
        }
        requireActivity().onBackPressedDispatcher.addCallback {
            viewModel.onBackClick(binding.textViewFlipper.displayedChild)
        }
        findNavController().getSavedStateHandle<String?>(KEY_TRUE_CLOUD_PHOTO_EDITOR_TEXT)
            ?.observe(viewLifecycleOwner) { _text ->
                if (_text != null) {
                    val style = TextStyleBuilder().apply {
                        getFont()?.let {
                            withTextFont(it)
                        }
                        withTextColor(Color.WHITE)
                        withGravity(getAlignment())
                    }
                    photoEditor.addText(_text, style)
                } else {
                    findNavController().popBackStack(R.id.photoEditorFragment, false)
                }
            }
    }

    private fun initFontListener() {
        binding.notoTextView.setOnClickListener {
            pickFont(it.id)
        }
        binding.notoBoldTextView.setOnClickListener {
            pickFont(it.id)
        }
        binding.sukhumvitTextView.setOnClickListener {
            pickFont(it.id)
        }
        binding.sukhumvitBoldTextView.setOnClickListener {
            pickFont(it.id)
        }
    }

    private fun initColorPickerListener(
        colorPickerCardView: CardView,
        colorPickerLayout: View,
        gray1CardView: CardView,
        gray2CardView: CardView,
        gray3CardView: CardView,
        gray4CardView: CardView,
        colorPickerView: ColorPickerView,
        alphaSlideBar: AlphaSlideBar,
        brightnessSlideBar: BrightnessSlideBar,
        colorPickerTextView: TextView,
        colorMode: ColorMode,
    ) {
        colorPickerCardView.setOnClickListener {
            colorPickerLayout.isVisible = !colorPickerLayout.isVisible
        }
        gray1CardView.setOnClickListener {
            colorPickerLayout.visibility = View.GONE
            pickColor(
                gray1CardView.cardBackgroundColor.defaultColor,
                colorPickerCardView,
                colorPickerTextView,
                colorMode
            )
        }
        gray2CardView.setOnClickListener {
            colorPickerLayout.visibility = View.GONE
            pickColor(
                gray2CardView.cardBackgroundColor.defaultColor,
                colorPickerCardView,
                colorPickerTextView,
                colorMode
            )
        }
        gray3CardView.setOnClickListener {
            colorPickerLayout.visibility = View.GONE
            pickColor(
                gray3CardView.cardBackgroundColor.defaultColor,
                colorPickerCardView,
                colorPickerTextView,
                colorMode
            )
        }
        gray4CardView.setOnClickListener {
            colorPickerLayout.visibility = View.GONE
            pickColor(
                gray4CardView.cardBackgroundColor.defaultColor,
                colorPickerCardView,
                colorPickerTextView,
                colorMode
            )
        }
        colorPickerView.setColorListener(object : ColorListener {
            override fun onColorSelected(color: Int, fromUser: Boolean) {
                pickColor(color, colorPickerCardView, colorPickerTextView, colorMode)
            }
        })
        colorPickerView.attachAlphaSlider(alphaSlideBar)
        colorPickerView.attachBrightnessSlider(brightnessSlideBar)
    }

    private fun pickColor(
        color: Int,
        colorPickerCardView: CardView,
        colorPickerTextView: TextView,
        colorMode: ColorMode,
    ) {
        val colorWithoutTransparent = color or COLOR_TRANSPARENT
        colorPickerCardView.setCardBackgroundColor(color)
        val contrast = ColorUtils.calculateContrast(Color.BLACK, colorWithoutTransparent)
        if (contrast > COLOR_PICKER_MIN_CONTRAST) {
            colorPickerTextView.setTextColor(Color.BLACK)
            colorPickerTextView.updateDrawableTint(Color.BLACK)
        } else {
            colorPickerTextView.setTextColor(Color.WHITE)
            colorPickerTextView.updateDrawableTint(Color.WHITE)
        }
        val style = when (colorMode) {
            ColorMode.TEXT -> {
                TextStyleBuilder().apply {
                    withTextColor(color)
                }
            }

            ColorMode.BACKGROUND -> {
                TextStyleBuilder().apply {
                    withBackgroundColor(color)
                }
            }
        }
        updateTextStyle(style)
    }

    private fun pickFont(id: Int) {
        binding.fontPickerLayout.children.flatMap {
            if (it is ViewGroup) {
                it.children
            } else {
                emptySequence()
            }
        }.filterIsInstance(AppCompatCheckedTextView::class.java).forEach { v ->
            v.isChecked = v.id == id
        }
        val style = TextStyleBuilder().apply {
            getFont()?.let {
                withTextFont(it)
            }
        }
        updateTextStyle(style)
    }

    private fun pickAlignment(id: Int) {
        binding.alignmentPickerLayout.forEach { v ->
            if (v is AppCompatCheckedTextView) {
                v.isChecked = v.id == id
            }
        }
        val style = TextStyleBuilder().apply {
            withGravity(getAlignment())
        }
        updateTextStyle(style)
    }

    private fun observeViewModel() {
        fileViewModel.onGenerateUri.observe(viewLifecycleOwner) { _uri ->
            findNavController().setSavedStateHandle(
                KEY_TRUE_CLOUD_PHOTO_EDITOR_IMAGE,
                _uri
            )
            findNavController().navigateUp()
        }
        viewModel.onSaveSetting.observe(viewLifecycleOwner) {
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
                        val errorMessage = e?.message ?: ""
                        binding.root.snackBar(
                            errorMessage,
                            R.color.true_cloudv3_color_toast_error
                        )
                    }
                },
            )
        }
        viewModel.onResetMenu.observe(viewLifecycleOwner) {
            binding.colorPickerLayout.gone()
            binding.bgColorPickerLayout.gone()
            binding.textViewFlipper.displayedChild = VIEW_FLIPPER_MAIN_INDEX
        }
        viewModel.onShowConfirm.observe(viewLifecycleOwner) {
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
    }

    private fun updateTextStyle(style: TextStyleBuilder) {
        val view = binding.photoEditorView.children.last()
        val textView = view.findViewById<TextView>(R.id.tvPhotoEditorText)
        if (textView != null) {
            photoEditor.editText(
                view,
                textView.text?.toString(),
                style,
            )
        }
    }

    private fun getFont(): Typeface? {
        val font = when {
            binding.notoTextView.isChecked -> R.font.noto_sans_thai_regular
            binding.notoBoldTextView.isChecked -> R.font.noto_sans_thai_bold
            binding.sukhumvitTextView.isChecked -> R.font.sukhumvit_tadmai_regular
            binding.sukhumvitBoldTextView.isChecked -> R.font.sukhumvit_tadmai_bold
            else -> R.font.noto_sans_thai_regular
        }
        return ResourcesCompat.getFont(requireContext(), font)
    }

    private fun getAlignment(): Int {
        return when {
            binding.textAlignLeftTextView.isChecked -> Gravity.START
            binding.textAlignCenterTextView.isChecked -> Gravity.CENTER
            binding.textAlignRightTextView.isChecked -> Gravity.END
            else -> Gravity.START
        }
    }

    private fun TextView.updateDrawableTint(@ColorInt color: Int) {
        compoundDrawables.forEach { drawable ->
            drawable?.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN)
        }
    }

    private enum class ColorMode {
        TEXT,
        BACKGROUND,
    }
}
