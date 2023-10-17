package com.truedigital.features.tuned.data.database.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.truedigital.core.extensions.fromJson
import com.truedigital.features.tuned.data.util.LocalisedString

class StationConverters {

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
}
