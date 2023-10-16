package com.truedigital.component.dialog.trueid

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DialogButtonData(
    val text: String = ""
) : Parcelable
