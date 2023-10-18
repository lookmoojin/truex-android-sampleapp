package com.truedigital.features.music.data.trending.model.response.album

import com.google.gson.annotations.SerializedName

data class MusicTrendingAlbumResponse(

    @SerializedName("Offset")
    val offset: Int? = null,

    @SerializedName("Count")
    val count: Int? = null,

    @SerializedName("Total")
    val total: Int? = null,

    @SerializedName("Results")
    val results: List<AlbumResponse>? = null
)
