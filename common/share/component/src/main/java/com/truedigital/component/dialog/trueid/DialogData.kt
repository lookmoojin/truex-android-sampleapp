package com.truedigital.component.dialog.trueid

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DialogData(
    val type: DialogType,
    var iconType: DialogIconType = DialogIconType.OTHER,
    var title: String = "",
    var subtitle: String = "",
    var contentAlignment: DialogContentAlignment = DialogContentAlignment.CENTER,
    var isCancelable: Boolean = false,
    var isCanceledOnTouchOutSide: Boolean = false,
    var primaryButton: DialogButtonData = DialogButtonData(),
    var secondaryButton: DialogButtonData = DialogButtonData(),
    var tertiaryButton: DialogButtonData = DialogButtonData(),
    var theme: DialogTheme = DialogTheme.LIGHT,
    var topNavigation: DialogTopNavigationType = DialogTopNavigationType.BACK_BUTTON
) : Parcelable
