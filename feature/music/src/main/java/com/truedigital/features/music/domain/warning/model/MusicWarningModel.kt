package com.truedigital.features.music.domain.warning.model

import android.os.Parcelable
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class MusicWarningModel(
    @StringRes
    val title: Int? = null,
    @StringRes
    val description: Int? = null,
    @StringRes
    val cancelText: Int? = null,
    @StringRes
    val confirmText: Int? = null,

    val type: MusicWarningType,
) : Parcelable

enum class MusicWarningType {
    FORCE_ANSWER, CHOICE_ANSWER
}
