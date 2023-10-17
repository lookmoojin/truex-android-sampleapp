package com.truedigital.features.tuned.presentation.album.presenter

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.VisibleForTesting
import com.truedigital.features.tuned.common.extensions.cacheOnMainThread
import com.truedigital.features.tuned.common.extensions.tunedSubscribe
import com.truedigital.features.tuned.data.album.model.Album
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.domain.facade.album.AlbumFacade
import com.truedigital.features.tuned.injection.module.NetworkModule
import com.truedigital.features.tuned.presentation.bottomsheet.PickerOptions
import com.truedigital.features.tuned.presentation.common.Presenter
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

class AlbumPresenter @Inject constructor(private val albumFacade: AlbumFacade) : Presenter {

    companion object {
        const val ALBUM_ID_KEY = "album_id"
        const val SONG_ID_KEY = "song_id"
        const val AUTO_PLAY_KEY = "auto_play"
    }

    private lateinit var view: ViewSurface
    private lateinit var router: RouterSurface

    private var album: Album? = null
    private var tracks: List<Track>? = null
    private var selectedSongId: Int = 0

    private var albumObservable: Single<Album>? = null
    private var albumSubscription: Disposable? = null
    private var selectedTrackObservable: Single<Track>? = null
    private var selectedTrackSubscription: Disposable? = null
    private var albumTracksObservable: Single<List<Track>>? = null
    private var albumTracksSubscription: Disposable? = null
    private var isFavouritedObservable: Single<Boolean>? = null
    private var isFavouritedSubscription: Disposable? = null
    private var toggleFavouriteObservable: Single<Any>? = null
    private var toggleFavouriteSubscription: Disposable? = null
    private var albumMoreObservable: Single<List<Album>>? = null
    private var albumMoreSubscription: Disposable? = null

    private var isFavourited = false
    private var playingTrackId: Int? = null
    private var autoPlay = false

    fun onInject(view: ViewSurface, router: RouterSurface) {
        this.view = view
        this.router = router
    }

    // region lifecycle

    override fun onStart(arguments: Bundle?) {
        super.onStart(arguments)
        initRights()
        if (albumFacade.getAlbumNavigationAllowed()) {
            arguments?.let {
                val albumId = it.getInt(ALBUM_ID_KEY)
                selectedSongId = it.getInt(SONG_ID_KEY)
                autoPlay = it.getBoolean(AUTO_PLAY_KEY)
                when {
                    albumId != 0 -> {
                        albumObservable = getAlbumObservable(albumId)
                        isFavouritedObservable = getIsFavouritedObservable(albumId)
                    }
                    selectedSongId != 0 -> {
                        selectedTrackObservable =
                            getSelectedTrackObservable(selectedSongId)
                    }
                    else -> view.showLoadAlbumError()
                }
            }
        } else {
            view.showNavigationNotAllowed()
        }
    }

    override fun onResume() {
        super.onResume()
        albumSubscription = getAlbumSubscription()
        selectedTrackSubscription = getSelectedTrackSubscription()
        albumTracksSubscription = getAlbumTracksSubscription()
        isFavouritedSubscription = getIsFavouritedSubscription()
        toggleFavouriteSubscription = getToggleFavouriteSubscription()
        albumMoreSubscription = getAlbumMoreSubscription()
    }

    override fun onPause() {
        super.onPause()
        albumSubscription?.dispose()
        selectedTrackSubscription?.dispose()
        albumTracksSubscription?.dispose()
        isFavouritedSubscription?.dispose()
        toggleFavouriteSubscription?.dispose()
        albumMoreSubscription?.dispose()
    }

    // endregion

    fun onUpdatePlaybackState(
        albumId: Int?,
        trackId: Int?,
        @PlaybackStateCompat.State state: Int?
    ) {
        Timber.d("$albumId|$state")
        if (playingTrackId != trackId) {
            view.showCurrentPlayingTrack(trackId)
            playingTrackId = trackId
        }
    }

