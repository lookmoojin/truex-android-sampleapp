package com.truedigital.features.tuned.data.database.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.truedigital.core.extensions.fromJson
import com.truedigital.features.tuned.data.playlist.model.PlaylistTrack
import com.truedigital.features.tuned.data.util.LocalisedString
import java.util.Date

class PlaylistConverters {

    @TypeConverter
    fun fromTimestampToDate(timestamp: Long?): Date? {
        return timestamp?.let { Date(it) }
    }

    @TypeConverter
    fun fromDateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromLocalisedStringListToJSON(value: List<LocalisedString>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun fromJSONToLocalisedStringList(json: String): List<LocalisedString> {
        return try {
            Gson().fromJson<List<LocalisedString>>(json)
        } catch (e: Exception) {
            listOf()
        }
    }

    @TypeConverter
    fun fromPlaylistTrackListToJSON(value: List<PlaylistTrack>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun fromJSONToPlaylistTrackList(json: String): List<PlaylistTrack> {
        return try {
            Gson().fromJson<List<PlaylistTrack>>(json)
        } catch (e: Exception) {
            listOf()
        }
    }
}
