package com.truedigital.features.tuned.data.artist.model.response

import com.google.gson.annotations.SerializedName

data class ArtistProfile(
    @SerializedName("Identity") var identity: Identity,
    @SerializedName("Image") var image: String
) {
    data class Identity(
        @SerializedName("ArtistId") var id: Int,
        @SerializedName("Name") var name: String
    )
}
