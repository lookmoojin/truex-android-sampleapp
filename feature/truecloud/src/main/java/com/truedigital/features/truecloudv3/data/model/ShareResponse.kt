package com.truedigital.features.truecloudv3.data.model
import com.google.gson.annotations.SerializedName

data class ShareResponse(
    @SerializedName("data")
    var data: ShareData? = null,
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

data class ShareConfigResponse(
    @SerializedName("data")
    var data: SharedFile? = null,
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

data class ShareData(
    @SerializedName("shared_url")
    val sharedUrl: String?
)

data class SharedFile(
    @SerializedName("id")
    val id: String,
    @SerializedName("is_private")
    val isPrivate: Boolean = false,
    @SerializedName("expire_at")
    val expireAt: String? = null,
    @SerializedName("password")
    val password: String? = null,
    @SerializedName("updated_at")
    val updatedAt: String? = null,
    @SerializedName("created_at")
    val createdAt: String? = null,
)
