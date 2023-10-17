package com.truedigital.features.tuned.data.station.model

import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.player.PlayerSource
import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.util.LocalisedString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.util.Date

internal class StationTest {

    private val mockAlbum = Album(
        id = 1,
        name = "name",
        artists = emptyList(),
        primaryRelease = null,
        releaseIds = listOf(1, 2)
    )

    private val mockArtist = Artist(
        id = 1,
        name = "name",
        image = "image"
    )

    private val mockStation = Station(
        id = 1234,
        type = Station.StationType.TAG,
        name = listOf(),
        description = listOf(),
        coverImage = listOf(),
        bannerImage = listOf(),
        bannerURL = "banner_url",
        isActive = true
    )

    private val mockPlaylist = Playlist(
        id = 1234,
        name = listOf(),
        description = listOf(),
        creatorId = 123,
        creatorName = "creatorName",
        creatorImage = "creatorImage",
        trackCount = 10,
        publicTrackCount = 10,
        duration = 3000,
        createDate = Date(),
        updateDate = Date(),
        trackIds = listOf(),
        coverImage = listOf(),
        isVideo = true,
        isPublic = true,
        typedTags = listOf(),
        isOwner = false
    )

    private val mockTrack = Track(
        id = 1,
        playlistTrackId = 2,
        songId = 3,
        releaseId = 4,
        artists = emptyList(),
        name = "name",
        originalCredit = "originalCredit",
        isExplicit = true,
        trackNumber = 10,
        trackNumberInVolume = 11,
        volumeNumber = 12,
        releaseArtists = emptyList(),
        sample = "sample",
        isOnCompilation = true,
        releaseName = "releaseName",
        allowDownload = true,
        allowStream = true,
        duration = 100,
        image = "image",
        hasLyrics = true,
        video = null,
        isVideo = false,
        vote = Rating.LIKED,
        isDownloaded = true,
        syncProgress = 10f,
        isCached = true
    )

    private val localizeString = LocalisedString(
        language = "TH",
        value = "THValue"
    )

    private val mockId = 1
    private val mockType = Station.StationType.PRESET
    private val mockBannerUrl = "bannerURL"
    private val mockIsActive = true
    private val nameLocalizeString = localizeString.copy("name")
    private val descriptionLocalizeString = localizeString.copy("description")
    private val coverImageLocalizeString = localizeString.copy("coverImage")
    private val bannerImageLocalizeString = localizeString.copy("bannerImage")

    @Test
    fun testGetValue() {
        val station = Station(
            id = mockId,
            type = mockType,
            name = listOf(nameLocalizeString),
            description = listOf(descriptionLocalizeString),
            coverImage = listOf(coverImageLocalizeString),
            bannerImage = listOf(bannerImageLocalizeString),
            bannerURL = mockBannerUrl,
            isActive = mockIsActive
        )

        assertEquals(station.id, mockId)
        assertEquals(station.type, mockType)
        assertEquals(station.name.first(), nameLocalizeString)
        assertEquals(station.description.first(), descriptionLocalizeString)
        assertEquals(station.coverImage.first(), coverImageLocalizeString)
        assertEquals(station.bannerImage.first(), bannerImageLocalizeString)
        assertEquals(station.bannerURL, mockBannerUrl)
        assertEquals(station.isActive, mockIsActive)
    }

    @Test
    fun testSetValue() {
        val station = Station(
            id = 3,
            type = Station.StationType.SINGLE_ARTIST,
            name = emptyList(),
            description = emptyList(),
            coverImage = emptyList(),
            bannerImage = emptyList(),
            bannerURL = "",
            isActive = false
        )
        station.id = mockId
        station.type = mockType
        station.name = listOf(nameLocalizeString)
        station.description = listOf(descriptionLocalizeString)
        station.coverImage = listOf(coverImageLocalizeString)
        station.bannerImage = listOf(bannerImageLocalizeString)
        station.bannerURL = mockBannerUrl
        station.isActive = mockIsActive

        assertEquals(station.id, mockId)
        assertEquals(station.type, mockType)
        assertEquals(station.name.first(), nameLocalizeString)
        assertEquals(station.description.first(), descriptionLocalizeString)
        assertEquals(station.coverImage.first(), coverImageLocalizeString)
        assertEquals(station.bannerImage.first(), bannerImageLocalizeString)
        assertEquals(station.bannerURL, mockBannerUrl)
        assertEquals(station.isActive, mockIsActive)
    }

