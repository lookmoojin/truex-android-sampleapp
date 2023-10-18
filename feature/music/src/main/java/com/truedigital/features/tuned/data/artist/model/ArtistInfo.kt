package com.truedigital.features.tuned.data.artist.model

import com.google.gson.annotations.SerializedName

data class ArtistInfo(
    @SerializedName("ArtistId") var id: Int,
    @SerializedName("Name") var name: String
)
