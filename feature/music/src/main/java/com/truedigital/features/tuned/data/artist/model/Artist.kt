package com.truedigital.features.tuned.data.artist.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.database.entity.ArtistEntity
import com.truedigital.features.tuned.data.player.PlayerSource
import com.truedigital.features.tuned.data.player.model.MediaType
import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.product.model.Product
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.util.LocalisedString
import kotlinx.parcelize.Parcelize
import java.util.Objects

@Parcelize
data class Artist(
    @SerializedName("ArtistId") override var id: Int,
    @SerializedName("Name") var name: String,
    @SerializedName("Image") var image: String?
) : Parcelable, PlayerSource, Product {

    override var sourceId = id
    override var sourceImage = listOf(LocalisedString("en", image))
    override var sourceType = MediaType.ARTIST_SHUFFLE.name
    override var sourceAlbum: Album? = null
    override var sourceArtist: Artist? = this
    override var sourceStation: Station? = null
    override var sourcePlaylist: Playlist? = null
    override var sourceTrack: Track? = null
    override var isOffline = false

    override fun resetPlayerSource(isOffline: Boolean) {
        sourceId = id
        sourceImage = listOf(LocalisedString("en", image))
        sourceType = MediaType.ARTIST_SHUFFLE.name
        sourceAlbum = null
        sourceArtist = this
        sourceStation = null
        sourcePlaylist = null
        sourceTrack = null
        this.isOffline = isOffline
    }

    override fun equals(other: Any?): Boolean = other is Artist && other.id == id

    override fun hashCode(): Int {
        return Objects.hash(id)
    }

    fun toArtistEntity(): ArtistEntity {
        return ArtistEntity(
            id = this.id,
            name = this.name,
            image = this.image
        )
    }
}
