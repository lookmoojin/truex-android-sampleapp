package com.truedigital.features.music.data.trending.model.response.artist

import com.google.gson.annotations.SerializedName

data class ArtistResponse(

    @SerializedName("ArtistId")
    val artistId: Int? = null,

    @SerializedName("Name")
    val name: String? = null,

    @SerializedName("Image")
    val image: String? = null
)
