package com.truedigital.features.tuned.data.database.converter

import com.google.gson.Gson
import com.truedigital.features.tuned.data.util.LocalisedString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class StationConvertersTest {

    private lateinit var stationConverters: StationConverters
    private val mockLocalisedStringList = listOf(LocalisedString(language = "th", value = "value"))

    @BeforeEach
    fun setUp() {
        stationConverters = StationConverters()
    }

    @Test
    fun fromLocalisedStringListToJSON_returnLocalisedStringListJSON() {
        // When
        val result = stationConverters.fromLocalisedStringListToJSON(mockLocalisedStringList)

        // Then
        val expect = Gson().toJson(mockLocalisedStringList)
        assertEquals(expect, result)
    }

    @Test
    fun fromJSONToLocalisedStringList_returnLocalisedStringListModel() {
        // Given
        val localisedStringListJson = Gson().toJson(mockLocalisedStringList)

        // When
        val result = stationConverters.fromJSONToLocalisedStringList(localisedStringListJson)

        // Then
        assertEquals(mockLocalisedStringList, result)
    }

    @Test
    fun fromJSONToLocalisedStringList_JSONIsNotLocalisedStringList_returnEmptyList() {
        // When
        val result = stationConverters.fromJSONToLocalisedStringList("json")

        // Then
        assertTrue(result.isEmpty())
    }
}
