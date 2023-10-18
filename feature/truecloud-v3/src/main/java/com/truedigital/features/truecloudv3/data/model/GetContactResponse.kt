package com.truedigital.features.truecloudv3.data.model

import com.google.gson.annotations.SerializedName

data class GetContactResponse(
    @SerializedName("data")
    val data: ContactData? = null,
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

data class ContactData(
    @SerializedName("id")
    val id: String = "",
    @SerializedName("user_id")
    val userId: String? = null,
    @SerializedName("object_type")
    val objectType: String? = null,
    @SerializedName("parent_object_id")
    val parentObjectId: String? = null,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("mime_type")
    val mimeType: String? = null,
    @SerializedName("cover_image_key")
    val coverImageKey: String? = null,
    @SerializedName("cover_image_size")
    val coverImageSize: String? = null,
    @SerializedName("category")
    val category: String? = null,
    @SerializedName("size")
    val size: String? = null,
    @SerializedName("checksum")
    val checksum: String? = null,
    @SerializedName("is_uploaded")
    val isUploaded: Boolean = false,
    @SerializedName("device_id")
    val deviceId: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String = "",
    @SerializedName("created_at")
    val createdAt: String? = null,
    @SerializedName("last_modified")
    val lastModified: String? = null
)
