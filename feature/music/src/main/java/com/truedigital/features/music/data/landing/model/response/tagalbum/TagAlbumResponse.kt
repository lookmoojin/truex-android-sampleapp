package com.truedigital.features.music.data.landing.model.response.tagalbum

import com.google.gson.annotations.SerializedName

data class TagAlbumResponse(
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
        @SerializedName("AlbumId")
        val albumId: Int? = null,
        @SerializedName("Name")
        val name: String? = null,
        @SerializedName("PrimaryRelease")
        val primaryRelease: PrimaryRelease? = null,
        @SerializedName("Artists")
        val artists: List<Artist>? = null
    )

    data class Artist(
        @SerializedName("Name")
        val name: String? = null
    )

    data class PrimaryRelease(
        @SerializedName("ReleaseId")
        val releaseId: Int? = null,
        @SerializedName("Image")
        val image: String? = null
    )
}
