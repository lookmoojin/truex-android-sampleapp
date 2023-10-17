package com.truedigital.features.tuned.data.station.model

import com.google.gson.annotations.SerializedName
import java.util.Date

data class Vote(
    @SerializedName("Id") var id: Int,
    @SerializedName("Vote") var vote: String,
    @SerializedName("Type") var type: String,
    @SerializedName("ActionDate") var actionDate: Date
)
