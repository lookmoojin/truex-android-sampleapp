package com.truedigital.features.truecloudv3.data.model

import com.google.gson.annotations.SerializedName

data class CompleteUploadResponse(
    @SerializedName("data")
    val data: TrueCloudV3StorageData? = null,
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
