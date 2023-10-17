package com.truedigital.features.tuned.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.truedigital.features.tuned.data.station.model.request.OfflinePlaybackState
import com.truedigital.features.tuned.data.station.model.request.PlaybackState
import java.util.Date

@Entity(tableName = "playback_state_table")
class PlaybackStateEntity(
    @PrimaryKey
    var trackId: Int = -1,
    var state: String = "",
    var fileSource: String = "",
    var elapsedSeconds: Long = -1L,
    var source: String = "",
    var sourceId: Int = -1,
    var timestamp: Long = -1,
    var guid: String = "",
) {

    fun toOfflinePlaybackState(): OfflinePlaybackState {
        return OfflinePlaybackState(
            date = Date(timestamp),
            PlaybackState(
                trackId = this.trackId,
                state = this.state,
                fileSource = this.fileSource,
                elapsedSeconds = this.elapsedSeconds,
                source = source,
                sourceId = this.sourceId,
                guid = this.guid.ifEmpty { null }
            )
        )
    }
}
