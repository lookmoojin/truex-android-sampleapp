package com.truedigital.features.tuned.data.artist.model

import com.truedigital.features.tuned.data.player.model.MediaType
import com.truedigital.features.tuned.data.util.LocalisedString
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ArtistTest {

    @Test
    fun testArtistData_setDefaultData() {
        val mockArtist = Artist(
            id = 1,
            name = "name",
            image = null
        )
        assertEquals(1, mockArtist.id)
        assertEquals("name", mockArtist.name)
        assertEquals(null, mockArtist.image)
    }

    @Test
    fun testArtistData_initialData() {
        val mockArtist = Artist(
            id = 1,
            name = "name",
            image = "image"
        )
        assertEquals(1, mockArtist.id)
        assertEquals("name", mockArtist.name)
        assertEquals("image", mockArtist.image)
    }

    @Test
    fun testArtistData_setValueData() {
        val mockArtist = Artist(
            id = 1,
            name = "name",
            image = null
        )
        mockArtist.id = 2
        mockArtist.name = "name2"
        mockArtist.image = "image2"

        assertEquals(2, mockArtist.id)
        assertEquals("name2", mockArtist.name)
        assertEquals("image2", mockArtist.image)
    }

    @Test
    fun testPlayerSource_setData() {
        val mockArtist = Artist(
            id = 1,
            name = "name",
            image = "image"
        )
        assertEquals(mockArtist.id, mockArtist.sourceId)
        assertEquals(listOf(LocalisedString("en", mockArtist.image)), mockArtist.sourceImage)
        assertEquals(MediaType.ARTIST_SHUFFLE.name, mockArtist.sourceType)
        assertEquals(null, mockArtist.sourceAlbum)
        assertEquals(mockArtist, mockArtist.sourceArtist)
        assertEquals(null, mockArtist.sourceStation)
        assertEquals(null, mockArtist.sourcePlaylist)
        assertEquals(null, mockArtist.sourceTrack)
        assertEquals(false, mockArtist.isOffline)
    }

    @Test
    fun testResetPlayerSource_isOfflineTrue() {
        val mockArtist = Artist(
            id = 1,
            name = "name",
            image = "image"
        )
        mockArtist.resetPlayerSource(true)

        assertEquals(mockArtist.id, mockArtist.sourceId)
        assertEquals(listOf(LocalisedString("en", mockArtist.image)), mockArtist.sourceImage)
        assertEquals(MediaType.ARTIST_SHUFFLE.name, mockArtist.sourceType)
        assertEquals(null, mockArtist.sourceAlbum)
        assertEquals(mockArtist, mockArtist.sourceArtist)
        assertEquals(null, mockArtist.sourceStation)
        assertEquals(null, mockArtist.sourcePlaylist)
        assertEquals(null, mockArtist.sourceTrack)
        assertEquals(true, mockArtist.isOffline)
    }

    @Test
    fun testEquals_isArtistAndMatchId_returnTrue() {
        val mockArtist = Artist(
            id = 1,
            name = "name",
            image = "image"
        )

        assertEquals(true, mockArtist == mockArtist)
    }

    @Test
    fun testEquals_isArtistAndNotMatchId_returnFalse() {
        val mockArtist = Artist(
            id = 1,
            name = "name",
            image = "image"
        )

        val mockArtist2 = Artist(
            id = 2,
            name = "name",
            image = "image"
        )

        assertEquals(false, mockArtist == mockArtist2)
    }

    @Test
    fun testEquals_null_returnFalse() {
        val mockArtist = Artist(
            id = 1,
            name = "name",
            image = "image"
        )

        assertEquals(false, mockArtist.equals(null))
    }
}
