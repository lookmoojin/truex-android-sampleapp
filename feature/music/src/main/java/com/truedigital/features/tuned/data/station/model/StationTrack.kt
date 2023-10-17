package com.truedigital.features.tuned.data.station.model

import com.google.gson.annotations.SerializedName
import com.truedigital.features.tuned.data.track.model.Track

data class StationTrack(
    @SerializedName("StationTrackId") var stationTrackId: Int,
    @SerializedName("Track") var track: Track,
    @SerializedName("Score") var score: Double,
    @SerializedName("Vote") var vote: Rating?
)