    // region callbacks

    fun onPlayAlbum() {
        if (albumFacade.getHasAlbumShuffleRight()) {
            val playableTracks = tracks?.filter { it.allowStream } ?: return
            if (playableTracks.isNotEmpty()) {
                album?.let {
                    view.playAlbum(
                        it,
                        playableTracks,
                        forceShuffle = true,
                        isOffline = it.isOffline
                    )
                }
            }
        } else {
            view.showUpgradeDialog()
        }
    }

    fun onShowMoreOptions() {
        album?.let { view.showMoreOptions(it) }
    }

    fun onMoreOptionSelected(selection: PickerOptions): Boolean {
        var actionHandled = true
        when (selection) {
            PickerOptions.ADD_TO_COLLECTION -> onToggleFavourite()
            PickerOptions.ADD_TO_QUEUE -> {
                view.showAddToQueueToast()
                actionHandled = false
            }
            PickerOptions.REMOVE_FROM_COLLECTION -> onToggleFavourite()
            PickerOptions.SHARE -> onShareAlbumMenu()
            PickerOptions.SHOW_ARTIST -> onArtistSelected()
            else -> {
                actionHandled = false
            }
        }
        return actionHandled
    }

    fun onMoreTrackOptionSelected(selection: PickerOptions) {
        when (selection) {
            PickerOptions.ADD_TO_QUEUE -> view.showAddToQueueToast()
            else -> {
                // Do nothing
            }
        }
    }

    fun onShareAlbumMenu() {
        album?.let { album ->
            view.showShareAlbum(album)
        }
    }

    fun onToggleFavourite() {
        album?.let {
            if (toggleFavouriteObservable == null) {
                toggleFavouriteObservable = getToggleFavouriteObservable(it)
                toggleFavouriteSubscription = getToggleFavouriteSubscription()
                isFavourited = !isFavourited
                view.showFavourited(isFavourited)
            }
        }
    }

    fun onSongSelected(trackId: Int) {
        if (albumFacade.getHasAlbumShuffleRight()) {
            val playableTracks = tracks?.filter { it.allowStream } ?: return
            val startIndex = playableTracks.indexOfFirst { it.id == trackId }
            if (playableTracks.isNotEmpty() && startIndex != -1) {
                album?.let {
                    view.playAlbum(
                        it,
                        playableTracks,
                        tracks?.indexOfFirst { it.id == trackId },
                        forceSequential = true,
                        isOffline = it.isOffline
                    )
                }
            }
        } else {
            view.showUpgradeDialog()
        }
    }

    fun onAlbumSelected(album: Album) {
        router.navigateToAlbum(album)
    }

    fun onArtistSelected() {
        album?.let {
            if (it.primaryRelease?.artists?.isNotEmpty() == true) {
                router.navigateToArtist(it.primaryRelease.artists.first().id)
            }
        }
    }

    fun onImageSelected() {
        album?.primaryRelease?.image?.let {
            view.showEnlargedImage(it)
        }
    }

    fun onRetryTracks() {
        album?.let {
            albumTracksObservable = getAlbumTracksObservable(it)
            albumTracksSubscription = getAlbumTracksSubscription()
        }
    }

    fun onRetryMoreAlbums() {
        album?.let {
            albumMoreObservable = getAlbumMoreObservable(it)
            albumMoreSubscription = getAlbumMoreSubscription()
        }
    }

    fun onFavouriteSelect(isFavourited: Boolean, isSuccess: Boolean) {
        if (isSuccess) {
            if (isFavourited) {
                view.showFavouritedToast()
            } else {
                view.showUnFavouritedToast()
            }
        } else {
            if (isFavourited) {
                view.showFavouritedError()
            } else {
                view.showUnFavouritedError()
            }
        }
    }

    private fun initRights() {
        if (!albumFacade.getHasAlbumShuffleRight()) view.showNoAlbumShuffleRight()
    }

