package com.truedigital.features.truecloudv3.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CustomPhoneLabelModel(
    val tagId: Int,
    var label: String = "",
    var number: String = ""
) : Parcelable
