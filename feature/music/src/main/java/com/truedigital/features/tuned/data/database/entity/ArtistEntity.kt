package com.truedigital.features.tuned.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.truedigital.features.tuned.data.artist.model.Artist

@Entity(tableName = "artist_table")
class ArtistEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val image: String?
) {

    fun toArtist(): Artist {
        return Artist(
            id = this.id,
            name = this.name,
            image = this.image
        )
    }
}
