package com.truedigital.features.music.data.landing.model.response.playlisttrack

import com.google.gson.annotations.SerializedName
import com.truedigital.features.music.data.trending.model.response.playlist.Translation

data class PlaylistTrackResponse(
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
        @SerializedName("Artists")
        val artist: List<Artist>? = emptyList(),
        @SerializedName("Image")
        val image: String? = null,
        @SerializedName("Name")
        val name: String? = null,
        @SerializedName("PlaylistTrackId")
        val playlistTrackId: Int? = null,
        @SerializedName("TrackId")
        val trackId: Int? = null,
        @SerializedName("Translations")
        val translations: List<Translation> = emptyList(),
        @SerializedName("Position")
        val position: String? = null,

        @SerializedName("SongId")
        var songId: Int? = null,
        @SerializedName("ReleaseId")
        var releaseId: Int? = null,
        @SerializedName("ReleaseName")
        var releaseName: String? = null,
        @SerializedName("IsExplicit")
        val isExplicit: Boolean? = false,
        @SerializedName("Duration")
        val duration: Long? = null,
        @SerializedName("HasLyrics")
        val hasLyrics: Boolean? = false,
        @SerializedName("IsVideo")
        val isVideo: Boolean? = false,
        @SerializedName("Owner")
        val owner: String? = null,
        @SerializedName("Label")
        val label: String? = null,
        @SerializedName("ContentLanguage")
        val contentLanguage: String? = null,
        @SerializedName("Genre")
        val genreList: List<String>? = null
    ) {
        data class Artist(
            @SerializedName("ArtistId")
            val artistId: Int? = null,
            @SerializedName("Name")
            val name: String? = null
        )
    }
}
