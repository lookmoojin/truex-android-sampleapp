package com.truedigital.features.tuned.data.ad.model.request

import com.google.gson.annotations.SerializedName
import com.truedigital.features.tuned.data.ad.model.AdSourceType

data class TritonAdRequest(
    @SerializedName("TritonLsid") var lsid: String,
    @SerializedName("type") var type: AdSourceType,
    @SerializedName("ObjectId") var id: Int
)
