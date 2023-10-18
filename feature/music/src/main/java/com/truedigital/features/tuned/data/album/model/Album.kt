package com.truedigital.features.tuned.data.album.model

import com.google.gson.annotations.SerializedName
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.artist.model.ArtistInfo
import com.truedigital.features.tuned.data.database.entity.AlbumEntity
import com.truedigital.features.tuned.data.player.PlayerSource
import com.truedigital.features.tuned.data.player.model.MediaType
import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.product.model.Product
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.util.LocalisedString

data class Album(
    @SerializedName("AlbumId") override val id: Int,
    @SerializedName("Name") val name: String,
    @SerializedName("Artists") val artists: List<ArtistInfo>,
    @SerializedName("PrimaryRelease") val primaryRelease: Release?,
    @SerializedName("ReleaseIds") val releaseIds: List<Int>
) : PlayerSource, Product {

    override var sourceId = id
    override var sourceImage = listOf(LocalisedString("en", primaryRelease?.image))
    override var sourceType = MediaType.ALBUM.name
    override var sourceAlbum: Album? = this
    override var sourceArtist: Artist? = null
    override var sourceStation: Station? = null
    override var sourcePlaylist: Playlist? = null
    override var sourceTrack: Track? = null
    override var isOffline = false

    override fun resetPlayerSource(isOffline: Boolean) {
        sourceId = id
        sourceImage = listOf(LocalisedString("en", primaryRelease?.image))
        sourceType = MediaType.ALBUM.name
        sourceAlbum = this
        sourceArtist = null
        sourceStation = null
        sourcePlaylist = null
        sourceTrack = null
        this.isOffline = isOffline
    }

    fun toAlbumEntity(): AlbumEntity {
        return AlbumEntity(
            albumId = this.id,
            name = this.name,
            artists = this.artists,
            primaryRelease = this.primaryRelease,
            releaseIds = this.releaseIds
        )
    }
}
