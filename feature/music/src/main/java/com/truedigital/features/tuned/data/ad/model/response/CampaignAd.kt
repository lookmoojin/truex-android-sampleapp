package com.truedigital.features.tuned.data.ad.model.response

import com.google.gson.annotations.SerializedName
import com.truedigital.features.tuned.data.ad.model.Ad

data class CampaignAd(
    @SerializedName("VastObject") val ad: Ad,
    @SerializedName("VastData") val xml: String
)
