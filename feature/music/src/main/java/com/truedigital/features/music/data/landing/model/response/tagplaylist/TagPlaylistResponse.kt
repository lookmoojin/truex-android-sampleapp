package com.truedigital.features.music.data.landing.model.response.tagplaylist

import com.google.gson.annotations.SerializedName
import com.truedigital.features.music.data.trending.model.response.playlist.Translation

data class TagPlaylistResponse(
    @SerializedName("Count")
    val count: Int? = null,
    @SerializedName("Offset")
    val offset: Int? = null,
    @SerializedName("Results")
    val results: List<Result>? = emptyList(),
    @SerializedName("Total")
    val total: Int? = null
) {
    data class Result(
        @SerializedName("CoverImage")
        val coverImage: List<Translation>? = emptyList(),
        @SerializedName("Name")
        val name: List<Translation>? = emptyList(),
        @SerializedName("PlaylistId")
        val playlistId: Int? = null
    )
}
