package com.truedigital.features.tuned.data.station.repository

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.api.station.StationMetadataApiInterface
import com.truedigital.features.tuned.api.station.StationServiceApiInterface
import com.truedigital.features.tuned.data.ad.model.Ad
import com.truedigital.features.tuned.data.ad.model.AdProvider
import com.truedigital.features.tuned.data.ad.model.response.CampaignAd
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.database.repository.MusicRoomRepository
import com.truedigital.features.tuned.data.station.model.LikedTrack
import com.truedigital.features.tuned.data.station.model.Rating
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.data.station.model.StationTrack
import com.truedigital.features.tuned.data.station.model.StationVote
import com.truedigital.features.tuned.data.station.model.Vote
import com.truedigital.features.tuned.data.station.model.response.Stakkar
import com.truedigital.features.tuned.data.station.model.response.StationContext
import com.truedigital.features.tuned.data.station.model.response.TrackExtras
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.util.LocalisedString
import com.truedigital.features.tuned.data.util.PagedResults
import io.reactivex.Single
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Date
import java.util.concurrent.TimeUnit

internal class StationRepositoryTest {

    private val stationServiceApi: StationServiceApiInterface = mock()
    private val stationMetadataApi: StationMetadataApiInterface = mock()
    private val musicRoomRepository: MusicRoomRepository = mock()
    private lateinit var stationRepository: StationRepository

