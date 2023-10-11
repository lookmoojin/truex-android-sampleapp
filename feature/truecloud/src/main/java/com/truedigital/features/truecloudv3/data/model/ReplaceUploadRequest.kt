package com.truedigital.features.truecloudv3.data.model

import com.google.gson.annotations.SerializedName

data class ReplaceUploadRequest(
    @SerializedName("re_upload_status")
    val status: String,
    @SerializedName("cover_image_key")
    val coverImageKey: String?,
    @SerializedName("cover_image_size")
    val coverImageSize: Int?
)
