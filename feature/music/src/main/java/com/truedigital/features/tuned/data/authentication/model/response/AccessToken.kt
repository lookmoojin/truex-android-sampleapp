package com.truedigital.features.tuned.data.authentication.model.response

import com.google.gson.annotations.SerializedName

data class AccessToken(
    @SerializedName("access_token") var accessToken: String = "",
    @SerializedName("token_type") var tokenType: String = "",
    @SerializedName("expires_in") var expiresIn: Long = 0L,
    @SerializedName("refresh_token") var refreshToken: String = ""
)
