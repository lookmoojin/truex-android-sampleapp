package com.truedigital.features.tuned.data.station.model

import com.google.gson.annotations.SerializedName
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.track.model.Track

data class LikedTrack(
    @SerializedName("Type") var type: Rating,
    @SerializedName("Track") var track: Track?,
    @SerializedName("Artists") var artists: List<Artist>
) {

    fun getArtistString(variousString: String): String =
        if (artists.isNotEmpty()) artistString else variousString

    val artistString: String
        get() = artists.joinToString(",") { it.name }
}
