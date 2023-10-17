package com.truedigital.features.tuned.data.playlist.model

import com.truedigital.features.tuned.data.player.model.MediaType
import com.truedigital.features.tuned.data.tag.model.TypedTag
import com.truedigital.features.tuned.data.util.LocalisedString
import io.mockk.every
import io.mockk.mockkClass
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.Date
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class PlaylistTest {

    private fun mockPlaylist(
        creatorName: String? = null,
        creatorImage: String? = null,
        publicTrackCount: Int? = null,
        typedTagsList: List<TypedTag>? = null
    ) = Playlist(
        id = 1,
        name = listOf(),
        description = listOf(),
        creatorId = 1,
        creatorName = creatorName,
        creatorImage = creatorImage,
        trackCount = 10,
        publicTrackCount = publicTrackCount,
        duration = 3000,
        createDate = Date(),
        updateDate = Date(),
        trackIds = listOf(),
        coverImage = listOf(),
        isVideo = false,
        isPublic = false,
        typedTags = typedTagsList,
        isOwner = false
    )

    @Test
    fun testPlaylistData_defaultData() {
        val mockPlaylist = mockPlaylist()
        assertEquals(1, mockPlaylist.id)
        assertEquals(0, mockPlaylist.name.size)
        assertEquals(0, mockPlaylist.description.size)
        assertEquals(1, mockPlaylist.creatorId)
        assertEquals(null, mockPlaylist.creatorName)
        assertEquals(null, mockPlaylist.creatorImage)
        assertEquals(10, mockPlaylist.trackCount)
        assertEquals(null, mockPlaylist.publicTrackCount)
        assertEquals(3000, mockPlaylist.duration)
        assertNotNull(mockPlaylist.createDate)
        assertNotNull(mockPlaylist.updateDate)
        assertEquals(0, mockPlaylist.trackIds.size)
        assertEquals(0, mockPlaylist.coverImage.size)
        assertEquals(false, mockPlaylist.isVideo)
        assertEquals(false, mockPlaylist.isPublic)
        assertEquals(null, mockPlaylist.typedTags)
        assertEquals(false, mockPlaylist.isOwner)
    }

    @Test
    fun testPlaylistData_valueData() {
        val mockPlaylist = mockPlaylist(
            creatorName = "creatorName",
            creatorImage = "creatorImage",
            publicTrackCount = 10,
            typedTagsList = listOf()
        )
        assertEquals(1, mockPlaylist.id)
        assertEquals(0, mockPlaylist.name.size)
        assertEquals(0, mockPlaylist.description.size)
        assertEquals(1, mockPlaylist.creatorId)
        assertEquals("creatorName", mockPlaylist.creatorName)
        assertEquals("creatorImage", mockPlaylist.creatorImage)
        assertEquals(10, mockPlaylist.trackCount)
        assertEquals(10, mockPlaylist.publicTrackCount)
        assertEquals(3000, mockPlaylist.duration)
        assertNotNull(mockPlaylist.createDate)
        assertNotNull(mockPlaylist.updateDate)
        assertEquals(0, mockPlaylist.trackIds.size)
        assertEquals(0, mockPlaylist.coverImage.size)
        assertEquals(false, mockPlaylist.isVideo)
        assertEquals(false, mockPlaylist.isPublic)
        assertEquals(0, mockPlaylist.typedTags?.size)
        assertEquals(false, mockPlaylist.isOwner)
    }

    @Test
    fun testPlaylistData_setData() {
        val mockPlaylist = mockPlaylist()
        mockPlaylist.id = 2
        mockPlaylist.name = listOf(
            LocalisedString(language = "TH", value = "nameTh")
        )
        mockPlaylist.description = listOf(
            LocalisedString(language = "TH", value = "nameTh")
        )
        mockPlaylist.creatorId = 2
        mockPlaylist.creatorName = "creatorName"
        mockPlaylist.creatorImage = "creatorImage"
        mockPlaylist.trackCount = 20
        mockPlaylist.publicTrackCount = 20
        mockPlaylist.duration = 1000
        mockPlaylist.createDate = Date()
        mockPlaylist.updateDate = Date()
        mockPlaylist.trackIds = listOf(
            PlaylistTrack(id = 2, trackId = 2)
        )
        mockPlaylist.coverImage = listOf(
            LocalisedString(language = "TH", value = "nameTh")
        )
        mockPlaylist.isVideo = true
        mockPlaylist.isPublic = true
        mockPlaylist.typedTags = listOf(
            TypedTag(
                name = "name",
                displayName = listOf()
            )
        )
        mockPlaylist.isOwner = true

        assertEquals(2, mockPlaylist.id)
        assertEquals(1, mockPlaylist.name.size)
        assertEquals(1, mockPlaylist.description.size)
        assertEquals(2, mockPlaylist.creatorId)
        assertEquals("creatorName", mockPlaylist.creatorName)
        assertEquals("creatorImage", mockPlaylist.creatorImage)
        assertEquals(20, mockPlaylist.trackCount)
        assertEquals(20, mockPlaylist.publicTrackCount)
        assertEquals(1000, mockPlaylist.duration)
        assertNotNull(mockPlaylist.createDate)
        assertNotNull(mockPlaylist.updateDate)
        assertEquals(1, mockPlaylist.trackIds.size)
        assertEquals(1, mockPlaylist.coverImage.size)
        assertEquals(true, mockPlaylist.isVideo)
        assertEquals(true, mockPlaylist.isPublic)
        assertEquals(1, mockPlaylist.typedTags?.size)
        assertEquals(true, mockPlaylist.isOwner)
    }

    @Test
    fun testGetDurationString_zero_returnOm() {
        val mockPlaylist = mockPlaylist()
        mockPlaylist.duration = 0
        assertEquals("0m", mockPlaylist.getDurationString())
    }

    @Test
    fun testGetDurationString_hourAndMinute_returnDurationString() {
        val mockPlaylist = mockPlaylist()
        mockPlaylist.duration = 4917

        val mockStringBuilder = mockkClass(StringBuilder::class)
        every { mockStringBuilder.append(1) } returns StringBuilder("1")
        every { mockStringBuilder.append(41) } returns StringBuilder("41")
        every { mockStringBuilder.append("h") } returns StringBuilder("h")
        every { mockStringBuilder.append("m") } returns StringBuilder("m")

        assertEquals("1h21m", mockPlaylist.getDurationString())
    }

    @Test
    fun testGetDurationString_tenMinute_returnDurationString() {
        val mockPlaylist = mockPlaylist()
        mockPlaylist.duration = 600

        val mockStringBuilder = mockkClass(StringBuilder::class)
        every { mockStringBuilder.append(10) } returns StringBuilder("10")
        every { mockStringBuilder.append(0) } returns StringBuilder("0")
        every { mockStringBuilder.append("h") } returns StringBuilder("h")
        every { mockStringBuilder.append("m") } returns StringBuilder("m")

        assertEquals("10m", mockPlaylist.getDurationString())
    }

    @Test
    fun testGetDurationString_oneHour_returnDurationString() {
        val mockPlaylist = mockPlaylist()
        mockPlaylist.duration = 3600

        val mockStringBuilder = mockkClass(StringBuilder::class)
        every { mockStringBuilder.append(1) } returns StringBuilder("1")
        every { mockStringBuilder.append(0) } returns StringBuilder("0")
        every { mockStringBuilder.append("h") } returns StringBuilder("h")
        every { mockStringBuilder.append("m") } returns StringBuilder("m")

        assertEquals("1h", mockPlaylist.getDurationString())
    }

    @Test
    fun testResetPlayerSource_mapData() {
        val mockPlaylist = mockPlaylist()
        mockPlaylist.resetPlayerSource(true)

        assertEquals(mockPlaylist.id, mockPlaylist.sourceId)
        assertEquals(mockPlaylist.coverImage, mockPlaylist.sourceImage)
        assertEquals(MediaType.PLAYLIST.name, mockPlaylist.sourceType)
        assertNull(mockPlaylist.sourceAlbum)
        assertNull(mockPlaylist.sourceArtist)
        assertNull(mockPlaylist.sourceStation)
        assertEquals(mockPlaylist, mockPlaylist.sourcePlaylist)
        assertNull(mockPlaylist.sourceTrack)
        assertTrue(mockPlaylist.isOffline)
    }
}
