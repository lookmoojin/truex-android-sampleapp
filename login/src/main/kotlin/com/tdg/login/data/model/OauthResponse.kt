package com.tdg.login.data.model

import com.google.gson.annotations.SerializedName

data class OauthResponse(
    @SerializedName("access_token")
    val accessToken: String? = null,
    @SerializedName("refresh_token")
    val refreshToken: String? = null
)