    private val mockStation = Station(
        id = 1,
        type = Station.StationType.PRESET,
        name = listOf(LocalisedString(language = "TH", value = "nameTh")),
        description = listOf(LocalisedString(language = "TH", value = "descriptionTh")),
        coverImage = listOf(LocalisedString(language = "TH", value = "coverImageTh")),
        bannerImage = listOf(LocalisedString(language = "TH", value = "bannerImageTh")),
        bannerURL = "bannerURL",
        isActive = true
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

    private val mockStationTrack = StationTrack(
        stationTrackId = 1,
        track = mockTrack,
        score = 10.0,
        vote = Rating.LIKED
    )

    private val mockStakkar = Stakkar(
        id = 1,
        publisher = Stakkar.Publisher(
            profile = Stakkar.Publisher.Profile(
                name = "name",
                image = "image"
            )
        ),
        type = com.truedigital.features.tuned.data.station.model.Stakkar.MediaType.AUDIO,
        links = emptyList(),
        bannerUrl = "bannerUrl",
        bannerImage = null,
        hideDialog = true
    )

    private val mockVote = Vote(
        id = 1,
        vote = "vote",
        type = "type",
        actionDate = Date()
    )

    private val mockPagedResults = PagedResults(
        offset = 1,
        count = 1,
        total = 10,
        results = listOf(mockStation)
    )

    @BeforeEach
    fun setUp() {
        stationRepository = StationRepositoryImpl(
            stationServiceApi = stationServiceApi,
            stationMetadataApi = stationMetadataApi,
            musicRoomRepository = musicRoomRepository
        )
    }

    @Test
    fun get_refreshIsTrue_returnStationFromService() {
        whenever(stationMetadataApi.station(any())).thenReturn(Single.just(mockStation))
        whenever(musicRoomRepository.insertStation(any())).thenReturn(Single.just(1L))

        stationRepository.get(10, true)
            .test()
            .awaitDone(5, TimeUnit.SECONDS)
            .assertNoErrors()
            .assertValue { station ->
                station.id == mockStation.id &&
                    station.name == mockStation.name
            }

        verify(stationMetadataApi, times(1)).station(any())
        verify(musicRoomRepository, times(1)).insertStation(any())
    }

    @Test
    fun get_refreshIsFalse_returnStationFromCache() {
        whenever(musicRoomRepository.getStation(any())).thenReturn(Single.just(mockStation))

        stationRepository.get(10, false)
            .test()
            .awaitDone(5, TimeUnit.SECONDS)
            .assertNoErrors()
            .assertValue { station ->
                station.id == mockStation.id &&
                    station.name == mockStation.name
            }

        verify(musicRoomRepository, times(1)).getStation(any())
    }

    @Test
    fun getLocalMix_returnStation() {
        whenever(stationServiceApi.addBySeed(any(), any())).thenReturn(Single.just(mockStation))

        stationRepository.getLocalMix(1)
            .test()
            .assertNoErrors()
            .assertValue { station ->
                station.id == mockStation.id &&
                    station.type == mockStation.type &&
                    station.name == mockStation.name &&
                    station.description == mockStation.description &&
                    station.coverImage == mockStation.coverImage &&
                    station.bannerImage == mockStation.bannerImage &&
                    station.bannerURL == mockStation.bannerURL &&
                    station.isActive
            }

        verify(stationServiceApi, times(1)).addBySeed(any(), any())
    }

    @Test
    fun getByArtist_returnStation() {
        whenever(stationServiceApi.addBySeed(any(), any())).thenReturn(
            Single.just(mockStation)
        )
        whenever(musicRoomRepository.insertStation(any())).thenReturn(Single.just(1L))

        stationRepository.getByArtist(10)
            .test()
            .awaitDone(5, TimeUnit.SECONDS)
            .assertNoErrors()
            .assertValue { station ->
                station.id == mockStation.id &&
                    station.type == mockStation.type &&
                    station.name == mockStation.name &&
                    station.description == mockStation.description &&
                    station.coverImage == mockStation.coverImage &&
                    station.bannerImage == mockStation.bannerImage &&
                    station.bannerURL == mockStation.bannerURL &&
                    station.isActive
            }

        verify(stationServiceApi, times(1)).addBySeed(any(), any())
        verify(musicRoomRepository, times(1)).insertStation(any())
    }

    @Test
    fun getContainingArtist_returnStationList() {
        whenever(stationMetadataApi.containingArtist(any())).thenReturn(
            Single.just(listOf(mockStation))
        )
        whenever(musicRoomRepository.insertStations(any())).thenReturn(Single.just(listOf(1L)))

        stationRepository.getContainingArtist(10)
            .test()
            .awaitDone(5, TimeUnit.SECONDS)
            .assertNoErrors()
            .assertValue { stationList ->
                val station = stationList.first()
                station.id == mockStation.id &&
                    station.type == mockStation.type &&
                    station.name == mockStation.name &&
                    station.description == mockStation.description &&
                    station.coverImage == mockStation.coverImage &&
                    station.bannerImage == mockStation.bannerImage &&
                    station.bannerURL == mockStation.bannerURL &&
                    station.isActive
            }

        verify(stationMetadataApi, times(1)).containingArtist(any())
        verify(musicRoomRepository, times(1)).insertStations(any())
    }

    @Test
    fun getSimilar_returnStationList() {
        whenever(stationMetadataApi.similarStations(any())).thenReturn(
            Single.just(listOf(mockStation))
        )
        whenever(musicRoomRepository.insertStations(any())).thenReturn(Single.just(listOf(1L)))

        stationRepository.getSimilar(10)
            .test()
            .awaitDone(5, TimeUnit.SECONDS)
            .assertNoErrors()
            .assertValue { stationList ->
                val station = stationList.first()
                station.id == mockStation.id &&
                    station.type == mockStation.type &&
                    station.name == mockStation.name &&
                    station.description == mockStation.description &&
                    station.coverImage == mockStation.coverImage &&
                    station.bannerImage == mockStation.bannerImage &&
                    station.bannerURL == mockStation.bannerURL &&
                    station.isActive
            }

        verify(stationMetadataApi, times(1)).similarStations(any())
        verify(musicRoomRepository, times(1)).insertStations(any())
    }

    @Test
    fun getPopular_returnStationList() {
        whenever(stationMetadataApi.trendingStations()).thenReturn(
            Single.just(listOf(mockStation))
        )
        whenever(musicRoomRepository.insertStations(any())).thenReturn(Single.just(listOf(1L)))

        stationRepository.getPopular()
            .test()
            .awaitDone(5, TimeUnit.SECONDS)
            .assertNoErrors()
            .assertValue { stationList ->
                val station = stationList.first()
                station.id == mockStation.id &&
                    station.type == mockStation.type &&
                    station.name == mockStation.name &&
                    station.description == mockStation.description &&
                    station.coverImage == mockStation.coverImage &&
                    station.bannerImage == mockStation.bannerImage &&
                    station.bannerURL == mockStation.bannerURL &&
                    station.isActive
            }

        verify(stationMetadataApi, times(1)).trendingStations()
        verify(musicRoomRepository, times(1)).insertStations(any())
    }

    @Test
    fun getRecent_returnStationList() {
        whenever(stationServiceApi.lastPlayed()).thenReturn(
            Single.just(listOf(mockStation))
        )
        whenever(musicRoomRepository.insertStations(any())).thenReturn(Single.just(listOf(1L)))

        stationRepository.getRecent()
            .test()
            .assertNoErrors()
            .assertValue { stationList ->
                val station = stationList.first()
                station.id == mockStation.id &&
                    station.type == mockStation.type &&
                    station.name == mockStation.name &&
                    station.description == mockStation.description &&
                    station.coverImage == mockStation.coverImage &&
                    station.bannerImage == mockStation.bannerImage &&
                    station.bannerURL == mockStation.bannerURL &&
                    station.isActive
            }

        verify(stationServiceApi, times(1)).lastPlayed()
        verify(musicRoomRepository, times(1)).insertStations(any())
    }

    @Test
    fun getFavourited_returnStationList() {
        whenever(stationServiceApi.favouritedStations(any(), any())).thenReturn(
            Single.just(mockPagedResults)
        )
        whenever(musicRoomRepository.insertStations(any())).thenReturn(Single.just(listOf(1L)))

        stationRepository.getFavourited(1, 100)
            .test()
            .awaitDone(5, TimeUnit.SECONDS)
            .assertNoErrors()
            .assertValue { stationList ->
                val station = stationList.first()
                station.id == mockStation.id &&
                    station.type == mockStation.type &&
                    station.name == mockStation.name &&
                    station.description == mockStation.description &&
                    station.coverImage == mockStation.coverImage &&
                    station.bannerImage == mockStation.bannerImage &&
                    station.bannerURL == mockStation.bannerURL &&
                    station.isActive
            }

        verify(stationServiceApi, times(1)).favouritedStations(any(), any())
        verify(musicRoomRepository, times(1)).insertStations(any())
    }

    @Test
    fun getSuggested_returnStationList() {
        whenever(stationMetadataApi.presetStations(any(), any())).thenReturn(
            Single.just(
                PagedResults(
                    offset = 1,
                    count = 1,
                    total = 10,
                    results = listOf(mockStation)
                )
            )
        )
        whenever(musicRoomRepository.insertStations(any())).thenReturn(Single.just(listOf(1L)))

        stationRepository.getSuggested(1, 100)
            .test()
            .awaitDone(5, TimeUnit.SECONDS)
            .assertNoErrors()
            .assertValue { stationList ->
                val station = stationList.first()
                station.id == mockStation.id &&
                    station.type == mockStation.type &&
                    station.name == mockStation.name &&
                    station.description == mockStation.description &&
                    station.coverImage == mockStation.coverImage &&
                    station.bannerImage == mockStation.bannerImage &&
                    station.bannerURL == mockStation.bannerURL &&
                    station.isActive
            }

        verify(stationMetadataApi, times(1)).presetStations(any(), any())
        verify(musicRoomRepository, times(1)).insertStations(any())
    }

    @Test
    fun getSyncTracks_voteIsNotNull_returnTrackList() {
        whenever(stationServiceApi.stationSyncTracks(any())).thenReturn(
            Single.just(listOf(mockStationTrack))
        )

        stationRepository.getSyncTracks(1)
            .test()
            .assertNoErrors()
            .assertValue { trackList ->
                val track = trackList.first()
                track == mockTrack
            }

        verify(stationServiceApi, times(1)).stationSyncTracks(any())
    }

    @Test
    fun getSyncTracks_voteIsNull_returnTrackList() {
        val mockStationTrackValue = mockStationTrack.copy(vote = null)

        whenever(stationServiceApi.stationSyncTracks(any())).thenReturn(
            Single.just(listOf(mockStationTrackValue))
        )

        stationRepository.getSyncTracks(1)
            .test()
            .assertNoErrors()
            .assertValue { trackList ->
                val track = trackList.first()
                track == mockTrack
            }

        verify(stationServiceApi, times(1)).stationSyncTracks(any())
    }

    @Test
    fun getStakkar_returnStakkar() {
        whenever(stationServiceApi.stakkar(any())).thenReturn(
            Single.just(mockStakkar)
        )

        stationRepository.getStakkar(1)
            .test()
            .assertNoErrors()
            .assertValue { stakkar ->
                stakkar.id == mockStakkar.id &&
                    stakkar.publisherImage == mockStakkar.publisher.profile.image &&
                    stakkar.publisherName == mockStakkar.publisher.profile.name &&
                    stakkar.type == mockStakkar.type &&
                    stakkar.links == mockStakkar.links &&
                    stakkar.bannerUrl == mockStakkar.bannerUrl &&
                    stakkar.bannerImage == mockStakkar.bannerImage &&
                    stakkar.hideDialog == mockStakkar.hideDialog
            }

        verify(stationServiceApi, times(1)).stakkar(any())
    }

    @Test
    fun getTracks_voteIsNotNull_returnTrackList() {
        whenever(stationServiceApi.stationTracks(any(), any(), any())).thenReturn(
            Single.just(listOf(mockStationTrack))
        )

        stationRepository.getTracks(1, "trackHash")
            .test()
            .assertNoErrors()
            .assertValue { trackList ->
                val track = trackList.first()
                track == mockTrack
            }

        verify(stationServiceApi, times(1)).stationTracks(any(), any(), any())
    }

    @Test
    fun getTracks_voteIsNull_returnTrackList() {
        val mockStationTrackValue = mockStationTrack.copy(vote = null)

        whenever(stationServiceApi.stationTracks(any(), any(), any())).thenReturn(
            Single.just(listOf(mockStationTrackValue))
        )

        stationRepository.getTracks(1, "trackHash")
            .test()
            .assertNoErrors()
            .assertValue { trackList ->
                val track = trackList.first()
                track == mockTrack
            }

        verify(stationServiceApi, times(1)).stationTracks(any(), any(), any())
    }

    @Test
    fun getTrackExtras_returnTrackExtras() {
        val mockStakkarRequest = com.truedigital.features.tuned.data.station.model.Stakkar(
            id = 1,
            publisherImage = "publisherImage",
            publisherName = "publisherName",
            type = com.truedigital.features.tuned.data.station.model.Stakkar.MediaType.AUDIO,
            links = emptyList(),
            bannerUrl = "bannerUrl",
            bannerImage = emptyList(),
            hideDialog = true
        )
        val mockAd = Ad(
            title = "title",
            impressionUrl = "impressionUrl",
            duration = "duration",
            mediaFile = "mediaFile",
            image = "image",
            clickUrl = "clickUrl",
            vast = "vast"
        )
        val mockCampaignAd = CampaignAd(ad = mockAd, xml = "xml")
        val mockTrackExtras = TrackExtras(
            stakkars = listOf(mockStakkarRequest),
            campaignAds = listOf(mockCampaignAd)
        )

        whenever(
            stationServiceApi.stationTrackExtras(
                any(),
                any(),
                any(),
                any(),
                any(),
                any(),
                any()
            )
        ).thenReturn(
            Single.just(mockTrackExtras)
        )

        stationRepository.getTrackExtras(1, 2, 3, 4, true, AdProvider.NONE, "lsid")
            .test()
            .assertNoErrors()
            .assertValue { trackExtras ->
                val stakkar = trackExtras.stakkars.first()
                val ads = trackExtras.ads.first()

                stakkar.id == mockStakkarRequest.id &&
                    ads.vast == mockAd.vast
            }

        verify(stationServiceApi, times(1)).stationTrackExtras(
            any(),
            any(),
            any(),
            any(),
            any(),
            any(),
            any()
        )
    }

    @Test
    fun getVotes_returnLikedTrack() {
        val mockLikedTrack = LikedTrack(
            type = Rating.LIKED,
            track = null,
            artists = listOf(
                Artist(
                    id = 1,
                    name = "name",
                    image = "image"
                )
            )
        )

        whenever(stationServiceApi.stationUserVotes(any(), any(), any())).thenReturn(
            Single.just(listOf(mockLikedTrack))
        )

        stationRepository.getVotes(1, 2)
            .test()
            .assertNoErrors()
            .assertValue { likedTrackList ->
                val likedTrack = likedTrackList.first()
                likedTrack.type == mockLikedTrack.type &&
                    likedTrack.artists.first() == mockLikedTrack.artists.first()
            }

        verify(stationServiceApi, times(1)).stationUserVotes(any(), any(), any())
    }

    @Test
    fun deleteVote_returnStationVoteList() {
        val mockStationVote = StationVote(
            vote = mockVote,
            success = true
        )

        whenever(stationServiceApi.stationVote(any(), any())).thenReturn(
            Single.just(listOf(mockStationVote))
        )

        stationRepository.deleteVote(1, 2)
            .test()
            .assertNoErrors()
            .assertValue { stationVoteList ->
                val stationVote = stationVoteList.first()

                stationVote.vote == mockStationVote.vote &&
                    stationVote.success == stationVote.success
            }

        verify(stationServiceApi, times(1)).stationVote(any(), any())
    }

    @Test
    fun deleteVotes_returnStationVoteList() {
        whenever(stationServiceApi.deleteVotes(any())).thenReturn(Single.just(Any()))

        stationRepository.deleteVotes(1).test()

        verify(stationServiceApi, times(1)).deleteVotes(any())
    }

    @Test
    fun putVote_ratingIsLiked_returnStationVoteList() {
        val mockStationVote = StationVote(
            vote = mockVote,
            success = true
        )

        whenever(stationServiceApi.stationVote(any(), any())).thenReturn(
            Single.just(listOf(mockStationVote))
        )

        stationRepository.putVote(1, 2, Rating.LIKED)
            .test()
            .assertNoErrors()
            .assertValue { vote ->
                vote == mockVote
            }

        verify(stationServiceApi, times(1)).stationVote(any(), any())
    }

    @Test
    fun putVote_ratingIsDisliked_returnStationVoteList() {
        val mockStationVote = StationVote(
            vote = mockVote,
            success = true
        )

        whenever(stationServiceApi.stationVote(any(), any())).thenReturn(
            Single.just(listOf(mockStationVote))
        )

        stationRepository.putVote(1, 2, Rating.DISLIKED)
            .test()
            .assertNoErrors()
            .assertValue { vote ->
                vote == mockVote
            }

        verify(stationServiceApi, times(1)).stationVote(any(), any())
    }

    @Test
    fun isFavourite_returnIsFavourite() {
        val mockIsFavorite = true

        whenever(stationServiceApi.userContext(any())).thenReturn(
            Single.just(StationContext(mockIsFavorite))
        )

        stationRepository.isFavourited(1)
            .test()
            .assertValue(mockIsFavorite)

        verify(stationServiceApi, times(1)).userContext(any())
    }

    @Test
    fun removeFavourite_verifyUnFavourite() {
        whenever(stationServiceApi.unfavouriteStation(any())).thenReturn(Single.just(Any()))
        stationRepository.removeFavourite(1)
        verify(stationServiceApi, times(1)).unfavouriteStation(any())
    }

    @Test
    fun addFavourite_verifyAddFavourite() {
        whenever(stationServiceApi.favouriteStation(any())).thenReturn(Single.just(Any()))
        stationRepository.addFavourite(1)
        verify(stationServiceApi, times(1)).favouriteStation(any())
    }

    @Test
    fun getFavouritedCount_returnFavouriteCount() {
        whenever(stationServiceApi.favouritedStations(any(), any())).thenReturn(
            Single.just(mockPagedResults)
        )

        stationRepository.getFavouritedCount()
            .test()
            .assertNoErrors()
            .assertValue { total ->
                total == mockPagedResults.total
            }

        verify(stationServiceApi, times(1)).favouritedStations(any(), any())
    }
}
