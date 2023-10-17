package com.truedigital.features.tuned.data.album.model

import com.google.gson.annotations.SerializedName

data class Volume(
    @SerializedName("FirstTrackIndex") val firstTrackIndex: Int,
    @SerializedName("LastTrackIndex") val lastTrackIndex: Int
)
