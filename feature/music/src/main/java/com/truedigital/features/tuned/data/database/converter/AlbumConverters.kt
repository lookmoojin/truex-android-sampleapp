package com.truedigital.features.tuned.data.database.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.truedigital.core.extensions.fromJson
import com.truedigital.features.tuned.data.album.model.Release
import com.truedigital.features.tuned.data.artist.model.ArtistInfo

class AlbumConverters {

    @TypeConverter
    fun fromArtistInfoToJSON(value: List<ArtistInfo>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun fromJSONToArtistInfoList(json: String): List<ArtistInfo> {
        return try {
            Gson().fromJson<List<ArtistInfo>>(json)
        } catch (e: Exception) {
            listOf()
        }
    }

    @TypeConverter
    fun fromReleaseToJSON(value: Release?): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun fromJSONToRelease(json: String): Release? {
        return try {
            Gson().fromJson<Release>(json)
        } catch (e: Exception) {
            null
        }
    }

    @TypeConverter
    fun fromIntListToJSON(value: List<Int>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun fromJSONtoIntList(json: String): List<Int> {
        return try {
            Gson().fromJson<List<Int>>(json)
        } catch (e: Exception) {
            listOf()
        }
    }
}
