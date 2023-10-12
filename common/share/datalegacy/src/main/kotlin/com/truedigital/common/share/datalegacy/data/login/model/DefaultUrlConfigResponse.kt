package com.truedigital.common.share.datalegacy.data.login.model

import com.google.gson.annotations.SerializedName

data class DefaultUrlConfigResponse(
    @SerializedName("alpha")
    val alpha: DefaultSignInUrl? = null,

    @SerializedName("preprod")
    val preprod: DefaultSignInUrl? = null,

    @SerializedName("production")
    val production: DefaultSignInUrl? = null
)

data class DefaultSignInUrl(
    @SerializedName("default_signin_url")
    val signInUrl: String? = null
)
