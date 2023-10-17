package com.truedigital.features.tuned.data.playlist.repository

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.api.playlist.PlaylistMetadataApiInterface
import com.truedigital.features.tuned.api.playlist.PlaylistServicesApiInterface
import com.truedigital.features.tuned.data.database.repository.MusicRoomRepository
import com.truedigital.features.tuned.data.playlist.model.response.PlaylistContext
import com.truedigital.features.tuned.data.track.model.request.TrackRequestType
import com.truedigital.features.tuned.data.util.PagedResults
import com.truedigital.features.utils.MockDataModel
import io.reactivex.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.concurrent.TimeUnit

class PlaylistRepositoryTest {

    private lateinit var playlistRepository: PlaylistRepository
    private val playlistServiceApi: PlaylistServicesApiInterface = mock()
    private val playlistMetadataApi: PlaylistMetadataApiInterface = mock()
    private val musicRoomRepository: MusicRoomRepository = mock()
    private val mockPlaylist = MockDataModel.mockPlaylist.apply {
        id = 1234
        creatorId = 123
        isOwner = false
    }
    private val mockTrack = MockDataModel.mockTrack

    @BeforeEach
    fun setup() {
        playlistRepository = PlaylistRepositoryImpl(
            playlistServiceApi = playlistServiceApi,
            playlistMetadataApi = playlistMetadataApi,
            musicRoomRepository = musicRoomRepository
        )
    }

    @Test
    fun testGet_notRefresh_returnPlaylist() {
        whenever(musicRoomRepository.getPlaylist(any())).thenReturn(Single.just(mockPlaylist))

        playlistRepository.get(1, false)
            .test()
            .awaitDone(5, TimeUnit.SECONDS)
            .assertNoErrors()
            .assertValue {
                it.id == 1234
            }

        verify(musicRoomRepository, times(1)).getPlaylist(any())
        verify(playlistServiceApi, times(0)).playlist(any())
        verify(musicRoomRepository, times(0)).insertPlaylist(any())
    }

    @Test
    fun testGet_refresh_returnPlaylist() {
        whenever(playlistServiceApi.playlist(any())).thenReturn(Single.just(mockPlaylist))
        whenever(musicRoomRepository.insertPlaylist(any())).thenReturn(Single.just(1L))

        playlistRepository.get(1, true)
            .test()
            .awaitDone(5, TimeUnit.SECONDS)
            .assertNoErrors()
            .assertValue {
                it.id == 1234
            }

        verify(musicRoomRepository, times(0)).getPlaylist(any())
        verify(playlistServiceApi, times(1)).playlist(any())
        verify(musicRoomRepository, times(1)).insertPlaylist(any())
    }

    @Test
    fun testGetTrending_updateRealm_returnListOfPlaylist() {
        whenever(playlistMetadataApi.trendingPlaylists(any(), any(), any())).thenReturn(
            Single.just(
                PagedResults(offset = 1, count = 1, total = 10, results = listOf(mockPlaylist))
            )
        )
        whenever(musicRoomRepository.insertPlaylists(any())).thenReturn(Single.just(listOf(1L)))

        playlistRepository.getTrending(1, 1, TrackRequestType.AUDIO)
            .test()
            .awaitDone(5, TimeUnit.SECONDS)
            .assertNoErrors()
            .assertValue { list ->
                list.size == 1 && list.firstOrNull()?.id == 1234
            }

        verify(playlistMetadataApi, times(1)).trendingPlaylists(any(), any(), any())
        verify(musicRoomRepository, times(1)).insertPlaylists(any())
    }

    @Test
    fun testGetTracks_mapName_returnListOfTrack() {
        whenever(playlistMetadataApi.getTracks(any(), any(), any())).thenReturn(
            Single.just(
                PagedResults(
                    offset = 1,
                    count = 1,
                    total = 10,
                    results = listOf(mockTrack)
                )
            )
        )

        playlistRepository.getTracks(1, 1, 10)
            .test()
            .assertNoErrors()
            .assertValue { list ->
                list.size == 1 && list.firstOrNull()?.name == "nameTh"
            }

        verify(playlistMetadataApi, times(1)).getTracks(1, 1, 10)
    }

