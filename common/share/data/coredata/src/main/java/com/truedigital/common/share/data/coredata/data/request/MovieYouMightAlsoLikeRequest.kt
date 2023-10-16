package com.truedigital.common.share.data.coredata.data.request

import com.google.gson.annotations.SerializedName

class MovieYouMightAlsoLikeRequest {
    @SerializedName("globalItemId")
    var globalItemId: String = ""
    @SerializedName("deviceId")
    var deviceId: String? = ""
    @SerializedName("ssoId")
    var ssoId: String? = ""
    @SerializedName("content_rights")
    var contentRights: String? = ""
    @SerializedName("maxItems")
    var maxItems: String? = ""
    @SerializedName("language")
    var language: String? = ""
    @SerializedName("is_vod_layer")
    var isVodLayer: String? = null
}
