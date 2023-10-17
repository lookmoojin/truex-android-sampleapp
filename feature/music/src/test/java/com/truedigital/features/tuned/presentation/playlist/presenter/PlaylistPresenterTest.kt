package com.truedigital.features.tuned.presentation.playlist.presenter

import android.os.Bundle
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.anyOrNull
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.common.share.analytics.measurement.AnalyticManager
import com.truedigital.features.tuned.data.util.LocalisedString
import com.truedigital.features.tuned.domain.facade.playlist.PlaylistFacade
import com.truedigital.features.tuned.presentation.bottomsheet.PickerOptions
import com.truedigital.features.utils.MockDataModel
import com.truedigital.features.utils.MockJson
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import kotlin.test.assertFalse
import kotlin.test.assertTrue

internal class PlaylistPresenterTest {

    private lateinit var presenter: PlaylistPresenter
    private val playlistFacade: PlaylistFacade = mock()
    private val router: PlaylistPresenter.RouterSurface = mock()
    private val view: PlaylistPresenter.ViewSurface = mock()
    private val playlistSubscription: Disposable = mock()
    private val playlistTracksSubscription: Disposable = mock()
    private val isOwnerSubscription: Disposable = mock()
    private val isFavouritedSubscription: Disposable = mock()
    private val toggleFavouriteSubscription: Disposable = mock()
    private val analyticManager: AnalyticManager = mockk()

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        every { analyticManager.trackEvent(any()) } just runs