    private fun getAlbumObservable(id: Int) = albumFacade.loadAlbum(id).cacheOnMainThread()

    private fun getAlbumSubscription() =
        albumObservable
            ?.doOnSuccess {
                albumTracksObservable = getAlbumTracksObservable(it)
                albumTracksSubscription = getAlbumTracksSubscription()
                if (it.primaryRelease?.artists?.isNotEmpty() == true) {
                    albumMoreObservable = getAlbumMoreObservable(it)
                    albumMoreSubscription = getAlbumMoreSubscription()
                }
            }
            ?.tunedSubscribe(
                {
                    albumObservable = null
                    album = it
                    view.initAlbum(it)
                    view.showOnlineAlbum(it)
                },
                {
                    albumObservable = null
                    view.showLoadAlbumError()
                }
            )

    private fun getSelectedTrackObservable(id: Int) = albumFacade.loadTrack(id).cacheOnMainThread()

    private fun getSelectedTrackSubscription() =
        selectedTrackObservable?.tunedSubscribe(
            {
                selectedTrackObservable = null
                albumObservable = getAlbumObservable(it.releaseId)
                albumSubscription = getAlbumSubscription()
                isFavouritedObservable = getIsFavouritedObservable(it.releaseId)
                isFavouritedSubscription = getIsFavouritedSubscription()
            },
            {
                selectedTrackObservable = null
                view.showLoadAlbumError()
            }
        )

    private fun getAlbumTracksObservable(album: Album) =
        albumFacade.loadTracks(album, album.primaryRelease?.trackIds.orEmpty())
            .cacheOnMainThread()
            .doOnSubscribe { view.showLoadingTracks() }

    private fun getAlbumTracksSubscription() =
        albumTracksObservable?.tunedSubscribe(
            {
                albumTracksObservable = null
                tracks = it
                tracks?.let { tracks -> view.showTracks(tracks, selectedSongId) }

                if (autoPlay) {
                    val position = if (selectedSongId != 0)
                        tracks?.indexOfFirst { it.id == selectedSongId }
                    else -1

                    if (position == -1)
                        onPlayAlbum()
                    else
                        onSongSelected(selectedSongId)
                }
            },
            {
                albumTracksObservable = null
                view.showTracksError()
            }
        )

    private fun getIsFavouritedObservable(id: Int) =
        albumFacade.loadFavourited(id).observeOn(AndroidSchedulers.mainThread())

    private fun getIsFavouritedSubscription() =
        isFavouritedObservable?.tunedSubscribe(
            {
                isFavouritedObservable = null
                isFavourited = it
                view.showFavourited(it)
            },
            {
                isFavouritedObservable = null
                view.showFavourited(false)
            }
        )

    private fun getToggleFavouriteObservable(album: Album) =
        albumFacade.toggleFavourite(album).observeOn(AndroidSchedulers.mainThread())

    private fun getToggleFavouriteSubscription(): Disposable? {
        val wasFavourited = isFavourited
        return toggleFavouriteObservable?.tunedSubscribe(
            {
                toggleFavouriteObservable = null
                if (isFavourited) {
                    view.showFavouritedToast()
                } else {
                    view.showUnFavouritedToast()
                }
            },
            {
                if (isFavourited) {
                    view.showFavouritedError()
                } else {
                    view.showUnFavouritedError()
                }
                toggleFavouriteObservable = null
                isFavourited = wasFavourited
                view.showFavourited(isFavourited)
            }
        )
    }

    private fun getAlbumMoreObservable(album: Album) =
        albumFacade.loadMoreFromArtist(album.primaryRelease?.artists?.first()?.id ?: 0)
            .cacheOnMainThread()
            .doOnSubscribe { view.showLoadingMoreAlbums() }