    @Test
    fun testGetPlayerSource() {
        val stationPlayerSource: PlayerSource = Station(
            id = mockId,
            type = mockType,
            name = listOf(nameLocalizeString),
            description = listOf(descriptionLocalizeString),
            coverImage = listOf(coverImageLocalizeString),
            bannerImage = listOf(bannerImageLocalizeString),
            bannerURL = mockBannerUrl,
            isActive = mockIsActive
        )

        assertEquals(stationPlayerSource.sourceId, mockId)
        assertEquals(stationPlayerSource.sourceImage.first(), coverImageLocalizeString)
        assertEquals(stationPlayerSource.sourceType, mockType.name)
        assertNull(stationPlayerSource.sourceAlbum)
        assertNull(stationPlayerSource.sourceArtist)
        assertEquals(stationPlayerSource.sourceStation, stationPlayerSource)
        assertNull(stationPlayerSource.sourcePlaylist)
        assertNull(stationPlayerSource.sourceTrack)
        assertFalse(stationPlayerSource.isOffline)
    }

    @Test
    fun testSetPlayerSource() {
        val stationPlayerSource: PlayerSource = Station(
            id = 3,
            type = Station.StationType.SINGLE_ARTIST,
            name = emptyList(),
            description = emptyList(),
            coverImage = emptyList(),
            bannerImage = emptyList(),
            bannerURL = "",
            isActive = false
        )

        stationPlayerSource.sourceId = mockId
        stationPlayerSource.sourceImage = listOf(coverImageLocalizeString)
        stationPlayerSource.sourceType = mockType.name
        stationPlayerSource.sourceAlbum = mockAlbum
        stationPlayerSource.sourceArtist = mockArtist
        stationPlayerSource.sourceStation = mockStation
        stationPlayerSource.sourcePlaylist = mockPlaylist
        stationPlayerSource.sourceTrack = mockTrack
        stationPlayerSource.isOffline = true

        assertEquals(stationPlayerSource.sourceId, mockId)
        assertEquals(stationPlayerSource.sourceImage.first(), coverImageLocalizeString)
        assertEquals(stationPlayerSource.sourceType, mockType.name)
        assertEquals(stationPlayerSource.sourceAlbum, mockAlbum)
        assertEquals(stationPlayerSource.sourceArtist, mockArtist)
        assertEquals(stationPlayerSource.sourceStation, mockStation)
        assertEquals(stationPlayerSource.sourcePlaylist, mockPlaylist)
        assertEquals(stationPlayerSource.sourceTrack, mockTrack)
        assertTrue(stationPlayerSource.isOffline)
    }

    @Test
    fun resetPlayerSource_playerSourceIsReset() {
        val id = 3
        val type = Station.StationType.SINGLE_ARTIST
        val stationPlayerSource: PlayerSource = Station(
            id = id,
            type = type,
            name = emptyList(),
            description = emptyList(),
            coverImage = emptyList(),
            bannerImage = emptyList(),
            bannerURL = "",
            isActive = false
        )

        stationPlayerSource.sourceId = mockId
        stationPlayerSource.sourceImage = listOf(coverImageLocalizeString)
        stationPlayerSource.sourceType = mockType.name
        stationPlayerSource.sourceAlbum = mockAlbum
        stationPlayerSource.sourceArtist = mockArtist
        stationPlayerSource.sourceStation = mockStation
        stationPlayerSource.sourcePlaylist = mockPlaylist
        stationPlayerSource.sourceTrack = mockTrack
        stationPlayerSource.isOffline = true

        stationPlayerSource.resetPlayerSource(false)

        assertEquals(stationPlayerSource.sourceId, id)
        assertEquals(stationPlayerSource.sourceType, type.name)
        assertNull(stationPlayerSource.sourceAlbum)
        assertNull(stationPlayerSource.sourceArtist)
        assertEquals(stationPlayerSource.sourceStation, stationPlayerSource)
        assertNull(stationPlayerSource.sourcePlaylist)
        assertNull(stationPlayerSource.sourceTrack)
        assertFalse(stationPlayerSource.isOffline)
    }
}
