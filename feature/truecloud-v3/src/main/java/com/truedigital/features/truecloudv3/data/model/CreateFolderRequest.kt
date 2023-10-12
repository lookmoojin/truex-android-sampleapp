package com.truedigital.features.truecloudv3.data.model

import com.google.gson.annotations.SerializedName

data class CreateFolderRequest(
    @SerializedName("parent_object_id")
    val parentObjectId: String?,
    @SerializedName("name")
    val name: String?
)