    private fun getAlbumMoreSubscription() =
        albumMoreObservable?.tunedSubscribe(
            {
                albumMoreObservable = null
                album?.let { album ->
                    val filteredResults = it.filter { it.id != album.id }
                    if (filteredResults.isEmpty()) {
                        view.hideMoreAlbums()
                    } else {
                        view.showMoreAlbums(filteredResults)
                    }
                }
            },
            {
                albumMoreObservable = null
                if (it is HttpException && it.code() == NetworkModule.HTTP_CODE_RESOURCE_NOT_FOUND) {
                    view.hideMoreAlbums()
                } else {
                    view.showMoreAlbumsError()
                }
            }
        )

    @VisibleForTesting
    fun setPrivateData(
        playingTrackId: Int = 0,
        trackList: List<Track> = mutableListOf(),
        album: Album? = null,
        toggleFavouriteObservable: Single<Any>? = null,
        isFavourited: Boolean = false
    ) {
        this.playingTrackId = playingTrackId
        this.tracks = trackList.toMutableList()
        this.album = album
        this.toggleFavouriteObservable = toggleFavouriteObservable
        this.isFavourited = isFavourited
    }

    @VisibleForTesting
    fun setSubscription(
        albumSubscription: Disposable? = null,
        selectedTrackSubscription: Disposable? = null,
        albumTracksSubscription: Disposable? = null,
        isFavouritedSubscription: Disposable? = null,
        toggleFavouriteSubscription: Disposable? = null,
        albumMoreSubscription: Disposable? = null
    ) {
        this.albumSubscription = albumSubscription
        this.selectedTrackSubscription = selectedTrackSubscription
        this.albumTracksSubscription = albumTracksSubscription
        this.isFavouritedSubscription = isFavouritedSubscription
        this.toggleFavouriteSubscription = toggleFavouriteSubscription
        this.albumMoreSubscription = albumMoreSubscription
    }

    @VisibleForTesting
    fun setObservable(
        albumObservable: Single<Album>? = null,
        selectedTrackObservable: Single<Track>? = null,
        albumTracksObservable: Single<List<Track>>? = null,
        isFavouritedObservable: Single<Boolean>? = null,
        toggleFavouriteObservable: Single<Any>? = null,
        albumMoreObservable: Single<List<Album>>? = null
    ) {
        this.albumObservable = albumObservable
        this.selectedTrackObservable = selectedTrackObservable
        this.albumTracksObservable = albumTracksObservable
        this.isFavouritedObservable = isFavouritedObservable
        this.toggleFavouriteObservable = toggleFavouriteObservable
        this.albumMoreObservable = albumMoreObservable
    }

    interface ViewSurface {
        fun initAlbum(album: Album)
        fun showOnlineAlbum(album: Album)
        fun showNoAlbumShuffleRight()
        fun showLoadAlbumError()
        fun showLoadingTracks()
        fun showCurrentPlayingTrack(trackId: Int?)
        fun playAlbum(
            album: Album,
            tracks: List<Track>,
            startIndex: Int? = null,
            forceShuffle: Boolean = false,
            forceSequential: Boolean = false,
            isOffline: Boolean
        )

        fun showTracks(tracks: List<Track>, selectedTrackId: Int)
        fun showTracksError()
        fun showLoadingMoreAlbums()
        fun showMoreAlbums(albums: List<Album>)
        fun showMoreAlbumsError()
        fun hideMoreAlbums()
        fun showFavourited(favourited: Boolean)
        fun showFavouritedError()
        fun showFavouritedToast()
        fun showUnFavouritedToast()
        fun showUnFavouritedError()
        fun showNavigationNotAllowed()
        fun showMoreOptions(album: Album)
        fun showUpgradeDialog()
        fun showShareLoading()
        fun showEnlargedImage(image: String)
        fun showShareAlbum(album: Album)
        fun showAddToQueueToast()
    }

    interface RouterSurface {
        fun navigateToAlbum(album: Album)
        fun navigateToArtist(id: Int)
    }
}
