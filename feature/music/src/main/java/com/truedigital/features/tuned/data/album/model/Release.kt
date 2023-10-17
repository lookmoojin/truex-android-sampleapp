package com.truedigital.features.tuned.data.album.model

import com.google.gson.annotations.SerializedName
import com.truedigital.features.tuned.data.artist.model.ArtistInfo
import com.truedigital.features.tuned.data.product.model.Product
import java.util.Calendar
import java.util.Date

data class Release(
    @SerializedName("ReleaseId") override val id: Int,
    @SerializedName("AlbumId") val albumId: Int,
    @SerializedName("Artists") val artists: List<ArtistInfo>,
    @SerializedName("Name") val name: String,
    @SerializedName("IsExplicit") val isExplicit: Boolean,
    @SerializedName("NumberOfVolumes") val numberOfVolumes: Int,
    @SerializedName("TrackIds") val trackIds: List<Int>,
    @SerializedName("Duration") val duration: Int,
    @SerializedName("Volumes") val volumes: List<Volume>,
    @SerializedName("Image") val image: String,
    @SerializedName("WebPath") val webPath: String?,
    @SerializedName("Copyright") val copyRight: String?,
    @SerializedName("Label") val label: Label?,
    @SerializedName("OriginalReleaseDate") val originalReleaseDate: Date?,
    @SerializedName("PhysicalReleaseDate") val physicalReleaseDate: Date?,
    @SerializedName("DigitalReleaseDate") val digitalReleaseDate: Date?,
    @SerializedName("SaleAvailabilityDateTime") val saleAvailabilityDateTime: Date?,
    @SerializedName("StreamAvailabilityDateTime") val streamAvailabilityDateTime: Date?,
    @SerializedName("AllowDownload") val allowDownload: Boolean,
    @SerializedName("AllowStream") val allowStream: Boolean
) : Product {

    companion object {
        private const val MINUTES_60 = 60
        private const val SECOND_3600 = 3600
    }

    fun releaseYear(): Int? =
        originalReleaseDate?.let {
            val calendar = Calendar.getInstance()
            calendar.time = it
            calendar[Calendar.YEAR]
        }

    fun getArtistString(variousString: String): String =
        if (artists.isNotEmpty()) artistString else variousString

    val artistString: String
        get() = artists.joinToString(",") { it.name }

    fun getDurationString(): String {
        if (duration <= 0) {
            return "0m"
        }

        // Example formats 1h23m
        val hours = duration / SECOND_3600
        val minutes = duration / MINUTES_60 - (hours * MINUTES_60)
        val durationStringBuilder = StringBuilder()
        if (hours > 0) {
            durationStringBuilder.append(hours)
            durationStringBuilder.append("h")
        }
        if (minutes > 0) {
            durationStringBuilder.append(minutes)
            durationStringBuilder.append("m")
        }

        return durationStringBuilder.toString()
    }
}
