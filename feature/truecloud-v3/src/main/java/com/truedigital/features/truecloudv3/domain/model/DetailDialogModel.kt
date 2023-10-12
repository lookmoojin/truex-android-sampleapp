package com.truedigital.features.truecloudv3.domain.model

import android.os.Parcelable
import com.truedigital.component.dialog.trueid.DialogIconType
import com.truedigital.features.truecloudv3.domain.usecase.NodePermission
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetailDialogModel(
    val nodePermission: NodePermission,
    val iconType: DialogIconType,
    val title: String,
    val subTitle: String
) : Parcelable
