package com.truedigital.features.tuned.data.station.model

import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.track.model.Track
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class LikedTrackTest {

    private val mockType = Rating.LIKED

    private val mockArtist = Artist(
        id = 1,
        name = "name",
        image = null
    )

    private val mockTrack = Track(
        id = 1,
        playlistTrackId = 1,
        songId = 1,
        releaseId = 1,
        artists = listOf(),
        name = "name",
        originalCredit = "originalCredit",
        isExplicit = false,
        trackNumber = 1,
        trackNumberInVolume = 1,
        volumeNumber = 1,
        releaseArtists = listOf(),
        sample = "sample",
        isOnCompilation = false,
        releaseName = "releaseName",
        allowDownload = false,
        allowStream = false,
        duration = 3L,
        image = "image",
        hasLyrics = false,
        video = null,
        isVideo = false,
        vote = null,
        isDownloaded = false,
        syncProgress = 1F,
        isCached = false
    )

    @Test
    fun testGetValue() {
        val likedTrack = LikedTrack(
            type = mockType,
            track = mockTrack,
            artists = listOf(mockArtist)
        )

        assertEquals(likedTrack.type, mockType)
        assertEquals(likedTrack.track, mockTrack)
        assertEquals(likedTrack.artists.first(), mockArtist)
    }

    @Test
    fun testSetValue() {
        val mockTypeValue = Rating.DISLIKED
        val mockTrackValue = mockTrack.copy(id = 200)
        val mockArtistValue = mockArtist.copy(id = 100)

        val likedTrack = LikedTrack(
            type = mockType,
            track = mockTrack,
            artists = listOf(mockArtist)
        )

        likedTrack.type = mockTypeValue
        likedTrack.track = mockTrackValue
        likedTrack.artists = listOf(mockArtistValue)

        assertEquals(likedTrack.type, mockTypeValue)
        assertEquals(likedTrack.track, mockTrackValue)
        assertEquals(likedTrack.artists.first(), mockArtistValue)
    }

    @Test
    fun getArtistString_artistsIsNotEmpty_returnArtistString() {
        val mockArtistValue01 = mockArtist.copy(name = "name01")
        val mockArtistValue02 = mockArtist.copy(name = "name02")

        val likedTrack = LikedTrack(
            type = mockType,
            track = mockTrack,
            artists = listOf(mockArtistValue01, mockArtistValue02)
        )

        val result = likedTrack.getArtistString("variousString")
        assertEquals(result, "${mockArtistValue01.name},${mockArtistValue02.name}")
    }

    @Test
    fun getArtistString_artistsIsEmpty_returnVariousString() {
        val mockVariousString = "VariousString"
        val likedTrack = LikedTrack(
            type = mockType,
            track = mockTrack,
            artists = emptyList(),
        )

        val result = likedTrack.getArtistString(mockVariousString)
        assertEquals(result, mockVariousString)
    }

    @Test
    fun getArtistStringVariable_returnArtistString() {
        val mockArtistValue01 = mockArtist.copy(name = "name01")
        val mockArtistValue02 = mockArtist.copy(name = "name02")

        val likedTrack = LikedTrack(
            type = mockType,
            track = mockTrack,
            artists = listOf(mockArtistValue01, mockArtistValue02)
        )

        val result = likedTrack.artistString
        assertEquals(result, "${mockArtistValue01.name},${mockArtistValue02.name}")
    }
}
