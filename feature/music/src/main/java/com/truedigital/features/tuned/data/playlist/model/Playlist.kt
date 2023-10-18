package com.truedigital.features.tuned.data.playlist.model

import android.content.Context
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.truedigital.core.constant.DateFormatConstant.dd_MMM_yyyy
import com.truedigital.features.tuned.R
import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.database.entity.PlaylistEntity
import com.truedigital.features.tuned.data.player.PlayerSource
import com.truedigital.features.tuned.data.player.model.MediaType
import com.truedigital.features.tuned.data.product.model.Product
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.tag.model.TypedTag
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.util.LocalisedString
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Playlist(
    @SerializedName("PlaylistId") override var id: Int,
    @SerializedName("Name") var name: List<LocalisedString>,
    @SerializedName("Description") var description: List<LocalisedString> = emptyList(),
    @SerializedName("CreatorId") var creatorId: Int = 0,
    @SerializedName("CreatorName") var creatorName: String? = null,
    @SerializedName("CreatorImage") var creatorImage: String? = null,
    @SerializedName("TrackCount") var trackCount: Int = 0,
    @SerializedName("PublicTrackCount") var publicTrackCount: Int? = null,
    @SerializedName("DurationSecs") var duration: Int = 0,
    @SerializedName("DateCreated") var createDate: Date = Date(),
    @SerializedName("DateUpdated") var updateDate: Date = Date(),
    @SerializedName("TrackIds") var trackIds: List<PlaylistTrack> = emptyList(),
    @SerializedName("CoverImage") var coverImage: List<LocalisedString> = emptyList(),
    @SerializedName("IsVideo") var isVideo: Boolean = false,
    @SerializedName("IsPublic") var isPublic: Boolean = false,
    @SerializedName("TypedTags") var typedTags: List<TypedTag>? = null,
    var isOwner: Boolean = false
) : PlayerSource, Product {
    // not a fan of parcelable, use gson here
    companion object {
        private const val MILL_SEC = 3600
        private const val MINUTES_60 = 60
        fun fromString(str: String): Playlist =
            Gson().fromJson(str, Playlist::class.java)
    }

    override fun toString(): String =
        Gson().toJson(this)

    fun getCreateDateString(context: Context): String {
        val displayDateFormat = SimpleDateFormat(dd_MMM_yyyy, Locale.getDefault())
        return context.getString(
            R.string.playlist_created_date,
            displayDateFormat.format(createDate)
        )
    }

    fun getDurationString(): String {
        if (duration <= 0) {
            return "0m"
        }

        // Example formats 1h23m
        val hours = duration / MILL_SEC
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

    override var sourceId = id
    override var sourceImage = coverImage
    override var sourceType = MediaType.PLAYLIST.name
    override var sourceStation: Station? = null
    override var sourceAlbum: Album? = null
    override var sourceArtist: Artist? = null
    override var sourcePlaylist: Playlist? = this
    override var sourceTrack: Track? = null
    override var isOffline: Boolean = false

    override fun resetPlayerSource(isOffline: Boolean) {
        sourceId = id
        sourceImage = coverImage
        sourceType = MediaType.PLAYLIST.name
        sourceAlbum = null
        sourceArtist = null
        sourceStation = null
        sourcePlaylist = this
        sourceTrack = null
        this.isOffline = isOffline
    }

    fun toPlaylistEntity(): PlaylistEntity {
        return PlaylistEntity(
            playlistId = this.id,
            name = this.name,
            description = this.description,
            creatorId = this.creatorId,
            creatorName = this.creatorName,
            creatorImage = this.creatorImage,
            trackCount = this.trackCount,
            publicTrackCount = this.publicTrackCount ?: -1,
            duration = this.duration,
            createDate = this.createDate,
            updateDate = this.updateDate,
            trackIds = this.trackIds,
            coverImage = this.coverImage,
            isVideo = this.isVideo,
            isPublic = this.isPublic
        )
    }
}
