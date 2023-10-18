package com.truedigital.features.tuned.data.ad.model

import com.google.gson.annotations.SerializedName

class Ad(
    @SerializedName("Title") val title: String,
    @SerializedName("ImpressionUrl") val impressionUrl: String,
    @SerializedName("Duration") val duration: String,
    @SerializedName("MediaFile") val mediaFile: String,
    @SerializedName("Image") val image: String,
    @SerializedName("ClickUrl") val clickUrl: String,
    var vast: String
)
