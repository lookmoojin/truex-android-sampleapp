package com.truedigital.features.tuned.data.station.model.request

import com.google.gson.annotations.SerializedName
import com.truedigital.features.tuned.data.database.entity.PlaybackStateEntity

data class PlaybackState(
    @SerializedName("TrackId") val trackId: Int = -1,
    @SerializedName("LogPlayType") val state: String = "",
    @SerializedName("FileSource") val fileSource: String = "",
    @SerializedName("Seconds") val elapsedSeconds: Long = -1,
    @SerializedName("Source") val source: String = "",
    @SerializedName("SourceId") val sourceId: Int = -1,
    @SerializedName("Guid") val guid: String? = null
) {
    fun toPlaybackStateEntity(timestamp: Long): PlaybackStateEntity {
        return PlaybackStateEntity(
            trackId = trackId,
            state = state,
            fileSource = fileSource,
            elapsedSeconds = elapsedSeconds,
            source = source,
            sourceId = sourceId,
            timestamp = timestamp,
            guid = guid.orEmpty()
        )
    }
}
