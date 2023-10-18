package com.truedigital.features.tuned.data.artist.model

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ArtistInfoTest {

    @Test
    fun testArtistInfoData_initialData() {
        val mockArtistInfo = ArtistInfo(
            id = 1,
            name = "name"
        )
        assertEquals(1, mockArtistInfo.id)
        assertEquals("name", mockArtistInfo.name)
    }

    @Test
    fun testArtistInfoData_setData() {
        val mockArtistInfo = ArtistInfo(
            id = 1,
            name = "name"
        )
        mockArtistInfo.id = 2
        mockArtistInfo.name = "name2"

        assertEquals(2, mockArtistInfo.id)
        assertEquals("name2", mockArtistInfo.name)
    }
}
