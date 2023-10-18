package com.truedigital.common.share.datalegacy.data.recommend.model.request

import com.google.gson.annotations.SerializedName

class RecommendedDataRequest {
    @SerializedName("deviceId")
    var deviceId: String? = null
    @SerializedName("ssoId")
    var ssoId: String? = null
    @SerializedName("maxItems")
    var maxItems: String? = null
    @SerializedName("language")
    var language: String? = null
    @SerializedName("isVodLayer")
    var isVodLayer: String? = null
}
