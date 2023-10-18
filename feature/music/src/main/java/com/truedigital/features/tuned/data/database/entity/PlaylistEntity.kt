package com.truedigital.features.tuned.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.truedigital.features.tuned.data.database.converter.PlaylistConverters
import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.playlist.model.PlaylistTrack
import com.truedigital.features.tuned.data.util.LocalisedString
import java.util.Date

@Entity(tableName = "playlist_table")
@TypeConverters(PlaylistConverters::class)
class PlaylistEntity(
    @PrimaryKey
    val playlistId: Int = -1,
    val name: List<LocalisedString> = emptyList(),
    val description: List<LocalisedString> = emptyList(),
    val creatorId: Int = -1,
    val creatorName: String? = null,
    val creatorImage: String? = null,
    val trackCount: Int = -1,
    val publicTrackCount: Int = -1,
    val duration: Int = -1,
    val createDate: Date = Date(),
    val updateDate: Date = Date(),
    val trackIds: List<PlaylistTrack> = emptyList(),
    val coverImage: List<LocalisedString> = emptyList(),
    val isVideo: Boolean = false,
    val isPublic: Boolean = false
) {

    fun toPlaylist(): Playlist {
        return Playlist(
            id = this.playlistId,
            name = this.name,
            description = this.description,
            creatorId = this.creatorId,
            creatorName = this.creatorName,
            creatorImage = this.creatorImage,
            trackCount = this.trackCount,
            publicTrackCount = this.publicTrackCount,
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
