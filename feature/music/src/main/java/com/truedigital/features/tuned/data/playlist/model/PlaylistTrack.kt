package com.truedigital.features.tuned.data.playlist.model

import com.google.gson.annotations.SerializedName

data class PlaylistTrack(
    @SerializedName("PlaylistTrackId") var id: Int,
    @SerializedName("TrackId") var trackId: Int
)
