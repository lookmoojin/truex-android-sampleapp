package com.truedigital.features.tuned.presentation.playlist.presenter

import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.VisibleForTesting
import com.truedigital.common.share.analytics.measurement.AnalyticManager
import com.truedigital.common.share.analytics.measurement.constant.MeasurementConstant
import com.truedigital.features.listens.share.constant.MusicConstant
import com.truedigital.features.tuned.application.configuration.Configuration
import com.truedigital.features.tuned.common.extensions.cacheOnMainThread
import com.truedigital.features.tuned.common.extensions.localisedStringForLanguage
import com.truedigital.features.tuned.common.extensions.tunedSubscribe
import com.truedigital.features.tuned.data.playlist.model.Playlist
import com.truedigital.features.tuned.data.track.model.Track
import com.truedigital.features.tuned.data.util.LocalisedString
import com.truedigital.features.tuned.domain.facade.playlist.PlaylistFacade
import com.truedigital.features.tuned.injection.module.NetworkModule
import com.truedigital.features.tuned.presentation.bottomsheet.PickerOptions
import com.truedigital.features.tuned.presentation.common.Presenter
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

class PlaylistPresenter @Inject constructor(
    private val playlistFacade: PlaylistFacade,
    private val config: Configuration,
    private val analyticManager: AnalyticManager
) : Presenter {

    companion object {
        const val PLAYLIST_KEY = "playlist"
        const val PLAYLIST_ID_KEY = "playlist_id"
        const val AUTO_PLAY_KEY = "auto_play"
        private const val CONTENT_TYPE = "music_playlist"
        private const val INITIAL_OFFSET = 1
        private const val DEFAULT_PAGE_SIZE = 15
        private const val COUNT_99 = 99
    }

    private lateinit var view: ViewSurface
    private lateinit var router: RouterSurface

    private var itemOffset = INITIAL_OFFSET

    private var playlistObservable: Single<Playlist>? = null
    private var playlistSubscription: Disposable? = null
    private var playlistTracksObservable: Single<List<Track>>? = null
    private var playlistTracksSubscription: Disposable? = null
    private var isOwnerObservable: Single<Boolean>? = null
    private var isOwnerSubscription: Disposable? = null
    private var isFavouritedObservable: Single<Boolean>? = null
    private var isFavouritedSubscription: Disposable? = null
    private var toggleFavouriteObservable: Single<Any>? = null
    private var toggleFavouriteSubscription: Disposable? = null

    private var playlist: Playlist? = null
    private var tracks: MutableList<Track> = mutableListOf()

    private var isFavourited = false
    private var isOwner = false
    private var autoPlay = false

    fun onInject(view: ViewSurface, router: RouterSurface) {
        this.view = view
        this.router = router
    }

    // region lifecycle

    override fun onStart(arguments: Bundle?) {
        super.onStart(arguments)
        arguments?.let { bundle ->
            val playlistId = bundle.getInt(PLAYLIST_ID_KEY)
            val playlistString = bundle.getString(PLAYLIST_KEY)
            autoPlay = bundle.getBoolean(AUTO_PLAY_KEY)
            when {
                !playlistString.isNullOrEmpty() -> {
                    playlist = Playlist.fromString(playlistString)
                    playlist?.let { playlist ->
                        view.initPlaylist(playlist)
                        view.showOnlinePlaylist(playlist)
                        isOwnerObservable = getIsOwnerObservable(playlist.creatorId)
                    }
                }
                playlistId != 0 -> playlistObservable = getPlaylistObservable(playlistId)
                else -> view.showLoadPlaylistError()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        playlistSubscription = getPlaylistSubscription()
        playlistTracksSubscription = getPlaylistTracksSubscription()
        isOwnerSubscription = getIsOwnerSubscription()
        isFavouritedSubscription = getIsFavouritedSubscription()
        toggleFavouriteSubscription = getToggleFavouriteSubscription()
    }

    override fun onPause() {
        playlistSubscription?.dispose()
        playlistTracksSubscription?.dispose()
        isOwnerSubscription?.dispose()
        isFavouritedSubscription?.dispose()
        toggleFavouriteSubscription?.dispose()
        super.onPause()
    }

    // endregion

    fun onUpdatePlaybackState(trackId: Int?, @PlaybackStateCompat.State state: Int?) {
        Timber.d("$state")
        if (trackId != null)
            view.showCurrentPlayingTrack(trackId)
    }

    // region callbacks

    fun onPlayPlaylist() {
        if (tracks.isNotEmpty()) {
            val playableTracks = tracks.filter { it.allowStream }
            if (playableTracks.isNotEmpty()) {
                playlist?.let { playlist ->
                    view.playPlaylist(
                        playlist,
                        playableTracks,
                        forceShuffle = true,
                        isOffline = playlist.isOffline
                    )
                }
            }
        }
    }

    fun onImageSelected() {
        view.showEnlargedImage(playlist?.coverImage)
    }

    fun onShowMoreOptions() {
        playlist?.let { view.showMoreOptions(it) }
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

    fun onSharePlaylistClick() {
        playlist?.let { playlist ->
            view.showShareOptions(playlist)
        }
    }

    fun onToggleFavourite() {
        playlist?.let {
            if (toggleFavouriteObservable == null) {
                toggleFavouriteObservable = getToggleFavouriteObservable(it)
                toggleFavouriteSubscription = getToggleFavouriteSubscription()
                isFavourited = !isFavourited
                view.showFavourited(isFavourited)
            }
        }
    }

    fun onSongSelected(position: Int) {
        val playableTracks = tracks.filter { it.allowStream }
        if (playableTracks.isNotEmpty()) {
            playlist?.let { playlist ->
                view.playPlaylist(
                    playlist,
                    playableTracks,
                    position,
                    forceSequential = true,
                    isOffline = playlist.isOffline
                )
            }
        }
    }

    fun onRetrySongs() {
        playlist?.let {
            itemOffset = INITIAL_OFFSET
            tracks.clear()
            view.showPlaylistSongsLoading()
            loadMoreTracks()
        }
    }

    fun loadMoreTracks() {
        playlist?.let {
            playlistTracksObservable =
                if (isOwner && config.enablePlaylistEditing && playlistFacade.hasPlaylistWriteRight())
                    getPlaylistTracksObservable(it.id, 1, COUNT_99)
                else
                    getPlaylistTracksObservable(it.id, itemOffset, DEFAULT_PAGE_SIZE)
            playlistTracksSubscription = getPlaylistTracksSubscription()
            itemOffset += DEFAULT_PAGE_SIZE
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

    private fun getPlaylistObservable(id: Int) =
        playlistFacade.getPlaylist(id)
            .doOnSubscribe { view.showPlayButtonLoading() }
            .cacheOnMainThread()

    private fun getPlaylistSubscription() = playlistObservable
        ?.tunedSubscribe(
            {
                playlistObservable = null
                playlist = it
                view.initPlaylist(it)
                isOwnerObservable = getIsOwnerObservable(it.creatorId)
                isOwnerSubscription = getIsOwnerSubscription()
            },
            {
                playlistObservable = null
                view.showLoadPlaylistError()
            }
        )

    private fun getPlaylistTracksObservable(playlistId: Int, offset: Int, count: Int) =
        playlistFacade.getPlaylistTracks(playlistId, offset, count)
            .cacheOnMainThread()

    private fun getPlaylistTracksSubscription() = playlistTracksObservable
        ?.tunedSubscribe(
            {
                playlistTracksObservable = null
                tracks.addAll(it)
                view.showPlaylistSongs(tracks, (it.size >= DEFAULT_PAGE_SIZE))
                if (autoPlay) onPlayPlaylist()
            },
            {
                playlistTracksObservable = null
                if (it is HttpException && it.code() == NetworkModule.HTTP_CODE_RESOURCE_NOT_FOUND)
                    if (tracks.isEmpty()) {
                        view.showPlaylistNoSongs()
                    } else
                        view.showPlaylistSongs(tracks, false)
                else
                    view.showPlaylistSongsError()
            }
        )

    private fun getIsOwnerObservable(creatorId: Int) =
        playlistFacade.isOwner(creatorId).cacheOnMainThread()

    private fun getIsOwnerSubscription() = isOwnerObservable
        ?.tunedSubscribe(
            { owner ->
                isOwnerObservable = null
                isOwner = owner
                view.showOwner(
                    isOwner = owner,
                    isPublic = playlist?.isPublic ?: false,
                    hasPlaylistWriteRight = playlistFacade.hasPlaylistWriteRight()
                )
                playlist?.let { playlist ->
                    if (!isOwner) {
                        isFavouritedObservable = getIsFavouritedObservable(playlist.id)
                        isFavouritedSubscription = getIsFavouritedSubscription()
                    }
                    view.showPlaylistSongsLoading()
                    loadMoreTracks()
                }
            },
            {
                isOwnerObservable = null
                view.showLoadPlaylistError()
            }
        )

    private fun getIsFavouritedObservable(id: Int) =
        playlistFacade.loadFavourited(id).cacheOnMainThread()

    private fun getIsFavouritedSubscription() =
        isFavouritedObservable?.tunedSubscribe(
            {
                isFavouritedObservable = null
                isFavourited = it
                view.showFavourited(it)
            },
            {
                isFavouritedObservable = null
            }
        )

    private fun getToggleFavouriteObservable(playlist: Playlist) =
        playlistFacade.toggleFavourite(playlist.id).cacheOnMainThread()

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

    fun trackFirebasePlaylistShare() {
        val firebaseAnalyticsHashMap = HashMap<String, Any>().apply {
            put(
                MeasurementConstant.Key.KEY_EVENT_NAME,
                MeasurementConstant.Event.EVENT_SOCIAL_SHARE
            )
            put(MeasurementConstant.Key.KEY_CONTENT_TYPE, CONTENT_TYPE)

            playlist?.let { playlist ->
                put(MeasurementConstant.Key.KEY_CMS_ID, playlist.id)

                playlist.name.localisedStringForLanguage("en")?.value?.let { name ->
                    put(
                        MeasurementConstant.Key.KEY_TITLE,
                        name.take(com.truedigital.features.listens.share.constant.MusicConstant.FA.CHARACTERS_LIMIT)
                    )
                }
            }
        }

        analyticManager.trackEvent(firebaseAnalyticsHashMap)
    }

    @VisibleForTesting
    fun setPrivateData(
        trackList: List<Track> = mutableListOf(),
        playlist: Playlist? = null,
        isFavourited: Boolean = false
    ) {
        this.tracks = trackList.toMutableList()
        this.playlist = playlist
        this.isFavourited = isFavourited
    }

    @VisibleForTesting
    fun setSubscription(
        playlistSubscription: Disposable? = null,
        playlistTracksSubscription: Disposable? = null,
        isOwnerSubscription: Disposable? = null,
        isFavouritedSubscription: Disposable? = null,
        toggleFavouriteSubscription: Disposable? = null
    ) {
        this.playlistSubscription = playlistSubscription
        this.playlistTracksSubscription = playlistTracksSubscription
        this.isOwnerSubscription = isOwnerSubscription
        this.isFavouritedSubscription = isFavouritedSubscription
        this.toggleFavouriteSubscription = toggleFavouriteSubscription
    }

    @VisibleForTesting
    fun setObservable(
        playlistObservable: Single<Playlist>? = null,
        playlistTracksObservable: Single<List<Track>>? = null,
        isOwnerObservable: Single<Boolean>? = null,
        isFavouritedObservable: Single<Boolean>? = null,
        toggleFavouriteObservable: Single<Any>? = null
    ) {
        this.playlistObservable = playlistObservable
        this.playlistTracksObservable = playlistTracksObservable
        this.isOwnerObservable = isOwnerObservable
        this.isFavouritedObservable = isFavouritedObservable
        this.toggleFavouriteObservable = toggleFavouriteObservable
    }

    interface ViewSurface {
        fun initPlaylist(playlist: Playlist)
        fun showOnlinePlaylist(playlist: Playlist)
        fun showOwner(isOwner: Boolean, isPublic: Boolean, hasPlaylistWriteRight: Boolean)
        fun showLoadPlaylistError()
        fun showPlaylistSongs(tracks: List<Track>, morePages: Boolean)
        fun showPlaylistSongsLoading()
        fun showPlaylistNoSongs()
        fun showPlaylistSongsError()
        fun showPlayButtonLoading()
        fun showCurrentPlayingTrack(trackId: Int?)
        fun playPlaylist(
            playlist: Playlist,
            tracks: List<Track>,
            startIndex: Int? = null,
            forceShuffle: Boolean = false,
            forceSequential: Boolean = false,
            isOffline: Boolean
        )

        fun showLoading()

        fun showFavourited(favourited: Boolean)
        fun showFavouritedError()
        fun showFavouritedToast()
        fun showUnFavouritedError()
        fun showUnFavouritedToast()
        fun showMoreOptions(playlist: Playlist)
        fun showShareOptions(playlist: Playlist)

        fun showEnlargedImage(images: List<LocalisedString>?)
        fun showAddToQueueToast()
    }

    interface RouterSurface {
        fun sharePlaylist(playlist: Playlist, link: String)
    }
}
