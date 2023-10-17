package com.truedigital.features.music.data.trending.model.response.album

import com.google.gson.annotations.SerializedName

data class AlbumResponse(

    @SerializedName("AlbumId")
    val albumId: Int? = null,

    @SerializedName("Name")
    val name: String? = null,

    @SerializedName("Artists")
    val artists: List<AlbumArtist> = emptyList(),

    @SerializedName("AlbumType")
    val albumType: String? = null,

    @SerializedName("PrimaryRelease")
    val primaryRelease: AlbumPrimaryRelease? = null
)

data class AlbumArtist(
    @SerializedName("ArtistId")
    val artistId: Int? = null,

    @SerializedName("Name")
    val name: String? = null
)

data class AlbumPrimaryRelease(
    @SerializedName("Image")
    val image: String? = null
)
