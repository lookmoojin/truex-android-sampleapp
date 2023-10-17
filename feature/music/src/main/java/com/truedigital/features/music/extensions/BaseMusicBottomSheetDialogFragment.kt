package com.truedigital.features.music.extensions

import android.view.View
import androidx.fragment.app.setFragmentResultListener
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.truedigital.component.dialog.TrueIDProgressDialog
import com.truedigital.features.music.domain.warning.model.MusicWarningModel
import com.truedigital.features.music.presentation.warning.MusicWarningBottomSheetDialog
import com.truedigital.features.music.presentation.warning.MusicWarningBottomSheetDialog.Companion.IS_CONFIRM_BUTTON_CLICKED
import com.truedigital.features.music.presentation.warning.MusicWarningBottomSheetDialog.Companion.MUSIC_WARNING_DIALOG_REQUEST_CODE
import com.truedigital.foundation.extension.clearKeyboardFocus

open class BaseMusicBottomSheetDialogFragment : BottomSheetDialogFragment() {

    private var progressDialog: TrueIDProgressDialog? = null

    protected fun showProgress() {
        context?.let { _context ->
            if (progressDialog?.isShowing == true) {
                progressDialog?.dismiss()
            }
            progressDialog = TrueIDProgressDialog(_context).apply {
                setCancelable(false)
                show()
            }
        }
    }

    protected fun hideProgress() {
        if (progressDialog?.isShowing == true) {
            progressDialog?.dismiss()
        }
    }

    protected fun showMusicWarningDialog(
        musicWarningModel: MusicWarningModel,
        onConfirmClicked: (() -> Unit)? = null,
        onCancelClicked: (() -> Unit)? = null
    ) {
        MusicWarningBottomSheetDialog.newInstance(musicWarningModel).let { dialog ->
            dialog.show(requireActivity().supportFragmentManager, MusicWarningBottomSheetDialog.TAG)
            dialog.setFragmentResultListener(MUSIC_WARNING_DIALOG_REQUEST_CODE) { _, result ->
                result.getBoolean(IS_CONFIRM_BUTTON_CLICKED).let { isConfirmButtonClicked ->
                    if (isConfirmButtonClicked) {
                        onConfirmClicked?.invoke()
                    } else {
                        onCancelClicked?.invoke()
                    }
                }
            }
        }
    }

    protected fun hideSoftKeyboard(view: View) {
        view.clearKeyboardFocus()
    }
}
