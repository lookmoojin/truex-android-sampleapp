package com.truedigital.features.tuned.data.artist.repository

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.music.data.trending.model.response.playlist.Translation
import com.truedigital.features.tuned.api.artist.ArtistMetadataApiInterface
import com.truedigital.features.tuned.api.artist.ArtistServicesApiInterface
import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.artist.model.ArtistPlayCount
import com.truedigital.features.tuned.data.artist.model.response.ArtistContext
import com.truedigital.features.tuned.data.database.repository.MusicRoomRepository
import com.truedigital.features.tuned.data.track.model.PrimaryTrack
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.track.model.request.TrackRequestType
import com.truedigital.features.tuned.data.util.PagedResults
import io.reactivex.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class ArtistRepositoryTest {

    private lateinit var artistRepository: ArtistRepository
    private val artistServiceApi: ArtistServicesApiInterface = mock()
    private val artistMetadataApi: ArtistMetadataApiInterface = mock()
    private val musicRoomRepository: MusicRoomRepository = mock()
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
        artistRepository = ArtistRepositoryImpl(
            artistServiceApi = artistServiceApi,
            artistMetadataApi = artistMetadataApi,
            musicRoomRepository = musicRoomRepository
        )
    }

    @Test
    fun testGet_notRefresh_returnArtist() {
        whenever(musicRoomRepository.getArtist(any())).thenReturn(Single.just(mockArtist))

        artistRepository.get(1, false)
            .test()
            .awaitDone(5, TimeUnit.SECONDS)
            .assertNoErrors()
            .assertValue {
                it.id == 1234 && it.name == "artist"
            }

        verify(musicRoomRepository, times(1)).getArtist(any())
        verify(artistMetadataApi, times(0)).artist(any())
        verify(musicRoomRepository, times(0)).insertArtist(any())
    }

    @Test
    fun testGetPlayCount_returnGlobalTotal() {
        val mockGlobalTotal = 10
        whenever(artistMetadataApi.playCount(any())).thenReturn(
            Single.just(
                ArtistPlayCount(
                    globalTotal = mockGlobalTotal,
                    globalRecent = 1,
                    distinctGlobalTotal = 1,
                    distinctGlobalRecent = 1
                )
            )
        )

        artistRepository.getPlayCount(1)
            .test()
            .assertNoErrors()
            .assertValue {
                it == mockGlobalTotal
            }

        verify(artistMetadataApi, times(1)).playCount(1)
    }

    @Test
    fun testGetTrending_returnListOfArtist() {
        whenever(artistMetadataApi.trendingArtists(any(), any())).thenReturn(
            Single.just(
                PagedResults(
                    offset = 1,
                    count = 1,
                    total = 10,
                    results = listOf(mockArtist)
                )
            )
        )
        whenever(musicRoomRepository.insertArtists(any())).thenReturn(Single.just(listOf(1L)))

        artistRepository.getTrending(1, 1)
            .test()
            .awaitDone(5, TimeUnit.SECONDS)
            .assertNoErrors()
            .assertValue { list ->
                list.size == 1 && list.firstOrNull()?.id == 1234
            }

        verify(artistMetadataApi, times(1)).trendingArtists(any(), any())
        verify(musicRoomRepository, times(1)).insertArtists(any())
    }

    @Test
    fun testGetStationTrending_returnListOfArtist() {
        whenever(artistMetadataApi.stationTrendingArtists(any())).thenReturn(
            Single.just(listOf(mockArtist))
        )
        whenever(musicRoomRepository.insertArtists(any())).thenReturn(Single.just(listOf(1L)))

        artistRepository.getStationTrending(1)
            .test()
            .awaitDone(5, TimeUnit.SECONDS)
            .assertNoErrors()
            .assertValue { list ->
                list.size == 1 && list.firstOrNull()?.id == 1234
            }

        verify(artistMetadataApi, times(1)).stationTrendingArtists(any())
        verify(musicRoomRepository, times(1)).insertArtists(any())
    }

    @Test
    fun testGetSimilar_returnListOfArtist() {
        whenever(artistMetadataApi.similarArtists(any())).thenReturn(
            Single.just(listOf(mockArtist))
        )
        whenever(musicRoomRepository.insertArtists(any())).thenReturn(Single.just(listOf(1L)))

        artistRepository.getSimilar(1)
            .test()
            .awaitDone(5, TimeUnit.SECONDS)
            .assertNoErrors()
            .assertValue { list ->
                list.size == 1 && list.firstOrNull()?.id == 1234
            }

        verify(artistMetadataApi, times(1)).similarArtists(any())
        verify(musicRoomRepository, times(1)).insertArtists(any())
    }

    @Test
    fun testGetAlbums_returnListOfAlbum() {
        whenever(artistMetadataApi.getAlbums(any(), anyOrNull(), any(), any())).thenReturn(
            Single.just(
                PagedResults(
                    offset = 1,
                    count = 1,
                    total = 10,
                    results = listOf(mockAlbum)
                )
            )
        )

        artistRepository.getAlbums(1, null, 1, 1)
            .test()
            .assertNoErrors()
            .assertValue { list ->
                list.size == 1 && list.firstOrNull()?.id == 1234
            }

        verify(artistMetadataApi, times(1)).getAlbums(1, null, 1, 1)
    }

    @Test
    fun testGetAppearsOn_returnListOfAlbum() {
        whenever(artistMetadataApi.getAppearsOn(any(), anyOrNull())).thenReturn(
            Single.just(
                PagedResults(
                    offset = 1,
                    count = 1,
                    total = 10,
                    results = listOf(mockAlbum)
                )
            )
        )

        artistRepository.getAppearsOn(1, null)
            .test()
            .assertNoErrors()
            .assertValue { list ->
                list.size == 1 && list.firstOrNull()?.id == 1234
            }

        verify(artistMetadataApi, times(1)).getAppearsOn(1, null)
    }

    @Test
    fun testGetTracks_returnListOfTrack() {
        whenever(
            artistMetadataApi.getTracks(
                any(),
                any(),
                any(),
                any(),
                anyOrNull(),
                anyOrNull()
            )
        ).thenReturn(
            Single.just(
                PagedResults(
                    offset = 1,
                    count = 1,
                    total = 10,
                    results = listOf(
                        PrimaryTrack(
                            primaryTrack = mockTrack
                        )
                    )
                )
            )
        )

        artistRepository.getTracks(1, 1, 1, TrackRequestType.AUDIO, null, null)
            .test()
            .assertNoErrors()
            .assertValue { list ->
                list.size == 1 && list.firstOrNull()?.name == "nameTh"
            }

        verify(artistMetadataApi, times(1)).getTracks(
            1,
            1,
            1,
            TrackRequestType.AUDIO.value,
            null,
            null
        )
    }

    // -------------------------------- Favorite ------------------------------------------------ //
    @Test
    fun testGetRecommendedArtists_returnListOfArtist() {
        whenever(artistServiceApi.recommendedArtists(any(), any())).thenReturn(
            Single.just(
                PagedResults(
                    offset = 1,
                    count = 1,
                    total = 10,
                    results = listOf(mockArtist)
                )
            )
        )
        whenever(musicRoomRepository.insertArtists(any())).thenReturn(Single.just(listOf(1L)))

        artistRepository.getRecommendedArtists(1, 10)
            .test()
            .awaitDone(5, TimeUnit.SECONDS)
            .assertNoErrors()
            .assertValue { list ->
                list.size == 1 && list.firstOrNull()?.id == 1234
            }

        verify(artistServiceApi, times(1)).recommendedArtists(any(), any())
        verify(musicRoomRepository, times(1)).insertArtists(any())
    }

    @Test
    fun testGetFollowed_returnListOfArtist() {
        whenever(artistServiceApi.followedArtists(any(), any())).thenReturn(
            Single.just(
                PagedResults(
                    offset = 1,
                    count = 1,
                    total = 10,
                    results = listOf(mockArtist)
                )
            )
        )
        whenever(musicRoomRepository.insertArtists(any())).thenReturn(Single.just(listOf(1L)))

        artistRepository.getFollowed(1, 1)
            .test()
            .awaitDone(5, TimeUnit.SECONDS)
            .assertNoErrors()
            .assertValue { list ->
                list.size == 1 && list.firstOrNull()?.id == 1234
            }

        verify(artistServiceApi, times(1)).followedArtists(any(), any())
        verify(musicRoomRepository, times(1)).insertArtists(any())
    }

    @Test
    fun testIsFollowing_returnIsFollowing() {
        val mockIsFollowing = true
        whenever(artistServiceApi.userContext(any())).thenReturn(
            Single.just(ArtistContext(isFollowing = mockIsFollowing))
        )

        artistRepository.isFollowing(1)
            .test()
            .assertNoErrors()
            .assertValue(mockIsFollowing)

        verify(artistServiceApi, times(1)).userContext(1)
    }

    @Test
    fun testRemoveFollowing_verifyUnFollowArtist() {
        whenever(artistServiceApi.unfollowArtist(any())).thenReturn(Single.just(Any()))

        artistRepository.removeFollow(1)

        verify(artistServiceApi, times(1)).unfollowArtist(1)
    }

    @Test
    fun testAddFollowing_verifyFollowArtist() {
        whenever(artistServiceApi.followArtist(any())).thenReturn(Single.just(Any()))

        artistRepository.addFollow(1)

        verify(artistServiceApi, times(1)).followArtist(1)
    }

    @Test
    fun testFollowedCount_returnTotal() {
        val mockTotal = 10
        whenever(artistServiceApi.followedArtists(any(), any())).thenReturn(
            Single.just(
                PagedResults(
                    offset = 1,
                    count = 1,
                    total = mockTotal,
                    results = listOf(mockArtist)
                )
            )
        )

        artistRepository.getFollowedCount()
            .test()
            .assertNoErrors()
            .assertValue {
                it == mockTotal
            }

        verify(artistServiceApi, times(1)).followedArtists(1, 1)
    }

    @Test
    fun testClearArtistVotes_verifyClearArtistVotes() {
        whenever(artistServiceApi.clearArtistVotes(any(), any())).thenReturn(Single.just(Any()))

        artistRepository.clearArtistVotes(1, "type")

        verify(artistServiceApi, times(1)).clearArtistVotes(1, "type")
    }
}