        presenter =
            PlaylistPresenter(playlistFacade, MockDataModel.mockConfiguration, analyticManager)
        presenter.onInject(view, router)
    }

    @Test
    fun onStart_argumentIsNotNull_playlistStringIsNotNullOrEmpty_playlistIsNotNull_playlistIdEqualsZero_showOnlinePlaylist() {
        // Given
        val mockBundle = Mockito.mock(Bundle::class.java)
        whenever(mockBundle.getInt(PlaylistPresenter.PLAYLIST_ID_KEY)).thenReturn(0)
        whenever(mockBundle.getString(PlaylistPresenter.PLAYLIST_KEY)).thenReturn(MockJson.playlistJSON)
        whenever(mockBundle.getBoolean(PlaylistPresenter.AUTO_PLAY_KEY)).thenReturn(true)

        whenever(playlistFacade.isOwner(any())).thenReturn(Single.just(true))

        // When
        presenter.onStart(mockBundle)

        // Then
        verify(view, times(1)).initPlaylist(any())
        verify(view, times(1)).showOnlinePlaylist(any())
        verify(playlistFacade, times(1)).isOwner(any())
    }

    @Test
    fun onStart_argumentIsNotNull_playlistStringIsNull_playlistIdNotEqualsZero_getPlaylist() {
        // Given
        val mockBundle = Mockito.mock(Bundle::class.java)
        whenever(mockBundle.getInt(PlaylistPresenter.PLAYLIST_ID_KEY)).thenReturn(1)
        whenever(mockBundle.getBoolean(PlaylistPresenter.AUTO_PLAY_KEY)).thenReturn(true)

        whenever(playlistFacade.getPlaylist(any())).thenReturn(Single.just(MockDataModel.mockPlaylist))

        // When
        presenter.onStart(mockBundle)

        // Then
        verify(playlistFacade, times(1)).getPlaylist(any())
    }

    @Test
    fun onStart_argumentIsNotNull_playlistStringIsEmpty_playlistIdNotEqualsZero_getPlaylist() {
        // Given
        val mockBundle = Mockito.mock(Bundle::class.java)
        whenever(mockBundle.getInt(PlaylistPresenter.PLAYLIST_ID_KEY)).thenReturn(1)
        whenever(mockBundle.getString(PlaylistPresenter.PLAYLIST_KEY)).thenReturn("")
        whenever(mockBundle.getBoolean(PlaylistPresenter.AUTO_PLAY_KEY)).thenReturn(true)

        whenever(playlistFacade.getPlaylist(any())).thenReturn(Single.just(MockDataModel.mockPlaylist))

        // When
        presenter.onStart(mockBundle)

        // Then
        verify(playlistFacade, times(1)).getPlaylist(any())
    }

    @Test
    fun onStart_argumentIsNotNull_playlistStringIsEmpty_playlistIdEqualsZero_showLoadPlaylistError() {
        // Given
        val mockBundle = Mockito.mock(Bundle::class.java)
        whenever(mockBundle.getInt(PlaylistPresenter.PLAYLIST_ID_KEY)).thenReturn(0)
        whenever(mockBundle.getString(PlaylistPresenter.PLAYLIST_KEY)).thenReturn("")
        whenever(mockBundle.getBoolean(PlaylistPresenter.AUTO_PLAY_KEY)).thenReturn(true)

        // When
        presenter.onStart(mockBundle)

        // Then
        verify(view, times(1)).showLoadPlaylistError()
    }

    @Test
    fun onStart_argumentIsNull_doNothing() {
        // When
        presenter.onStart(null)

        // Then
        verify(view, times(0)).initPlaylist(any())
        verify(view, times(0)).showOnlinePlaylist(any())
        verify(playlistFacade, times(0)).isOwner(any())
        verify(playlistFacade, times(0)).getPlaylist(any())
        verify(view, times(0)).showLoadPlaylistError()
    }

    @Test
    fun onResume_getPlaylistSubscription_success_initPlaylist() {
        // Given
        val mockPlaylistSubscription: Disposable = mock()
        val mockIsOwnerSubscription: Disposable = mock()
        presenter.setSubscription(
            playlistSubscription = mockPlaylistSubscription,
            isOwnerSubscription = mockIsOwnerSubscription
        )
        presenter.setObservable(
            playlistObservable = Single.just(MockDataModel.mockPlaylist),
            isOwnerObservable = Single.just(true)
        )

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).initPlaylist(any())
        verify(playlistFacade, times(1)).isOwner(any())
    }

    @Test
    fun onResume_getPlaylistSubscription_fail_showLoadPlaylistError() {
        // Given
        val mockPlaylistSubscription: Disposable = mock()
        presenter.setSubscription(playlistSubscription = mockPlaylistSubscription)
        presenter.setObservable(playlistObservable = Single.error(Throwable("error")))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showLoadPlaylistError()
    }

    @Test
    fun onResume_getPlaylistTracksSubscription_success_showPlaylistSongs() {
        // Given
        val mockPlaylistTracksSubscription: Disposable = mock()
        presenter.setSubscription(playlistTracksSubscription = mockPlaylistTracksSubscription)
        presenter.setObservable(playlistTracksObservable = Single.just(listOf(MockDataModel.mockTrack)))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showPlaylistSongs(any(), any())
    }

    @Test
    fun onResume_getPlaylistTracksSubscription_fail_errorIsHttpException_errorCodeIsResourceNotFound_trackIsEmpty_showPlaylistNoSongs() {
        // Given
        val mockPlaylistTracksSubscription: Disposable = mock()
        presenter.setPrivateData(trackList = listOf())
        presenter.setSubscription(playlistTracksSubscription = mockPlaylistTracksSubscription)
        presenter.setObservable(playlistTracksObservable = Single.error(MockDataModel.mockHttpExceptionCodeResourceNotFound))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showPlaylistNoSongs()
    }

    @Test
    fun onResume_getPlaylistTracksSubscription_fail_errorIsHttpException_errorCodeIsResourceNotFound_trackIsNotEmpty_showPlaylistSongs() {
        // Given
        val mockPlaylistTracksSubscription: Disposable = mock()
        presenter.setPrivateData(trackList = listOf(MockDataModel.mockTrack))
        presenter.setSubscription(playlistTracksSubscription = mockPlaylistTracksSubscription)
        presenter.setObservable(playlistTracksObservable = Single.error(MockDataModel.mockHttpExceptionCodeResourceNotFound))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showPlaylistSongs(any(), any())
    }

    @Test
    fun onResume_getPlaylistTracksSubscription_fail_errorIsHttpException_errorCodeIsNotResourceNotFound_showPlaylistSongsError() {
        // Given
        val mockPlaylistTracksSubscription: Disposable = mock()
        presenter.setPrivateData(trackList = listOf(MockDataModel.mockTrack))
        presenter.setSubscription(playlistTracksSubscription = mockPlaylistTracksSubscription)
        presenter.setObservable(playlistTracksObservable = Single.error(MockDataModel.mockHttpExceptionCodeUnauthorised))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showPlaylistSongsError()
    }

    @Test
    fun onResume_getPlaylistTracksSubscription_fail_errorIsNotHttpException_errorCodeIsNotResourceNotFound_showPlaylistSongsError() {
        // Given
        val mockPlaylistTracksSubscription: Disposable = mock()
        presenter.setSubscription(playlistTracksSubscription = mockPlaylistTracksSubscription)
        presenter.setObservable(playlistTracksObservable = Single.error(Throwable("error")))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showPlaylistSongsError()
    }

    @Test
    fun onResume_getIsOwnerSubscription_success_playlistIsNotNull_ownerIsFalse_showOwnerAndShowPlaylistSongsLoading() {
        // Given
        val mockIsOwnerSubscription: Disposable = mock()
        val mockIsFavouritedSubscription: Disposable = mock()
        presenter.setSubscription(
            isOwnerSubscription = mockIsOwnerSubscription,
            isFavouritedSubscription = mockIsFavouritedSubscription
        )
        presenter.setObservable(
            isOwnerObservable = Single.just(false),
            isFavouritedObservable = Single.just(false)
        )
        presenter.setPrivateData(playlist = MockDataModel.mockPlaylist)
        whenever(playlistFacade.hasPlaylistWriteRight()).thenReturn(true)
        whenever(playlistFacade.loadFavourited(any())).thenReturn(Single.just(true))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showOwner(any(), any(), any())
        verify(view, times(1)).showPlaylistSongsLoading()
        verify(playlistFacade, times(1)).hasPlaylistWriteRight()
        verify(playlistFacade, times(1)).loadFavourited(any())
    }

    @Test
    fun onResume_getIsOwnerSubscription_success_playlistIsNotNull_ownerIsTrue_showOwnerAndShowPlaylistSongsLoading() {
        // Given
        val mockIsOwnerSubscription: Disposable = mock()
        presenter.setSubscription(isOwnerSubscription = mockIsOwnerSubscription)
        presenter.setObservable(isOwnerObservable = Single.just(true))
        presenter.setPrivateData(playlist = MockDataModel.mockPlaylist)
        whenever(playlistFacade.hasPlaylistWriteRight()).thenReturn(true)

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showOwner(any(), any(), any())
        verify(view, times(1)).showPlaylistSongsLoading()
        verify(playlistFacade, times(1)).hasPlaylistWriteRight()
    }

    @Test
    fun onResume_getIsOwnerSubscription_success_playlistIsNull_showOwner() {
        // Given
        val mockIsOwnerSubscription: Disposable = mock()
        presenter.setSubscription(isOwnerSubscription = mockIsOwnerSubscription)
        presenter.setObservable(isOwnerObservable = Single.just(true))
        presenter.setPrivateData(playlist = null)
        whenever(playlistFacade.hasPlaylistWriteRight()).thenReturn(true)

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showOwner(any(), any(), any())
        verify(view, times(0)).showPlaylistSongsLoading()
        verify(playlistFacade, times(1)).hasPlaylistWriteRight()
    }

    @Test
    fun onResume_getIsOwnerSubscription_fail_showLoadPlaylistError() {
        // Given
        val mockIsOwnerSubscription: Disposable = mock()
        presenter.setSubscription(isOwnerSubscription = mockIsOwnerSubscription)
        presenter.setObservable(isOwnerObservable = Single.error(Throwable("error")))
        presenter.setPrivateData(playlist = null)

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showLoadPlaylistError()
    }

    @Test
    fun onResume_getIsFavouritedSubscription_success_showFavourited() {
        // Given
        val mockIsFavouritedSubscription: Disposable = mock()
        presenter.setSubscription(isFavouritedSubscription = mockIsFavouritedSubscription)
        presenter.setObservable(isFavouritedObservable = Single.just(true))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showFavourited(any())
    }

    @Test
    fun onResume_getIsFavouritedSubscription_fail_doNothing() {
        // Given
        val mockIsFavouritedSubscription: Disposable = mock()
        presenter.setSubscription(isFavouritedSubscription = mockIsFavouritedSubscription)
        presenter.setObservable(isFavouritedObservable = Single.error(Throwable("error")))

        // When
        presenter.onResume()

        // Then
        verify(view, times(0)).showFavourited(any())
    }

    @Test
    fun onResume_getToggleFavouriteSubscription_success_favouriteIsTrue_showFavouriteToast() {
        // Given
        val mockToggleFavouriteSubscription: Disposable = mock()
        presenter.setSubscription(toggleFavouriteSubscription = mockToggleFavouriteSubscription)
        presenter.setObservable(toggleFavouriteObservable = Single.just(Any()))
        presenter.setPrivateData(isFavourited = true)

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showFavouritedToast()
    }

    @Test
    fun onResume_getToggleFavouriteSubscription_success_favouriteIsFalse_showUnFavouritedToast() {
        // Given
        val mockToggleFavouriteSubscription: Disposable = mock()
        presenter.setSubscription(toggleFavouriteSubscription = mockToggleFavouriteSubscription)
        presenter.setObservable(toggleFavouriteObservable = Single.just(Any()))
        presenter.setPrivateData(isFavourited = false)

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showUnFavouritedToast()
    }

    @Test
    fun onResume_getToggleFavouriteSubscription_fail_favouriteIsTrue_showFavouritedError() {
        // Given
        val mockToggleFavouriteSubscription: Disposable = mock()
        presenter.setPrivateData(isFavourited = true)
        presenter.setSubscription(toggleFavouriteSubscription = mockToggleFavouriteSubscription)
        presenter.setObservable(toggleFavouriteObservable = Single.error(Throwable("error")))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showFavourited(any())
        verify(view, times(1)).showFavouritedError()
    }

    @Test
    fun onResume_getToggleFavouriteSubscription_fail_favouriteIsFalse_showUnFavouritedError() {
        // Given
        val mockToggleFavouriteSubscription: Disposable = mock()
        presenter.setPrivateData(isFavourited = false)
        presenter.setSubscription(toggleFavouriteSubscription = mockToggleFavouriteSubscription)
        presenter.setObservable(toggleFavouriteObservable = Single.error(Throwable("error")))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showFavourited(any())
        verify(view, times(1)).showUnFavouritedError()
    }

    @Test
    fun onPause_subscriptionIsNotNull_disposeSubscription() {
        // Given
        presenter.setSubscription(
            playlistSubscription = playlistSubscription,
            playlistTracksSubscription = playlistTracksSubscription,
            isOwnerSubscription = isOwnerSubscription,
            isFavouritedSubscription = isFavouritedSubscription,
            toggleFavouriteSubscription = toggleFavouriteSubscription
        )

        // When
        presenter.onPause()

        // Then
        verify(playlistSubscription, times(1)).dispose()
        verify(playlistTracksSubscription, times(1)).dispose()
        verify(isOwnerSubscription, times(1)).dispose()
        verify(isFavouritedSubscription, times(1)).dispose()
        verify(toggleFavouriteSubscription, times(1)).dispose()
    }

    @Test
    fun onPause_subscriptionIsNull_doNothing() {
        // Given
        presenter.setSubscription(
            playlistSubscription = null,
            playlistTracksSubscription = null,
            isOwnerSubscription = null,
            isFavouritedSubscription = null,
            toggleFavouriteSubscription = null
        )

        // When
        presenter.onPause()

        // Then
        verify(playlistSubscription, times(0)).dispose()
        verify(playlistTracksSubscription, times(0)).dispose()
        verify(isOwnerSubscription, times(0)).dispose()
        verify(isFavouritedSubscription, times(0)).dispose()
        verify(toggleFavouriteSubscription, times(0)).dispose()
    }

    @Test
    fun onUpdatePlaybackState_trackIdIsNotEmpty_showCurrentPlayingTrack() {
        // When
        presenter.onUpdatePlaybackState(10, 20)

        // Then
        verify(view, times(1)).showCurrentPlayingTrack(any())
    }

    @Test
    fun onUpdatePlaybackState_trackIdIsEmpty_doNothing() {
        // When
        presenter.onUpdatePlaybackState(null, 20)

        // Then
        verify(view, times(0)).showCurrentPlayingTrack(any())
    }

    @Test
    fun onPlayPlaylist_tracksIsNotEmpty_allowStreamIsNotEmpty_playlistIsNotNull_playPlaylist() {
        // Given
        val mockTrackValue = MockDataModel.mockTrack.copy(allowStream = true)
        presenter.setPrivateData(trackList = listOf(mockTrackValue), MockDataModel.mockPlaylist)

        // When
        presenter.onPlayPlaylist()

        // Then
        verify(view, times(1)).playPlaylist(any(), any(), anyOrNull(), any(), any(), any())
    }

    @Test
    fun onPlayPlaylist_tracksIsNotEmpty_allowStreamIsNotEmpty_playlistIsNull_doNothing() {
        // Given
        val mockTrackValue = MockDataModel.mockTrack.copy(allowStream = true)
        presenter.setPrivateData(trackList = listOf(mockTrackValue))

        // When
        presenter.onPlayPlaylist()

        // Then
        verify(view, times(0)).playPlaylist(any(), any(), any(), any(), any(), any())
    }

    @Test
    fun onPlayPlaylist_tracksIsNotEmpty_allowStreamIsEmpty_doNothing() {
        // Given
        val mockTrackValue = MockDataModel.mockTrack.copy(allowStream = false)
        presenter.setPrivateData(trackList = listOf(mockTrackValue))

        // When
        presenter.onPlayPlaylist()

        // Then
        verify(view, times(0)).playPlaylist(any(), any(), any(), any(), any(), any())
    }

    @Test
    fun onPlayPlaylist_tracksIsEmpty_doNothing() {
        // Given
        presenter.setPrivateData(trackList = emptyList())

        // When
        presenter.onPlayPlaylist()

        // Then
        verify(view, times(0)).playPlaylist(any(), any(), any(), any(), any(), any())
    }

    @Test
    fun onImageSelected_playlistIsNotNull_showEnlargedImage() {
        // Given
        presenter.setPrivateData(playlist = MockDataModel.mockPlaylist)

        // When
        presenter.onImageSelected()

        // Then
        verify(view, times(1)).showEnlargedImage(any())
    }

    @Test
    fun onImageSelected_playlistIsNull_showEnlargedImage() {
        // Given
        presenter.setPrivateData(playlist = null)

        // When
        presenter.onImageSelected()

        // Then
        verify(view, times(0)).showEnlargedImage(any())
    }

    @Test
    fun onShowMoreOptions_playlistIsNotNull_showMoreOptions() {
        // Given
        presenter.setPrivateData(playlist = MockDataModel.mockPlaylist)

        // When
        presenter.onShowMoreOptions()

        // Then
        verify(view, times(1)).showMoreOptions(any())
    }

    @Test
    fun onShowMoreOptions_playlistIsNull_doNothing() {
        // Given
        presenter.setPrivateData(playlist = null)

        // When
        presenter.onShowMoreOptions()

        // Then
        verify(view, times(0)).showMoreOptions(any())
    }

    @Test
    fun onMoreOptionSelected_selectionIsElse_returnFalse() {
        // When
        val result = presenter.onMoreOptionSelected(PickerOptions.DOWNLOAD)

        // Then
        assertFalse(result)
    }

    @Test
    fun onMoreOptionSelected_selectionIsAddToCollection_playlistIsNotNull_returnTrueAndShowFavourite() {
        // Given
        presenter.setPrivateData(playlist = MockDataModel.mockPlaylist)
        presenter.setObservable(toggleFavouriteObservable = null)
        whenever(playlistFacade.toggleFavourite(any())).thenReturn(Single.just(Any()))

        // When
        val result = presenter.onMoreOptionSelected(PickerOptions.ADD_TO_COLLECTION)

        // Then
        assertTrue(result)
        verify(view, times(1)).showFavourited(any())
        verify(playlistFacade, times(1)).toggleFavourite(any())
    }

    @Test
    fun onMoreOptionSelected_selectionIsAddToCollection_playlistIsNull_returnTrueAndNotShowFavourite() {
        // Given
        presenter.setPrivateData(playlist = null)
        presenter.setObservable(toggleFavouriteObservable = null)
        whenever(playlistFacade.toggleFavourite(any())).thenReturn(Single.just(Any()))

        // When
        val result = presenter.onMoreOptionSelected(PickerOptions.ADD_TO_COLLECTION)

        // Then
        assertTrue(result)
        verify(view, times(0)).showFavourited(any())
        verify(playlistFacade, times(0)).toggleFavourite(any())
    }

    @Test
    fun onMoreOptionSelected_selectionIsRemoveToggleFavorite_playlistIsNotNull_returnTrueAndShowFavourite() {
        // Given
        presenter.setPrivateData(playlist = MockDataModel.mockPlaylist)
        presenter.setObservable(toggleFavouriteObservable = null)
        whenever(playlistFacade.toggleFavourite(any())).thenReturn(Single.just(Any()))

        // When
        val result = presenter.onMoreOptionSelected(PickerOptions.REMOVE_FROM_COLLECTION)
        assertTrue(result)

        // Then
        verify(view, times(1)).showFavourited(any())
        verify(playlistFacade, times(1)).toggleFavourite(any())
    }

    @Test
    fun onMoreOptionSelected_selectionIsRemoveToggleFavorite_playlistIsNull_returnTrueAndNotShowFavourite() {
        // Given
        presenter.setPrivateData(playlist = null)
        presenter.setObservable(toggleFavouriteObservable = null)
        whenever(playlistFacade.toggleFavourite(any())).thenReturn(Single.just(Any()))

        // When
        val result = presenter.onMoreOptionSelected(PickerOptions.REMOVE_FROM_COLLECTION)
        assertTrue(result)

        // Then
        verify(view, times(0)).showFavourited(any())
        verify(playlistFacade, times(0)).toggleFavourite(any())
    }

    @Test
    fun onMoreOptionSelected_selectionIsAddToQueue_showAddToQueueToast() {
        // When
        presenter.onMoreOptionSelected(PickerOptions.ADD_TO_QUEUE)
        // Then
        verify(view, times(1)).showAddToQueueToast()
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
    fun onSharePlaylistClick_playlistIsNotNull_showShareOptions() {
        // Given
        presenter.setPrivateData(playlist = MockDataModel.mockPlaylist)

        // When
        presenter.onSharePlaylistClick()

        // Then
        verify(view, times(1)).showShareOptions(any())
    }

    @Test
    fun onSharePlaylistClick_playlistIsNull_doNothing() {
        // Given
        presenter.setPrivateData(playlist = null)

        // When
        presenter.onSharePlaylistClick()

        // Then
        verify(view, times(0)).showShareOptions(any())
    }

    @Test
    fun onToggleFavourite_playlistIsNotNull_toggleFavouriteObservableIsNull_showFavourite() {
        // Given
        presenter.setPrivateData(playlist = MockDataModel.mockPlaylist)
        presenter.setObservable(toggleFavouriteObservable = null)
        whenever(playlistFacade.toggleFavourite(any())).thenReturn(Single.just(Any()))

        // When
        presenter.onToggleFavourite()

        // Then
        verify(view, times(1)).showFavourited(any())
    }

    @Test
    fun onToggleFavourite_playlistIsNull_toggleFavouriteObservableIsNotNull_doNothing() {
        // Given
        presenter.setPrivateData(playlist = null, isFavourited = true)
        presenter.setObservable(toggleFavouriteObservable = Single.just(Any()))
        whenever(playlistFacade.toggleFavourite(any())).thenReturn(Single.just(Any()))

        // When
        presenter.onToggleFavourite()

        // Then
        verify(view, times(0)).showFavourited(any())
    }

    @Test
    fun onToggleFavourite_playlistIsNull_toggleFavouriteObservableIsNull_doNothing() {
        // Given
        presenter.setPrivateData(playlist = null, isFavourited = false)
        presenter.setObservable(toggleFavouriteObservable = null)
        whenever(playlistFacade.toggleFavourite(any())).thenReturn(Single.just(Any()))

        // When
        presenter.onToggleFavourite()

        // Then
        verify(view, times(0)).showFavourited(any())
    }

    @Test
    fun onSongSelected_allowStreamIsTrue_playlistIsNotNull_playPlaylist() {
        // Given
        val mockTrackValue = MockDataModel.mockTrack.copy(allowStream = true)
        presenter.setPrivateData(
            trackList = listOf(mockTrackValue),
            playlist = MockDataModel.mockPlaylist
        )

        // When
        presenter.onSongSelected(1)

        // Then
        verify(view, times(1)).playPlaylist(any(), any(), any(), any(), any(), any())
    }

    @Test
    fun onSongSelected_allowStreamIsTrue_playlistIsNull_doNothing() {
        // Given
        val mockTrackValue = MockDataModel.mockTrack.copy(allowStream = true)
        presenter.setPrivateData(trackList = listOf(mockTrackValue), playlist = null)

        // When
        presenter.onSongSelected(1)

        // Then
        verify(view, times(0)).playPlaylist(any(), any(), any(), any(), any(), any())
    }

    @Test
    fun onSongSelected_allowStreamIsFalse_playlistIsNotNull_playPlaylist() {
        // Given
        val mockTrackValue = MockDataModel.mockTrack.copy(allowStream = false)
        presenter.setPrivateData(
            trackList = listOf(mockTrackValue, MockDataModel.mockTrack),
            playlist = MockDataModel.mockPlaylist
        )

        // When
        presenter.onSongSelected(1)

        // Then
        verify(view, times(1)).playPlaylist(any(), any(), any(), any(), any(), any())
    }

    @Test
    fun onRetrySongs_playlistIsNotNull_showPlaylistSongsLoading() {
        // Given
        presenter.setPrivateData(playlist = MockDataModel.mockPlaylist)
        whenever(playlistFacade.hasPlaylistWriteRight()).thenReturn(false)
        whenever(playlistFacade.getPlaylistTracks(any(), any(), any())).thenReturn(
            Single.just(listOf(MockDataModel.mockTrack))
        )

        // When
        presenter.onRetrySongs()

        // Then
        verify(view, times(1)).showPlaylistSongsLoading()
    }

    @Test
    fun onRetrySongs_playlistIsNotNull_doNothing() {
        // Given
        presenter.setPrivateData(playlist = null)

        // When
        presenter.onRetrySongs()

        // Then
        verify(view, times(0)).showPlaylistSongsLoading()
    }

    @Test
    fun trackFirebasePlaylistShare_playlistIsNull_FAIsTracked() {
        // Given
        presenter.setPrivateData(playlist = null)

        // When
        presenter.trackFirebasePlaylistShare()

        // Then
        verify(exactly = 1) { analyticManager.trackEvent(any()) }
    }

    @Test
    fun trackFirebasePlaylistShare_playlistIsNotNull_playlistNameHaveEn_FAIsTracked() {
        // Given
        val mockPlaylist = MockDataModel.mockPlaylist.copy(
            name = listOf(LocalisedString(language = "en", "nameEN"))
        )
        presenter.setPrivateData(playlist = mockPlaylist)

        // When
        presenter.trackFirebasePlaylistShare()

        // Then
        verify(exactly = 1) { analyticManager.trackEvent(any()) }
    }

    @Test
    fun trackFirebasePlaylistShare_playlistIsNotNull_playlistNameDoNotHaveEn_FAIsTracked() {
        // Given
        val mockPlaylist = MockDataModel.mockPlaylist.copy(
            name = listOf(LocalisedString(language = "th", "nameTH"))
        )
        presenter.setPrivateData(playlist = mockPlaylist)

        // When
        presenter.trackFirebasePlaylistShare()

        // Then
        verify(exactly = 1) { analyticManager.trackEvent(any()) }
    }

    @Test
    fun onFavouriteSelect_isFavouritedIsTrue_isSuccessIsTrue_thenShowFavouritedToast() {
        // When
        presenter.onFavouriteSelect(isFavourited = true, isSuccess = true)

        // Then
        verify(view, times(1)).showFavouritedToast()
    }

    @Test
    fun onFavouriteSelect_isFavouritedIsFalse_isSuccessIsTrue_thenShowUnFavouritedToast() {
        // When
        presenter.onFavouriteSelect(isFavourited = false, isSuccess = true)

        // Then
        verify(view, times(1)).showUnFavouritedToast()
    }

    @Test
    fun onFavouriteSelect_isFavouritedIsTrue_isSuccessIsFalse_thenShowFavouritedError() {
        // When
        presenter.onFavouriteSelect(isFavourited = true, isSuccess = false)

        // Then
        verify(view, times(1)).showFavouritedError()
    }

    @Test
    fun onFavouriteSelect_isFavouritedIsFalse_isSuccessIsFalse_thenShowUnFavouritedError() {
        // When
        presenter.onFavouriteSelect(isFavourited = false, isSuccess = false)

        // Then
        verify(view, times(1)).showUnFavouritedError()
    }
}
