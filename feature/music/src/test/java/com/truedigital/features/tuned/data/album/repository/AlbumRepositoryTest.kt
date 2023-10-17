package com.truedigital.features.tuned.data.album.repository

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.music.data.trending.model.response.playlist.Translation
import com.truedigital.features.tuned.api.album.AlbumMetadataApiInterface
import com.truedigital.features.tuned.api.album.AlbumReleaseApiInterface
import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.album.model.Release
import com.truedigital.features.tuned.data.album.model.response.AlbumContext
import com.truedigital.features.tuned.data.database.repository.MusicRoomRepository
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.track.model.request.TrackRequestType
import com.truedigital.features.tuned.data.util.PagedResults
import io.reactivex.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class AlbumRepositoryTest {

    private lateinit var albumRepository: AlbumRepository
    private val albumReleaseApi: AlbumReleaseApiInterface = mock()
    private val albumMetadataApi: AlbumMetadataApiInterface = mock()
    private val musicRoomRepository: MusicRoomRepository = mock()
    private val mockAlbum = Album(
        id = 1234,
        name = "album",
        artists = listOf(),
        primaryRelease = null,
        releaseIds = listOf()
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
        isCached = false,
        translationsList = listOf(
            Translation(
                language = com.truedigital.features.listens.share.constant.MusicConstant.Language.LANG_TH,
                value = "nameTh"
            )
        )
    )

    @BeforeEach
    fun setup() {
        albumRepository = AlbumRepositoryImpl(
            albumReleaseApi = albumReleaseApi,
            albumMetadataApi = albumMetadataApi,
            musicRoomRepository = musicRoomRepository
        )
    }

    @Test
    fun testGet_notRefresh_returnAlbum() {
        whenever(musicRoomRepository.getAlbum(any())).thenReturn(Single.just(mockAlbum))

        albumRepository.get(1, false)
            .test()
            .awaitDone(5, TimeUnit.SECONDS)
            .assertNoErrors()
            .assertValue {
                it.id == 1234 && it.name == "album"
            }

        verify(musicRoomRepository, times(1)).getAlbum(any())
        verify(albumMetadataApi, times(0)).album(any())
        verify(musicRoomRepository, times(0)).insertAlbum(any())
    }

    @Test
    fun testGet_refresh_returnAlbum() {
        whenever(albumMetadataApi.album(any())).thenReturn(Single.just(mockAlbum))
        whenever(musicRoomRepository.insertAlbum(any())).thenReturn(Single.just(1L))

        albumRepository.get(1, true)
            .test()
            .awaitDone(5, TimeUnit.SECONDS)
            .assertValue {
                it.id == 1234 && it.name == "album"
            }

        verify(musicRoomRepository, times(0)).getAlbum(any())
        verify(albumMetadataApi, times(1)).album(any())
        verify(musicRoomRepository, times(1)).insertAlbum(any())
    }

    @Test
    fun testGetTracks_mapName_returnListOfTrack() {
        whenever(albumMetadataApi.releaseTracks(any(), any())).thenReturn(
            Single.just(listOf(mockTrack))
        )

        albumRepository.getTracks(1, TrackRequestType.AUDIO)
            .test()
            .assertNoErrors()
            .assertValue { list ->
                list.size == 1 && list.firstOrNull()?.name == "nameTh"
            }

        verify(albumMetadataApi, times(1)).releaseTracks(1, TrackRequestType.AUDIO.value)
    }

    @Test
    fun testGetTrending_updateRealm_returnListOfAlbum() {
        whenever(albumMetadataApi.trendingAlbums(any(), any())).thenReturn(
            Single.just(
                PagedResults(
                    offset = 1,
                    count = 1,
                    total = 10,
                    results = listOf(mockAlbum)
                )
            )
        )
        whenever(musicRoomRepository.insertAlbums(any())).thenReturn(Single.just(listOf(1L)))

        albumRepository.getTrending(1, 10)
            .test()
            .assertNoErrors()
            .assertValue { list ->
                list.size == 1 && list.firstOrNull()?.id == 1234
            }

        verify(albumMetadataApi, times(1)).trendingAlbums(any(), any())
        verify(musicRoomRepository, times(1)).insertAlbums(any())
    }

    @Test
    fun testGetNewReleases_updateRealm_returnListOfAlbum() {
        whenever(albumMetadataApi.newReleases(any(), any())).thenReturn(
            Single.just(
                PagedResults(
                    offset = 1,
                    count = 1,
                    total = 10,
                    results = listOf(mockAlbum)
                )
            )
        )
        whenever(musicRoomRepository.insertAlbums(any())).thenReturn(Single.just(listOf(1L)))

        albumRepository.getNewReleases(1, 10)
            .test()
            .assertNoErrors()
            .assertValue { list ->
                list.size == 1 && list.firstOrNull()?.id == 1234
            }

        verify(albumMetadataApi, times(1)).newReleases(any(), any())
        verify(musicRoomRepository, times(1)).insertAlbums(any())
    }

    @Test
    fun testGetMoreFromArtist_updateRealm_returnListOfAlbum() {
        whenever(albumMetadataApi.artistAlbums(any())).thenReturn(
            Single.just(
                PagedResults(
                    offset = 1,
                    count = 1,
                    total = 10,
                    results = listOf(mockAlbum)
                )
            )
        )
        whenever(musicRoomRepository.insertAlbums(any())).thenReturn(Single.just(listOf(1L)))

        albumRepository.getMoreFromArtist(1)
            .test()
            .assertNoErrors()
            .assertValue { list ->
                list.size == 1 && list.firstOrNull()?.id == 1234
            }

        verify(albumMetadataApi, times(1)).artistAlbums(any())
        verify(musicRoomRepository, times(1)).insertAlbums(any())
    }

    // -------------------------------- Favorite ------------------------------------------------ //
    @Test
    fun testIsFavourite_returnIsFavourite() {
        val mockIsFavorite = true
        whenever(albumReleaseApi.userContext(any())).thenReturn(
            Single.just(AlbumContext(isFavourited = mockIsFavorite))
        )

        albumRepository.isFavourited(1)
            .test()
            .assertValue(mockIsFavorite)

        verify(albumReleaseApi, times(1)).userContext(1)
    }

    @Test
    fun testRemoveFavourite_verifyUnFavourite() {
        whenever(albumReleaseApi.unFavourite(any())).thenReturn(Single.just(Any()))

        albumRepository.removeFavourite(1)

        verify(albumReleaseApi, times(1)).unFavourite(1)
    }

    @Test
    fun testAddFavourite_verifyAddFavourite() {
        whenever(albumReleaseApi.favourite(any())).thenReturn(Single.just(Any()))

        albumRepository.addFavourite(1)

        verify(albumReleaseApi, times(1)).favourite(1)
    }

    @Test
    fun testGetFavourite_returnListOfRelease() {
        val mockRelease = Release(
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
        whenever(albumReleaseApi.favourited(any(), any())).thenReturn(
            Single.just(
                PagedResults(
                    offset = 1,
                    count = 1,
                    total = 10,
                    results = listOf(mockRelease)
                )
            )
        )

        albumRepository.getFavourited(1, 10)
            .test()
            .assertNoErrors()
            .assertValue { list ->
                list.size == 1 && list.firstOrNull()?.id == 1
            }

        verify(albumReleaseApi, times(1)).favourited(1, 10)
    }

    @Test
    fun testGetFavouriteCount_returnTotal() {
        val mockTotal = 10
        val mockRelease = Release(
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
        whenever(albumReleaseApi.favourited(any(), any())).thenReturn(
            Single.just(
                PagedResults(
                    offset = 1,
                    count = 1,
                    total = mockTotal,
                    results = listOf(mockRelease)
                )
            )
        )

        albumRepository.getFavouritedCount()
            .test()
            .assertNoErrors()
            .assertValue {
                it == mockTotal
            }

        verify(albumReleaseApi, times(1)).favourited(1, 1)
    }
}
