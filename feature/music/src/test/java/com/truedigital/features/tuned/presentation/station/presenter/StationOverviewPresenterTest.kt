package com.truedigital.features.tuned.presentation.station.presenter

import android.net.Uri
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.station.model.Station
import com.truedigital.features.tuned.presentation.station.facade.StationOverviewFacade
import com.truedigital.features.utils.MockDataModel
import io.mockk.every
import io.mockk.mockkStatic
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito

internal class StationOverviewPresenterTest {

    private val overviewFacade: StationOverviewFacade = mock()
    private val view: StationOverviewPresenter.ViewSurface = mock()
    private val router: StationOverviewPresenter.RouterSurface = mock()
    private val featuredArtistsSubscription: Disposable = mock()
    private val similarStationsSubscription: Disposable = mock()
    private lateinit var presenter: StationOverviewPresenter

    private val mockStation = Station(
        id = 1,
        type = Station.StationType.ARTIST,
        name = emptyList(),
        description = emptyList(),
        coverImage = emptyList(),
        bannerImage = emptyList(),
        bannerURL = "bannerURL",
        isActive = true
    )

    private val mockArtist = Artist(
        id = 1,
        name = "name",
        image = "image"
    )

    @BeforeEach
    fun setUp() {
        presenter = StationOverviewPresenter(overviewFacade)
        presenter.onInject(view, router)
    }

    @Test
    fun onStart_bundleNull_nothing() {
        // When
        presenter.onStart(null)

        // Then
        verify(view, times(0)).initStation(any())
        verify(view, times(0)).showOnlineStation(any())
    }

    @Test
    fun onStart_stationNull_nothing() {
        // Given
        val mockBundle = Mockito.mock(Bundle::class.java)
        whenever(mockBundle.getParcelable<Station>(StationOverviewPresenter.STATION_KEY)).thenReturn(
            null
        )
        whenever(mockBundle.getString(StationOverviewPresenter.TRACK_HASH_KEY)).thenReturn("hash")
        whenever(mockBundle.getBoolean(StationOverviewPresenter.AUTO_PLAY_KEY)).thenReturn(true)

        // When
        presenter.onStart(mockBundle)

        // Then
        verify(view, times(0)).initStation(any())
        verify(view, times(0)).showOnlineStation(any())
    }

    @Test
    fun onStart_isAutoPlayTrue_verifyPlayStation() {
        // Given
        val mockBundle = Mockito.mock(Bundle::class.java)
        whenever(mockBundle.getParcelable<Station>(StationOverviewPresenter.STATION_KEY)).thenReturn(
            mockStation
        )
        whenever(mockBundle.getString(StationOverviewPresenter.TRACK_HASH_KEY)).thenReturn("hash")
        whenever(mockBundle.getBoolean(StationOverviewPresenter.AUTO_PLAY_KEY)).thenReturn(true)
        whenever(overviewFacade.loadFeaturedArtists(anyOrNull())).thenReturn(
            Single.just(
                listOf(
                    mockArtist
                )
            )
        )
        whenever(overviewFacade.loadSimilarStations(anyOrNull())).thenReturn(
            Single.just(listOf(mockStation))
        )

        // When
        presenter.onStart(mockBundle)

        // Then
        verify(view, times(1)).initStation(mockStation)
        verify(view, times(1)).showOnlineStation(mockStation)
        verify(view, times(1)).playStation(mockStation, "hash")
        verify(view, times(0)).showNoArtistShuffleRights()
    }

    @Test
    fun onStart_isAutoPlayFalse_notVerifyPlayStation() {
        // Given
        val mockBundle = Mockito.mock(Bundle::class.java)
        whenever(mockBundle.getParcelable<Station>(StationOverviewPresenter.STATION_KEY)).thenReturn(
            mockStation
        )
        whenever(mockBundle.getString(StationOverviewPresenter.TRACK_HASH_KEY)).thenReturn("hash")
        whenever(mockBundle.getBoolean(StationOverviewPresenter.AUTO_PLAY_KEY)).thenReturn(false)
        whenever(overviewFacade.loadFeaturedArtists(anyOrNull())).thenReturn(
            Single.just(
                listOf(
                    mockArtist
                )
            )
        )
        whenever(overviewFacade.loadSimilarStations(anyOrNull())).thenReturn(
            Single.just(listOf(mockStation))
        )

        // When
        presenter.onStart(mockBundle)

        // Then
        verify(view, times(1)).initStation(mockStation)
        verify(view, times(1)).showOnlineStation(mockStation)
        verify(view, times(0)).playStation(mockStation, "hash")
        verify(view, times(0)).showNoArtistShuffleRights()
    }

