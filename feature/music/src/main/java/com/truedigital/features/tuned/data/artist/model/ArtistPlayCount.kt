package com.truedigital.features.tuned.data.artist.model

import com.google.gson.annotations.SerializedName

data class ArtistPlayCount(
    @SerializedName("GlobalTotal") var globalTotal: Int,
    @SerializedName("GlobalRecent") var globalRecent: Int,
    @SerializedName("DistinctGlobalTotal") var distinctGlobalTotal: Int,
    @SerializedName("DistinctGlobalRecent") var distinctGlobalRecent: Int
)
