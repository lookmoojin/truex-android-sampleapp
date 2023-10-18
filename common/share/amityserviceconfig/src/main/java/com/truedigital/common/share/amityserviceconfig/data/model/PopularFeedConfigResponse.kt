package com.truedigital.common.share.amityserviceconfig.data.model

import com.google.gson.annotations.SerializedName

data class PopularFeedConfigResponse(
    @SerializedName("enable")
    var enable: Enable? = null,
)

data class Enable(
    @SerializedName("android")
    var android: Boolean? = null,

    @SerializedName("ios")
    var ios: Boolean? = null
)
