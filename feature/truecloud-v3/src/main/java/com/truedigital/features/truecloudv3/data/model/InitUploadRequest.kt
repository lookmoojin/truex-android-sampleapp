package com.truedigital.features.truecloudv3.data.model

import com.google.gson.annotations.SerializedName

data class InitUploadRequest(
    @SerializedName("parent_object_id")
    val parentObjectId: String?,
    @SerializedName("name")
    val name: String,
    @SerializedName("mime_type")
    val mimeType: String,
    @SerializedName("size")
    val size: Int
)
