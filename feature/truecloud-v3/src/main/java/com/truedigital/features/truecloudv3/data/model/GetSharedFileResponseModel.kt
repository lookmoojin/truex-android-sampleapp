package com.truedigital.features.truecloudv3.data.model

import com.google.gson.annotations.SerializedName

data class GetSharedFileResponseModel(
    @SerializedName("code")
    var code: Int? = null,
    @SerializedName("message")
    var message: String? = null,
    @SerializedName("platform_module")
    val platformModule: Int? = null,
    @SerializedName("report_dashboard")
    val reportDashboard: Int? = null,
    @SerializedName("data")
    var data: SharedFileData? = null,
    @SerializedName("status_code")
    val statusCode: Int? = 200,
    @SerializedName("status_message")
    val statusMessage: String? = ""
)

data class SharedFileData(
    @SerializedName("id")
    val id: String? = "",
    @SerializedName("updated_at")
    var updatedAt: String? = null,
    @SerializedName("created_at")
    var createdAt: String? = null,
    @SerializedName("object")
    val fileObject: SharedFileObject? = null
)

data class SharedFileObject(
    @SerializedName("file_url")
    val fileUrl: String? = null,
    @SerializedName("id")
    val id: String? = null,
    @SerializedName("object_type")
    val objectType: String? = "",
    @SerializedName("category")
    val category: String? = "",
    @SerializedName("cover_image_key")
    val coverImageKey: String? = null,
    @SerializedName("mime_type")
    val mimeType: String? = "",
    @SerializedName("name")
    val name: String? = "",
    @SerializedName("updated_at")
    var updatedAt: String? = null,
    @SerializedName("created_at")
    var createdAt: String? = null
)