    @Test
    fun testGetTracks_defaultParam_mapName_returnListOfTrack() {
        whenever(playlistMetadataApi.getTracks(any(), any(), any())).thenReturn(
            Single.just(
                PagedResults(
                    offset = 1,
                    count = 1,
                    total = 10,
                    results = listOf(mockTrack)
                )
            )
        )

        playlistRepository.getTracks(1)
            .test()
            .assertNoErrors()
            .assertValue { list ->
                list.size == 1 && list.firstOrNull()?.name == "nameTh"
            }

        verify(playlistMetadataApi, times(1)).getTracks(1, 1, 99)
    }

    // -------------------------------- Favorite ------------------------------------------------ //
    @Test
    fun testIsFavourite_returnIsFavourite() {
        val mockIsFavorite = true
        whenever(playlistServiceApi.userContext(any())).thenReturn(
            Single.just(PlaylistContext(isFavourited = mockIsFavorite))
        )

        playlistRepository.isFavourited(1)
            .test()
            .assertValue(mockIsFavorite)

        verify(playlistServiceApi, times(1)).userContext(1)
    }

    @Test
    fun testRemoveFavourite_verifyUnFavourite() {
        whenever(playlistServiceApi.unfavourite(any())).thenReturn(Single.just(Any()))

        playlistRepository.removeFavourite(1)

        verify(playlistServiceApi, times(1)).unfavourite(1)
    }

    @Test
    fun testAddFavourite_verifyAddFavourite() {
        whenever(playlistServiceApi.favourite(any())).thenReturn(Single.just(Any()))

        playlistRepository.addFavourite(1)

        verify(playlistServiceApi, times(1)).favourite(1)
    }

    @Test
    fun testGetFavouriteAndOwned_isOwner_returnListOfPlaylist() {
        whenever(playlistServiceApi.favouritedAndOwned(any(), any())).thenReturn(
            Single.just(
                PagedResults(
                    offset = 1,
                    count = 1,
                    total = 10,
                    results = listOf(mockPlaylist)
                )
            )
        )
        whenever(musicRoomRepository.insertPlaylists(any())).thenReturn(Single.just(listOf(1L)))

        playlistRepository.getFavouritedAndOwned(1, 1, 123)
            .test()
            .awaitDone(5, TimeUnit.SECONDS)
            .assertNoErrors()
            .assertValue { list ->
                list.size == 1 && list.firstOrNull()?.isOwner == true
            }

        verify(playlistServiceApi, times(1)).favouritedAndOwned(any(), any())
        verify(musicRoomRepository, times(1)).insertPlaylists(any())
    }

    @Test
    fun testGetFavouriteAndOwned_isNotOwner_returnListOfPlaylist() {
        whenever(playlistServiceApi.favouritedAndOwned(any(), any())).thenReturn(
            Single.just(
                PagedResults(
                    offset = 1,
                    count = 1,
                    total = 10,
                    results = listOf(mockPlaylist)
                )
            )
        )
        whenever(musicRoomRepository.insertPlaylists(any())).thenReturn(Single.just(listOf(1L)))

        playlistRepository.getFavouritedAndOwned(1, 1, 1)
            .test()
            .awaitDone(5, TimeUnit.SECONDS)
            .assertNoErrors()
            .assertValue { list ->
                list.size == 1 && list.firstOrNull()?.isOwner == false
            }

        verify(playlistServiceApi, times(1)).favouritedAndOwned(any(), any())
        verify(musicRoomRepository, times(1)).insertPlaylists(any())
    }

