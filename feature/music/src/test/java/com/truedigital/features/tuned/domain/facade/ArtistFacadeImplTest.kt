package com.truedigital.features.tuned.domain.facade

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.artist.repository.ArtistRepository
import com.truedigital.features.tuned.data.authentication.model.AuthenticationToken
import com.truedigital.features.tuned.data.authentication.repository.AuthenticationTokenRepository
import com.truedigital.features.tuned.data.cache.repository.CacheRepository
import com.truedigital.features.tuned.data.device.repository.DeviceRepository
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.station.repository.StationRepository
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.user.repository.MusicUserRepository
import com.truedigital.features.tuned.domain.facade.artist.ArtistFacade
import com.truedigital.features.tuned.domain.facade.artist.ArtistFacadeImpl
import com.truedigital.features.utils.MockDataModel
import io.reactivex.Single
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ArtistFacadeImplTest {

    private lateinit var artistFacade: ArtistFacade
    private val artistRepository: ArtistRepository = mock()
    private val stationRepository: StationRepository = mock()
    private val cacheRepository: CacheRepository = mock()
    private val musicUserRepository: MusicUserRepository = mock()
    private val deviceRepository: DeviceRepository = mock()
    private val authenticationTokenRepository: AuthenticationTokenRepository = mock()

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

    @BeforeEach
    fun setup() {
        artistFacade = ArtistFacadeImpl(
            artistRepository,
            stationRepository,
            cacheRepository,
            musicUserRepository,
            deviceRepository,
            authenticationTokenRepository
        )
    }

    @Test
    fun testLoadArtist_verifyOneTimeRequest() {
        val mockId = 1
        whenever(artistRepository.get(mockId, false))
            .thenReturn(
                Single.just(
                    Artist(
                        id = 1,
                        name = "name",
                        image = "image"
                    )
                )
            )

        artistFacade.loadArtist(mockId)
            .test()
            .assertNoErrors()

        verify(artistRepository, times(1)).get(mockId, false)
        verify(artistRepository, times(0)).get(2, false)
    }

    @Test
    fun testLoadArtistPlayCount_verifyOneTimeRequest() {
        val mockId = 1
        whenever(artistRepository.getPlayCount(mockId)).thenReturn(Single.just(2))

        artistFacade.loadArtistPlayCount(mockId)
            .test()
            .assertNoErrors()

        verify(artistRepository, times(1)).getPlayCount(mockId)
        verify(artistRepository, times(0)).getPlayCount(2)
    }

    @Test
    fun testLoadArtistAlbums_verifyOneTimeRequest() {
        val mockId = 1
        val mockOffset = 1
        val mockCount = 5
        val mockSortType = "sortType"
        whenever(
            artistRepository.getAlbums(
                artistId = mockId, sortType = mockSortType,
                offset = mockOffset, count = mockCount
            )
        )
            .thenReturn(
                Single.just(
                    listOf(
                        Album(
                            id = 1,
                            name = "album",
                            artists = listOf(),
                            primaryRelease = null,
                            releaseIds = listOf()
                        )
                    )
                )
            )

        artistFacade.loadArtistAlbums(mockId, mockOffset, mockCount, mockSortType)
            .test()
            .assertNoErrors()

        verify(artistRepository, times(1)).getAlbums(mockId, mockSortType, mockOffset, mockCount)
    }

    @Test
    fun testLoadArtistAlbums_sortTypeNull_verifyOneTimeRequest() {
        val mockId = 1
        val mockOffset = 1
        val mockCount = 5
        val mockSortType = null
        whenever(
            artistRepository.getAlbums(
                artistId = mockId, sortType = mockSortType,
                offset = mockOffset, count = mockCount
            )
        )
            .thenReturn(
                Single.just(
                    listOf(
                        Album(
                            id = 1,
                            name = "album",
                            artists = listOf(),
                            primaryRelease = null,
                            releaseIds = listOf()
                        )
                    )
                )
            )

        artistFacade.loadArtistAlbums(mockId, mockOffset, mockCount)
            .test()
            .assertNoErrors()

        verify(artistRepository, times(1)).getAlbums(mockId, mockSortType, mockOffset, mockCount)
    }

    @Test
    fun testLoadPopularSongs_getTrackLocationIfExistIsNull_isCachedIsFalse() {
        val mockId = 1
        whenever(artistRepository.getTracks(any(), any(), any(), any(), anyOrNull(), anyOrNull()))
            .thenReturn(Single.just(listOf(mockTrack)))
        whenever(cacheRepository.getTrackLocationIfExist(any())).thenReturn(null)

        artistFacade.loadPopularSongs(mockId).test().assertValue {
            it.first().isCached.not()
        }

        verify(artistRepository, times(1)).getTracks(
            any(),
            any(),
            any(),
            any(),
            anyOrNull(),
            anyOrNull()
        )
        verify(cacheRepository, times(1)).getTrackLocationIfExist(any())
    }

    @Test
    fun testLoadPopularSongs_getTrackLocationIfExistIsEmptyString_isCachedIsTrue() {
        val mockId = 1
        whenever(artistRepository.getTracks(any(), any(), any(), any(), anyOrNull(), anyOrNull()))
            .thenReturn(Single.just(listOf(mockTrack)))
        whenever(cacheRepository.getTrackLocationIfExist(any())).thenReturn("")

        artistFacade.loadPopularSongs(mockId).test().assertValue {
            it.first().isCached
        }

        verify(artistRepository, times(1)).getTracks(
            any(),
            any(),
            any(),
            any(),
            anyOrNull(),
            anyOrNull()
        )
        verify(cacheRepository, times(1)).getTrackLocationIfExist(any())
    }

    @Test
    fun testLoadPopularSongs_getTrackLocationIfExistIsNotEmpty_isCachedIsTrue() {
        val mockId = 1
        whenever(artistRepository.getTracks(any(), any(), any(), any(), anyOrNull(), anyOrNull()))
            .thenReturn(Single.just(listOf(mockTrack)))
        whenever(cacheRepository.getTrackLocationIfExist(any())).thenReturn("location")

        artistFacade.loadPopularSongs(mockId).test().assertValue {
            it.first().isCached
        }

        verify(artistRepository, times(1)).getTracks(
            any(),
            any(),
            any(),
            any(),
            anyOrNull(),
            anyOrNull()
        )
        verify(cacheRepository, times(1)).getTrackLocationIfExist(any())
    }

    @Test
    fun testLoadLatestSongs_getTrackLocationIfExistIsNull_isCachedIsFalse() {
        val mockId = 1
        whenever(artistRepository.getTracks(any(), any(), any(), any(), anyOrNull(), anyOrNull()))
            .thenReturn(Single.just(listOf(mockTrack)))
        whenever(cacheRepository.getTrackLocationIfExist(any())).thenReturn(null)

        artistFacade.loadLatestSongs(mockId).test().assertValue {
            it.first().isCached.not()
        }

        verify(artistRepository, times(1)).getTracks(
            any(),
            any(),
            any(),
            any(),
            anyOrNull(),
            anyOrNull()
        )
        verify(cacheRepository, times(1)).getTrackLocationIfExist(any())
    }

    @Test
    fun testLoadLatestSongs_getTrackLocationIfExistIsEmptyString_isCachedIsTrue() {
        val mockId = 1
        whenever(artistRepository.getTracks(any(), any(), any(), any(), anyOrNull(), anyOrNull()))
            .thenReturn(Single.just(listOf(mockTrack)))
        whenever(cacheRepository.getTrackLocationIfExist(any())).thenReturn("")

        artistFacade.loadLatestSongs(mockId).test().assertValue {
            it.first().isCached
        }

        verify(artistRepository, times(1)).getTracks(
            any(),
            any(),
            any(),
            any(),
            anyOrNull(),
            anyOrNull()
        )
        verify(cacheRepository, times(1)).getTrackLocationIfExist(any())
    }

    @Test
    fun testLoadLatestSongs_getTrackLocationIfExistIsNotEmpty_isCachedIsTrue() {
        val mockId = 1
        whenever(artistRepository.getTracks(any(), any(), any(), any(), anyOrNull(), anyOrNull()))
            .thenReturn(Single.just(listOf(mockTrack)))
        whenever(cacheRepository.getTrackLocationIfExist(any())).thenReturn("location")

        artistFacade.loadLatestSongs(mockId).test().assertValue {
            it.first().isCached
        }

        verify(artistRepository, times(1)).getTracks(
            any(),
            any(),
            any(),
            any(),
            anyOrNull(),
            anyOrNull()
        )
        verify(cacheRepository, times(1)).getTrackLocationIfExist(any())
    }

    @Test
    fun testLoadArtistAppearsOn_verifyOneTimeRequest() {
        val mockId = 1
        val mockSortType = "sortType"
        whenever(artistRepository.getAppearsOn(mockId, mockSortType))
            .thenReturn(
                Single.just(
                    listOf(
                        Album(
                            id = 12333,
                            name = "album",
                            artists = listOf(),
                            primaryRelease = null,
                            releaseIds = listOf()
                        )
                    )
                )
            )

        artistFacade.loadArtistAppearsOn(mockId, mockSortType)
            .test()
            .assertNoErrors()

        verify(artistRepository, times(1)).getAppearsOn(mockId, mockSortType)
    }

    @Test
    fun testLoadArtistAppearsOn_sortTypeNull_verifyOneTimeRequest() {
        val mockId = 1
        val mockSortType = null
        whenever(artistRepository.getAppearsOn(mockId, mockSortType))
            .thenReturn(
                Single.just(
                    listOf(
                        Album(
                            id = 12333,
                            name = "album",
                            artists = listOf(),
                            primaryRelease = null,
                            releaseIds = listOf()
                        )
                    )
                )
            )

        artistFacade.loadArtistAppearsOn(mockId)
            .test()
            .assertNoErrors()

        verify(artistRepository, times(1)).getAppearsOn(mockId, mockSortType)
    }

    @Test
    fun testLoadStationsAppearsIn_verifyOneTimeRequest() {
        val mockId = 1
        whenever(stationRepository.getContainingArtist(mockId))
            .thenReturn(
                Single.just(
                    listOf(
                        Station(
                            id = 1,
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
            )

        artistFacade.loadStationsAppearsIn(mockId)
            .test()
            .assertNoErrors()

        verify(stationRepository, times(1)).getContainingArtist(mockId)
    }

    @Test
    fun testLoadSimilarArtists_verifyOneTimeRequest() {
        val mockId = 1
        whenever(artistRepository.getSimilar(mockId))
            .thenReturn(
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

        artistFacade.loadSimilarArtists(mockId)
            .test()
            .assertNoErrors()

        verify(artistRepository, times(1)).getSimilar(mockId)
    }

    @Test
    fun testLoadArtistAndSimilarStation_verifyOneTimeRequest() {
        val mockId = 1
        whenever(stationRepository.getByArtist(mockId))
            .thenReturn(
                Single.just(
                    Station(
                        id = 1,
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

        artistFacade.loadArtistAndSimilarStation(mockId)
            .test()
            .assertNoErrors()

        verify(stationRepository, times(1)).getByArtist(mockId)
    }

    @Test
    fun testLoadFollowed_verifyOneTimeRequest() {
        val mockId = 1
        whenever(artistRepository.isFollowing(mockId)).thenReturn(Single.just(true))

        artistFacade.loadFollowed(mockId)
            .test()
            .assertNoErrors()

        verify(artistRepository, times(1)).isFollowing(mockId)
    }

    @Test
    fun testToggleFavourite_isFollowedIsTrue_verifyRemoveFollow() {
        val mockId = 1
        whenever(artistRepository.isFollowing(mockId)).thenReturn(Single.just(true))
        whenever(artistRepository.removeFollow(mockId)).thenReturn(Single.just(Any()))
        whenever(artistRepository.addFollow(mockId)).thenReturn(Single.just(Any()))

        artistFacade.toggleFavourite(mockId)
            .test()
            .assertNoErrors()

        verify(artistRepository, times(1)).removeFollow(mockId)
        verify(artistRepository, times(0)).addFollow(mockId)
    }

    @Test
    fun testToggleFavourite_isFollowedIsFalse_verifyAddFollow() {
        val mockId = 1
        whenever(artistRepository.isFollowing(mockId)).thenReturn(Single.just(false))
        whenever(artistRepository.removeFollow(mockId)).thenReturn(Single.just(Any()))
        whenever(artistRepository.addFollow(mockId)).thenReturn(Single.just(Any()))

        artistFacade.toggleFavourite(mockId)
            .test()
            .assertNoErrors()

        verify(artistRepository, times(0)).removeFollow(mockId)
        verify(artistRepository, times(1)).addFollow(mockId)
    }

    @Test
    fun testGetHasArtistShuffleRight_getCurrentTokenIsNotNull_accessTokenIsNull_returnHasArtistShuffleRight() {
        val mock =
            AuthenticationToken(refreshToken = "refreshToken", expiration = 1L, accessToken = null)
        whenever(authenticationTokenRepository.getCurrentToken()).thenReturn(mock)

        assertFalse(artistFacade.getHasArtistShuffleRight())
    }

    @Test
    fun testGetHasArtistShuffleRight_getCurrentTokenIsNull_returnFalse() {
        whenever(authenticationTokenRepository.getCurrentToken()).thenReturn(null)

        assertFalse(artistFacade.getHasArtistShuffleRight())
    }

    @Test
    fun testClearArtistVotes_verifyOneTimeRequest() {
        val mockId = 1
        val mockType = "type"
        whenever(artistRepository.clearArtistVotes(mockId, mockType)).thenReturn(Single.just(Any()))

        artistFacade.clearArtistVotes(mockId, mockType)
            .test()
            .assertNoErrors()

        verify(artistRepository, times(1)).clearArtistVotes(mockId, mockType)
    }

    @Test
    fun testGetAlbumNavigationAllowed_getSettingsIsNull_returnFalse() {
        whenever(musicUserRepository.getSettings()).thenReturn(null)

        assertFalse(artistFacade.getAlbumNavigationAllowed())
    }

    @Test
    fun testGetAlbumNavigationAllowed_getSettingsIsNotNull_allowAlbumNavigationIsTrue_returnTrue() {
        whenever(musicUserRepository.getSettings()).thenReturn(MockDataModel.mockSetting)

        assertTrue(artistFacade.getAlbumNavigationAllowed())
    }

    @Test
    fun testGetAlbumNavigationAllowed_getSettingsIsNotNull_allowAlbumNavigationIsFalse_returnFalse() {
        whenever(musicUserRepository.getSettings()).thenReturn(
            MockDataModel.mockSetting.copy(
                allowAlbumNavigation = false
            )
        )

        assertFalse(artistFacade.getAlbumNavigationAllowed())
    }

    @Test
    fun testGetVideoAppearsIn_verifyOneTimeRequest() {
        val mockId = 1
        val mockOffset = 1
        val mockCount = 5
        val mockSortType = "sortType"
        whenever(artistRepository.getTracks(any(), any(), any(), any(), anyOrNull(), anyOrNull()))
            .thenReturn(Single.just(listOf(mockTrack)))

        artistFacade.getVideoAppearsIn(mockId, mockOffset, mockCount, mockSortType)
            .test()
            .assertNoErrors()

        verify(artistRepository, times(1)).getTracks(
            any(),
            any(),
            any(),
            any(),
            anyOrNull(),
            anyOrNull()
        )
    }

    @Test
    fun testGetVideoAppearsIn_sortTypeNull_verifyOneTimeRequest() {
        val mockId = 1
        val mockOffset = 1
        val mockCount = 5
        whenever(artistRepository.getTracks(any(), any(), any(), any(), anyOrNull(), anyOrNull()))
            .thenReturn(Single.just(listOf(mockTrack)))

        artistFacade.getVideoAppearsIn(mockId, mockOffset, mockCount)
            .test()
            .assertNoErrors()

        verify(artistRepository, times(1)).getTracks(
            any(),
            any(),
            any(),
            any(),
            anyOrNull(),
            anyOrNull()
        )
    }

    @Test
    fun testIsArtistHintShown_returnIsArtistHintShownTrue() {
        whenever(deviceRepository.isArtistHintShown()).thenReturn(true)

        assertTrue(artistFacade.isArtistHintShown())
    }

    @Test
    fun testIsArtistHintShown_returnIsArtistHintShownFalse() {
        whenever(deviceRepository.isArtistHintShown()).thenReturn(false)

        assertFalse(artistFacade.isArtistHintShown())
    }

    @Test
    fun testSetArtistHintShown_verifyOneTimeRequest() {
        artistFacade.setArtistHintShown()

        verify(deviceRepository, times(1)).setArtistHintStatus(true)
    }

    @Test
    fun testGetIsDMCAEnabled_getSettingsIsNotNull_dmcaEnabledIsFalse_returnFalse() {
        whenever(musicUserRepository.getSettings()).thenReturn(
            MockDataModel.mockSetting.copy(
                allowAlbumNavigation = false
            )
        )

        assertFalse(artistFacade.getIsDMCAEnabled())
    }

    @Test
    fun testGetIsDMCAEnabled_getSettingsIsNotNull_dmcaEnabledIsTrue_returnTrue() {
        whenever(musicUserRepository.getSettings()).thenReturn(
            MockDataModel.mockSetting.copy(
                dmcaEnabled = true
            )
        )

        assertTrue(artistFacade.getIsDMCAEnabled())
    }

    @Test
    fun testGetIsDMCAEnabled_getSettingsIsNull_returnFalse() {
        whenever(musicUserRepository.getSettings()).thenReturn(null)

        assertFalse(artistFacade.getIsDMCAEnabled())
    }
}
