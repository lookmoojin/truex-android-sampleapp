package com.truedigital.features.tuned.domain.facade

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.album.repository.AlbumRepository
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.artist.repository.ArtistRepository
import com.truedigital.features.tuned.data.cache.repository.CacheRepository
import com.truedigital.features.tuned.data.playlist.repository.PlaylistRepository
import com.truedigital.features.tuned.data.profile.model.Profile
import com.truedigital.features.tuned.data.profile.repository.ProfileRepository
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.station.repository.StationRepository
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.track.repository.TrackRepository
import com.truedigital.features.tuned.data.user.model.User
import com.truedigital.features.tuned.data.user.repository.MusicUserRepository
import com.truedigital.features.tuned.domain.facade.bottomsheetproduct.BottomSheetProductFacade
import com.truedigital.features.tuned.domain.facade.bottomsheetproduct.BottomSheetProductFacadeImpl
import com.truedigital.features.tuned.presentation.bottomsheet.ProductPickerType
import com.truedigital.features.utils.MockDataModel
import io.reactivex.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BottomSheetProductFacadeImplTest {

    private lateinit var bottomSheetProductFacade: BottomSheetProductFacade
    private val stationRepository: StationRepository = mock()
    private val artistRepository: ArtistRepository = mock()
    private val albumRepository: AlbumRepository = mock()
    private val playlistRepository: PlaylistRepository = mock()
    private val trackRepository: TrackRepository = mock()
    private val musicUserRepository: MusicUserRepository = mock()
    private val cacheRepository: CacheRepository = mock()
    private val profileRepository: ProfileRepository = mock()

    private val mockPlaylist = MockDataModel.mockPlaylist.copy(id = 2)
    private val mockTrack = MockDataModel.mockTrack.copy(id = 2)
    private fun mockTrack(trackNumber: Int): Track =
        MockDataModel.mockTrack.copy(id = 2, trackNumber = trackNumber)

    @BeforeEach
    fun setup() {
        bottomSheetProductFacade = BottomSheetProductFacadeImpl(
            stationRepository,
            artistRepository,
            albumRepository,
            playlistRepository,
            trackRepository,
            musicUserRepository,
            cacheRepository,
            profileRepository
        )
    }

    // //////////////////////////////////////// getProduct //////////////////////////////////////////
    @Test
    fun testGetProduct_productTypeIsAlbum_verifyAlbumRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.ALBUM
        whenever(albumRepository.get(mockId))
            .thenReturn(
                Single.just(
                    Album(
                        id = 2,
                        name = "album",
                        artists = listOf(),
                        primaryRelease = null,
                        releaseIds = listOf()
                    )
                )
            )

        bottomSheetProductFacade.getProduct(mockId, mockType).test().assertValue {
            it.id == 2
        }

        verify(albumRepository, times(1)).get(mockId, false)
    }

    @Test
    fun testGetProduct_productTypeIsArtist_verifyArtistRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.ARTIST
        whenever(artistRepository.get(mockId))
            .thenReturn(
                Single.just(
                    Artist(
                        id = 2,
                        name = "album",
                        image = "image"
                    )
                )
            )

        bottomSheetProductFacade.getProduct(mockId, mockType).test().assertValue {
            it.id == 2
        }

        verify(artistRepository, times(1)).get(mockId, false)
    }

    @Test
    fun testGetProduct_productTypeIsMix_verifyStationRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.MIX
        whenever(stationRepository.get(mockId))
            .thenReturn(
                Single.just(
                    Station(
                        id = 2,
                        type = Station.StationType.ARTIST,
                        name = listOf(),
                        description = listOf(),
                        coverImage = listOf(),
                        bannerImage = listOf(),
                        bannerURL = null,
                        isActive = false
                    )
                )
            )

        bottomSheetProductFacade.getProduct(mockId, mockType).test().assertValue {
            it.id == 2
        }

        verify(stationRepository, times(1)).get(mockId, false)
    }

    @Test
    fun testGetProduct_productTypeIsPlaylist_verifyPlaylistRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.PLAYLIST
        whenever(playlistRepository.get(mockId)).thenReturn(Single.just(mockPlaylist))

        bottomSheetProductFacade.getProduct(mockId, mockType).test().assertValue {
            it.id == 2
        }

        verify(playlistRepository, times(1)).get(mockId, false)
    }

    @Test
    fun testGetProduct_productTypeIsPlaylistOwner_verifyPlaylistRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.PLAYLIST_OWNER
        whenever(
            playlistRepository.get(
                any(),
                any()
            )
        ).thenReturn(Single.just(mockPlaylist))

        bottomSheetProductFacade.getProduct(mockId, mockType).test().assertValue {
            it.id == 2
        }

        verify(playlistRepository, times(1)).get(mockId, true)
    }

    @Test
    fun testGetProduct_productTypeIsPlaylistVerifiedOwner_verifyPlaylistRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.PLAYLIST_VERIFIED_OWNER
        whenever(playlistRepository.get(mockId)).thenReturn(Single.just(mockPlaylist))

        bottomSheetProductFacade.getProduct(mockId, mockType).test().assertValue {
            it.id == 2
        }

        verify(playlistRepository, times(1)).get(mockId, false)
    }

    @Test
    fun testGetProduct_productTypeIsProfile_verifyProfileRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.PROFILE
        whenever(profileRepository.get(mockId))
            .thenReturn(
                Single.just(
                    Profile(
                        id = 2,
                        username = "username",
                        name = "name",
                        image = null,
                        backgroundImage = null,
                        isVerified = false,
                        contentKey = null,
                        followerCount = 0
                    )
                )
            )

        bottomSheetProductFacade.getProduct(mockId, mockType).test().assertValue {
            it.id == 2
        }

        verify(profileRepository, times(1)).get(mockId)
    }

    @Test
    fun testGetProduct_productTypeIsSong_verifyTrackRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.SONG
        whenever(trackRepository.get(mockId)).thenReturn(Single.just(mockTrack))

        bottomSheetProductFacade.getProduct(mockId, mockType).test().assertValue {
            it.id == 2
        }

        verify(trackRepository, times(1)).get(mockId)
    }

    @Test
    fun testGetProduct_productTypeIsVideo_verifyTrackRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.VIDEO
        whenever(trackRepository.get(mockId)).thenReturn(Single.just(mockTrack))

        bottomSheetProductFacade.getProduct(mockId, mockType).test().assertValue {
            it.id == 2
        }

        verify(trackRepository, times(1)).get(mockId)
    }

    @Test
    fun testGetProduct_productTypeIsArtistSong_verifyTrackRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.ARTIST_SONG
        whenever(trackRepository.get(mockId)).thenReturn(Single.just(mockTrack))

        bottomSheetProductFacade.getProduct(mockId, mockType).test().assertValue {
            it.id == 2
        }

        verify(trackRepository, times(1)).get(mockId)
    }

    @Test
    fun testGetProduct_productTypeIsAlbumSong_verifyTrackRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.ALBUM_SONG
        whenever(trackRepository.get(mockId)).thenReturn(Single.just(mockTrack))

        bottomSheetProductFacade.getProduct(mockId, mockType).test().assertValue {
            it.id == 2
        }

        verify(trackRepository, times(1)).get(mockId)
    }

    @Test
    fun testGetProduct_productTypeIsPlayListSong_verifyTrackRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.PLAYLIST_SONG
        whenever(trackRepository.get(mockId)).thenReturn(Single.just(mockTrack))

        bottomSheetProductFacade.getProduct(mockId, mockType).test().assertValue {
            it.id == 2
        }

        verify(trackRepository, times(1)).get(mockId)
    }

    @Test
    fun testGetProduct_productTypeIsSongPlayer_verifyTrackRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.SONG_PLAYER
        whenever(trackRepository.get(mockId)).thenReturn(Single.just(mockTrack))

        bottomSheetProductFacade.getProduct(mockId, mockType).test().assertValue {
            it.id == 2
        }

        verify(trackRepository, times(1)).get(mockId)
    }

    @Test
    fun testGetProduct_productTypeIsSongQueue_verifyTrackRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.SONG_QUEUE
        whenever(trackRepository.get(mockId)).thenReturn(Single.just(mockTrack))

        bottomSheetProductFacade.getProduct(mockId, mockType).test().assertValue {
            it.id == 2
        }

        verify(trackRepository, times(1)).get(mockId)
    }

    @Test
    fun testGetProduct_productTypeIsVideoPlayer_verifyTrackRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.VIDEO_PLAYER
        whenever(trackRepository.get(mockId)).thenReturn(Single.just(mockTrack))

        bottomSheetProductFacade.getProduct(mockId, mockType).test().assertValue {
            it.id == 2
        }

        verify(trackRepository, times(1)).get(mockId)
    }

    @Test
    fun testGetProduct_productTypeNull_errorIllegalStateException() {
        val mockId = 1
        val mockType = null

        bottomSheetProductFacade.getProduct(mockId, mockType)
            .test()
            .assertError(IllegalStateException::class.java)
    }

    // ///////////////////////////////////// addToCollection ////////////////////////////////////////
    @Test
    fun testAddToCollection_productTypeIsAlbum_verifyAlbumRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.ALBUM
        whenever(albumRepository.addFavourite(mockId)).thenReturn(Single.just(Any()))

        bottomSheetProductFacade.addToCollection(mockId, mockType)
            .test()
            .assertNoErrors()

        verify(albumRepository, times(1)).addFavourite(mockId)
    }

    @Test
    fun testAddToCollection_productTypeIsArtist_verifyArtistRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.ARTIST
        whenever(artistRepository.addFollow(mockId)).thenReturn(Single.just(Any()))

        bottomSheetProductFacade.addToCollection(mockId, mockType)
            .test()
            .assertNoErrors()

        verify(artistRepository, times(1)).addFollow(mockId)
    }

    @Test
    fun testAddToCollection_productTypeIsMix_verifyStationRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.MIX
        whenever(stationRepository.addFavourite(mockId)).thenReturn(Single.just(Any()))

        bottomSheetProductFacade.addToCollection(mockId, mockType)
            .test()
            .assertNoErrors()

        verify(stationRepository, times(1)).addFavourite(mockId)
    }

    @Test
    fun testAddToCollection_productTypeIsPlayList_verifyPlayListRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.PLAYLIST
        whenever(playlistRepository.addFavourite(mockId)).thenReturn(Single.just(Any()))

        bottomSheetProductFacade.addToCollection(mockId, mockType)
            .test()
            .assertNoErrors()

        verify(playlistRepository, times(1)).addFavourite(mockId)
    }

    @Test
    fun testAddToCollection_productTypeIsPlayListOwner_verifyPlayListRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.PLAYLIST_OWNER
        whenever(playlistRepository.addFavourite(mockId)).thenReturn(Single.just(Any()))

        bottomSheetProductFacade.addToCollection(mockId, mockType)
            .test()
            .assertNoErrors()

        verify(playlistRepository, times(1)).addFavourite(mockId)
    }

    @Test
    fun testAddToCollection_productTypeIsPlayListVerifiedOwner_verifyPlayListRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.PLAYLIST_VERIFIED_OWNER
        whenever(playlistRepository.addFavourite(mockId)).thenReturn(Single.just(Any()))

        bottomSheetProductFacade.addToCollection(mockId, mockType)
            .test()
            .assertNoErrors()

        verify(playlistRepository, times(1)).addFavourite(mockId)
    }

    @Test
    fun testAddToCollection_productTypeIsProfile_verifyProfileRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.PROFILE
        whenever(profileRepository.addFollow(mockId)).thenReturn(Single.just(Any()))

        bottomSheetProductFacade.addToCollection(mockId, mockType)
            .test()
            .assertNoErrors()

        verify(profileRepository, times(1)).addFollow(mockId)
    }

    @Test
    fun testAddToCollection_productTypeIsSong_verifyTrackRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.SONG
        whenever(trackRepository.addFavourite(mockId)).thenReturn(Single.just(Any()))

        bottomSheetProductFacade.addToCollection(mockId, mockType)
            .test()
            .assertNoErrors()

        verify(trackRepository, times(1)).addFavourite(mockId)
    }

    @Test
    fun testAddToCollection_productTypeIsVideo_verifyTrackRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.VIDEO
        whenever(trackRepository.addFavourite(mockId)).thenReturn(Single.just(Any()))

        bottomSheetProductFacade.addToCollection(mockId, mockType)
            .test()
            .assertNoErrors()

        verify(trackRepository, times(1)).addFavourite(mockId)
    }

    @Test
    fun testAddToCollection_productTypeIsArtistSong_verifyTrackRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.ARTIST_SONG
        whenever(trackRepository.addFavourite(mockId)).thenReturn(Single.just(Any()))

        bottomSheetProductFacade.addToCollection(mockId, mockType)
            .test()
            .assertNoErrors()

        verify(trackRepository, times(1)).addFavourite(mockId)
    }

    @Test
    fun testAddToCollection_productTypeIsAlbumSong_verifyTrackRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.ALBUM_SONG
        whenever(trackRepository.addFavourite(mockId)).thenReturn(Single.just(Any()))

        bottomSheetProductFacade.addToCollection(mockId, mockType)
            .test()
            .assertNoErrors()

        verify(trackRepository, times(1)).addFavourite(mockId)
    }

    @Test
    fun testAddToCollection_productTypeIsPlayListSong_verifyTrackRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.PLAYLIST_SONG
        whenever(trackRepository.addFavourite(mockId)).thenReturn(Single.just(Any()))

        bottomSheetProductFacade.addToCollection(mockId, mockType)
            .test()
            .assertNoErrors()

        verify(trackRepository, times(1)).addFavourite(mockId)
    }

    @Test
    fun testAddToCollection_productTypeIsSongPlayer_verifyTrackRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.SONG_PLAYER
        whenever(trackRepository.addFavourite(mockId)).thenReturn(Single.just(Any()))

        bottomSheetProductFacade.addToCollection(mockId, mockType)
            .test()
            .assertNoErrors()

        verify(trackRepository, times(1)).addFavourite(mockId)
    }

    @Test
    fun testAddToCollection_productTypeIsSongQueue_verifyTrackRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.SONG_QUEUE
        whenever(trackRepository.addFavourite(mockId)).thenReturn(Single.just(Any()))

        bottomSheetProductFacade.addToCollection(mockId, mockType)
            .test()
            .assertNoErrors()

        verify(trackRepository, times(1)).addFavourite(mockId)
    }

    @Test
    fun testAddToCollection_productTypeIsVideoPlayer_verifyTrackRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.VIDEO_PLAYER
        whenever(trackRepository.addFavourite(mockId)).thenReturn(Single.just(Any()))

        bottomSheetProductFacade.addToCollection(mockId, mockType)
            .test()
            .assertNoErrors()

        verify(trackRepository, times(1)).addFavourite(mockId)
    }

    @Test
    fun testAddToCollection_productTypeIsNull_errorIllegalStateException() {
        val mockId = 1
        val mockType = null

        bottomSheetProductFacade.addToCollection(mockId, mockType)
            .test()
            .assertError(IllegalStateException::class.java)
    }

    // /////////////////////////////////// removeFromCollection /////////////////////////////////////
    @Test
    fun testRemoveFromCollection_productTypeIsAlbum_verifyAlbumRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.ALBUM
        whenever(albumRepository.removeFavourite(mockId)).thenReturn(Single.just(Any()))

        bottomSheetProductFacade.removeFromCollection(mockId, mockType)
            .test()
            .assertNoErrors()

        verify(albumRepository, times(1)).removeFavourite(mockId)
    }

    @Test
    fun testRemoveFromCollection_productTypeIsArtist_verifyArtistRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.ARTIST
        whenever(artistRepository.removeFollow(mockId)).thenReturn(Single.just(Any()))

        bottomSheetProductFacade.removeFromCollection(mockId, mockType)
            .test()
            .assertNoErrors()

        verify(artistRepository, times(1)).removeFollow(mockId)
    }

    @Test
    fun testRemoveFromCollection_productTypeIsMix_verifyStationRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.MIX
        whenever(stationRepository.removeFavourite(mockId)).thenReturn(Single.just(Any()))

        bottomSheetProductFacade.removeFromCollection(mockId, mockType)
            .test()
            .assertNoErrors()

        verify(stationRepository, times(1)).removeFavourite(mockId)
    }

    @Test
    fun testRemoveFromCollection_productTypeIsPlayList_verifyPlayListRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.PLAYLIST
        whenever(playlistRepository.removeFavourite(mockId)).thenReturn(Single.just(Any()))

        bottomSheetProductFacade.removeFromCollection(mockId, mockType)
            .test()
            .assertNoErrors()

        verify(playlistRepository, times(1)).removeFavourite(mockId)
    }

    @Test
    fun testRemoveFromCollection_productTypeIsPlayListOwner_verifyPlayListRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.PLAYLIST_OWNER
        whenever(playlistRepository.removeFavourite(mockId)).thenReturn(Single.just(Any()))

        bottomSheetProductFacade.removeFromCollection(mockId, mockType)
            .test()
            .assertNoErrors()

        verify(playlistRepository, times(1)).removeFavourite(mockId)
    }

    @Test
    fun testRemoveFromCollection_productTypeIsPlayListVerifiedOwner_verifyPlayListRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.PLAYLIST_VERIFIED_OWNER
        whenever(playlistRepository.removeFavourite(mockId)).thenReturn(Single.just(Any()))

        bottomSheetProductFacade.removeFromCollection(mockId, mockType)
            .test()
            .assertNoErrors()

        verify(playlistRepository, times(1)).removeFavourite(mockId)
    }

    @Test
    fun testRemoveFromCollection_productTypeIsProfile_verifyProfileRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.PROFILE
        whenever(profileRepository.removeFollow(mockId)).thenReturn(Single.just(Any()))

        bottomSheetProductFacade.removeFromCollection(mockId, mockType)
            .test()
            .assertNoErrors()

        verify(profileRepository, times(1)).removeFollow(mockId)
    }

    @Test
    fun testRemoveFromCollection_productTypeIsSong_verifyTrackRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.SONG
        whenever(trackRepository.removeFavourite(mockId)).thenReturn(Single.just(Any()))

        bottomSheetProductFacade.removeFromCollection(mockId, mockType)
            .test()
            .assertNoErrors()

        verify(trackRepository, times(1)).removeFavourite(mockId)
    }

    @Test
    fun testRemoveFromCollection_productTypeIsVideo_verifyTrackRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.VIDEO
        whenever(trackRepository.removeFavourite(mockId)).thenReturn(Single.just(Any()))

        bottomSheetProductFacade.removeFromCollection(mockId, mockType)
            .test()
            .assertNoErrors()

        verify(trackRepository, times(1)).removeFavourite(mockId)
    }

    @Test
    fun testRemoveFromCollection_productTypeIsArtistSong_verifyTrackRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.ARTIST_SONG
        whenever(trackRepository.removeFavourite(mockId)).thenReturn(Single.just(Any()))

        bottomSheetProductFacade.removeFromCollection(mockId, mockType)
            .test()
            .assertNoErrors()

        verify(trackRepository, times(1)).removeFavourite(mockId)
    }

    @Test
    fun testRemoveFromCollection_productTypeIsAlbumSong_verifyTrackRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.ALBUM_SONG
        whenever(trackRepository.removeFavourite(mockId)).thenReturn(Single.just(Any()))

        bottomSheetProductFacade.removeFromCollection(mockId, mockType)
            .test()
            .assertNoErrors()

        verify(trackRepository, times(1)).removeFavourite(mockId)
    }

    @Test
    fun testRemoveFromCollection_productTypeIsPlayListSong_verifyTrackRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.PLAYLIST_SONG
        whenever(trackRepository.removeFavourite(mockId)).thenReturn(Single.just(Any()))

        bottomSheetProductFacade.removeFromCollection(mockId, mockType)
            .test()
            .assertNoErrors()

        verify(trackRepository, times(1)).removeFavourite(mockId)
    }

    @Test
    fun testRemoveFromCollection_productTypeIsSongPlayer_verifyTrackRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.SONG_PLAYER
        whenever(trackRepository.removeFavourite(mockId)).thenReturn(Single.just(Any()))

        bottomSheetProductFacade.removeFromCollection(mockId, mockType)
            .test()
            .assertNoErrors()

        verify(trackRepository, times(1)).removeFavourite(mockId)
    }

    @Test
    fun testRemoveFromCollection_productTypeIsSongQueue_verifyTrackRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.SONG_QUEUE
        whenever(trackRepository.removeFavourite(mockId)).thenReturn(Single.just(Any()))

        bottomSheetProductFacade.removeFromCollection(mockId, mockType)
            .test()
            .assertNoErrors()

        verify(trackRepository, times(1)).removeFavourite(mockId)
    }

    @Test
    fun testRemoveFromCollection_productTypeIsVideoPlayer_verifyTrackRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.VIDEO_PLAYER
        whenever(trackRepository.removeFavourite(mockId)).thenReturn(Single.just(Any()))

        bottomSheetProductFacade.removeFromCollection(mockId, mockType)
            .test()
            .assertNoErrors()

        verify(trackRepository, times(1)).removeFavourite(mockId)
    }

    @Test
    fun testRemoveFromCollection_productTypeIsNull_errorIllegalStateException() {
        val mockId = 1
        val mockType = null

        bottomSheetProductFacade.removeFromCollection(mockId, mockType)
            .test()
            .assertError(IllegalStateException::class.java)
    }

    // ////////////////////////////////////// isInCollection ////////////////////////////////////////
    @Test
    fun testIsInCollection_productTypeIsAlbum_verifyAlbumRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.ALBUM
        whenever(albumRepository.isFavourited(mockId)).thenReturn(Single.just(true))

        bottomSheetProductFacade.isInCollection(mockId, mockType).test().assertValue(true)

        verify(albumRepository, times(1)).isFavourited(mockId)
    }

    @Test
    fun testIsInCollection_productTypeIsArtist_verifyArtistRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.ARTIST
        whenever(artistRepository.isFollowing(mockId)).thenReturn(Single.just(true))

        bottomSheetProductFacade.isInCollection(mockId, mockType).test().assertValue(true)

        verify(artistRepository, times(1)).isFollowing(mockId)
    }

    @Test
    fun testIsInCollection_productTypeIsMix_verifyStationRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.MIX
        whenever(stationRepository.isFavourited(mockId)).thenReturn(Single.just(true))

        bottomSheetProductFacade.isInCollection(mockId, mockType).test().assertValue(true)

        verify(stationRepository, times(1)).isFavourited(mockId)
    }

    @Test
    fun testIsInCollection_productTypeIsPlayList_verifyPlayListRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.PLAYLIST
        whenever(playlistRepository.isFavourited(mockId)).thenReturn(Single.just(true))

        bottomSheetProductFacade.isInCollection(mockId, mockType).test().assertValue(true)

        verify(playlistRepository, times(1)).isFavourited(mockId)
    }

    @Test
    fun testIsInCollection_productTypeIsPlayListOwner_verifyPlayListRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.PLAYLIST_OWNER
        whenever(playlistRepository.isFavourited(mockId)).thenReturn(Single.just(true))

        bottomSheetProductFacade.isInCollection(mockId, mockType).test().assertValue(true)

        verify(playlistRepository, times(1)).isFavourited(mockId)
    }

    @Test
    fun testIsInCollection_productTypeIsPlayListVerifiedOwner_verifyPlayListRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.PLAYLIST_VERIFIED_OWNER
        whenever(playlistRepository.isFavourited(mockId)).thenReturn(Single.just(true))

        bottomSheetProductFacade.isInCollection(mockId, mockType).test().assertValue(true)

        verify(playlistRepository, times(1)).isFavourited(mockId)
    }

    @Test
    fun testIsInCollection_productTypeIsProfile_verifyProfileRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.PROFILE
        whenever(profileRepository.isFollowing(mockId)).thenReturn(Single.just(true))

        bottomSheetProductFacade.isInCollection(mockId, mockType).test().assertValue(true)

        verify(profileRepository, times(1)).isFollowing(mockId)
    }

    @Test
    fun testIsInCollection_productTypeIsSong_verifyTrackRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.SONG
        whenever(trackRepository.isFavourited(mockId)).thenReturn(Single.just(true))

        bottomSheetProductFacade.isInCollection(mockId, mockType).test().assertValue(true)

        verify(trackRepository, times(1)).isFavourited(mockId)
    }

    @Test
    fun testIsInCollection_productTypeIsVideo_verifyTrackRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.VIDEO
        whenever(trackRepository.isFavourited(mockId)).thenReturn(Single.just(true))

        bottomSheetProductFacade.isInCollection(mockId, mockType).test().assertValue(true)

        verify(trackRepository, times(1)).isFavourited(mockId)
    }

    @Test
    fun testIsInCollection_productTypeIsArtistSong_verifyTrackRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.ARTIST_SONG
        whenever(trackRepository.isFavourited(mockId)).thenReturn(Single.just(true))

        bottomSheetProductFacade.isInCollection(mockId, mockType).test().assertValue(true)

        verify(trackRepository, times(1)).isFavourited(mockId)
    }

    @Test
    fun testIsInCollection_productTypeIsAlbumSong_verifyTrackRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.ALBUM_SONG
        whenever(trackRepository.isFavourited(mockId)).thenReturn(Single.just(true))

        bottomSheetProductFacade.isInCollection(mockId, mockType).test().assertValue(true)

        verify(trackRepository, times(1)).isFavourited(mockId)
    }

    @Test
    fun testIsInCollection_productTypeIsPlayListSong_verifyTrackRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.PLAYLIST_SONG
        whenever(trackRepository.isFavourited(mockId)).thenReturn(Single.just(true))

        bottomSheetProductFacade.isInCollection(mockId, mockType).test().assertValue(true)

        verify(trackRepository, times(1)).isFavourited(mockId)
    }

    @Test
    fun testIsInCollection_productTypeIsSongPlayer_verifyTrackRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.SONG_PLAYER
        whenever(trackRepository.isFavourited(mockId)).thenReturn(Single.just(true))

        bottomSheetProductFacade.isInCollection(mockId, mockType).test().assertValue(true)

        verify(trackRepository, times(1)).isFavourited(mockId)
    }

    @Test
    fun testIsInCollection_productTypeIsSongQueue_verifyTrackRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.SONG_QUEUE
        whenever(trackRepository.isFavourited(mockId)).thenReturn(Single.just(true))

        bottomSheetProductFacade.isInCollection(mockId, mockType).test().assertValue(true)

        verify(trackRepository, times(1)).isFavourited(mockId)
    }

    @Test
    fun testIsInCollection_productTypeIsVideoPlayer_verifyTrackRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.VIDEO_PLAYER
        whenever(trackRepository.isFavourited(mockId)).thenReturn(Single.just(true))

        bottomSheetProductFacade.isInCollection(mockId, mockType).test().assertValue(true)

        verify(trackRepository, times(1)).isFavourited(mockId)
    }

    @Test
    fun testIsInCollection_productTypeIsNull_errorIllegalStateException() {
        val mockId = 1
        val mockType = null

        bottomSheetProductFacade.isInCollection(mockId, mockType)
            .test()
            .assertError(IllegalStateException::class.java)
    }

    // ////////////////////////////// getHasArtistAndSimilarStation /////////////////////////////////
    @Test
    fun testGetHasArtistAndSimilarStation_responseListSizeUpper3_returnTrue() {
        val mockId = 1
        whenever(artistRepository.getSimilar(mockId)).thenReturn(
            Single.just(
                listOf(
                    Artist(
                        id = 1,
                        name = "album",
                        image = "image"
                    ),
                    Artist(
                        id = 2,
                        name = "album",
                        image = "image"
                    ),
                    Artist(
                        id = 3,
                        name = "album",
                        image = "image"
                    ),
                    Artist(
                        id = 4,
                        name = "album",
                        image = "image"
                    )
                )
            )
        )

        bottomSheetProductFacade.getHasArtistAndSimilarStation(mockId).test().assertValue(true)

        verify(artistRepository, times(1)).getSimilar(mockId)
    }

    @Test
    fun testGetHasArtistAndSimilarStation_responseListSizeIs3_returnFalse() {
        val mockId = 1
        whenever(artistRepository.getSimilar(mockId)).thenReturn(
            Single.just(
                listOf(
                    Artist(
                        id = 1,
                        name = "album",
                        image = "image"
                    ),
                    Artist(
                        id = 2,
                        name = "album",
                        image = "image"
                    ),
                    Artist(
                        id = 3,
                        name = "album",
                        image = "image"
                    )
                )
            )
        )

        bottomSheetProductFacade.getHasArtistAndSimilarStation(mockId).test().assertValue(false)

        verify(artistRepository, times(1)).getSimilar(mockId)
    }

    @Test
    fun testGetHasArtistAndSimilarStation_responseListSizeLower3_returnFalse() {
        val mockId = 1
        whenever(artistRepository.getSimilar(mockId)).thenReturn(
            Single.just(
                listOf(
                    Artist(
                        id = 1,
                        name = "album",
                        image = "image"
                    )
                )
            )
        )

        bottomSheetProductFacade.getHasArtistAndSimilarStation(mockId).test().assertValue(false)

        verify(artistRepository, times(1)).getSimilar(mockId)
    }

    // //////////////////////////////////// checkPlaylistType ///////////////////////////////////////

    @Test
    fun testCheckPlaylistType_equalIdAndUserIsVerifiedTrue_returnProductTypePlayListVerifiedOwner() {
        val mockId = 1
        whenever(musicUserRepository.get()).thenReturn(
            Single.just(
                User(
                    userId = 1,
                    displayName = "Test",
                    firstName = "Test",
                    lastName = "Test",
                    primaryEmail = "Email",
                    isPrimaryEmailValidated = true,
                    image = "image",
                    backgroundImage = "image",
                    followers = listOf(),
                    following = listOf(),
                    isPublic = true,
                    optedIn = true,
                    blocked = listOf(),
                    language = "th",
                    subscriptions = listOf(),
                    devices = listOf(),
                    isFacebookUser = true,
                    circle = "",
                    birthYear = 1,
                    gender = "",
                    logins = listOf(),
                    action = "action",
                    audioQuality = "audioQuality",
                    contentLanguages = listOf(),
                    country = "",
                    isVerified = true,
                    isTwitterUser = false
                )
            )
        )

        bottomSheetProductFacade.checkPlaylistType(mockId)
            .test()
            .assertValue {
                it == ProductPickerType.PLAYLIST_VERIFIED_OWNER
            }

        verify(musicUserRepository, times(1)).get()
    }

    @Test
    fun testCheckPlaylistType_equalIdAndUserIsVerifiedFalse_returnProductTypePlayListOwner() {
        val mockId = 1
        whenever(musicUserRepository.get()).thenReturn(
            Single.just(
                User(
                    userId = 1,
                    displayName = "Test",
                    firstName = "Test",
                    lastName = "Test",
                    primaryEmail = "Email",
                    isPrimaryEmailValidated = true,
                    image = "image",
                    backgroundImage = "image",
                    followers = listOf(),
                    following = listOf(),
                    isPublic = true,
                    optedIn = true,
                    blocked = listOf(),
                    language = "th",
                    subscriptions = listOf(),
                    devices = listOf(),
                    isFacebookUser = true,
                    circle = "",
                    birthYear = 1,
                    gender = "",
                    logins = listOf(),
                    action = "action",
                    audioQuality = "audioQuality",
                    contentLanguages = listOf(),
                    country = "",
                    isVerified = false,
                    isTwitterUser = false
                )
            )
        )

        bottomSheetProductFacade.checkPlaylistType(mockId)
            .test()
            .assertValue {
                it == ProductPickerType.PLAYLIST_OWNER
            }

        verify(musicUserRepository, times(1)).get()
    }

    @Test
    fun testCheckPlaylistType_notEqualIdAndUserIsVerifiedTrue_returnProductTypePlayList() {
        val mockId = 1
        whenever(musicUserRepository.get()).thenReturn(
            Single.just(
                User(
                    userId = 2,
                    displayName = "Test",
                    firstName = "Test",
                    lastName = "Test",
                    primaryEmail = "Email",
                    isPrimaryEmailValidated = true,
                    image = "image",
                    backgroundImage = "image",
                    followers = listOf(),
                    following = listOf(),
                    isPublic = true,
                    optedIn = true,
                    blocked = listOf(),
                    language = "th",
                    subscriptions = listOf(),
                    devices = listOf(),
                    isFacebookUser = true,
                    circle = "",
                    birthYear = 1,
                    gender = "",
                    logins = listOf(),
                    action = "action",
                    audioQuality = "audioQuality",
                    contentLanguages = listOf(),
                    country = "",
                    isVerified = true,
                    isTwitterUser = false
                )
            )
        )

        bottomSheetProductFacade.checkPlaylistType(mockId)
            .test()
            .assertValue {
                it == ProductPickerType.PLAYLIST
            }

        verify(musicUserRepository, times(1)).get()
    }

    @Test
    fun testCheckPlaylistType_notEqualIdAndUserIsVerifiedFalse_returnProductTypePlayList() {
        val mockId = 1
        whenever(musicUserRepository.get()).thenReturn(
            Single.just(
                User(
                    userId = 2,
                    displayName = "Test",
                    firstName = "Test",
                    lastName = "Test",
                    primaryEmail = "Email",
                    isPrimaryEmailValidated = true,
                    image = "image",
                    backgroundImage = "image",
                    followers = listOf(),
                    following = listOf(),
                    isPublic = true,
                    optedIn = true,
                    blocked = listOf(),
                    language = "th",
                    subscriptions = listOf(),
                    devices = listOf(),
                    isFacebookUser = true,
                    circle = "",
                    birthYear = 1,
                    gender = "",
                    logins = listOf(),
                    action = "action",
                    audioQuality = "audioQuality",
                    contentLanguages = listOf(),
                    country = "",
                    isVerified = false,
                    isTwitterUser = false
                )
            )
        )

        bottomSheetProductFacade.checkPlaylistType(mockId)
            .test()
            .assertValue {
                it == ProductPickerType.PLAYLIST
            }

        verify(musicUserRepository, times(1)).get()
    }

    // /////////////////////////////////// getTracksForProduct //////////////////////////////////////
    @Test
    fun testGetTracksForProduct_productTypeIsAlbumAndTrackLocationIsEmpty_returnSortByTrackNumberAndIsCachedTrue() {
        val mockId = 1
        val mockType = ProductPickerType.ALBUM
        whenever(albumRepository.getTracks(any(), any()))
            .thenReturn(
                Single.just(
                    listOf(
                        mockTrack(2),
                        mockTrack(3),
                        mockTrack(1)
                    )
                )
            )
        whenever(cacheRepository.getTrackLocationIfExist(any())).thenReturn("")

        bottomSheetProductFacade.getTracksForProduct(mockId, mockType)
            .test()
            .assertValue {
                (
                    it[0].trackNumber < it[1].trackNumber &&
                        it[1].trackNumber < it[2].trackNumber
                    ) && it[0].isCached
            }
    }

    @Test
    fun testGetTracksForProduct_productTypeIsAlbumAndTrackLocationIsNull_returnSortByTrackNumberAndIsCachedFalse() {
        val mockId = 1
        val mockType = ProductPickerType.ALBUM
        whenever(albumRepository.getTracks(any(), any()))
            .thenReturn(
                Single.just(
                    listOf(
                        mockTrack(2),
                        mockTrack(3),
                        mockTrack(1)
                    )
                )
            )
        whenever(cacheRepository.getTrackLocationIfExist(any())).thenReturn(null)

        bottomSheetProductFacade.getTracksForProduct(mockId, mockType)
            .test()
            .assertValue {
                (
                    it[0].trackNumber < it[1].trackNumber &&
                        it[1].trackNumber < it[2].trackNumber
                    ) && it[0].isCached.not()
            }
    }

    @Test
    fun testGetTracksForProduct_productTypeIsAlbumAndTrackLocationIsNotEmpty_returnIsCachedTrue() {
        val mockId = 1
        val mockType = ProductPickerType.ALBUM
        whenever(albumRepository.getTracks(any(), any()))
            .thenReturn(
                Single.just(
                    listOf(
                        mockTrack(2),
                        mockTrack(3),
                        mockTrack(1)
                    )
                )
            )
        whenever(cacheRepository.getTrackLocationIfExist(any())).thenReturn("location")

        bottomSheetProductFacade.getTracksForProduct(mockId, mockType)
            .test()
            .assertValue {
                (
                    it[0].trackNumber < it[1].trackNumber &&
                        it[1].trackNumber < it[2].trackNumber
                    ) && it[0].isCached
            }
    }

    @Test
    fun testGetTracksForProduct_productTypeIsMixAndTrackLocationIsNotEmpty_returnIsCachedTrue() {
        val mockId = 1
        val mockType = ProductPickerType.MIX
        whenever(stationRepository.getSyncTracks(any()))
            .thenReturn(
                Single.just(
                    listOf(
                        mockTrack(2),
                        mockTrack(3),
                        mockTrack(1)
                    )
                )
            )
        whenever(cacheRepository.getTrackLocationIfExist(any())).thenReturn("location")

        bottomSheetProductFacade.getTracksForProduct(mockId, mockType)
            .test()
            .assertValue {
                it[0].trackNumber == 2 &&
                    it[1].trackNumber == 3 &&
                    it[2].trackNumber == 1 &&
                    it[0].isCached
            }
    }

    @Test
    fun testGetTracksForProduct_productTypeIsPlayListAndTrackLocationIsNotEmpty_returnIsCachedTrue() {
        val mockId = 1
        val mockType = ProductPickerType.PLAYLIST
        whenever(playlistRepository.getTracks(any(), any(), any()))
            .thenReturn(
                Single.just(
                    listOf(
                        mockTrack(2),
                        mockTrack(3),
                        mockTrack(1)
                    )
                )
            )
        whenever(cacheRepository.getTrackLocationIfExist(any())).thenReturn("location")

        bottomSheetProductFacade.getTracksForProduct(mockId, mockType)
            .test()
            .assertValue {
                it[0].trackNumber == 2 &&
                    it[1].trackNumber == 3 &&
                    it[2].trackNumber == 1 &&
                    it[0].isCached
            }
    }

    @Test
    fun testGetTracksForProduct_productTypeIsPlayListOwnerAndTrackLocationIsNotEmpty_returnIsCachedTrue() {
        val mockId = 1
        val mockType = ProductPickerType.PLAYLIST_OWNER
        whenever(playlistRepository.getTracks(any(), any(), any()))
            .thenReturn(
                Single.just(
                    listOf(
                        mockTrack(2),
                        mockTrack(3),
                        mockTrack(1)
                    )
                )
            )
        whenever(cacheRepository.getTrackLocationIfExist(any())).thenReturn("location")

        bottomSheetProductFacade.getTracksForProduct(mockId, mockType)
            .test()
            .assertValue {
                it[0].trackNumber == 2 &&
                    it[1].trackNumber == 3 &&
                    it[2].trackNumber == 1 &&
                    it[0].isCached
            }
    }

    @Test
    fun testGetTracksForProduct_productTypeIsPlayListVerifiedOwnerAndTrackLocationIsNotEmpty_returnIsCachedTrue() {
        val mockId = 1
        val mockType = ProductPickerType.PLAYLIST_VERIFIED_OWNER
        whenever(playlistRepository.getTracks(any(), any(), any()))
            .thenReturn(
                Single.just(
                    listOf(
                        mockTrack(2),
                        mockTrack(3),
                        mockTrack(1)
                    )
                )
            )
        whenever(cacheRepository.getTrackLocationIfExist(any())).thenReturn("location")

        bottomSheetProductFacade.getTracksForProduct(mockId, mockType)
            .test()
            .assertValue {
                it[0].trackNumber == 2 &&
                    it[1].trackNumber == 3 &&
                    it[2].trackNumber == 1 &&
                    it[0].isCached
            }
    }

    @Test
    fun testGetTracksForProduct_productTypeIsNull_returnError() {
        val mockId = 1

        bottomSheetProductFacade.getTracksForProduct(mockId, null)
            .test()
            .assertError(IllegalStateException::class.java)
    }

    // /////////////////////////////////////// clearVotes ///////////////////////////////////////////
    @Test
    fun testClearVotes_productTypeIsMix_verifyStationRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.MIX
        whenever(stationRepository.deleteVotes(any())).thenReturn(Single.just(Any()))

        bottomSheetProductFacade.clearVotes(mockId, mockType, "")
            .test()
            .assertNoErrors()

        verify(stationRepository, times(1)).deleteVotes(any())
    }

    @Test
    fun testClearVotes_productTypeIsArtist_verifyArtistRepositoryRequest() {
        val mockId = 1
        val mockType = ProductPickerType.ARTIST
        whenever(artistRepository.clearArtistVotes(any(), any())).thenReturn(Single.just(Any()))

        bottomSheetProductFacade.clearVotes(mockId, mockType, "")
            .test()
            .assertNoErrors()

        verify(artistRepository, times(1)).clearArtistVotes(any(), any())
    }

    @Test
    fun testClearVotes_productTypeIsNull_returnError() {
        val mockId = 1
        whenever(artistRepository.clearArtistVotes(any(), any())).thenReturn(Single.just(Any()))

        bottomSheetProductFacade.clearVotes(mockId, null, "")
            .test()
            .assertError(IllegalStateException::class.java)
    }
}
