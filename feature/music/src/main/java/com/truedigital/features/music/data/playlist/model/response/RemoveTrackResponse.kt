package com.truedigital.features.music.data.playlist.model.response

import com.google.gson.annotations.SerializedName

data class RemoveTrackResponse(
    @SerializedName("Value")
    val value: Boolean = false
)
