package com.truedigital.features.tuned.data.tag.repository

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.api.tag.TagMetadataApiInterface
import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.database.repository.MusicRoomRepository
import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.tag.model.Tag
import com.truedigital.features.tuned.data.track.model.request.TrackRequestType
import com.truedigital.features.tuned.data.util.PagedResults
import io.reactivex.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Date
import java.util.concurrent.TimeUnit

class TagRepositoryTest {

    private lateinit var tagPlaylistRepository: TagRepository
    private val tagMetadataApi: TagMetadataApiInterface = mock()
    private val musicRoomRepository: MusicRoomRepository = mock()

    private val mockTag = Tag(
        id = 1234,
        name = "Tag",
        image = "tag_image",
        images = listOf(),
        displayName = listOf()
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
    private val mockArtist = Artist(
        id = 1234,
        name = "artist",
        image = "image"
    )
    private val mockAlbum = Album(
        id = 1234,
        name = "album",
        artists = listOf(),
        primaryRelease = null,
        releaseIds = listOf()
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

    @BeforeEach
    fun setup() {
        tagPlaylistRepository = TagRepositoryImpl(
            tagMetadataApi = tagMetadataApi,
            musicRoomRepository = musicRoomRepository
        )
    }

    @Test
    fun testGetTagByName_returnSuccess() {
        whenever(tagMetadataApi.getTagByName(any())).thenReturn(
            Single.just(mockTag)
        )
        tagPlaylistRepository.getTagByName("TAG")
            .test()
            .assertNoErrors()
            .assertValue { tag ->
                tag == mockTag
            }
        verify(tagMetadataApi, times(1)).getTagByName("TAG")
    }

    @Test
    fun testGetTags_returnSuccess() {
        val mockTags = listOf(mockTag)
        whenever(tagMetadataApi.getMultipleByName(any())).thenReturn(
            Single.just(listOf(mockTag))
        )

        tagPlaylistRepository.getTags("TAGS")
            .test()
            .assertNoErrors()
            .assertValue { tag ->
                tag == mockTags
            }

        verify(tagMetadataApi, times(1)).getMultipleByName("TAGS")
    }

    @Test
    fun testGetPlaylistsByTag_returnSuccess() {
        val mockPlaylists = listOf(mockPlaylist)
        whenever(tagMetadataApi.getPlaylistsByTag(any(), any(), any(), any())).thenReturn(
            Single.just(PagedResults(1, 1, 1, mockPlaylists))
        )
        whenever(musicRoomRepository.insertPlaylists(any())).thenReturn(Single.just(listOf(1L)))

        tagPlaylistRepository.getPlaylistsByTag("TAG", 1, 1, TrackRequestType.AUDIO)
            .test()
            .awaitDone(5, TimeUnit.SECONDS)
            .assertNoErrors()
            .assertValue {
                it == mockPlaylists
            }

        verify(tagMetadataApi, times(1)).getPlaylistsByTag(any(), any(), any(), any())
        verify(musicRoomRepository, times(1)).insertPlaylists(any())
    }

    @Test
    fun testGetArtistsByTag_returnSuccess() {
        val mockArtists = listOf(mockArtist)
        whenever(tagMetadataApi.getArtistByTag(any(), any(), any())).thenReturn(
            Single.just(PagedResults(1, 1, 1, mockArtists))
        )
        whenever(musicRoomRepository.insertArtists(any())).thenReturn(Single.just(listOf(1L)))

        tagPlaylistRepository.getArtistsByTag("TAG", 1, 1)
            .test()
            .awaitDone(5, TimeUnit.SECONDS)
            .assertNoErrors()
            .assertValue {
                it == mockArtists
            }

        verify(tagMetadataApi, times(1)).getArtistByTag("TAG", 1, 1)
        verify(musicRoomRepository, times(1)).insertArtists(any())
    }

    @Test
    fun testGetAlbumsByTag_returnSuccess() {
        val mockAlbums = listOf(mockAlbum)
        whenever(tagMetadataApi.getAlbumsByTag(any(), any(), any())).thenReturn(
            Single.just(PagedResults(1, 1, 1, mockAlbums))
        )
        whenever(musicRoomRepository.insertAlbums(any())).thenReturn(Single.just(listOf(1L)))

        tagPlaylistRepository.getAlbumsByTag("TAG", 1, 1)
            .test()
            .awaitDone(5, TimeUnit.SECONDS)
            .assertNoErrors()
            .assertValue {
                it == mockAlbums
            }

        verify(tagMetadataApi, times(1)).getAlbumsByTag(any(), any(), any())
        verify(musicRoomRepository, times(1)).insertAlbums(any())
    }

    @Test
    fun testGetStationsByTag_returnSuccess() {
        val mockStations = listOf(mockStation)
        whenever(tagMetadataApi.getStationsByTag(any(), any(), any())).thenReturn(
            Single.just(PagedResults(1, 1, 1, mockStations))
        )
        whenever(musicRoomRepository.insertStations(any())).thenReturn(Single.just(listOf(1L)))

        tagPlaylistRepository.getStationsByTag("TAG", 1, 1)
            .test()
            .awaitDone(5, TimeUnit.SECONDS)
            .assertNoErrors()
            .assertValue {
                it == mockStations
            }

        verify(tagMetadataApi, times(1)).getStationsByTag("TAG", 1, 1)
        verify(musicRoomRepository, times(1)).insertStations(mockStations)
    }

    @Test
    fun testGetAlbumsByTagGroup_returnSuccess() {
        val mockAlbums = listOf(mockAlbum)
        whenever(tagMetadataApi.getAlbumsByTagGroup(any(), any(), any(), any())).thenReturn(
            Single.just(PagedResults(1, 1, 1, mockAlbums))
        )
        whenever(musicRoomRepository.insertAlbums(any())).thenReturn(Single.just(listOf(1L)))

        tagPlaylistRepository.getAlbumsByTagGroup("TAG", "ALL", 1, 1)
            .test()
            .awaitDone(5, TimeUnit.SECONDS)
            .assertNoErrors()
            .assertValue {
                it == mockAlbums
            }

        verify(tagMetadataApi, times(1)).getAlbumsByTagGroup(any(), any(), any(), any())
        verify(musicRoomRepository, times(1)).insertAlbums(any())
    }
}
