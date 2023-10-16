package com.truedigital.component.dialog.trueid

import androidx.fragment.app.DialogFragment

interface DialogInterface {

    interface OnClickListener {
        fun onClick(dialog: DialogFragment)
    }

    interface OnDismissListener {
        fun onDismiss(dialog: DialogFragment)
    }

    interface OnCancelListener {
        fun onCancel(dialog: DialogFragment)
    }
}
