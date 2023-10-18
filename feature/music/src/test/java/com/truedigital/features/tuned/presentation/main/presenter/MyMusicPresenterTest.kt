package com.truedigital.features.tuned.presentation.main.presenter

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.truedigital.features.tuned.presentation.main.facade.MyMusicFacade
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class MyMusicPresenterTest {

    private val myMusicFacade: MyMusicFacade = mock()
    private val view: MyMusicPresenter.ViewSurface = mock()
    private val router: MyMusicPresenter.RouterSurface = mock()

    private val artistCountSubscription: Disposable = mock()
    private val albumCountSubscription: Disposable = mock()
    private val songCountSubscription: Disposable = mock()
    private val playlistCountSubscription: Disposable = mock()

    private lateinit var presenter: MyMusicPresenter

    @BeforeEach
    fun setUp() {
        presenter = MyMusicPresenter(myMusicFacade)
        presenter.onInject(view, router)
    }

    @Test
    fun onResume_currentTimeMillisMoreThanOrEqualsNextRefreshTimestamp_refresh() {
        // Given
        presenter.setPrivateData(nextRefreshTimestamp = System.currentTimeMillis() - 100L)
        whenever(myMusicFacade.getFollowedArtistCount()).thenReturn(Single.just(10))
        whenever(myMusicFacade.getFavouritedAlbumCount()).thenReturn(Single.just(10))
        whenever(myMusicFacade.getFavouritedSongCount()).thenReturn(Single.just(10))
        whenever(myMusicFacade.getFavouritedPlaylistCount()).thenReturn(Single.just(10))

        // When
        presenter.onResume()

        // Then
        verify(myMusicFacade, times(1)).getFollowedArtistCount()
        verify(myMusicFacade, times(1)).getFavouritedAlbumCount()
        verify(myMusicFacade, times(1)).getFavouritedSongCount()
        verify(myMusicFacade, times(1)).getFavouritedPlaylistCount()
    }

    @Test
    fun onResume_getArtistCountSubscription_success_showArtistCount() {
        // Given
        val mockArtistCountSubscription: Disposable = mock()
        presenter.setPrivateData(nextRefreshTimestamp = System.currentTimeMillis() + 1000L)
        presenter.setSubscription(artistCountSubscription = mockArtistCountSubscription)
        presenter.setObservable(artistCountObservable = Single.just(10))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showArtistCount(any())
    }

    @Test
    fun onResume_getArtistCountSubscription_fail_hideArtistCount() {
        // Given
        val mockArtistCountSubscription: Disposable = mock()
        presenter.setPrivateData(nextRefreshTimestamp = System.currentTimeMillis() + 1000L)
        presenter.setSubscription(artistCountSubscription = mockArtistCountSubscription)
        presenter.setObservable(artistCountObservable = Single.error(Throwable("error")))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).hideArtistCount()
    }

    @Test
    fun onResume_getAlbumCountSubscription_success_showAlbumCount() {
        // Given
        val mockAlbumCountSubscription: Disposable = mock()
        presenter.setPrivateData(nextRefreshTimestamp = System.currentTimeMillis() + 1000L)
        presenter.setSubscription(albumCountSubscription = mockAlbumCountSubscription)
        presenter.setObservable(albumCountObservable = Single.just(10))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showAlbumCount(any())
    }

    @Test
    fun onResume_getAlbumCountSubscription_fail_hideArtistCount() {
        // Given
        val mockAlbumCountSubscription: Disposable = mock()
        presenter.setPrivateData(nextRefreshTimestamp = System.currentTimeMillis() + 1000L)
        presenter.setSubscription(albumCountSubscription = mockAlbumCountSubscription)
        presenter.setObservable(albumCountObservable = Single.error(Throwable("error")))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).hideAlbumCount()
    }

    @Test
    fun onResume_getSongCountSubscription_success_showSongCount() {
        // Given
        val mockSongCountSubscription: Disposable = mock()
        presenter.setPrivateData(nextRefreshTimestamp = System.currentTimeMillis() + 1000L)
        presenter.setSubscription(songCountSubscription = mockSongCountSubscription)
        presenter.setObservable(songCountObservable = Single.just(10))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showSongCount(any())
    }

    @Test
    fun onResume_getSongCountSubscription_fail_hideSongCount() {
        // Given
        val mockSongCountSubscription: Disposable = mock()
        presenter.setPrivateData(nextRefreshTimestamp = System.currentTimeMillis() + 1000L)
        presenter.setSubscription(songCountSubscription = mockSongCountSubscription)
        presenter.setObservable(songCountObservable = Single.error(Throwable("error")))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).hideSongCount()
    }

    @Test
    fun onResume_getPlaylistCountSubscription_success_showPlaylistCount() {
        // Given
        val mockPlaylistCountSubscription: Disposable = mock()
        presenter.setPrivateData(nextRefreshTimestamp = System.currentTimeMillis() + 1000L)
        presenter.setSubscription(playlistCountSubscription = mockPlaylistCountSubscription)
        presenter.setObservable(playlistCountObservable = Single.just(10))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).showPlaylistCount(any())
    }

    @Test
    fun onResume_getPlaylistCountSubscription_fail_hidePlaylistCount() {
        // Given
        val mockPlaylistCountSubscription: Disposable = mock()
        presenter.setPrivateData(nextRefreshTimestamp = System.currentTimeMillis() + 1000L)
        presenter.setSubscription(playlistCountSubscription = mockPlaylistCountSubscription)
        presenter.setObservable(playlistCountObservable = Single.error(Throwable("error")))

        // When
        presenter.onResume()

        // Then
        verify(view, times(1)).hidePlaylistCount()
    }

    @Test
    fun onPause_subscriptionIsNotNull_disposeSubscription() {
        // Given
        presenter.setSubscription(
            artistCountSubscription = artistCountSubscription,
            albumCountSubscription = albumCountSubscription,
            songCountSubscription = songCountSubscription,
            playlistCountSubscription = playlistCountSubscription
        )

        // When
        presenter.onPause()

        // Then
        verify(artistCountSubscription, times(1)).dispose()
        verify(albumCountSubscription, times(1)).dispose()
        verify(songCountSubscription, times(1)).dispose()
        verify(playlistCountSubscription, times(1)).dispose()
    }

    @Test
    fun onPause_subscriptionIsNull_doNothing() {
        // Given
        presenter.setSubscription(
            artistCountSubscription = null,
            albumCountSubscription = null,
            songCountSubscription = null,
            playlistCountSubscription = null
        )

        // When
        presenter.onPause()

        // Then
        verify(artistCountSubscription, times(0)).dispose()
        verify(albumCountSubscription, times(0)).dispose()
        verify(songCountSubscription, times(0)).dispose()
        verify(playlistCountSubscription, times(0)).dispose()
    }

    @Test
    fun refresh_artist_getCountObservableIsCalled() {
        // Given
        whenever(myMusicFacade.getFollowedArtistCount()).thenReturn(Single.just(10))
        whenever(myMusicFacade.getFavouritedAlbumCount()).thenReturn(Single.just(10))
        whenever(myMusicFacade.getFavouritedSongCount()).thenReturn(Single.just(10))
        whenever(myMusicFacade.getFavouritedPlaylistCount()).thenReturn(Single.just(10))

        // When
        presenter.refresh()

        // Then
        verify(myMusicFacade, times(1)).getFollowedArtistCount()
        verify(myMusicFacade, times(1)).getFavouritedAlbumCount()
        verify(myMusicFacade, times(1)).getFavouritedSongCount()
        verify(myMusicFacade, times(1)).getFavouritedPlaylistCount()
    }

    @Test
    fun onFavouriteAlbumsSelected_navigateToFavouriteAlbums() {
        // When
        presenter.onFavouriteAlbumsSelected()

        // Then
        verify(router, times(1)).navigateToFavouriteAlbums()
    }

    @Test
    fun onFollowedArtistsSelected_navigateToFavouriteAlbums() {
        // When
        presenter.onFollowedArtistsSelected()

        // Then
        verify(router, times(1)).navigateToFollowedArtists()
    }

    @Test
    fun onFavouritePlaylistsSelected_navigateToFavouriteAlbums() {
        // When
        presenter.onFavouritePlaylistsSelected()

        // Then
        verify(router, times(1)).navigateToFavouritePlaylists()
    }

    @Test
    fun onFavouriteSongsSelected_navigateToFavouriteAlbums() {
        // When
        presenter.onFavouriteSongsSelected()

        // Then
        verify(router, times(1)).navigateToFavouriteSongs()
    }
}
