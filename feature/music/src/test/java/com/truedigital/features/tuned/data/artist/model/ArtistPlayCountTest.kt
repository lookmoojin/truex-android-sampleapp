package com.truedigital.features.tuned.data.artist.model

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ArtistPlayCountTest {

    @Test
    fun testArtistPlayCountData_initialData() {
        val mockArtistPlayCount = ArtistPlayCount(
            globalTotal = 1,
            globalRecent = 2,
            distinctGlobalTotal = 3,
            distinctGlobalRecent = 4
        )
        assertEquals(1, mockArtistPlayCount.globalTotal)
        assertEquals(2, mockArtistPlayCount.globalRecent)
        assertEquals(3, mockArtistPlayCount.distinctGlobalTotal)
        assertEquals(4, mockArtistPlayCount.distinctGlobalRecent)
    }

    @Test
    fun testArtistPlayCountData_setData() {
        val mockArtistPlayCount = ArtistPlayCount(
            globalTotal = 1,
            globalRecent = 2,
            distinctGlobalTotal = 3,
            distinctGlobalRecent = 4
        )
        mockArtistPlayCount.globalTotal = 5
        mockArtistPlayCount.globalRecent = 6
        mockArtistPlayCount.distinctGlobalTotal = 7
        mockArtistPlayCount.distinctGlobalRecent = 8

        assertEquals(5, mockArtistPlayCount.globalTotal)
        assertEquals(6, mockArtistPlayCount.globalRecent)
        assertEquals(7, mockArtistPlayCount.distinctGlobalTotal)
        assertEquals(8, mockArtistPlayCount.distinctGlobalRecent)
    }
}