    @Test
    fun onStart_typeIsSingleArtist_getHasArtistShuffleRightTrue_notVerifyShowNoArtistShuffleRights() {
        // Given
        val mockBundle = Mockito.mock(Bundle::class.java)
        whenever(mockBundle.getParcelable<Station>(StationOverviewPresenter.STATION_KEY)).thenReturn(
            mockStation.copy(type = Station.StationType.SINGLE_ARTIST)
        )
        whenever(mockBundle.getString(StationOverviewPresenter.TRACK_HASH_KEY)).thenReturn("hash")
        whenever(mockBundle.getBoolean(StationOverviewPresenter.AUTO_PLAY_KEY)).thenReturn(false)
        whenever(overviewFacade.loadFeaturedArtists(anyOrNull())).thenReturn(
            Single.just(
                listOf(
                    mockArtist
                )
            )
        )
        whenever(overviewFacade.loadSimilarStations(anyOrNull())).thenReturn(
            Single.just(listOf(mockStation))
        )
        whenever(overviewFacade.getHasArtistShuffleRight()).thenReturn(true)

        // When
        presenter.onStart(mockBundle)

        // Then
        verify(view, times(1)).initStation(mockStation)
        verify(view, times(1)).showOnlineStation(mockStation)
        verify(view, times(0)).playStation(any(), any())
        verify(view, times(0)).showNoArtistShuffleRights()
    }

    @Test
    fun onStart_typeIsSingleArtist_getHasArtistShuffleRightFalse_verifyShowNoArtistShuffleRights() {
        // Given
        val mockBundle = Mockito.mock(Bundle::class.java)
        whenever(mockBundle.getParcelable<Station>(StationOverviewPresenter.STATION_KEY)).thenReturn(
            mockStation.copy(type = Station.StationType.SINGLE_ARTIST)
        )
        whenever(mockBundle.getString(StationOverviewPresenter.TRACK_HASH_KEY)).thenReturn("hash")
        whenever(mockBundle.getBoolean(StationOverviewPresenter.AUTO_PLAY_KEY)).thenReturn(false)
        whenever(overviewFacade.loadFeaturedArtists(anyOrNull())).thenReturn(
            Single.just(
                listOf(
                    mockArtist
                )
            )
        )
        whenever(overviewFacade.loadSimilarStations(anyOrNull())).thenReturn(
            Single.just(listOf(mockStation))
        )
        whenever(overviewFacade.getHasArtistShuffleRight()).thenReturn(false)

        // When
        presenter.onStart(mockBundle)

        // Then
        verify(view, times(1)).initStation(mockStation)
        verify(view, times(1)).showOnlineStation(mockStation)
        verify(view, times(0)).playStation(any(), any())
        verify(view, times(1)).showNoArtistShuffleRights()
    }

    @Test
    fun onResume_getSimilarStationsSubscription_success_showSimilarStations() {
        // Given
        val mockSimilarStationsSubscription: Disposable = mock()
        presenter.setPrivateData(similarStationsSubscription = mockSimilarStationsSubscription)
        presenter.setObservable(similarStationsObservable = Single.just(listOf(MockDataModel.mockStation)))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showSimilarStations(any())
    }

