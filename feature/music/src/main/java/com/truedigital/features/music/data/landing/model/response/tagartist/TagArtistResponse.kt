package com.truedigital.features.music.data.landing.model.response.tagartist

import com.google.gson.annotations.SerializedName

data class TagArtistResponse(
    @SerializedName("Count")
    val count: Int? = null,
    @SerializedName("Offset")
    val offset: Int? = null,
    @SerializedName("Results")
    val results: List<Result?>? = emptyList(),
    @SerializedName("Total")
    val total: Int? = null
) {
    data class Result(
        @SerializedName("ArtistId")
        val artistId: Int? = null,
        @SerializedName("Image")
        val image: String? = null,
        @SerializedName("Name")
        val name: String? = null
    )
}
