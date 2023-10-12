package com.truedigital.features.truecloudv3.data.model

import com.google.gson.annotations.SerializedName

data class ObjectInfoResponse(
    @SerializedName("data")
    var data: TrueCloudV3ObjectInfo? = null,
    @SerializedName("error")
    val error: ErrorResponse? = null,
    @SerializedName("code")
    val code: Int = 0,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("platform_module")
    val platformModule: String? = null,
    @SerializedName("report_dashboard")
    val reportDashboard: String? = null
)

data class TrueCloudV3ObjectInfo(
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("parent_object_id")
    var parentObjectId: String? = null,
    @SerializedName("object_type")
    var objectType: String? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("size")
    var size: String? = null,
    @SerializedName("mime_type")
    var mimeType: String? = null,
    @SerializedName("category")
    var category: String? = null,
    @SerializedName("cover_image_key")
    var coverImageKey: String? = null,
    @SerializedName("cover_image_size")
    var coverImageSize: String? = null,
    @SerializedName("updated_at")
    var updatedAt: String? = null,
    @SerializedName("created_at")
    var createdAt: String? = null,
    @SerializedName("file_url")
    var fileUrl: String? = null,
)
