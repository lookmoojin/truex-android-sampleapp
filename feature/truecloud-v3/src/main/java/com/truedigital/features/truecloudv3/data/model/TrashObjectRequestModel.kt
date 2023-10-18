package com.truedigital.features.truecloudv3.data.model

import com.google.gson.annotations.SerializedName

data class TrashObjectRequestModel(
    @SerializedName("object_ids")
    val objectIds: List<String>
)
