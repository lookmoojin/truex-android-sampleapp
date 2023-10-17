package com.truedigital.features.tuned.presentation.album.presenter

import android.os.Bundle
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.album.model.Release
import com.truedigital.features.tuned.data.artist.model.ArtistInfo
import com.truedigital.features.tuned.data.station.model.Rating
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.domain.facade.album.AlbumFacade
import com.truedigital.features.tuned.presentation.bottomsheet.PickerOptions
import com.truedigital.features.utils.MockDataModel
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mockito
import java.util.Date
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class AlbumPresenterTest {

    private val albumFacade: AlbumFacade = mock()
    private val router: AlbumPresenter.RouterSurface = mock()
    private val view: AlbumPresenter.ViewSurface = mock()
    private val albumSubscription: Disposable = mock()
    private val selectedTrackSubscription: Disposable = mock()
    private val albumTracksSubscription: Disposable = mock()
    private val isFavouritedSubscription: Disposable = mock()
    private val toggleFavouriteSubscription: Disposable = mock()
    private val albumMoreSubscription: Disposable = mock()

    private lateinit var albumPresenter: AlbumPresenter

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

    private val mockArtistInfo = ArtistInfo(
        id = 10,
        name = "name"
    )

    private val mockAlbum = Album(
        id = 1,
        name = "name",
        artists = listOf(mockArtistInfo),
        primaryRelease = null,
        releaseIds = listOf(1, 2)
    )

    private val mockRelease = Release(
        id = 1,
        albumId = 1,
        artists = emptyList(),
        name = "name",
        isExplicit = true,
        numberOfVolumes = 1,
        trackIds = listOf(),
        duration = 1,
        volumes = listOf(),
        image = "image",
        webPath = "webPath",
        copyRight = "copyRight",
        label = null,
        originalReleaseDate = Date(),
        digitalReleaseDate = null,
        physicalReleaseDate = null,
        saleAvailabilityDateTime = null,
        streamAvailabilityDateTime = null,
        allowStream = true,
        allowDownload = true
    )

    @BeforeEach
    fun setUp() {
        albumPresenter = AlbumPresenter(albumFacade)
        albumPresenter.onInject(view, router)
    }

    @Test
    fun onStart_albumNavigationAllowedIsTrue_AlbumIdNotEqualsZero_selectedSongIdEqualZero_loadAlbumAndLoadFavourite() {
        // Given
        val mockBundle = Mockito.mock(Bundle::class.java)
        whenever(mockBundle.getInt(AlbumPresenter.ALBUM_ID_KEY)).thenReturn(10)
        whenever(mockBundle.getInt(AlbumPresenter.SONG_ID_KEY)).thenReturn(0)
        whenever(mockBundle.getBoolean(AlbumPresenter.SONG_ID_KEY)).thenReturn(true)

        whenever(albumFacade.getHasAlbumShuffleRight()).thenReturn(false)
        whenever(albumFacade.getAlbumNavigationAllowed()).thenReturn(true)
        whenever(albumFacade.loadAlbum(any())).thenReturn(Single.just(mockAlbum))
        whenever(albumFacade.loadFavourited(any())).thenReturn(Single.just(true))

        // When
        albumPresenter.onStart(mockBundle)

        // Then
        verify(albumFacade, times(1)).getHasAlbumShuffleRight()
        verify(albumFacade, times(1)).getAlbumNavigationAllowed()
        verify(albumFacade, times(1)).loadAlbum(any())
        verify(albumFacade, times(1)).loadFavourited(any())
        verify(view, times(1)).showNoAlbumShuffleRight()
    }

    @Test
    fun onStart_albumNavigationAllowedIsTrue_AlbumIdEqualsZero_selectedSongIdNotEqualZero_loadAlbumAndLoadFavourite() {
        // Given
        val mockBundle = Mockito.mock(Bundle::class.java)
        whenever(mockBundle.getInt(AlbumPresenter.ALBUM_ID_KEY)).thenReturn(0)
        whenever(mockBundle.getInt(AlbumPresenter.SONG_ID_KEY)).thenReturn(10)
        whenever(mockBundle.getBoolean(AlbumPresenter.SONG_ID_KEY)).thenReturn(true)

        whenever(albumFacade.getHasAlbumShuffleRight()).thenReturn(true)
        whenever(albumFacade.getAlbumNavigationAllowed()).thenReturn(true)
        whenever(albumFacade.loadTrack(any())).thenReturn(Single.just(mockTrack))

        // When
        albumPresenter.onStart(mockBundle)

        // Then
        verify(albumFacade, times(1)).getHasAlbumShuffleRight()
        verify(albumFacade, times(1)).getAlbumNavigationAllowed()
        verify(albumFacade, times(1)).loadTrack(any())
        verify(view, times(0)).showNoAlbumShuffleRight()
    }

    @Test
    fun onStart_albumNavigationAllowedIsTrue_AlbumIdEqualsZero_selectedSongIdNEqualZero_loadAlbumAndLoadFavourite() {
        // Given
        val mockBundle = Mockito.mock(Bundle::class.java)
        whenever(mockBundle.getInt(AlbumPresenter.ALBUM_ID_KEY)).thenReturn(0)
        whenever(mockBundle.getInt(AlbumPresenter.SONG_ID_KEY)).thenReturn(0)
        whenever(mockBundle.getBoolean(AlbumPresenter.SONG_ID_KEY)).thenReturn(true)

        whenever(albumFacade.getHasAlbumShuffleRight()).thenReturn(false)
        whenever(albumFacade.getAlbumNavigationAllowed()).thenReturn(true)

        // When
        albumPresenter.onStart(mockBundle)

        // Then
        verify(albumFacade, times(1)).getHasAlbumShuffleRight()
        verify(albumFacade, times(1)).getAlbumNavigationAllowed()
        verify(view, times(1)).showLoadAlbumError()
        verify(view, times(1)).showNoAlbumShuffleRight()
    }

    @Test
    fun onStart_albumNavigationAllowedIsTrue_argumentIsNull_doNothing() {
        // Given
        whenever(albumFacade.getHasAlbumShuffleRight()).thenReturn(false)
        whenever(albumFacade.getAlbumNavigationAllowed()).thenReturn(true)

        // When
        albumPresenter.onStart(null)

        // Then
        verify(albumFacade, times(1)).getHasAlbumShuffleRight()
        verify(albumFacade, times(1)).getAlbumNavigationAllowed()
        verify(albumFacade, times(0)).loadTrack(any())
        verify(albumFacade, times(0)).loadAlbum(any())
        verify(albumFacade, times(0)).loadFavourited(any())
        verify(view, times(0)).showLoadAlbumError()
        verify(view, times(1)).showNoAlbumShuffleRight()
    }

    @Test
    fun onStart_albumNavigationAllowedIsFalse_showNavigationNotAllowed() {
        // Given
        whenever(albumFacade.getAlbumNavigationAllowed()).thenReturn(false)

        // When
        albumPresenter.onStart(null)

        // Then
        verify(view, times(1)).showNavigationNotAllowed()
    }

    @Test
    fun onResume_getAlbumSubscription_success_primaryReleaseIsNull_initAlbumAndShowOnlineAlbum() {
        // Given
        val mockAlbumSubscription: Disposable = mock()
        val mockAlbumTrackSubscription: Disposable = mock()
        val mockAlbumValue = MockDataModel.mockAlbum.copy(primaryRelease = null)
        val albumResponse = Single.just(mockAlbumValue)
        val albumTrackResponse = Single.just(listOf(MockDataModel.mockTrack))

        albumPresenter.setSubscription(
            albumSubscription = mockAlbumSubscription,
            albumTracksSubscription = mockAlbumTrackSubscription
        )
        albumPresenter.setObservable(
            albumObservable = albumResponse,
            albumTracksObservable = albumTrackResponse
        )

        whenever(albumFacade.loadTracks(any(), any())).thenReturn(albumTrackResponse)

        // When
        albumPresenter.onResume()

        // Then
        verify(view, times(1)).initAlbum(any())
        verify(view, times(1)).showOnlineAlbum(any())
        verify(albumFacade, times(1)).loadTracks(any(), any())
    }

    @Test
    fun onResume_getAlbumSubscription_success_primaryReleaseIsNotNull_initAlbumAndShowOnlineAlbum() {
        // Given
        val mockAlbumSubscription: Disposable = mock()
        val mockAlbumTrackSubscription: Disposable = mock()
        val mockAlbumMoreSubscription: Disposable = mock()
        val mockAlbumValue =
            MockDataModel.mockAlbum.copy(primaryRelease = MockDataModel.mockRelease)
        val albumResponse = Single.just(mockAlbumValue)
        val albumTrackResponse = Single.just(listOf(MockDataModel.mockTrack))
        val albumListResponse = Single.just(listOf(MockDataModel.mockAlbum))

        albumPresenter.setSubscription(
            albumSubscription = mockAlbumSubscription,
            albumTracksSubscription = mockAlbumTrackSubscription,
            albumMoreSubscription = mockAlbumMoreSubscription

        )
        albumPresenter.setObservable(
            albumObservable = albumResponse,
            albumTracksObservable = albumTrackResponse,
            albumMoreObservable = albumListResponse
        )

        whenever(albumFacade.loadTracks(any(), any())).thenReturn(albumTrackResponse)
        whenever(albumFacade.loadMoreFromArtist(any())).thenReturn(albumListResponse)

        // When
        albumPresenter.onResume()

        // Then
        verify(view, times(1)).initAlbum(any())
        verify(view, times(1)).showOnlineAlbum(any())
        verify(albumFacade, times(1)).loadTracks(any(), any())
    }

    @Test
    fun onResume_getAlbumSubscription_fail_showLoadAlbumError() {
        // Given
        val mockAlbumSubscription: Disposable = mock()
        albumPresenter.setSubscription(albumSubscription = mockAlbumSubscription)
        albumPresenter.setObservable(albumObservable = Single.error(Throwable("error")))

        // When
        albumPresenter.onResume()

        // Then
        verify(view, times(1)).showLoadAlbumError()
    }

    @Test
    fun onResume_getSelectedTrackSubscription_success_callApiLoadAlbumAndLoadFavourite() {
        // Given
        val mockSelectedTrackSubscription: Disposable = mock()
        val mockAlbumSubscription: Disposable = mock()
        val mockisFavouritedSubscription: Disposable = mock()
        val trackResponse = Single.just(MockDataModel.mockTrack)
        val albumResponse = Single.just(MockDataModel.mockAlbum)
        val isFavouriteResponse = Single.just(true)

        albumPresenter.setSubscription(
            selectedTrackSubscription = mockSelectedTrackSubscription,
            albumSubscription = mockAlbumSubscription,
            isFavouritedSubscription = mockisFavouritedSubscription
        )
        albumPresenter.setObservable(
            selectedTrackObservable = trackResponse,
            albumObservable = albumResponse,
            isFavouritedObservable = isFavouriteResponse
        )

        whenever(albumFacade.loadAlbum(any())).thenReturn(albumResponse)
        whenever(albumFacade.loadFavourited(any())).thenReturn(isFavouriteResponse)

        // When
        albumPresenter.onResume()

        // Then
        verify(albumFacade, times(1)).loadAlbum(any())
        verify(albumFacade, times(1)).loadFavourited(any())
    }

    @Test
    fun onResume_getSelectedTrackSubscription_fail_showLoadAlbumError() {
        // Given
        val mockSelectedTrackSubscription: Disposable = mock()
        albumPresenter.setSubscription(selectedTrackSubscription = mockSelectedTrackSubscription)
        albumPresenter.setObservable(selectedTrackObservable = Single.error(Throwable("error")))

        // When
        albumPresenter.onResume()

        // Then
        verify(view, times(1)).showLoadAlbumError()
    }

    @Test
    fun onResume_getAlbumTracksSubscription_success_showTrack() {
        // Given
        val mockAlbumTrackSubscription: Disposable = mock()
        val albumTrackResponse = Single.just(listOf(MockDataModel.mockTrack))
        albumPresenter.setSubscription(albumTracksSubscription = mockAlbumTrackSubscription)
        albumPresenter.setObservable(albumTracksObservable = albumTrackResponse)

        // When
        albumPresenter.onResume()

        // Then
        verify(view, times(1)).showTracks(any(), any())
    }

    @Test
    fun onResume_getAlbumTracksSubscription_fail_showTrackError() {
        // Given
        val mockAlbumTrackSubscription: Disposable = mock()
        albumPresenter.setSubscription(albumTracksSubscription = mockAlbumTrackSubscription)
        albumPresenter.setObservable(albumTracksObservable = Single.error(Throwable("error")))

        // When
        albumPresenter.onResume()

        // Then
        verify(view, times(1)).showTracksError()
    }

    @Test
    fun onResume_getIsFavouritedSubscription_success_showFavourited() {
        // Given
        val mockIsFavouritedSubscription: Disposable = mock()
        val isFavouritedResponse = Single.just(true)
        albumPresenter.setSubscription(isFavouritedSubscription = mockIsFavouritedSubscription)
        albumPresenter.setObservable(isFavouritedObservable = isFavouritedResponse)

        // When
        albumPresenter.onResume()

        // Then
        verify(view, times(1)).showFavourited(any())
    }

    @Test
    fun onResume_getIsFavouritedSubscription_fail_showFavourited() {
        // Given
        val mockIsFavouritedSubscription: Disposable = mock()
        albumPresenter.setSubscription(isFavouritedSubscription = mockIsFavouritedSubscription)
        albumPresenter.setObservable(isFavouritedObservable = Single.error(Throwable("error")))

        // When
        albumPresenter.onResume()

        // Then
        verify(view, times(1)).showFavourited(any())
    }

    @Test
    fun onResume_getToggleFavouriteSubscription_success_favouriteIsTrue_showFavouritedToast() {
        // Given
        val mockToggleFavouriteSubscription: Disposable = mock()
        val toggleFavouriteResponse = Single.just(Any())
        albumPresenter.setPrivateData(isFavourited = true)
        albumPresenter.setSubscription(toggleFavouriteSubscription = mockToggleFavouriteSubscription)
        albumPresenter.setObservable(toggleFavouriteObservable = toggleFavouriteResponse)

        // When
        albumPresenter.onResume()

        // Then
        verify(view, times(1)).showFavouritedToast()
    }

    @Test
    fun onResume_getToggleFavouriteSubscription_success_favouriteIsFalse_showUnFavouritedToast() {
        // Given
        val mockToggleFavouriteSubscription: Disposable = mock()
        val toggleFavouriteResponse = Single.just(Any())
        albumPresenter.setPrivateData(isFavourited = false)
        albumPresenter.setSubscription(toggleFavouriteSubscription = mockToggleFavouriteSubscription)
        albumPresenter.setObservable(toggleFavouriteObservable = toggleFavouriteResponse)

        // When
        albumPresenter.onResume()

        // Then
        verify(view, times(1)).showUnFavouritedToast()
    }

    @Test
    fun onResume_getToggleFavouriteSubscription_fail_favouriteIsTrue_showFavouritedError() {
        // Given
        val mockToggleFavouriteSubscription: Disposable = mock()
        albumPresenter.setPrivateData(isFavourited = true)
        albumPresenter.setSubscription(toggleFavouriteSubscription = mockToggleFavouriteSubscription)
        albumPresenter.setObservable(toggleFavouriteObservable = Single.error(Throwable("error")))

        // When
        albumPresenter.onResume()

        // Then
        verify(view, times(1)).showFavourited(any())
        verify(view, times(1)).showFavouritedError()
    }

    @Test
    fun onResume_getToggleFavouriteSubscription_fail_favouriteIsFalse_showUnFavouritedError() {
        // Given
        val mockToggleFavouriteSubscription: Disposable = mock()
        albumPresenter.setPrivateData(isFavourited = false)
        albumPresenter.setSubscription(toggleFavouriteSubscription = mockToggleFavouriteSubscription)
        albumPresenter.setObservable(toggleFavouriteObservable = Single.error(Throwable("error")))

        // When
        albumPresenter.onResume()

        // Then
        verify(view, times(1)).showFavourited(any())
        verify(view, times(1)).showUnFavouritedError()
    }

    @Test
    fun onResume_getAlbumMoreSubscription_success_filterResultsIsEmpty_hideMoreAlbums() {
        // Given
        val mockAlbumMoreSubscription: Disposable = mock()
        val albumResponse = Single.just(listOf(MockDataModel.mockAlbum.copy(id = 10)))
        val mockAlbumGlobalValue = MockDataModel.mockAlbum.copy(id = 10)
        albumPresenter.setPrivateData(album = mockAlbumGlobalValue)
        albumPresenter.setSubscription(albumMoreSubscription = mockAlbumMoreSubscription)
        albumPresenter.setObservable(albumMoreObservable = albumResponse)

        // When
        albumPresenter.onResume()

        // Then
        verify(view, times(1)).hideMoreAlbums()
    }

    @Test
    fun onResume_getAlbumMoreSubscription_success_filterResultsIsNotEmpty_showMoreAlbums() {
        // Given
        val mockAlbumMoreSubscription: Disposable = mock()
        val albumResponse = Single.just(listOf(MockDataModel.mockAlbum.copy(id = 20)))
        val mockAlbumGlobalValue = MockDataModel.mockAlbum.copy(id = 10)
        albumPresenter.setPrivateData(album = mockAlbumGlobalValue)
        albumPresenter.setSubscription(albumMoreSubscription = mockAlbumMoreSubscription)
        albumPresenter.setObservable(albumMoreObservable = albumResponse)

        // When
        albumPresenter.onResume()

        // Then
        verify(view, times(1)).showMoreAlbums(any())
    }

    @Test
    fun onResume_getAlbumMoreSubscription_success_albumIsNull_doNothing() {
        // Given
        val mockAlbumMoreSubscription: Disposable = mock()
        val albumResponse = Single.just(listOf(MockDataModel.mockAlbum.copy(id = 20)))
        albumPresenter.setPrivateData(album = null)
        albumPresenter.setSubscription(albumMoreSubscription = mockAlbumMoreSubscription)
        albumPresenter.setObservable(albumMoreObservable = albumResponse)

        // When
        albumPresenter.onResume()

        // Then
        verify(view, times(0)).showMoreAlbums(any())
        verify(view, times(0)).hideMoreAlbums()
    }

    @Test
    fun onResume_getAlbumMoreSubscription_fail_showMoreAlbumError() {
        // Given
        val mockAlbumMoreSubscription: Disposable = mock()
        albumPresenter.setSubscription(albumMoreSubscription = mockAlbumMoreSubscription)
        albumPresenter.setObservable(albumMoreObservable = Single.error(Throwable("error")))

        // When
        albumPresenter.onResume()

        // Then
        verify(view, times(1)).showMoreAlbumsError()
    }

    @Test
    fun onPause_subscriptionIsNotNull_disposeSubscription() {
        // Given
        albumPresenter.setSubscription(
            albumSubscription = albumSubscription,
            selectedTrackSubscription = selectedTrackSubscription,
            albumTracksSubscription = albumTracksSubscription,
            isFavouritedSubscription = isFavouritedSubscription,
            toggleFavouriteSubscription = toggleFavouriteSubscription,
            albumMoreSubscription = albumMoreSubscription
        )

        // When
        albumPresenter.onPause()

        // Then
        verify(albumSubscription, times(1)).dispose()
        verify(selectedTrackSubscription, times(1)).dispose()
        verify(albumTracksSubscription, times(1)).dispose()
        verify(isFavouritedSubscription, times(1)).dispose()
        verify(toggleFavouriteSubscription, times(1)).dispose()
        verify(albumMoreSubscription, times(1)).dispose()
    }

    @Test
    fun onPause_subscriptionIsNull_doNothing() {
        // Given
        albumPresenter.setSubscription(
            albumSubscription = null,
            selectedTrackSubscription = null,
            albumTracksSubscription = null,
            isFavouritedSubscription = null,
            toggleFavouriteSubscription = null,
            albumMoreSubscription = null
        )

        // When
        albumPresenter.onPause()

        // Then
        verify(albumSubscription, times(0)).dispose()
        verify(selectedTrackSubscription, times(0)).dispose()
        verify(albumTracksSubscription, times(0)).dispose()
        verify(isFavouritedSubscription, times(0)).dispose()
        verify(toggleFavouriteSubscription, times(0)).dispose()
        verify(albumMoreSubscription, times(0)).dispose()
    }

    @Test
    fun onUpdatePlaybackState_playingTrackIdNotEqualsTrackId_showCurrentPlayingTrack() {
        // Given
        albumPresenter.setPrivateData(playingTrackId = 1)

        // When
        albumPresenter.onUpdatePlaybackState(albumId = 100, trackId = 200, state = null)

        // Then
        verify(view, times(1)).showCurrentPlayingTrack(anyInt())
    }

    @Test
    fun onUpdatePlaybackState_playingTrackIdEqualsTrackId_doNothing() {
        // Given
        albumPresenter.setPrivateData(playingTrackId = 1)

        // When
        albumPresenter.onUpdatePlaybackState(albumId = 100, trackId = 1, state = null)

        // Then
        verify(view, times(0)).showCurrentPlayingTrack(anyInt())
    }

    @Test
    fun onPlayAlbum_hasAlbumShuffleRightIsTrue_playbackTrackIsTrue_albumIsNotNull_playAlbum() {
        // Given
        albumPresenter.setPrivateData(trackList = listOf(mockTrack), album = mockAlbum)
        whenever(albumFacade.getHasAlbumShuffleRight()).thenReturn(true)

        // When
        albumPresenter.onPlayAlbum()

        // Then
        verify(view, times(1)).playAlbum(any(), any(), anyOrNull(), any(), any(), any())
    }

    @Test
    fun onPlayAlbum_hasAlbumShuffleRightIsTrue_playbackTrackIsFalse_albumIsNull_doNothing() {
        // Given
        val mockTrackValue = mockTrack.copy(allowStream = false)
        albumPresenter.setPrivateData(trackList = listOf(mockTrackValue), album = mockAlbum)
        whenever(albumFacade.getHasAlbumShuffleRight()).thenReturn(true)

        // When
        albumPresenter.onPlayAlbum()

        // Then
        verify(view, times(0)).playAlbum(any(), any(), anyOrNull(), any(), any(), any())
    }

    @Test
    fun onPlayAlbum_hasAlbumShuffleRightIsTrue_playbackTrackIsTrue_albumIsNull_doNothing() {
        // Given
        albumPresenter.setPrivateData(trackList = listOf(mockTrack), album = null)
        whenever(albumFacade.getHasAlbumShuffleRight()).thenReturn(true)

        // When
        albumPresenter.onPlayAlbum()

        // Then
        verify(view, times(0)).playAlbum(any(), any(), anyOrNull(), any(), any(), any())
    }

    @Test
    fun onPlayAlbum_hasAlbumShuffleRightIsTrue_trackIsNull_albumIsNull_doNothing() {
        // Given
        albumPresenter.setPrivateData(trackList = listOf(mockTrack), album = null)
        whenever(albumFacade.getHasAlbumShuffleRight()).thenReturn(true)

        // When
        albumPresenter.onPlayAlbum()

        // Then
        verify(view, times(0)).playAlbum(any(), any(), anyOrNull(), any(), any(), any())
    }

    @Test
    fun onPlayAlbum_hasAlbumShuffleRightIsFalse_showUpdateDialog() {
        // Given
        whenever(albumFacade.getHasAlbumShuffleRight()).thenReturn(false)

        // When
        albumPresenter.onPlayAlbum()

        // Then
        verify(view, times(1)).showUpgradeDialog()
    }

    @Test
    fun onShowMoreOptions_albumIsNotNull_showMoreOptions() {
        // Given
        albumPresenter.setPrivateData(album = mockAlbum)

        // When
        albumPresenter.onShowMoreOptions()

        // Then
        verify(view, times(1)).showMoreOptions(any())
    }

    @Test
    fun onShowMoreOptions_albumIsNull_doNothing() {
        // Given
        albumPresenter.setPrivateData(album = null)

        // When
        albumPresenter.onShowMoreOptions()

        // Then
        verify(view, times(0)).showMoreOptions(any())
    }

    @Test
    fun onMoreOptionSelected_selectionIsShare_albumIsNotNull_returnTrueAndShowOptions() {
        // Given
        albumPresenter.setPrivateData(album = mockAlbum)

        // When
        val result = albumPresenter.onMoreOptionSelected(PickerOptions.SHARE)

        // Then
        assertTrue(result)
        verify(view, times(1)).showShareAlbum(any())
    }

    @Test
    fun onMoreOptionSelected_selectionIsShare_albumIsNotNull_returnTrue() {
        // Given
        albumPresenter.setPrivateData(album = null)

        // When
        val result = albumPresenter.onMoreOptionSelected(PickerOptions.SHARE)

        // Then
        assertTrue(result)
        verify(view, times(0)).showShareAlbum(any())
    }

    @Test
    fun onMoreOptionSelected_selectionIsAddToCollection_albumIsNotNull_returnTrueAndShowFavourite() {
        // Given
        albumPresenter.setPrivateData(album = mockAlbum)
        whenever(albumFacade.toggleFavourite(any())).thenReturn(Single.just(Any()))

        // When
        val result = albumPresenter.onMoreOptionSelected(PickerOptions.ADD_TO_COLLECTION)

        // Then
        assertTrue(result)
        verify(view, times(1)).showFavourited(any())
    }

    @Test
    fun onMoreOptionSelected_selectionIsAddToCollection_albumIsNull_returnTrue() {
        // Given
        albumPresenter.setPrivateData(album = null)
        whenever(albumFacade.toggleFavourite(any())).thenReturn(Single.just(Any()))

        // When
        val result = albumPresenter.onMoreOptionSelected(PickerOptions.ADD_TO_COLLECTION)

        // Then
        assertTrue(result)
        verify(view, times(0)).showFavourited(any())
    }

    @Test
    fun onMoreOptionSelected_selectionIsAddToQueue_showAddToQueueToast() {
        // When
        albumPresenter.onMoreOptionSelected(PickerOptions.ADD_TO_QUEUE)

        // Then
        verify(view, times(1)).showAddToQueueToast()
    }

    @Test
    fun onMoreOptionSelected_selectionIsRemoveFromCollection_albumIsNotNull_returnTrueAndShowFavourite() {
        // Given
        albumPresenter.setPrivateData(album = mockAlbum)
        whenever(albumFacade.toggleFavourite(any())).thenReturn(Single.just(Any()))

        // When
        val result = albumPresenter.onMoreOptionSelected(PickerOptions.REMOVE_FROM_COLLECTION)

        // Then
        assertTrue(result)
        verify(view, times(1)).showFavourited(any())
    }

    @Test
    fun onMoreOptionSelected_selectionIsRemoveFromCollection_albumIsNull_returnTrue() {
        // Given
        albumPresenter.setPrivateData(album = null)
        whenever(albumFacade.toggleFavourite(any())).thenReturn(Single.just(Any()))

        // When
        val result = albumPresenter.onMoreOptionSelected(PickerOptions.REMOVE_FROM_COLLECTION)

        assertTrue(result)
        verify(view, times(0)).showFavourited(any())
    }

    @Test
    fun onMoreOptionSelected_selectionIsShowArtist_albumIsNotNull_primaryReleaseArtistIsNotEmpty_returnTrueAndRouteToArtist() {
        // Given
        val mockPrimaryReleaseValue = mockRelease.copy(artists = listOf(mockArtistInfo))
        val mockAlbumValue = mockAlbum.copy(primaryRelease = mockPrimaryReleaseValue)
        albumPresenter.setPrivateData(album = mockAlbumValue)

        // When
        val result = albumPresenter.onMoreOptionSelected(PickerOptions.SHOW_ARTIST)

        // Then
        assertTrue(result)
        verify(router, times(1)).navigateToArtist(any())
    }

    @Test
    fun onMoreOptionSelected_selectionIsShowArtist_albumIsNotNull_primaryReleaseArtistIsEmpty_returnTrue() {
        // Given
        val mockPrimaryReleaseValue = mockRelease.copy(artists = emptyList())
        val mockAlbumValue = mockAlbum.copy(primaryRelease = mockPrimaryReleaseValue)
        albumPresenter.setPrivateData(album = mockAlbumValue)

        // When
        val result = albumPresenter.onMoreOptionSelected(PickerOptions.SHOW_ARTIST)

        // Then
        assertTrue(result)
        verify(view, times(0)).showFavourited(any())
    }

    @Test
    fun onMoreOptionSelected_selectionIsShowArtist_albumIsNotNull_primaryReleaseIsNull_returnTrue() {
        // Given
        val mockAlbumValue = mockAlbum.copy(primaryRelease = null)
        albumPresenter.setPrivateData(album = mockAlbumValue)

        // When
        val result = albumPresenter.onMoreOptionSelected(PickerOptions.SHOW_ARTIST)

        // Then
        assertTrue(result)
        verify(view, times(0)).showFavourited(any())
    }

    @Test
    fun onMoreOptionSelected_selectionIsElse_returnFalse() {
        // When
        val result = albumPresenter.onMoreOptionSelected(PickerOptions.DOWNLOAD)

        // Then
        assertFalse(result)
    }

    @Test
    fun onMoreTrackOptionSelected_selectionIsAddToQueue_showAddToQueueToast() {
        // When
        albumPresenter.onMoreTrackOptionSelected(PickerOptions.ADD_TO_QUEUE)

        // Then
        verify(view, times(1)).showAddToQueueToast()
    }

    @Test
    fun onMoreTrackOptionSelected_selectionIsElse_doNothing() {
        // When
        albumPresenter.onMoreTrackOptionSelected(PickerOptions.SHOW_ARTIST)

        // Then
        verify(view, times(0)).showAddToQueueToast()
    }

    @Test
    fun onShareAlbumMenu_albumIsNotNull_showShareAlbum() {
        // Given
        albumPresenter.setPrivateData(album = mockAlbum)

        // When
        albumPresenter.onShareAlbumMenu()

        // Then
        verify(view, times(1)).showShareAlbum(any())
    }

    @Test
    fun onShareAlbumMenu_albumIsNull_doNothing() {
        // Given
        albumPresenter.setPrivateData(album = null)

        // When
        albumPresenter.onShareAlbumMenu()

        // Then
        verify(view, times(0)).showShareAlbum(any())
    }

    @Test
    fun onToggleFavourite_albumIsNotNull_toggleFavouriteObservableIsNull_showFavourite() {
        // Given
        albumPresenter.setPrivateData(album = mockAlbum, toggleFavouriteObservable = null)
        whenever(albumFacade.toggleFavourite(any())).thenReturn(Single.just(Any()))

        // When
        albumPresenter.onToggleFavourite()

        // Then
        verify(view, times(1)).showFavourited(any())
    }

    @Test
    fun onToggleFavourite_albumIsNotNull_toggleFavouriteObservableIsNotNull_doNothing() {
        // Given
        albumPresenter.setPrivateData(
            album = mockAlbum,
            toggleFavouriteObservable = Single.just(Any())
        )
        whenever(albumFacade.toggleFavourite(any())).thenReturn(Single.just(Any()))

        // When
        albumPresenter.onToggleFavourite()

        // Then
        verify(view, times(0)).showFavourited(any())
    }

    @Test
    fun onToggleFavourite_albumIstNull_toggleFavouriteObservableIsNull_doNothing() {
        // Given
        albumPresenter.setPrivateData(album = null, toggleFavouriteObservable = null)
        whenever(albumFacade.toggleFavourite(any())).thenReturn(Single.just(Any()))

        // When
        albumPresenter.onToggleFavourite()

        // Then
        verify(view, times(0)).showFavourited(any())
    }

    @Test
    fun onSongSelected_hasAlbumShuffleRightIsTrue_allowStreamIsTrue_trackIdEqualsTrackIdParam_albumIsNotNull_playAlbum() {
        // Given
        val mockId = 10
        val mockTracksValue = mockTrack.copy(allowStream = true, id = mockId)
        albumPresenter.setPrivateData(trackList = listOf(mockTracksValue), album = mockAlbum)
        whenever(albumFacade.getHasAlbumShuffleRight()).thenReturn(true)

        // When
        albumPresenter.onSongSelected(mockId)

        // Then
        verify(view, times(1)).playAlbum(any(), any(), anyOrNull(), any(), any(), any())
    }

    @Test
    fun onSongSelected_hasAlbumShuffleRightIsTrue_allowStreamIsTrue_trackIdEqualsTrackIdParam_albumIsNull_doNothing() {
        // Given
        val mockId = 10
        val mockTracksValue = mockTrack.copy(allowStream = true, id = mockId)
        albumPresenter.setPrivateData(trackList = listOf(mockTracksValue), album = null)
        whenever(albumFacade.getHasAlbumShuffleRight()).thenReturn(true)

        // When
        albumPresenter.onSongSelected(mockId)

        // Then
        verify(view, times(0)).playAlbum(any(), any(), anyOrNull(), any(), any(), any())
    }

    @Test
    fun onSongSelected_hasAlbumShuffleRightIsTrue_allowStreamIsTrue_trackIdNotEqualsTrackIdParam_doNothing() {
        // Given
        val mockId = 10
        val mockTracksValue = mockTrack.copy(allowStream = true, id = mockId)
        albumPresenter.setPrivateData(trackList = listOf(mockTracksValue), album = null)
        whenever(albumFacade.getHasAlbumShuffleRight()).thenReturn(true)

        // When
        albumPresenter.onSongSelected(1)

        // Then
        verify(view, times(0)).playAlbum(any(), any(), anyOrNull(), any(), any(), any())
    }

    @Test
    fun onSongSelected_hasAlbumShuffleRightIsTrue_allowStreamIsFalse_doNothing() {
        // Given
        val mockId = 10
        val mockTracksValue = mockTrack.copy(allowStream = false, id = mockId)
        albumPresenter.setPrivateData(trackList = listOf(mockTracksValue), album = null)
        whenever(albumFacade.getHasAlbumShuffleRight()).thenReturn(true)

        // When
        albumPresenter.onSongSelected(1)

        // Then
        verify(view, times(0)).playAlbum(any(), any(), anyOrNull(), any(), any(), any())
    }

    @Test
    fun onSongSelected_hasAlbumShuffleRightIsTrue_tracksIsNull_doNothing() {
        // Given
        val mockId = 10
        val mockTracksValue = mockTrack.copy(allowStream = false, id = mockId)
        albumPresenter.setPrivateData(trackList = listOf(mockTracksValue), album = null)
        whenever(albumFacade.getHasAlbumShuffleRight()).thenReturn(true)

        // When
        albumPresenter.onSongSelected(1)

        // Then
        verify(view, times(0)).playAlbum(any(), any(), anyOrNull(), any(), any(), any())
    }

    @Test
    fun onSongSelected_hasAlbumShuffleRightIsFalse_showUpgradeDialog() {
        // Given
        whenever(albumFacade.getHasAlbumShuffleRight()).thenReturn(false)

        // When
        albumPresenter.onSongSelected(1)

        // Then
        verify(view, times(1)).showUpgradeDialog()
    }

    @Test
    fun onAlbumSelected_navigateToAlbum() {
        // When
        albumPresenter.onAlbumSelected(mockAlbum)

        // Then
        verify(router, times(1)).navigateToAlbum(any())
    }

    @Test
    fun onArtistSelected_albumIsNotNull_primaryReleaseArtistsIsNotEmpty_navigateToArtist() {
        // Given
        val mockPrimaryReleaseValue = mockRelease.copy(artists = listOf(mockArtistInfo))
        val mockAlbumValue = mockAlbum.copy(primaryRelease = mockPrimaryReleaseValue)
        albumPresenter.setPrivateData(album = mockAlbumValue)

        // When
        albumPresenter.onArtistSelected()

        // Then
        verify(router, times(1)).navigateToArtist(any())
    }

    @Test
    fun onArtistSelected_albumIsNotNull_primaryReleaseArtistsIsEmpty_doNothing() {
        // Given
        val mockPrimaryReleaseValue = mockRelease.copy(artists = emptyList())
        val mockAlbumValue = mockAlbum.copy(primaryRelease = mockPrimaryReleaseValue)
        albumPresenter.setPrivateData(album = mockAlbumValue)

        // When
        albumPresenter.onArtistSelected()

        // Then
        verify(router, times(0)).navigateToArtist(any())
    }

    @Test
    fun onArtistSelected_albumIsNotNull_primaryReleaseIsNull_doNothing() {
        // Given
        val mockAlbumValue = mockAlbum.copy(primaryRelease = null)
        albumPresenter.setPrivateData(album = mockAlbumValue)

        // When
        albumPresenter.onArtistSelected()

        // Then
        verify(router, times(0)).navigateToArtist(any())
    }

    @Test
    fun onArtistSelected_albumIsNull_doNothing() {
        // Given
        albumPresenter.setPrivateData(album = null)

        // When
        albumPresenter.onArtistSelected()

        // Then
        verify(router, times(0)).navigateToArtist(any())
    }

    @Test
    fun onImageSelected_albumIsNotNull_primaryReleaseIsNotNull_showEnlargedImage() {
        // Given
        val mockPrimaryReleaseValue = mockRelease.copy(image = "image")
        val mockAlbumValue = mockAlbum.copy(primaryRelease = mockPrimaryReleaseValue)
        albumPresenter.setPrivateData(album = mockAlbumValue)

        // When
        albumPresenter.onImageSelected()

        // Then
        verify(view, times(1)).showEnlargedImage(any())
    }

    @Test
    fun onImageSelected_albumIsNotNull_primaryReleaseIsNull_doNothing() {
        // Given
        val mockAlbumValue = mockAlbum.copy(primaryRelease = null)
        albumPresenter.setPrivateData(album = mockAlbumValue)

        // When
        albumPresenter.onImageSelected()

        // Then
        verify(view, times(0)).showEnlargedImage(any())
    }

    @Test
    fun onImageSelected_albumIsNull_doNothing() {
        // Given
        albumPresenter.setPrivateData(album = null)

        // When
        albumPresenter.onImageSelected()

        // Then
        verify(view, times(0)).showEnlargedImage(any())
    }

    @Test
    fun onRetryTracks_albumIsNotNull_loadTracksAndShowLoadingTrack() {
        // Given
        albumPresenter.setPrivateData(album = mockAlbum)
        whenever(albumFacade.loadTracks(any(), any())).thenReturn(Single.just(listOf(mockTrack)))

        // When
        albumPresenter.onRetryTracks()

        // Then
        verify(albumFacade, times(1)).loadTracks(any(), any())
        verify(view, times(1)).showLoadingTracks()
    }

    @Test
    fun onRetryTracks_albumIsNull_doNothing() {
        // Given
        albumPresenter.setPrivateData(album = null)

        // When
        albumPresenter.onRetryTracks()

        // Then
        verify(albumFacade, times(0)).loadTracks(any(), any())
        verify(view, times(0)).showLoadingTracks()
    }

    @Test
    fun onRetryMoreAlbums_albumIsNotNull_loadMoreFromArtistAndShowLoading() {
        // Given
        albumPresenter.setPrivateData(album = mockAlbum)
        whenever(albumFacade.loadMoreFromArtist(any())).thenReturn(Single.just(listOf(mockAlbum)))

        // When
        albumPresenter.onRetryMoreAlbums()

        // Then
        verify(albumFacade, times(1)).loadMoreFromArtist(any())
        verify(view, times(1)).showLoadingMoreAlbums()
    }

    @Test
    fun onRetryMoreAlbums_albumIsNull_doNothing() {
        // Given
        albumPresenter.setPrivateData(album = null)

        // When
        albumPresenter.onRetryMoreAlbums()

        // Then
        verify(albumFacade, times(0)).loadMoreFromArtist(any())
        verify(view, times(0)).showLoadingMoreAlbums()
    }

    @Test
    fun onFavouriteSelect_isFavouritedIsTrue_isSuccessIsTrue_thenShowFavouritedToast() {
        // When
        albumPresenter.onFavouriteSelect(isFavourited = true, isSuccess = true)

        // Then
        verify(view, times(1)).showFavouritedToast()
    }

    @Test
    fun onFavouriteSelect_isFavouritedIsFalse_isSuccessIsTrue_thenShowUnFavouritedToast() {
        // When
        albumPresenter.onFavouriteSelect(isFavourited = false, isSuccess = true)

        // Then
        verify(view, times(1)).showUnFavouritedToast()
    }

    @Test
    fun onFavouriteSelect_isFavouritedIsTrue_isSuccessIsFalse_thenShowFavouritedError() {
        // When
        albumPresenter.onFavouriteSelect(isFavourited = true, isSuccess = false)

        // Then
        verify(view, times(1)).showFavouritedError()
    }

    @Test
    fun onFavouriteSelect_isFavouritedIsFalse_isSuccessIsFalse_thenShowUnFavouritedError() {
        // When
        albumPresenter.onFavouriteSelect(isFavourited = false, isSuccess = false)

        // Then
        verify(view, times(1)).showUnFavouritedError()
    }
}
