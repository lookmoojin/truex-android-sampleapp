package com.truedigital.features.truecloudv3.data.model

import com.google.gson.annotations.SerializedName

data class SharedObjectAccessResponseModel(
    @SerializedName("code")
    var code: Int? = null,
    @SerializedName("message")
    var message: String? = null,
    @SerializedName("platform_module")
    val platformModule: Int? = null,
    @SerializedName("report_dashboard")
    val reportDashboard: Int? = null,
    @SerializedName("data")
    val accessToken: ShareObjectAccessToken? = null
)

data class ShareObjectAccessToken(
    @SerializedName("shared_object_access_token")
    val sharedObjectAccessToken: String? = null
)