    @Test
    fun onResume_getSimilarStationsSubscription_fail_errorIsHttpException_errorCodeIsResourceNotFound_hideSimilarStations() {
        // Given
        val mockSimilarStationsSubscription: Disposable = mock()
        presenter.setPrivateData(similarStationsSubscription = mockSimilarStationsSubscription)
        presenter.setObservable(similarStationsObservable = Single.error(MockDataModel.mockHttpExceptionCodeResourceNotFound))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).hideSimilarStations()
    }

    @Test
    fun onResume_getSimilarStationsSubscription_fail_errorIsHttpException_errorCodeIsNotResourceNotFound_showSimilarStationsError() {
        // Given
        val mockSimilarStationsSubscription: Disposable = mock()
        presenter.setPrivateData(similarStationsSubscription = mockSimilarStationsSubscription)
        presenter.setObservable(similarStationsObservable = Single.error(MockDataModel.mockHttpExceptionCodeUnauthorised))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showSimilarStationsError()
    }

    @Test
    fun onResume_getSimilarStationsSubscription_fail_errorIsNotHttpException_showSimilarStationsError() {
        // Given
        val mockSimilarStationsSubscription: Disposable = mock()
        presenter.setPrivateData(similarStationsSubscription = mockSimilarStationsSubscription)
        presenter.setObservable(similarStationsObservable = Single.error(Throwable("error")))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showSimilarStationsError()
    }

    @Test
    fun onResume_getFeaturedArtistsSubscription_success_showFeaturedArtists() {
        // Given
        val mockFeaturedArtistsSubscription: Disposable = mock()
        presenter.setPrivateData(featuredArtistsSubscription = mockFeaturedArtistsSubscription)
        presenter.setObservable(featuredArtistsObservable = Single.just(listOf(MockDataModel.mockArtist)))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showFeaturedArtists(any())
    }

    @Test
    fun onResume_getFeaturedArtistsSubscription_fail_errorIsHttpException_errorCodeIsResourceNotFound_hideFeaturedArtists() {
        // Given
        val mockFeaturedArtistsSubscription: Disposable = mock()
        presenter.setPrivateData(featuredArtistsSubscription = mockFeaturedArtistsSubscription)
        presenter.setObservable(featuredArtistsObservable = Single.error(MockDataModel.mockHttpExceptionCodeResourceNotFound))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).hideFeaturedArtists()
    }

    @Test
    fun onResume_getFeaturedArtistsSubscription_fail_errorIsHttpException_errorCodeIsNotResourceNotFound_showFeaturedArtistsError() {
        // Given
        val mockFeaturedArtistsSubscription: Disposable = mock()
        presenter.setPrivateData(featuredArtistsSubscription = mockFeaturedArtistsSubscription)
        presenter.setObservable(featuredArtistsObservable = Single.error(MockDataModel.mockHttpExceptionCodeUnauthorised))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showFeaturedArtistsError()
    }

    @Test
    fun onResume_getFeaturedArtistsSubscription_fail_errorIsNotHttpException_showFeaturedArtistsError() {
        // Given
        val mockFeaturedArtistsSubscription: Disposable = mock()
        presenter.setPrivateData(featuredArtistsSubscription = mockFeaturedArtistsSubscription)
        presenter.setObservable(featuredArtistsObservable = Single.error(Throwable("error")))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showFeaturedArtistsError()
    }

    @Test
    fun onPause_subscriptionIsNotNull_disposeSubscription() {
        // Given
        presenter.setPrivateData(
            similarStationsSubscription = similarStationsSubscription,
            featuredArtistsSubscription = featuredArtistsSubscription
        )

        // When
        presenter.onPause()

        // Then
        verify(similarStationsSubscription, times(1)).dispose()
        verify(featuredArtistsSubscription, times(1)).dispose()
    }

    @Test
    fun onPause_subscriptionIsNull_doNothing() {
        // Given
        presenter.setPrivateData(
            similarStationsSubscription = null,
            featuredArtistsSubscription = null
        )

        // When
        presenter.onPause()

        // Then
        verify(similarStationsSubscription, times(0)).dispose()
        verify(featuredArtistsSubscription, times(0)).dispose()
    }

    @Test
    fun onPlayStation_stationIsNotnull_stationTypeIsSingleArtist_hasArtistShuffleRightIsTrue_showPlayButtonLoadingAndPlayStation() {
        // Given
        val mockStationValue = mockStation.copy(type = Station.StationType.SINGLE_ARTIST)
        presenter.setPrivateData(station = mockStationValue)
        whenever(overviewFacade.getHasArtistShuffleRight()).thenReturn(true)

        // When
        presenter.onPlayStation()

        // Then
        verify(overviewFacade, times(1)).getHasArtistShuffleRight()
        verify(view, times(1)).showPlayButtonLoading()
        verify(view, times(1)).playStation(any(), anyOrNull())
    }

    @Test
    fun onPlayStation_stationIsNotnull_stationTypeIsSingleArtist_hasArtistShuffleRightIsFalse_showUpgradeDialog() {
        // Given
        val mockStationValue = mockStation.copy(type = Station.StationType.SINGLE_ARTIST)
        presenter.setPrivateData(station = mockStationValue)
        whenever(overviewFacade.getHasArtistShuffleRight()).thenReturn(false)

        // When
        presenter.onPlayStation()

        // Then
        verify(overviewFacade, times(1)).getHasArtistShuffleRight()
        verify(view, times(1)).showUpgradeDialog()
    }

    @Test
    fun onPlayStation_stationIsNotnull_stationTypeIsNotSingleArtist_showPlayButtonLoadingAndPlayStation() {
        // Given
        val mockStationValue = mockStation.copy(type = Station.StationType.PRESET)
        presenter.setPrivateData(station = mockStationValue)

        // When
        presenter.onPlayStation()

        // Then
        verify(view, times(1)).showPlayButtonLoading()
        verify(view, times(1)).playStation(any(), anyOrNull())
    }

    @Test
    fun onPlayStation_stationIsnull_stationTypeIsNotSingleArtist_showPlayButtonLoading() {
        // Given
        presenter.setPrivateData(station = null)

        // When
        presenter.onPlayStation()

        // Then
        verify(view, times(1)).showPlayButtonLoading()
        verify(view, times(0)).playStation(any(), anyOrNull())
    }

    @Test
    fun onRetryFeaturedArtists_featuredArtistsSubscriptionIsNotNull_loadArtists() {
        // Given
        presenter.setPrivateData(featuredArtistsSubscription = featuredArtistsSubscription)
        whenever(overviewFacade.loadFeaturedArtists(anyOrNull())).thenReturn(
            Single.just(
                listOf(
                    mockArtist
                )
            )
        )

        // When
        presenter.onRetryFeaturedArtists()

        // Then
        verify(featuredArtistsSubscription, times(1)).dispose()
        verify(overviewFacade, times(1)).loadFeaturedArtists(anyOrNull())
    }

    @Test
    fun onRetryFeaturedArtists_featuredArtistsSubscriptionIsNull_loadArtists() {
        // Given
        presenter.setPrivateData(featuredArtistsSubscription = null)
        whenever(overviewFacade.loadFeaturedArtists(anyOrNull())).thenReturn(
            Single.just(
                listOf(
                    mockArtist
                )
            )
        )

        // When
        presenter.onRetryFeaturedArtists()

        // Then
        verify(featuredArtistsSubscription, times(0)).dispose()
        verify(overviewFacade, times(1)).loadFeaturedArtists(anyOrNull())
    }

    @Test
    fun onRetrySimilarStations_similarStationsSubscriptionIsNotNull_loadSimilarStations() {
        // Given
        presenter.setPrivateData(similarStationsSubscription = similarStationsSubscription)
        whenever(overviewFacade.loadSimilarStations(anyOrNull())).thenReturn(
            Single.just(listOf(mockStation))
        )

        // When
        presenter.onRetrySimilarStations()

        // Then
        verify(similarStationsSubscription, times(1)).dispose()
        verify(overviewFacade, times(1)).loadSimilarStations(anyOrNull())
    }

    @Test
    fun onRetrySimilarStations_similarStationsSubscriptionIsNull_loadSimilarStations() {
        // Given
        presenter.setPrivateData(similarStationsSubscription = null)
        whenever(overviewFacade.loadSimilarStations(anyOrNull())).thenReturn(
            Single.just(listOf(mockStation))
        )

        // When
        presenter.onRetrySimilarStations()

        // Then
        verify(similarStationsSubscription, times(0)).dispose()
        verify(overviewFacade, times(1)).loadSimilarStations(anyOrNull())
    }

    @Test
    fun onFeaturingSeeAllSelected_stationIsNotNull_navigateToProductList() {
        // Given
        presenter.setPrivateData(station = mockStation)

        // When
        presenter.onFeaturingSeeAllSelected()

        // Then
        verify(router, times(1)).navigateToProductList(any(), any())
    }

    @Test
    fun onFeaturingSeeAllSelected_stationIsNull_doNoting() {
        // Given
        presenter.setPrivateData(station = null)

        // When
        presenter.onFeaturingSeeAllSelected()

        // Then
        verify(router, times(0)).navigateToProductList(any(), any())
    }

    @Test
    fun onSimilarSeeAllSelected_stationIsNotNull_navigateToProductList() {
        // Given
        presenter.setPrivateData(station = mockStation)

        // When
        presenter.onSimilarSeeAllSelected()

        // Then
        verify(router, times(1)).navigateToProductList(any(), any())
    }

    @Test
    fun onSimilarSeeAllSelected_stationIsNull_doNoting() {
        // Given
        presenter.setPrivateData(station = null)

        // When
        presenter.onSimilarSeeAllSelected()

        // Then
        verify(router, times(0)).navigateToProductList(any(), any())
    }

    @Test
    fun onArtistSelected_navigateToArtist() {
        // When
        presenter.onArtistSelected(mockArtist)

        // Then
        verify(router, times(1)).navigateToArtist(any())
    }

    @Test
    fun onStationSelected_navigateToStation() {
        // When
        presenter.onStationSelected(mockStation)

        // Then
        verify(router, times(1)).navigateToStation(any())
    }

    @Test
    fun onStationBannerSelected_stationIsNotNull_bannerURLIsNotNullOrEmpty_navigateToUrl() {
        // Given
        val mockStationValue = mockStation.copy(bannerURL = "bannerUrl")
        presenter.setPrivateData(station = mockStationValue)
        mockkStatic(Uri::class)
        every { Uri.parse(any()).lastPathSegment } returns "lastPath"

        // When
        presenter.onStationBannerSelected()

        // Then
        verify(router, times(1)).navigateToUrl(any())
    }

    @Test
    fun onStationBannerSelected_stationIsNotNull_bannerURLIsNull_doNothing() {
        // Given
        val mockStationValue = mockStation.copy(bannerURL = null)
        presenter.setPrivateData(station = mockStationValue)

        // When
        presenter.onStationBannerSelected()

        // Then
        verify(router, times(0)).navigateToUrl(any())
    }

    @Test
    fun onStationBannerSelected_stationIsNotNull_bannerURLIsEmpty_doNothing() {
        // Given
        val mockStationValue = mockStation.copy(bannerURL = "")
        presenter.setPrivateData(station = mockStationValue)

        // When
        presenter.onStationBannerSelected()

        // Then
        verify(router, times(0)).navigateToUrl(any())
    }

    @Test
    fun onStationBannerSelected_stationIsNull_doNothing() {
        // Given
        presenter.setPrivateData(station = null)

        // When
        presenter.onStationBannerSelected()

        // Then
        verify(router, times(0)).navigateToUrl(any())
    }

    @Test
    fun onUpdatePlaybackState_stationIdIsGlobalStationId_stateIsStateBuffering_showPlayButtonLoading() {
        // Given
        val stationId = 10
        val mockStationValue = mockStation.copy(id = stationId)
        presenter.setPrivateData(station = mockStationValue)

        // When
        presenter.onUpdatePlaybackState(
            stationId = stationId,
            state = PlaybackStateCompat.STATE_BUFFERING
        )

        // Then
        verify(view, times(1)).showPlayButtonLoading()
    }

    @Test
    fun onUpdatePlaybackState_stationIdIsGlobalStationId_stateIsNotStateBuffering_showPlayStation() {
        // Given
        val stationId = 10
        val mockStationValue = mockStation.copy(id = stationId)
        presenter.setPrivateData(station = mockStationValue)

        // When
        presenter.onUpdatePlaybackState(
            stationId = stationId,
            state = PlaybackStateCompat.STATE_CONNECTING
        )

        // Then
        verify(view, times(1)).showPlayStation()
    }

    @Test
    fun onUpdatePlaybackState_stationIdIsNotGlobalStationId_stateIsStateBuffering_showPlayStation() {
        // Given
        val stationId = 10
        val mockStationValue = mockStation.copy(id = stationId)
        presenter.setPrivateData(station = mockStationValue)

        // When
        presenter.onUpdatePlaybackState(
            stationId = 20,
            state = PlaybackStateCompat.STATE_BUFFERING
        )

        // Then
        verify(view, times(1)).showPlayStation()
    }

    @Test
    fun onUpdatePlaybackState_stationIdIsNotGlobalStationId_stateIsNotStateBuffering_showPlayStation() {
        // Given
        val stationId = 10
        val mockStationValue = mockStation.copy(id = stationId)
        presenter.setPrivateData(station = mockStationValue)

        // When
        presenter.onUpdatePlaybackState(
            stationId = 20,
            state = PlaybackStateCompat.STATE_NONE
        )

        // Then
        verify(view, times(1)).showPlayStation()
    }
}
