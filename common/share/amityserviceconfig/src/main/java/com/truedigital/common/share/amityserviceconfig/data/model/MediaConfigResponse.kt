package com.truedigital.common.share.amityserviceconfig.data.model

import com.google.gson.annotations.SerializedName

data class MediaConfigResponse(
    @SerializedName("enable")
    var enable: Enable? = null,
)
