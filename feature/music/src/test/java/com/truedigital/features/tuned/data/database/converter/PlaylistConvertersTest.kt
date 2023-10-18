package com.truedigital.features.tuned.data.database.converter

import com.google.gson.Gson
import com.truedigital.features.tuned.data.playlist.model.PlaylistTrack
import com.truedigital.features.tuned.data.util.LocalisedString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Date

internal class PlaylistConvertersTest {

    private lateinit var playlistConverters: PlaylistConverters

    private val mockTimeStamp = 1000L
    private val mockLocalisedStringList = listOf(LocalisedString(language = "th", value = "value"))
    private val mockPlaylistTrackList = listOf(PlaylistTrack(id = 1, trackId = 12))

    @BeforeEach
    fun setUp() {
        playlistConverters = PlaylistConverters()
    }

    @Test
    fun fromTimestampToDate_timestampIsNotNull_returnDate() {
        // When
        val result = playlistConverters.fromTimestampToDate(mockTimeStamp)

        // Then
        assertEquals(mockTimeStamp, result?.time)
    }

    @Test
    fun fromTimestampToDate_timestampIsNull_returnNull() {
        // When
        val result = playlistConverters.fromTimestampToDate(null)

        // Then
        assertNull(result)
    }

    @Test
    fun fromDateToTimestamp_dateIsNotNull_returnTimestamp() {
        // When
        val result = playlistConverters.fromDateToTimestamp(Date(mockTimeStamp))

        // Then
        assertEquals(mockTimeStamp, result)
    }

    @Test
    fun fromDateToTimestamp_dateIsNull_returnNull() {
        // When
        val result = playlistConverters.fromDateToTimestamp(null)

        // Then
        assertNull(result)
    }

    @Test
    fun fromLocalisedStringListToJSON_returnLocalisedStringListJSON() {
        // When
        val result = playlistConverters.fromLocalisedStringListToJSON(mockLocalisedStringList)

        // Then
        val expect = Gson().toJson(mockLocalisedStringList)
        assertEquals(expect, result)
    }

    @Test
    fun fromJSONToLocalisedStringList_returnLocalisedStringListModel() {
        // Given
        val localisedStringListJson = Gson().toJson(mockLocalisedStringList)

        // When
        val result = playlistConverters.fromJSONToLocalisedStringList(localisedStringListJson)

        // Then
        assertEquals(mockLocalisedStringList, result)
    }

    @Test
    fun fromJSONToLocalisedStringList_JSONIsNotLocalisedStringList_returnEmptyList() {
        // When
        val result = playlistConverters.fromJSONToLocalisedStringList("json")

        // Then
        assertTrue(result.isEmpty())
    }

    @Test
    fun fromPlaylistTrackListToJSON_returnPlaylistTrackListJSON() {
        // When
        val result = playlistConverters.fromPlaylistTrackListToJSON(mockPlaylistTrackList)

        // Then
        val expect = Gson().toJson(mockPlaylistTrackList)
        assertEquals(expect, result)
    }

    @Test
    fun fromJSONToPlaylistTrackList_returnPlaylistTrackListModel() {
        // Given
        val playlistTrackListJson = Gson().toJson(mockPlaylistTrackList)

        // When
        val result = playlistConverters.fromJSONToPlaylistTrackList(playlistTrackListJson)

        // Then
        assertEquals(mockPlaylistTrackList, result)
    }

    @Test
    fun fromJSONToPlaylistTrackList_JSONIsNotPlaylistTrackList_returnEmptyList() {
        // When
        val result = playlistConverters.fromJSONToPlaylistTrackList("json")

        // Then
        assertTrue(result.isEmpty())
    }
}
