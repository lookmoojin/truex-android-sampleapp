package com.truedigital.features.tuned.data.playlist.model

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PlaylistTrackTest {

    @Test
    fun testPlaylistTrackData_mapData() {
        val mockPlaylistTrack = PlaylistTrack(
            id = 1,
            trackId = 2
        )
        assertEquals(1, mockPlaylistTrack.id)
        assertEquals(2, mockPlaylistTrack.trackId)
    }

    @Test
    fun testPlaylistTrackData_setData() {
        val mockPlaylistTrack = PlaylistTrack(
            id = 1,
            trackId = 2
        )
        mockPlaylistTrack.id = 3
        mockPlaylistTrack.trackId = 4

        assertEquals(3, mockPlaylistTrack.id)
        assertEquals(4, mockPlaylistTrack.trackId)
    }
}
