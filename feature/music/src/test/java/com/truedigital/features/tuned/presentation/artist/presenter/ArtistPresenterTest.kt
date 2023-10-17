package com.truedigital.features.tuned.presentation.artist.presenter

import android.os.Bundle
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.application.configuration.Configuration
import com.truedigital.features.tuned.data.artist.model.Artist
import com.truedigital.features.tuned.data.artist.model.ArtistInfo
import com.truedigital.features.tuned.data.station.model.Rating
import com.truedigital.features.tuned.domain.facade.artist.ArtistFacade
import com.truedigital.features.tuned.presentation.bottomsheet.PickerOptions
import com.truedigital.features.utils.MockDataModel
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class ArtistPresenterTest {

    private val artistFacade: ArtistFacade = mock()
    private val mockConfiguration: Configuration = mock()
    private val router: ArtistPresenter.RouterSurface = mock()
    private val view: ArtistPresenter.ViewSurface = mock()
    private val artistSubscription: Disposable = mock()
    private val artistPlayCountSubscription: Disposable = mock()
    private val popularSongsSubscription: Disposable = mock()
    private val latestSongsSubscription: Disposable = mock()
    private val artistSimilarStationSubscription: Disposable = mock()
    private val isFollowedSubscription: Disposable = mock()
    private val toggleFollowedSubscription: Disposable = mock()
    private val similarArtistsSubscription: Disposable = mock()
    private val appearsInSubscription: Disposable = mock()
    private val videoAppearsInSubscription: Disposable = mock()
    private val albumsSubscription: Disposable = mock()
    private val albumsAppearsOnSubscription: Disposable = mock()
    private val clearArtistVotesSubscription: Disposable = mock()

    private lateinit var presenter: ArtistPresenter

    private val mockArtistInfo = ArtistInfo(
        id = 10,
        name = "name"
    )

    private val mockAlbum = MockDataModel.mockAlbum.copy(
        name = "name",
        artists = listOf(mockArtistInfo),
        releaseIds = listOf(1, 2)
    )

    private val mockTrack = MockDataModel.mockTrack.copy(
        id = 1,
        playlistTrackId = 2,
        songId = 3,
        releaseId = 4,
        isExplicit = true,
        trackNumber = 10,
        trackNumberInVolume = 11,
        volumeNumber = 12,
        isOnCompilation = true,
        allowDownload = true,
        duration = 100,
        hasLyrics = true,
        vote = Rating.LIKED,
        isDownloaded = true,
        syncProgress = 10f,
        isCached = true
    )

    private fun mockOnStartObservable() {
        whenever(artistFacade.loadPopularSongs(any())).thenReturn(Single.just(listOf()))
        whenever(artistFacade.loadLatestSongs(any())).thenReturn(Single.just(listOf()))
        whenever(artistFacade.loadFollowed(any())).thenReturn(Single.just(true))
        whenever(artistFacade.loadStationsAppearsIn(any())).thenReturn(Single.just(listOf()))
        whenever(
            artistFacade.getVideoAppearsIn(
                any(),
                any(),
                any(),
                anyOrNull()
            )
        ).thenReturn(Single.just(listOf()))
        whenever(artistFacade.loadSimilarArtists(any())).thenReturn(Single.just(listOf()))
    }

    @BeforeEach
    fun setUp() {
        presenter = ArtistPresenter(artistFacade, mockConfiguration)
        presenter.onInject(view, router)
    }

    @Test
    fun testOnStart_argumentNull_notVerify() {
        presenter.onStart(null)
        verify(view, times(0)).showNoArtistShuffleRight()
        verify(view, times(0)).showArtistHint()
    }

    @Test
    fun testOnStart_hasArtistKey_artistNotNull_verifyShowArtist() {
        val mockBundle = Mockito.mock(Bundle::class.java)
        whenever(mockBundle.getBoolean(ArtistPresenter.AUTO_PLAY_KEY)).thenReturn(true)
        whenever(mockBundle.containsKey(ArtistPresenter.ARTIST_KEY)).thenReturn(true)
        whenever(mockBundle.getParcelable<Artist>(ArtistPresenter.ARTIST_KEY))
            .thenReturn(MockDataModel.mockArtist)
        whenever(artistFacade.getHasArtistShuffleRight()).thenReturn(false)
        whenever(artistFacade.isArtistHintShown()).thenReturn(false)
        whenever(mockConfiguration.enableArtistCount).thenReturn(true)
        whenever(artistFacade.loadArtistPlayCount(any())).thenReturn(Single.just(1))
        whenever(artistFacade.getAlbumNavigationAllowed()).thenReturn(true)
        whenever(
            artistFacade.loadArtistAlbums(
                any(),
                any(),
                any(),
                anyOrNull()
            )
        ).thenReturn(Single.just(listOf()))
        whenever(
            artistFacade.loadArtistAppearsOn(
                any(),
                anyOrNull()
            )
        ).thenReturn(Single.just(listOf()))
        mockOnStartObservable()

        presenter.onStart(mockBundle)

        verify(view, times(1)).showArtist(MockDataModel.mockArtist)
        verify(view, times(1)).showNoArtistShuffleRight()
        verify(view, times(1)).showArtistHint()
    }

    @Test
    fun testOnStart_enableArtistCountFalse_getAlbumNavigationAllowedFalse_verifyShowArtist() {
        val mockBundle = Mockito.mock(Bundle::class.java)
        whenever(mockBundle.getBoolean(ArtistPresenter.AUTO_PLAY_KEY)).thenReturn(true)
        whenever(mockBundle.containsKey(ArtistPresenter.ARTIST_KEY)).thenReturn(true)
        whenever(mockBundle.getParcelable<Artist>(ArtistPresenter.ARTIST_KEY))
            .thenReturn(MockDataModel.mockArtist)
        whenever(artistFacade.getHasArtistShuffleRight()).thenReturn(false)
        whenever(artistFacade.isArtistHintShown()).thenReturn(false)
        whenever(mockConfiguration.enableArtistCount).thenReturn(false)
        whenever(artistFacade.getAlbumNavigationAllowed()).thenReturn(false)
        mockOnStartObservable()

        presenter.onStart(mockBundle)

        verify(artistFacade, times(0)).loadArtistPlayCount(any())
        verify(artistFacade, times(0)).loadArtistAlbums(any(), any(), any(), anyOrNull())
        verify(artistFacade, times(0)).loadArtistAppearsOn(any(), anyOrNull())
        verify(view, times(1)).showNoArtistShuffleRight()
        verify(view, times(1)).showArtistHint()
    }

    @Test
    fun testOnStart_hasArtistKey_artistNull_notVerifyShowArtist() {
        val mockBundle = Mockito.mock(Bundle::class.java)
        whenever(mockBundle.getBoolean(ArtistPresenter.AUTO_PLAY_KEY)).thenReturn(true)
        whenever(mockBundle.containsKey(ArtistPresenter.ARTIST_KEY)).thenReturn(true)
        whenever(mockBundle.getParcelable<Artist>(ArtistPresenter.ARTIST_KEY)).thenReturn(null)

        whenever(artistFacade.getHasArtistShuffleRight()).thenReturn(true)
        whenever(artistFacade.isArtistHintShown()).thenReturn(true)

        presenter.onStart(mockBundle)

        verify(view, times(0)).showArtist(MockDataModel.mockArtist)
        verify(view, times(0)).showNoArtistShuffleRight()
        verify(view, times(0)).showArtistHint()
    }

    @Test
    fun testOnStart_hasArtistIdKey_verifyLoadArtist() {
        val mockBundle = Mockito.mock(Bundle::class.java)
        whenever(mockBundle.getBoolean(ArtistPresenter.AUTO_PLAY_KEY)).thenReturn(true)
        whenever(mockBundle.containsKey(ArtistPresenter.ARTIST_KEY)).thenReturn(false)
        whenever(mockBundle.containsKey(ArtistPresenter.ARTIST_ID_KEY)).thenReturn(true)
        whenever(mockBundle.getInt(ArtistPresenter.ARTIST_ID_KEY)).thenReturn(1)
        whenever(artistFacade.getHasArtistShuffleRight()).thenReturn(false)
        whenever(artistFacade.isArtistHintShown()).thenReturn(false)
        whenever(artistFacade.loadArtist(any())).thenReturn(Single.just(MockDataModel.mockArtist))

        presenter.onStart(mockBundle)

        verify(view, times(0)).showArtist(MockDataModel.mockArtist)
        verify(artistFacade, times(1)).loadArtist(any())
        verify(view, times(1)).showNoArtistShuffleRight()
        verify(view, times(1)).showArtistHint()
    }

    @Test
    fun testOnStart_notHasArtistIdKey_notVerifyLoadArtist() {
        val mockBundle = Mockito.mock(Bundle::class.java)
        whenever(mockBundle.getBoolean(ArtistPresenter.AUTO_PLAY_KEY)).thenReturn(true)
        whenever(mockBundle.containsKey(ArtistPresenter.ARTIST_KEY)).thenReturn(false)
        whenever(mockBundle.containsKey(ArtistPresenter.ARTIST_ID_KEY)).thenReturn(false)
        whenever(mockBundle.getInt(ArtistPresenter.ARTIST_ID_KEY)).thenReturn(1)
        whenever(artistFacade.getHasArtistShuffleRight()).thenReturn(false)
        whenever(artistFacade.isArtistHintShown()).thenReturn(false)

        presenter.onStart(mockBundle)

        verify(view, times(0)).showArtist(MockDataModel.mockArtist)
        verify(artistFacade, times(0)).loadArtist(any())
        verify(view, times(1)).showNoArtistShuffleRight()
        verify(view, times(1)).showArtistHint()
    }

    @Test
    fun testOnResume() {
        presenter.onResume()
    }

    @Test
    fun onPause_subscriptionIsNotNull_disposeSubscription() {
        // Given
        presenter.setSubscriptionFirstSection(
            artistSubscription = artistSubscription,
            artistPlayCountSubscription = artistPlayCountSubscription,
            popularSongsSubscription = popularSongsSubscription,
            latestSongsSubscription = latestSongsSubscription,
            artistSimilarStationSubscription = artistSimilarStationSubscription,
            isFollowedSubscription = isFollowedSubscription,
            toggleFollowedSubscription = toggleFollowedSubscription
        )
        presenter.setSubscriptionSecondSection(
            similarArtistsSubscription = similarArtistsSubscription,
            appearsInSubscription = appearsInSubscription,
            videoAppearsInSubscription = videoAppearsInSubscription,
            albumsSubscription = albumsSubscription,
            albumsAppearsOnSubscription = albumsAppearsOnSubscription,
            clearArtistVotesSubscription = clearArtistVotesSubscription
        )

        // When
        presenter.onPause()

        // Then
        verify(artistSubscription, times(1)).dispose()
        verify(artistPlayCountSubscription, times(1)).dispose()
        verify(popularSongsSubscription, times(1)).dispose()
        verify(latestSongsSubscription, times(1)).dispose()
        verify(artistSimilarStationSubscription, times(1)).dispose()
        verify(isFollowedSubscription, times(1)).dispose()
        verify(toggleFollowedSubscription, times(1)).dispose()
        verify(similarArtistsSubscription, times(1)).dispose()
        verify(appearsInSubscription, times(1)).dispose()
        verify(videoAppearsInSubscription, times(1)).dispose()
        verify(albumsSubscription, times(1)).dispose()
        verify(albumsAppearsOnSubscription, times(1)).dispose()
        verify(clearArtistVotesSubscription, times(1)).dispose()
    }

    @Test
    fun onPause_subscriptionIsNull_doNothing() {
        // Given
        presenter.setSubscriptionFirstSection(
            artistSubscription = null,
            artistPlayCountSubscription = null,
            popularSongsSubscription = null,
            latestSongsSubscription = null,
            artistSimilarStationSubscription = null,
            isFollowedSubscription = null,
            toggleFollowedSubscription = null
        )
        presenter.setSubscriptionSecondSection(
            similarArtistsSubscription = null,
            appearsInSubscription = null,
            videoAppearsInSubscription = null,
            albumsSubscription = null,
            albumsAppearsOnSubscription = null,
            clearArtistVotesSubscription = null
        )

        // When
        presenter.onPause()

        // Then
        verify(artistSubscription, times(0)).dispose()
        verify(artistPlayCountSubscription, times(0)).dispose()
        verify(popularSongsSubscription, times(0)).dispose()
        verify(latestSongsSubscription, times(0)).dispose()
        verify(artistSimilarStationSubscription, times(0)).dispose()
        verify(isFollowedSubscription, times(0)).dispose()
        verify(toggleFollowedSubscription, times(0)).dispose()
        verify(similarArtistsSubscription, times(0)).dispose()
        verify(appearsInSubscription, times(0)).dispose()
        verify(videoAppearsInSubscription, times(0)).dispose()
        verify(albumsSubscription, times(0)).dispose()
        verify(albumsAppearsOnSubscription, times(0)).dispose()
        verify(clearArtistVotesSubscription, times(0)).dispose()
    }

    @Test
    fun onResume_getArtistSubscription_success_showArtist() {
        // Given
        val mockArtistSubscription: Disposable = mock()
        presenter.setSubscriptionFirstSection(artistSubscription = mockArtistSubscription)
        presenter.setObservableFirstSection(artistObservable = Single.just(MockDataModel.mockArtist))

        whenever(artistFacade.getAlbumNavigationAllowed()).thenReturn(true)
        whenever(artistFacade.loadPopularSongs(any())).thenReturn(Single.just(listOf(MockDataModel.mockTrack)))
        whenever(artistFacade.loadLatestSongs(any())).thenReturn(Single.just(listOf(MockDataModel.mockTrack)))
        whenever(artistFacade.loadFollowed(any())).thenReturn(Single.just(true))
        whenever(artistFacade.loadStationsAppearsIn(any())).thenReturn(
            Single.just(listOf(MockDataModel.mockStation))
        )
        whenever(artistFacade.getVideoAppearsIn(any(), any(), any(), any())).thenReturn(
            Single.just(listOf(MockDataModel.mockTrack))
        )
        whenever(artistFacade.loadSimilarArtists(any())).thenReturn(Single.just(listOf(MockDataModel.mockArtist)))
        whenever(artistFacade.loadArtistAlbums(any(), any(), any(), any())).thenReturn(
            Single.just(listOf(MockDataModel.mockAlbum))
        )
        whenever(artistFacade.loadArtistAppearsOn(any(), any())).thenReturn(
            Single.just(listOf(MockDataModel.mockAlbum))
        )

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showArtist(any())
        verify(artistFacade, times(1)).loadPopularSongs(any())
        verify(artistFacade, times(1)).loadLatestSongs(any())
        verify(artistFacade, times(1)).loadFollowed(any())
        verify(artistFacade, times(1)).loadStationsAppearsIn(any())
        verify(artistFacade, times(1)).getVideoAppearsIn(any(), any(), any(), any())
        verify(artistFacade, times(1)).loadSimilarArtists(any())
        verify(artistFacade, times(1)).loadArtistAlbums(any(), any(), any(), any())
    }

    @Test
    fun onResume_getArtistSubscription_fail_showLoadArtistError() {
        // Given
        val mockArtistSubscription: Disposable = mock()
        presenter.setSubscriptionFirstSection(artistSubscription = mockArtistSubscription)
        presenter.setObservableFirstSection(artistObservable = Single.error(Throwable("error")))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showLoadArtistError()
    }

    @Test
    fun onResume_getArtistPlayCountSubscription_success_showArtistPlayCount() {
        // Given
        val mockArtistPlayCountSubscription: Disposable = mock()
        presenter.setSubscriptionFirstSection(artistPlayCountSubscription = mockArtistPlayCountSubscription)
        presenter.setObservableFirstSection(artistPlayCountObservable = Single.just(1))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showArtistPlayCount(any())
    }

    @Test
    fun onResume_getArtistPlayCountSubscription_fail_hideArtistPlayCount() {
        // Given
        val mockArtistPlayCountSubscription: Disposable = mock()
        presenter.setSubscriptionFirstSection(artistPlayCountSubscription = mockArtistPlayCountSubscription)
        presenter.setObservableFirstSection(artistPlayCountObservable = Single.error(Throwable("error")))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).hideArtistPlayCount()
    }

    @Test
    fun onResume_getPopularSongsSubscription_success_showPopularSongs() {
        // Given
        val mockPopularSongsSubscription: Disposable = mock()
        presenter.setSubscriptionFirstSection(popularSongsSubscription = mockPopularSongsSubscription)
        presenter.setObservableFirstSection(
            popularSongsObservable = Single.just(listOf(MockDataModel.mockTrack))
        )

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showPopularSongs(any())
    }

    @Test
    fun onResume_getPopularSongsSubscription_fail_errorIsHttpException_errorCodeIsCodeResourceNotFound_hidePopularSongs() {
        // Given
        val mockPopularSongsSubscription: Disposable = mock()
        presenter.setSubscriptionFirstSection(popularSongsSubscription = mockPopularSongsSubscription)
        presenter.setObservableFirstSection(
            popularSongsObservable = Single.error(MockDataModel.mockHttpExceptionCodeResourceNotFound)
        )

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).hidePopularSongs()
    }

    @Test
    fun onResume_getPopularSongsSubscription_fail_errorIsHttpException_errorCodeIsNotCodeResourceNotFound_showPopularSongsError() {
        // Given
        val mockPopularSongsSubscription: Disposable = mock()
        presenter.setSubscriptionFirstSection(popularSongsSubscription = mockPopularSongsSubscription)
        presenter.setObservableFirstSection(
            popularSongsObservable = Single.error(MockDataModel.mockHttpExceptionCodeUnauthorised)
        )

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showPopularSongsError()
    }

    @Test
    fun onResume_getPopularSongsSubscription_fail_errorIsNotHttpException_showPopularSongsError() {
        // Given
        val mockPopularSongsSubscription: Disposable = mock()
        presenter.setSubscriptionFirstSection(popularSongsSubscription = mockPopularSongsSubscription)
        presenter.setObservableFirstSection(popularSongsObservable = Single.error(Throwable("error")))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showPopularSongsError()
    }

    @Test
    fun onResume_getLatestSongsSubscription_success_showLatestSongs() {
        // Given
        val mockLatestSongsSubscription: Disposable = mock()
        presenter.setSubscriptionFirstSection(latestSongsSubscription = mockLatestSongsSubscription)
        presenter.setObservableFirstSection(
            latestSongsObservable = Single.just(listOf(MockDataModel.mockTrack))
        )

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showLatestSongs(any())
    }

    @Test
    fun onResume_getLatestSongsSubscription_fail_errorIsHttpException_errorCodeIsResourceNotFound_hideLatestSongs() {
        // Given
        val mockLatestSongsSubscription: Disposable = mock()
        presenter.setSubscriptionFirstSection(latestSongsSubscription = mockLatestSongsSubscription)
        presenter.setObservableFirstSection(
            latestSongsObservable = Single.error(MockDataModel.mockHttpExceptionCodeResourceNotFound)
        )

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).hideLatestSongs()
    }

    @Test
    fun onResume_getLatestSongsSubscription_fail_errorIsHttpException_errorCodeIsNotResourceNotFound_showLatestSongs() {
        // Given
        val mockLatestSongsSubscription: Disposable = mock()
        presenter.setSubscriptionFirstSection(latestSongsSubscription = mockLatestSongsSubscription)
        presenter.setObservableFirstSection(
            latestSongsObservable = Single.error(MockDataModel.mockHttpExceptionCodeUnauthorised)
        )

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showLatestSongsError()
    }

    @Test
    fun onResume_getLatestSongsSubscription_fail_errorIsNotHttpException_showLatestSongs() {
        // Given
        val mockLatestSongsSubscription: Disposable = mock()
        presenter.setSubscriptionFirstSection(latestSongsSubscription = mockLatestSongsSubscription)
        presenter.setObservableFirstSection(
            latestSongsObservable = Single.error(Throwable("error"))
        )

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showLatestSongsError()
    }

    @Test
    fun onResume_getArtistSimilarStationSubscription_success_playArtistMixAndShowPlayArtistMix() {
        // Given
        val mockArtistSimilarStationSubscription: Disposable = mock()
        presenter.setSubscriptionFirstSection(artistSimilarStationSubscription = mockArtistSimilarStationSubscription)
        presenter.setObservableFirstSection(
            artistSimilarStationObservable = Single.just(MockDataModel.mockStation)
        )

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).playArtistMix(any())
        verify(view, times(1)).showPlayArtistMix()
    }

    @Test
    fun onResume_getArtistSimilarStationSubscription_fail_showArtistMixEnabledAndShowArtistSimilarStationError() {
        // Given
        val mockArtistSimilarStationSubscription: Disposable = mock()
        presenter.setSubscriptionFirstSection(artistSimilarStationSubscription = mockArtistSimilarStationSubscription)
        presenter.setObservableFirstSection(
            artistSimilarStationObservable = Single.error(Throwable("error"))
        )

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showArtistMixEnabled()
        verify(view, times(1)).showArtistSimilarStationError()
    }

    @Test
    fun onResume_getIsFollowedSubscription_success_showFollowed() {
        // Given
        val mockIsFollowedSubscription: Disposable = mock()
        presenter.setSubscriptionFirstSection(isFollowedSubscription = mockIsFollowedSubscription)
        presenter.setObservableFirstSection(
            isFollowedObservable = Single.just(true)
        )

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showFollowed(any())
    }

    @Test
    fun onResume_getIsFollowedSubscription_fail_showFollowed() {
        // Given
        val mockIsFollowedSubscription: Disposable = mock()
        presenter.setSubscriptionFirstSection(isFollowedSubscription = mockIsFollowedSubscription)
        presenter.setObservableFirstSection(
            isFollowedObservable = Single.error(Throwable("error"))
        )

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showFollowed(any())
    }

    @Test
    fun onResume_getToggleFollowedSubscription_success_followedIsTrue_showFollowSuccess() {
        // Given
        val mockToggleFollowedSubscription: Disposable = mock()
        presenter.setPrivateData(isFollowed = true)
        presenter.setSubscriptionFirstSection(toggleFollowedSubscription = mockToggleFollowedSubscription)
        presenter.setObservableFirstSection(toggleFollowedObservable = Single.just(Any()))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showFollowSuccess()
    }

    @Test
    fun onResume_getToggleFollowedSubscription_success_followedIsFalse_showUnFollowSuccess() {
        // Given
        val mockToggleFollowedSubscription: Disposable = mock()
        presenter.setPrivateData(isFollowed = false)
        presenter.setSubscriptionFirstSection(toggleFollowedSubscription = mockToggleFollowedSubscription)
        presenter.setObservableFirstSection(toggleFollowedObservable = Single.just(Any()))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showUnFollowSuccess()
    }

    @Test
    fun onResume_getToggleFollowedSubscription_fail_followedIsTrue_showFollowError() {
        // Given
        val mockToggleFollowedSubscription: Disposable = mock()
        presenter.setPrivateData(isFollowed = true)
        presenter.setSubscriptionFirstSection(toggleFollowedSubscription = mockToggleFollowedSubscription)
        presenter.setObservableFirstSection(toggleFollowedObservable = Single.error(Throwable("error")))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showFollowed(any())
        verify(view, times(1)).showFollowError()
    }

    @Test
    fun onResume_getToggleFollowedSubscription_fail_followedIsFalse_showFollowError() {
        // Given
        val mockToggleFollowedSubscription: Disposable = mock()
        presenter.setPrivateData(isFollowed = false)
        presenter.setSubscriptionFirstSection(toggleFollowedSubscription = mockToggleFollowedSubscription)
        presenter.setObservableFirstSection(toggleFollowedObservable = Single.error(Throwable("error")))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showFollowed(any())
        verify(view, times(1)).showUnFollowError()
    }

    @Test
    fun onResume_getSimilarArtistSubscription_success_artistSizeIsMoreThanThree_DMCAEnableIsFalse_showArtistMixEnabled() {
        // Given
        val mockSimilarArtistsSubscription: Disposable = mock()
        val mockArtistList = listOf(
            MockDataModel.mockArtist.copy(id = 1),
            MockDataModel.mockArtist.copy(id = 2),
            MockDataModel.mockArtist.copy(id = 3),
            MockDataModel.mockArtist.copy(id = 4)
        )
        presenter.setSubscriptionSecondSection(similarArtistsSubscription = mockSimilarArtistsSubscription)
        presenter.setObservableSecondSection(similarArtistsObservable = Single.just(mockArtistList))
        whenever(artistFacade.getIsDMCAEnabled()).thenReturn(false)

        // When
        presenter.onResume()

        // Then
        verify(artistFacade, times(1)).getIsDMCAEnabled()
        verify(view, times(1)).showSimilarArtists(any())
        verify(view, times(1)).showArtistMixEnabled()
    }

    @Test
    fun onResume_getSimilarArtistSubscription_success_artistSizeIsMoreThanThree_DMCAEnableIsTrue_showArtistMixDisabled() {
        // Given
        val mockSimilarArtistsSubscription: Disposable = mock()
        val mockArtistList = listOf(
            MockDataModel.mockArtist.copy(id = 1),
            MockDataModel.mockArtist.copy(id = 2),
            MockDataModel.mockArtist.copy(id = 3),
            MockDataModel.mockArtist.copy(id = 4)
        )
        presenter.setSubscriptionSecondSection(similarArtistsSubscription = mockSimilarArtistsSubscription)
        presenter.setObservableSecondSection(similarArtistsObservable = Single.just(mockArtistList))
        whenever(artistFacade.getIsDMCAEnabled()).thenReturn(true)

        // When
        presenter.onResume()

        // Then
        verify(artistFacade, times(1)).getIsDMCAEnabled()
        verify(view, times(1)).showSimilarArtists(any())
        verify(view, times(1)).showArtistMixDisabled()
    }

    @Test
    fun onResume_getSimilarArtistSubscription_success_artistSizeIsLessThanThree_DMCAEnableIsFalse_showArtistMixDisabled() {
        // Given
        val mockSimilarArtistsSubscription: Disposable = mock()
        val mockArtistList = listOf(
            MockDataModel.mockArtist.copy(id = 1),
            MockDataModel.mockArtist.copy(id = 2)
        )
        presenter.setSubscriptionSecondSection(similarArtistsSubscription = mockSimilarArtistsSubscription)
        presenter.setObservableSecondSection(similarArtistsObservable = Single.just(mockArtistList))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showSimilarArtists(any())
        verify(view, times(1)).showArtistMixDisabled()
    }

    @Test
    fun onResume_getSimilarArtistSubscription_fail_errorIsHttpException_errorCodeIsResourceNotFound_hideSimilarArtists() {
        // Given
        val mockSimilarArtistsSubscription: Disposable = mock()
        presenter.setSubscriptionSecondSection(similarArtistsSubscription = mockSimilarArtistsSubscription)
        presenter.setObservableSecondSection(
            similarArtistsObservable = Single.error(MockDataModel.mockHttpExceptionCodeResourceNotFound)
        )

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).hideSimilarArtists()
        verify(view, times(1)).showArtistMixDisabled()
    }

    @Test
    fun onResume_getSimilarArtistSubscription_fail_errorIsHttpException_errorCodeIsNotResourceNotFound_showSimilarArtistsError() {
        // Given
        val mockSimilarArtistsSubscription: Disposable = mock()
        presenter.setSubscriptionSecondSection(similarArtistsSubscription = mockSimilarArtistsSubscription)
        presenter.setObservableSecondSection(
            similarArtistsObservable = Single.error(MockDataModel.mockHttpExceptionCodeUnauthorised)
        )

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showSimilarArtistsError()
        verify(view, times(1)).showArtistMixDisabled()
    }

    @Test
    fun onResume_getSimilarArtistSubscription_fail_errorIsNotHttpException_showSimilarArtistsError() {
        // Given
        val mockSimilarArtistsSubscription: Disposable = mock()
        presenter.setSubscriptionSecondSection(similarArtistsSubscription = mockSimilarArtistsSubscription)
        presenter.setObservableSecondSection(similarArtistsObservable = Single.error(Throwable("error")))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showSimilarArtistsError()
        verify(view, times(1)).showArtistMixDisabled()
    }

    @Test
    fun onResume_getAppearsInSubscription_success_showStationsAppearsIn() {
        // Given
        val mockAppearsInSubscription: Disposable = mock()
        presenter.setSubscriptionSecondSection(appearsInSubscription = mockAppearsInSubscription)
        presenter.setObservableSecondSection(appearsInObservable = Single.just(listOf(MockDataModel.mockStation)))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showStationsAppearsIn(any())
    }

    @Test
    fun onResume_getAppearsInSubscription_fail_errorIsHttpException_errorCodeIsResourceNotFound_hideStationAppearsIn() {
        // Given
        val mockAppearsInSubscription: Disposable = mock()
        presenter.setSubscriptionSecondSection(appearsInSubscription = mockAppearsInSubscription)
        presenter.setObservableSecondSection(
            appearsInObservable = Single.error(MockDataModel.mockHttpExceptionCodeResourceNotFound)
        )

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).hideStationsAppearsIn()
    }

    @Test
    fun onResume_getAppearsInSubscription_fail_errorIsHttpException_errorCodeIsNotResourceNotFound_showStationsAppearsInError() {
        // Given
        val mockAppearsInSubscription: Disposable = mock()
        presenter.setSubscriptionSecondSection(appearsInSubscription = mockAppearsInSubscription)
        presenter.setObservableSecondSection(
            appearsInObservable = Single.error(MockDataModel.mockHttpExceptionCodeUnauthorised)
        )

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showStationsAppearsInError()
    }

    @Test
    fun onResume_getAppearsInSubscription_fail_errorIsNotHttpException_showStationsAppearsInError() {
        // Given
        val mockAppearsInSubscription: Disposable = mock()
        presenter.setSubscriptionSecondSection(appearsInSubscription = mockAppearsInSubscription)
        presenter.setObservableSecondSection(appearsInObservable = Single.error(Throwable("error")))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showStationsAppearsInError()
    }

    @Test
    fun onResume_getVideoAppearsInSubscription_success_showVideoAppearsIn() {
        // Given
        val mockVideoAppearsInSubscription: Disposable = mock()
        presenter.setSubscriptionSecondSection(videoAppearsInSubscription = mockVideoAppearsInSubscription)
        presenter.setObservableSecondSection(
            videoAppearsInObservable = Single.just(listOf(MockDataModel.mockTrack))
        )

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showVideoAppearsIn(any(), any())
    }

    @Test
    fun onResume_getVideoAppearsInSubscription_fail_errorIsHttpException_errorCodeIsResourceNotFound_hideVideoAppearsIn() {
        // Given
        val mockVideoAppearsInSubscription: Disposable = mock()
        presenter.setSubscriptionSecondSection(videoAppearsInSubscription = mockVideoAppearsInSubscription)
        presenter.setObservableSecondSection(
            videoAppearsInObservable = Single.error(MockDataModel.mockHttpExceptionCodeResourceNotFound)
        )

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).hideVideoAppearsIn()
    }

    @Test
    fun onResume_getVideoAppearsInSubscription_fail_errorIsHttpException_errorCodeIsNotResourceNotFound_showVideoAppearsInError() {
        // Given
        val mockVideoAppearsInSubscription: Disposable = mock()
        presenter.setSubscriptionSecondSection(videoAppearsInSubscription = mockVideoAppearsInSubscription)
        presenter.setObservableSecondSection(
            videoAppearsInObservable = Single.error(MockDataModel.mockHttpExceptionCodeUnauthorised)
        )

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showVideoAppearsInError()
    }

    @Test
    fun onResume_getVideoAppearsInSubscription_fail_errorIsNotHttpException_showVideoAppearsInError() {
        // Given
        val mockVideoAppearsInSubscription: Disposable = mock()
        presenter.setSubscriptionSecondSection(videoAppearsInSubscription = mockVideoAppearsInSubscription)
        presenter.setObservableSecondSection(videoAppearsInObservable = Single.error(Throwable("error")))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showVideoAppearsInError()
    }

    @Test
    fun onResume_getAlbumsSubscription_success_showAlbums() {
        // Given
        val mockAlbumsSubscription: Disposable = mock()
        presenter.setSubscriptionSecondSection(albumsSubscription = mockAlbumsSubscription)
        presenter.setObservableSecondSection(albumsObservable = Single.just(listOf(MockDataModel.mockAlbum)))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showAlbums(any(), any())
    }

    @Test
    fun onResume_getAlbumsSubscription_fail_errorIsHttpException_errorCodeIsResourceNotFound_albumIsEmpty_hideAlbums() {
        // Given
        val mockAlbumsSubscription: Disposable = mock()
        presenter.setPrivateDataSecondSection(albums = mutableListOf())
        presenter.setSubscriptionSecondSection(albumsSubscription = mockAlbumsSubscription)
        presenter.setObservableSecondSection(
            albumsObservable = Single.error(MockDataModel.mockHttpExceptionCodeResourceNotFound)
        )

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).hideAlbums()
    }

    @Test
    fun onResume_getAlbumsSubscription_fail_errorIsHttpException_errorCodeIsResourceNotFound_albumIsNotEmpty_showAlbums() {
        // Given
        val mockAlbumsSubscription: Disposable = mock()
        presenter.setPrivateDataSecondSection(albums = mutableListOf(MockDataModel.mockAlbum))
        presenter.setSubscriptionSecondSection(albumsSubscription = mockAlbumsSubscription)
        presenter.setObservableSecondSection(
            albumsObservable = Single.error(MockDataModel.mockHttpExceptionCodeResourceNotFound)
        )

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showAlbums(any(), any())
    }

    @Test
    fun onResume_getAlbumsSubscription_fail_errorIsHttpException_errorCodeIsNotResourceNotFound_showAlbumsError() {
        // Given
        val mockAlbumsSubscription: Disposable = mock()
        presenter.setSubscriptionSecondSection(albumsSubscription = mockAlbumsSubscription)
        presenter.setObservableSecondSection(
            albumsObservable = Single.error(MockDataModel.mockHttpExceptionCodeUnauthorised)
        )

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showAlbumsError()
    }

    @Test
    fun onResume_getAlbumsSubscription_fail_errorIsNotHttpException_showAlbumsError() {
        // Given
        val mockAlbumsSubscription: Disposable = mock()
        presenter.setSubscriptionSecondSection(albumsSubscription = mockAlbumsSubscription)
        presenter.setObservableSecondSection(albumsObservable = Single.error(Throwable("error")))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showAlbumsError()
    }

    @Test
    fun onResume_getAlbumsAppearsOnSubscription_success_showAlbumsAppearsOn() {
        // Given
        val mockAlbumsAppearsOnSubscription: Disposable = mock()
        presenter.setSubscriptionSecondSection(albumsAppearsOnSubscription = mockAlbumsAppearsOnSubscription)
        presenter.setObservableSecondSection(
            albumsAppearsOnObservable = Single.just(listOf(MockDataModel.mockAlbum))
        )

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showAlbumsAppearsOn(any())
    }

    @Test
    fun onResume_getAlbumsAppearsOnSubscription_fail_errorIsHttpException_errorCodeIsResourceNotFound_hideAlbumsAppearsOn() {
        // Given
        val mockAlbumsAppearsOnSubscription: Disposable = mock()
        presenter.setSubscriptionSecondSection(albumsAppearsOnSubscription = mockAlbumsAppearsOnSubscription)
        presenter.setObservableSecondSection(
            albumsAppearsOnObservable = Single.error(MockDataModel.mockHttpExceptionCodeResourceNotFound)
        )

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).hideAlbumsAppearsOn()
    }

    @Test
    fun onResume_getAlbumsAppearsOnSubscription_fail_errorIsHttpException_errorCodeIsNotResourceNotFound_showAlbumsAppearsOnError() {
        // Given
        val mockAlbumsAppearsOnSubscription: Disposable = mock()
        presenter.setSubscriptionSecondSection(albumsAppearsOnSubscription = mockAlbumsAppearsOnSubscription)
        presenter.setObservableSecondSection(
            albumsAppearsOnObservable = Single.error(MockDataModel.mockHttpExceptionCodeUnauthorised)
        )

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showAlbumsAppearsOnError()
    }

    @Test
    fun onResume_getAlbumsAppearsOnSubscription_fail_errorIsNotHttpException_showAlbumsAppearsOnError() {
        // Given
        val mockAlbumsAppearsOnSubscription: Disposable = mock()
        presenter.setSubscriptionSecondSection(albumsAppearsOnSubscription = mockAlbumsAppearsOnSubscription)
        presenter.setObservableSecondSection(albumsAppearsOnObservable = Single.error(Throwable("error")))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showAlbumsAppearsOnError()
    }

    @Test
    fun onResume_getClearArtistVotesSubscription_success_showArtistVotesCleared() {
        // Given
        val mockClearArtistVotesSubscription: Disposable = mock()
        presenter.setSubscriptionSecondSection(clearArtistVotesSubscription = mockClearArtistVotesSubscription)
        presenter.setObservableSecondSection(clearArtistVotesObservable = Single.just(Any()))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showArtistVotesCleared()
    }

    @Test
    fun onResume_getClearArtistVotesSubscription_fail_errorIsHttpException_errorCodeIsResourceNotFound_showArtistVotesCleared() {
        // Given
        val mockClearArtistVotesSubscription: Disposable = mock()
        presenter.setSubscriptionSecondSection(clearArtistVotesSubscription = mockClearArtistVotesSubscription)
        presenter.setObservableSecondSection(
            clearArtistVotesObservable = Single.error(MockDataModel.mockHttpExceptionCodeResourceNotFound)
        )

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showArtistVotesCleared()
    }

    @Test
    fun onResume_getClearArtistVotesSubscription_fail_errorIsHttpException_errorCodeIsNotResourceNotFound_showClearArtistVotesError() {
        // Given
        val mockClearArtistVotesSubscription: Disposable = mock()
        presenter.setSubscriptionSecondSection(clearArtistVotesSubscription = mockClearArtistVotesSubscription)
        presenter.setObservableSecondSection(
            clearArtistVotesObservable = Single.error(MockDataModel.mockHttpExceptionCodeUnauthorised)
        )

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showClearArtistVotesError()
    }

    @Test
    fun onResume_getClearArtistVotesSubscription_fail_errorIsNotHttpException_showClearArtistVotesError() {
        // Given
        val mockClearArtistVotesSubscription: Disposable = mock()
        presenter.setSubscriptionSecondSection(clearArtistVotesSubscription = mockClearArtistVotesSubscription)
        presenter.setObservableSecondSection(clearArtistVotesObservable = Single.error(Throwable("error")))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showClearArtistVotesError()
    }

    @Test
    fun onPlayArtistMix_stationIsNotNull_playArtistMixAndShowPlayArtistMix() {
        // Given
        presenter.setPrivateData(station = MockDataModel.mockStation)

        // When
        presenter.onPlayArtistMix()

        // Then
        verify(view, times(1)).playArtistMix(any())
        verify(view, times(1)).showPlayArtistMix()
    }

    @Test
    fun onPlayArtistMix_stationIsNull_artistSimilarStationObservableIsNull_artistIsNotNull_showLoadingArtistMix() {
        // Given
        presenter.setPrivateData(station = null, artist = MockDataModel.mockArtist)
        presenter.setObservableFirstSection(artistSimilarStationObservable = null)
        whenever(artistFacade.loadArtistAndSimilarStation(any())).thenReturn(
            Single.just(MockDataModel.mockStation)
        )

        // When
        presenter.onPlayArtistMix()

        // Then
        verify(view, times(1)).showLoadingArtistMix()
    }

    @Test
    fun onPlayArtistMix_stationIsNull_artistSimilarStationObservableIsNull_artistIsNull_doNothing() {
        // Given
        presenter.setPrivateData(station = null, artist = null)
        presenter.setObservableFirstSection(artistSimilarStationObservable = null)
        whenever(artistFacade.loadArtistAndSimilarStation(any())).thenReturn(
            Single.just(MockDataModel.mockStation)
        )

        // When
        presenter.onPlayArtistMix()

        // Then
        verify(view, times(0)).showLoadingArtistMix()
    }

    @Test
    fun onPlayArtistMix_stationIsNull_artistSimilarStationObservableIsNotNull_artistIsNull_doNothing() {
        // Given
        presenter.setPrivateData(station = null, artist = null)
        presenter.setObservableFirstSection(
            artistSimilarStationObservable = Single.just(MockDataModel.mockStation)
        )
        whenever(artistFacade.loadArtistAndSimilarStation(any())).thenReturn(
            Single.just(MockDataModel.mockStation)
        )

        // When
        presenter.onPlayArtistMix()

        // Then
        verify(view, times(0)).showLoadingArtistMix()
    }

    @Test
    fun onPlayArtistShuffle_hasArtistShuffleRightIsTrue_playableTracksIsNotEmpty_artistIsNotNull_playArtistSongs() {
        // Given
        val mockTrackValue = mockTrack.copy(allowStream = true)
        presenter.setPrivateData(artist = MockDataModel.mockArtist, tracks = listOf(mockTrackValue))
        whenever(artistFacade.getHasArtistShuffleRight()).thenReturn(true)

        // When
        presenter.onPlayArtistShuffle()

        // Then
        verify(view, times(1)).playArtistSongs(any(), any(), anyOrNull(), any(), any())
    }

    @Test
    fun onPlayArtistShuffle_hasArtistShuffleRightIsTrue_playableTracksIsEmpty_artistIsNotNull_playArtistSongs() {
        // Given
        val mockTrackValue = mockTrack.copy(allowStream = false)
        presenter.setPrivateData(artist = MockDataModel.mockArtist, tracks = listOf(mockTrackValue))
        whenever(artistFacade.getHasArtistShuffleRight()).thenReturn(true)

        // When
        presenter.onPlayArtistShuffle()

        // Then
        verify(view, times(0)).playArtistSongs(any(), any(), anyOrNull(), any(), any())
    }

    @Test
    fun onPlayArtistShuffle_hasArtistShuffleRightIsTrue_playableTracksIsNotEmpty_artistIsNull_doNothing() {
        // Given
        val mockTrackValue = mockTrack.copy(allowStream = true)
        presenter.setPrivateData(artist = null, tracks = listOf(mockTrackValue))
        whenever(artistFacade.getHasArtistShuffleRight()).thenReturn(true)

        // When
        presenter.onPlayArtistShuffle()

        // Then
        verify(view, times(0)).playArtistSongs(any(), any(), anyOrNull(), any(), any())
    }

    @Test
    fun onPlayArtistShuffle_hasArtistShuffleRightIsTrue_tracksIsNull_doNothing() {
        // Given
        presenter.setPrivateData(tracks = null)
        whenever(artistFacade.getHasArtistShuffleRight()).thenReturn(true)

        // When
        presenter.onPlayArtistShuffle()

        // Then
        verify(view, times(0)).playArtistSongs(any(), any(), anyOrNull(), any(), any())
    }

    @Test
    fun onPlayArtistShuffle_hasArtistShuffleRightIsFalse_showUpgradeDialog() {
        // Given
        whenever(artistFacade.getHasArtistShuffleRight()).thenReturn(false)

        // When
        presenter.onPlayArtistShuffle()

        // Then
        verify(view, times(1)).showUpgradeDialog()
    }

    @Test
    fun onPlayArtistTrendingTrack_hasArtistShuffleRightIsTrue_playableTracksIsNotEmpty_artistIsNotNull_playArtistSongs() {
        // Given
        val mockTrackValue = mockTrack.copy(allowStream = true)
        presenter.setPrivateData(artist = MockDataModel.mockArtist, tracks = listOf(mockTrackValue))
        whenever(artistFacade.getHasArtistShuffleRight()).thenReturn(true)

        // When
        presenter.onPlayArtistTrendingTrack(1)

        // Then
        verify(view, times(1)).playArtistSongs(any(), any(), anyOrNull(), any(), any())
    }

    @Test
    fun onPlayArtistTrendingTrack_hasArtistShuffleRightIsTrue_playableTracksIsEmpty_artistIsNotNull_playArtistSongs() {
        // Given
        val mockTrackValue = mockTrack.copy(allowStream = false)
        presenter.setPrivateData(artist = MockDataModel.mockArtist, tracks = listOf(mockTrackValue))
        whenever(artistFacade.getHasArtistShuffleRight()).thenReturn(true)

        // When
        presenter.onPlayArtistTrendingTrack(1)

        // Then
        verify(view, times(0)).playArtistSongs(any(), any(), anyOrNull(), any(), any())
    }

    @Test
    fun onPlayArtistTrendingTrack_hasArtistShuffleRightIsTrue_playableTracksIsNotEmpty_artistIsNull_doNothing() {
        // Given
        val mockTrackValue = mockTrack.copy(allowStream = true)
        presenter.setPrivateData(artist = null, tracks = listOf(mockTrackValue))
        whenever(artistFacade.getHasArtistShuffleRight()).thenReturn(true)

        // When
        presenter.onPlayArtistTrendingTrack(1)

        // Then
        verify(view, times(0)).playArtistSongs(any(), any(), anyOrNull(), any(), any())
    }

    @Test
    fun onPlayArtistTrendingTrack_hasArtistShuffleRightIsTrue_tracksIsNull_doNothing() {
        // Given
        presenter.setPrivateData(tracks = null)
        whenever(artistFacade.getHasArtistShuffleRight()).thenReturn(true)

        // When
        presenter.onPlayArtistTrendingTrack(1)

        // Then
        verify(view, times(0)).playArtistSongs(any(), any(), anyOrNull(), any(), any())
    }

    @Test
    fun onPlayArtistTrendingTrack_hasArtistShuffleRightIsFalse_showUpgradeDialog() {
        // Given
        whenever(artistFacade.getHasArtistShuffleRight()).thenReturn(false)

        // When
        presenter.onPlayArtistTrendingTrack(1)

        // Then
        verify(view, times(1)).showUpgradeDialog()
    }

    @Test
    fun onArtistLatestTrackSelected_hasArtistShuffleRightIsTrue_playableTracksIsNotEmpty_trackIsInPlayableTracks_artistIsNotNull_playArtistSongs() {
        // Given
        val mockTrackValue = mockTrack.copy(allowStream = true)
        presenter.setPrivateData(
            latestTracks = listOf(mockTrackValue),
            artist = MockDataModel.mockArtist
        )
        whenever(artistFacade.getHasArtistShuffleRight()).thenReturn(true)

        // When
        presenter.onArtistLatestTrackSelected(mockTrackValue)

        // Then
        verify(view, times(1)).playArtistSongs(any(), any(), anyOrNull(), any(), any())
    }

    @Test
    fun onArtistLatestTrackSelected_hasArtistShuffleRightIsTrue_playableTracksIsNotEmpty_trackIsInPlayableTracks_artistIsNull_doNothing() {
        // Given
        val mockTrackValue = mockTrack.copy(allowStream = true)
        presenter.setPrivateData(latestTracks = listOf(mockTrackValue), artist = null)
        whenever(artistFacade.getHasArtistShuffleRight()).thenReturn(true)

        // When
        presenter.onArtistLatestTrackSelected(mockTrackValue)

        // Then
        verify(view, times(0)).playArtistSongs(any(), any(), anyOrNull(), any(), any())
    }

    @Test
    fun onArtistLatestTrackSelected_hasArtistShuffleRightIsTrue_playableTracksIsNotEmpty_trackIsNotInPlayableTracks_doNothing() {
        // Given
        val mockTrackValue = mockTrack.copy(allowStream = true)
        presenter.setPrivateData(latestTracks = listOf(mockTrackValue))
        whenever(artistFacade.getHasArtistShuffleRight()).thenReturn(true)

        // When
        presenter.onArtistLatestTrackSelected(mockTrack)

        // Then
        verify(view, times(0)).playArtistSongs(any(), any(), anyOrNull(), any(), any())
    }

    @Test
    fun onArtistLatestTrackSelected_hasArtistShuffleRightIsTrue_lastTracksIsNull_doNothing() {
        // Given
        presenter.setPrivateData(latestTracks = null)
        whenever(artistFacade.getHasArtistShuffleRight()).thenReturn(true)

        // When
        presenter.onArtistLatestTrackSelected(mockTrack)

        // Then
        verify(view, times(0)).playArtistSongs(any(), any(), anyOrNull(), any(), any())
    }

    @Test
    fun onArtistLatestTrackSelected_hasArtistShuffleRightIsFalse_showUpgradeDialog() {
        // Given
        whenever(artistFacade.getHasArtistShuffleRight()).thenReturn(false)

        // When
        presenter.onArtistLatestTrackSelected(mockTrack)

        // Then
        verify(view, times(1)).showUpgradeDialog()
    }

    @Test
    fun onStationSelected_navigateToStation() {
        // When
        presenter.onStationSelected(MockDataModel.mockStation)

        // Then
        verify(router, times(1)).navigateToStation(MockDataModel.mockStation)
    }

    @Test
    fun onAlbumSelected_navigateToStation() {
        // When
        presenter.onAlbumSelected(mockAlbum)

        // Then
        verify(router, times(1)).navigateToAlbum(mockAlbum)
    }

    @Test
    fun onArtistSelected_navigateToStation() {
        // When
        presenter.onArtistSelected(MockDataModel.mockArtist)

        // Then
        verify(router, times(1)).navigateToArtist(MockDataModel.mockArtist)
    }

    @Test
    fun onVideoSelected_navigateToStation() {
        // When
        presenter.onVideoSelected(mockTrack)

        // Then
        verify(view, times(1)).playVideo(mockTrack)
    }

    @Test
    fun onImageSelected_artistIsNotNull_imageIsNotNull_showEnlargedImage() {
        // Given
        val mockArtistValue = MockDataModel.mockArtist.copy(image = "image")
        presenter.setPrivateData(artist = mockArtistValue)

        // When
        presenter.onImageSelected()

        // Then
        verify(view, times(1)).showEnlargedImage(any())
    }

    @Test
    fun onImageSelected_artistIsNotNull_imageIsNull_doNothing() {
        // Given
        val mockArtistValue = MockDataModel.mockArtist.copy(image = null)
        presenter.setPrivateData(artist = mockArtistValue)

        // When
        presenter.onImageSelected()

        // Then
        verify(view, times(0)).showEnlargedImage(any())
    }

    @Test
    fun onImageSelected_artistIsNull_doNothing() {
        // Given
        presenter.setPrivateData(artist = null)

        // When
        presenter.onImageSelected()

        // Then
        verify(view, times(0)).showEnlargedImage(any())
    }

    @Test
    fun onTrendingSeeAllSelected_artistIsNotNull_navigateToProductList() {
        // Given
        presenter.setPrivateData(artist = MockDataModel.mockArtist)

        // When
        presenter.onTrendingSeeAllSelected()

        // Then
        verify(router, times(1)).navigateToProductList(any(), any())
    }

    @Test
    fun onTrendingSeeAllSelected_artistIsNull_doNothing() {
        // Given
        presenter.setPrivateData(artist = null)

        // When
        presenter.onTrendingSeeAllSelected()

        // Then
        verify(router, times(0)).navigateToProductList(any(), any())
    }

    @Test
    fun onLatestSeeAllSelected_artistIsNotNull_navigateToProductList() {
        // Given
        presenter.setPrivateData(artist = MockDataModel.mockArtist)

        // When
        presenter.onLatestSeeAllSelected()

        // Then
        verify(router, times(1)).navigateToProductList(any(), any())
    }

    @Test
    fun onLatestSeeAllSelected_artistIsNull_doNothing() {
        // Given
        presenter.setPrivateData(artist = null)

        // When
        presenter.onLatestSeeAllSelected()

        // Then
        verify(router, times(0)).navigateToProductList(any(), any())
    }

    @Test
    fun onVideoAppearsInSeeAllSelected_artistIsNotNull_navigateToProductList() {
        // Given
        presenter.setPrivateData(artist = MockDataModel.mockArtist)

        // When
        presenter.onVideoAppearsInSeeAllSelected()

        // Then
        verify(router, times(1)).navigateToProductList(any(), any())
    }

    @Test
    fun onVideoAppearsInSeeAllSelected_artistIsNull_doNothing() {
        // Given
        presenter.setPrivateData(artist = null)

        // When
        presenter.onVideoAppearsInSeeAllSelected()

        // Then
        verify(router, times(0)).navigateToProductList(any(), any())
    }

    @Test
    fun onAppearsInSeeAllSelected_artistIsNotNull_navigateToProductList() {
        // Given
        presenter.setPrivateData(artist = MockDataModel.mockArtist)

        // When
        presenter.onAppearsInSeeAllSelected()

        // Then
        verify(router, times(1)).navigateToProductList(any(), any())
    }

    @Test
    fun onAppearsInSeeAllSelected_artistIsNull_doNothing() {
        // Given
        presenter.setPrivateData(artist = null)

        // When
        presenter.onAppearsInSeeAllSelected()

        // Then
        verify(router, times(0)).navigateToProductList(any(), any())
    }

    @Test
    fun onAlbumsSeeAllSelected_artistIsNotNull_navigateToProductList() {
        // Given
        presenter.setPrivateData(artist = MockDataModel.mockArtist)

        // When
        presenter.onAlbumsSeeAllSelected()

        // Then
        verify(router, times(1)).navigateToProductList(any(), any())
    }

    @Test
    fun onAlbumsSeeAllSelected_artistIsNull_doNothing() {
        // Given
        presenter.setPrivateData(artist = null)

        // When
        presenter.onAlbumsSeeAllSelected()

        // Then
        verify(router, times(0)).navigateToProductList(any(), any())
    }

    @Test
    fun onAppearsOnAlbumsSeeAllSelected_artistIsNotNull_navigateToProductList() {
        // Given
        presenter.setPrivateData(artist = MockDataModel.mockArtist)

        // When
        presenter.onAppearsOnAlbumsSeeAllSelected()

        // Then
        verify(router, times(1)).navigateToProductList(any(), any())
    }

    @Test
    fun onAppearsOnAlbumsSeeAllSelected_artistIsNull_doNothing() {
        // Given
        presenter.setPrivateData(artist = null)

        // When
        presenter.onAppearsOnAlbumsSeeAllSelected()

        // Then
        verify(router, times(0)).navigateToProductList(any(), any())
    }

    @Test
    fun onSimilarSeeAllSelected_artistIsNotNull_navigateToProductList() {
        // Given
        presenter.setPrivateData(artist = MockDataModel.mockArtist)

        // When
        presenter.onSimilarSeeAllSelected()

        // Then
        verify(router, times(1)).navigateToProductList(any(), any())
    }

    @Test
    fun onSimilarSeeAllSelected_artistIsNull_doNothing() {
        // Given
        presenter.setPrivateData(artist = null)

        // When
        presenter.onSimilarSeeAllSelected()

        // Then
        verify(router, times(0)).navigateToProductList(any(), any())
    }

    @Test
    fun onRetryPopularSongs_artistIsNotNull_showPopularSongsLoadingAndLoadPopularSongs() {
        // Given
        presenter.setPrivateData(artist = MockDataModel.mockArtist)
        whenever(artistFacade.loadPopularSongs(any())).thenReturn(Single.just(listOf(mockTrack)))

        // When
        presenter.onRetryPopularSongs()

        // Then
        verify(view, times(1)).showPopularSongsLoading()
        verify(artistFacade, times(1)).loadPopularSongs(any())
    }

    @Test
    fun onRetryPopularSongs_artistIsNull_doNothing() {
        // Given
        presenter.setPrivateData(artist = null)

        // When
        presenter.onRetryLatestSongs()

        // Then
        verify(view, times(0)).showPopularSongsLoading()
        verify(artistFacade, times(0)).loadPopularSongs(any())
    }

    @Test
    fun onRetryLatestSongs_artistIsNotNull_showPopularSongsLoadingAndLoadLatestSongs() {
        // Given
        presenter.setPrivateData(artist = MockDataModel.mockArtist)
        whenever(artistFacade.loadLatestSongs(any())).thenReturn(Single.just(listOf(mockTrack)))

        // When
        presenter.onRetryLatestSongs()

        // Then
        verify(view, times(1)).showLatestSongsLoading()
        verify(artistFacade, times(1)).loadLatestSongs(any())
    }

    @Test
    fun onRetryLatestSongs_artistIsNull_doNothing() {
        // Given
        presenter.setPrivateData(artist = null)

        // When
        presenter.onRetryPopularSongs()

        // Then
        verify(view, times(0)).showLatestSongsLoading()
        verify(artistFacade, times(0)).loadLatestSongs(any())
    }

    @Test
    fun onRetryStationsAppearsIn_artistIsNotNull_showPopularSongsLoadingAndLoadStationsAppearsIn() {
        // Given
        presenter.setPrivateData(artist = MockDataModel.mockArtist)
        whenever(artistFacade.loadStationsAppearsIn(any())).thenReturn(
            Single.just(listOf(MockDataModel.mockStation))
        )

        // When
        presenter.onRetryStationsAppearsIn()

        // Then
        verify(view, times(1)).showStationsAppearsInLoading()
        verify(artistFacade, times(1)).loadStationsAppearsIn(any())
    }

    @Test
    fun onRetryStationsAppearsIn_artistIsNull_doNothing() {
        // Given
        presenter.setPrivateData(artist = null)

        // When
        presenter.onRetryStationsAppearsIn()

        // Then
        verify(view, times(0)).showStationsAppearsInLoading()
        verify(artistFacade, times(0)).loadStationsAppearsIn(any())
    }

    @Test
    fun onRetryVideosAppearsIn_artistIsNotNull_showPopularSongsLoadingAndLoadStationsAppearsIn() {
        // Given
        presenter.setPrivateData(artist = MockDataModel.mockArtist)
        whenever(artistFacade.getVideoAppearsIn(any(), any(), any(), anyOrNull())).thenReturn(
            Single.just(listOf(mockTrack))
        )

        // When
        presenter.onRetryVideosAppearsIn()

        // Then
        verify(view, times(1)).showVideoAppearsInLoading()
        verify(artistFacade, times(1)).getVideoAppearsIn(any(), any(), any(), anyOrNull())
    }

    @Test
    fun onRetryVideosAppearsIn_artistIsNull_doNothing() {
        // Given
        presenter.setPrivateData(artist = null)

        // When
        presenter.onRetryVideosAppearsIn()

        // Then
        verify(view, times(0)).showVideoAppearsInLoading()
        verify(artistFacade, times(0)).getVideoAppearsIn(any(), any(), any(), anyOrNull())
    }

    @Test
    fun onRetrySimilarArtists_artistIsNotNull_showPopularSongsLoadingAndLoadSimilarArtists() {
        // Given
        presenter.setPrivateData(artist = MockDataModel.mockArtist)
        whenever(artistFacade.loadSimilarArtists(any())).thenReturn(
            Single.just(listOf(MockDataModel.mockArtist))
        )

        // When
        presenter.onRetrySimilarArtists()

        // Then
        verify(view, times(1)).showSimilarArtistsLoading()
        verify(artistFacade, times(1)).loadSimilarArtists(any())
    }

    @Test
    fun onRetrySimilarArtists_artistIsNull_doNothing() {
        // Given
        presenter.setPrivateData(artist = null)

        // When
        presenter.onRetrySimilarArtists()

        // Then
        verify(view, times(0)).showSimilarArtistsLoading()
        verify(artistFacade, times(0)).loadSimilarArtists(any())
    }

    @Test
    fun onRetryAlbums_artistIsNotNull_showPopularSongsLoadingAndLoadArtistAlbums() {
        // Given
        presenter.setPrivateData(artist = MockDataModel.mockArtist)
        whenever(artistFacade.loadArtistAlbums(any(), any(), any(), any())).thenReturn(
            Single.just(listOf(mockAlbum))
        )

        // When
        presenter.onRetryAlbums()

        // Then
        verify(view, times(1)).showAlbumsLoading()
        verify(artistFacade, times(1)).loadArtistAlbums(any(), any(), any(), any())
    }

    @Test
    fun onRetryAlbums_artistIsNull_doNothing() {
        // Given
        presenter.setPrivateData(artist = null)

        // When
        presenter.onRetryAlbums()

        // Then
        verify(view, times(0)).showAlbumsLoading()
        verify(artistFacade, times(0)).loadArtistAlbums(any(), any(), any(), any())
    }

    @Test
    fun onRetryAlbumsAppearsOn_artistIsNotNull_showPopularSongsLoadingAndLoadArtistAppeasOn() {
        // Given
        presenter.setPrivateData(artist = MockDataModel.mockArtist)
        whenever(artistFacade.loadArtistAppearsOn(any(), any())).thenReturn(
            Single.just(listOf(mockAlbum))
        )

        // When
        presenter.onRetryAlbumsAppearsOn()

        // Then
        verify(view, times(1)).showAlbumsAppearsOnLoading()
        verify(artistFacade, times(1)).loadArtistAppearsOn(any(), any())
    }

    @Test
    fun onRetryAlbumsAppearsOn_artistIsNull_doNothing() {
        // Given
        presenter.setPrivateData(artist = null)

        // When
        presenter.onRetryAlbumsAppearsOn()

        // Then
        verify(view, times(0)).showAlbumsAppearsOnLoading()
        verify(artistFacade, times(0)).loadArtistAppearsOn(any(), any())
    }

    @Test
    fun onLoadMoreAlbums_artistIsNotNull_loadArtistAlbums() {
        // Given
        presenter.setPrivateData(artist = MockDataModel.mockArtist)
        whenever(artistFacade.loadArtistAlbums(any(), any(), any(), any())).thenReturn(
            Single.just(listOf(mockAlbum))
        )

        // When
        presenter.onLoadMoreAlbums()

        // Then
        verify(artistFacade, times(1)).loadArtistAlbums(any(), any(), any(), any())
    }

    @Test
    fun onLoadMoreAlbums_artistIsNull_doNothing() {
        // Given
        presenter.setPrivateData(artist = null)

        // When
        presenter.onLoadMoreAlbums()

        // Then
        verify(artistFacade, times(0)).loadArtistAlbums(any(), any(), any(), any())
    }

    @Test
    fun onLoadMoreVideos_artistIsNotNull_getVideoAppearsInIsCalled() {
        // Given
        presenter.setPrivateData(artist = MockDataModel.mockArtist)
        whenever(artistFacade.getVideoAppearsIn(any(), any(), any(), anyOrNull())).thenReturn(
            Single.just(listOf(mockTrack))
        )

        // When
        presenter.onLoadMoreVideos()

        // Then
        verify(artistFacade, times(1)).getVideoAppearsIn(any(), any(), any(), anyOrNull())
    }

    @Test
    fun onLoadMoreVideos_artistIsNull_doNothing() {
        // Given
        presenter.setPrivateData(artist = null)

        // When
        presenter.onLoadMoreVideos()

        // Then
        verify(artistFacade, times(0)).getVideoAppearsIn(any(), any(), any(), anyOrNull())
    }

    @Test
    fun onShowMoreOptions_artistIsNotNull_showMoreOptions() {
        // Given
        presenter.setPrivateData(artist = MockDataModel.mockArtist)

        // When
        presenter.onShowMoreOptions()

        // Then
        verify(view, times(1)).showMoreOptions(any())
    }

    @Test
    fun onShowMoreOptions_artistIsNull_doNothing() {
        // Given
        presenter.setPrivateData(artist = null)

        // When
        presenter.onShowMoreOptions()

        // Then
        verify(view, times(0)).showMoreOptions(any())
    }

    @Test
    fun onMoreOptionSelected_selectionIsAddToCollection_artistIsNotNull_toggleFollowedObservableIsNull_returnTrueAndShowFollowed() {
        // Given
        presenter.setPrivateData(artist = MockDataModel.mockArtist)
        presenter.setObservableFirstSection(toggleFollowedObservable = null)
        whenever(artistFacade.toggleFavourite(any())).thenReturn(Single.just(Any()))

        // When
        val result = presenter.onMoreOptionSelected(PickerOptions.ADD_TO_COLLECTION)

        // Then
        assertTrue(result)
        verify(view, times(1)).showFollowed(any())
        verify(artistFacade, times(1)).toggleFavourite(any())
    }

    @Test
    fun onMoreOptionSelected_selectionIsAddToCollection_artistIsNotNull_toggleFollowedObservableIsNotNull_returnTrue() {
        // Given
        presenter.setPrivateData(artist = MockDataModel.mockArtist)
        presenter.setObservableFirstSection(toggleFollowedObservable = Single.just(Any()))
        whenever(artistFacade.toggleFavourite(any())).thenReturn(Single.just(Any()))

        // When
        val result = presenter.onMoreOptionSelected(PickerOptions.ADD_TO_COLLECTION)

        // Then
        assertTrue(result)
        verify(view, times(0)).showFollowed(any())
        verify(artistFacade, times(0)).toggleFavourite(any())
    }

    @Test
    fun onMoreOptionSelected_selectionIsAddToCollection_artistIsNull_returnTrue() {
        // Given
        presenter.setPrivateData(artist = null)
        whenever(artistFacade.toggleFavourite(any())).thenReturn(Single.just(Any()))

        // When
        val result = presenter.onMoreOptionSelected(PickerOptions.ADD_TO_COLLECTION)

        // Then
        assertTrue(result)
        verify(view, times(0)).showFollowed(any())
        verify(artistFacade, times(0)).toggleFavourite(any())
    }

    @Test
    fun onMoreOptionSelected_selectionIsRemoveFromCollection_artistIsNotNull_toggleFollowedObservableIsNull_returnTrueAndShowFollowed() {
        // Given
        presenter.setPrivateData(artist = MockDataModel.mockArtist)
        presenter.setObservableFirstSection(toggleFollowedObservable = null)
        whenever(artistFacade.toggleFavourite(any())).thenReturn(Single.just(Any()))

        // When
        val result = presenter.onMoreOptionSelected(PickerOptions.REMOVE_FROM_COLLECTION)

        // Then
        assertTrue(result)
        verify(view, times(1)).showFollowed(any())
        verify(artistFacade, times(1)).toggleFavourite(any())
    }

    @Test
    fun onMoreOptionSelected_selectionIsRemoveFromCollection_artistIsNotNull_toggleFollowedObservableIsNotNull_returnTrue() {
        // Given
        presenter.setPrivateData(artist = MockDataModel.mockArtist)
        presenter.setObservableFirstSection(toggleFollowedObservable = Single.just(Any()))
        whenever(artistFacade.toggleFavourite(any())).thenReturn(Single.just(Any()))

        // When
        val result = presenter.onMoreOptionSelected(PickerOptions.REMOVE_FROM_COLLECTION)

        // Then
        assertTrue(result)
        verify(view, times(0)).showFollowed(any())
        verify(artistFacade, times(0)).toggleFavourite(any())
    }

    @Test
    fun onMoreOptionSelected_selectionIsRemoveFromCollection_artistIsNull_returnTrue() {
        // Given
        presenter.setPrivateData(artist = null)
        whenever(artistFacade.toggleFavourite(any())).thenReturn(Single.just(Any()))

        // When
        val result = presenter.onMoreOptionSelected(PickerOptions.REMOVE_FROM_COLLECTION)

        // Then
        assertTrue(result)
        verify(view, times(0)).showFollowed(any())
        verify(artistFacade, times(0)).toggleFavourite(any())
    }

    @Test
    fun onMoreOptionSelected_selectionIsClearVote_artistIsNotNull_clearArtistVotesObservableIsNull_returnTrue() {
        // Given
        presenter.setPrivateData(artist = MockDataModel.mockArtist)
        presenter.setObservableSecondSection(clearArtistVotesObservable = null)
        whenever(artistFacade.clearArtistVotes(any(), any())).thenReturn(Single.just(Any()))

        // When
        val result = presenter.onMoreOptionSelected(PickerOptions.CLEAR_VOTE)

        // Then
        assertTrue(result)
        verify(artistFacade, times(1)).clearArtistVotes(any(), any())
    }

    @Test
    fun onMoreOptionSelected_selectionIsClearVote_artistIsNotNull_clearArtistVotesObservableIsNotNull_returnTrue() {
        // Given
        presenter.setPrivateData(artist = MockDataModel.mockArtist)
        presenter.setObservableSecondSection(clearArtistVotesObservable = Single.just(Any()))

        // When
        val result = presenter.onMoreOptionSelected(PickerOptions.CLEAR_VOTE)

        // Then
        assertTrue(result)
        verify(artistFacade, times(0)).clearArtistVotes(any(), any())
    }

    @Test
    fun onMoreOptionSelected_selectionIsClearVote_artistIsNull_returnTrue() {
        // Given
        presenter.setPrivateData(artist = null)

        // When
        val result = presenter.onMoreOptionSelected(PickerOptions.CLEAR_VOTE)

        // Then
        assertTrue(result)
        verify(artistFacade, times(0)).clearArtistVotes(any(), any())
    }

    @Test
    fun onMoreOptionSelected_selectionIsElse_returnFalse() {
        // When
        val result = presenter.onMoreOptionSelected(PickerOptions.FOLLOW)

        // Then
        assertFalse(result)
    }

    @Test
    fun onMoreTrackOptionSelected_selectionIsAddToQueue_showAddToQueueToast() {
        // When
        presenter.onMoreTrackOptionSelected(PickerOptions.ADD_TO_QUEUE)

        // Then
        verify(view, times(1)).showAddToQueueToast()
    }

    @Test
    fun onMoreTrackOptionSelected_selectionIsElse_doNothing() {
        // When
        presenter.onMoreTrackOptionSelected(PickerOptions.SHOW_ARTIST)

        // Then
        verify(view, times(0)).showAddToQueueToast()
    }

    @Test
    fun onToggleFollow_artistIsNotNull_toggleFollowedObservableIsNull_showFollowed() {
        // Given
        presenter.setPrivateData(artist = MockDataModel.mockArtist)
        presenter.setObservableFirstSection(toggleFollowedObservable = null)
        whenever(artistFacade.toggleFavourite(any())).thenReturn(Single.just(Any()))

        // When
        presenter.onToggleFollow()

        // Then
        verify(view, times(1)).showFollowed(any())
    }

    @Test
    fun onToggleFollow_artistIsNotNull_toggleFollowedObservableIsNotNull_doNothing() {
        // Given
        presenter.setPrivateData(artist = MockDataModel.mockArtist)
        presenter.setObservableFirstSection(toggleFollowedObservable = Single.just(Any()))

        // When
        presenter.onToggleFollow()

        // Then
        verify(view, times(0)).showFollowed(any())
    }

    @Test
    fun onToggleFollow_artistIsNull_doNothing() {
        // Given
        presenter.setPrivateData(artist = null)

        // When
        presenter.onToggleFollow()

        // Then
        verify(view, times(0)).showFollowed(any())
    }

    @Test
    fun onCloseHint_setArtistHintShown() {
        // When
        presenter.onCloseHint()

        // Then
        verify(artistFacade, times(1)).setArtistHintShown()
    }

    @Test
    fun onUpdatePlaybackState_trackIdNotEqualsPlayingTrackId_showCurrentPlayingTrack() {
        // Given
        presenter.setPrivateData(playingTrackId = 1)

        // When
        presenter.onUpdatePlaybackState(100, 200, 100)

        // Then
        verify(view, times(1)).showCurrentPlayingTrack(anyOrNull())
    }

    @Test
    fun onUpdatePlaybackState_trackIdEqualsPlayingTrackId_doNothing() {
        // Given
        presenter.setPrivateData(playingTrackId = 1)

        // When
        presenter.onUpdatePlaybackState(100, 1, 100)

        // Then
        verify(view, times(0)).showCurrentPlayingTrack(anyOrNull())
    }

    @Test
    fun onFavouriteSelect_isFavouritedIsTrue_isSuccessIsTrue_thenShowFollowSuccess() {
        // When
        presenter.onFavouriteSelect(isFavourited = true, isSuccess = true)

        // Then
        verify(view, times(1)).showFollowSuccess()
    }

    @Test
    fun onFavouriteSelect_isFavouritedIsFalse_isSuccessIsTrue_thenShowUnFollowSuccess() {
        // When
        presenter.onFavouriteSelect(isFavourited = false, isSuccess = true)

        // Then
        verify(view, times(1)).showUnFollowSuccess()
    }

    @Test
    fun onFavouriteSelect_isFavouritedIsTrue_isSuccessIsFalse_thenShowFollowError() {
        // When
        presenter.onFavouriteSelect(isFavourited = true, isSuccess = false)

        // Then
        verify(view, times(1)).showFollowError()
    }

    @Test
    fun onFavouriteSelect_isFavouritedIsFalse_isSuccessIsFalse_thenShowUnFollowError() {
        // When
        presenter.onFavouriteSelect(isFavourited = false, isSuccess = false)

        // Then
        verify(view, times(1)).showUnFollowError()
    }
}
