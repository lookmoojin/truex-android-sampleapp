package com.truedigital.features.music.data.trending.model.response.artist

import com.google.gson.annotations.SerializedName

data class MusicTrendingArtistResponse(

    @SerializedName("Offset")
    val offset: Int? = null,

    @SerializedName("Count")
    val count: Int? = null,

    @SerializedName("Total")
    val total: Int? = null,

    @SerializedName("Results")
    val results: List<ArtistResponse>? = null
)
