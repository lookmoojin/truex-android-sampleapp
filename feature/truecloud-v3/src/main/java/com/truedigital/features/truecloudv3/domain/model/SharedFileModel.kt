package com.truedigital.features.truecloudv3.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SharedFileModel(
    val fileUrl: String? = "",
    val id: String = "",
    val objectType: String? = "FILE",
    val category: String? = "IMAGE",
    val mimeType: String? = "image/jpeg",
    val name: String = "",
    var status: Int? = 200,
    var statusMessage: String? = ""
) : Parcelable
