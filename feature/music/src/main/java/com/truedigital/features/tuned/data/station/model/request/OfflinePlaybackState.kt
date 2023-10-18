package com.truedigital.features.tuned.data.station.model.request

import com.google.gson.annotations.SerializedName
import java.util.Date

data class OfflinePlaybackState(
    @SerializedName("When") val date: Date,
    @SerializedName("Log") val playbackState: PlaybackState
)
