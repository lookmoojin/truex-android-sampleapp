package com.truedigital.features.tuned.data.station.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.database.entity.StationEntity
import com.truedigital.features.tuned.data.player.PlayerSource
import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.product.model.Product
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.util.LocalisedString
import kotlinx.parcelize.Parcelize
import java.util.Objects

@Parcelize
data class Station(
    @SerializedName("StationId") override var id: Int,
    @SerializedName("Type") var type: StationType,
    @SerializedName("Name") var name: List<LocalisedString>,
    @SerializedName("Description") var description: List<LocalisedString>,
    @SerializedName("CoverImage") var coverImage: List<LocalisedString>,
    @SerializedName("BannerImage") var bannerImage: List<LocalisedString>,
    @SerializedName("BannerURL") var bannerURL: String?,
    @SerializedName("IsActive") var isActive: Boolean
) : Parcelable, PlayerSource, Product {

    // should be safe to remove parcelable implementation as we can use realm to store and retrieve data
    override var sourceId = id
    override var sourceImage = coverImage
    override var sourceType = type.name
    override var sourceAlbum: Album? = null
    override var sourceArtist: Artist? = null
    override var sourceStation: Station? = this
    override var sourcePlaylist: Playlist? = null
    override var sourceTrack: Track? = null
    override var isOffline = false

    override fun equals(other: Any?): Boolean = other is Station && other.id == id

    override fun hashCode(): Int {
        return Objects.hash(id)
    }

    enum class StationType(val value: String) {
        @SerializedName(value = "Preset", alternate = ["preset"])
        PRESET("Preset"),

        // artist + similar artist
        @SerializedName(value = "Artist", alternate = ["artist"])
        ARTIST("Artist"),

        // artist shuffle
        @SerializedName(value = "SingleArtist", alternate = ["singleartist"])
        SINGLE_ARTIST("SingleArtist"),

        @SerializedName(value = "Tag", alternate = ["tag"])
        TAG("Tag"),

        @SerializedName(value = "User", alternate = ["user"])
        USER("User");

        companion object {
            fun fromString(input: String) = values().first { it.value == input }
        }
    }

    //region Player Source
    override fun resetPlayerSource(isOffline: Boolean) {
        sourceId = id
        sourceImage = coverImage
        sourceType = type.name
        sourceAlbum = null
        sourceArtist = null
        sourceStation = this
        sourcePlaylist = null
        sourceTrack = null
        this.isOffline = isOffline
    }

    fun toStationEntity(): StationEntity {
        return StationEntity(
            stationId = this.id,
            stationType = this.type.value,
            name = this.name,
            description = this.description,
            coverImage = this.coverImage,
            bannerImage = this.bannerImage,
            bannerUrl = this.bannerURL,
            isActive = this.isActive
        )
    }
}
