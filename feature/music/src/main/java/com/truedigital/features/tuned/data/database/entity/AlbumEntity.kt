package com.truedigital.features.tuned.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.album.model.Release
import com.truedigital.features.tuned.data.artist.model.ArtistInfo
import com.truedigital.features.tuned.data.database.converter.AlbumConverters

@Entity(tableName = "album_table")
@TypeConverters(AlbumConverters::class)
class AlbumEntity(
    @PrimaryKey
    val albumId: Int = -1,
    val name: String = "",
    val artists: List<ArtistInfo> = emptyList(),
    val primaryRelease: Release? = null,
    val releaseIds: List<Int> = emptyList()
) {

    fun toAlbum(): Album {
        return Album(
            id = albumId,
            name = name,
            artists = artists,
            primaryRelease = primaryRelease,
            releaseIds = releaseIds
        )
    }
}
