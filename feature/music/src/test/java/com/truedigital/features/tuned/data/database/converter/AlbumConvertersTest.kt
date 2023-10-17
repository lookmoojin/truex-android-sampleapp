package com.truedigital.features.tuned.data.database.converter

import com.google.gson.Gson
import com.truedigital.features.tuned.data.artist.model.ArtistInfo
import com.truedigital.features.utils.MockDataModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class AlbumConvertersTest {

    private lateinit var albumConverters: AlbumConverters

    private val mockArtistInfo = ArtistInfo(id = 1, name = "name")
    private val mockArtistInfoList = listOf(mockArtistInfo)
    private val mockIntList = listOf(1, 2, 3)

    @BeforeEach
    fun setUp() {
        albumConverters = AlbumConverters()
    }

    @Test
    fun fromArtistInfoToJSON_returnArtistInfoJSON() {
        // When
        val result = albumConverters.fromArtistInfoToJSON(mockArtistInfoList)

        // Then
        val expect = Gson().toJson(mockArtistInfoList)
        assertEquals(expect, result)
    }

    @Test
    fun fromJSONToArtistInfoList_returnArtistInfoListModel() {
        // Given
        val artistInfoJson = Gson().toJson(mockArtistInfoList)

        // When
        val result = albumConverters.fromJSONToArtistInfoList(artistInfoJson)

        // Then
        assertEquals(mockArtistInfoList, result)
    }

    @Test
    fun fromJSONToArtistInfoList_JSONIsNotArtistInfoList_returnEmptyList() {
        // When
        val result = albumConverters.fromJSONToArtistInfoList("json")

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun fromReleaseToJSON_returnReleaseJSON() {
        // When
        val result = albumConverters.fromReleaseToJSON(MockDataModel.mockRelease)

        // Then
        val expect = Gson().toJson(MockDataModel.mockRelease)
        assertEquals(expect, result)
    }

    @Test
    fun fromJSONToRelease_returnReleaseModel() {
        // Given
        val releaseJson = Gson().toJson(MockDataModel.mockRelease)

        // When
        val result = albumConverters.fromJSONToRelease(releaseJson)

        // Then
        assertEquals(MockDataModel.mockRelease, result)
    }

    @Test
    fun fromJSONToRelease_JSONIsNotRelease_returnNull() {
        // When
        val result = albumConverters.fromJSONToRelease("json")

        // Then
        assertNull(result)
    }

    @Test
    fun fromIntListToJSON_returnIntListJSON() {
        // When
        val result = albumConverters.fromIntListToJSON(mockIntList)

        // Then
        val expect = Gson().toJson(mockIntList)
        assertEquals(expect, result)
    }

    @Test
    fun fromJSONtoIntList_returnIntListModel() {
        // Given
        val intListJson = Gson().toJson(mockIntList)

        // When
        val result = albumConverters.fromJSONtoIntList(intListJson)

        // Then
        assertEquals(mockIntList, result)
    }

    @Test
    fun fromJSONtoIntList_JSONIsNotIntList_returnEmptyList() {
        // When
        val result = albumConverters.fromJSONtoIntList("json")

        // Then
        assertTrue(result.isEmpty())
    }
}
