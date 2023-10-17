package com.truedigital.features.tuned.data.album.model

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.data.artist.model.ArtistInfo
import io.mockk.every
import io.mockk.mockkClass
import io.mockk.mockkStatic
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.Calendar
import java.util.Date
import kotlin.test.assertEquals

class ReleaseTest {

    private fun mockRelease(
        originalReleaseDate: Date? = null,
        artistList: List<ArtistInfo> = listOf(),
        duration: Int = 1
    ): Release {
        return Release(
            id = 1,
            albumId = 1,
            artists = artistList,
            name = "name",
            isExplicit = true,
            numberOfVolumes = 1,
            trackIds = listOf(),
            duration = duration,
            volumes = listOf(),
            image = "image",
            webPath = "webPath",
            copyRight = "copyRight",
            label = null,
            originalReleaseDate = originalReleaseDate,
            digitalReleaseDate = null,
            physicalReleaseDate = null,
            saleAvailabilityDateTime = null,
            streamAvailabilityDateTime = null,
            allowStream = true,
            allowDownload = true
        )
    }

    @Test
    fun testReleaseData_setData() {
        val mockRelease = mockRelease()

        assertEquals(1, mockRelease.id)
        assertEquals(1, mockRelease.albumId)
        assertEquals(0, mockRelease.artists.size)
        assertEquals("name", mockRelease.name)
        assertTrue(mockRelease.isExplicit)
        assertEquals(1, mockRelease.numberOfVolumes)
        assertEquals(0, mockRelease.trackIds.size)
        assertEquals(1, mockRelease.duration)
        assertEquals(0, mockRelease.volumes.size)
        assertEquals("image", mockRelease.image)
        assertEquals("webPath", mockRelease.webPath)
        assertEquals("copyRight", mockRelease.copyRight)
        assertNull(mockRelease.label)
        assertNull(mockRelease.originalReleaseDate)
        assertNull(mockRelease.digitalReleaseDate)
        assertNull(mockRelease.physicalReleaseDate)
        assertNull(mockRelease.saleAvailabilityDateTime)
        assertNull(mockRelease.streamAvailabilityDateTime)
        assertTrue(mockRelease.allowStream)
        assertTrue(mockRelease.allowDownload)
    }

    @Test
    fun testReleaseYear_null_returnNull() {
        val mockRelease = mockRelease()
        assertNull(mockRelease.releaseYear())
    }

    @Test
    fun testReleaseYear_notNull_returnData() {
        val mockRelease = mockRelease(
            originalReleaseDate = Date()
        )

        val calendar = mock<Calendar>()
        mockkStatic(Calendar::class)
        every { Calendar.getInstance() } returns calendar
        whenever(calendar.time).thenReturn(Date())
        whenever(calendar.get(Calendar.YEAR)).thenReturn(5)

        assertEquals(5, mockRelease.releaseYear())
    }

    @Test
    fun testGetArtistString_artistListEmpty_returnVariousString() {
        val mockVariousString = "variousString"
        val mockRelease = mockRelease()

        assertEquals(mockVariousString, mockRelease.getArtistString(mockVariousString))
    }

    @Test
    fun testGetArtistString_artistListNotEmpty_returnArtistString() {
        val mockVariousString = "variousString"
        val mockRelease = mockRelease(
            artistList = listOf(
                ArtistInfo(
                    id = 1,
                    name = "name1"
                ),
                ArtistInfo(
                    id = 2,
                    name = "name2"
                )
            )
        )

        assertEquals("name1,name2", mockRelease.getArtistString(mockVariousString))
    }

    @Test
    fun testGetDurationString_zero_returnOm() {
        val mockRelease = mockRelease(
            duration = 0
        )
        assertEquals("0m", mockRelease.getDurationString())
    }

    @Test
    fun testGetDurationString_hourAndMinute_returnDurationString() {
        val mockRelease = mockRelease(
            duration = 4917
        )

        val mockStringBuilder = mockkClass(StringBuilder::class)
        every { mockStringBuilder.append(1) } returns StringBuilder("1")
        every { mockStringBuilder.append(41) } returns StringBuilder("41")
        every { mockStringBuilder.append("h") } returns StringBuilder("h")
        every { mockStringBuilder.append("m") } returns StringBuilder("m")

        assertEquals("1h21m", mockRelease.getDurationString())
    }

    @Test
    fun testGetDurationString_tenMinute_returnDurationString() {
        val mockRelease = mockRelease(
            duration = 600
        )

        val mockStringBuilder = mockkClass(StringBuilder::class)
        every { mockStringBuilder.append(10) } returns StringBuilder("10")
        every { mockStringBuilder.append(0) } returns StringBuilder("0")
        every { mockStringBuilder.append("h") } returns StringBuilder("h")
        every { mockStringBuilder.append("m") } returns StringBuilder("m")

        assertEquals("10m", mockRelease.getDurationString())
    }

    @Test
    fun testGetDurationString_oneHour_returnDurationString() {
        val mockRelease = mockRelease(
            duration = 3600
        )

        val mockStringBuilder = mockkClass(StringBuilder::class)
        every { mockStringBuilder.append(1) } returns StringBuilder("1")
        every { mockStringBuilder.append(0) } returns StringBuilder("0")
        every { mockStringBuilder.append("h") } returns StringBuilder("h")
        every { mockStringBuilder.append("m") } returns StringBuilder("m")

        assertEquals("1h", mockRelease.getDurationString())
    }
}