    @Test
    fun testGetFavouriteAndOwned_currentUserIdNull_returnListOfPlaylistNotOwner() {
        whenever(playlistServiceApi.favouritedAndOwned(any(), any())).thenReturn(
            Single.just(
                PagedResults(
                    offset = 1,
                    count = 1,
                    total = 10,
                    results = listOf(mockPlaylist)
                )
            )
        )
        whenever(musicRoomRepository.insertPlaylists(any())).thenReturn(Single.just(listOf(1L)))

        playlistRepository.getFavouritedAndOwned(1, 1)
            .test()
            .assertNoErrors()
            .assertValue { list ->
                list.size == 1 && list.firstOrNull()?.isOwner == false
            }

        verify(playlistServiceApi, times(1)).favouritedAndOwned(any(), any())
        verify(musicRoomRepository, times(1)).insertPlaylists(any())
    }

    @Test
    fun testGetFavourite_isOwner_returnListOfPlaylist() {
        whenever(playlistServiceApi.favourited(any(), any())).thenReturn(
            Single.just(
                PagedResults(
                    offset = 1,
                    count = 1,
                    total = 10,
                    results = listOf(mockPlaylist)
                )
            )
        )
        whenever(musicRoomRepository.insertPlaylists(any())).thenReturn(Single.just(listOf(1L)))

        playlistRepository.getFavourited(1, 1, 123)
            .test()
            .awaitDone(5, TimeUnit.SECONDS)
            .assertNoErrors()
            .assertValue { list ->
                list.size == 1 && list.firstOrNull()?.isOwner == true
            }

        verify(playlistServiceApi, times(1)).favourited(any(), any())
        verify(musicRoomRepository, times(1)).insertPlaylists(any())
    }

    @Test
    fun testGetFavourite_isNotOwner_returnListOfPlaylist() {
        whenever(playlistServiceApi.favourited(any(), any())).thenReturn(
            Single.just(
                PagedResults(
                    offset = 1,
                    count = 1,
                    total = 10,
                    results = listOf(mockPlaylist, mockPlaylist)
                )
            )
        )
        whenever(musicRoomRepository.insertPlaylists(any())).thenReturn(Single.just(listOf(1L, 2L)))

        playlistRepository.getFavourited(1, 1, 1)
            .test()
            .awaitDone(5, TimeUnit.SECONDS)
            .assertNoErrors()
            .assertValue { list ->
                list.size == 2 && !list[0].isOwner && !list[1].isOwner
            }

        verify(playlistServiceApi, times(1)).favourited(1, 1)
        verify(musicRoomRepository, times(1)).insertPlaylists(any())
    }

    @Test
    fun testGetFavourite_currentUserIdNull_returnListOfPlaylistNotOwner() {
        whenever(playlistServiceApi.favourited(any(), any())).thenReturn(
            Single.just(
                PagedResults(
                    offset = 1,
                    count = 1,
                    total = 10,
                    results = listOf(mockPlaylist)
                )
            )
        )
        whenever(musicRoomRepository.insertPlaylists(any())).thenReturn(Single.just(listOf(1L)))

        playlistRepository.getFavourited(1, 1)
            .test()
            .awaitDone(5, TimeUnit.SECONDS)
            .assertNoErrors()
            .assertValue { list ->
                list.size == 1 && !list[0].isOwner
            }

        verify(playlistServiceApi, times(1)).favourited(1, 1)
        verify(musicRoomRepository, times(1)).insertPlaylists(any())
    }

    @Test
    fun testGetFavouriteCount_returnTotal() {
        val mockTotal = 10
        whenever(playlistServiceApi.favourited(any(), any())).thenReturn(
            Single.just(
                PagedResults(
                    offset = 1,
                    count = 1,
                    total = mockTotal,
                    results = listOf(mockPlaylist)
                )
            )
        )

        playlistRepository.getFavouritedCount()
            .test()
            .assertValue {
                it == mockTotal
            }

        verify(playlistServiceApi, times(1)).favourited(1, 1)
    }
}
