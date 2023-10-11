package com.truedigital.features.truecloudv3.data.model

import com.google.gson.annotations.SerializedName

data class RenameRequest(
    @SerializedName("name")
    val name: String?
)
