package com.truedigital.features.tuned.presentation.main.presenter

import androidx.annotation.VisibleForTesting
import com.truedigital.features.tuned.common.extensions.cacheOnMainThread
import com.truedigital.features.tuned.common.extensions.tunedSubscribe
import com.truedigital.features.tuned.injection.module.NetworkModule.Companion.HTTP_CODE_RESOURCE_NOT_FOUND
import com.truedigital.features.tuned.presentation.common.Presenter
import com.truedigital.features.tuned.presentation.main.facade.MyMusicFacade
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import retrofit2.HttpException
import javax.inject.Inject

class MyMusicPresenter @Inject constructor(
    private val myMusicFacade: MyMusicFacade
) : Presenter {
    private lateinit var view: ViewSurface
    private lateinit var router: RouterSurface

    companion object {
        private const val REFRESH_INTERVAL = 10 * 1000L // 10 second
    }

    private var artistCountObservable: Single<Int>? = null
    private var artistCountSubscription: Disposable? = null
    private var albumCountObservable: Single<Int>? = null
    private var albumCountSubscription: Disposable? = null
    private var songCountObservable: Single<Int>? = null
    private var songCountSubscription: Disposable? = null
    private var playlistCountObservable: Single<Int>? = null
    private var playlistCountSubscription: Disposable? = null

    private var nextRefreshTimestamp = 0L

    fun onInject(view: ViewSurface, router: RouterSurface) {
        this.view = view
        this.router = router
    }

    // region Lifecycle

    override fun onResume() {
        if (System.currentTimeMillis() >= nextRefreshTimestamp) refresh()
        else {
            // prevent any sub being called twice (especially my playlist causing dupe results)
            // if user switch to My Music tab right after getting to the home screen
            disposeAllSubs()

            artistCountSubscription = getArtistCountSubscription()
            albumCountSubscription = getAlbumCountSubscription()
            songCountSubscription = getSongCountSubscription()
            playlistCountSubscription = getPlaylistCountSubscription()
        }
    }

    override fun onPause() {
        disposeAllSubs()
    }

    private fun disposeAllSubs() {
        artistCountSubscription?.dispose()
        albumCountSubscription?.dispose()
        songCountSubscription?.dispose()
        playlistCountSubscription?.dispose()
    }

    // region Callbacks

    fun refresh() {
        nextRefreshTimestamp = System.currentTimeMillis() + REFRESH_INTERVAL
        disposeAllSubs()

        artistCountObservable = getArtistCountObservable()
        albumCountObservable = getAlbumCountObservable()
        songCountObservable = getSongCountObservable()
        playlistCountObservable = getPlaylistCountObservable()

        artistCountSubscription = getArtistCountSubscription()
        albumCountSubscription = getAlbumCountSubscription()
        songCountSubscription = getSongCountSubscription()
        playlistCountSubscription = getPlaylistCountSubscription()
    }

    fun onFavouriteAlbumsSelected() {
        router.navigateToFavouriteAlbums()
    }

    fun onFollowedArtistsSelected() {
        router.navigateToFollowedArtists()
    }

    fun onFavouritePlaylistsSelected() {
        router.navigateToFavouritePlaylists()
    }

    fun onFavouriteSongsSelected() {
        router.navigateToFavouriteSongs()
    }

    // region RX

    private fun getArtistCountObservable() =
        myMusicFacade.getFollowedArtistCount().cacheOnMainThread()

    private fun getArtistCountSubscription() = artistCountObservable
        ?.tunedSubscribe(
            {
                artistCountObservable = null
                view.showArtistCount(it)
            },
            {
                artistCountObservable = null
                if (it is HttpException && it.code() == HTTP_CODE_RESOURCE_NOT_FOUND) {
                    view.showArtistCount(0)
                } else {
                    view.hideArtistCount()
                }
            }
        )

    private fun getAlbumCountObservable() =
        myMusicFacade.getFavouritedAlbumCount().cacheOnMainThread()

    private fun getAlbumCountSubscription() = albumCountObservable
        ?.tunedSubscribe(
            {
                albumCountObservable = null
                view.showAlbumCount(it)
            },
            {
                albumCountObservable = null
                if (it is HttpException && it.code() == HTTP_CODE_RESOURCE_NOT_FOUND) {
                    view.showAlbumCount(0)
                } else {
                    view.hideAlbumCount()
                }
            }
        )

    private fun getSongCountObservable() =
        myMusicFacade.getFavouritedSongCount().cacheOnMainThread()

    private fun getSongCountSubscription() = songCountObservable
        ?.tunedSubscribe(
            {
                songCountObservable = null
                view.showSongCount(it)
            },
            {
                songCountObservable = null
                if (it is HttpException && it.code() == HTTP_CODE_RESOURCE_NOT_FOUND) {
                    view.showSongCount(0)
                } else {
                    view.hideSongCount()
                }
            }
        )

    private fun getPlaylistCountObservable() =
        myMusicFacade.getFavouritedPlaylistCount().cacheOnMainThread()

    private fun getPlaylistCountSubscription() = playlistCountObservable
        ?.tunedSubscribe(
            {
                playlistCountObservable = null
                view.showPlaylistCount(it)
            },
            {
                playlistCountObservable = null
                if (it is HttpException && it.code() == HTTP_CODE_RESOURCE_NOT_FOUND) {
                    view.showPlaylistCount(0)
                } else {
                    view.hidePlaylistCount()
                }
            }
        )

    @VisibleForTesting
    fun setPrivateData(nextRefreshTimestamp: Long = 0L) {
        this.nextRefreshTimestamp = nextRefreshTimestamp
    }

    @VisibleForTesting
    fun setSubscription(
        artistCountSubscription: Disposable? = null,
        albumCountSubscription: Disposable? = null,
        songCountSubscription: Disposable? = null,
        playlistCountSubscription: Disposable? = null
    ) {
        this.artistCountSubscription = artistCountSubscription
        this.albumCountSubscription = albumCountSubscription
        this.songCountSubscription = songCountSubscription
        this.playlistCountSubscription = playlistCountSubscription
    }

    @VisibleForTesting
    fun setObservable(
        artistCountObservable: Single<Int>? = null,
        albumCountObservable: Single<Int>? = null,
        songCountObservable: Single<Int>? = null,
        playlistCountObservable: Single<Int>? = null
    ) {
        this.artistCountObservable = artistCountObservable
        this.albumCountObservable = albumCountObservable
        this.songCountObservable = songCountObservable
        this.playlistCountObservable = playlistCountObservable
    }

    interface ViewSurface {
        fun showArtistCount(count: Int)
        fun hideArtistCount()
        fun showAlbumCount(count: Int)
        fun hideAlbumCount()
        fun showSongCount(count: Int)
        fun hideSongCount()
        fun showPlaylistCount(count: Int)
        fun hidePlaylistCount()
        fun refreshFavorite()
    }

    interface RouterSurface {
        fun navigateToFollowedArtists()
        fun navigateToFavouriteAlbums()
        fun navigateToFavouritePlaylists()
        fun navigateToFavouriteSongs()
    }
}
