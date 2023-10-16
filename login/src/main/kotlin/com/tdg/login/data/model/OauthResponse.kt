package com.tdg.login.data.model

import com.google.gson.annotations.SerializedName

data class OauthResponse(
    @SerializedName("access_token")
    val accessToken: String? = null,
    @SerializedName("refresh_token")
    val refreshToken: String? = null,
    @SerializedName("error")
    val error: Error? = null,
)

data class Error(
    @SerializedName("code")
    val code: String? = null,
    @SerializedName("messages")
    val messages: Messages? = Messages(),
    @SerializedName("message")
    val message: String? = null
) {
    data class Messages(
        @SerializedName("th")
        val th: String? = null,
        @SerializedName("en")
        val en: String? = null
    )
}
