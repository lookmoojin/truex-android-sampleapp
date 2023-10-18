package com.truedigital.features.music.presentation.warning

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.truedigital.core.extensions.viewBinding
import com.truedigital.features.music.domain.warning.model.MusicWarningModel
import com.truedigital.features.music.domain.warning.model.MusicWarningType
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.databinding.DialogMusicWarningBinding
import com.truedigital.foundation.extension.gone
import com.truedigital.foundation.extension.parcelable
import com.truedigital.foundation.extension.visible

class MusicWarningBottomSheetDialog : BottomSheetDialogFragment() {

    companion object {
        val TAG = MusicWarningBottomSheetDialog::class.simpleName
        const val MUSIC_WARNING_MODEL = "music_warning"
        const val MUSIC_WARNING_DIALOG_REQUEST_CODE = "WARNING_DIALOG_REQUEST_CODE"
        const val IS_CONFIRM_BUTTON_CLICKED = "IS_CONFIRM_BUTTON_CLICKED"

        fun newInstance(musicWarningModel: MusicWarningModel): MusicWarningBottomSheetDialog {
            return MusicWarningBottomSheetDialog().apply {
                this.arguments = Bundle().apply {
                    this.putParcelable(MUSIC_WARNING_MODEL, musicWarningModel)
                }
            }
        }
    }

    private val binding by viewBinding(DialogMusicWarningBinding::bind)
    private val musicWarningArgs: MusicWarningModel?
        get() = arguments?.parcelable(MUSIC_WARNING_MODEL)

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            isCancelable = false
            setOnShowListener {
                val bottomSheet =
                    findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
                bottomSheet.setBackgroundResource(R.color.transparent)
                BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_music_warning, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initOnClick()
        renderTitle()
        renderDescription()
        renderButton()
    }

    private fun initOnClick() = with(binding) {
        warningConfirmButton.setOnClickListener {
            setFragmentResult(
                MUSIC_WARNING_DIALOG_REQUEST_CODE,
                bundleOf(IS_CONFIRM_BUTTON_CLICKED to true)
            )
            dismiss()
        }
        warningCancelButton.setOnClickListener {
            setFragmentResult(
                MUSIC_WARNING_DIALOG_REQUEST_CODE,
                bundleOf(IS_CONFIRM_BUTTON_CLICKED to false)
            )
            dismiss()
        }
    }

    private fun renderTitle() = with(binding) {
        musicWarningArgs?.title?.let {
            warningTitleTextView.text = context?.getString(it)
        }
    }

    private fun renderDescription() = with(binding) {
        musicWarningArgs?.description?.let {
            warningDescriptionTextView.text = context?.getString(it)
        }
    }

    private fun renderButton() {
        musicWarningArgs?.let {
            when (it.type) {
                MusicWarningType.FORCE_ANSWER -> {
                    renderForceAnswer(it)
                }
                MusicWarningType.CHOICE_ANSWER -> {
                    renderChoiceAnswer(it)
                }
            }
        }
    }

    private fun renderForceAnswer(musicWarningModel: MusicWarningModel) = with(binding) {
        musicWarningModel.confirmText?.let { confirmText ->
            warningConfirmButton.text = context?.getString(confirmText)
            warningConfirmButton.visible()
            warningCancelButton.gone()
        }
    }

    private fun renderChoiceAnswer(musicWarningModel: MusicWarningModel) = with(binding) {
        musicWarningModel.cancelText?.let { cancelText ->
            warningCancelButton.text = context?.getString(cancelText)
            warningCancelButton.visible()
        }
        musicWarningModel.confirmText?.let { confirmText ->
            warningConfirmButton.text = context?.getString(confirmText)
            warningConfirmButton.visible()
        }
    }
}
