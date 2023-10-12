package com.truedigital.features.truecloudv3.data.model

import com.google.gson.annotations.SerializedName

data class ShareConfigRequest(
    @SerializedName("is_private")
    var isPrivate: Boolean? = null,
    @SerializedName("password")
    var password: String? = null,
    @SerializedName("expire_at")
    var expireAt: String? = null,
    @SerializedName("is_new_password")
    var isNewPassword: Boolean? = null
)
