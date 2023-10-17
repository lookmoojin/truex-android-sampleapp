package com.truedigital.features.tuned.data.album.model

import com.google.gson.annotations.SerializedName

data class Label(
    @SerializedName("LabelId") val id: Int?,
    @SerializedName("Name") val name: String?
)
