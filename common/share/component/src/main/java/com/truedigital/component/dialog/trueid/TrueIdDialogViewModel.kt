package com.truedigital.component.dialog.trueid

import androidx.lifecycle.ViewModel

class TrueIdDialogViewModel : ViewModel() {
    var onPrimaryClick: DialogInterface.OnClickListener? = null
    var onSecondaryClick: DialogInterface.OnClickListener? = null
    var onTertiaryClick: DialogInterface.OnClickListener? = null
    var onBackButtonClick: DialogInterface.OnDismissListener? = null
    var onCloseButtonClick: DialogInterface.OnDismissListener? = null
    var onDismissListener: DialogInterface.OnDismissListener? = null
    var onCancelListener: DialogInterface.OnCancelListener? = null
}
