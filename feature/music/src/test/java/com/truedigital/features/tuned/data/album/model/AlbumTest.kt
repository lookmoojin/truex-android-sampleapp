package com.truedigital.features.tuned.data.album.model

import com.truedigital.features.tuned.data.artist.model.ArtistInfo
import com.truedigital.features.tuned.data.player.model.MediaType
import com.truedigital.features.tuned.data.util.LocalisedString
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class AlbumTest {

    private fun mockRelease(): Release {
        return Release(
            id = 1, albumId = 1, artists = listOf(),
            name = "",
            isExplicit = true,
            numberOfVolumes = 1, trackIds = listOf(),
            duration = 1,
            volumes = listOf(),
            image = "image",
            webPath = "webPath",
            copyRight = "copyRight",
            label = null,
            originalReleaseDate = null,
            digitalReleaseDate = null,
            physicalReleaseDate = null,
            saleAvailabilityDateTime = null,
            streamAvailabilityDateTime = null,
            allowStream = true,
            allowDownload = true
        )
    }

    @Test
    fun testAlbumData_valueData() {
        val mockAlbum = Album(
            id = 1,
            name = "name",
            artists = listOf(
                ArtistInfo(
                    id = 2,
                    name = "artistName"
                )
            ),
            primaryRelease = null,
            releaseIds = listOf(1, 2)
        )

        assertEquals(1, mockAlbum.id)
        assertEquals("name", mockAlbum.name)
        assertEquals(1, mockAlbum.artists.size)
        assertNull(mockAlbum.primaryRelease)
        assertEquals(2, mockAlbum.releaseIds.size)
    }

    @Test
    fun testPlayerSource_primaryReleaseNull_returnData() {
        val mockAlbum = Album(
            id = 1,
            name = "name",
            artists = listOf(
                ArtistInfo(
                    id = 2,
                    name = "artistName"
                )
            ),
            primaryRelease = null,
            releaseIds = listOf(1, 2)
        )

        assertEquals(mockAlbum.id, mockAlbum.sourceId)
        assertEquals(listOf(LocalisedString("en", null)), mockAlbum.sourceImage)
        assertEquals(MediaType.ALBUM.name, mockAlbum.sourceType)
        assertEquals(mockAlbum, mockAlbum.sourceAlbum)
        assertNull(mockAlbum.sourceArtist)
        assertNull(mockAlbum.sourceStation)
        assertNull(mockAlbum.sourcePlaylist)
        assertNull(mockAlbum.sourceTrack)
        assertFalse(mockAlbum.isOffline)
    }

    @Test
    fun testPlayerSource_primaryReleaseNotNull_returnData() {
        val mockAlbum = Album(
            id = 1,
            name = "name",
            artists = listOf(
                ArtistInfo(
                    id = 2,
                    name = "artistName"
                )
            ),
            primaryRelease = mockRelease(),
            releaseIds = listOf(1, 2)
        )

        assertEquals(listOf(LocalisedString("en", mockRelease().image)), mockAlbum.sourceImage)
    }

    @Test
    fun testResetPlayerSource_primaryReleaseNull_returnData() {
        val mockAlbum = Album(
            id = 1,
            name = "name",
            artists = listOf(
                ArtistInfo(
                    id = 2,
                    name = "artistName"
                )
            ),
            primaryRelease = null,
            releaseIds = listOf(1, 2)
        )
        mockAlbum.resetPlayerSource(true)

        assertEquals(mockAlbum.id, mockAlbum.sourceId)
        assertEquals(listOf(LocalisedString("en", null)), mockAlbum.sourceImage)
        assertEquals(MediaType.ALBUM.name, mockAlbum.sourceType)
        assertEquals(mockAlbum, mockAlbum.sourceAlbum)
        assertNull(mockAlbum.sourceArtist)
        assertNull(mockAlbum.sourceStation)
        assertNull(mockAlbum.sourcePlaylist)
        assertNull(mockAlbum.sourceTrack)
        assertTrue(mockAlbum.isOffline)
    }

    @Test
    fun testResetPlayerSource_primaryReleaseNotNull_returnData() {
        val mockAlbum = Album(
            id = 1,
            name = "name",
            artists = listOf(
                ArtistInfo(
                    id = 2,
                    name = "artistName"
                )
            ),
            primaryRelease = mockRelease(),
            releaseIds = listOf(1, 2)
        )
        mockAlbum.resetPlayerSource(true)

        assertEquals(listOf(LocalisedString("en", mockRelease().image)), mockAlbum.sourceImage)
        assertTrue(mockAlbum.isOffline)
    }
}
