package com.truedigital.features.music.data.trending.model.response.playlist

import com.google.gson.annotations.SerializedName

data class PlaylistResponse(

    @SerializedName("PlaylistId")
    val playlistId: Int? = null,

    @SerializedName("Name")
    val name: List<Translation>? = null,

    @SerializedName("CoverImage")
    val coverImage: List<Translation>? = null,
)
