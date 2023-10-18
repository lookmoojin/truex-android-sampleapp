package com.truedigital.features.tuned.data.station.model.response

import com.google.gson.annotations.SerializedName
import com.truedigital.features.tuned.data.ad.model.response.CampaignAd
import com.truedigital.features.tuned.data.station.model.Stakkar

data class TrackExtras(
    @SerializedName("Stakkars") var stakkars: List<Stakkar>,
    @SerializedName("CampaignAds") var campaignAds: List<CampaignAd>
)
