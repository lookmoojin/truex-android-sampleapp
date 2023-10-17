package com.truedigital.features.tuned.domain.facade

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.common.share.analytics.measurement.AnalyticManager
import com.truedigital.features.tuned.data.ad.model.Ad
import com.truedigital.features.tuned.data.ad.model.AdProvider
import com.truedigital.features.tuned.data.ad.repository.AdRepository
import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.authentication.repository.AuthenticationTokenRepository
import com.truedigital.features.tuned.data.cache.repository.CacheRepository
import com.truedigital.features.tuned.data.database.entity.SkipEntity
import com.truedigital.features.tuned.data.database.entity.TrackHistoryEntity
import com.truedigital.features.tuned.data.database.repository.MusicRoomRepository
import com.truedigital.features.tuned.data.device.repository.DeviceRepository
import com.truedigital.features.tuned.data.player.PlayerSource
import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.setting.repository.SettingRepository
import com.truedigital.features.tuned.data.station.model.Rating
import com.truedigital.features.tuned.data.station.model.Stakkar
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.station.model.StationVote
import com.truedigital.features.tuned.data.station.model.TrackExtras
import com.truedigital.features.tuned.data.station.model.Vote
import com.truedigital.features.tuned.data.station.model.request.PlaybackState
import com.truedigital.features.tuned.data.station.repository.StationRepository
import com.truedigital.features.tuned.data.stream.repository.StreamRepository
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.track.repository.TrackRepository
import com.truedigital.features.tuned.data.user.model.AssociatedDevice
import com.truedigital.features.tuned.data.user.model.Period
import com.truedigital.features.tuned.data.user.model.Settings
import com.truedigital.features.tuned.data.user.model.Subscription
import com.truedigital.features.tuned.data.user.model.User
import com.truedigital.features.tuned.data.user.repository.MusicUserRepository
import com.truedigital.features.tuned.data.util.LocalisedString
import com.truedigital.features.tuned.service.music.facade.MusicPlayerFacade
import com.truedigital.foundation.player.model.MediaAsset
import io.mockk.mockk
import io.reactivex.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import timber.log.Timber
import java.util.Date
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class MusicPlayerFacadeImplTest {

    private val streamRepository: StreamRepository = mock()
    private val authenticationTokenRepository: AuthenticationTokenRepository = mock()
    private val stationRepository: StationRepository = mock()
    private val musicUserRepository: MusicUserRepository = mock()
    private val deviceRepository: DeviceRepository = mock()
    private val trackRepository: TrackRepository = mock()
    private val cacheRepository: CacheRepository = mock()
    private val settingRepository: SettingRepository = mock()
    private val adRepository: AdRepository = mock()
    private val analyticManager: AnalyticManager = mockk()
    private val musicRoomRepository: MusicRoomRepository = mock()

    private lateinit var musicPlayerFacade: MusicPlayerFacade

    private val mockStakkar = Stakkar(
        id = 1,
        publisherImage = "publisherImage",
        publisherName = "publisherName",
        type = Stakkar.MediaType.VIDEO,
        links = emptyList(),
        bannerUrl = "bannerUrl",
        bannerImage = emptyList(),
        hideDialog = true,
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
        isCached = true,
    )

    private val mockStation = Station(
        id = 1,
        type = Station.StationType.PRESET,
        name = listOf(LocalisedString(language = "TH", value = "nameTh")),
        description = listOf(LocalisedString(language = "TH", value = "descriptionTh")),
        coverImage = listOf(LocalisedString(language = "TH", value = "coverImageTh")),
        bannerImage = listOf(LocalisedString(language = "TH", value = "bannerImageTh")),
        bannerURL = "bannerURL",
        isActive = true,
    )

    private val mockTrackHistory = TrackHistoryEntity(
        trackId = 1,
        lastPlayedDate = 100L
    )

    private val mockMediaAsset = MediaAsset(
        id = 1,
        location = "location",
        cachePath = "cachePath",
        sessionId = "sessionId",
    )

    private val mockSetting = Settings(
        allowStreams = true,
        limitSkips = true,
        adFirstMinutes = 1,
        adIntervalMinutes = 2,
        allowPurchase = true,
        allowAlbumNavigation = true,
        allowOffline = false,
        allowSync = false,
        syncCutOffDays = 1,
        maxSkipsPerHour = 1,
        limitPlays = true,
        monthlyPlayLimit = 1,
        adProvider = AdProvider.NONE,
        tracksPerAd = 1,
        interstitialId = "interstitialId",
        facebookUrl = "facebookUrl",
        twitterUrl = "twitterUrl",
        instagramUrl = "instagramUrl",
        youtubeUrl = "youtubeUrl",
        supportEmail = "supportEmail",
        dmcaEnabled = true,
        offlineMaximumDuration = 100L,
    )

    private val mockAlbum = Album(
        id = 1,
        name = "name",
        artists = emptyList(),
        primaryRelease = null,
        releaseIds = emptyList(),
    )

    private val mockPlaylist = Playlist(
        id = 1,
        name = emptyList(),
        description = emptyList(),
        creatorId = 2,
        creatorName = "creatorName",
        creatorImage = "creatorImage",
        trackCount = 1,
        publicTrackCount = 2,
        duration = 3,
        createDate = Date(),
        updateDate = Date(),
        trackIds = emptyList(),
        coverImage = emptyList(),
        isVideo = true,
        isPublic = true,
        typedTags = emptyList(),
        isOwner = true,
    )

    private val mockAd = Ad(
        title = "title",
        impressionUrl = "impressionUrl",
        duration = "duration",
        mediaFile = "mediaFile",
        image = "image",
        clickUrl = "clickUrl",
        vast = "vast",
    )

    private val mockUser = User(
        userId = 1,
        displayName = "displayName",
        firstName = "firstName",
        lastName = "lastName",
        primaryEmail = "primaryEmail",
        isPrimaryEmailValidated = true,
        image = "image",
        backgroundImage = "backgroundImage",
        followers = emptyList(),
        following = emptyList(),
        isPublic = true,
        optedIn = true,
        blocked = emptyList(),
        language = "language",
        subscriptions = emptyList(),
        devices = emptyList(),
        birthYear = 1996,
        gender = "gender",
        logins = emptyList(),
        action = "action",
        audioQuality = "audioQuality",
        contentLanguages = emptyList(),
        country = "country",
        isVerified = true,
        circle = "circle",
        isFacebookUser = true,
        isTwitterUser = true,
    )

    private val mockPeriod = Period(
        id = 1,
        dateFrom = "dateFrom",
        dateTo = "dateTo",
        status = 2,
    )

    private val mockSubscription = Subscription(
        isEnabled = true,
        id = 1,
        packageId = 2,
        key = "Key",
        startDate = Date(),
        endDate = Date(),
        isAutoRenew = true,
        description = emptyList(),
        name = emptyList(),
        period = mockPeriod,
        renewalCostId = null,
        renewalCostTitle = emptyList(),
    )

    private val mockTrackExtras = TrackExtras(
        stakkars = listOf(mockStakkar),
        ads = listOf(mockAd),
    )

    private val mockVote = Vote(
        id = 1,
        vote = "vote",
        type = "type",
        actionDate = Date(),
    )

    private val mockAssociatedDevice = AssociatedDevice(
        deviceId = 1,
        uniqueId = "uniqueId",
        displayName = "displayName",
        lastSeen = "lastSeen",
    )

    private val mockArtist = Artist(
        id = 1,
        name = "name",
        image = "image",
    )

    private val mockPlayerSourceImpl = MockPlayerSourceImpl(
        sourceId = 1,
        sourceImage = emptyList(),
        sourceType = "sourceType",
        sourceStation = null,
        sourceAlbum = null,
        sourceArtist = null,
        sourcePlaylist = null,
        sourceTrack = null,
        isOffline = true,
    )

    private val mockPlaybackState = PlaybackState(
        trackId = 1,
        state = "state",
        fileSource = "fileSource",
        elapsedSeconds = 1000L,
        source = "source",
        sourceId = 2,
        guid = "guid",
    )

    @BeforeEach
    fun setUp() {
        musicPlayerFacade = MusicPlayerFacadeImpl(
            analyticManager,
            streamRepository,
            authenticationTokenRepository,
            stationRepository,
            musicUserRepository,
            deviceRepository,
            trackRepository,
            cacheRepository,
            settingRepository,
            adRepository,
            musicRoomRepository,
        )
    }

    @Test
    fun loadStakkar_returnStakkar() {
        whenever(stationRepository.getStakkar(any())).thenReturn(Single.just(mockStakkar))

        musicPlayerFacade.loadStakkar(1)
            .test()
            .assertNoErrors()
            .assertValue { stakkar ->
                stakkar == mockStakkar
            }

        verify(stationRepository, times(1)).getStakkar(any())
    }

    @Test
    fun loadTrack_returnTrack() {
        whenever(trackRepository.get(any())).thenReturn(Single.just(mockTrack))

        musicPlayerFacade.loadTrack(1)
            .test()
            .assertNoErrors()
            .assertValue { track ->
                track == mockTrack
            }

        verify(trackRepository, times(1)).get(any())
    }

    @Test
    fun enqueueStation_returnTrack() {
        whenever(stationRepository.getTracks(any(), any())).thenReturn(
            Single.just(listOf(mockTrack)),
        )

        musicPlayerFacade.enqueueStation(mockStation, "trackHash")
            .test()
            .assertNoErrors()
            .assertValue { trackList ->
                val track = trackList.first()
                track == mockTrack
            }

        verify(stationRepository, times(1)).getTracks(any(), any())
    }

    @Test
    fun streamTrack_locationIsNotNull_returnMediaAssetData() {
        val mockLocation = "location"

        whenever(cacheRepository.getTrackLocationIfExist(any())).thenReturn(mockLocation)

        musicPlayerFacade.streamTrack(mockTrack)
            .test()
            .assertNoErrors()
            .assertValue { mediaAsset ->
                mediaAsset.id == mockTrack.id &&
                    mediaAsset.location == mockLocation &&
                    mediaAsset.cachePath.isNullOrEmpty()
            }

        verify(cacheRepository, times(1)).getTrackLocationIfExist(any())
    }

    @Test
    fun streamTrack_locationIsNull_returnMediaAssetData() {
        val mockCachePath = "cachePaht"
        val mockUserValue = mockUser.copy(
            devices = listOf(mockAssociatedDevice),
        )

        whenever(cacheRepository.getTrackLocationIfExist(any())).thenReturn(null)
        whenever(musicRoomRepository.getTrackHistories()).thenReturn(
            Single.just(
                listOf(
                    mockTrackHistory
                )
            )
        )
        whenever(musicUserRepository.get()).thenReturn(Single.just(mockUserValue))
        whenever(deviceRepository.getUniqueId()).thenReturn("aaaa")
        whenever(streamRepository.get(any(), any(), any())).thenReturn(Single.just(mockMediaAsset))
        whenever(cacheRepository.getTrackFileLocation(any(), any())).thenReturn(mockCachePath)

        musicPlayerFacade.streamTrack(mockTrack)
            .test()
            .awaitDone(5, TimeUnit.SECONDS)
            .assertNoErrors()
            .assertValue { mediaAsset ->
                mediaAsset.id == mockMediaAsset.id &&
                    mediaAsset.location == mockMediaAsset.location &&
                    mediaAsset.sessionId == mockMediaAsset.sessionId &&
                    mediaAsset.cachePath == mockCachePath
            }

        verify(cacheRepository, times(1)).getTrackLocationIfExist(any())
        verify(musicRoomRepository, times(1)).getTrackHistories()
        verify(musicUserRepository, times(1)).get()
        verify(streamRepository, times(1)).get(any(), any(), any())
        verify(deviceRepository, times(1)).getUniqueId()
        verify(cacheRepository, times(1)).pruneCache(any())
        verify(cacheRepository, times(1)).pruneCache(any())
    }

    @Test
    fun streamStakkar_returnMediaAsset() {
        whenever(musicUserRepository.get()).thenReturn(Single.just(mockUser))
        whenever(streamRepository.get(any())).thenReturn(Single.just(mockMediaAsset))

        musicPlayerFacade.streamStakkar(mockStakkar)
            .test()
            .assertNoErrors()
            .assertValue { mediaAsset ->
                mediaAsset.id == mockMediaAsset.id &&
                    mediaAsset.location == mockMediaAsset.location &&
                    mediaAsset.cachePath == mockMediaAsset.cachePath &&
                    mediaAsset.sessionId == mockMediaAsset.sessionId
            }

        verify(musicUserRepository, times(1)).get()
        verify(streamRepository, times(1)).get(any())
    }

    // ----------------------------- AdSourceType == ALBUM ------------------------------------------
    @Test
    fun enqueueTrackExtras_adSourceTypeIsAlbum_includeAdsIsTrue_adProviderIsTriton_activeSubIsTrue_returnTrackExtrasEmptyValue() {
        val mockPlayerSource = MockPlayerSourceImpl(
            sourceId = 1,
            sourceImage = emptyList(),
            sourceType = "sourceType",
            sourceStation = null,
            sourceAlbum = mockAlbum,
            sourceArtist = null,
            sourcePlaylist = null,
            sourceTrack = null,
            isOffline = true,
        )
        val mockSettingValue = mockSetting.copy(adProvider = AdProvider.TRITON)
        val mockSubscriptionValue = mockSubscription.copy(isAutoRenew = true)
        val mockUserValue = mockUser.copy(subscriptions = listOf(mockSubscriptionValue))

        whenever(musicUserRepository.getSettings()).thenReturn(mockSettingValue)
        whenever(musicUserRepository.get()).thenReturn(Single.just(mockUserValue))

        musicPlayerFacade.enqueueTrackExtras(
            source = mockPlayerSource,
            nextTrack = mockTrack,
            previousTrack = mockTrack,
            includeAds = true,
        )
            .test()
            .assertNoErrors()
            .assertValue { trackExtras ->
                trackExtras.stakkars.isEmpty() &&
                    trackExtras.ads.isEmpty()
            }
    }

    @Test
    fun enqueueTrackExtras_adSourceTypeIsAlbum_includeAdsIsTrue_adProviderIsTriton_activeSubIsFalse_returnTrackExtrasValue() {
        val mockPlayerSource = MockPlayerSourceImpl(
            sourceId = 1,
            sourceImage = emptyList(),
            sourceType = "sourceType",
            sourceStation = null,
            sourceAlbum = mockAlbum,
            sourceArtist = null,
            sourcePlaylist = null,
            sourceTrack = null,
            isOffline = true,
        )
        val mockSettingValue = mockSetting.copy(adProvider = AdProvider.TRITON)
        val mockSubscriptionValue = mockSubscription.copy(isAutoRenew = false)
        val mockUserValue = mockUser.copy(subscriptions = listOf(mockSubscriptionValue))
        val mockLSID = "LSID"

        whenever(musicUserRepository.getSettings()).thenReturn(mockSettingValue)
        whenever(musicUserRepository.get()).thenReturn(Single.just(mockUserValue))
        whenever(deviceRepository.getLSID()).thenReturn(Single.just(mockLSID))
        whenever(adRepository.getTritonAds(any(), any(), any())).thenReturn(Single.just(mockAd))

        musicPlayerFacade.enqueueTrackExtras(
            source = mockPlayerSource,
            nextTrack = mockTrack,
            previousTrack = mockTrack,
            includeAds = true,
        )
            .test()
            .assertNoErrors()
            .assertValue { trackExtras ->
                val ad = trackExtras.ads.first()
                trackExtras.stakkars.isEmpty() &&
                    ad.title == mockAd.title &&
                    ad.impressionUrl == mockAd.impressionUrl &&
                    ad.duration == mockAd.duration
            }
    }

    @Test
    fun enqueueTrackExtras_adSourceTypeIsAlbum_includeAdsIsTrue_adProviderIsNotTriton_activeSubIsTrue_returnTrackExtrasEmptyValue() {
        val mockPlayerSource = MockPlayerSourceImpl(
            sourceId = 1,
            sourceImage = emptyList(),
            sourceType = "sourceType",
            sourceStation = null,
            sourceAlbum = mockAlbum,
            sourceArtist = null,
            sourcePlaylist = null,
            sourceTrack = null,
            isOffline = true,
        )
        val mockSettingValue = mockSetting.copy(adProvider = AdProvider.NONE)

        whenever(musicUserRepository.getSettings()).thenReturn(mockSettingValue)

        musicPlayerFacade.enqueueTrackExtras(
            source = mockPlayerSource,
            nextTrack = mockTrack,
            previousTrack = mockTrack,
            includeAds = true,
        )
            .test()
            .assertNoErrors()
            .assertValue { trackExtras ->
                trackExtras.stakkars.isEmpty() &&
                    trackExtras.ads.isEmpty()
            }
    }

    @Test
    fun enqueueTrackExtras_adSourceTypeIsAlbum_includeAdsIsFalse_adProviderIsTriton_activeSubIsTrue_returnTrackExtrasEmptyValue() {
        val mockPlayerSource = MockPlayerSourceImpl(
            sourceId = 1,
            sourceImage = emptyList(),
            sourceType = "sourceType",
            sourceStation = null,
            sourceAlbum = mockAlbum,
            sourceArtist = null,
            sourcePlaylist = null,
            sourceTrack = null,
            isOffline = true,
        )
        val mockSettingValue = mockSetting.copy(adProvider = AdProvider.TRITON)

        whenever(musicUserRepository.getSettings()).thenReturn(mockSettingValue)

        musicPlayerFacade.enqueueTrackExtras(
            source = mockPlayerSource,
            nextTrack = mockTrack,
            previousTrack = mockTrack,
            includeAds = false,
        )
            .test()
            .assertNoErrors()
            .assertValue { trackExtras ->
                trackExtras.stakkars.isEmpty() &&
                    trackExtras.ads.isEmpty()
            }
    }

    // ----------------------------- AdSourceType == PLAYLIST ------------------------------------------
    @Test
    fun enqueueTrackExtras_adSourceTypeIsPlaylist_includeAdsIsTrue_adProviderIsTriton_activeSubIsTrue_returnTrackExtrasEmptyValue() {
        val mockPlayerSource = MockPlayerSourceImpl(
            sourceId = 1,
            sourceImage = emptyList(),
            sourceType = "sourceType",
            sourceStation = null,
            sourceAlbum = null,
            sourceArtist = null,
            sourcePlaylist = mockPlaylist,
            sourceTrack = null,
            isOffline = true,
        )
        val mockSettingValue = mockSetting.copy(adProvider = AdProvider.TRITON)
        val mockSubscriptionValue = mockSubscription.copy(isAutoRenew = true)
        val mockUserValue = mockUser.copy(subscriptions = listOf(mockSubscriptionValue))

        whenever(musicUserRepository.getSettings()).thenReturn(mockSettingValue)
        whenever(musicUserRepository.get()).thenReturn(Single.just(mockUserValue))

        musicPlayerFacade.enqueueTrackExtras(
            source = mockPlayerSource,
            nextTrack = mockTrack,
            previousTrack = mockTrack,
            includeAds = true,
        )
            .test()
            .assertNoErrors()
            .assertValue { trackExtras ->
                trackExtras.stakkars.isEmpty() &&
                    trackExtras.ads.isEmpty()
            }
    }

    @Test
    fun enqueueTrackExtras_adSourceTypeIsPlaylist_includeAdsIsTrue_adProviderIsTriton_activeSubIsFalse_returnTrackExtrasValue() {
        val mockPlayerSource = MockPlayerSourceImpl(
            sourceId = 1,
            sourceImage = emptyList(),
            sourceType = "sourceType",
            sourceStation = null,
            sourceAlbum = null,
            sourceArtist = null,
            sourcePlaylist = mockPlaylist,
            sourceTrack = null,
            isOffline = true,
        )
        val mockSettingValue = mockSetting.copy(adProvider = AdProvider.TRITON)
        val mockSubscriptionValue = mockSubscription.copy(isAutoRenew = false)
        val mockUserValue = mockUser.copy(subscriptions = listOf(mockSubscriptionValue))
        val mockLSID = "LSID"

        whenever(musicUserRepository.getSettings()).thenReturn(mockSettingValue)
        whenever(musicUserRepository.get()).thenReturn(Single.just(mockUserValue))
        whenever(deviceRepository.getLSID()).thenReturn(Single.just(mockLSID))
        whenever(adRepository.getTritonAds(any(), any(), any())).thenReturn(Single.just(mockAd))

        musicPlayerFacade.enqueueTrackExtras(
            source = mockPlayerSource,
            nextTrack = mockTrack,
            previousTrack = mockTrack,
            includeAds = true,
        )
            .test()
            .assertNoErrors()
            .assertValue { trackExtras ->
                val ad = trackExtras.ads.first()
                trackExtras.stakkars.isEmpty() &&
                    ad.title == mockAd.title &&
                    ad.impressionUrl == mockAd.impressionUrl &&
                    ad.duration == mockAd.duration
            }
    }

    @Test
    fun enqueueTrackExtras_adSourceTypeIsPlaylist_includeAdsIsTrue_adProviderIsNotTriton_activeSubIsTrue_returnTrackExtrasEmptyValue() {
        val mockPlayerSource = MockPlayerSourceImpl(
            sourceId = 1,
            sourceImage = emptyList(),
            sourceType = "sourceType",
            sourceStation = null,
            sourceAlbum = null,
            sourceArtist = null,
            sourcePlaylist = mockPlaylist,
            sourceTrack = null,
            isOffline = true,
        )
        val mockSettingValue = mockSetting.copy(adProvider = AdProvider.NONE)

        whenever(musicUserRepository.getSettings()).thenReturn(mockSettingValue)

        musicPlayerFacade.enqueueTrackExtras(
            source = mockPlayerSource,
            nextTrack = mockTrack,
            previousTrack = mockTrack,
            includeAds = true,
        )
            .test()
            .assertNoErrors()
            .assertValue { trackExtras ->
                trackExtras.stakkars.isEmpty() &&
                    trackExtras.ads.isEmpty()
            }
    }

    @Test
    fun enqueueTrackExtras_adSourceTypeIsPlaylist_includeAdsIsFalse_adProviderIsTriton_activeSubIsTrue_returnTrackExtrasEmptyValue() {
        val mockPlayerSource = MockPlayerSourceImpl(
            sourceId = 1,
            sourceImage = emptyList(),
            sourceType = "sourceType",
            sourceStation = null,
            sourceAlbum = null,
            sourceArtist = null,
            sourcePlaylist = mockPlaylist,
            sourceTrack = null,
            isOffline = true,
        )
        val mockSettingValue = mockSetting.copy(adProvider = AdProvider.TRITON)

        whenever(musicUserRepository.getSettings()).thenReturn(mockSettingValue)

        musicPlayerFacade.enqueueTrackExtras(
            source = mockPlayerSource,
            nextTrack = mockTrack,
            previousTrack = mockTrack,
            includeAds = false,
        )
            .test()
            .assertNoErrors()
            .assertValue { trackExtras ->
                trackExtras.stakkars.isEmpty() &&
                    trackExtras.ads.isEmpty()
            }
    }

    // ----------------------------- AdSourceType == NONE ------------------------------------------
    @Test
    fun enqueueTrackExtras_adSourceTypeIsNone_includeAdsIsTrue_adProviderIsNull_activeSubIsTrue_returnTrackExtrasEmptyValue() {
        val mockPlayerSource = MockPlayerSourceImpl(
            sourceId = 1,
            sourceImage = emptyList(),
            sourceType = "sourceType",
            sourceStation = null,
            sourceAlbum = null,
            sourceArtist = null,
            sourcePlaylist = null,
            sourceTrack = null,
            isOffline = true,
        )
        val mockSubscriptionValue = mockSubscription.copy(isAutoRenew = true)
        val mockUserValue = mockUser.copy(subscriptions = listOf(mockSubscriptionValue))

        whenever(musicUserRepository.getSettings()).thenReturn(null)
        whenever(musicUserRepository.get()).thenReturn(Single.just(mockUserValue))

        musicPlayerFacade.enqueueTrackExtras(
            source = mockPlayerSource,
            nextTrack = mockTrack,
            previousTrack = mockTrack,
            includeAds = true,
        )
            .test()
            .assertNoErrors()
            .assertValue { trackExtras ->
                trackExtras.stakkars.isEmpty() &&
                    trackExtras.ads.isEmpty()
            }
    }

    @Test
    fun enqueueTrackExtras_adSourceTypeIsNone_includeAdsIsTrue_adProviderIsTriton_activeSubIsTrue_returnTrackExtrasEmptyValue() {
        val mockPlayerSource = MockPlayerSourceImpl(
            sourceId = 1,
            sourceImage = emptyList(),
            sourceType = "sourceType",
            sourceStation = null,
            sourceAlbum = null,
            sourceArtist = null,
            sourcePlaylist = null,
            sourceTrack = null,
            isOffline = true,
        )
        val mockSettingValue = mockSetting.copy(adProvider = AdProvider.TRITON)
        val mockSubscriptionValue = mockSubscription.copy(isAutoRenew = true)
        val mockUserValue = mockUser.copy(subscriptions = listOf(mockSubscriptionValue))

        whenever(musicUserRepository.getSettings()).thenReturn(mockSettingValue)
        whenever(musicUserRepository.get()).thenReturn(Single.just(mockUserValue))

        musicPlayerFacade.enqueueTrackExtras(
            source = mockPlayerSource,
            nextTrack = mockTrack,
            previousTrack = mockTrack,
            includeAds = true,
        )
            .test()
            .assertNoErrors()
            .assertValue { trackExtras ->
                trackExtras.stakkars.isEmpty() &&
                    trackExtras.ads.isEmpty()
            }
    }

    @Test
    fun enqueueTrackExtras_adSourceTypeIsNone_includeAdsIsTrue_adProviderIsTriton_activeSubIsFalse_returnTrackExtrasValue() {
        val mockPlayerSource = MockPlayerSourceImpl(
            sourceId = 1,
            sourceImage = emptyList(),
            sourceType = "sourceType",
            sourceStation = null,
            sourceAlbum = null,
            sourceArtist = null,
            sourcePlaylist = null,
            sourceTrack = null,
            isOffline = true,
        )
        val mockSettingValue = mockSetting.copy(adProvider = AdProvider.TRITON)
        val mockSubscriptionValue = mockSubscription.copy(isAutoRenew = false)
        val mockUserValue = mockUser.copy(subscriptions = listOf(mockSubscriptionValue))
        val mockLSID = "LSID"

        whenever(musicUserRepository.getSettings()).thenReturn(mockSettingValue)
        whenever(musicUserRepository.get()).thenReturn(Single.just(mockUserValue))
        whenever(deviceRepository.getLSID()).thenReturn(Single.just(mockLSID))
        whenever(adRepository.getTritonAds(any(), any(), any())).thenReturn(Single.just(mockAd))

        musicPlayerFacade.enqueueTrackExtras(
            source = mockPlayerSource,
            nextTrack = mockTrack,
            previousTrack = mockTrack,
            includeAds = true,
        )
            .test()
            .assertNoErrors()
            .assertValue { trackExtras ->
                val ad = trackExtras.ads.first()
                trackExtras.stakkars.isEmpty() &&
                    ad.title == mockAd.title &&
                    ad.impressionUrl == mockAd.impressionUrl &&
                    ad.duration == mockAd.duration
            }
    }

    @Test
    fun enqueueTrackExtras_adSourceTypeIsNone_includeAdsIsTrue_adProviderIsNotTriton_activeSubIsTrue_returnTrackExtrasEmptyValue() {
        val mockPlayerSource = MockPlayerSourceImpl(
            sourceId = 1,
            sourceImage = emptyList(),
            sourceType = "sourceType",
            sourceStation = null,
            sourceAlbum = null,
            sourceArtist = null,
            sourcePlaylist = null,
            sourceTrack = null,
            isOffline = true,
        )
        val mockSettingValue = mockSetting.copy(adProvider = AdProvider.NONE)

        whenever(musicUserRepository.getSettings()).thenReturn(mockSettingValue)

        musicPlayerFacade.enqueueTrackExtras(
            source = mockPlayerSource,
            nextTrack = mockTrack,
            previousTrack = mockTrack,
            includeAds = true,
        )
            .test()
            .assertNoErrors()
            .assertValue { trackExtras ->
                trackExtras.stakkars.isEmpty() &&
                    trackExtras.ads.isEmpty()
            }
    }

    @Test
    fun enqueueTrackExtras_adSourceTypeIsNone_includeAdsIsFalse_adProviderIsTriton_activeSubIsTrue_returnTrackExtrasEmptyValue() {
        val mockPlayerSource = MockPlayerSourceImpl(
            sourceId = 1,
            sourceImage = emptyList(),
            sourceType = "sourceType",
            sourceStation = null,
            sourceAlbum = null,
            sourceArtist = null,
            sourcePlaylist = null,
            sourceTrack = null,
            isOffline = true,
        )
        val mockSettingValue = mockSetting.copy(adProvider = AdProvider.TRITON)

        whenever(musicUserRepository.getSettings()).thenReturn(mockSettingValue)

        musicPlayerFacade.enqueueTrackExtras(
            source = mockPlayerSource,
            nextTrack = mockTrack,
            previousTrack = mockTrack,
            includeAds = false,
        )
            .test()
            .assertNoErrors()
            .assertValue { trackExtras ->
                trackExtras.stakkars.isEmpty() &&
                    trackExtras.ads.isEmpty()
            }
    }

    // ----------------------------- AdSourceType == STATION ------------------------------------------
    @Test
    fun enqueueTrackExtras_adSourceTypeIsNone_returnTrackExtrasValue() {
        val mockPlayerSource = MockPlayerSourceImpl(
            sourceId = 1,
            sourceImage = emptyList(),
            sourceType = "sourceType",
            sourceStation = mockStation,
            sourceAlbum = null,
            sourceArtist = null,
            sourcePlaylist = null,
            sourceTrack = null,
            isOffline = true,
        )
        val mockAssociatedDevice = AssociatedDevice(
            deviceId = 1,
            uniqueId = "uniqueId",
            displayName = "displayName",
            lastSeen = "lastSeen",
        )
        val mockLSID = "LSID"
        val mockSubscriptionValue = mockSubscription.copy(isAutoRenew = true)
        val mockUserValue = mockUser.copy(
            subscriptions = listOf(mockSubscriptionValue),
            devices = listOf(mockAssociatedDevice),
        )

        whenever(musicUserRepository.get()).thenReturn(Single.just(mockUserValue))
        whenever(deviceRepository.getLSID()).thenReturn(Single.just(mockLSID))
        whenever(deviceRepository.getUniqueId()).thenReturn(mockAssociatedDevice.uniqueId)
        whenever(
            stationRepository.getTrackExtras(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
            ),
        ).thenReturn(Single.just(mockTrackExtras))

        musicPlayerFacade.enqueueTrackExtras(
            source = mockPlayerSource,
            nextTrack = mockTrack,
            previousTrack = mockTrack,
            includeAds = true,
        )
            .test()
            .assertNoErrors()
            .assertValue { trackExtras ->
                val ad = trackExtras.ads.first()
                trackExtras.stakkars.first() == mockStakkar &&
                    ad.title == mockAd.title &&
                    ad.impressionUrl == mockAd.impressionUrl &&
                    ad.duration == mockAd.duration
            }
    }

    @Test
    fun enqueueTrackExtras_adSourceTypeIsNone_nextTrackIsNull_previousTrackIsNull_includeAdsIsFalse_returnTrackExtrasValue() {
        val mockPlayerSource = MockPlayerSourceImpl(
            sourceId = 1,
            sourceImage = emptyList(),
            sourceType = "sourceType",
            sourceStation = mockStation,
            sourceAlbum = null,
            sourceArtist = null,
            sourcePlaylist = null,
            sourceTrack = null,
            isOffline = true,
        )
        val mockLSID = "LSID"
        val mockSubscriptionValue = mockSubscription.copy(isAutoRenew = true)
        val mockUserValue = mockUser.copy(
            subscriptions = listOf(mockSubscriptionValue),
            devices = listOf(mockAssociatedDevice),
        )

        whenever(musicUserRepository.get()).thenReturn(Single.just(mockUserValue))
        whenever(deviceRepository.getLSID()).thenReturn(Single.just(mockLSID))
        whenever(deviceRepository.getUniqueId()).thenReturn(mockAssociatedDevice.uniqueId)
        whenever(
            stationRepository.getTrackExtras(
                any(),
                any(),
                anyOrNull(),
                anyOrNull(),
                any(),
                any(),
                any(),
            ),
        ).thenReturn(Single.just(mockTrackExtras))

        musicPlayerFacade.enqueueTrackExtras(
            source = mockPlayerSource,
            nextTrack = null,
            previousTrack = null,
            includeAds = false,
        )
            .test()
            .assertNoErrors()
            .assertValue { trackExtras ->
                val ad = trackExtras.ads.first()
                trackExtras.stakkars.first() == mockStakkar &&
                    ad.title == mockAd.title &&
                    ad.impressionUrl == mockAd.impressionUrl &&
                    ad.duration == mockAd.duration
            }
    }

    @Test
    fun checkAdCount_adCountMoreThanTracksPerAd_returnTrue() {
        val mockSettingValue = mockSetting.copy(tracksPerAd = 5)
        whenever(settingRepository.getAdCounter()).thenReturn(10)
        whenever(musicUserRepository.getSettings()).thenReturn(mockSettingValue)

        val value = musicPlayerFacade.checkAdCount()
        assertTrue(value)
    }

    @Test
    fun checkAdCount_adCountLessThanTracksPerAd_returnFalse() {
        val mockSettingValue = mockSetting.copy(tracksPerAd = 10)
        whenever(settingRepository.getAdCounter()).thenReturn(5)
        whenever(musicUserRepository.getSettings()).thenReturn(mockSettingValue)

        val value = musicPlayerFacade.checkAdCount()
        assertFalse(value)
    }

    @Test
    fun checkAdCount_adCountEqualsTracksPerAd_returnTrue() {
        val mockSettingValue = mockSetting.copy(tracksPerAd = 1)
        whenever(settingRepository.getAdCounter()).thenReturn(1)
        whenever(musicUserRepository.getSettings()).thenReturn(mockSettingValue)

        val value = musicPlayerFacade.checkAdCount()
        assertTrue(value)
    }

    @Test
    fun checkAdCount_tracksPerAdIsNull_adCountMoreThanZero_returnTrue() {
        whenever(settingRepository.getAdCounter()).thenReturn(10)
        whenever(musicUserRepository.getSettings()).thenReturn(null)

        val value = musicPlayerFacade.checkAdCount()
        assertTrue(value)
    }

    @Test
    fun checkAdCount_tracksPerAdIsNull_adCountMoreIsZero_returnTrue() {
        whenever(settingRepository.getAdCounter()).thenReturn(0)
        whenever(musicUserRepository.getSettings()).thenReturn(null)

        val value = musicPlayerFacade.checkAdCount()
        assertTrue(value)
    }

    @Test
    fun resetAdCount_resetAdCounterIsCalled() {
        musicPlayerFacade.resetAdCount()
        verify(settingRepository, times(1)).resetAdCounter()
    }

    @Test
    fun addTrackHistory_createTrackHistoryIsCalled() {
        whenever(musicRoomRepository.insertTrackHistory(any(), any())).thenReturn(Single.just(1L))

        musicPlayerFacade.addTrackHistory(mockTrack)

        verify(musicRoomRepository, times(1)).insertTrackHistory(any(), any())
    }

    @Test
    fun getTotalSkips_settingIsNotNull_returnTotalSkips() {
        val mockSkipEntityList = listOf(
            SkipEntity(timestamp = 1L),
            SkipEntity(timestamp = 2L),
            SkipEntity(timestamp = 3L)
        )
        val mockSkipPerHour = 10
        val mockRemainSkips = 2
        val mockSettingValue = mockSetting.copy(maxSkipsPerHour = mockSkipPerHour)

        whenever(musicRoomRepository.getSkips()).thenReturn(Single.just(mockSkipEntityList))
        whenever(musicUserRepository.getSettings()).thenReturn(mockSettingValue)
        whenever(musicRoomRepository.deleteExpireSkips(any())).thenReturn(
            Single.just(
                mockRemainSkips
            )
        )

        musicPlayerFacade.getTotalSkips()
            .test()
            .awaitDone(5, TimeUnit.SECONDS)
            .assertNoErrors()
            .assertValue { totalSkips ->
                val result = ((mockSkipPerHour - mockSkipEntityList.size) + mockRemainSkips)
                totalSkips == result
            }
    }

    @Test
    fun getTotalSkips_settingIsNull_returnTotalSkips() {
        val mockSkipEntityList = listOf(
            SkipEntity(timestamp = 1L),
            SkipEntity(timestamp = 2L),
            SkipEntity(timestamp = 3L)
        )
        val mockRemainSkips = 2

        whenever(musicRoomRepository.getSkips()).thenReturn(Single.just(mockSkipEntityList))
        whenever(musicUserRepository.getSettings()).thenReturn(null)
        whenever(musicRoomRepository.deleteExpireSkips(any())).thenReturn(
            Single.just(
                mockRemainSkips
            )
        )

        musicPlayerFacade.getTotalSkips()
            .test()
            .awaitDone(5, TimeUnit.SECONDS)
            .assertNoErrors()
            .assertValue { totalSkips ->
                val result = ((0 - mockSkipEntityList.size) + mockRemainSkips)
                totalSkips == result
            }
    }

    @Test
    fun checkLocalSkipCount_returnLocalSkipCount() {
        val mockLocalSkipCount = 100
        whenever(musicRoomRepository.deleteExpireSkips(any())).thenReturn(
            Single.just(
                mockLocalSkipCount
            )
        )

        musicPlayerFacade.checkLocalSkipCount()
            .test()
            .assertNoErrors()
            .assertValue { localSkipCount ->
                localSkipCount == mockLocalSkipCount
            }
    }

    @Test
    fun addLocalSkip_addLocalSkipIsCalled() {
        whenever(musicRoomRepository.insertSkip(any())).thenReturn(Single.just(10L))

        musicPlayerFacade.addLocalSkip()

        verify(musicRoomRepository, times(1)).insertSkip(any())
    }

    @Test
    fun getTotalPlays_success_returnTotalPlays() {
        val mockPlays = listOf<Long>(1L, 2L, 3L)
        val mockMonthPlayLimit = 10
        val mockRemainPlays = 2
        val mockSettingValue = mockSetting.copy(monthlyPlayLimit = mockMonthPlayLimit)

        whenever(musicRoomRepository.getPlays()).thenReturn(Single.just(mockPlays))
        whenever(musicUserRepository.getSettings()).thenReturn(mockSettingValue)
        whenever(musicRoomRepository.deleteExpirePlays(any())).thenReturn(
            Single.just(mockRemainPlays)
        )

        musicPlayerFacade.getTotalPlays()
            .test()
            .awaitDone(5, TimeUnit.SECONDS)
            .assertNoErrors()
            .assertValue { totalPlays ->
                val result = ((mockMonthPlayLimit - mockPlays.size) + mockRemainPlays)
                totalPlays == result
            }
    }

    @Test
    fun logTrackSkip_elapsedSecondsMoreThanTrackDuration_doNothing() {
        val mockTrackValue = mockTrack.copy(duration = 1)
        val mockSessionId = "sessionId"

        musicPlayerFacade.logTrackSkip(
            source = mockPlayerSourceImpl,
            track = mockTrackValue,
            elapsed = 3000L,
            sessionId = mockSessionId,
        )

        verify(musicUserRepository, times(0)).get()
    }

    @Test
    fun addLocalPlay_addPlayIsCalled() {
        whenever(musicRoomRepository.insertPlay(any())).thenReturn(Single.just(1L))
        musicPlayerFacade.addLocalPlay()

        verify(musicRoomRepository, times(1)).insertPlay(any())
    }

    @Test
    fun resetPlayTrackLog_trackIsReset() {
        musicPlayerFacade.resetPlayTrackLog()
    }

    @Test
    fun likeTrack_returnVote() {
        whenever(stationRepository.putVote(any(), any(), any())).thenReturn(Single.just(mockVote))

        musicPlayerFacade.likeTrack(mockStation, mockTrack)
            .test()
            .assertNoErrors()
            .assertValue { vote ->
                vote == mockVote
            }
    }

    @Test
    fun dislikeTrack_returnVote() {
        whenever(stationRepository.putVote(any(), any(), any())).thenReturn(Single.just(mockVote))

        musicPlayerFacade.dislikeTrack(mockStation, mockTrack)
            .test()
            .assertNoErrors()
            .assertValue { vote ->
                vote == mockVote
            }
    }

    @Test
    fun removeRating_returnVote() {
        val mockStationVote = StationVote(
            vote = mockVote,
            success = true,
        )
        whenever(stationRepository.deleteVote(any(), any())).thenReturn(
            Single.just(listOf(mockStationVote)),
        )

        musicPlayerFacade.removeRating(mockStation, mockTrack)
            .test()
            .assertNoErrors()
            .assertValue { vote ->
                vote == mockVote
            }
    }

    @Test
    fun setShufflePlay_setShufflePlayIsCalled() {
        musicPlayerFacade.setShufflePlay(true)

        verify(settingRepository, times(1)).setShufflePlay(any())
    }

    @Test
    fun isShufflePlayEnabled_returnShufflePlayEnabledStatus() {
        whenever(settingRepository.isShufflePlayEnabled()).thenReturn(true)

        val value = musicPlayerFacade.isShufflePlayEnabled()
        assertTrue(value)

        verify(settingRepository, times(1)).isShufflePlayEnabled()
    }

    @Test
    fun setRepeatMode_setRepeatModeIsCalled() {
        musicPlayerFacade.setRepeatMode(1)

        verify(settingRepository, times(1)).setRepeatMode(any())
    }

    @Test
    fun getRepeatMode_returnRepeatMode() {
        val mockRepeatMode = 1
        whenever(settingRepository.getRepeatMode()).thenReturn(mockRepeatMode)

        val value = musicPlayerFacade.getRepeatMode()
        assertEquals(mockRepeatMode, value)

        verify(settingRepository, times(1)).getRepeatMode()
    }

    @Test
    fun isStreamingEnabled_allowMobileDataStreamingIsTrue_wifiConnectedIsTrue_returnTrue() {
        whenever(settingRepository.allowMobileDataStreaming()).thenReturn(true)
        whenever(deviceRepository.isWifiConnected()).thenReturn(true)

        val value = musicPlayerFacade.isStreamingEnabled()
        assertTrue(value)

        verify(settingRepository, times(1)).allowMobileDataStreaming()
    }

    @Test
    fun isStreamingEnabled_allowMobileDataStreamingIsTrue_wifiConnectedIsFalse_returnTrue() {
        whenever(settingRepository.allowMobileDataStreaming()).thenReturn(true)
        whenever(deviceRepository.isWifiConnected()).thenReturn(false)

        val value = musicPlayerFacade.isStreamingEnabled()
        assertTrue(value)

        verify(settingRepository, times(1)).allowMobileDataStreaming()
    }

    @Test
    fun isStreamingEnabled_allowMobileDataStreamingIsFalse_wifiConnectedIsTrue_returnTrue() {
        whenever(settingRepository.allowMobileDataStreaming()).thenReturn(false)
        whenever(deviceRepository.isWifiConnected()).thenReturn(true)

        val value = musicPlayerFacade.isStreamingEnabled()
        assertTrue(value)

        verify(settingRepository, times(1)).allowMobileDataStreaming()
        verify(deviceRepository, times(1)).isWifiConnected()
    }

    @Test
    fun isStreamingEnabled_allowMobileDataStreamingIsFalse_wifiConnectedIsFalse_returnFalse() {
        whenever(settingRepository.allowMobileDataStreaming()).thenReturn(false)
        whenever(deviceRepository.isWifiConnected()).thenReturn(false)

        val value = musicPlayerFacade.isStreamingEnabled()
        assertFalse(value)

        verify(settingRepository, times(1)).allowMobileDataStreaming()
        verify(deviceRepository, times(1)).isWifiConnected()
    }

    @Test
    fun isPlayLimitEnabled_settingIsNotNull_returnPlayLimitEnabledStatus() {
        val mockSettingValue = mockSetting.copy(limitPlays = false)
        whenever(musicUserRepository.getSettings()).thenReturn(mockSettingValue)

        val value = musicPlayerFacade.isPlayLimitEnabled()
        assertFalse(value)
    }

    @Test
    fun isPlayLimitEnabled_settingIsNull_returnTrue() {
        whenever(musicUserRepository.getSettings()).thenReturn(null)

        val value = musicPlayerFacade.isPlayLimitEnabled()
        assertTrue(value)
    }

    @Test
    fun isSkipLimitEnabled_settingIsNotNull_returnSkipLimitEnabledStatus() {
        val mockSettingValue = mockSetting.copy(limitSkips = false)
        whenever(musicUserRepository.getSettings()).thenReturn(mockSettingValue)

        val value = musicPlayerFacade.isSkipLimitEnabled()
        assertFalse(value)
    }

    @Test
    fun isSkipLimitEnabled_settingIsNull_returnTrue() {
        whenever(musicUserRepository.getSettings()).thenReturn(null)

        val value = musicPlayerFacade.isSkipLimitEnabled()
        assertTrue(value)
    }

    @Test
    fun getLikesCount_returnLikesCount() {
        val mockLikesCount = 10
        whenever(deviceRepository.getLikesCount()).thenReturn(mockLikesCount)

        val value = musicPlayerFacade.getLikesCount()
        assertEquals(mockLikesCount, value)
    }

    inner class MockPlayerSourceImpl(
        override var sourceId: Int,
        override var sourceImage: List<LocalisedString>,
        override var sourceType: String,
        override var sourceStation: Station?,
        override var sourceAlbum: Album?,
        override var sourceArtist: Artist?,
        override var sourcePlaylist: Playlist?,
        override var sourceTrack: Track?,
        override var isOffline: Boolean,
    ) : PlayerSource {
        override fun resetPlayerSource(isOffline: Boolean) {
            Timber.d("$isOffline")
        }
    }
}
