package com.truedigital.features.truecloudv3.data.model

import com.google.gson.annotations.SerializedName

data class S3Model(
    @SerializedName("bucket")
    val bucket: String? = null,
    @SerializedName("endpoint")
    val endpoint: String? = null,
    @SerializedName("secure_token_service")
    val secureTokenService: SecureTokenService? = null
)

data class SecureTokenService(
    @SerializedName("access_token")
    val accessToken: String? = null,
    @SerializedName("secret_key")
    val secretKey: String? = null,
    @SerializedName("session_key")
    val sessionKey: String? = null,
    @SerializedName("expires_at")
    val expiresAt: String? = null
)
