package com.truedigital.features.tuned.domain.facade

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.atLeastOnce
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.data.ad.model.AdProvider
import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.album.model.Release
import com.truedigital.features.tuned.data.album.repository.AlbumRepository
import com.truedigital.features.tuned.data.authentication.repository.AuthenticationTokenRepository
import com.truedigital.features.tuned.data.cache.repository.CacheRepository
import com.truedigital.features.tuned.data.device.repository.DeviceRepository
import com.truedigital.features.tuned.data.setting.repository.SettingRepository
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.track.repository.TrackRepository
import com.truedigital.features.tuned.data.user.model.Settings
import com.truedigital.features.tuned.data.user.repository.MusicUserRepository
import com.truedigital.features.tuned.domain.facade.album.AlbumFacade
import com.truedigital.features.tuned.domain.facade.album.AlbumFacadeImpl
import io.reactivex.Single
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class AlbumFacadeImplTest {

    private lateinit var albumFacade: AlbumFacade
    private val albumRepository: AlbumRepository = mock()
    private val trackRepository: TrackRepository = mock()
    private val musicUserRepository: MusicUserRepository = mock()
    private val deviceRepository: DeviceRepository = mock()
    private val settingRepository: SettingRepository = mock()
    private val cacheRepository: CacheRepository = mock()
    private val authenticationTokenRepository: AuthenticationTokenRepository = mock()

    private val mockRelease = Release(
        id = 1,
        albumId = 1,
        artists = listOf(),
        name = "name",
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

    private val mockAlbum = Album(
        id = 1,
        name = "2",
        artists = listOf(),
        primaryRelease = mockRelease,
        releaseIds = listOf()
    )

    private val mockSetting = Settings(
        allowStreams = false,
        limitSkips = true,
        adFirstMinutes = 1,
        adIntervalMinutes = 1,
        allowPurchase = true,
        allowAlbumNavigation = true,
        allowOffline = true,
        allowSync = true,
        syncCutOffDays = 1,
        maxSkipsPerHour = 1,
        limitPlays = true,
        monthlyPlayLimit = 1,
        adProvider = AdProvider.NONE,
        tracksPerAd = 1,
        interstitialId = "1",
        facebookUrl = "",
        twitterUrl = "",
        instagramUrl = "",
        youtubeUrl = "",
        supportEmail = "",
        dmcaEnabled = true,
        offlineMaximumDuration = 11L
    )

    @BeforeEach
    fun setup() {
        albumFacade = AlbumFacadeImpl(
            albumRepository,
            trackRepository,
            musicUserRepository,
            deviceRepository,
            settingRepository,
            cacheRepository,
            authenticationTokenRepository
        )
    }

    @Test
    fun loadAlbum_check_1_time_call() {
        whenever(albumRepository.get(1, false)).thenReturn(
            Single.just(
                Album(
                    id = 12333,
                    name = "album",
                    artists = listOf(),
                    primaryRelease = null,
                    releaseIds = listOf()
                )
            )
        )

        albumFacade.loadAlbum(1)

        verify(albumRepository, times(1)).get(1, false)
    }

    @Test
    fun loadMoreFromArtist_check_1_time_call() {
        val album = Album(
            id = 12333,
            name = "album",
            artists = listOf(),
            primaryRelease = null,
            releaseIds = listOf()
        )
        whenever(albumRepository.getMoreFromArtist(any())).thenReturn(
            Single.just(
                listOf(album)
            )
        )

        albumFacade.loadMoreFromArtist(1)

        verify(albumRepository, times(1)).getMoreFromArtist(any())
    }

    @Test
    fun loadTrack_check_1_time_call() {
        whenever(trackRepository.get(1)).thenReturn(Single.just(getTrack(1)))

        albumFacade.loadTrack(1)

        verify(trackRepository, times(1)).get(1)
    }

    @Test
    fun loadTracks_primaryReleaseIsNotNull_returnTrackListSorted() {
        val trackList = getTrackList()
        whenever(albumRepository.getTracks(any(), any())).thenReturn(
            Single.just(trackList)
        )
        whenever(cacheRepository.getTrackLocationIfExist(any())).thenReturn("data")

        albumFacade.loadTracks(mockAlbum, listOf())
            .test()
            .assertValue {
                it[0].isCached &&
                    it[0].trackNumber == 135 &&
                    it[1].trackNumber == 452 &&
                    it[2].trackNumber == 894 &&
                    it[3].trackNumber == 5642
            }
    }

    @Test
    fun loadTracks_primaryReleaseIsNull_returnTrackListSorted() {
        val mockAlbumValue = mockAlbum.copy(primaryRelease = null)
        val trackList = getTrackList()

        whenever(albumRepository.getTracks(any(), any())).thenReturn(
            Single.just(trackList)
        )
        whenever(cacheRepository.getTrackLocationIfExist(any())).thenReturn(null)

        albumFacade.loadTracks(mockAlbumValue, listOf())
            .test()
            .assertValue {
                !it[0].isCached &&
                    it[0].trackNumber == 135 &&
                    it[1].trackNumber == 452 &&
                    it[2].trackNumber == 894 &&
                    it[3].trackNumber == 5642
            }
    }

    @Test
    fun loadFavourite_check_1_time_call() {
        whenever(albumRepository.isFavourited(1)).thenReturn(Single.just(true))

        albumFacade.loadFavourited(1)

        verify(albumRepository, times(1)).isFavourited(1)
    }

    @Test
    fun toggleFavourite_primaryReleaseIsNotNull_favoriteIsTrue_favoriteIsRemoved() {
        whenever(albumRepository.isFavourited(any())).thenReturn(Single.just(true))
        whenever(albumRepository.removeFavourite(any())).thenReturn(Single.just(Any()))

        albumFacade.toggleFavourite(mockAlbum)
            .test()
            .assertNoErrors()

        verify(albumRepository, times(1)).isFavourited(any())
        verify(albumRepository, times(1)).removeFavourite(any())
    }

    @Test
    fun toggleFavourite_primaryReleaseIsNull_favoriteIsTrue_favoriteIsRemoved() {
        val mockAlbumValue = mockAlbum.copy(primaryRelease = null)

        whenever(albumRepository.isFavourited(any())).thenReturn(Single.just(true))
        whenever(albumRepository.removeFavourite(any())).thenReturn(Single.just(Any()))

        albumFacade.toggleFavourite(mockAlbumValue)
            .test()
            .assertNoErrors()

        verify(albumRepository, times(1)).isFavourited(any())
        verify(albumRepository, times(1)).removeFavourite(any())
    }

    @Test
    fun toggleFavourite_primaryReleaseIsNotNull_favoriteIsFalse_favoriteIsAdded() {
        whenever(albumRepository.isFavourited(any())).thenReturn(Single.just(false))
        whenever(albumRepository.addFavourite(any())).thenReturn(Single.just(Any()))

        albumFacade.toggleFavourite(mockAlbum)
            .test()
            .assertNoErrors()

        verify(albumRepository, times(1)).isFavourited(any())
        verify(albumRepository, times(1)).addFavourite(any())
    }

    @Test
    fun toggleFavourite_primaryReleaseIsNull_favoriteIsFalse_favoriteIsAdded() {
        val mockAlbumValue = mockAlbum.copy(primaryRelease = null)

        whenever(albumRepository.isFavourited(any())).thenReturn(Single.just(false))
        whenever(albumRepository.addFavourite(any())).thenReturn(Single.just(Any()))

        albumFacade.toggleFavourite(mockAlbumValue)
            .test()
            .assertNoErrors()

        verify(albumRepository, times(1)).isFavourited(any())
        verify(albumRepository, times(1)).addFavourite(any())
    }

    @Test
    fun getAlbumNavigationAllowed_settingIsNotNull_returnAllowAlbumNavigationValue() {
        whenever(musicUserRepository.getSettings()).thenReturn(mockSetting)

        val result = albumFacade.getAlbumNavigationAllowed()
        assertTrue(result)
    }

    @Test
    fun getAlbumNavigationAllowed_settingIsNull_returnFalseValue() {
        whenever(musicUserRepository.getSettings()).thenReturn(null)

        val result = albumFacade.getAlbumNavigationAllowed()
        assertFalse(result)
    }

    @Test
    fun getAllowMobileData_check_allowMobileDataStreaming_is_true_and_isWifiConnected_is_true() {
        whenever(settingRepository.allowMobileDataStreaming()).thenReturn(true)
        whenever(deviceRepository.isWifiConnected()).thenReturn(true)

        assert(albumFacade.getAllowMobileData())
    }

    @Test
    fun getAllowMobileData_check_allowMobileDataStreaming_is_false_and_isWifiConnected_is_true() {
        whenever(settingRepository.allowMobileDataStreaming()).thenReturn(false)
        whenever(deviceRepository.isWifiConnected()).thenReturn(true)

        assert(albumFacade.getAllowMobileData())
    }

    @Test
    fun getAllowMobileData_check_allowMobileDataStreaming_is_true_and_isWifiConnected_is_false() {
        whenever(settingRepository.allowMobileDataStreaming()).thenReturn(true)
        whenever(deviceRepository.isWifiConnected()).thenReturn(false)

        assert(albumFacade.getAllowMobileData())
    }

    @Test
    fun getAllowMobileData_check_allowMobileDataStreaming_is_false_and_isWifiConnected_is_false() {
        whenever(settingRepository.allowMobileDataStreaming()).thenReturn(false)
        whenever(deviceRepository.isWifiConnected()).thenReturn(false)

        assert(!albumFacade.getAllowMobileData())
    }

    @Test
    fun getIsEmulator_check_isEmulator_is_true() {
        whenever(deviceRepository.isEmulator()).thenReturn(true)

        assert(albumFacade.getIsEmulator())
        verify(deviceRepository, atLeastOnce()).isEmulator()
    }

    @Test
    fun getIsEmulator_check_isEmulator_is_false() {
        whenever(deviceRepository.isEmulator()).thenReturn(false)

        assert(!albumFacade.getIsEmulator())
        verify(deviceRepository, atLeastOnce()).isEmulator()
    }

    @Test
    fun getHasAlbumShuffleRight_currentTokenIsNull_returnFalseValue() {
        whenever(authenticationTokenRepository.getCurrentToken()).thenReturn(null)

        val result = albumFacade.getHasAlbumShuffleRight()
        assertFalse(result)
    }

    private fun getTrackList(): List<Track> {
        val trackList = mutableListOf<Track>()
        trackList.add(getTrack(452))
        trackList.add(getTrack(5642))
        trackList.add(getTrack(135))
        trackList.add(getTrack(894))
        return trackList.toList()
    }

    private fun getTrack(trackNumber: Int): Track {
        return Track(
            id = 111,
            playlistTrackId = 1,
            songId = 1,
            releaseId = 1,
            artists = listOf(),
            name = "name",
            originalCredit = "",
            isExplicit = true,
            trackNumber = trackNumber,
            trackNumberInVolume = 1,
            volumeNumber = 1,
            releaseArtists = listOf(),
            sample = "",
            isOnCompilation = true,
            releaseName = "",
            allowDownload = true,
            allowStream = true,
            duration = 1111111,
            image = "image",
            hasLyrics = true,
            video = null,
            isVideo = true,
            vote = null,
            isDownloaded = true,
            syncProgress = 2.2f,
            isCached = true
        )
    }
}
