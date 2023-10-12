package com.truedigital.features.truecloudv3.data.model

import com.google.gson.annotations.SerializedName

data class InitialDataResponse(
    @SerializedName("id")
    val id: String? = "",
    @SerializedName("folder_id")
    val folderId: String? = null,
    @SerializedName("mime_type")
    val mimeType: String? = null,
    @SerializedName("size")
    val size: Int? = null,
    @SerializedName("s3")
    val s3: S3Model? = null
)
