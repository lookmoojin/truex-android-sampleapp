package com.truedigital.features.truecloudv3.data.model

import com.google.gson.annotations.SerializedName

data class GetSecureTokenServiceResponse(
    @SerializedName("data")
    val data: SecureTokenServiceDataResponse? = null,
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

data class SecureTokenServiceDataResponse(
    @SerializedName("access_token")
    val accessToken: String? = null,
    @SerializedName("secret_key")
    val secretKey: String? = null,
    @SerializedName("session_key")
    val sessionKey: String? = null,
    @SerializedName("expires_at")
    val expiresAt: String? = null,
    @SerializedName("endpoint")
    val endpoint: String? = null
)
